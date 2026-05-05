package dao_impl;

import connect.DBConnect;
import entity.PhieuDatBan;
import entity.PhieuDatBan_Ban;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class PhieuDatBan_DAO {

    private PhieuDatBan mapPhieuDatBan(Record r, String maKhachHang, String maNhanVien) {
        var node = r.get("p");
        Timestamp thoiGianDat = null;
        if (!node.get("thoiGianDat").isNull()) {
            Object dateObj = node.get("thoiGianDat").asObject();
            if (dateObj instanceof LocalDateTime) {
                thoiGianDat = Timestamp.valueOf((LocalDateTime) dateObj);
            } else if (dateObj instanceof java.time.ZonedDateTime) {
                thoiGianDat = Timestamp.from(((java.time.ZonedDateTime) dateObj).toInstant());
            } else if (dateObj instanceof Number) {
                thoiGianDat = new Timestamp(((Number) dateObj).longValue());
            }
        }
        return new PhieuDatBan(
                node.get("maPhieuDatBan").asString(),
                thoiGianDat,
                node.get("trangThai").asString(),
                maKhachHang,
                maNhanVien,
                node.get("tienDatCoc").isNull() ? 0.0 : node.get("tienDatCoc").asDouble(),
                node.get("ghiChu").isNull() ? "" : node.get("ghiChu").asString()
        );
    }

    public List<PhieuDatBan> getPhieuDatBanChoHomNay() {
        List<PhieuDatBan> ds = new ArrayList<>();
        long todayStart = atStartOfDay(new java.util.Date()).getTime();
        long todayEnd = atEndOfDay(new java.util.Date()).getTime();
        LocalDateTime ldtStart = new java.sql.Timestamp(todayStart).toLocalDateTime();
        LocalDateTime ldtEnd = new java.sql.Timestamp(todayEnd).toLocalDateTime();

        String cypher = "MATCH (p:PhieuDatBan) WHERE p.trangThai = 'Đang chờ' " +
                "AND p.thoiGianDat >= $start AND p.thoiGianDat <= $end " +
                "OPTIONAL MATCH (p)-[:DAT_BOI]->(kh:KhachHang) " +
                "OPTIONAL MATCH (p)-[:LAP_BOI]->(nv:NhanVien) " +
                "RETURN p, kh.maKhachHang AS maKH, nv.maNhanVien AS maNV";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("start", ldtStart, "end", ldtEnd));
            while (result.hasNext()) {
                Record r = result.next();
                String maKH = r.get("maKH").isNull() ? "" : r.get("maKH").asString();
                String maNV = r.get("maNV").isNull() ? "" : r.get("maNV").asString();
                ds.add(mapPhieuDatBan(r, maKH, maNV));
            }
        }
        return ds;
    }

    public Map<String, String> layThongTinBanDatVaTenKhach(java.util.Date ngayCanXem) {
        Map<String, String> map = new HashMap<>();
        long start = atStartOfDay(ngayCanXem).getTime();
        long end = atEndOfDay(ngayCanXem).getTime();
        LocalDateTime ldtStart = new java.sql.Timestamp(start).toLocalDateTime();
        LocalDateTime ldtEnd = new java.sql.Timestamp(end).toLocalDateTime();

        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[:GOM_BAN]->(b:BanAn) " +
                "OPTIONAL MATCH (p)-[:DAT_BOI]->(k:KhachHang) " +
                "WHERE p.thoiGianDat >= $start AND p.thoiGianDat <= $end " +
                "RETURN b.maBan AS maBan, coalesce(k.hoTen, 'Khách đặt trước') AS hoTen";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("start", ldtStart, "end", ldtEnd));
            while (result.hasNext()) {
                Record r = result.next();
                map.put(r.get("maBan").asString(), r.get("hoTen").asString());
            }
        }
        return map;
    }

    public String sinhMaPhieuDatTuDong() {
        String cypher = "MATCH (p:PhieuDatBan) WHERE p.maPhieuDatBan STARTS WITH 'PDB' " +
                "RETURN p.maPhieuDatBan AS ma ORDER BY ma DESC LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            if (result.hasNext()) {
                String maCuoi = result.next().get("ma").asString();
                int so = Integer.parseInt(maCuoi.substring(3));
                return String.format("PDB%05d", so + 1);
            }
        }
        return "PDB00001";
    }

    public synchronized boolean themPhieuDatBan(PhieuDatBan phieu) {
        String cypher = "CREATE (p:PhieuDatBan {maPhieuDatBan: trim($ma), thoiGianDat: $thoiGian, trangThai: trim($trangThai), " +
                "tienDatCoc: $coc, ghiChu: trim($ghiChu)}) " +
                "WITH p " +
                "OPTIONAL MATCH (nv:NhanVien) WHERE trim(nv.maNhanVien) = trim($maNV) " +
                "FOREACH (ignore IN CASE WHEN nv IS NOT NULL THEN [1] ELSE [] END | MERGE (p)-[:LAP_BOI]->(nv)) " +
                "WITH p " +
                "OPTIONAL MATCH (kh:KhachHang) WHERE trim(kh.maKhachHang) = trim($maKH) " +
                "FOREACH (ignore IN CASE WHEN kh IS NOT NULL THEN [1] ELSE [] END | MERGE (p)-[:DAT_BOI]->(kh))";
        try (Session session = DBConnect.getSession()) {
            LocalDateTime ldt = new java.sql.Timestamp(phieu.getThoiGianDat().getTime()).toLocalDateTime();
            session.run(cypher, Values.parameters(
                    "ma", phieu.getMaPhieuDatBan(), "thoiGian", ldt,
                    "trangThai", phieu.getTrangThai(), "maKH", phieu.getMaKhachHang(),
                    "maNV", phieu.getMaNhanVien(), "coc", phieu.getTienDatCoc(), "ghiChu", phieu.getGhiChu()
            ));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean themPhieuDatBan_Ban(PhieuDatBan_Ban phieuBan) {
        String cypher = "MATCH (p:PhieuDatBan), (b:BanAn) " +
                "WHERE trim(p.maPhieuDatBan) = trim($maPhieu) AND trim(b.maBan) = trim($maBan) " +
                "MERGE (p)-[:GOM_BAN]->(b)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", phieuBan.getMaPhieuDatBan(), "maBan", phieuBan.getMaBan()));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<String> layDanhSachMaBanDaDatTheoNgay(java.util.Date ngayCanXem) {
        List<String> ds = new ArrayList<>();
        long start = atStartOfDay(ngayCanXem).getTime();
        long end = atEndOfDay(ngayCanXem).getTime();
        LocalDateTime ldtStart = new java.sql.Timestamp(start).toLocalDateTime();
        LocalDateTime ldtEnd = new java.sql.Timestamp(end).toLocalDateTime();

        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[:GOM_BAN]->(b:BanAn) " +
                "WHERE p.thoiGianDat >= $start AND p.thoiGianDat <= $end RETURN b.maBan AS maBan";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("start", ldtStart, "end", ldtEnd));
            while (result.hasNext()) ds.add(result.next().get("maBan").asString());
        }
        return ds;
    }

    public boolean huyDatBan(String maBan, java.util.Date ngayDat) {
        long start = atStartOfDay(ngayDat).getTime();
        long end = atEndOfDay(ngayDat).getTime();
        LocalDateTime ldtStart = new java.sql.Timestamp(start).toLocalDateTime();
        LocalDateTime ldtEnd = new java.sql.Timestamp(end).toLocalDateTime();

        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[:GOM_BAN]->(b:BanAn) " +
                "WHERE trim(b.maBan) = trim($maBan) AND p.thoiGianDat >= $start AND p.thoiGianDat <= $end " +
                "SET p.trangThai = 'Đã hủy'";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBan", maBan, "start", ldtStart, "end", ldtEnd));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean chuyenBanDatTruoc(String maBanCu, String maBanMoi, java.util.Date ngayDat) {
        long start = atStartOfDay(ngayDat).getTime();
        long end = atEndOfDay(ngayDat).getTime();
        LocalDateTime ldtStart = new java.sql.Timestamp(start).toLocalDateTime();
        LocalDateTime ldtEnd = new java.sql.Timestamp(end).toLocalDateTime();

        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[r:GOM_BAN]->(bCu:BanAn) " +
                "WHERE trim(bCu.maBan) = trim($maBanCu) AND p.thoiGianDat >= $start AND p.thoiGianDat <= $end " +
                "DELETE r WITH p MATCH (bMoi:BanAn) WHERE trim(bMoi.maBan) = trim($maBanMoi) MERGE (p)-[:GOM_BAN]->(bMoi)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBanCu", maBanCu, "maBanMoi", maBanMoi, "start", ldtStart, "end", ldtEnd));
            return true;
        } catch (Exception e) { return false; }
    }

    public PhieuDatBan getPhieuDatBanTheoMa(String maPhieu) {
        String cypher = "MATCH (p:PhieuDatBan) WHERE trim(p.maPhieuDatBan) = trim($ma) " +
                "OPTIONAL MATCH (p)-[:DAT_BOI]->(kh:KhachHang) " +
                "OPTIONAL MATCH (p)-[:LAP_BOI]->(nv:NhanVien) " +
                "RETURN p, kh.maKhachHang AS maKH, nv.maNhanVien AS maNV";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ma", maPhieu));
            if (result.hasNext()) {
                Record r = result.next();
                String maKH = r.get("maKH").isNull() ? "" : r.get("maKH").asString();
                String maNV = r.get("maNV").isNull() ? "" : r.get("maNV").asString();
                return mapPhieuDatBan(r, maKH, maNV);
            }
        }
        return null;
    }

    public String timMaPhieuDatDangChoTheoBan(String maBan, java.util.Date ngayDat) {
        long start = atStartOfDay(ngayDat).getTime();
        long end = atEndOfDay(ngayDat).getTime();
        LocalDateTime ldtStart = new java.sql.Timestamp(start).toLocalDateTime();
        LocalDateTime ldtEnd = new java.sql.Timestamp(end).toLocalDateTime();

        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[:GOM_BAN]->(b:BanAn) " +
                "WHERE trim(b.maBan) = trim($maBan) AND p.thoiGianDat >= $start AND p.thoiGianDat <= $end RETURN p.maPhieuDatBan AS maPhieu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maBan", maBan, "start", ldtStart, "end", ldtEnd));
            if (result.hasNext()) return result.next().get("maPhieu").asString();
        }
        return null;
    }

    public boolean capNhatTrangThai(String maPhieu, String trangThaiMoi) {
        String cypher = "MATCH (p:PhieuDatBan) WHERE trim(p.maPhieuDatBan) = trim($ma) SET p.trangThai = trim($trangThai)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", maPhieu, "trangThai", trangThaiMoi));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean capNhatTienCoc(String maPhieuDatBan, double tienDatCoc) {
        String cypher = "MATCH (p:PhieuDatBan) WHERE trim(p.maPhieuDatBan) = trim($ma) SET p.tienDatCoc = $coc";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", maPhieuDatBan, "coc", tienDatCoc));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<String> layDanhSachMaBanTheoPhieuDat(String maPhieu) {
        List<String> ds = new ArrayList<>();
        String cypher = "MATCH (p:PhieuDatBan)-[:GOM_BAN]->(b:BanAn) WHERE trim(p.maPhieuDatBan) = trim($ma) RETURN b.maBan AS maBan";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ma", maPhieu));
            while (result.hasNext()) ds.add(result.next().get("maBan").asString());
        }
        return ds;
    }

    public int huyPhieuDatQuaGio(int phutTreChoPhep) {
        long timeThreshold = System.currentTimeMillis() - ((long) phutTreChoPhep * 60 * 1000);
        LocalDateTime ldtThreshold = new java.sql.Timestamp(timeThreshold).toLocalDateTime();
        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'}) " +
                "WHERE p.thoiGianDat < $threshold " +
                "OPTIONAL MATCH (p)-[:GOM_BAN]->(b:BanAn) " +
                "SET p.trangThai = 'Đã hủy', b.trangThai = 'Bàn đang trống' " +
                "RETURN count(DISTINCT p) AS soPhieuBiHuy";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("threshold", ldtThreshold));
            if (result.hasNext()) return result.next().get("soPhieuBiHuy").asInt();
        }
        return 0;
    }

    private java.util.Date atStartOfDay(java.util.Date date) {
        Calendar c = Calendar.getInstance(); c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private java.util.Date atEndOfDay(java.util.Date date) {
        Calendar c = Calendar.getInstance(); c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59); c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
}
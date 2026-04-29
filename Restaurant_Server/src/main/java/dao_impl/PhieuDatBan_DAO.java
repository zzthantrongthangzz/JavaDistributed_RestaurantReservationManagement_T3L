package dao_impl;

import connect.DBConnect;
import entity.PhieuDatBan;
import entity.PhieuDatBan_Ban;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.sql.Timestamp;
import java.util.*;

public class PhieuDatBan_DAO {

    private PhieuDatBan mapPhieuDatBan(Record r) {
        return new PhieuDatBan(
                r.get("maPhieuDatBan").asString(),
                new Timestamp(r.get("thoiGianDat").asLong()),
                r.get("trangThai").asString(),
                r.get("maKhachHang").isNull() ? null : r.get("maKhachHang").asString(),
                r.get("maNhanVien").isNull() ? null : r.get("maNhanVien").asString(),
                r.get("tienDatCoc").asDouble(),
                r.get("ghiChu").isNull() ? null : r.get("ghiChu").asString()
        );
    }

    public List<PhieuDatBan> getPhieuDatBanChoHomNay() {
        List<PhieuDatBan> ds = new ArrayList<>();
        long todayStart = atStartOfDay(new java.util.Date()).getTime();
        long todayEnd = atEndOfDay(new java.util.Date()).getTime();

        String cypher = "MATCH (p:PhieuDatBan) WHERE p.trangThai = 'Đang chờ' " +
                "AND p.thoiGianDat >= $start AND p.thoiGianDat <= $end RETURN p";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("start", todayStart, "end", todayEnd));
            while (result.hasNext()) {
                Record r = result.next();
                var node = r.get("p");
                ds.add(new PhieuDatBan(
                        node.get("maPhieuDatBan").asString(), new Timestamp(node.get("thoiGianDat").asLong()),
                        node.get("trangThai").asString(), node.get("maKhachHang").asString(),
                        node.get("maNhanVien").asString(), node.get("tienDatCoc").asDouble(), node.get("ghiChu").asString()
                ));
            }
        }
        return ds;
    }

    public Map<String, String> layThongTinBanDatVaTenKhach(java.util.Date ngayCanXem) {
        Map<String, String> map = new HashMap<>();
        long start = atStartOfDay(ngayCanXem).getTime();
        long end = atEndOfDay(ngayCanXem).getTime();

        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[:DAT_BAN]->(b:BanAn), " +
                "(k:KhachHang {maKhachHang: p.maKhachHang}) " +
                "WHERE p.thoiGianDat >= $start AND p.thoiGianDat <= $end " +
                "RETURN b.maBan AS maBan, k.hoTen AS hoTen";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("start", start, "end", end));
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
        String cypher = "CREATE (p:PhieuDatBan {maPhieuDatBan: $ma, thoiGianDat: $thoiGian, trangThai: $trangThai, " +
                "maKhachHang: $maKH, maNhanVien: $maNV, tienDatCoc: $coc, ghiChu: $ghiChu})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "ma", phieu.getMaPhieuDatBan(), "thoiGian", phieu.getThoiGianDat().getTime(),
                    "trangThai", phieu.getTrangThai(), "maKH", phieu.getMaKhachHang(),
                    "maNV", phieu.getMaNhanVien(), "coc", phieu.getTienDatCoc(), "ghiChu", phieu.getGhiChu()
            ));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean themPhieuDatBan_Ban(PhieuDatBan_Ban phieuBan) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu}), (b:BanAn {maBan: $maBan}) " +
                "MERGE (p)-[:DAT_BAN]->(b)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", phieuBan.getMaPhieuDatBan(), "maBan", phieuBan.getMaBan()));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<String> layDanhSachMaBanDaDatTheoNgay(java.util.Date ngayCanXem) {
        List<String> ds = new ArrayList<>();
        long start = atStartOfDay(ngayCanXem).getTime();
        long end = atEndOfDay(ngayCanXem).getTime();

        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[:DAT_BAN]->(b:BanAn) " +
                "WHERE p.thoiGianDat >= $start AND p.thoiGianDat <= $end RETURN b.maBan AS maBan";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("start", start, "end", end));
            while (result.hasNext()) ds.add(result.next().get("maBan").asString());
        }
        return ds;
    }

    public boolean huyDatBan(String maBan, java.util.Date ngayDat) {
        long start = atStartOfDay(ngayDat).getTime();
        long end = atEndOfDay(ngayDat).getTime();
        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[:DAT_BAN]->(b:BanAn {maBan: $maBan}) " +
                "WHERE p.thoiGianDat >= $start AND p.thoiGianDat <= $end " +
                "SET p.trangThai = 'Đã hủy'";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBan", maBan, "start", start, "end", end));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean chuyenBanDatTruoc(String maBanCu, String maBanMoi, java.util.Date ngayDat) {
        long start = atStartOfDay(ngayDat).getTime();
        long end = atEndOfDay(ngayDat).getTime();
        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[r:DAT_BAN]->(bCu:BanAn {maBan: $maBanCu}) " +
                "WHERE p.thoiGianDat >= $start AND p.thoiGianDat <= $end " +
                "DELETE r WITH p MATCH (bMoi:BanAn {maBan: $maBanMoi}) MERGE (p)-[:DAT_BAN]->(bMoi)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBanCu", maBanCu, "maBanMoi", maBanMoi, "start", start, "end", end));
            return true;
        } catch (Exception e) { return false; }
    }

    public PhieuDatBan getPhieuDatBanTheoMa(String maPhieu) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $ma}) RETURN p";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ma", maPhieu));
            if (result.hasNext()) {
                var node = result.next().get("p");
                return new PhieuDatBan(
                        node.get("maPhieuDatBan").asString(), new Timestamp(node.get("thoiGianDat").asLong()),
                        node.get("trangThai").asString(), node.get("maKhachHang").asString(),
                        node.get("maNhanVien").asString(), node.get("tienDatCoc").asDouble(), node.get("ghiChu").asString()
                );
            }
        }
        return null;
    }

    public String timMaPhieuDatDangChoTheoBan(String maBan, java.util.Date ngayDat) {
        long start = atStartOfDay(ngayDat).getTime();
        long end = atEndOfDay(ngayDat).getTime();
        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[:DAT_BAN]->(b:BanAn {maBan: $maBan}) " +
                "WHERE p.thoiGianDat >= $start AND p.thoiGianDat <= $end RETURN p.maPhieuDatBan AS maPhieu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maBan", maBan, "start", start, "end", end));
            if (result.hasNext()) return result.next().get("maPhieu").asString();
        }
        return null;
    }

    public boolean capNhatTrangThai(String maPhieu, String trangThaiMoi) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $ma}) SET p.trangThai = $trangThai";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", maPhieu, "trangThai", trangThaiMoi));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean capNhatTienCoc(String maPhieuDatBan, double tienDatCoc) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $ma}) SET p.tienDatCoc = $coc";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", maPhieuDatBan, "coc", tienDatCoc));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<String> layDanhSachMaBanTheoPhieuDat(String maPhieu) {
        List<String> ds = new ArrayList<>();
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $ma})-[:DAT_BAN]->(b:BanAn) RETURN b.maBan AS maBan";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ma", maPhieu));
            while (result.hasNext()) ds.add(result.next().get("maBan").asString());
        }
        return ds;
    }

    public int huyPhieuDatQuaGio(int phutTreChoPhep) {
        long timeThreshold = System.currentTimeMillis() - ((long) phutTreChoPhep * 60 * 1000);
        String cypher = "MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'}) " +
                "WHERE p.thoiGianDat < $threshold " +
                "OPTIONAL MATCH (p)-[:DAT_BAN]->(b:BanAn) " +
                "SET p.trangThai = 'Đã hủy', b.trangThai = 'Bàn đang trống' " +
                "RETURN count(DISTINCT p) AS soPhieuBiHuy";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("threshold", timeThreshold));
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
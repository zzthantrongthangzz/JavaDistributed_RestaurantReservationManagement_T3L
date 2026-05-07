package dao_impl;

import connect.DBConnect;
import entity.BanAn;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.*;

public class BanAn_DAO {

    private BanAn mapBanAn(Record record) {
        int sucChua = 4;
        if (!record.get("sucChua").isNull()) {
            try {
                sucChua = record.get("sucChua").asInt();
            } catch (Exception e) {
                try {
                    sucChua = Integer.parseInt(record.get("sucChua").asString());
                } catch (Exception ex) {
                    sucChua = 4;
                }
            }
        }

        BanAn banAn = new BanAn(
                record.get("maBan").isNull() ? "" : record.get("maBan").asString(""),
                record.get("tenBan").isNull() ? "Chưa có tên" : record.get("tenBan").asString(""),
                record.get("loaiBan").isNull() ? "Bàn thường" : record.get("loaiBan").asString("Bàn thường"),
                sucChua,
                record.get("trangThai").isNull() ? "Bàn đang trống" : record.get("trangThai").asString("Bàn đang trống"),
                record.get("maKhu").isNull() ? null : record.get("maKhu").asString()
        );

        banAn.setTenKhachHang(record.get("tenKhachHang").isNull() ? null : record.get("tenKhachHang").asString());
        banAn.setTenKhu(record.get("tenKhu").isNull() ? "Chưa phân khu" : record.get("tenKhu").asString());
        banAn.setTenTang(record.get("tenTang").isNull() ? "Chưa phân tầng" : record.get("tenTang").asString());

        return banAn;
    }

    // ĐÃ FIX: Thay OPTIONAL MATCH thành MATCH cứng. Mọi bàn ăn trên hệ thống ĐỀU PHẢI có Khu và Tầng.
    private final String CYPHER_BASE_MATCH =
            "MATCH (b:BanAn)-[:THUOC_KHU]->(k:Khu)-[:THUOC_TANG]->(t:Tang) ";

    private final String CYPHER_RETURN =
            "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, " +
                    "b.sucChua AS sucChua, b.trangThai AS trangThai, k.maKhu AS maKhu, " +
                    "k.tenKhu AS tenKhu, t.tenTang AS tenTang, null AS tenKhachHang ";

    public List<BanAn> docDanhSachBan() {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + CYPHER_RETURN + " ORDER BY b.tenBan ASC";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                try {
                    list.add(mapBanAn(result.next()));
                } catch (Exception e) {
                    System.err.println("Lỗi đọc 1 dòng bàn ăn: " + e.getMessage());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean themBanMoi(BanAn banAn) {
        String cypher = "MATCH (k:Khu {maKhu: $maKhu}) " +
                "CREATE (b:BanAn {maBan: $maBan, tenBan: $tenBan, loaiBan: $loaiBan, sucChua: $sucChua, trangThai: $trangThai}) " +
                "-[:THUOC_KHU]->(k)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maKhu", banAn.getMaKhu(),
                    "maBan", banAn.getMaBan(),
                    "tenBan", banAn.getTenBan(),
                    "loaiBan", banAn.getLoaiBan(),
                    "sucChua", banAn.getSucChua(),
                    "trangThai", banAn.getTrangThai()
            ));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
        String cypher = "MATCH (b:BanAn {maBan: $maBan}) SET b.trangThai = $trangThaiMoi";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBan", maBan, "trangThaiMoi", trangThaiMoi));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean capNhatBan(BanAn banAn) {
        String cypher = "MATCH (b:BanAn {maBan: $maBan}) " +
                "OPTIONAL MATCH (b)-[r:THUOC_KHU]->() " +
                "DELETE r " +
                "WITH b " +
                "MATCH (k:Khu {maKhu: $maKhu}) " +
                "MERGE (b)-[:THUOC_KHU]->(k) " +
                "SET b.tenBan = $tenBan, b.loaiBan = $loaiBan, b.sucChua = $sucChua, b.trangThai = $trangThai";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maBan", banAn.getMaBan(),
                    "maKhu", banAn.getMaKhu(),
                    "tenBan", banAn.getTenBan(),
                    "loaiBan", banAn.getLoaiBan(),
                    "sucChua", banAn.getSucChua(),
                    "trangThai", banAn.getTrangThai()
            ));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean xoaBan(String maBan) {
        String cypher = "MATCH (b:BanAn {maBan: $maBan}) DETACH DELETE b";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBan", maBan));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public BanAn timBanAnTheoTenHoacSDTKhachHang(String tuKhoa) {
        String cypher = CYPHER_BASE_MATCH +
                "OPTIONAL MATCH (kh:KhachHang)<-[:DAT_BOI]-(p:PhieuDatBan)-[:GOM_BAN]->(b) " +
                "WHERE p.trangThai = 'Đang chờ' AND (toUpper(kh.hoTen) CONTAINS toUpper($tuKhoa) OR kh.soDienThoai CONTAINS $tuKhoa) " +
                "WITH b, k, t, kh.hoTen AS tenKH1 " +
                "OPTIONAL MATCH (kh2:KhachHang)<-[:CUA_KHACH]-(hd:HoaDon)-[:SU_DUNG_BAN]->(b) " +
                "WHERE hd.trangThai = 'Chưa thanh toán' AND (toUpper(kh2.hoTen) CONTAINS toUpper($tuKhoa) OR kh2.soDienThoai CONTAINS $tuKhoa) " +
                "WITH b, k, t, coalesce(tenKH1, kh2.hoTen) AS tenKhachHang " +
                "WHERE tenKhachHang IS NOT NULL " +
                "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, " +
                "b.sucChua AS sucChua, b.trangThai AS trangThai, k.maKhu AS maKhu, " +
                "k.tenKhu AS tenKhu, t.tenTang AS tenTang, tenKhachHang LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            if (result.hasNext()) return mapBanAn(result.next());
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<BanAn> layDanhSachBanTrongTheoNgay(Date ngayCanXem) {
        List<BanAn> list = new ArrayList<>();
        Calendar c = Calendar.getInstance(); c.setTime(ngayCanXem);
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0);
        long start = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
        long end = c.getTimeInMillis();

        String cypher = CYPHER_BASE_MATCH +
                "WHERE NOT EXISTS { MATCH (p:PhieuDatBan)-[:GOM_BAN]->(b) " +
                "WHERE ((p.thoiGianDat >= localdatetime({epochMillis: $start}) AND p.thoiGianDat <= localdatetime({epochMillis: $end})) " +
                "OR (p.thoiGianDat >= $start AND p.thoiGianDat <= $end)) AND p.trangThai <> 'Đã hủy' } " +
                CYPHER_RETURN + " ORDER BY b.tenBan ASC";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("start", start, "end", end));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<BanAn> timKiemBanTheoKhachHang(String tuKhoa) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH +
                "OPTIONAL MATCH (kh:KhachHang)<-[:DAT_BOI]-(p:PhieuDatBan)-[:GOM_BAN]->(b) " +
                "WHERE p.trangThai = 'Đang chờ' AND (toUpper(kh.hoTen) CONTAINS toUpper($tuKhoa) OR kh.soDienThoai CONTAINS $tuKhoa) " +
                "WITH b, k, t, kh.hoTen AS tenKH1 " +
                "OPTIONAL MATCH (kh2:KhachHang)<-[:CUA_KHACH]-(hd:HoaDon)-[:SU_DUNG_BAN]->(b) " +
                "WHERE hd.trangThai = 'Chưa thanh toán' AND (toUpper(kh2.hoTen) CONTAINS toUpper($tuKhoa) OR kh2.soDienThoai CONTAINS $tuKhoa) " +
                "WITH b, k, t, coalesce(tenKH1, kh2.hoTen) AS tenKhachHang " +
                "WHERE tenKhachHang IS NOT NULL " +
                "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, " +
                "b.sucChua AS sucChua, b.trangThai AS trangThai, k.maKhu AS maKhu, " +
                "k.tenKhu AS tenKhu, t.tenTang AS tenTang, tenKhachHang ORDER BY b.tenBan ASC";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public BanAn timBanAnTheoMa(String maBan) {
        String cypher = CYPHER_BASE_MATCH + "WHERE toUpper(trim(b.maBan)) = toUpper(trim($maBan)) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maBan", maBan));
            if (result.hasNext()) return mapBanAn(result.next());
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<BanAn> locTheoLoaiBan(String loaiBan) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + "WHERE trim(b.loaiBan) = trim($loaiBan) " + CYPHER_RETURN + " ORDER BY b.tenBan ASC";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("loaiBan", loaiBan));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<BanAn> timKiemBan(String tuKhoa) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + "WHERE toUpper(b.tenBan) CONTAINS toUpper(trim($tuKhoa)) OR toUpper(b.maBan) CONTAINS toUpper(trim($tuKhoa)) " + CYPHER_RETURN + " ORDER BY b.tenBan ASC";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<BanAn> locTheoTrangThai(String trangThai) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + "WHERE trim(b.trangThai) = trim($trangThai) " + CYPHER_RETURN + " ORDER BY b.tenBan ASC";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("trangThai", trangThai));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<String> docDanhSachTenTang() {
        List<String> list = new ArrayList<>();
        list.add("Tất cả");
        String cypher = "MATCH (t:Tang) RETURN DISTINCT t.tenTang AS tenTang ORDER BY tenTang";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) list.add(result.next().get("tenTang").asString());
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<String> docDanhSachTenKhuTheoTang(String tenTang) {
        List<String> list = new ArrayList<>();
        list.add("Tất cả");
        String cypher;
        if (tenTang == null || tenTang.trim().equals("Tất cả")) {
            cypher = "MATCH (k:Khu) RETURN DISTINCT k.tenKhu AS tenKhu ORDER BY k.tenKhu";
        } else {
            cypher = "MATCH (t:Tang)<-[:THUOC_TANG]-(k:Khu) WHERE trim(t.tenTang) = trim($tenTang) RETURN DISTINCT k.tenKhu AS tenKhu ORDER BY k.tenKhu";
        }
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tenTang", tenTang));
            while (result.hasNext()) list.add(result.next().get("tenKhu").asString());
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<BanAn> locBanAn(String tenTang, String tenKhu, String loaiBan) {
        List<BanAn> list = new ArrayList<>();
        StringBuilder cypher = new StringBuilder(CYPHER_BASE_MATCH + "WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (tenTang != null && !tenTang.trim().isEmpty() && !tenTang.trim().equals("Tất cả")) {
            cypher.append("AND trim(t.tenTang) = trim($tenTang) ");
            params.put("tenTang", tenTang);
        }
        if (tenKhu != null && !tenKhu.trim().isEmpty() && !tenKhu.trim().equals("Tất cả")) {
            cypher.append("AND trim(k.tenKhu) = trim($tenKhu) ");
            params.put("tenKhu", tenKhu);
        }
        if (loaiBan != null && !loaiBan.trim().isEmpty() && !loaiBan.trim().equals("Tất cả")) {
            cypher.append("AND trim(b.loaiBan) = trim($loaiBan) ");
            params.put("loaiBan", loaiBan);
        }
        cypher.append(CYPHER_RETURN).append(" ORDER BY b.tenBan ASC");

        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher.toString(), params);
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<BanAn> layDanhSachBanTheoKhuVuc(String tenKhu) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + "WHERE trim(k.tenKhu) = trim($tenKhu) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tenKhu", tenKhu));
            while (result.hasNext()) {
                list.add(mapBanAn(result.next()));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public String layMaBanTiepTheo() {
        try (Session session = DBConnect.getSession()) {
            Result result = session.run("MATCH (b:BanAn) RETURN count(b) + 1 AS soThuTu");
            if (result.hasNext()) {
                long count = result.next().get("soThuTu").asLong();
                return String.format("MB%06d", count);
            }
        }
        return "MB000001";
    }

    public int laySoThuTuBanTiepTheo(String loaiBan) {
        String cypher = "MATCH (b:BanAn) WHERE trim(b.loaiBan) = trim($loaiBan) RETURN count(b) + 1 AS soThuTu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("loaiBan", loaiBan));
            if (result.hasNext()) return result.next().get("soThuTu").asInt();
        } catch (Exception e) { e.printStackTrace(); }
        return 1;
    }

    private Date atStartOfDay(Date date) {
        Calendar c = Calendar.getInstance(); c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
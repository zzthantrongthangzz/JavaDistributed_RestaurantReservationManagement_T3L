package dao_impl;

import connect.DBConnect;
import entity.BanAn;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.*;

public class BanAn_DAO {

    // Hàm mapping CHỐNG LỖI ÉP KIỂU và DỮ LIỆU RỖNG
    private BanAn mapBanAn(Record record) {
        int sucChua = 4; // Mặc định nếu DB bị null
        if (!record.get("sucChua").isNull()) {
            try {
                sucChua = record.get("sucChua").asInt();
            } catch (Exception e) {
                try {
                    // Cứu cánh nếu lỡ lưu thành chuỗi ("4") trong DB
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

    // SỬ DỤNG ĐÚNG NHÃN :BanAn THEO DATABASE CỦA BẠN
    private final String CYPHER_BASE_MATCH =
            "MATCH (b:BanAn) " +
                    "OPTIONAL MATCH (b)-[:THUOC_KHU]->(k:Khu) " +
                    "OPTIONAL MATCH (k)-[:THUOC_TANG]->(t:Tang) ";

    private final String CYPHER_RETURN =
            "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, " +
                    "b.sucChua AS sucChua, b.trangThai AS trangThai, k.maKhu AS maKhu, " +
                    "k.tenKhu AS tenKhu, t.tenTang AS tenTang, null AS tenKhachHang ";

    // =========================================================================
    // 1. ĐỌC DANH SÁCH BÀN
    // =========================================================================
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

    // =========================================================================
    // 2. THÊM BÀN MỚI
    // =========================================================================
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

    // =========================================================================
    // 3. CẬP NHẬT TRẠNG THÁI BÀN
    // =========================================================================
    public boolean capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
        String cypher = "MATCH (b:BanAn {maBan: $maBan}) SET b.trangThai = $trangThaiMoi";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBan", maBan, "trangThaiMoi", trangThaiMoi));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // =========================================================================
    // 4. CẬP NHẬT THÔNG TIN BÀN
    // =========================================================================
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

    // =========================================================================
    // 5. XÓA BÀN
    // =========================================================================
    public boolean xoaBan(String maBan) {
        String cypher = "MATCH (b:BanAn {maBan: $maBan}) DETACH DELETE b";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBan", maBan));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // =========================================================================
    // 6. TÌM BÀN THEO TÊN HOẶC SĐT KHÁCH HÀNG (Dành cho Phiếu Đặt / Hóa Đơn)
    // =========================================================================
    public BanAn timBanAnTheoTenHoacSDTKhachHang(String tuKhoa) {
        String cypher = CYPHER_BASE_MATCH +
                "OPTIONAL MATCH (kh:KhachHang)<-[:CUA_KHACH_HANG]-(p:PhieuDatBan)-[:DAT_BAN]->(b) " +
                "WHERE p.trangThai = 'Đang chờ' AND (toUpper(kh.hoTen) CONTAINS toUpper($tuKhoa) OR kh.soDienThoai CONTAINS $tuKhoa) " +
                "WITH b, k, t, kh.hoTen AS tenKH1 " +
                "OPTIONAL MATCH (kh2:KhachHang)<-[:CUA_KHACH_HANG]-(hd:HoaDon)-[:SU_DUNG]->(b) " +
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

    // =========================================================================
    // 7. LẤY DANH SÁCH BÀN TRỐNG TRONG NGÀY
    // =========================================================================
    public List<BanAn> layDanhSachBanTrongTheoNgay(Date ngayCanXem) {
        List<BanAn> list = new ArrayList<>();
        Calendar c = Calendar.getInstance(); c.setTime(ngayCanXem);
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0);
        long start = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
        long end = c.getTimeInMillis();

        String cypher = CYPHER_BASE_MATCH +
                "WHERE NOT EXISTS { MATCH (p:PhieuDatBan)-[:DAT_BAN]->(b) WHERE p.ngayNhanBan >= $start AND p.ngayNhanBan <= $end AND p.trangThai <> 'Đã hủy' } " +
                CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("start", start, "end", end));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // =========================================================================
    // 8. TÌM KIẾM BÀN THEO KHÁCH HÀNG (Trả về list)
    // =========================================================================
    public List<BanAn> timKiemBanTheoKhachHang(String tuKhoa) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH +
                "OPTIONAL MATCH (kh:KhachHang)<-[:CUA_KHACH_HANG]-(p:PhieuDatBan)-[:DAT_BAN]->(b) " +
                "WHERE p.trangThai = 'Đang chờ' AND (toUpper(kh.hoTen) CONTAINS toUpper($tuKhoa) OR kh.soDienThoai CONTAINS $tuKhoa) " +
                "WITH b, k, t, kh.hoTen AS tenKH1 " +
                "OPTIONAL MATCH (kh2:KhachHang)<-[:CUA_KHACH_HANG]-(hd:HoaDon)-[:SU_DUNG]->(b) " +
                "WHERE hd.trangThai = 'Chưa thanh toán' AND (toUpper(kh2.hoTen) CONTAINS toUpper($tuKhoa) OR kh2.soDienThoai CONTAINS $tuKhoa) " +
                "WITH b, k, t, coalesce(tenKH1, kh2.hoTen) AS tenKhachHang " +
                "WHERE tenKhachHang IS NOT NULL " +
                "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, " +
                "b.sucChua AS sucChua, b.trangThai AS trangThai, k.maKhu AS maKhu, " +
                "k.tenKhu AS tenKhu, t.tenTang AS tenTang, tenKhachHang";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // =========================================================================
    // 9. TÌM BÀN THEO MÃ
    // =========================================================================
    public BanAn timBanAnTheoMa(String maBan) {
        String cypher = CYPHER_BASE_MATCH + "WHERE toUpper(b.maBan) = toUpper($maBan) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maBan", maBan));
            if (result.hasNext()) return mapBanAn(result.next());
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // =========================================================================
    // 10. LỌC THEO LOẠI BÀN
    // =========================================================================
    public List<BanAn> locTheoLoaiBan(String loaiBan) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + "WHERE b.loaiBan = $loaiBan " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("loaiBan", loaiBan));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // =========================================================================
    // 11. TÌM KIẾM BÀN (Theo Mã hoặc Tên)
    // =========================================================================
    public List<BanAn> timKiemBan(String tuKhoa) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + "WHERE toUpper(b.tenBan) CONTAINS toUpper($tuKhoa) OR toUpper(b.maBan) CONTAINS toUpper($tuKhoa) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // =========================================================================
    // 12. LỌC THEO TRẠNG THÁI
    // =========================================================================
    public List<BanAn> locTheoTrangThai(String trangThai) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + "WHERE b.trangThai = $trangThai " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("trangThai", trangThai));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // =========================================================================
    // 13. ĐỌC DANH SÁCH TÊN TẦNG
    // =========================================================================
    public List<String> docDanhSachTenTang() {
        List<String> list = new ArrayList<>();
        String cypher = "MATCH (t:Tang) RETURN t.tenTang AS tenTang";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) list.add(result.next().get("tenTang").asString());
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // =========================================================================
    // 14. ĐỌC DANH SÁCH TÊN KHU THEO TẦNG
    // =========================================================================
    public List<String> docDanhSachTenKhuTheoTang(String tenTang) {
        List<String> list = new ArrayList<>();
        String cypher = "MATCH (t:Tang {tenTang: $tenTang})<-[:THUOC_TANG]-(k:Khu) RETURN k.tenKhu AS tenKhu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tenTang", tenTang));
            while (result.hasNext()) list.add(result.next().get("tenKhu").asString());
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // =========================================================================
    // 15. LỌC BÀN ĂN NÂNG CAO (Tầng, Khu, Loại)
    // =========================================================================
    public List<BanAn> locBanAn(String tenTang, String tenKhu, String loaiBan) {
        List<BanAn> list = new ArrayList<>();
        StringBuilder cypher = new StringBuilder(CYPHER_BASE_MATCH + "WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (tenTang != null && !tenTang.isEmpty() && !tenTang.equals("Tất cả")) {
            cypher.append("AND t.tenTang = $tenTang ");
            params.put("tenTang", tenTang);
        }
        if (tenKhu != null && !tenKhu.isEmpty() && !tenKhu.equals("Tất cả")) {
            cypher.append("AND k.tenKhu = $tenKhu ");
            params.put("tenKhu", tenKhu);
        }
        if (loaiBan != null && !loaiBan.isEmpty() && !loaiBan.equals("Tất cả")) {
            cypher.append("AND b.loaiBan = $loaiBan ");
            params.put("loaiBan", loaiBan);
        }
        cypher.append(CYPHER_RETURN);

        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher.toString(), params);
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // =========================================================================
    // CÁC HÀM TIỆN ÍCH KHÁC TỪ FILE CŨ
    // =========================================================================
    public List<BanAn> layDanhSachBanTheoKhuVuc(String tenKhu) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + "WHERE k.tenKhu = $tenKhu " + CYPHER_RETURN;
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
        String cypher = "MATCH (b:BanAn {loaiBan: $loaiBan}) RETURN count(b) + 1 AS soThuTu";
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
package dao_impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

import connect.DBConnect;
import entity.BanAn;

public class BanAn_DAO {

    private final String SQL_BASE_SELECT = 
        "SELECT B.*, KH.hoTen AS tenKhachHang, K.tenKhu, T.tenTang " +
        "FROM Ban B " +
        "JOIN Khu K ON B.maKhu = K.maKhu " + 
        "JOIN Tang T ON K.maTang = T.maTang " + 
        "LEFT JOIN HoaDon_Ban HDB ON B.maBan = HDB.maBan " +
        "LEFT JOIN HoaDon HD ON HDB.maHoaDon = HD.maHoaDon AND HD.trangThai = N'Chưa thanh toán' " +
        "LEFT JOIN KhachHang KH ON HD.maKhachHang = KH.maKhachHang";

    // Chuyển đổi ResultSet sang đối tượng BanAn
    private BanAn mapBanAn(ResultSet rs) throws SQLException {
        BanAn banAn = new BanAn(
            rs.getString("maBan"),
            rs.getString("tenBan"),
            rs.getString("loaiBan"),
            rs.getInt("sucChua"), 
            rs.getString("trangThai"),
            rs.getString("maKhu") 
        );
        banAn.setTenKhachHang(rs.getString("tenKhachHang"));
        banAn.setTenKhu(rs.getString("tenKhu")); 
        banAn.setTenTang(rs.getString("tenTang")); 
        return banAn;
    }

    // Lấy mã bàn tự động tiếp theo
    public String layMaBanTiepTheo() {
        String sql = "SELECT COUNT(*) + 1 as soThuTu FROM Ban";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int soThuTu = rs.getInt("soThuTu");
                return String.format("MB%06d", soThuTu);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy mã bàn tiếp theo: " + e.getMessage());
        }
        return "MB000001";
    }

    // Lấy số thứ tự tiếp theo theo loại bàn
    public int laySoThuTuBanTiepTheo(String loaiBan) {
        String sql = "SELECT COUNT(*) + 1 as soThuTu FROM Ban WHERE loaiBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, loaiBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("soThuTu");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy số thứ tự bàn: " + e.getMessage());
        }
        return 1;
    }

    // Thêm bàn mới
    public boolean themBanMoi(BanAn banAn) {
        String sql = "INSERT INTO Ban (maBan, tenBan, loaiBan, sucChua, trangThai, maKhu) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, banAn.getMaBan());
            stmt.setString(2, banAn.getTenBan());
            stmt.setString(3, banAn.getLoaiBan());
            stmt.setInt(4, banAn.getSucChua());
            stmt.setString(5, banAn.getTrangThai());
            stmt.setString(6, banAn.getMaKhu()); 
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm bàn mới: " + e.getMessage());
            return false;
        }
    }

    // Đọc danh sách toàn bộ bàn ăn
    public List<BanAn> docDanhSachBan() {
        List<BanAn> danhSachBan = new ArrayList<>();
        String sql = SQL_BASE_SELECT; 
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                danhSachBan.add(mapBanAn(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đọc dữ liệu Bàn Ăn: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachBan;
    }
    
    // Tìm kiếm bàn theo mã hoặc tên
    public List<BanAn> timKiemBan(String tuKhoa) {
        List<BanAn> danhSachBan = new ArrayList<>();
        String sql = SQL_BASE_SELECT + " WHERE UPPER(B.maBan) = ? OR UPPER(B.tenBan) LIKE ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tuKhoa.toUpperCase());
            stmt.setString(2, "%" + tuKhoa.toUpperCase() + "%"); 
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSachBan.add(mapBanAn(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm Bàn Ăn: " + e.getMessage());
        }
        return danhSachBan;
    }
    
    // Tìm bàn ăn theo mã
    public BanAn timBanAnTheoMa(String maBan) {
        String sql = SQL_BASE_SELECT + " WHERE B.maBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapBanAn(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm bàn ăn theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Lọc bàn theo loại bàn
    public List<BanAn> locTheoLoaiBan(String loaiBan) {
        List<BanAn> danhSachBan = new ArrayList<>();
        String sql = SQL_BASE_SELECT + " WHERE B.loaiBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, loaiBan);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSachBan.add(mapBanAn(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lọc Bàn Ăn theo loại: " + e.getMessage());
        }
        return danhSachBan;
    }
    
    // Lọc bàn theo trạng thái
    public List<BanAn> locTheoTrangThai(String trangThai) {
        List<BanAn> danhSachBan = new ArrayList<>();
        String sql = SQL_BASE_SELECT + " WHERE B.trangThai = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThai);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSachBan.add(mapBanAn(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lọc Bàn Ăn theo trạng thái: " + e.getMessage());
        }
        return danhSachBan;
    }
    
    // Đọc danh sách tên các tầng
    public List<String> docDanhSachTenTang() {
        List<String> danhSachTang = new ArrayList<>();
        danhSachTang.add("Tất cả");
        String sql = "SELECT DISTINCT tenTang FROM Tang ORDER BY tenTang";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                danhSachTang.add(rs.getString("tenTang"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đọc danh sách tầng: " + e.getMessage());
        }
        return danhSachTang;
    }

    // Đọc danh sách tên khu theo tầng
    public List<String> docDanhSachTenKhuTheoTang(String tenTang) {
        List<String> danhSachKhu = new ArrayList<>();
        danhSachKhu.add("Tất cả");
        String sql;
        if (tenTang == null || "Tất cả".equals(tenTang)) {
            sql = "SELECT DISTINCT tenKhu FROM Khu ORDER BY tenKhu";
        } else {
            sql = "SELECT K.tenKhu FROM Khu K JOIN Tang T ON K.maTang = T.maTang WHERE T.tenTang = ? ORDER BY K.tenKhu";
        }
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (tenTang != null && !"Tất cả".equals(tenTang)) {
                stmt.setString(1, tenTang);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSachKhu.add(rs.getString("tenKhu"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đọc danh sách khu theo tầng: " + e.getMessage());
        }
        return danhSachKhu;
    }

    // Lọc bàn ăn theo tầng, khu và loại
    public List<BanAn> locBanAn(String tenTang, String tenKhu, String loaiBan) {
        List<BanAn> danhSachBan = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SQL_BASE_SELECT);
        sql.append(" WHERE 1=1"); 
        List<Object> params = new ArrayList<>();
        if (tenTang != null && !"Tất cả".equals(tenTang)) {
            sql.append(" AND T.tenTang = ?");
            params.add(tenTang);
        }
        if (tenKhu != null && !"Tất cả".equals(tenKhu)) {
            sql.append(" AND K.tenKhu = ?");
            params.add(tenKhu);
        }
        if (loaiBan != null && !"Tất cả".equals(loaiBan)) {
            sql.append(" AND B.loaiBan = ?");
            params.add(loaiBan);
        }
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSachBan.add(mapBanAn(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lọc Bàn Ăn: " + e.getMessage());
        }
        return danhSachBan;
    }

    // Cập nhật trạng thái bàn ăn
    public boolean capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
        String sql = "UPDATE Ban SET trangThai = ? WHERE maBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật trạng thái Bàn Ăn: " + e.getMessage());
            return false;
        }
    }
    
    // Cập nhật thông tin bàn ăn
    public boolean capNhatBan(BanAn banAn) {
        String sql = "UPDATE Ban SET tenBan = ?, loaiBan = ?, sucChua = ?, maKhu = ? WHERE maBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, banAn.getTenBan());
            stmt.setString(2, banAn.getLoaiBan());
            stmt.setInt(3, banAn.getSucChua());
            stmt.setString(4, banAn.getMaKhu()); 
            stmt.setString(5, banAn.getMaBan()); 
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật thông tin Bàn Ăn: " + e.getMessage());
            return false;
        }
    }
    
    // Xóa bàn ăn theo mã
    public boolean xoaBan(String maBan) {
        String sql = "DELETE FROM Ban WHERE maBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa Bàn Ăn: " + e.getMessage());
            return false;
        }
    }
    
    // Tìm bàn ăn theo tên hoặc SĐT khách hàng đang sử dụng
    public BanAn timBanAnTheoTenHoacSDTKhachHang(String tuKhoa) {
        String sql = "SELECT B.*, KH.hoTen AS tenKhachHang, K.tenKhu, T.tenTang " + 
                     "FROM Ban B " +
                     "JOIN Khu K ON B.maKhu = K.maKhu " + 
                     "JOIN Tang T ON K.maTang = T.maTang " + 
                     "JOIN HoaDon_Ban HDB ON B.maBan = HDB.maBan " +
                     "JOIN HoaDon HD ON HDB.maHoaDon = HD.maHoaDon " +
                     "JOIN KhachHang KH ON HD.maKhachHang = KH.maKhachHang " +
                     "WHERE HD.trangThai = N'Chưa thanh toán' " +
                     "AND (UPPER(KH.hoTen) LIKE ? OR KH.soDienThoai LIKE ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String keyword = "%" + tuKhoa.toUpperCase() + "%";
            stmt.setString(1, keyword); 
            stmt.setString(2, keyword); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapBanAn(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm bàn theo SĐT hoặc tên khách hàng: " + e.getMessage());
        }
        return null; 
    }

    // Lấy danh sách bàn trống theo ngày
    public List<BanAn> layDanhSachBanTrongTheoNgay(Date ngayCanXem) {
        Date homNay = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        boolean isHomNay = sdf.format(ngayCanXem).equals(sdf.format(homNay));
        return thucThiLayBanTrongTheoNgay(ngayCanXem, isHomNay);
    }

    // Thực thi truy vấn lấy bàn trống theo ngày
    private List<BanAn> thucThiLayBanTrongTheoNgay(Date ngayCanXem, boolean isHomNay) {
        Map<String, BanAn> mapBanDuyNhat = new LinkedHashMap<>();
        String baseSql = "SELECT B.*, KH.hoTen AS tenKhachHang, K.tenKhu, T.tenTang " +
                         "FROM Ban B " +
                         "JOIN Khu K ON B.maKhu = K.maKhu " +
                         "JOIN Tang T ON K.maTang = T.maTang " +
                         "LEFT JOIN HoaDon_Ban HDB ON B.maBan = HDB.maBan " +
                         "LEFT JOIN HoaDon HD ON HDB.maHoaDon = HD.maHoaDon AND HD.trangThai = N'Chưa thanh toán' " +
                         "LEFT JOIN KhachHang KH ON HD.maKhachHang = KH.maKhachHang ";
        StringBuilder sql = new StringBuilder(baseSql);
        sql.append(" WHERE NOT EXISTS (" +
                   "    SELECT 1 FROM PhieuDatBan P " +
                   "    JOIN PhieuDatBan_Ban PDB ON P.maPhieuDatBan = PDB.maPhieuDatBan " +
                   "    WHERE PDB.maBan = B.maBan " +
                   "    AND P.trangThai = N'Đang chờ' " +
                   "    AND CAST(P.thoiGianDat AS DATE) = CAST(? AS DATE)" +
                   ")");
        if (isHomNay) {
            sql.append(" AND B.trangThai = N'Bàn đang trống'");
        }
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setDate(1, new java.sql.Date(ngayCanXem.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maBan = rs.getString("maBan");
                    if (!mapBanDuyNhat.containsKey(maBan)) {
                        BanAn ban = mapBanAn(rs);
                        if (!isHomNay) {
                            ban.setTrangThai("Bàn đang trống");
                        }
                        mapBanDuyNhat.put(maBan, ban);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(mapBanDuyNhat.values());
    }
    
    // Tìm kiếm bàn ăn theo thông tin khách hàng (hiện tại và đặt trước)
    public List<BanAn> timKiemBanTheoKhachHang(String tuKhoa) {
        List<BanAn> danhSach = new ArrayList<>();
        // SỬ DỤNG CASE WHEN ĐỂ GÁN TRẠNG THÁI CHÍNH XÁC DỰA TRÊN DỮ LIỆU TÌM THẤY
        // Thay vì lấy B.trangThai (có thể bị sai/chưa cập nhật), ta ưu tiên trạng thái từ Hóa đơn hoặc Phiếu đặt
        String sql = "SELECT DISTINCT B.maBan, B.tenBan, B.loaiBan, B.sucChua, B.maKhu, " +
                     "CASE " +
                     "   WHEN HD.maHoaDon IS NOT NULL THEN N'Bàn đang phục vụ' " +
                     "   WHEN PDB.maPhieuDatBan IS NOT NULL THEN N'Bàn đang chờ' " +
                     "   ELSE B.trangThai " +
                     "END AS trangThai, " +
                     "K.tenKhu, T.tenTang, " +
                     "ISNULL(KH_HD.hoTen, KH_PDB.hoTen) AS tenKhachHang " +
                     "FROM Ban B " +
                     "JOIN Khu K ON B.maKhu = K.maKhu " +
                     "JOIN Tang T ON K.maTang = T.maTang " +
                     "LEFT JOIN HoaDon_Ban HDB ON B.maBan = HDB.maBan " +
                     "LEFT JOIN HoaDon HD ON HDB.maHoaDon = HD.maHoaDon AND HD.trangThai = N'Chưa thanh toán' " +
                     "LEFT JOIN KhachHang KH_HD ON HD.maKhachHang = KH_HD.maKhachHang " +
                     "LEFT JOIN PhieuDatBan_Ban PDBB ON B.maBan = PDBB.maBan " +
                     "LEFT JOIN PhieuDatBan PDB ON PDBB.maPhieuDatBan = PDB.maPhieuDatBan AND PDB.trangThai = N'Đang chờ' " +
                     "LEFT JOIN KhachHang KH_PDB ON PDB.maKhachHang = KH_PDB.maKhachHang " +
                     "WHERE (UPPER(KH_HD.hoTen) LIKE ? OR KH_HD.soDienThoai LIKE ? " +
                     "OR UPPER(KH_PDB.hoTen) LIKE ? OR KH_PDB.soDienThoai LIKE ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String keyword = "%" + tuKhoa.toUpperCase() + "%";
            for(int i = 1; i <= 4; i++) stmt.setString(i, keyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapBanAn(rs));
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return danhSach;
    }
}
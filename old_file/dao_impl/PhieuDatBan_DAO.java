package dao_impl;

import connect.DBConnect;
import entity.PhieuDatBan;
import entity.PhieuDatBan_Ban;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatBan_DAO {
    
    // Ánh xạ ResultSet sang đối tượng PhieuDatBan
    private PhieuDatBan mapPhieuDatBan(ResultSet rs) throws SQLException {
        return new PhieuDatBan(
            rs.getString("maPhieuDatBan"),
            rs.getTimestamp("thoiGianDat"),
            rs.getString("trangThai"),
            rs.getString("maKhachHang"),
            rs.getString("maNhanVien"),
            rs.getDouble("tienDatCoc"),
            rs.getString("ghiChu")
        );
    }

    // Lấy danh sách phiếu đặt đang chờ trong hôm nay
    public List<PhieuDatBan> getPhieuDatBanChoHomNay() {
        List<PhieuDatBan> danhSachPhieu = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDatBan WHERE trangThai = N'Đang chờ' AND CAST(thoiGianDat AS DATE) = CAST(GETDATE() AS DATE)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) danhSachPhieu.add(mapPhieuDatBan(rs));
        } catch (SQLException e) {
            System.err.println("Lỗi lấy phiếu đặt bàn chờ hôm nay: " + e.getMessage());
        }
        return danhSachPhieu;
    }
    
    // Lấy thông tin mã bàn và tên khách của phiếu đang chờ
    public java.util.Map<String, String> layThongTinBanDatVaTenKhach(java.util.Date ngayCanXem) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        String sql = "SELECT pb.maBan, k.hoTen FROM PhieuDatBan p JOIN PhieuDatBan_Ban pb ON p.maPhieuDatBan = pb.maPhieuDatBan JOIN KhachHang k ON p.maKhachHang = k.maKhachHang WHERE p.trangThai = N'Đang chờ' AND CAST(p.thoiGianDat AS DATE) = CAST(? AS DATE)";
        try (java.sql.Connection conn = connect.DBConnect.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(ngayCanXem.getTime()));
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) map.put(rs.getString("maBan"), rs.getString("hoTen"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    
    // Sinh mã phiếu đặt tự động
    public String sinhMaPhieuDatTuDong() {
        String maMoi = "PDB00001";
        String sql = "SELECT TOP 1 maPhieuDatBan FROM PhieuDatBan ORDER BY maPhieuDatBan DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maCuoi = rs.getString("maPhieuDatBan");
                if (maCuoi.startsWith("PDB")) {
                    try {
                        int so = Integer.parseInt(maCuoi.substring(3));
                        maMoi = String.format("PDB%05d", so + 1);
                    } catch (NumberFormatException e) { e.printStackTrace(); }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return maMoi;
    }
    
    // Thêm phiếu đặt bàn mới
    public boolean themPhieuDatBan(PhieuDatBan phieu) {
        String sql = "INSERT INTO PhieuDatBan(maPhieuDatBan, thoiGianDat, trangThai, maKhachHang, maNhanVien, tienDatCoc, ghiChu) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phieu.getMaPhieuDatBan());
            stmt.setTimestamp(2, new java.sql.Timestamp(phieu.getThoiGianDat().getTime()));
            stmt.setString(3, phieu.getTrangThai());
            stmt.setString(4, phieu.getMaKhachHang());
            stmt.setString(5, phieu.getMaNhanVien());
            stmt.setDouble(6, phieu.getTienDatCoc());
            stmt.setString(7, phieu.getGhiChu());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // Thêm liên kết phiếu đặt và bàn vào bảng trung gian
    public boolean themPhieuDatBan_Ban(PhieuDatBan_Ban phieuBan) {
        String sql = "INSERT INTO PhieuDatBan_Ban(maPhieuDatBan, maBan) VALUES(?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phieuBan.getMaPhieuDatBan());
            stmt.setString(2, phieuBan.getMaBan());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // Lấy danh sách mã bàn đã đặt theo ngày
    public List<String> layDanhSachMaBanDaDatTheoNgay(java.util.Date ngayCanXem) {
        List<String> dsMaBan = new ArrayList<>();
        String sql = "SELECT hdb.maBan FROM PhieuDatBan p JOIN PhieuDatBan_Ban hdb ON p.maPhieuDatBan = hdb.maPhieuDatBan WHERE p.trangThai = N'Đang chờ' AND CAST(p.thoiGianDat AS DATE) = CAST(? AS DATE)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(ngayCanXem.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) dsMaBan.add(rs.getString("maBan"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return dsMaBan;
    }
    
    // Hủy trạng thái phiếu đặt bàn
    public boolean huyDatBan(String maBan, java.util.Date ngayDat) {
        String sql = "UPDATE PhieuDatBan SET trangThai = N'Đã hủy' WHERE maPhieuDatBan IN ( SELECT p.maPhieuDatBan FROM PhieuDatBan p JOIN PhieuDatBan_Ban hdb ON p.maPhieuDatBan = hdb.maPhieuDatBan WHERE hdb.maBan = ? AND p.trangThai = N'Đang chờ' AND CAST(p.thoiGianDat AS DATE) = CAST(? AS DATE) )";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBan);
            stmt.setDate(2, new java.sql.Date(ngayDat.getTime()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // Chuyển bàn đã đặt trước
    public boolean chuyenBanDatTruoc(String maBanCu, String maBanMoi, java.util.Date ngayDat) {
        String sql = "UPDATE PhieuDatBan_Ban SET maBan = ? WHERE maBan = ? AND maPhieuDatBan IN ( SELECT maPhieuDatBan FROM PhieuDatBan WHERE trangThai = N'Đang chờ' AND CAST(thoiGianDat AS DATE) = CAST(? AS DATE) )";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBanMoi);
            stmt.setString(2, maBanCu);
            stmt.setDate(3, new java.sql.Date(ngayDat.getTime()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // Lấy phiếu đặt bàn theo mã
    public entity.PhieuDatBan getPhieuDatBanTheoMa(String maPhieu) {
        entity.PhieuDatBan phieu = null;
        String sql = "SELECT * FROM PhieuDatBan WHERE maPhieuDatBan = ?";
        try (java.sql.Connection conn = connect.DBConnect.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieu);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) phieu = mapPhieuDatBan(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return phieu;
    }
    
    // Tìm mã phiếu đặt đang chờ theo mã bàn
    public String timMaPhieuDatDangChoTheoBan(String maBan, java.util.Date ngayDat) {
        String maPhieu = null;
        String sql = "SELECT p.maPhieuDatBan FROM PhieuDatBan p JOIN PhieuDatBan_Ban pb ON p.maPhieuDatBan = pb.maPhieuDatBan WHERE pb.maBan = ? AND p.trangThai = N'Đang chờ' AND CAST(p.thoiGianDat AS DATE) = CAST(? AS DATE)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBan);
            stmt.setDate(2, new java.sql.Date(ngayDat.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) maPhieu = rs.getString("maPhieuDatBan");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return maPhieu;
    }

    // Cập nhật trạng thái phiếu đặt bàn
    public boolean capNhatTrangThai(String maPhieu, String trangThaiMoi) {
        String sql = "UPDATE PhieuDatBan SET trangThai = ? WHERE maPhieuDatBan = ?";
        try (java.sql.Connection conn = connect.DBConnect.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, maPhieu);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    // Cập nhật tiền đặt cọc
    public boolean capNhatTienCoc(String maPhieuDatBan, double tienDatCoc) {
        String sql = "UPDATE PhieuDatBan SET tienDatCoc = ? WHERE maPhieuDatBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, tienDatCoc);
            stmt.setString(2, maPhieuDatBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật tiền cọc: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy danh sách mã bàn theo phiếu đặt
    public List<String> layDanhSachMaBanTheoPhieuDat(String maPhieu) {
        List<String> dsMaBan = new ArrayList<>();
        String sql = "SELECT maBan FROM PhieuDatBan_Ban WHERE maPhieuDatBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) dsMaBan.add(rs.getString("maBan"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return dsMaBan;
    }
    
    // Tự động hủy phiếu đặt quá giờ quy định
    public int huyPhieuDatQuaGio(int phutTreChoPhep) {
        int rowCount = 0;
        Connection conn = null;
        PreparedStatement stmtUpdateBan = null;
        PreparedStatement stmtUpdatePhieu = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); 
            String sqlBan = "UPDATE Ban SET trangThai = N'Bàn đang trống' WHERE maBan IN ( SELECT pb.maBan FROM PhieuDatBan_Ban pb JOIN PhieuDatBan p ON pb.maPhieuDatBan = p.maPhieuDatBan WHERE p.trangThai = N'Đang chờ' AND DATEADD(MINUTE, ?, p.thoiGianDat) < GETDATE() )";
            stmtUpdateBan = conn.prepareStatement(sqlBan);
            stmtUpdateBan.setInt(1, phutTreChoPhep);
            stmtUpdateBan.executeUpdate();
            String sqlPhieu = "UPDATE PhieuDatBan SET trangThai = N'Đã hủy' WHERE trangThai = N'Đang chờ' AND DATEADD(MINUTE, ?, thoiGianDat) < GETDATE()";
            stmtUpdatePhieu = conn.prepareStatement(sqlPhieu);
            stmtUpdatePhieu.setInt(1, phutTreChoPhep);
            rowCount = stmtUpdatePhieu.executeUpdate();
            conn.commit(); 
        } catch (SQLException e) {
            System.err.println("Lỗi tự động hủy phiếu: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (stmtUpdateBan != null) stmtUpdateBan.close();
                if (stmtUpdatePhieu != null) stmtUpdatePhieu.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return rowCount;
    }
}
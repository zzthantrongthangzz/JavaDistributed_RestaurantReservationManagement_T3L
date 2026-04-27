package dao_impl;

import connect.DBConnect;
import entity.HoaDon_Ban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_Ban_DAO {

    // Thêm liên kết hóa đơn và bàn
    public boolean themHoaDon_Ban(String maHoaDon, String maBan) {
        String sql = "INSERT INTO HoaDon_Ban(maHoaDon, maBan) VALUES(?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            stmt.setString(2, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm vào bảng HoaDon_Ban: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Thêm liên kết dựa trên đối tượng HoaDon_Ban
    public boolean themHoaDon_Ban(HoaDon_Ban hdb) {
        return themHoaDon_Ban(hdb.getMaHoaDon(), hdb.getMaBan());
    }

    // Cập nhật chuyển bàn trong hóa đơn
    public boolean chuyenBan(String maHoaDon, String maBanCu, String maBanMoi) {
        String sql = "UPDATE HoaDon_Ban SET maBan = ? WHERE maHoaDon = ? AND maBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBanMoi);
            stmt.setString(2, maHoaDon);
            stmt.setString(3, maBanCu);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách mã bàn theo hóa đơn
    public List<String> layDanhSachMaBanTheoHoaDon(String maHoaDon) {
        List<String> dsMaBan = new ArrayList<>();
        String sql = "SELECT maBan FROM HoaDon_Ban WHERE maHoaDon = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dsMaBan.add(rs.getString("maBan"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsMaBan;
    }
    
    // Xóa liên kết hóa đơn và bàn cụ thể
    public boolean xoaHoaDon_Ban(String maHoaDon, String maBan) {
        String sql = "DELETE FROM HoaDon_Ban WHERE maHoaDon = ? AND maBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            stmt.setString(2, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa tất cả các bàn thuộc về một hóa đơn
    public boolean xoaTatCaBanCuaHoaDon(String maHoaDon) {
        String sql = "DELETE FROM HoaDon_Ban WHERE maHoaDon = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
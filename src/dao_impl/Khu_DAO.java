package dao_impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import connect.DBConnect;
import entity.Khu;

public class Khu_DAO {

    // Lấy mã khu dựa vào tên khu
    public String layMaKhuTheoTen(String tenKhu) {
        String sql = "SELECT maKhu FROM Khu WHERE tenKhu = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenKhu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("maKhu");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy mã khu theo tên: " + e.getMessage());
        }
        return null;
    }
    
    // Tìm đối tượng Khu dựa vào tên
    public Khu timKhuTheoTen(String tenKhu) {
        String sql = "SELECT * FROM Khu WHERE tenKhu = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenKhu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Khu(rs.getString("maKhu"), rs.getString("tenKhu"), rs.getString("maTang"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm khu theo tên: " + e.getMessage());
        }
        return null;
    }

    // Sinh mã khu tự động
    public String sinhMaKhuTuDong() {
        String sql = "SELECT COUNT(*) + 1 as soThuTu FROM Khu";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return String.format("K%02d", rs.getInt("soThuTu"));
        } catch (SQLException e) {
            System.err.println("Lỗi sinh mã khu: " + e.getMessage());
        }
        return "K01";
    }

    // Thêm khu mới vào hệ thống
    public boolean themKhu(Khu khu) {
        String sql = "INSERT INTO Khu (maKhu, tenKhu, maTang) VALUES (?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, khu.getMaKhu());
            stmt.setString(2, khu.getTenKhu());
            stmt.setString(3, khu.getMaTang());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm khu mới: " + e.getMessage());
            return false;
        }
    }
}
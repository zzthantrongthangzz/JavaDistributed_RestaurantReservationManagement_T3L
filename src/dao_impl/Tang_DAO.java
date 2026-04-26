package dao_impl;

import connect.DBConnect;
import entity.Tang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Tang_DAO {

    // Lấy danh sách tên tất cả các tầng
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

    // Tìm thông tin tầng theo tên tầng
    public Tang timTangTheoTen(String tenTang) {
        String sql = "SELECT * FROM Tang WHERE tenTang = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenTang);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Tang(rs.getString("maTang"), rs.getString("tenTang"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm tầng theo tên: " + e.getMessage());
        }
        return null;
    }

    // Tự động sinh mã tầng mới (T01, T02...)
    public String sinhMaTangTuDong() {
        String sql = "SELECT COUNT(*) + 1 as soThuTu FROM Tang";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return String.format("T%02d", rs.getInt("soThuTu"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi sinh mã tầng: " + e.getMessage());
        }
        return "T01";
    }

    // Lưu thông tin tầng mới vào cơ sở dữ liệu
    public boolean themTang(Tang tang) {
        String sql = "INSERT INTO Tang (maTang, tenTang) VALUES (?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tang.getMaTang());
            stmt.setString(2, tang.getTenTang());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm tầng mới: " + e.getMessage());
            return false;
        }
    }
}
package dao_impl;

import connect.DBConnect;
import entity.ChiTietHoaDon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO {

    // Lấy toàn bộ danh sách chi tiết hóa đơn
    public List<ChiTietHoaDon> getAllChiTietHoaDon() {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maMon, soLuong, donGia FROM ChiTietHoaDon";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new ChiTietHoaDon(
                        rs.getString("maHoaDon"),
                        rs.getString("maMon"),
                        rs.getInt("soLuong"),
                        rs.getBigDecimal("donGia")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách chi tiết theo mã hóa đơn
    public List<ChiTietHoaDon> getChiTietTheoMaHoaDon(String maHoaDon) {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHoaDon = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ds.add(new ChiTietHoaDon(
                    rs.getString("maHoaDon"),
                    rs.getString("maMon"),
                    rs.getInt("soLuong"),
                    rs.getBigDecimal("donGia")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // Thêm mới chi tiết hóa đơn
    public boolean themChiTietHoaDon(ChiTietHoaDon cthd) {
        String sql = "INSERT INTO ChiTietHoaDon (maHoaDon, maMon, soLuong, donGia) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cthd.getMaHoaDon());
            stmt.setString(2, cthd.getMaMon());
            stmt.setInt(3, cthd.getSoLuong());
            stmt.setBigDecimal(4, cthd.getDonGia());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật chi tiết hóa đơn
    public boolean capNhatChiTietHoaDon(ChiTietHoaDon cthd) {
        String sql = "UPDATE ChiTietHoaDon SET soLuong=?, donGia=? WHERE maHoaDon=? AND maMon=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cthd.getSoLuong());
            stmt.setBigDecimal(2, cthd.getDonGia());
            stmt.setString(3, cthd.getMaHoaDon());
            stmt.setString(4, cthd.getMaMon());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa một món trong hóa đơn
    public boolean xoaChiTietHoaDon(String maHoaDon, String maMon) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHoaDon=? AND maMon=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            stmt.setString(2, maMon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm chi tiết hóa đơn cụ thể theo mã hóa đơn và mã món
    public ChiTietHoaDon timChiTiet(String maHoaDon, String maMon) {
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHoaDon=? AND maMon=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            stmt.setString(2, maMon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ChiTietHoaDon(
                            rs.getString("maHoaDon"),
                            rs.getString("maMon"),
                            rs.getInt("soLuong"),
                            rs.getBigDecimal("donGia")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Xóa toàn bộ món của một hóa đơn
    public boolean xoaChiTietTheoMaHoaDon(String maHoaDon) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHoaDon=?";
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
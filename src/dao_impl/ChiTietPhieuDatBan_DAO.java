package dao_impl;

import connect.DBConnect;
import entity.ChiTietPhieuDatBan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuDatBan_DAO {

    // Thêm chi tiết phiếu đặt
    public boolean themChiTietPhieuDat(ChiTietPhieuDatBan ct) {
        String sql = "INSERT INTO ChiTietPhieuDatBan(maPhieuDatBan, maMon, soLuong, donGia) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ct.getMaPhieuDatBan());
            stmt.setString(2, ct.getMaMon());
            stmt.setInt(3, ct.getSoLuong());
            stmt.setBigDecimal(4, ct.getDonGia());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách chi tiết theo mã phiếu đặt
    public List<ChiTietPhieuDatBan> getChiTietTheoPhieu(String maPhieuDatBan) {
        List<ChiTietPhieuDatBan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuDatBan WHERE maPhieuDatBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieuDatBan);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(new ChiTietPhieuDatBan(
                        rs.getString("maPhieuDatBan"),
                        rs.getString("maMon"),
                        rs.getInt("soLuong"),
                        rs.getBigDecimal("donGia")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // Xóa tất cả chi tiết theo mã phiếu đặt
    public boolean xoaChiTietTheoPhieu(String maPhieuDatBan) {
        String sql = "DELETE FROM ChiTietPhieuDatBan WHERE maPhieuDatBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieuDatBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật chi tiết phiếu đặt
    public boolean capNhatChiTiet(ChiTietPhieuDatBan ct) {
        String sql = "UPDATE ChiTietPhieuDatBan SET soLuong = ?, donGia = ? WHERE maPhieuDatBan = ? AND maMon = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ct.getSoLuong());
            stmt.setBigDecimal(2, ct.getDonGia());
            stmt.setString(3, ct.getMaPhieuDatBan());
            stmt.setString(4, ct.getMaMon());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
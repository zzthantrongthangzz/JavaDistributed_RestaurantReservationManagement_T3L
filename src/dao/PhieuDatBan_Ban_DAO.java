package dao;

import connect.DBConnect;
import entity.BanAn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatBan_Ban_DAO {

    // Lấy danh sách bàn ăn theo mã phiếu đặt
    public List<BanAn> getDanhSachBanTheoPhieu(String maPhieu) {
        List<BanAn> list = new ArrayList<>();
        String sql = "SELECT b.* FROM PhieuDatBan_Ban pdb JOIN Ban b ON pdb.maBan = b.maBan WHERE pdb.maPhieuDatBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new BanAn(
                        rs.getString("maBan"),
                        rs.getString("tenBan"),
                        rs.getString("loaiBan"),
                        rs.getInt("sucChua"),
                        rs.getString("trangThai"),
                        rs.getString("maKhu")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Thêm liên kết phiếu đặt và bàn
    public boolean themPhieuDatBan_Ban(String maPhieu, String maBan) {
        String sql = "INSERT INTO PhieuDatBan_Ban(maPhieuDatBan, maBan) VALUES(?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieu);
            stmt.setString(2, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Đếm số lượng bàn trong một phiếu đặt
    public int demSoBanCuaPhieu(String maPhieu) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM PhieuDatBan_Ban WHERE maPhieuDatBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    // Xóa một bàn cụ thể khỏi phiếu đặt
    public boolean xoaBanKhoiPhieu(String maPhieu, String maBan) {
        String sql = "DELETE FROM PhieuDatBan_Ban WHERE maPhieuDatBan = ? AND maBan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieu);
            stmt.setString(2, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
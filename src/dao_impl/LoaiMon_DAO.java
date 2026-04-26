package dao_impl;

import connect.DBConnect;
import entity.LoaiMon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoaiMon_DAO {

    // Ánh xạ ResultSet sang đối tượng LoaiMon
    private LoaiMon mapLoaiMon(ResultSet rs) throws SQLException {
        return new LoaiMon(rs.getString("maLoai"), rs.getString("tenLoai"));
    }

    // Đọc danh sách toàn bộ loại món
    public List<LoaiMon> docDanhSachLoaiMon() {
        List<LoaiMon> dsLoai = new ArrayList<>();
        String sql = "SELECT * FROM LoaiMon";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) dsLoai.add(mapLoaiMon(rs));
        } catch (SQLException e) {
            System.err.println("Lỗi đọc dữ liệu LoaiMon: " + e.getMessage());
        }
        return dsLoai;
    }

    // Thêm loại món mới
    public boolean themLoaiMon(LoaiMon loaiMon) {
        String sql = "INSERT INTO LoaiMon (maLoai, tenLoai) VALUES (?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, loaiMon.getMaLoai());
            stmt.setString(2, loaiMon.getTenLoai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm loại món: " + e.getMessage());
            return false;
        }
    }

    // Sinh mã loại món tự động
    public String sinhMaLoaiTuDong() {
        String maMoi = "LM000001";
        String sql = "SELECT MAX(maLoai) AS maxMa FROM LoaiMon";
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maxMa = rs.getString("maxMa");
                if (maxMa != null && maxMa.startsWith("LM")) {
                    try {
                        int soThuTu = Integer.parseInt(maxMa.substring(2)) + 1;
                        maMoi = String.format("LM%06d", soThuTu);
                    } catch (NumberFormatException e) {
                         System.err.println("Lỗi định dạng mã loại món: " + maxMa);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi sinh mã loại món tự động: " + e.getMessage());
        }
        return maMoi;
    }
    
    // Tìm loại món theo tên
    public LoaiMon timLoaiTheoTen(String tenLoai) {
        String sql = "SELECT * FROM LoaiMon WHERE tenLoai = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenLoai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapLoaiMon(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm loại món theo tên: " + e.getMessage());
        }
        return null;
    }
}
package dao_impl;

import connect.DBConnect;
import entity.ChucVu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChucVu_DAO {

    // Ánh xạ ResultSet sang đối tượng ChucVu
    private ChucVu mapChucVu(ResultSet rs) throws SQLException {
        return new ChucVu(rs.getString("maChucVu"), rs.getString("tenChucVu"));
    }

    // Đọc toàn bộ danh sách chức vụ
    public List<ChucVu> docDanhSachChucVu() {
        List<ChucVu> ds = new ArrayList<>();
        String sql = "SELECT * FROM ChucVu";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ds.add(mapChucVu(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đọc dữ liệu ChucVu: " + e.getMessage());
        }
        return ds;
    }

    // Thêm chức vụ mới
    public boolean themChucVu(ChucVu chucVu) {
        String sql = "INSERT INTO ChucVu (maChucVu, tenChucVu) VALUES (?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chucVu.getMaChucVu());
            stmt.setString(2, chucVu.getTenChucVu());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm chức vụ: " + e.getMessage());
            return false;
        }
    }

    // Tìm chức vụ theo tên
    public ChucVu timChucVuTheoTen(String tenCV) {
        String sql = "SELECT * FROM ChucVu WHERE tenChucVu = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenCV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapChucVu(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm chức vụ theo tên: " + e.getMessage());
        }
        return null;
    }

    // Sinh mã chức vụ tự động
    public String sinhMaChucVuTuDong() {
        String maMoi = "CV001"; 
        String sql = "SELECT MAX(maChucVu) AS maxMa FROM ChucVu";
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maxMa = rs.getString("maxMa");
                if (maxMa != null && maxMa.startsWith("CV")) {
                    try {
                        int soThuTu = Integer.parseInt(maxMa.substring(2)) + 1;
                        maMoi = String.format("CV%03d", soThuTu);
                    } catch (NumberFormatException e) {
                         System.err.println("Lỗi định dạng mã chức vụ: " + maxMa);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi sinh mã chức vụ tự động: " + e.getMessage());
        }
        return maMoi;
    }
}
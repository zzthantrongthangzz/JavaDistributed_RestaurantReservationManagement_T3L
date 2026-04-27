package dao_impl;

import connect.DBConnect;
import entity.LichSuHuyDatBan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LichSuHuyDatBan_DAO {

    // Ghi log khi thực hiện hủy đặt bàn
    public boolean ghiLogHuyDatBan(LichSuHuyDatBan log) {
        String sql = "INSERT INTO LichSuHuyDatBan(maPhieuDatBan, tenBan, tenKhachHang, sdtKhachHang, maNhanVien, tenNhanVien, thoiGianHuy, lyDoHuy) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, log.getMaPhieuDatBan());
            stmt.setString(2, log.getTenBan());
            stmt.setString(3, log.getTenKhachHang());
            stmt.setString(4, log.getSdtKhachHang());
            stmt.setString(5, log.getMaNhanVien());
            stmt.setString(6, log.getTenNhanVien());
            stmt.setTimestamp(7, new Timestamp(log.getThoiGianHuy().getTime()));
            stmt.setString(8, log.getLyDoHuy());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy toàn bộ lịch sử hủy đặt bàn
    public List<LichSuHuyDatBan> layTatCaLichSu() {
        List<LichSuHuyDatBan> dsLog = new ArrayList<>();
        String sql = "SELECT * FROM LichSuHuyDatBan ORDER BY thoiGianHuy DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LichSuHuyDatBan log = new LichSuHuyDatBan();
                log.setMaLog(rs.getInt("maLog"));
                log.setMaPhieuDatBan(rs.getString("maPhieuDatBan"));
                log.setTenBan(rs.getString("tenBan"));
                log.setTenKhachHang(rs.getString("tenKhachHang"));
                log.setSdtKhachHang(rs.getString("sdtKhachHang"));
                log.setMaNhanVien(rs.getString("maNhanVien"));
                log.setTenNhanVien(rs.getString("tenNhanVien"));
                log.setThoiGianHuy(rs.getTimestamp("thoiGianHuy"));
                log.setLyDoHuy(rs.getString("lyDoHuy"));
                dsLog.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsLog;
    }
}
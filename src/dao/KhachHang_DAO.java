package dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.math.BigDecimal;
import connect.DBConnect; 
import entity.KhachHang;

public class KhachHang_DAO {

    // Ánh xạ ResultSet sang đối tượng KhachHang
    private KhachHang mapKhachHang(ResultSet rs) throws SQLException {
        return new KhachHang(
            rs.getString("maKhachHang"),
            rs.getString("hoTen"),
            rs.getString("soDienThoai"),
            rs.getString("email"),       
            rs.getString("diaChi"),      
            rs.getDate("ngaySinh"),    
            rs.getBoolean("gioiTinh"),
            rs.getInt("tichDiem")
        );
    }

    // Tự động phát sinh mã khách hàng mới
    public String phatSinhMaKhachHang() {
        String maKH = "KH000001";
        String sql = "SELECT TOP 1 maKhachHang FROM KhachHang ORDER BY maKhachHang DESC";
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maMax = rs.getString(1);
                if (maMax != null && maMax.length() > 2) {
                    int soMoi = Integer.parseInt(maMax.substring(2)) + 1;
                    maKH = String.format("KH%06d", soMoi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maKH;
    }

    // Đọc danh sách khách hàng đang hoạt động
    public List<KhachHang> docDanhSachKhachHang() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE trangThai = 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) ds.add(mapKhachHang(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    // Thêm khách hàng mới
    public boolean themKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang (maKhachHang, hoTen, soDienThoai, email, diaChi, ngaySinh, gioiTinh, tichDiem, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kh.getMaKhachHang());
            stmt.setString(2, kh.getHoTen());
            stmt.setString(3, kh.getSoDienThoai());
            stmt.setString(4, kh.getEmail());
            stmt.setString(5, kh.getDiaChi());
            stmt.setDate(6, kh.getNgaySinh()); 
            stmt.setBoolean(7, kh.isGioiTinh());
            stmt.setInt(8, kh.getTichDiem());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Cập nhật thông tin khách hàng
    public boolean capNhatKhachHang(KhachHang kh) {
        String sql = "UPDATE KhachHang SET hoTen = ?, soDienThoai = ?, email = ?, diaChi = ?, ngaySinh = ?, gioiTinh = ?, tichDiem = ? WHERE maKhachHang = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kh.getHoTen());
            stmt.setString(2, kh.getSoDienThoai());
            stmt.setString(3, kh.getEmail());
            stmt.setString(4, kh.getDiaChi());
            stmt.setDate(5, kh.getNgaySinh());
            stmt.setBoolean(6, kh.isGioiTinh());
            stmt.setInt(7, kh.getTichDiem());
            stmt.setString(8, kh.getMaKhachHang());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Tìm kiếm khách hàng theo mã
    public List<KhachHang> timKiemTheoMa(String ma) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE UPPER(maKhachHang) = ? AND trangThai = 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ma.toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) ds.add(mapKhachHang(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    // Tìm kiếm khách hàng theo tên
    public List<KhachHang> timKiemTheoTen(String ten) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE UPPER(hoTen) LIKE ? AND trangThai = 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + ten.toUpperCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) ds.add(mapKhachHang(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    // Tìm kiếm khách hàng theo SĐT (tương đối)
    public List<KhachHang> timKiemTheoSDT(String sdt) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE soDienThoai LIKE ? AND trangThai = 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + sdt + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) ds.add(mapKhachHang(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    // Lọc khách hàng theo giới tính
    public List<KhachHang> locKhachHangTheoGioiTinh(boolean gt) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE gioiTinh = ? AND trangThai = 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, gt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) ds.add(mapKhachHang(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    // Sắp xếp danh sách theo tên
    public List<KhachHang> sapXepTheoTen(boolean tangDan) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE trangThai = 1 ORDER BY hoTen " + (tangDan ? "ASC" : "DESC");
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) ds.add(mapKhachHang(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    // Sắp xếp danh sách theo điểm tích lũy
    public List<KhachHang> sapXepTheoDiem(boolean tangDan) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE trangThai = 1 ORDER BY tichDiem " + (tangDan ? "ASC" : "DESC");        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) ds.add(mapKhachHang(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    // Xóa khách hàng (xóa mềm bằng cách cập nhật trạng thái)
    public boolean xoaKhachHang(String ma) {
        String sql = "UPDATE KhachHang SET trangThai=0 WHERE maKhachHang = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ma);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Tìm một khách hàng duy nhất theo SĐT
    public KhachHang timKhachHangTheoSDT(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE soDienThoai = ? AND trangThai = 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sdt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapKhachHang(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    // Cập nhật điểm tích lũy cho khách hàng
    public boolean capNhatDiemTichLuy(String ma, int diem) {
        String sql = "UPDATE KhachHang SET tichDiem = tichDiem + ? WHERE maKhachHang = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, diem);
            stmt.setString(2, ma);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // Thống kê tổng số lượng khách hàng
    public int getTongSoKhachHang() {
        int tong = 0;
        String sql = "SELECT COUNT(maKhachHang) FROM KhachHang WHERE trangThai = 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) tong = rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return tong;
    }

    // Thống kê tổng điểm tích lũy của khách hàng có giao dịch trong khoảng thời gian
    public int getTongDiemTichLuy(Date tu, Date den) {
        int tong = 0;
        String sql = (tu == null || den == null) ? "SELECT SUM(tichDiem) FROM KhachHang WHERE trangThai = 1" :
                     "SELECT SUM(kh.tichDiem) FROM KhachHang kh WHERE kh.maKhachHang IN (SELECT DISTINCT maKhachHang FROM HoaDon WHERE ngayLapHoaDon BETWEEN ? AND ?) AND kh.trangThai = 1";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (tu != null && den != null) {
                stmt.setTimestamp(1, new Timestamp(tu.getTime()));
                stmt.setTimestamp(2, new Timestamp(den.getTime()));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) tong = rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tong;
    }

    // Thống kê số lượng khách hàng theo giới tính
    public Map<String, Integer> getSoLuongKhachHangTheoGioiTinh() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT (CASE WHEN gioiTinh = 1 THEN N'Nam' ELSE N'Nữ' END) AS GioiTinh, COUNT(*) AS SoLuong FROM KhachHang WHERE trangThai = 1 GROUP BY gioiTinh";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) data.put(rs.getString("GioiTinh"), rs.getInt("SoLuong"));
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    // Thống kê tổng chi tiêu của tất cả khách hàng trong khoảng thời gian
    public BigDecimal getTongChiTieuTatCaKhachHang(Date tu, Date den) {
        BigDecimal tong = BigDecimal.ZERO;
        StringBuilder sql = new StringBuilder("SELECT SUM(ct.soLuong * ct.donGia) FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang WHERE hd.maKhachHang IS NOT NULL AND hd.trangThai = N'Đã thanh toán' AND kh.trangThai = 1 ");
        if (tu != null && den != null) sql.append("AND hd.ngayLapHoaDon BETWEEN ? AND ?");
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (tu != null && den != null) {
                stmt.setTimestamp(1, new Timestamp(tu.getTime()));
                stmt.setTimestamp(2, new Timestamp(den.getTime()));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal sum = rs.getBigDecimal(1);
                    if (sum != null) tong = sum;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tong;
    }

    // Lấy top khách hàng chi tiêu nhiều nhất
    public Map<String, BigDecimal> getTopKhachHangTheoChiTieu(int topN) {
        Map<String, BigDecimal> data = new LinkedHashMap<>();
        String sql = "SELECT TOP (?) kh.hoTen, SUM(ct.soLuong * ct.donGia) AS TongChiTieu FROM KhachHang kh JOIN HoaDon hd ON kh.maKhachHang = hd.maKhachHang JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon WHERE hd.trangThai = N'Đã thanh toán' AND kh.trangThai = 1 GROUP BY kh.maKhachHang, kh.hoTen ORDER BY TongChiTieu DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, topN);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) data.put(rs.getString("hoTen"), rs.getBigDecimal("TongChiTieu"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }
    
    // Thống kê số lượng khách hàng có phát sinh giao dịch trong khoảng thời gian
    public int getTongSoKhachHang(Date tu, Date den) {
        int tong = 0;
        String sql = (tu == null || den == null) ? "SELECT COUNT(maKhachHang) FROM KhachHang WHERE trangThai = 1" :
                     "SELECT COUNT(DISTINCT maKhachHang) FROM HoaDon WHERE ngayLapHoaDon BETWEEN ? AND ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (tu != null && den != null) {
                stmt.setTimestamp(1, new Timestamp(tu.getTime()));
                stmt.setTimestamp(2, new Timestamp(den.getTime()));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) tong = rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tong;
    }

    // Lấy danh sách top khách hàng chi tiêu với thông tin đầy đủ
    public List<Object[]> getTopKhachHangDayDu(int topN, Date tu, Date den) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT TOP (?) kh.hoTen, SUM(ct.soLuong * ct.donGia) AS TongChiTieu, kh.tichDiem FROM KhachHang kh JOIN HoaDon hd ON kh.maKhachHang = hd.maKhachHang JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon WHERE hd.trangThai = N'Đã thanh toán' AND kh.trangThai = 1 ");
        if (tu != null && den != null) sql.append("AND hd.ngayLapHoaDon BETWEEN ? AND ? ");
        sql.append("GROUP BY kh.maKhachHang, kh.hoTen, kh.tichDiem ORDER BY TongChiTieu DESC");
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setInt(1, topN);
            if (tu != null && den != null) {
                stmt.setTimestamp(2, new Timestamp(tu.getTime()));
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.setTime(den); c.set(java.util.Calendar.HOUR_OF_DAY, 23); c.set(java.util.Calendar.MINUTE, 59); c.set(java.util.Calendar.SECOND, 59);
                stmt.setTimestamp(3, new Timestamp(c.getTimeInMillis()));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(new Object[]{rs.getString("hoTen"), rs.getBigDecimal("TongChiTieu"), rs.getInt("tichDiem")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    // Đọc danh sách khách hàng đã bị xóa
    public List<KhachHang> docDanhSachKhachHangDaXoa() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE trangThai = 0";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) ds.add(mapKhachHang(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    // Khôi phục khách hàng đã bị xóa
    public boolean khoiPhucKhachHang(String ma) {
        String sql = "UPDATE KhachHang SET trangThai = 1 WHERE maKhachHang = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ma);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
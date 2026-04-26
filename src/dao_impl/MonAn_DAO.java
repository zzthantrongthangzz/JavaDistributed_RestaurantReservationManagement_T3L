package dao_impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import connect.DBConnect;
import entity.MonAn;
import entity.LichSuGia;
import entity.LoaiMon;

public class MonAn_DAO {

    // Ánh xạ ResultSet sang đối tượng MonAn
    private MonAn mapMonAn(ResultSet rs) throws SQLException {
        LoaiMon loaiMon = new LoaiMon(rs.getString("maLoai"), rs.getString("tenLoai"));
        return new MonAn(
            rs.getString("maMon"),
            rs.getString("tenMon"),
            rs.getString("duongDanAnh"),
            rs.getDouble("gia"),
            rs.getString("tinhTrang"),
            rs.getString("moTa"),
            rs.getString("donVi"),
            loaiMon 
        );
    }

    private final String SQL_SELECT_BASE = "SELECT m.*, l.tenLoai FROM Mon m INNER JOIN LoaiMon l ON m.maLoai = l.maLoai ";

    // Lấy danh sách món ăn đang kinh doanh
    public List<MonAn> docDanhSachMon() {
        List<MonAn> danhSachMon = new ArrayList<>();
        String sql = SQL_SELECT_BASE + "WHERE m.tinhTrang = N'Đang kinh doanh'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) danhSachMon.add(mapMonAn(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSachMon;
    }

    // Tìm kiếm món ăn theo từ khóa
    public List<MonAn> timKiemMonAn(String tuKhoa) {
        List<MonAn> danhSachMon = new ArrayList<>();
        String sql = SQL_SELECT_BASE + "WHERE m.tinhTrang = N'Đang kinh doanh' AND (UPPER(m.tenMon) LIKE ? OR UPPER(m.maMon) LIKE ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String keyword = "%" + tuKhoa.toUpperCase() + "%";
            stmt.setString(1, keyword);
            stmt.setString(2, keyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) danhSachMon.add(mapMonAn(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSachMon;
    }

    // Tìm kiếm món ăn theo mã cụ thể
    public List<MonAn> timKiemTheoMa(String maMon) {
        List<MonAn> danhSachMon = new ArrayList<>();
        String sql = SQL_SELECT_BASE + "WHERE m.tinhTrang = N'Đang kinh doanh' AND UPPER(m.maMon) = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maMon.toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) danhSachMon.add(mapMonAn(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSachMon;
    }

    // Tìm kiếm món ăn theo tên
    public List<MonAn> timKiemTheoTen(String tenMon) {
        List<MonAn> danhSachMon = new ArrayList<>();
        String sql = SQL_SELECT_BASE + "WHERE m.tinhTrang = N'Đang kinh doanh' AND UPPER(m.tenMon) LIKE ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + tenMon.toUpperCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) danhSachMon.add(mapMonAn(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSachMon;
    }

    // Lọc món ăn theo loại món
    public List<MonAn> locMonAnTheoLoai(String tenLoai) {
        List<MonAn> danhSachMon = new ArrayList<>();
        String sql = SQL_SELECT_BASE + "WHERE m.tinhTrang = N'Đang kinh doanh' AND l.tenLoai = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenLoai);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) danhSachMon.add(mapMonAn(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSachMon;
    }

    // Sinh mã món ăn tự động
    public String sinhMaMonTuDong() {
        String maMoi = "MM000001";
        String sql = "SELECT MAX(maMon) AS maxMa FROM Mon";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String maxMa = rs.getString("maxMa");
                if (maxMa != null && maxMa.startsWith("MM")) {
                    int soThuTu = Integer.parseInt(maxMa.substring(2)) + 1;
                    maMoi = String.format("MM%06d", soThuTu);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return maMoi;
    }

    // Thêm món ăn mới vào hệ thống
    public boolean themMonAn(MonAn monAn) {
        String sql = "INSERT INTO Mon (maMon, tenMon, gia, duongDanAnh, tinhTrang, moTa, donVi, maLoai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, monAn.getMaMon());
            stmt.setString(2, monAn.getTenMon());
            stmt.setDouble(3, monAn.getGia());
            stmt.setString(4, monAn.getDuongDanAnh());
            stmt.setString(5, monAn.getTinhTrang());
            stmt.setString(6, monAn.getMoTa());
            stmt.setString(7, monAn.getDonVi());
            stmt.setString(8, monAn.getLoaiMon().getMaLoai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Tìm một món ăn theo mã duy nhất
    public MonAn timMotMonTheoMa(String maMon) {
        String sql = SQL_SELECT_BASE + "WHERE UPPER(m.maMon) = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maMon.toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapMonAn(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Cập nhật trạng thái ngừng kinh doanh (xóa mềm)
    public boolean xoaMem(String maMon) {
        String sql = "UPDATE Mon SET tinhTrang = N'Ngừng kinh doanh' WHERE maMon = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maMon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Cập nhật thông tin món ăn và ghi log lịch sử giá
    public boolean capNhatMonAn(MonAn monAn, String maNhanVienThucHien) {
        String sqlUpdate = "UPDATE Mon SET tenMon=?, gia=?, duongDanAnh=?, tinhTrang=?, moTa=?, donVi=?, maLoai=? WHERE maMon=?";
        String sqlLog = "INSERT INTO LichSuGia (maMon, giaCu, giaMoi, ngayThayDoi, maNhanVien) VALUES (?, ?, ?, GETDATE(), ?)";
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);
            double giaCu = 0;
            MonAn monCu = timMotMonTheoMa(monAn.getMaMon());
            if (monCu != null) giaCu = monCu.getGia();
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setString(1, monAn.getTenMon());
                stmt.setDouble(2, monAn.getGia());
                stmt.setString(3, monAn.getDuongDanAnh());
                stmt.setString(4, monAn.getTinhTrang());
                stmt.setString(5, monAn.getMoTa());
                stmt.setString(6, monAn.getDonVi());
                stmt.setString(7, monAn.getLoaiMon().getMaLoai());
                stmt.setString(8, monAn.getMaMon());
                stmt.executeUpdate();
            }
            if (Math.abs(monAn.getGia() - giaCu) > 0.001) {
                try (PreparedStatement stmtLog = conn.prepareStatement(sqlLog)) {
                    stmtLog.setString(1, monAn.getMaMon());
                    stmtLog.setDouble(2, giaCu);
                    stmtLog.setDouble(3, monAn.getGia());
                    stmtLog.setString(4, maNhanVienThucHien);
                    stmtLog.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace(); return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) {}
        }
    }

    // Thống kê doanh số món ăn theo khoảng thời gian
    public List<Object[]> getThongKeMonAn(Date tuNgay, Date denNgay) {
        List<Object[]> ketQua = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT m.maMon, m.tenMon, m.gia, SUM(ct.soLuong) AS TongSoLuong FROM Mon m INNER JOIN ChiTietHoaDon ct ON m.maMon = ct.maMon INNER JOIN HoaDon hd ON ct.maHoaDon = hd.maHoaDon WHERE hd.trangThai = N'Đã thanh toán' ");
        if (tuNgay != null && denNgay != null) sqlBuilder.append("AND hd.ngayLapHoaDon BETWEEN ? AND ? ");
        sqlBuilder.append("GROUP BY m.maMon, m.tenMon, m.gia ORDER BY TongSoLuong DESC");
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {
            if (tuNgay != null && denNgay != null) {
                stmt.setTimestamp(1, new Timestamp(tuNgay.getTime()));
                Calendar c = Calendar.getInstance();
                c.setTime(denNgay); c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
                stmt.setTimestamp(2, new Timestamp(c.getTimeInMillis()));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) ketQua.add(new Object[]{rs.getString("maMon"), rs.getString("tenMon"), rs.getDouble("gia"), rs.getInt("TongSoLuong")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ketQua;
    }

    // Thêm danh sách nhiều món ăn (dùng cho nhập liệu từ file)
    public boolean themDanhSachMonAn(List<MonAn> danhSachMon) {
        String sql = "INSERT INTO Mon (maMon, tenMon, gia, duongDanAnh, tinhTrang, moTa, donVi, maLoai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (MonAn mon : danhSachMon) {
                    stmt.setString(1, mon.getMaMon());
                    stmt.setString(2, mon.getTenMon());
                    stmt.setDouble(3, mon.getGia());
                    stmt.setString(4, mon.getDuongDanAnh() != null ? mon.getDuongDanAnh() : "/img/default_food.png");
                    stmt.setString(5, "Đang kinh doanh");
                    stmt.setString(6, mon.getMoTa());
                    stmt.setString(7, mon.getDonVi());
                    stmt.setString(8, mon.getLoaiMon().getMaLoai());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace(); return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) {}
        }
    }
    
    // Lấy lịch sử thay đổi giá của một món ăn
    public List<LichSuGia> getLichSuGia(String maMon) {
        List<LichSuGia> list = new ArrayList<>();
        String sql = "SELECT ls.*, nv.hoTen FROM LichSuGia ls LEFT JOIN NhanVien nv ON ls.maNhanVien = nv.maNhanVien WHERE ls.maMon = ? ORDER BY ls.ngayThayDoi DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maMon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new LichSuGia(rs.getInt("maLog"), rs.getString("maMon"), rs.getDouble("giaCu"), rs.getDouble("giaMoi"), rs.getTimestamp("ngayThayDoi"), rs.getString("hoTen")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
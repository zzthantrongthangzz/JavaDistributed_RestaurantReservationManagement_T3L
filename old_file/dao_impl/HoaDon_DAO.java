package dao_impl;

import connect.DBConnect;
import entity.HoaDon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class HoaDon_DAO {

    // Ánh xạ ResultSet sang đối tượng HoaDon
    private HoaDon mapHoaDon(ResultSet rs) throws SQLException {
        return new HoaDon(
            rs.getString("maHoaDon"),
            rs.getString("trangThai"),
            rs.getTimestamp("ngayLapHoaDon"),
            rs.getBigDecimal("thue"),
            rs.getString("maNhanVien"),
            rs.getString("maPhieuDatBan"),
            rs.getString("maKhachHang"),
            rs.getString("maKhuyenMai"),
            rs.getString("diaChi"),
            rs.getBigDecimal("tienDatCoc"),
            rs.getBigDecimal("soTienKhachTra"),
            rs.getBigDecimal("soTienThoi")
        );
    }

    // Lấy toàn bộ danh sách hóa đơn
    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon"; 
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapHoaDon(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Thêm hóa đơn mới
    public boolean themHoaDon(HoaDon hd) {
        String sql = "INSERT INTO HoaDon(maHoaDon, trangThai, ngayLapHoaDon, thue, maNhanVien, " +
                     "maPhieuDatBan, maKhachHang, maKhuyenMai, diaChi, tienDatCoc, soTienKhachTra, soTienThoi) " + 
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hd.getMaHoaDon());
            stmt.setString(2, hd.getTrangThai());
            stmt.setTimestamp(3, new Timestamp(hd.getNgayLapHoaDon().getTime()));
            stmt.setBigDecimal(4, hd.getThue());
            stmt.setString(5, hd.getMaNhanVien());
            stmt.setString(6, hd.getMaPhieuDatBan());
            stmt.setString(7, hd.getMaKhachHang());
            stmt.setString(8, hd.getMaKhuyenMai());
            stmt.setString(9, hd.getDiaChi());
            stmt.setBigDecimal(10, hd.getTienDatCoc());
            stmt.setBigDecimal(11, hd.getSoTienKhachTra());
            stmt.setBigDecimal(12, hd.getSoTienThoi());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật thông tin hóa đơn
    public boolean capNhatHoaDon(HoaDon hd) {
        String sql = "UPDATE HoaDon SET trangThai=?, ngayLapHoaDon=?, thue=?, maNhanVien=?, " +
                     "maPhieuDatBan=?, maKhachHang=?, maKhuyenMai=?, diaChi=?, tienDatCoc=?, soTienKhachTra=?, soTienThoi=? WHERE maHoaDon=?"; 
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hd.getTrangThai());
            stmt.setTimestamp(2, new Timestamp(hd.getNgayLapHoaDon().getTime()));
            stmt.setBigDecimal(3, hd.getThue());
            stmt.setString(4, hd.getMaNhanVien());
            stmt.setString(5, hd.getMaPhieuDatBan());
            stmt.setString(6, hd.getMaKhachHang());
            stmt.setString(7, hd.getMaKhuyenMai());
            stmt.setString(8, hd.getDiaChi());
            stmt.setBigDecimal(9, hd.getTienDatCoc());
            stmt.setBigDecimal(10, hd.getSoTienKhachTra());
            stmt.setBigDecimal(11, hd.getSoTienThoi());
            stmt.setString(12, hd.getMaHoaDon());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa hóa đơn theo mã
    public boolean xoaHoaDon(String maHoaDon) {
        String sql = "DELETE FROM HoaDon WHERE maHoaDon=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm hóa đơn theo mã
    public HoaDon timTheoMa(String maHoaDon) {
        String sql = "SELECT * FROM HoaDon WHERE maHoaDon=?";
        HoaDon hd = null;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    hd = mapHoaDon(rs);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return hd;
    }

    // Tìm hóa đơn theo khoảng ngày lập
    public List<HoaDon> timTheoNgay(java.util.Date tuNgay, java.util.Date denNgay) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE CAST(ngayLapHoaDon AS DATE) BETWEEN ? AND ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Tìm hóa đơn chưa thanh toán theo mã bàn
    public HoaDon timHoaDonChuaThanhToanTheoMaBan(String maBan) {
        String sql = "SELECT TOP 1 hd.* FROM HoaDon hd JOIN HoaDon_Ban hdb ON hd.maHoaDon = hdb.maHoaDon WHERE hdb.maBan=? AND hd.trangThai=N'Chưa thanh toán'";
        HoaDon hd = null;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    hd = timTheoMa(rs.getString("maHoaDon"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return hd;
    }
    
    // Sinh mã hóa đơn tự động
    public String sinhMaHoaDonTuDong() {
        String maMoi = "HD000001";
        String sql = "SELECT MAX(maHoaDon) FROM HoaDon";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String maxMa = rs.getString(1);
                if (maxMa != null && maxMa.startsWith("HD")) {
                    String soHienTaiStr = maxMa.substring(2);
                    int soHienTai = Integer.parseInt(soHienTaiStr);
                    soHienTai++;
                    maMoi = String.format("HD%06d", soHienTai);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi sinh mã hóa đơn tự động: " + e.getMessage());
        }
        return maMoi;
    }
    
    // Lọc hóa đơn theo trạng thái
    public List<HoaDon> locTheoTrangThai(String trangThai) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE trangThai = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThai);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Tìm kiếm hóa đơn theo từ khóa (Mã HĐ, Mã KH, Mã NV)
    public List<HoaDon> timKiemChung(String tuKhoa) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE maHoaDon LIKE ? OR maKhachHang LIKE ? OR maNhanVien LIKE ?";
        String keyword = "%" + tuKhoa + "%";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keyword);
            stmt.setString(2, keyword);
            stmt.setString(3, keyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Sắp xếp danh sách hóa đơn
    public List<HoaDon> sapXep(String orderBy) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon ORDER BY " + orderBy;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapHoaDon(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    // Tìm kiếm hóa đơn nâng cao theo nhiều tiêu chí
    public List<HoaDon> timKiemNangCao(String maHD, String maKH, String maNV, java.util.Date tuNgay, java.util.Date denNgay) {
        List<HoaDon> list = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM HoaDon WHERE 1=1");
        if (maHD != null && !maHD.isEmpty()) sqlBuilder.append(" AND maHoaDon LIKE ?");
        if (maKH != null && !maKH.isEmpty()) sqlBuilder.append(" AND maKhachHang LIKE ?");
        if (maNV != null && !maNV.isEmpty()) sqlBuilder.append(" AND maNhanVien LIKE ?");
        if (tuNgay != null) sqlBuilder.append(" AND CAST(ngayLapHoaDon AS DATE) >= ?");
        if (denNgay != null) sqlBuilder.append(" AND CAST(ngayLapHoaDon AS DATE) <= ?");

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {
            int paramIndex = 1;
            if (maHD != null && !maHD.isEmpty()) stmt.setString(paramIndex++, "%" + maHD + "%");
            if (maKH != null && !maKH.isEmpty()) stmt.setString(paramIndex++, "%" + maKH + "%");
            if (maNV != null && !maNV.isEmpty()) stmt.setString(paramIndex++, "%" + maNV + "%");
            if (tuNgay != null) stmt.setDate(paramIndex++, new java.sql.Date(tuNgay.getTime()));
            if (denNgay != null) stmt.setDate(paramIndex++, new java.sql.Date(denNgay.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                	list.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Tính tổng doanh thu theo khoảng thời gian
    public BigDecimal getTongDoanhThu(java.util.Date tuNgay, java.util.Date denNgay) {
        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        String sql = "SELECT SUM(ct.soLuong * ct.donGia) " +
                     "FROM HoaDon hd " +
                     "JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon " +
                     "WHERE hd.trangThai = N'Đã thanh toán' " +
                     "AND CAST(hd.ngayLapHoaDon AS DATE) BETWEEN ? AND ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal sum = rs.getBigDecimal(1);
                    if (sum != null) tongDoanhThu = sum;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongDoanhThu;
    }

    // Lấy tổng số hóa đơn đã thanh toán
    public int getTongSoHoaDon(java.util.Date tuNgay, java.util.Date denNgay) {
        int tongSoHoaDon = 0;
        String sql = "SELECT COUNT(maHoaDon) FROM HoaDon WHERE trangThai = N'Đã thanh toán' AND CAST(ngayLapHoaDon AS DATE) BETWEEN ? AND ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) tongSoHoaDon = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongSoHoaDon;
    }
    
    // Lấy doanh thu theo từng ngày
    public java.util.Map<java.util.Date, BigDecimal> getDoanhThuTheoNgay(java.util.Date tuNgay, java.util.Date denNgay) {
        java.util.Map<java.util.Date, BigDecimal> doanhThuTheoNgay = new java.util.TreeMap<>();
        String sql = "SELECT CAST(hd.ngayLapHoaDon AS DATE) AS ngay, SUM(ct.soLuong * ct.donGia) AS doanhThu " +
                     "FROM HoaDon hd " +
                     "JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon " +
                     "WHERE hd.trangThai = N'Đã thanh toán' " +
                     "AND CAST(hd.ngayLapHoaDon AS DATE) BETWEEN ? AND ? " +
                     "GROUP BY CAST(hd.ngayLapHoaDon AS DATE) ORDER BY ngay";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
            stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Date ngay = rs.getDate("ngay");
                    BigDecimal doanhThu = rs.getBigDecimal("doanhThu");
                    doanhThuTheoNgay.put(ngay, doanhThu);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doanhThuTheoNgay;
    }
    
    // Lấy tên khách hàng đang ngồi tại bàn
    public String getTenKhachHangTheoBan(String maBan) {
        String tenKhach = null;
        String sql = "SELECT k.hoTen FROM HoaDon hd JOIN HoaDon_Ban hdb ON hd.maHoaDon = hdb.maHoaDon " +
                "LEFT JOIN KhachHang k ON hd.maKhachHang = k.maKhachHang WHERE hdb.maBan = ? " +
                "AND hd.trangThai LIKE N'%Chưa thanh toán%'";
        try (java.sql.Connection conn = connect.DBConnect.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maBan);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) tenKhach = rs.getString("hoTen");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tenKhach;
    }
    
    // Tính tổng tiền của một hóa đơn
    public BigDecimal tinhTongTienCuaHoaDon(String maHoaDon) {
        BigDecimal tongTien = BigDecimal.ZERO;
        String sql = "SELECT SUM(soLuong * donGia) FROM ChiTietHoaDon WHERE maHoaDon = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal result = rs.getBigDecimal(1);
                    if (result != null) tongTien = result;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongTien;
    }

    // Lấy danh sách hóa đơn đã thanh toán theo khoảng thời gian
    public List<HoaDon> getDanhSachHoaDon(java.util.Date tuNgay, java.util.Date denNgay) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE trangThai = N'Đã thanh toán' " +
                     "AND CAST(ngayLapHoaDon AS DATE) BETWEEN ? AND ? ORDER BY ngayLapHoaDon DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (tuNgay != null && denNgay != null) {
                stmt.setDate(1, new java.sql.Date(tuNgay.getTime()));
                stmt.setDate(2, new java.sql.Date(denNgay.getTime()));
            } else {
                stmt.setDate(1, new java.sql.Date(0)); 
                stmt.setDate(2, new java.sql.Date(System.currentTimeMillis())); 
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
package dao_impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import entity.KhuyenMai;

public class KhuyenMai_DAO {
	private Connection con;

	public KhuyenMai_DAO(Connection con) {
		this.con = con;
	}

	// Lấy danh sách khuyến mãi còn hạn và đang hiển thị
	public List<KhuyenMai> getAllList() {
		List<KhuyenMai> list = new ArrayList<KhuyenMai>();
		String sql = "select * from KhuyenMai where hienThi=1 AND CAST(ngayKetThuc AS DATE) >= CAST(GETDATE() AS DATE)";
		try (Statement stmt = con.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				KhuyenMai km = new KhuyenMai(
					rs.getString("maKhuyenMai"),
					rs.getString("tenKhuyenMai"),
					rs.getString("loaiKhuyenMai"),
					rs.getDate("ngayBatDau"),
					rs.getDate("ngayKetThuc"),
					rs.getDouble("giaTriGiam"),
					rs.getInt("hienThi")
				);
				list.add(km);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// Lấy danh sách kèm sắp xếp linh hoạt
	public List<KhuyenMai> getAllListSorted(String sortBy) {
		List<KhuyenMai> list = new ArrayList<KhuyenMai>();
		String orderClause = "";
		switch (sortBy) {
			case "Tên": orderClause = "order by tenKhuyenMai"; break;
			case "Giá trị": orderClause = "order by giaTriGiam desc"; break;
			case "Ngày bắt đầu": orderClause = "order by ngayBatDau desc"; break;
			default: orderClause = "order by maKhuyenMai";
		}
		String sql = "select * from KhuyenMai where hienThi=1 " + orderClause;
		try (Statement stmt = con.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				KhuyenMai km = new KhuyenMai(
					rs.getString("maKhuyenMai"),
					rs.getString("tenKhuyenMai"),
					rs.getString("loaiKhuyenMai"),
					rs.getDate("ngayBatDau"),
					rs.getDate("ngayKetThuc"),
					rs.getDouble("giaTriGiam"),
					rs.getInt("hienThi")
				);
				list.add(km);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// Lọc khuyến mãi theo loại
	public List<KhuyenMai> getByLoaiKhuyenMai(String loaiKM) {
		List<KhuyenMai> list = new ArrayList<KhuyenMai>();
		String sql = "select * from KhuyenMai where hienThi=1 and loaiKhuyenMai=?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, loaiKM);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					KhuyenMai km = new KhuyenMai(
						rs.getString("maKhuyenMai"),
						rs.getString("tenKhuyenMai"),
						rs.getString("loaiKhuyenMai"),
						rs.getDate("ngayBatDau"),
						rs.getDate("ngayKetThuc"),
						rs.getDouble("giaTriGiam"),
						rs.getInt("hienThi")
					);
					list.add(km);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// Tìm khuyến mãi theo mã
	public KhuyenMai getByMaKhuyenMai(String maKM) {
		String sql = "select * from KhuyenMai where maKhuyenMai=?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maKM);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new KhuyenMai(
						rs.getString("maKhuyenMai"),
						rs.getString("tenKhuyenMai"),
						rs.getString("loaiKhuyenMai"),
						rs.getDate("ngayBatDau"),
						rs.getDate("ngayKetThuc"),
						rs.getDouble("giaTriGiam"),
						rs.getInt("hienThi")
					);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Thêm khuyến mãi mới
	public boolean themKhuyenMai(KhuyenMai km) {
		String sql = "insert into KhuyenMai(maKhuyenMai,tenKhuyenMai,loaiKhuyenMai,ngayBatDau,ngayKetThuc,giaTriGiam) values(?,?,?,?,?,?)";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, km.getMaKhuyenMai());
			ps.setString(2, km.getTenKhuyenMai());
			ps.setString(3, km.getLoaiKhuyenMai());
			ps.setDate(4, new java.sql.Date(km.getNgayBatDau().getTime()));
			ps.setDate(5, new java.sql.Date(km.getNgayKetThuc().getTime()));
			ps.setDouble(6, km.getGiaTriGiam());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Cập nhật thông tin khuyến mãi
	public boolean capNhatKhuyenMai(KhuyenMai km) {
		String sql = "update KhuyenMai set tenKhuyenMai=?, loaiKhuyenMai=?,ngayBatDau=?,ngayKetThuc=?,giaTriGiam=?,hienThi=? where maKhuyenMai=?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, km.getTenKhuyenMai());
			ps.setString(2, km.getLoaiKhuyenMai());
			ps.setDate(3, new java.sql.Date(km.getNgayBatDau().getTime()));
			ps.setDate(4, new java.sql.Date(km.getNgayKetThuc().getTime()));
			ps.setDouble(5, km.getGiaTriGiam());
			ps.setInt(6, km.getHienThi());
			ps.setString(7, km.getMaKhuyenMai());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Ẩn khuyến mãi (xóa mềm)
	public boolean anKhuyenMai(String maKM) {
		String sql = "update KhuyenMai set hienThi=0 where maKhuyenMai=?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maKM);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Lấy danh sách khuyến mãi đã hết hạn
	public List<KhuyenMai> getKhuyenMaiHetHan() {
		List<KhuyenMai> list = new ArrayList<>();
		String sql = "SELECT * FROM KhuyenMai WHERE hienThi=1 and CAST(ngayKetThuc AS DATE) < CAST(GETDATE() AS DATE)";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					KhuyenMai km = new KhuyenMai(
						rs.getString("maKhuyenMai"),
						rs.getString("tenKhuyenMai"),
						rs.getString("loaiKhuyenMai"),
						rs.getDate("ngayBatDau"),
						rs.getDate("ngayKetThuc"),
						rs.getDouble("giaTriGiam"),
						rs.getInt("hienThi")
					);
					list.add(km);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// Tìm kiếm theo mã hoặc tên
	public List<KhuyenMai> timKiem(String tuKhoa) {
		List<KhuyenMai> list = new ArrayList<KhuyenMai>();
		String sql = "select * from KhuyenMai where hienThi=1 and (maKhuyenMai like ? or tenKhuyenMai like ?)";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			String tuKhoaTimKiem = "%" + tuKhoa + "%";
			ps.setString(1, tuKhoaTimKiem);
			ps.setString(2, tuKhoaTimKiem);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					KhuyenMai km = new KhuyenMai(
						rs.getString("maKhuyenMai"),
						rs.getString("tenKhuyenMai"),
						rs.getString("loaiKhuyenMai"),
						rs.getDate("ngayBatDau"),
						rs.getDate("ngayKetThuc"),
						rs.getDouble("giaTriGiam"),
						rs.getInt("hienThi")
					);
					list.add(km);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// Tự động tạo mã KM mới
	public String taoMaKhuyenMaiTuDong() {
		String sql = "select top 1 maKhuyenMai from KhuyenMai where maKhuyenMai like 'KM%' order by maKhuyenMai desc";
		try (Statement stmt = con.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				String maKMCuoi = rs.getString("maKhuyenMai");
				String soPhan = maKMCuoi.substring(2);
				int soTiepTheo = Integer.parseInt(soPhan) + 1;
				return String.format("KM%06d", soTiepTheo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "KM000001";
	}

	// Kiểm tra sự tồn tại của mã KM
	public boolean kiemTraMaTonTai(String maKM) {
		String sql = "select count(*) from KhuyenMai where maKhuyenMai = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maKM);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Thống kê tổng lượt sử dụng trong khoảng thời gian
	public int getTongLuotSuDung(Date tuNgay, Date denNgay) {
		if (tuNgay == null || denNgay == null) return 0;
		int tongSoLuot = 0;
		String sql = "SELECT COUNT(maHoaDon) FROM HoaDon WHERE maKhuyenMai IS NOT NULL AND trangThai = N'Đã thanh toán' AND ngayLapHoaDon >= ? AND ngayLapHoaDon <= ?";
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setTimestamp(1, new java.sql.Timestamp(tuNgay.getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(denNgay.getTime()));
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) tongSoLuot = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tongSoLuot;
	}

	// Thống kê tổng số tiền đã giảm
	public BigDecimal getTongTienGiam(Date tuNgay, Date denNgay) {
		if (tuNgay == null || denNgay == null) return BigDecimal.ZERO;
		BigDecimal tongTienGiam = BigDecimal.ZERO;
		String sql = "SELECT SUM(CASE WHEN km.loaiKhuyenMai = N'Giảm tiền' THEN km.giaTriGiam WHEN km.loaiKhuyenMai = N'Giảm %' THEN ISNULL(bill.tongTien, 0) * (km.giaTriGiam / 100.0) ELSE 0 END) AS TongTienGiam " +
					 "FROM HoaDon hd JOIN KhuyenMai km ON hd.maKhuyenMai = km.maKhuyenMai " +
					 "LEFT JOIN (SELECT maHoaDon, SUM(soLuong * donGia) AS tongTien FROM ChiTietHoaDon GROUP BY maHoaDon) AS bill ON hd.maHoaDon = bill.maHoaDon " +
					 "WHERE hd.trangThai = N'Đã thanh toán' AND hd.ngayLapHoaDon >= ? AND hd.ngayLapHoaDon <= ?";
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setTimestamp(1, new java.sql.Timestamp(tuNgay.getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(denNgay.getTime()));
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					BigDecimal sum = rs.getBigDecimal(1);
					if (sum != null) tongTienGiam = sum;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tongTienGiam;
	}

	// Lấy dữ liệu tiền giảm theo từng ngày để vẽ biểu đồ
	public Map<Date, BigDecimal> getTienGiamTheoNgay(Date tuNgay, Date denNgay) {
		if (tuNgay == null || denNgay == null) return new TreeMap<>();
		Map<Date, BigDecimal> tienGiamTheoNgay = new TreeMap<>();
		String sql = "SELECT CAST(hd.ngayLapHoaDon AS DATE) AS Ngay, SUM(CASE WHEN km.loaiKhuyenMai = N'Giảm tiền' THEN km.giaTriGiam WHEN km.loaiKhuyenMai = N'Giảm %' THEN ISNULL(bill.tongTien, 0) * (km.giaTriGiam / 100.0) ELSE 0 END) AS TienGiamTrongNgay " +
					 "FROM HoaDon hd JOIN KhuyenMai km ON hd.maKhuyenMai = km.maKhuyenMai " +
					 "LEFT JOIN (SELECT maHoaDon, SUM(soLuong * donGia) AS tongTien FROM ChiTietHoaDon GROUP BY maHoaDon) AS bill ON hd.maHoaDon = bill.maHoaDon " +
					 "WHERE hd.trangThai = N'Đã thanh toán' AND hd.ngayLapHoaDon >= ? AND hd.ngayLapHoaDon <= ? GROUP BY CAST(hd.ngayLapHoaDon AS DATE) ORDER BY Ngay";
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setTimestamp(1, new java.sql.Timestamp(tuNgay.getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(denNgay.getTime()));
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					tienGiamTheoNgay.put(rs.getDate("Ngay"), rs.getBigDecimal("TienGiamTrongNgay"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tienGiamTheoNgay;
	}

	// Lọc danh sách nâng cao theo nhiều tiêu chí
	public List<KhuyenMai> locDanhSach(String maKM, String tenKM, String loaiKM, String giaTriFilter, Date tuNgay, Date denNgay, String sapXep) {
		List<KhuyenMai> list = new ArrayList<>();
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT * FROM KhuyenMai WHERE hienThi = 1");
		if (maKM != null && !maKM.isEmpty()) { sql.append(" AND maKhuyenMai LIKE ?"); params.add("%" + maKM + "%"); }
		if (tenKM != null && !tenKM.isEmpty()) { sql.append(" AND tenKhuyenMai LIKE ?"); params.add("%" + tenKM + "%"); }
		if (loaiKM != null && !loaiKM.isEmpty()) { sql.append(" AND loaiKhuyenMai = ?"); params.add(loaiKM); }
		if (tuNgay != null) { sql.append(" AND ngayBatDau >= ?"); params.add(new java.sql.Date(tuNgay.getTime())); }
		if (denNgay != null) { sql.append(" AND ngayKetThuc <= ?"); params.add(new java.sql.Date(denNgay.getTime())); }
		if (giaTriFilter != null) {
			switch (giaTriFilter) {
				case "Dưới 50K": sql.append(" AND giaTriGiam < 50000"); break;
				case "50K-100K": sql.append(" AND giaTriGiam BETWEEN 50000 AND 100000"); break;
				case "100K-200K": sql.append(" AND giaTriGiam BETWEEN 100001 AND 200000"); break;
				case "Trên 200K": sql.append(" AND giaTriGiam > 200000"); break;
			}
		}
		if (sapXep != null) {
			switch (sapXep) {
				case "Tên A-Z": sql.append(" ORDER BY tenKhuyenMai ASC"); break;
				case "Tên Z-A": sql.append(" ORDER BY tenKhuyenMai DESC"); break;
				case "Giá trị cao-thấp": sql.append(" ORDER BY giaTriGiam DESC"); break;
				case "Giá trị thấp-cao": sql.append(" ORDER BY giaTriGiam ASC"); break;
			}
		}
		try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(new KhuyenMai(rs.getString("maKhuyenMai"), rs.getString("tenKhuyenMai"), rs.getString("loaiKhuyenMai"), rs.getDate("ngayBatDau"), rs.getDate("ngayKetThuc"), rs.getDouble("giaTriGiam"), rs.getInt("hienThi")));
				}
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return list;
	}

	// Thống kê chi tiết hiệu quả từng chương trình khuyến mãi
	public List<Object[]> getThongKeChiTietKhuyenMai(Date tuNgay, Date denNgay) {
		if (tuNgay == null || denNgay == null) return new ArrayList<>();
		List<Object[]> list = new ArrayList<>();
		String sql = "SELECT km.maKhuyenMai, km.tenKhuyenMai, km.ngayBatDau, km.ngayKetThuc, COUNT(hd.maHoaDon) AS SoLuot, " +
					 "SUM(CASE WHEN km.loaiKhuyenMai = N'Giảm tiền' THEN km.giaTriGiam WHEN km.loaiKhuyenMai = N'Giảm %' THEN ISNULL(bill.tongTien, 0) * (km.giaTriGiam / 100.0) ELSE 0 END) AS TongTien " +
					 "FROM HoaDon hd JOIN KhuyenMai km ON hd.maKhuyenMai = km.maKhuyenMai " +
					 "LEFT JOIN (SELECT maHoaDon, SUM(soLuong * donGia) AS tongTien FROM ChiTietHoaDon GROUP BY maHoaDon) AS bill ON hd.maHoaDon = bill.maHoaDon " +
					 "WHERE hd.trangThai = N'Đã thanh toán' AND hd.ngayLapHoaDon >= ? AND hd.ngayLapHoaDon <= ? " +
					 "GROUP BY km.maKhuyenMai, km.tenKhuyenMai, km.ngayBatDau, km.ngayKetThuc, km.loaiKhuyenMai, km.giaTriGiam ORDER BY TongTien DESC";
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setTimestamp(1, new java.sql.Timestamp(tuNgay.getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(denNgay.getTime()));
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(new Object[]{rs.getString("maKhuyenMai"), rs.getString("tenKhuyenMai"), rs.getInt("SoLuot"), rs.getBigDecimal("TongTien") != null ? rs.getBigDecimal("TongTien") : BigDecimal.ZERO, rs.getDate("ngayBatDau"), rs.getDate("ngayKetThuc")});
				}
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return list;
	}
}
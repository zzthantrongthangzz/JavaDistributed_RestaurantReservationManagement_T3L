package dao;

import connect.DBConnect;
import entity.ChucVu;
import entity.NhanVien;
import java.sql.*;
import java.util.ArrayList;

public class NhanVien_DAO {

    // Ánh xạ ResultSet sang đối tượng NhanVien
	private NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
		ChucVu chucVu = new ChucVu(rs.getString("maChucVu"), rs.getString("tenChucVu"));
		return new NhanVien(rs.getString("maNhanVien"), rs.getString("hoTen"), rs.getBoolean("gioiTinh"),
				rs.getString("soDienThoai"), rs.getString("email"), rs.getDate("ngaySinh"), rs.getString("diaChi"),
				chucVu);
	}

	private final String BASE_SELECT_SQL = "SELECT nv.*, cv.maChucVu, cv.tenChucVu FROM NhanVien nv JOIN ChucVu cv ON nv.maChucVu = cv.maChucVu ";

    // Lấy danh sách nhân viên đang làm việc
	public ArrayList<NhanVien> getAllNhanVien() {
		ArrayList<NhanVien> danhSach = new ArrayList<>();
		String sql = BASE_SELECT_SQL + " WHERE nv.trangThai = 1";
		try (Connection conn = DBConnect.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) danhSach.add(mapResultSetToNhanVien(rs));
		} catch (SQLException e) { e.printStackTrace(); }
		return danhSach;
	}

    // Lấy danh sách nhân viên đã nghỉ việc
	public ArrayList<NhanVien> getAllNhanVienDaNghi() {
		ArrayList<NhanVien> danhSach = new ArrayList<>();
		String sql = BASE_SELECT_SQL + " WHERE nv.trangThai = 0";
		try (Connection conn = DBConnect.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) danhSach.add(mapResultSetToNhanVien(rs));
		} catch (SQLException e) { e.printStackTrace(); }
		return danhSach;
	}

    // Khôi phục trạng thái nhân viên
	public boolean khoiPhucNhanVien(String maNV) {
		String sql = "UPDATE NhanVien SET trangThai = 1 WHERE maNhanVien = ?";
		try (Connection conn = DBConnect.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, maNV);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

    // Tìm một nhân viên theo mã
	public NhanVien timMotNhanVienTheoMa(String maNhanVien) {
		String sql = BASE_SELECT_SQL + " WHERE nv.maNhanVien = ?";
		try (Connection conn = DBConnect.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, maNhanVien);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) return mapResultSetToNhanVien(rs);
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}

    // Tìm kiếm danh sách nhân viên theo mã
	public ArrayList<NhanVien> timKiemNhanVienTheoMa(String maNhanVien) {
	    ArrayList<NhanVien> danhSach = new ArrayList<>();
	    String sql = BASE_SELECT_SQL + " WHERE nv.trangThai = 1 AND nv.maNhanVien = ?";
	    try (Connection conn = DBConnect.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, maNhanVien.trim().toUpperCase());
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) danhSach.add(mapResultSetToNhanVien(rs));
	        }
	    } catch (SQLException e) { e.printStackTrace(); }
	    return danhSach;
	}
	
    // Tìm kiếm nhân viên theo họ tên
	public ArrayList<NhanVien> timKiemNhanVienTheoTen(String hoTen) {
	    ArrayList<NhanVien> danhSach = new ArrayList<>();
	    String sql = BASE_SELECT_SQL + " WHERE nv.trangThai = 1 AND LOWER(nv.hoTen) LIKE LOWER(?)";
	    try (Connection conn = DBConnect.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setNString(1, "%" + hoTen.trim() + "%");
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) danhSach.add(mapResultSetToNhanVien(rs));
	        }
	    } catch (SQLException e) { e.printStackTrace(); }
	    return danhSach;
	}
	
    // Tìm kiếm nhân viên theo số điện thoại
	public ArrayList<NhanVien> timKiemNhanVienTheoSDT(String sdt) {
	    ArrayList<NhanVien> danhSach = new ArrayList<>();
	    String sql = BASE_SELECT_SQL + " WHERE nv.trangThai = 1 AND nv.soDienThoai LIKE ?";
	    try (Connection conn = DBConnect.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, "%" + sdt.trim() + "%");
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) danhSach.add(mapResultSetToNhanVien(rs));
	        }
	    } catch (SQLException e) { e.printStackTrace(); }
	    return danhSach;
	}

    // Thêm nhân viên mới
	public boolean themNhanVien(NhanVien nv) {
		String sql = "INSERT INTO NhanVien (maNhanVien, hoTen, gioiTinh, soDienThoai, email, ngaySinh, diaChi, maChucVu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnect.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, nv.getMaNhanVien());
			pstmt.setString(2, nv.getHoTen());
			pstmt.setBoolean(3, nv.isGioiTinh());
			pstmt.setString(4, nv.getSoDienThoai());
			pstmt.setString(5, nv.getEmail());
			pstmt.setDate(6, nv.getNgaySinh());
			pstmt.setString(7, nv.getDiaChi());
			pstmt.setString(8, nv.getChucVu().getMaChucVu());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

    // Cập nhật thông tin nhân viên
	public boolean capNhatNhanVien(NhanVien nv) {
		String sql = "UPDATE NhanVien SET hoTen=?, gioiTinh=?, soDienThoai=?, email=?, ngaySinh=?, diaChi=?, maChucVu=? WHERE maNhanVien=?";
		try (Connection conn = DBConnect.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, nv.getHoTen());
			pstmt.setBoolean(2, nv.isGioiTinh());
			pstmt.setString(3, nv.getSoDienThoai());
			pstmt.setString(4, nv.getEmail());
			pstmt.setDate(5, nv.getNgaySinh());
			pstmt.setString(6, nv.getDiaChi());
			pstmt.setString(7, nv.getChucVu().getMaChucVu());
			pstmt.setString(8, nv.getMaNhanVien());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

    // Xóa mềm nhân viên
	public boolean xoaNhanVien(String maNV) {
		String sql = "UPDATE NhanVien SET trangThai = 0 WHERE maNhanVien = ?";
		try (Connection conn = DBConnect.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, maNV);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

    // Xóa vĩnh viễn nhân viên
	public boolean xoaSachNhanVien(String maNV) {
		String sql = "DELETE FROM NhanVien WHERE maNhanVien = ?";
		try (Connection conn = DBConnect.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, maNV);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

    // Sắp xếp danh sách nhân viên
	public ArrayList<NhanVien> sapXepNhanVien(String orderBy) {
		ArrayList<NhanVien> danhSach = new ArrayList<>();
		String sql = BASE_SELECT_SQL + " WHERE nv.trangThai = 1 ORDER BY " + orderBy;
		try (Connection conn = DBConnect.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql)) {
		while (rs.next()) danhSach.add(mapResultSetToNhanVien(rs));
		} catch (SQLException e) { e.printStackTrace(); }
		return danhSach;
	} 

    // Lọc nhân viên theo giới tính
	public ArrayList<NhanVien> locTheoGioiTinh(boolean gioiTinh) {
		ArrayList<NhanVien> danhSach = new ArrayList<>();
		String sql = BASE_SELECT_SQL + " WHERE nv.trangThai = 1 AND nv.gioiTinh = ?";
		try (Connection conn = DBConnect.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setBoolean(1, gioiTinh);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) danhSach.add(mapResultSetToNhanVien(rs));
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return danhSach;
	}

    // Lấy mã nhân viên tiếp theo tự động
	public String getMaNhanVienTiepTheo() {
		String maMoi = "NV000001";
		String sql = "SELECT TOP 1 maNhanVien FROM NhanVien ORDER BY maNhanVien DESC";
		try (Connection conn = DBConnect.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				String maLonNhat = rs.getString("maNhanVien");
				int soTiepTheo = Integer.parseInt(maLonNhat.substring(2)) + 1;
				maMoi = String.format("NV%06d", soTiepTheo);
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return maMoi;
	}

    // Xác thực thông tin đăng nhập
	public NhanVien xacThucDangNhap(String taiKhoan, String matKhau) {
		TaiKhoan_DAO taiKhoanDAO = new TaiKhoan_DAO();
		String matKhauMaHoaTuDB = taiKhoanDAO.getMatKhauMaHoa(taiKhoan);
		if (matKhauMaHoaTuDB == null) return null;
		if (TaiKhoan_DAO.kiemTraMatKhau(matKhau, matKhauMaHoaTuDB)) {
			return getNhanVienByTaiKhoan(taiKhoan);
		} else return null;
	}

    // Lấy thông tin nhân viên thông qua tài khoản
	public NhanVien getNhanVienByTaiKhoan(String taiKhoan) {
		NhanVien nv = null;
		String sql = BASE_SELECT_SQL + "JOIN TaiKhoan tk ON nv.maNhanVien = tk.maNhanVien WHERE nv.trangThai = 1 AND tk.taiKhoan = ?";
		try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, taiKhoan);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) nv = mapResultSetToNhanVien(rs);
			}
		} catch (Exception e) { e.printStackTrace(); }
		return nv;
	}
}
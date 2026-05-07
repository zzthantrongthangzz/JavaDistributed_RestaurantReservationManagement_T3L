package dao_impl;

import connect.DBConnect;
import entity.ChucVu;
import entity.NhanVien;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.sql.Date;
import java.util.ArrayList;

public class NhanVien_DAO {

	// ĐÃ TÁCH CYHPER THÀNH 2 PHẦN ĐỂ ĐẶT WHERE VÀO GIỮA
	private final String MATCH_NV = "MATCH (nv:NhanVien) ";
	private final String OPTIONAL_CV = " OPTIONAL MATCH (nv)-[:GIU_CHUC_VU]->(cv:ChucVu) ";
	private final String CYPHER_RETURN = "RETURN nv.maNhanVien AS maNhanVien, nv.hoTen AS hoTen, nv.gioiTinh AS gioiTinh, nv.soDienThoai AS soDienThoai, nv.email AS email, nv.ngaySinh AS ngaySinh, nv.diaChi AS diaChi, cv.maChucVu AS maChucVu, cv.tenChucVu AS tenChucVu ";

	private NhanVien mapNhanVien(Record r) {
		String maCV = r.get("maChucVu").isNull() ? "" : r.get("maChucVu").asString();
		String tenCV = r.get("tenChucVu").isNull() ? "Chưa có chức vụ" : r.get("tenChucVu").asString();
		ChucVu chucVu = new ChucVu(maCV, tenCV);

		Date ngaySinh = null;
		if (!r.get("ngaySinh").isNull()) {
			org.neo4j.driver.Value nsVal = r.get("ngaySinh");
			if (nsVal.type().name().equals("STRING")) {
				try { ngaySinh = Date.valueOf(nsVal.asString()); } catch (Exception ignored) {}
			} else if (nsVal.type().name().equals("DATE")) {
				ngaySinh = Date.valueOf(nsVal.asLocalDate());
			} else {
				ngaySinh = new Date(nsVal.asLong());
			}
		}

		boolean gioiTinh = true;
		if (!r.get("gioiTinh").isNull()) {
			if (r.get("gioiTinh").type().name().equals("STRING")) {
				gioiTinh = r.get("gioiTinh").asString().equalsIgnoreCase("Nam");
			} else {
				gioiTinh = r.get("gioiTinh").asBoolean();
			}
		}

		return new NhanVien(
				r.get("maNhanVien").asString(),
				r.get("hoTen").asString(),
				gioiTinh,
				r.get("soDienThoai").isNull() ? "" : r.get("soDienThoai").asString(),
				r.get("email").isNull() ? null : r.get("email").asString(),
				ngaySinh,
				r.get("diaChi").isNull() ? null : r.get("diaChi").asString(),
				chucVu
		);
	}

	public ArrayList<NhanVien> getAllNhanVien() {
		ArrayList<NhanVien> ds = new ArrayList<>();
		// SỬA Ở ĐÂY: WHERE nằm ngay sau MATCH_NV
		String cypher = MATCH_NV + "WHERE nv.trangThai = 1 " + OPTIONAL_CV + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher);
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	public ArrayList<NhanVien> getAllNhanVienDaNghi() {
		ArrayList<NhanVien> ds = new ArrayList<>();
		// SỬA Ở ĐÂY
		String cypher = MATCH_NV + "WHERE nv.trangThai = 0 " + OPTIONAL_CV + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher);
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	public NhanVien timMotNhanVienTheoMa(String maNhanVien) {
		// SỬA Ở ĐÂY
		String cypher = MATCH_NV + "WHERE nv.maNhanVien = $ma " + OPTIONAL_CV + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("ma", maNhanVien));
			if (result.hasNext()) return mapNhanVien(result.next());
		}
		return null;
	}

	public ArrayList<NhanVien> timKiemNhanVienTheoMa(String maNhanVien) {
		ArrayList<NhanVien> ds = new ArrayList<>();
		// SỬA Ở ĐÂY
		String cypher = MATCH_NV + "WHERE nv.trangThai = 1 AND nv.maNhanVien = $ma " + OPTIONAL_CV + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("ma", maNhanVien.trim().toUpperCase()));
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	public ArrayList<NhanVien> timKiemNhanVienTheoTen(String hoTen) {
		ArrayList<NhanVien> ds = new ArrayList<>();
		// SỬA Ở ĐÂY
		String cypher = MATCH_NV + "WHERE nv.trangThai = 1 AND toLower(nv.hoTen) CONTAINS toLower($ten) " + OPTIONAL_CV + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("ten", hoTen.trim()));
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	public ArrayList<NhanVien> timKiemNhanVienTheoSDT(String sdt) {
		ArrayList<NhanVien> ds = new ArrayList<>();
		// SỬA Ở ĐÂY
		String cypher = MATCH_NV + "WHERE nv.trangThai = 1 AND nv.soDienThoai CONTAINS $sdt " + OPTIONAL_CV + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("sdt", sdt.trim()));
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	public ArrayList<NhanVien> sapXepNhanVien(String orderBy) {
		ArrayList<NhanVien> ds = new ArrayList<>();
		// SỬA Ở ĐÂY
		String cypher = MATCH_NV + "WHERE nv.trangThai = 1 " + OPTIONAL_CV + CYPHER_RETURN + " ORDER BY " + orderBy;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher);
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	public ArrayList<NhanVien> locTheoGioiTinh(boolean gioiTinh) {
		ArrayList<NhanVien> ds = new ArrayList<>();
		// SỬA Ở ĐÂY
		String cypher = MATCH_NV + "WHERE nv.trangThai = 1 AND nv.gioiTinh = $gt " + OPTIONAL_CV + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("gt", gioiTinh));
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	public NhanVien getNhanVienByTaiKhoan(String taiKhoan) {
		// SỬA Ở ĐÂY: Dời WHERE nv.trangThai = 1 lên trước OPTIONAL MATCH
		String cypher = "MATCH (tk:TaiKhoan {taiKhoan: $tk})<-[:CO_TAI_KHOAN]-(nv:NhanVien) " +
				"WHERE nv.trangThai = 1 " + OPTIONAL_CV + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("tk", taiKhoan));
			if (result.hasNext()) return mapNhanVien(result.next());
		}
		return null;
	}

	// ====================================================================================
	// CÁC HÀM UPDATE / DELETE / INSERT BÊN DƯỚI KHÔNG CẦN ĐỔI VÌ CHÚNG KHÔNG DÙNG OPTIONAL MATCH
	// ====================================================================================

	public boolean themNhanVien(NhanVien nv) {
		String cypher = "MATCH (cv:ChucVu {maChucVu: $maCV}) CREATE (nv:NhanVien {maNhanVien: $ma, hoTen: $ten, gioiTinh: $gioiTinh, soDienThoai: $sdt, email: $email, ngaySinh: $ngaySinh, diaChi: $diaChi, trangThai: 1})-[:GIU_CHUC_VU]->(cv)";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters(
					"maCV", nv.getChucVu().getMaChucVu(),
					"ma", nv.getMaNhanVien(),
					"ten", nv.getHoTen(),
					"gioiTinh", nv.isGioiTinh(),
					"sdt", nv.getSoDienThoai(),
					"email", nv.getEmail(),
					"ngaySinh", nv.getNgaySinh() != null ? nv.getNgaySinh().toLocalDate() : null,
					"diaChi", nv.getDiaChi()
			));
			return true;
		} catch (Exception e) { return false; }
	}

	public boolean capNhatNhanVien(NhanVien nv) {
		String cypher = "MATCH (nv:NhanVien {maNhanVien: $ma})-[r:GIU_CHUC_VU]->() DELETE r WITH nv MATCH (cv:ChucVu {maChucVu: $maCV}) MERGE (nv)-[:GIU_CHUC_VU]->(cv) SET nv.hoTen = $ten, nv.gioiTinh = $gioiTinh, nv.soDienThoai = $sdt, nv.email = $email, nv.ngaySinh = $ngaySinh, nv.diaChi = $diaChi";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters(
					"ma", nv.getMaNhanVien(),
					"maCV", nv.getChucVu().getMaChucVu(),
					"ten", nv.getHoTen(),
					"gioiTinh", nv.isGioiTinh(),
					"sdt", nv.getSoDienThoai(),
					"email", nv.getEmail(),
					"ngaySinh", nv.getNgaySinh() != null ? nv.getNgaySinh().toLocalDate() : null,
					"diaChi", nv.getDiaChi()
			));
			return true;
		} catch (Exception e) { return false; }
	}

	public boolean xoaNhanVien(String maNV) {
		String cypher = "MATCH (nv:NhanVien {maNhanVien: $ma}) SET nv.trangThai = 0";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters("ma", maNV));
			return true;
		} catch (Exception e) { return false; }
	}

	public boolean xoaSachNhanVien(String maNV) {
		String cypher = "MATCH (nv:NhanVien {maNhanVien: $ma}) DETACH DELETE nv";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters("ma", maNV));
			return true;
		} catch (Exception e) { return false; }
	}

	public boolean khoiPhucNhanVien(String maNV) {
		String cypher = "MATCH (nv:NhanVien {maNhanVien: $ma}) SET nv.trangThai = 1";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters("ma", maNV));
			return true;
		} catch (Exception e) { return false; }
	}

	public String getMaNhanVienTiepTheo() {
		String cypher = "MATCH (nv:NhanVien) WHERE nv.maNhanVien STARTS WITH 'NV' RETURN nv.maNhanVien AS maxMa ORDER BY maxMa DESC LIMIT 1";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher);
			if (result.hasNext()) {
				String maxMa = result.next().get("maxMa").asString();
				int soTiepTheo = Integer.parseInt(maxMa.substring(2)) + 1;
				return String.format("NV%06d", soTiepTheo);
			}
		}
		return "NV000001";
	}

	public NhanVien xacThucDangNhap(String taiKhoan, String matKhau) {
		TaiKhoan_DAO taiKhoanDAO = new TaiKhoan_DAO();
		String matKhauMaHoaTuDB = taiKhoanDAO.getMatKhauMaHoa(taiKhoan);

		if (matKhauMaHoaTuDB == null) return null;

		if (TaiKhoan_DAO.kiemTraMatKhau(matKhau, matKhauMaHoaTuDB)) {
			return getNhanVienByTaiKhoan(taiKhoan);
		}
		return null;
	}
}
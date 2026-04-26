package dao_impl;

import connect.DBConnect;
import entity.ChucVu;
import entity.NhanVien;
import rmi_interfaces.INhanVien_DAO;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.util.ArrayList;

public class NhanVien_DAO_Impl extends UnicastRemoteObject implements INhanVien_DAO {

	public NhanVien_DAO_Impl() throws RemoteException {
		super();
	}

	private NhanVien mapNhanVien(Record r) {
		ChucVu chucVu = new ChucVu(r.get("maChucVu").asString(), r.get("tenChucVu").asString());
		return new NhanVien(
				r.get("maNhanVien").asString(), r.get("hoTen").asString(), r.get("gioiTinh").asBoolean(),
				r.get("soDienThoai").asString(), r.get("email").isNull() ? null : r.get("email").asString(),
				r.get("ngaySinh").isNull() ? null : new Date(r.get("ngaySinh").asLong()),
				r.get("diaChi").isNull() ? null : r.get("diaChi").asString(), chucVu
		);
	}

	private final String CYPHER_SELECT_BASE = "MATCH (nv:NhanVien)-[:GIU_CHUC_VU]->(cv:ChucVu) ";
	private final String CYPHER_RETURN = "RETURN nv.maNhanVien AS maNhanVien, nv.hoTen AS hoTen, nv.gioiTinh AS gioiTinh, " +
			"nv.soDienThoai AS soDienThoai, nv.email AS email, nv.ngaySinh AS ngaySinh, " +
			"nv.diaChi AS diaChi, cv.maChucVu AS maChucVu, cv.tenChucVu AS tenChucVu ";

	@Override
	public ArrayList<NhanVien> getAllNhanVien() throws RemoteException {
		ArrayList<NhanVien> ds = new ArrayList<>();
		String cypher = CYPHER_SELECT_BASE + "WHERE nv.trangThai = 1 " + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher);
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	@Override
	public ArrayList<NhanVien> getAllNhanVienDaNghi() throws RemoteException {
		ArrayList<NhanVien> ds = new ArrayList<>();
		String cypher = CYPHER_SELECT_BASE + "WHERE nv.trangThai = 0 " + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher);
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	@Override
	public boolean khoiPhucNhanVien(String maNV) throws RemoteException {
		String cypher = "MATCH (nv:NhanVien {maNhanVien: $ma}) SET nv.trangThai = 1";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters("ma", maNV));
			return true;
		} catch (Exception e) { return false; }
	}

	@Override
	public NhanVien timMotNhanVienTheoMa(String maNhanVien) throws RemoteException {
		String cypher = CYPHER_SELECT_BASE + "WHERE nv.maNhanVien = $ma " + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("ma", maNhanVien));
			if (result.hasNext()) return mapNhanVien(result.next());
		}
		return null;
	}

	@Override
	public ArrayList<NhanVien> timKiemNhanVienTheoMa(String maNhanVien) throws RemoteException {
		ArrayList<NhanVien> ds = new ArrayList<>();
		String cypher = CYPHER_SELECT_BASE + "WHERE nv.trangThai = 1 AND nv.maNhanVien = $ma " + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("ma", maNhanVien.trim().toUpperCase()));
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	@Override
	public ArrayList<NhanVien> timKiemNhanVienTheoTen(String hoTen) throws RemoteException {
		ArrayList<NhanVien> ds = new ArrayList<>();
		String cypher = CYPHER_SELECT_BASE + "WHERE nv.trangThai = 1 AND toLower(nv.hoTen) CONTAINS toLower($ten) " + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("ten", hoTen.trim()));
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	@Override
	public ArrayList<NhanVien> timKiemNhanVienTheoSDT(String sdt) throws RemoteException {
		ArrayList<NhanVien> ds = new ArrayList<>();
		String cypher = CYPHER_SELECT_BASE + "WHERE nv.trangThai = 1 AND nv.soDienThoai CONTAINS $sdt " + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("sdt", sdt.trim()));
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	@Override
	public boolean themNhanVien(NhanVien nv) throws RemoteException {
		String cypher = "MATCH (cv:ChucVu {maChucVu: $maCV}) " +
				"CREATE (nv:NhanVien {maNhanVien: $ma, hoTen: $ten, gioiTinh: $gioiTinh, " +
				"soDienThoai: $sdt, email: $email, ngaySinh: $ngaySinh, diaChi: $diaChi, trangThai: 1})-[:GIU_CHUC_VU]->(cv)";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters(
					"maCV", nv.getChucVu().getMaChucVu(), "ma", nv.getMaNhanVien(), "ten", nv.getHoTen(),
					"gioiTinh", nv.isGioiTinh(), "sdt", nv.getSoDienThoai(), "email", nv.getEmail(),
					"ngaySinh", nv.getNgaySinh() != null ? nv.getNgaySinh().getTime() : null, "diaChi", nv.getDiaChi()
			));
			return true;
		} catch (Exception e) { return false; }
	}

	@Override
	public boolean capNhatNhanVien(NhanVien nv) throws RemoteException {
		String cypher = "MATCH (nv:NhanVien {maNhanVien: $ma})-[r:GIU_CHUC_VU]->() " +
				"DELETE r WITH nv MATCH (cv:ChucVu {maChucVu: $maCV}) " +
				"MERGE (nv)-[:GIU_CHUC_VU]->(cv) " +
				"SET nv.hoTen = $ten, nv.gioiTinh = $gioiTinh, nv.soDienThoai = $sdt, " +
				"nv.email = $email, nv.ngaySinh = $ngaySinh, nv.diaChi = $diaChi";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters(
					"ma", nv.getMaNhanVien(), "maCV", nv.getChucVu().getMaChucVu(), "ten", nv.getHoTen(),
					"gioiTinh", nv.isGioiTinh(), "sdt", nv.getSoDienThoai(), "email", nv.getEmail(),
					"ngaySinh", nv.getNgaySinh() != null ? nv.getNgaySinh().getTime() : null, "diaChi", nv.getDiaChi()
			));
			return true;
		} catch (Exception e) { return false; }
	}

	@Override
	public boolean xoaNhanVien(String maNV) throws RemoteException {
		String cypher = "MATCH (nv:NhanVien {maNhanVien: $ma}) SET nv.trangThai = 0";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters("ma", maNV));
			return true;
		} catch (Exception e) { return false; }
	}

	@Override
	public boolean xoaSachNhanVien(String maNV) throws RemoteException {
		String cypher = "MATCH (nv:NhanVien {maNhanVien: $ma}) DETACH DELETE nv";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters("ma", maNV));
			return true;
		} catch (Exception e) { return false; }
	}

	@Override
	public ArrayList<NhanVien> sapXepNhanVien(String orderBy) throws RemoteException {
		ArrayList<NhanVien> ds = new ArrayList<>();
		String cypher = CYPHER_SELECT_BASE + "WHERE nv.trangThai = 1 " + CYPHER_RETURN + " ORDER BY " + orderBy;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher);
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	@Override
	public ArrayList<NhanVien> locTheoGioiTinh(boolean gioiTinh) throws RemoteException {
		ArrayList<NhanVien> ds = new ArrayList<>();
		String cypher = CYPHER_SELECT_BASE + "WHERE nv.trangThai = 1 AND nv.gioiTinh = $gt " + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("gt", gioiTinh));
			while (result.hasNext()) ds.add(mapNhanVien(result.next()));
		}
		return ds;
	}

	@Override
	public String getMaNhanVienTiepTheo() throws RemoteException {
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

	@Override
	public NhanVien xacThucDangNhap(String taiKhoan, String matKhau) throws RemoteException {
		// Đã đổi thành TaiKhoan_DAO_Impl
		TaiKhoan_DAO_Impl taiKhoanDAO = new TaiKhoan_DAO_Impl();
		String matKhauMaHoaTuDB = taiKhoanDAO.getMatKhauMaHoa(taiKhoan);

		if (matKhauMaHoaTuDB == null) return null;

		// Gọi hàm static từ TaiKhoan_DAO_Impl
		if (TaiKhoan_DAO_Impl.kiemTraMatKhau(matKhau, matKhauMaHoaTuDB)) {
			return getNhanVienByTaiKhoan(taiKhoan);
		}
		return null;
	}

	@Override
	public NhanVien getNhanVienByTaiKhoan(String taiKhoan) throws RemoteException {
		// Chuyển SQL JOIN thành việc tìm nhân viên thông qua Relationship [:CO_TAI_KHOAN] với TaiKhoan
		String cypher = "MATCH (tk:TaiKhoan {taiKhoan: $tk})<-[:CO_TAI_KHOAN]-(nv:NhanVien)-[:GIU_CHUC_VU]->(cv:ChucVu) " +
				"WHERE nv.trangThai = 1 " + CYPHER_RETURN;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("tk", taiKhoan));
			if (result.hasNext()) return mapNhanVien(result.next());
		}
		return null;
	}
}
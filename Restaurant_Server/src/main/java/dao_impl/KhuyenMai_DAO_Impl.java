package dao_impl;

import connect.DBConnect;
import entity.KhuyenMai;
import rmi_interfaces.IKhuyenMai_DAO;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KhuyenMai_DAO_Impl extends UnicastRemoteObject implements IKhuyenMai_DAO {

	public KhuyenMai_DAO_Impl() throws RemoteException {
		super();
	}

	// Hàm phụ trợ map dữ liệu từ Node sang Object
	private KhuyenMai mapKhuyenMai(Record r) {
		return new KhuyenMai(
				r.get("maKhuyenMai").asString(),
				r.get("tenKhuyenMai").asString(),
				r.get("loaiKhuyenMai").asString(),
				new Date(r.get("ngayBatDau").asLong()),
				new Date(r.get("ngayKetThuc").asLong()),
				r.get("giaTriGiam").asDouble(),
				r.get("hienThi").asInt()
		);
	}

	@Override
	public List<KhuyenMai> getAllList() throws RemoteException {
		List<KhuyenMai> list = new ArrayList<>();
		long now = System.currentTimeMillis();
		String cypher = "MATCH (km:KhuyenMai) WHERE km.hienThi = 1 AND km.ngayKetThuc >= $now " +
				"RETURN km.maKhuyenMai AS maKhuyenMai, km.tenKhuyenMai AS tenKhuyenMai, " +
				"km.loaiKhuyenMai AS loaiKhuyenMai, km.ngayBatDau AS ngayBatDau, " +
				"km.ngayKetThuc AS ngayKetThuc, km.giaTriGiam AS giaTriGiam, km.hienThi AS hienThi";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("now", now));
			while (result.hasNext()) {
				list.add(mapKhuyenMai(result.next()));
			}
		}
		return list;
	}

	@Override
	public List<KhuyenMai> getAllListSorted(String sortBy) throws RemoteException {
		List<KhuyenMai> list = new ArrayList<>();
		String orderClause = "ORDER BY km.maKhuyenMai";
		switch (sortBy) {
			case "Tên": orderClause = "ORDER BY km.tenKhuyenMai"; break;
			case "Giá trị": orderClause = "ORDER BY km.giaTriGiam DESC"; break;
			case "Ngày bắt đầu": orderClause = "ORDER BY km.ngayBatDau DESC"; break;
		}
		String cypher = "MATCH (km:KhuyenMai) WHERE km.hienThi = 1 RETURN km.maKhuyenMai AS maKhuyenMai, " +
				"km.tenKhuyenMai AS tenKhuyenMai, km.loaiKhuyenMai AS loaiKhuyenMai, " +
				"km.ngayBatDau AS ngayBatDau, km.ngayKetThuc AS ngayKetThuc, km.giaTriGiam AS giaTriGiam, " +
				"km.hienThi AS hienThi " + orderClause;
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher);
			while (result.hasNext()) list.add(mapKhuyenMai(result.next()));
		}
		return list;
	}

	@Override
	public List<KhuyenMai> getByLoaiKhuyenMai(String loaiKM) throws RemoteException {
		List<KhuyenMai> list = new ArrayList<>();
		String cypher = "MATCH (km:KhuyenMai) WHERE km.hienThi = 1 AND km.loaiKhuyenMai = $loai " +
				"RETURN km.maKhuyenMai AS maKhuyenMai, km.tenKhuyenMai AS tenKhuyenMai, " +
				"km.loaiKhuyenMai AS loaiKhuyenMai, km.ngayBatDau AS ngayBatDau, " +
				"km.ngayKetThuc AS ngayKetThuc, km.giaTriGiam AS giaTriGiam, km.hienThi AS hienThi";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("loai", loaiKM));
			while (result.hasNext()) list.add(mapKhuyenMai(result.next()));
		}
		return list;
	}

	@Override
	public KhuyenMai getByMaKhuyenMai(String maKM) throws RemoteException {
		String cypher = "MATCH (km:KhuyenMai {maKhuyenMai: $ma}) " +
				"RETURN km.maKhuyenMai AS maKhuyenMai, km.tenKhuyenMai AS tenKhuyenMai, " +
				"km.loaiKhuyenMai AS loaiKhuyenMai, km.ngayBatDau AS ngayBatDau, " +
				"km.ngayKetThuc AS ngayKetThuc, km.giaTriGiam AS giaTriGiam, km.hienThi AS hienThi";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("ma", maKM));
			if (result.hasNext()) return mapKhuyenMai(result.next());
		}
		return null;
	}

	@Override
	public boolean themKhuyenMai(KhuyenMai km) throws RemoteException {
		String cypher = "CREATE (km:KhuyenMai {maKhuyenMai: $ma, tenKhuyenMai: $ten, loaiKhuyenMai: $loai, " +
				"ngayBatDau: $batDau, ngayKetThuc: $ketThuc, giaTriGiam: $giaTri, hienThi: 1})";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters(
					"ma", km.getMaKhuyenMai(), "ten", km.getTenKhuyenMai(), "loai", km.getLoaiKhuyenMai(),
					"batDau", km.getNgayBatDau().getTime(), "ketThuc", km.getNgayKetThuc().getTime(),
					"giaTri", km.getGiaTriGiam()
			));
			return true;
		} catch (Exception e) { return false; }
	}

	@Override
	public boolean capNhatKhuyenMai(KhuyenMai km) throws RemoteException {
		String cypher = "MATCH (km:KhuyenMai {maKhuyenMai: $ma}) " +
				"SET km.tenKhuyenMai = $ten, km.loaiKhuyenMai = $loai, km.ngayBatDau = $batDau, " +
				"km.ngayKetThuc = $ketThuc, km.giaTriGiam = $giaTri, km.hienThi = $hienThi";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters(
					"ma", km.getMaKhuyenMai(), "ten", km.getTenKhuyenMai(), "loai", km.getLoaiKhuyenMai(),
					"batDau", km.getNgayBatDau().getTime(), "ketThuc", km.getNgayKetThuc().getTime(),
					"giaTri", km.getGiaTriGiam(), "hienThi", km.getHienThi()
			));
			return true;
		} catch (Exception e) { return false; }
	}

	@Override
	public boolean anKhuyenMai(String maKM) throws RemoteException {
		String cypher = "MATCH (km:KhuyenMai {maKhuyenMai: $ma}) SET km.hienThi = 0";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters("ma", maKM));
			return true;
		} catch (Exception e) { return false; }
	}

	@Override
	public List<KhuyenMai> getKhuyenMaiHetHan() throws RemoteException {
		List<KhuyenMai> list = new ArrayList<>();
		long now = System.currentTimeMillis();
		String cypher = "MATCH (km:KhuyenMai) WHERE km.hienThi = 1 AND km.ngayKetThuc < $now " +
				"RETURN km.maKhuyenMai AS maKhuyenMai, km.tenKhuyenMai AS tenKhuyenMai, " +
				"km.loaiKhuyenMai AS loaiKhuyenMai, km.ngayBatDau AS ngayBatDau, " +
				"km.ngayKetThuc AS ngayKetThuc, km.giaTriGiam AS giaTriGiam, km.hienThi AS hienThi";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("now", now));
			while (result.hasNext()) list.add(mapKhuyenMai(result.next()));
		}
		return list;
	}

	@Override
	public List<KhuyenMai> timKiem(String tuKhoa) throws RemoteException {
		List<KhuyenMai> list = new ArrayList<>();
		String cypher = "MATCH (km:KhuyenMai) WHERE km.hienThi = 1 AND " +
				"(km.maKhuyenMai CONTAINS $tuKhoa OR km.tenKhuyenMai CONTAINS $tuKhoa) " +
				"RETURN km.maKhuyenMai AS maKhuyenMai, km.tenKhuyenMai AS tenKhuyenMai, " +
				"km.loaiKhuyenMai AS loaiKhuyenMai, km.ngayBatDau AS ngayBatDau, " +
				"km.ngayKetThuc AS ngayKetThuc, km.giaTriGiam AS giaTriGiam, km.hienThi AS hienThi";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
			while (result.hasNext()) list.add(mapKhuyenMai(result.next()));
		}
		return list;
	}

	@Override
	public String taoMaKhuyenMaiTuDong() throws RemoteException {
		try (Session session = DBConnect.getSession()) {
			String cypher = "MATCH (km:KhuyenMai) WHERE km.maKhuyenMai STARTS WITH 'KM' " +
					"RETURN km.maKhuyenMai AS ma ORDER BY ma DESC LIMIT 1";
			Result result = session.run(cypher);
			if (result.hasNext()) {
				String maKMCuoi = result.next().get("ma").asString();
				int soTiepTheo = Integer.parseInt(maKMCuoi.substring(2)) + 1;
				return String.format("KM%06d", soTiepTheo);
			}
		}
		return "KM000001";
	}

	@Override
	public boolean kiemTraMaTonTai(String maKM) throws RemoteException {
		String cypher = "MATCH (km:KhuyenMai {maKhuyenMai: $ma}) RETURN count(km) AS sl";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("ma", maKM));
			if (result.hasNext()) return result.next().get("sl").asInt() > 0;
		}
		return false;
	}

	@Override
	public int getTongLuotSuDung(java.util.Date tuNgay, java.util.Date denNgay) throws RemoteException {
		if (tuNgay == null || denNgay == null) return 0;
		String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
				"WHERE hd.maKhuyenMai IS NOT NULL AND hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den " +
				"RETURN count(hd) AS tongSoLuot";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", denNgay.getTime()));
			if (result.hasNext()) return result.next().get("tongSoLuot").asInt();
		}
		return 0;
	}

	@Override
	public BigDecimal getTongTienGiam(java.util.Date tuNgay, java.util.Date denNgay) throws RemoteException {
		if (tuNgay == null || denNgay == null) return BigDecimal.ZERO;
		String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
				"WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den AND hd.maKhuyenMai IS NOT NULL " +
				"MATCH (km:KhuyenMai {maKhuyenMai: hd.maKhuyenMai}) " +
				"OPTIONAL MATCH (hd)-[c:BAO_GOM]->(m:MonAn) " +
				"WITH hd, km, sum(c.soLuong * c.donGia) AS tongTienHoaDon " +
				"RETURN SUM(CASE " +
				"   WHEN km.loaiKhuyenMai = 'Giảm tiền' THEN km.giaTriGiam " +
				"   WHEN km.loaiKhuyenMai = 'Giảm %' THEN coalesce(tongTienHoaDon, 0) * (km.giaTriGiam / 100.0) " +
				"   ELSE 0 END) AS TongTienGiam";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", denNgay.getTime()));
			if (result.hasNext()) return BigDecimal.valueOf(result.next().get("TongTienGiam").asDouble());
		}
		return BigDecimal.ZERO;
	}

	@Override
	public Map<java.util.Date, BigDecimal> getTienGiamTheoNgay(java.util.Date tuNgay, java.util.Date denNgay) throws RemoteException {
		Map<java.util.Date, BigDecimal> map = new TreeMap<>();
		if (tuNgay == null || denNgay == null) return map;

		// Nhóm theo ngày bằng cách chuyển milliseconds sang định dạng date của Neo4j
		String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
				"WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den AND hd.maKhuyenMai IS NOT NULL " +
				"MATCH (km:KhuyenMai {maKhuyenMai: hd.maKhuyenMai}) " +
				"OPTIONAL MATCH (hd)-[c:BAO_GOM]->(m:MonAn) " +
				"WITH date(datetime({epochMillis: hd.ngayLapHoaDon})) AS ngay, hd, km, sum(c.soLuong * c.donGia) AS tongTienHoaDon " +
				"RETURN ngay, SUM(CASE " +
				"   WHEN km.loaiKhuyenMai = 'Giảm tiền' THEN km.giaTriGiam " +
				"   WHEN km.loaiKhuyenMai = 'Giảm %' THEN coalesce(tongTienHoaDon, 0) * (km.giaTriGiam / 100.0) " +
				"   ELSE 0 END) AS TienGiamTrongNgay ORDER BY ngay";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", denNgay.getTime()));
			while (result.hasNext()) {
				Record r = result.next();
				// Parse date từ kết quả (ví dụ "2026-04-26")
				java.time.LocalDate localDate = r.get("ngay").asLocalDate();
				map.put(java.sql.Date.valueOf(localDate), BigDecimal.valueOf(r.get("TienGiamTrongNgay").asDouble()));
			}
		}
		return map;
	}

	@Override
	public List<KhuyenMai> locDanhSach(String maKM, String tenKM, String loaiKM, String giaTriFilter, java.util.Date tuNgay, java.util.Date denNgay, String sapXep) throws RemoteException {
		List<KhuyenMai> list = new ArrayList<>();
		StringBuilder cypher = new StringBuilder("MATCH (km:KhuyenMai) WHERE km.hienThi = 1 ");

		if (maKM != null && !maKM.isEmpty()) cypher.append("AND km.maKhuyenMai CONTAINS '").append(maKM).append("' ");
		if (tenKM != null && !tenKM.isEmpty()) cypher.append("AND km.tenKhuyenMai CONTAINS '").append(tenKM).append("' ");
		if (loaiKM != null && !loaiKM.isEmpty()) cypher.append("AND km.loaiKhuyenMai = '").append(loaiKM).append("' ");
		if (tuNgay != null) cypher.append("AND km.ngayBatDau >= ").append(tuNgay.getTime()).append(" ");
		if (denNgay != null) cypher.append("AND km.ngayKetThuc <= ").append(denNgay.getTime()).append(" ");

		if (giaTriFilter != null) {
			switch (giaTriFilter) {
				case "Dưới 50K": cypher.append("AND km.giaTriGiam < 50000 "); break;
				case "50K-100K": cypher.append("AND km.giaTriGiam >= 50000 AND km.giaTriGiam <= 100000 "); break;
				case "100K-200K": cypher.append("AND km.giaTriGiam > 100000 AND km.giaTriGiam <= 200000 "); break;
				case "Trên 200K": cypher.append("AND km.giaTriGiam > 200000 "); break;
			}
		}

		if (sapXep != null) {
			switch (sapXep) {
				case "Tên A-Z": cypher.append("ORDER BY km.tenKhuyenMai ASC"); break;
				case "Tên Z-A": cypher.append("ORDER BY km.tenKhuyenMai DESC"); break;
				case "Giá trị cao-thấp": cypher.append("ORDER BY km.giaTriGiam DESC"); break;
				case "Giá trị thấp-cao": cypher.append("ORDER BY km.giaTriGiam ASC"); break;
			}
		}

		String finalCypher = cypher.toString().replace("MATCH (km:KhuyenMai) WHERE km.hienThi = 1 ORDER", "MATCH (km:KhuyenMai) WHERE km.hienThi = 1 RETURN km.maKhuyenMai AS maKhuyenMai, km.tenKhuyenMai AS tenKhuyenMai, km.loaiKhuyenMai AS loaiKhuyenMai, km.ngayBatDau AS ngayBatDau, km.ngayKetThuc AS ngayKetThuc, km.giaTriGiam AS giaTriGiam, km.hienThi AS hienThi ORDER");
		if(!finalCypher.contains("RETURN")) {
			finalCypher += " RETURN km.maKhuyenMai AS maKhuyenMai, km.tenKhuyenMai AS tenKhuyenMai, km.loaiKhuyenMai AS loaiKhuyenMai, km.ngayBatDau AS ngayBatDau, km.ngayKetThuc AS ngayKetThuc, km.giaTriGiam AS giaTriGiam, km.hienThi AS hienThi";
		}

		try (Session session = DBConnect.getSession()) {
			Result result = session.run(finalCypher);
			while (result.hasNext()) list.add(mapKhuyenMai(result.next()));
		}
		return list;
	}

	@Override
	public List<Object[]> getThongKeChiTietKhuyenMai(java.util.Date tuNgay, java.util.Date denNgay) throws RemoteException {
		List<Object[]> list = new ArrayList<>();
		if (tuNgay == null || denNgay == null) return list;

		String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
				"WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den AND hd.maKhuyenMai IS NOT NULL " +
				"MATCH (km:KhuyenMai {maKhuyenMai: hd.maKhuyenMai}) " +
				"OPTIONAL MATCH (hd)-[c:BAO_GOM]->(m:MonAn) " +
				"WITH km, count(hd) AS SoLuot, sum(c.soLuong * c.donGia) AS tongTienHoaDon " +
				"RETURN km.maKhuyenMai AS ma, km.tenKhuyenMai AS ten, km.ngayBatDau AS batDau, km.ngayKetThuc AS ketThuc, SoLuot, " +
				"SUM(CASE " +
				"   WHEN km.loaiKhuyenMai = 'Giảm tiền' THEN km.giaTriGiam " +
				"   WHEN km.loaiKhuyenMai = 'Giảm %' THEN coalesce(tongTienHoaDon, 0) * (km.giaTriGiam / 100.0) " +
				"   ELSE 0 END) AS TongTien ORDER BY TongTien DESC";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", denNgay.getTime()));
			while (result.hasNext()) {
				Record r = result.next();
				list.add(new Object[]{
						r.get("ma").asString(),
						r.get("ten").asString(),
						r.get("SoLuot").asInt(),
						BigDecimal.valueOf(r.get("TongTien").asDouble()),
						new Date(r.get("batDau").asLong()),
						new Date(r.get("ketThuc").asLong())
				});
			}
		}
		return list;
	}
}
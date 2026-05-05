package dao_impl;

import connect.DBConnect;
import entity.KhuyenMai;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class KhuyenMai_DAO {

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

	public List<KhuyenMai> getAllList() {
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

	public List<KhuyenMai> getAllListSorted(String sortBy) {
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

	public List<KhuyenMai> getByLoaiKhuyenMai(String loaiKM) {
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

	public KhuyenMai getByMaKhuyenMai(String maKM) {
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

	public boolean themKhuyenMai(KhuyenMai km) {
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

	public boolean capNhatKhuyenMai(KhuyenMai km) {
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

	public boolean anKhuyenMai(String maKM) {
		String cypher = "MATCH (km:KhuyenMai {maKhuyenMai: $ma}) SET km.hienThi = 0";
		try (Session session = DBConnect.getSession()) {
			session.run(cypher, Values.parameters("ma", maKM));
			return true;
		} catch (Exception e) { return false; }
	}

	public List<KhuyenMai> getKhuyenMaiHetHan() {
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

	public List<KhuyenMai> timKiem(String tuKhoa) {
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

	public String taoMaKhuyenMaiTuDong() {
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

	public boolean kiemTraMaTonTai(String maKM) {
		String cypher = "MATCH (km:KhuyenMai {maKhuyenMai: $ma}) RETURN count(km) AS sl";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("ma", maKM));
			if (result.hasNext()) return result.next().get("sl").asInt() > 0;
		}
		return false;
	}

	public int getTongLuotSuDung(java.util.Date tuNgay, java.util.Date denNgay) {
		if (tuNgay == null || denNgay == null) return 0;
		String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
				"WHERE hd.maKhuyenMai IS NOT NULL " +
				"AND datetime(hd.ngayLapHoaDon + '+07:00').epochMillis >= $tu " +
				"AND datetime(hd.ngayLapHoaDon + '+07:00').epochMillis <= $den " +
				"RETURN count(hd) AS tongSoLuot";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", denNgay.getTime()));
			if (result.hasNext()) return result.next().get("tongSoLuot").asInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public BigDecimal getTongTienGiam(java.util.Date tuNgay, java.util.Date denNgay) {
		if (tuNgay == null || denNgay == null) return BigDecimal.ZERO;
		String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
				"WHERE hd.maKhuyenMai IS NOT NULL " +
				"AND datetime(hd.ngayLapHoaDon + '+07:00').epochMillis >= $tu " +
				"AND datetime(hd.ngayLapHoaDon + '+07:00').epochMillis <= $den " +
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BigDecimal.ZERO;
	}

	public Map<java.util.Date, BigDecimal> getTienGiamTheoNgay(java.util.Date tuNgay, java.util.Date denNgay) {
		Map<java.util.Date, BigDecimal> map = new TreeMap<>();
		if (tuNgay == null || denNgay == null) return map;

		String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
				"WHERE hd.maKhuyenMai IS NOT NULL " +
				"AND datetime(hd.ngayLapHoaDon + '+07:00').epochMillis >= $tu " +
				"AND datetime(hd.ngayLapHoaDon + '+07:00').epochMillis <= $den " +
				"MATCH (km:KhuyenMai {maKhuyenMai: hd.maKhuyenMai}) " +
				"OPTIONAL MATCH (hd)-[c:BAO_GOM]->(m:MonAn) " +
				"WITH date(datetime(hd.ngayLapHoaDon + '+07:00')) AS ngay, hd, km, sum(c.soLuong * c.donGia) AS tongTienHoaDon " +
				"RETURN ngay, SUM(CASE " +
				"   WHEN km.loaiKhuyenMai = 'Giảm tiền' THEN km.giaTriGiam " +
				"   WHEN km.loaiKhuyenMai = 'Giảm %' THEN coalesce(tongTienHoaDon, 0) * (km.giaTriGiam / 100.0) " +
				"   ELSE 0 END) AS TienGiamTrongNgay ORDER BY ngay";
		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", denNgay.getTime()));
			while (result.hasNext()) {
				Record r = result.next();
				java.time.LocalDate localDate = r.get("ngay").asLocalDate();
				map.put(java.sql.Date.valueOf(localDate), BigDecimal.valueOf(r.get("TienGiamTrongNgay").asDouble()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public List<KhuyenMai> locDanhSach(String maKM, String tenKM, String loaiKM, String giaTriFilter, java.util.Date tuNgay, java.util.Date denNgay, String sapXep) {
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

		cypher.append("RETURN km.maKhuyenMai AS maKhuyenMai, km.tenKhuyenMai AS tenKhuyenMai, km.loaiKhuyenMai AS loaiKhuyenMai, km.ngayBatDau AS ngayBatDau, km.ngayKetThuc AS ngayKetThuc, km.giaTriGiam AS giaTriGiam, km.hienThi AS hienThi ");

		if (sapXep != null && !sapXep.isEmpty()) {
			switch (sapXep) {
				case "Tên A-Z": cypher.append("ORDER BY km.tenKhuyenMai ASC"); break;
				case "Tên Z-A": cypher.append("ORDER BY km.tenKhuyenMai DESC"); break;
				case "Giá trị cao-thấp": cypher.append("ORDER BY km.giaTriGiam DESC"); break;
				case "Giá trị thấp-cao": cypher.append("ORDER BY km.giaTriGiam ASC"); break;
			}
		}

		try (Session session = DBConnect.getSession()) {
			Result result = session.run(cypher.toString());
			while (result.hasNext()) list.add(mapKhuyenMai(result.next()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<dto.ThongKeKhuyenMaiDTO> getThongKeChiTietKhuyenMai(java.util.Date tuNgay, java.util.Date denNgay) {
		List<dto.ThongKeKhuyenMaiDTO> list = new ArrayList<>();
		if (tuNgay == null || denNgay == null) return list;

		String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
				"WHERE hd.maKhuyenMai IS NOT NULL " +
				"AND datetime(hd.ngayLapHoaDon + '+07:00').epochMillis >= $tu " +
				"AND datetime(hd.ngayLapHoaDon + '+07:00').epochMillis <= $den " +
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
				list.add(new dto.ThongKeKhuyenMaiDTO(
						r.get("ma").asString(),
						r.get("ten").asString(),
						r.get("SoLuot").asInt(),
						BigDecimal.valueOf(r.get("TongTien").asDouble()),
						new Date(r.get("batDau").asLong()),
						new Date(r.get("ketThuc").asLong())
				));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
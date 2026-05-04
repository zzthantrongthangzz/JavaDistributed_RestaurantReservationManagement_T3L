package dao_impl;

import connect.DBConnect;
import entity.HoaDon;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class HoaDon_DAO {

    private final String RETURN_FIELDS = "hd.maHoaDon AS maHoaDon, hd.trangThai AS trangThai, " +
            "hd.ngayLapHoaDon AS ngayLapHoaDon, hd.thue AS thue, hd.maNhanVien AS maNhanVien, " +
            "hd.maPhieuDatBan AS maPhieuDatBan, hd.maKhachHang AS maKhachHang, hd.maKhuyenMai AS maKhuyenMai, " +
            "hd.diaChi AS diaChi, hd.tienDatCoc AS tienDatCoc, hd.soTienKhachTra AS soTienKhachTra, hd.soTienThoi AS soTienThoi";

    private HoaDon mapHoaDon(Record r) {
        Timestamp ngayLap = null;
        if (!r.get("ngayLapHoaDon").isNull()) {
            Object dateObj = r.get("ngayLapHoaDon").asObject();
            if (dateObj instanceof LocalDateTime) {
                ngayLap = Timestamp.valueOf((LocalDateTime) dateObj);
            } else if (dateObj instanceof java.time.ZonedDateTime) {
                ngayLap = Timestamp.from(((java.time.ZonedDateTime) dateObj).toInstant());
            } else if (dateObj instanceof Number) {
                ngayLap = new Timestamp(((Number) dateObj).longValue());
            } else if (dateObj instanceof String) {
                try { ngayLap = Timestamp.valueOf((String) dateObj); } catch(Exception ignored) {}
            }
        }

        return new HoaDon(
                r.get("maHoaDon").isNull() ? "" : r.get("maHoaDon").asString(),
                r.get("trangThai").isNull() ? "" : r.get("trangThai").asString(),
                ngayLap,
                r.get("thue").isNull() ? BigDecimal.ZERO : BigDecimal.valueOf(r.get("thue").asDouble()),
                r.get("maNhanVien").isNull() ? null : r.get("maNhanVien").asString(),
                r.get("maPhieuDatBan").isNull() ? null : r.get("maPhieuDatBan").asString(),
                r.get("maKhachHang").isNull() ? null : r.get("maKhachHang").asString(),
                r.get("maKhuyenMai").isNull() ? null : r.get("maKhuyenMai").asString(),
                r.get("diaChi").isNull() ? null : r.get("diaChi").asString(),
                r.get("tienDatCoc").isNull() ? BigDecimal.ZERO : BigDecimal.valueOf(r.get("tienDatCoc").asDouble()),
                r.get("soTienKhachTra").isNull() ? BigDecimal.ZERO : BigDecimal.valueOf(r.get("soTienKhachTra").asDouble()),
                r.get("soTienThoi").isNull() ? BigDecimal.ZERO : BigDecimal.valueOf(r.get("soTienThoi").asDouble())
        );
    }

    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }

    public synchronized boolean themHoaDon(HoaDon hd) {
        // Tự động tạo node HoaDon và vẽ luôn 2 relationship tới Khách Hàng và Nhân Viên
        String cypher = "CREATE (hd:HoaDon {maHoaDon: trim($maHD), trangThai: trim($trangThai), ngayLapHoaDon: $ngayLap, " +
                "thue: $thue, maNhanVien: trim($maNV), maPhieuDatBan: trim($maPhieu), maKhachHang: trim($maKH), " +
                "maKhuyenMai: trim($maKM), diaChi: trim($diaChi), tienDatCoc: $coc, soTienKhachTra: $tra, soTienThoi: $thoi}) " +
                "WITH hd " +
                "OPTIONAL MATCH (nv:NhanVien) WHERE trim(nv.maNhanVien) = trim($maNV) " +
                "FOREACH (ignore IN CASE WHEN nv IS NOT NULL THEN [1] ELSE [] END | MERGE (hd)-[:LAP_BOI]->(nv)) " +
                "WITH hd " +
                "OPTIONAL MATCH (kh:KhachHang) WHERE trim(kh.maKhachHang) = trim($maKH) " +
                "FOREACH (ignore IN CASE WHEN kh IS NOT NULL THEN [1] ELSE [] END | MERGE (hd)-[:CUA_KHACH]->(kh))";
        try (Session session = DBConnect.getSession()) {
            LocalDateTime ngayLapLDT = hd.getNgayLapHoaDon() != null ? new java.sql.Timestamp(hd.getNgayLapHoaDon().getTime()).toLocalDateTime() : LocalDateTime.now();
            session.run(cypher, Values.parameters(
                    "maHD", hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "",
                    "trangThai", hd.getTrangThai() != null ? hd.getTrangThai() : "",
                    "ngayLap", ngayLapLDT,
                    "thue", hd.getThue().doubleValue(),
                    "maNV", hd.getMaNhanVien() != null ? hd.getMaNhanVien() : "",
                    "maPhieu", hd.getMaPhieuDatBan() != null ? hd.getMaPhieuDatBan() : "",
                    "maKH", hd.getMaKhachHang() != null ? hd.getMaKhachHang() : "",
                    "maKM", hd.getMaKhuyenMai() != null ? hd.getMaKhuyenMai() : "",
                    "diaChi", hd.getDiaChi() != null ? hd.getDiaChi() : "",
                    "coc", hd.getTienDatCoc() != null ? hd.getTienDatCoc().doubleValue() : 0.0,
                    "tra", hd.getSoTienKhachTra() != null ? hd.getSoTienKhachTra().doubleValue() : 0.0,
                    "thoi", hd.getSoTienThoi() != null ? hd.getSoTienThoi().doubleValue() : 0.0
            ));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public synchronized boolean capNhatHoaDon(HoaDon hd) {
        String cypher = "MATCH (hd:HoaDon) WHERE trim(hd.maHoaDon) = trim($maHD) " +
                "SET hd.trangThai = trim($trangThai), hd.ngayLapHoaDon = $ngayLap, hd.thue = $thue, " +
                "hd.maNhanVien = trim($maNV), hd.maPhieuDatBan = trim($maPhieu), hd.maKhachHang = trim($maKH), " +
                "hd.maKhuyenMai = trim($maKM), hd.diaChi = trim($diaChi), hd.tienDatCoc = $coc, " +
                "hd.soTienKhachTra = $tra, hd.soTienThoi = $thoi";
        try (Session session = DBConnect.getSession()) {
            LocalDateTime ngayLapLDT = hd.getNgayLapHoaDon() != null ? new java.sql.Timestamp(hd.getNgayLapHoaDon().getTime()).toLocalDateTime() : LocalDateTime.now();
            session.run(cypher, Values.parameters(
                    "maHD", hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "",
                    "trangThai", hd.getTrangThai() != null ? hd.getTrangThai() : "",
                    "ngayLap", ngayLapLDT,
                    "thue", hd.getThue().doubleValue(),
                    "maNV", hd.getMaNhanVien() != null ? hd.getMaNhanVien() : "",
                    "maPhieu", hd.getMaPhieuDatBan() != null ? hd.getMaPhieuDatBan() : "",
                    "maKH", hd.getMaKhachHang() != null ? hd.getMaKhachHang() : "",
                    "maKM", hd.getMaKhuyenMai() != null ? hd.getMaKhuyenMai() : "",
                    "diaChi", hd.getDiaChi() != null ? hd.getDiaChi() : "",
                    "coc", hd.getTienDatCoc() != null ? hd.getTienDatCoc().doubleValue() : 0.0,
                    "tra", hd.getSoTienKhachTra() != null ? hd.getSoTienKhachTra().doubleValue() : 0.0,
                    "thoi", hd.getSoTienThoi() != null ? hd.getSoTienThoi().doubleValue() : 0.0
            ));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean xoaHoaDon(String maHoaDon) {
        String cypher = "MATCH (hd:HoaDon) WHERE trim(hd.maHoaDon) = trim($maHD) DETACH DELETE hd";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public HoaDon timTheoMa(String maHoaDon) {
        String cypher = "MATCH (hd:HoaDon) WHERE trim(hd.maHoaDon) = trim($maHD) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maHD", maHoaDon));
            if (result.hasNext()) {
                return mapHoaDon(result.next());
            }
        }
        return null;
    }

    public List<HoaDon> timTheoNgay(Date tuNgay, Date denNgay) {
        List<HoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon) " +
                "WHERE (hd.ngayLapHoaDon >= localdatetime({epochMillis: $tu}) AND hd.ngayLapHoaDon <= localdatetime({epochMillis: $den})) " +
                "OR (hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den) " +
                "RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Calendar c = Calendar.getInstance(); c.setTime(denNgay);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);

            Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", c.getTimeInMillis()));
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }

    // FIX LỖI KHÔNG TÌM THẤY HÓA ĐƠN
    public HoaDon timHoaDonChuaThanhToanTheoMaBan(String maBan) {
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Chưa thanh toán'})-[:SU_DUNG_BAN]->(b:BanAn) " +
                "WHERE trim(b.maBan) = trim($maBan) " +
                "RETURN " + RETURN_FIELDS + " LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maBan", maBan));
            if (result.hasNext()) {
                return mapHoaDon(result.next());
            }
        }
        return null;
    }

    public synchronized String sinhMaHoaDonTuDong() {
        try (Session session = DBConnect.getSession()) {
            Result result = session.run("MATCH (hd:HoaDon) RETURN hd.maHoaDon AS maxMa ORDER BY hd.maHoaDon DESC LIMIT 1");
            if (result.hasNext()) {
                String maxMa = result.next().get("maxMa").asString();
                if (maxMa != null && maxMa.startsWith("HD")) {
                    int soHienTai = Integer.parseInt(maxMa.substring(2)) + 1;
                    return String.format("HD%06d", soHienTai);
                }
            }
        }
        return "HD000001";
    }

    public List<HoaDon> locTheoTrangThai(String trangThai) {
        List<HoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon) WHERE trim(hd.trangThai) = trim($trangThai) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("trangThai", trangThai));
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }

    public List<HoaDon> timKiemChung(String tuKhoa) {
        List<HoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon) " +
                "WHERE hd.maHoaDon CONTAINS $kw OR hd.maKhachHang CONTAINS $kw OR hd.maNhanVien CONTAINS $kw " +
                "RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("kw", tuKhoa));
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }

    public List<HoaDon> sapXep(String orderBy) {
        List<HoaDon> list = new ArrayList<>();
        String safeOrder = orderBy.replaceAll("[^a-zA-Z0-9 ]", "");
        String cypher = "MATCH (hd:HoaDon) RETURN " + RETURN_FIELDS + " ORDER BY hd." + safeOrder;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }

    public List<HoaDon> timKiemNangCao(String maHD, String maKH, String maNV, Date tuNgay, Date denNgay) {
        List<HoaDon> list = new ArrayList<>();
        StringBuilder cypher = new StringBuilder("MATCH (hd:HoaDon) WHERE true ");
        Map<String, Object> params = new HashMap<>();

        if (maHD != null && !maHD.isEmpty()) {
            cypher.append(" AND hd.maHoaDon CONTAINS $maHD ");
            params.put("maHD", maHD);
        }
        if (maKH != null && !maKH.isEmpty()) {
            cypher.append(" AND hd.maKhachHang CONTAINS $maKH ");
            params.put("maKH", maKH);
        }
        if (maNV != null && !maNV.isEmpty()) {
            cypher.append(" AND hd.maNhanVien CONTAINS $maNV ");
            params.put("maNV", maNV);
        }
        if (tuNgay != null && denNgay != null) {
            cypher.append(" AND ((hd.ngayLapHoaDon >= localdatetime({epochMillis: $tu}) AND hd.ngayLapHoaDon <= localdatetime({epochMillis: $den})) ");
            cypher.append(" OR (hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den)) ");
            Calendar c = Calendar.getInstance(); c.setTime(denNgay);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
            params.put("tu", tuNgay.getTime());
            params.put("den", c.getTimeInMillis());
        }

        cypher.append(" RETURN ").append(RETURN_FIELDS);

        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher.toString(), params);
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }

    public BigDecimal getTongDoanhThu(Date tuNgay, Date denNgay) {
        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'})-[ct:GOM_MON]->(m:Mon) " +
                "WHERE (hd.ngayLapHoaDon >= localdatetime({epochMillis: $tu}) AND hd.ngayLapHoaDon <= localdatetime({epochMillis: $den})) " +
                "OR (hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den) " +
                "RETURN sum(ct.soLuong * ct.donGia) AS tong";
        try (Session session = DBConnect.getSession()) {
            Calendar c = Calendar.getInstance(); c.setTime(denNgay);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);

            Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", c.getTimeInMillis()));
            if (result.hasNext()) {
                tongDoanhThu = BigDecimal.valueOf(result.next().get("tong").asDouble());
            }
        }
        return tongDoanhThu;
    }

    public int getTongSoHoaDon(Date tuNgay, Date denNgay) {
        int tongSoHoaDon = 0;
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
                "WHERE (hd.ngayLapHoaDon >= localdatetime({epochMillis: $tu}) AND hd.ngayLapHoaDon <= localdatetime({epochMillis: $den})) " +
                "OR (hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den) " +
                "RETURN count(hd) AS tong";
        try (Session session = DBConnect.getSession()) {
            Calendar c = Calendar.getInstance(); c.setTime(denNgay);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);

            Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", c.getTimeInMillis()));
            if (result.hasNext()) {
                tongSoHoaDon = result.next().get("tong").asInt();
            }
        }
        return tongSoHoaDon;
    }

    public Map<Date, BigDecimal> getDoanhThuTheoNgay(Date tuNgay, Date denNgay) {
        Map<Date, BigDecimal> doanhThuTheoNgay = new TreeMap<>();
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'})-[ct:GOM_MON]->(m:Mon) " +
                "WHERE (hd.ngayLapHoaDon >= localdatetime({epochMillis: $tu}) AND hd.ngayLapHoaDon <= localdatetime({epochMillis: $den})) " +
                "OR (hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den) " +
                "RETURN date(hd.ngayLapHoaDon) AS ngay, sum(ct.soLuong * ct.donGia) AS doanhThu " +
                "ORDER BY ngay";
        try (Session session = DBConnect.getSession()) {
            Calendar c = Calendar.getInstance(); c.setTime(denNgay);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);

            Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", c.getTimeInMillis()));
            while (result.hasNext()) {
                Record r = result.next();
                if (!r.get("ngay").isNull()) {
                    java.time.LocalDate localDate = r.get("ngay").asLocalDate();
                    Date ngay = java.sql.Date.valueOf(localDate);
                    BigDecimal doanhThu = BigDecimal.valueOf(r.get("doanhThu").asDouble());
                    doanhThuTheoNgay.put(ngay, doanhThu);
                }
            }
        }
        return doanhThuTheoNgay;
    }

    // FIX LỖI "KHÁCH LẺ" KHI ĐẶT BÀN NGAY
    public String getTenKhachHangTheoBan(String maBan) {
        String tenKhach = null;
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Chưa thanh toán'})-[:SU_DUNG_BAN]->(b:BanAn) " +
                "WHERE trim(b.maBan) = trim($maBan) " +
                "OPTIONAL MATCH (hd)-[:CUA_KHACH]->(kh1:KhachHang) " +
                "OPTIONAL MATCH (kh2:KhachHang) WHERE trim(kh2.maKhachHang) = trim(hd.maKhachHang) " +
                "RETURN coalesce(kh1.hoTen, kh2.hoTen) AS hoTen LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maBan", maBan));
            if (result.hasNext()) {
                Record r = result.next();
                if (!r.get("hoTen").isNull()) {
                    tenKhach = r.get("hoTen").asString();
                }
            }
        }
        return tenKhach;
    }

    public BigDecimal tinhTongTienCuaHoaDon(String maHoaDon) {
        BigDecimal tongTien = BigDecimal.ZERO;
        String cypher = "MATCH (hd:HoaDon)-[ct:GOM_MON]->(m:Mon) WHERE trim(hd.maHoaDon) = trim($maHD) RETURN sum(ct.soLuong * ct.donGia) AS tong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maHD", maHoaDon));
            if (result.hasNext()) {
                tongTien = BigDecimal.valueOf(result.next().get("tong").asDouble());
            }
        }
        return tongTien;
    }

    public List<HoaDon> getDanhSachHoaDon(Date tuNgay, Date denNgay) {
        List<HoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
                "WHERE (hd.ngayLapHoaDon >= localdatetime({epochMillis: $tu}) AND hd.ngayLapHoaDon <= localdatetime({epochMillis: $den})) " +
                "OR (hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den) " +
                "RETURN " + RETURN_FIELDS + " ORDER BY hd.ngayLapHoaDon DESC";
        try (Session session = DBConnect.getSession()) {
            long epochTu = 0;
            long epochDen = System.currentTimeMillis();

            if (tuNgay != null && denNgay != null) {
                epochTu = tuNgay.getTime();
                Calendar c = Calendar.getInstance(); c.setTime(denNgay);
                c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
                epochDen = c.getTimeInMillis();
            }

            Result result = session.run(cypher, Values.parameters("tu", epochTu, "den", epochDen));
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }
}
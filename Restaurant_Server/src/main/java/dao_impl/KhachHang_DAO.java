package dao_impl;

import connect.DBConnect;
import entity.KhachHang;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.math.BigDecimal;
import java.util.*;

public class KhachHang_DAO {

    private final String RETURN_FIELDS = "kh.maKhachHang AS maKhachHang, kh.hoTen AS hoTen, kh.soDienThoai AS soDienThoai, " +
            "kh.email AS email, kh.diaChi AS diaChi, kh.ngaySinh AS ngaySinh, " +
            "kh.gioiTinh AS gioiTinh, kh.tichDiem AS tichDiem, kh.trangThai AS trangThai";

    private KhachHang mapKhachHang(Record r) {
        String maKH = r.get("maKhachHang").isNull() ? "" : String.valueOf(r.get("maKhachHang").asObject());
        String hoTen = r.get("hoTen").isNull() ? "" : String.valueOf(r.get("hoTen").asObject());
        String sdt = r.get("soDienThoai").isNull() ? "" : String.valueOf(r.get("soDienThoai").asObject());
        String email = r.get("email").isNull() ? "" : String.valueOf(r.get("email").asObject());
        String diaChi = r.get("diaChi").isNull() ? "" : String.valueOf(r.get("diaChi").asObject());

        boolean gioiTinh = true;
        if (!r.get("gioiTinh").isNull()) {
            Object gtObj = r.get("gioiTinh").asObject();
            if (gtObj instanceof Boolean) {
                gioiTinh = (Boolean) gtObj;
            } else if (gtObj instanceof String) {
                String gtStr = (String) gtObj;
                gioiTinh = gtStr.equalsIgnoreCase("Nam") || gtStr.equalsIgnoreCase("true") || gtStr.equals("1");
            } else if (gtObj instanceof Number) {
                gioiTinh = ((Number) gtObj).intValue() == 1;
            }
        }

        java.sql.Date ngaySinh = null;
        if (!r.get("ngaySinh").isNull()) {
            Object nsObj = r.get("ngaySinh").asObject();
            try {
                if (nsObj instanceof String) {
                    ngaySinh = java.sql.Date.valueOf((String) nsObj);
                } else if (nsObj instanceof Number) {
                    ngaySinh = new java.sql.Date(((Number) nsObj).longValue());
                } else if (nsObj instanceof java.time.LocalDate) {
                    ngaySinh = java.sql.Date.valueOf((java.time.LocalDate) nsObj);
                }
            } catch (Exception ignored) {}
        }

        int tichDiem = 0;
        if (!r.get("tichDiem").isNull()) {
            tichDiem = r.get("tichDiem").asInt(0);
        }

        boolean trangThai = true;
        if (!r.get("trangThai").isNull()) {
            Object ttObj = r.get("trangThai").asObject();
            if (ttObj instanceof Boolean) {
                trangThai = (Boolean) ttObj;
            } else if (ttObj instanceof Number) {
                trangThai = ((Number) ttObj).intValue() == 1;
            }
        }

        return new KhachHang(maKH, hoTen, sdt, email, diaChi, ngaySinh, gioiTinh, tichDiem, trangThai);
    }

    public synchronized String phatSinhMaKhachHang() {
        try (Session session = DBConnect.getSession()) {
            Result result = session.run("MATCH (kh:KhachHang) RETURN kh.maKhachHang AS maxMa ORDER BY kh.maKhachHang DESC LIMIT 1");
            if (result.hasNext()) {
                String maxMa = result.next().get("maxMa").asString();
                if (maxMa != null && maxMa.length() > 2) {
                    int soMoi = Integer.parseInt(maxMa.substring(2)) + 1;
                    return String.format("KH%06d", soMoi);
                }
            }
        }
        return "KH000001";
    }

    public List<KhachHang> docDanhSachKhachHang() {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                ds.add(mapKhachHang(result.next()));
            }
        }
        return ds;
    }

    public List<KhachHang> getKhachHangDaXoa() {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 0}) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                ds.add(mapKhachHang(result.next()));
            }
        }
        return ds;
    }

    public List<KhachHang> docDanhSachKhachHangDaXoa() {
        return getKhachHangDaXoa();
    }

    public synchronized boolean themKhachHang(KhachHang kh) {
        String cypher = "CREATE (kh:KhachHang {maKhachHang: $ma, hoTen: $ten, soDienThoai: $sdt, " +
                "email: $email, diaChi: $diaChi, ngaySinh: $ngaySinh, gioiTinh: $gioiTinh, tichDiem: $tichDiem, trangThai: 1})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "ma", kh.getMaKhachHang(), "ten", kh.getHoTen(), "sdt", kh.getSoDienThoai(),
                    "email", kh.getEmail(), "diaChi", kh.getDiaChi(),
                    "ngaySinh", kh.getNgaySinh() != null ? kh.getNgaySinh().getTime() : null,
                    "gioiTinh", kh.isGioiTinh(), "tichDiem", kh.getTichDiem()
            ));
            return true;
        } catch (Exception e) { return false; }
    }

    public synchronized boolean capNhatKhachHang(KhachHang kh) {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) " +
                "SET kh.hoTen = $ten, kh.soDienThoai = $sdt, kh.email = $email, kh.diaChi = $diaChi, " +
                "kh.ngaySinh = $ngaySinh, kh.gioiTinh = $gioiTinh, kh.tichDiem = $tichDiem";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "ma", kh.getMaKhachHang(), "ten", kh.getHoTen(), "sdt", kh.getSoDienThoai(),
                    "email", kh.getEmail(), "diaChi", kh.getDiaChi(),
                    "ngaySinh", kh.getNgaySinh() != null ? kh.getNgaySinh().getTime() : null,
                    "gioiTinh", kh.isGioiTinh(), "tichDiem", kh.getTichDiem()
            ));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean xoaKhachHang(String ma) {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) SET kh.trangThai = 0";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", ma));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean khoiPhucKhachHang(String ma) {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) SET kh.trangThai = 1";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", ma));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<KhachHang> timKiemTheoMa(String ma) {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) WHERE toUpper(kh.maKhachHang) = toUpper($ma) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ma", ma));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    public List<KhachHang> timKiemTheoTen(String ten) {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) WHERE toUpper(kh.hoTen) CONTAINS toUpper($ten) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ten", ten));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    public List<KhachHang> timKiemTheoSDT(String sdt) {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) WHERE kh.soDienThoai CONTAINS $sdt RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("sdt", sdt));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    public KhachHang timKhachHangTheoSDT(String sdt) {
        String cypher = "MATCH (kh:KhachHang {soDienThoai: $sdt, trangThai: 1}) RETURN " + RETURN_FIELDS + " LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("sdt", sdt));
            if (result.hasNext()) return mapKhachHang(result.next());
        }
        return null;
    }

    public List<KhachHang> locKhachHangTheoGioiTinh(boolean gt) {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {gioiTinh: $gt, trangThai: 1}) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("gt", gt));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    public List<KhachHang> sapXepTheoTen(boolean tangDan) {
        List<KhachHang> ds = new ArrayList<>();
        String order = tangDan ? "ASC" : "DESC";
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN " + RETURN_FIELDS + " ORDER BY kh.hoTen " + order;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    public List<KhachHang> sapXepTheoDiem(boolean tangDan) {
        List<KhachHang> ds = new ArrayList<>();
        String order = tangDan ? "ASC" : "DESC";
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN " + RETURN_FIELDS + " ORDER BY kh.tichDiem " + order;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    public synchronized boolean capNhatDiemTichLuy(String ma, int diem) {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) SET kh.tichDiem = kh.tichDiem + $diem";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", ma, "diem", diem));
            return true;
        } catch (Exception e) { return false; }
    }

    public int getTongSoKhachHang() {
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN count(kh) AS tong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            if (result.hasNext()) return result.next().get("tong").asInt();
        }
        return 0;
    }

    public int getTongSoKhachHang(Date tu, Date den) {
        if (tu == null || den == null) return getTongSoKhachHang();

        String cypher = "MATCH (hd:HoaDon) WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den " +
                "WITH DISTINCT hd.maKhachHang AS maKH " +
                "MATCH (kh:KhachHang {maKhachHang: maKH, trangThai: 1}) " +
                "RETURN count(kh) AS tong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tu", tu.getTime(), "den", den.getTime()));
            if (result.hasNext()) return result.next().get("tong").asInt();
        }
        return 0;
    }

    public int getTongDiemTichLuy(Date tu, Date den) {
        String cypher;
        if (tu == null || den == null) {
            cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN sum(kh.tichDiem) AS tong";
            try (Session session = DBConnect.getSession()) {
                Result result = session.run(cypher);
                if (result.hasNext()) return result.next().get("tong").asInt();
            }
        } else {
            cypher = "MATCH (hd:HoaDon) WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den " +
                    "WITH DISTINCT hd.maKhachHang AS maKH " +
                    "MATCH (kh:KhachHang {maKhachHang: maKH, trangThai: 1}) " +
                    "RETURN sum(kh.tichDiem) AS tong";
            try (Session session = DBConnect.getSession()) {
                Result result = session.run(cypher, Values.parameters("tu", tu.getTime(), "den", den.getTime()));
                if (result.hasNext()) return result.next().get("tong").asInt();
            }
        }
        return 0;
    }

    public Map<String, Integer> getSoLuongKhachHangTheoGioiTinh() {
        Map<String, Integer> data = new HashMap<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) " +
                "RETURN CASE WHEN kh.gioiTinh = true THEN 'Nam' ELSE 'Nữ' END AS GioiTinh, count(kh) AS SoLuong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                Record r = result.next();
                data.put(r.get("GioiTinh").asString(), r.get("SoLuong").asInt());
            }
        }
        return data;
    }

    public BigDecimal getTongChiTieuTatCaKhachHang(Date tu, Date den) {
        BigDecimal tong = BigDecimal.ZERO;
        StringBuilder cypher = new StringBuilder(
                "MATCH (kh:KhachHang {trangThai: 1}) " +
                        "MATCH (hd:HoaDon {maKhachHang: kh.maKhachHang, trangThai: 'Đã thanh toán'}) " +
                        "MATCH (ct:ChiTietHoaDon {maHoaDon: hd.maHoaDon}) "
        );
        if (tu != null && den != null) {
            cypher.append("WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den ");
        }
        cypher.append("RETURN sum(ct.soLuong * ct.donGia) AS tongChiTieu");

        try (Session session = DBConnect.getSession()) {
            Result result;
            if (tu != null && den != null) {
                result = session.run(cypher.toString(), Values.parameters("tu", tu.getTime(), "den", den.getTime()));
            } else {
                result = session.run(cypher.toString());
            }
            if (result.hasNext()) {
                tong = BigDecimal.valueOf(result.next().get("tongChiTieu").asDouble());
            }
        }
        return tong;
    }

    public Map<String, BigDecimal> getTopKhachHangTheoChiTieu(int topN) {
        Map<String, BigDecimal> data = new LinkedHashMap<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) " +
                "MATCH (hd:HoaDon {maKhachHang: kh.maKhachHang, trangThai: 'Đã thanh toán'}) " +
                "MATCH (ct:ChiTietHoaDon {maHoaDon: hd.maHoaDon}) " +
                "RETURN kh.hoTen AS hoTen, sum(ct.soLuong * ct.donGia) AS TongChiTieu " +
                "ORDER BY TongChiTieu DESC LIMIT $top";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("top", topN));
            while (result.hasNext()) {
                Record r = result.next();
                data.put(r.get("hoTen").asString(), BigDecimal.valueOf(r.get("TongChiTieu").asDouble()));
            }
        }
        return data;
    }

    public List<dto.ThongKeKhachHangDTO> getTopKhachHangDayDu(int topN, Date tu, Date den) {
        List<dto.ThongKeKhachHangDTO> list = new ArrayList<>();
        StringBuilder cypher = new StringBuilder(
                "MATCH (kh:KhachHang {trangThai: 1}) " +
                        "MATCH (hd:HoaDon {maKhachHang: kh.maKhachHang, trangThai: 'Đã thanh toán'}) " +
                        "MATCH (ct:ChiTietHoaDon {maHoaDon: hd.maHoaDon}) "
        );
        if (tu != null && den != null) {
            cypher.append("WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den ");
        }
        cypher.append("RETURN kh.hoTen AS hoTen, sum(ct.soLuong * ct.donGia) AS TongChiTieu, kh.tichDiem AS tichDiem " +
                "ORDER BY TongChiTieu DESC LIMIT $top");

        try (Session session = DBConnect.getSession()) {
            Result result;
            if (tu != null && den != null) {
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.setTime(den); c.set(java.util.Calendar.HOUR_OF_DAY, 23); c.set(java.util.Calendar.MINUTE, 59); c.set(java.util.Calendar.SECOND, 59);
                result = session.run(cypher.toString(), Values.parameters("tu", tu.getTime(), "den", c.getTimeInMillis(), "top", topN));
            } else {
                result = session.run(cypher.toString(), Values.parameters("top", topN));
            }
            while (result.hasNext()) {
                Record r = result.next();
                list.add(new dto.ThongKeKhachHangDTO(
                        r.get("hoTen").asString(),
                        BigDecimal.valueOf(r.get("TongChiTieu").asDouble()),
                        r.get("tichDiem").asInt()
                ));
            }
        }
        return list;
    }
}
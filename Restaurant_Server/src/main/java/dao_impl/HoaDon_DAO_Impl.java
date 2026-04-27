package dao_impl;

import connect.DBConnect;
import entity.HoaDon;
import rmi_interfaces.IHoaDon_DAO;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

public class HoaDon_DAO_Impl extends UnicastRemoteObject implements IHoaDon_DAO {

    public HoaDon_DAO_Impl() throws RemoteException {
        super();
    }

    // Chuỗi RETURN dùng chung để ánh xạ đối tượng
    private final String RETURN_FIELDS = "hd.maHoaDon AS maHoaDon, hd.trangThai AS trangThai, " +
            "hd.ngayLapHoaDon AS ngayLapHoaDon, hd.thue AS thue, hd.maNhanVien AS maNhanVien, " +
            "hd.maPhieuDatBan AS maPhieuDatBan, hd.maKhachHang AS maKhachHang, hd.maKhuyenMai AS maKhuyenMai, " +
            "hd.diaChi AS diaChi, hd.tienDatCoc AS tienDatCoc, hd.soTienKhachTra AS soTienKhachTra, hd.soTienThoi AS soTienThoi";

    // Ánh xạ Record của Neo4j sang đối tượng HoaDon
    private HoaDon mapHoaDon(Record r) {
        return new HoaDon(
                r.get("maHoaDon").asString(),
                r.get("trangThai").asString(),
                new Timestamp(r.get("ngayLapHoaDon").asLong()), // Lấy epoch time và chuyển lại thành Timestamp
                BigDecimal.valueOf(r.get("thue").asDouble()),
                r.get("maNhanVien").isNull() ? null : r.get("maNhanVien").asString(),
                r.get("maPhieuDatBan").isNull() ? null : r.get("maPhieuDatBan").asString(),
                r.get("maKhachHang").isNull() ? null : r.get("maKhachHang").asString(),
                r.get("maKhuyenMai").isNull() ? null : r.get("maKhuyenMai").asString(),
                r.get("diaChi").isNull() ? null : r.get("diaChi").asString(),
                BigDecimal.valueOf(r.get("tienDatCoc").asDouble()),
                BigDecimal.valueOf(r.get("soTienKhachTra").asDouble()),
                BigDecimal.valueOf(r.get("soTienThoi").asDouble())
        );
    }

    @Override
    public List<HoaDon> getAllHoaDon() throws RemoteException {
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

    @Override
    public synchronized boolean themHoaDon(HoaDon hd) throws RemoteException {
        String cypher = "CREATE (hd:HoaDon {maHoaDon: $maHD, trangThai: $trangThai, ngayLapHoaDon: $ngayLap, " +
                "thue: $thue, maNhanVien: $maNV, maPhieuDatBan: $maPhieu, maKhachHang: $maKH, " +
                "maKhuyenMai: $maKM, diaChi: $diaChi, tienDatCoc: $coc, soTienKhachTra: $tra, soTienThoi: $thoi})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maHD", hd.getMaHoaDon(), "trangThai", hd.getTrangThai(),
                    "ngayLap", hd.getNgayLapHoaDon().getTime(),
                    "thue", hd.getThue().doubleValue(),
                    "maNV", hd.getMaNhanVien(), "maPhieu", hd.getMaPhieuDatBan(), "maKH", hd.getMaKhachHang(),
                    "maKM", hd.getMaKhuyenMai(), "diaChi", hd.getDiaChi(),
                    "coc", hd.getTienDatCoc().doubleValue(), "tra", hd.getSoTienKhachTra().doubleValue(), "thoi", hd.getSoTienThoi().doubleValue()
            ));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public synchronized boolean capNhatHoaDon(HoaDon hd) throws RemoteException {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD}) " +
                "SET hd.trangThai = $trangThai, hd.ngayLapHoaDon = $ngayLap, hd.thue = $thue, " +
                "hd.maNhanVien = $maNV, hd.maPhieuDatBan = $maPhieu, hd.maKhachHang = $maKH, " +
                "hd.maKhuyenMai = $maKM, hd.diaChi = $diaChi, hd.tienDatCoc = $coc, " +
                "hd.soTienKhachTra = $tra, hd.soTienThoi = $thoi";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maHD", hd.getMaHoaDon(), "trangThai", hd.getTrangThai(), "ngayLap", hd.getNgayLapHoaDon().getTime(),
                    "thue", hd.getThue().doubleValue(), "maNV", hd.getMaNhanVien(), "maPhieu", hd.getMaPhieuDatBan(),
                    "maKH", hd.getMaKhachHang(), "maKM", hd.getMaKhuyenMai(), "diaChi", hd.getDiaChi(),
                    "coc", hd.getTienDatCoc().doubleValue(), "tra", hd.getSoTienKhachTra().doubleValue(), "thoi", hd.getSoTienThoi().doubleValue()
            ));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean xoaHoaDon(String maHoaDon) throws RemoteException {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD}) DETACH DELETE hd";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public HoaDon timTheoMa(String maHoaDon) throws RemoteException {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD}) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maHD", maHoaDon));
            if (result.hasNext()) {
                return mapHoaDon(result.next());
            }
        }
        return null;
    }

    @Override
    public List<HoaDon> timTheoNgay(Date tuNgay, Date denNgay) throws RemoteException {
        List<HoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon) WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            // Đẩy ngày đến về 23:59:59 để bao quát trọn ngày
            Calendar c = Calendar.getInstance(); c.setTime(denNgay);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);

            Result result = session.run(cypher, Values.parameters(
                    "tu", tuNgay.getTime(),
                    "den", c.getTimeInMillis()
            ));
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }

    @Override
    public HoaDon timHoaDonChuaThanhToanTheoMaBan(String maBan) throws RemoteException {
        // Tuân theo mô hình SQL (JOIN HoaDon_Ban), ánh xạ thành 2 Node kết nối gián tiếp theo maHoaDon
        String cypher = "MATCH (hd:HoaDon), (hdb:HoaDon_Ban {maBan: $maBan}) " +
                "WHERE hd.maHoaDon = hdb.maHoaDon AND hd.trangThai = 'Chưa thanh toán' " +
                "RETURN " + RETURN_FIELDS + " LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maBan", maBan));
            if (result.hasNext()) {
                return mapHoaDon(result.next());
            }
        }
        return null;
    }

    @Override
    public synchronized String sinhMaHoaDonTuDong() throws RemoteException {
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

    @Override
    public List<HoaDon> locTheoTrangThai(String trangThai) throws RemoteException {
        List<HoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon {trangThai: $trangThai}) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("trangThai", trangThai));
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }

    @Override
    public List<HoaDon> timKiemChung(String tuKhoa) throws RemoteException {
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

    @Override
    public List<HoaDon> sapXep(String orderBy) throws RemoteException {
        List<HoaDon> list = new ArrayList<>();
        // Ngăn chặn SQL/Cypher Injection cơ bản
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

    @Override
    public List<HoaDon> timKiemNangCao(String maHD, String maKH, String maNV, Date tuNgay, Date denNgay) throws RemoteException {
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
        if (tuNgay != null) {
            cypher.append(" AND hd.ngayLapHoaDon >= $tu ");
            params.put("tu", tuNgay.getTime());
        }
        if (denNgay != null) {
            cypher.append(" AND hd.ngayLapHoaDon <= $den ");
            Calendar c = Calendar.getInstance(); c.setTime(denNgay);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
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

    @Override
    public BigDecimal getTongDoanhThu(Date tuNgay, Date denNgay) throws RemoteException {
        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
                "MATCH (ct:ChiTietHoaDon {maHoaDon: hd.maHoaDon}) " +
                "WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den " +
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

    @Override
    public int getTongSoHoaDon(Date tuNgay, Date denNgay) throws RemoteException {
        int tongSoHoaDon = 0;
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
                "WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den " +
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

    @Override
    public Map<Date, BigDecimal> getDoanhThuTheoNgay(Date tuNgay, Date denNgay) throws RemoteException {
        Map<Date, BigDecimal> doanhThuTheoNgay = new TreeMap<>();
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
                "MATCH (ct:ChiTietHoaDon {maHoaDon: hd.maHoaDon}) " +
                "WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den " +
                "RETURN date(datetime({epochMillis: hd.ngayLapHoaDon})) AS ngay, sum(ct.soLuong * ct.donGia) AS doanhThu " +
                "ORDER BY ngay";
        try (Session session = DBConnect.getSession()) {
            Calendar c = Calendar.getInstance(); c.setTime(denNgay);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);

            Result result = session.run(cypher, Values.parameters("tu", tuNgay.getTime(), "den", c.getTimeInMillis()));
            while (result.hasNext()) {
                Record r = result.next();
                // Neo4j date to java.util.Date
                LocalDate localDate = r.get("ngay").asLocalDate();
                Date ngay = java.sql.Date.valueOf(localDate);
                BigDecimal doanhThu = BigDecimal.valueOf(r.get("doanhThu").asDouble());
                doanhThuTheoNgay.put(ngay, doanhThu);
            }
        }
        return doanhThuTheoNgay;
    }

    @Override
    public String getTenKhachHangTheoBan(String maBan) throws RemoteException {
        String tenKhach = null;
        String cypher = "MATCH (hd:HoaDon), (hdb:HoaDon_Ban {maBan: $maBan}) " +
                "WHERE hd.maHoaDon = hdb.maHoaDon AND hd.trangThai CONTAINS 'Chưa thanh toán' " +
                "OPTIONAL MATCH (kh:KhachHang {maKhachHang: hd.maKhachHang}) " +
                "RETURN kh.hoTen AS hoTen LIMIT 1";
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

    @Override
    public BigDecimal tinhTongTienCuaHoaDon(String maHoaDon) throws RemoteException {
        BigDecimal tongTien = BigDecimal.ZERO;
        String cypher = "MATCH (ct:ChiTietHoaDon {maHoaDon: $maHD}) RETURN sum(ct.soLuong * ct.donGia) AS tong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maHD", maHoaDon));
            if (result.hasNext()) {
                tongTien = BigDecimal.valueOf(result.next().get("tong").asDouble());
            }
        }
        return tongTien;
    }

    @Override
    public List<HoaDon> getDanhSachHoaDon(Date tuNgay, Date denNgay) throws RemoteException {
        List<HoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) " +
                "WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den " +
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
package dao_impl;

import connect.DBConnect;
import entity.KhachHang;
import rmi_interfaces.IKhachHang_DAO;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class KhachHang_DAO_Impl extends UnicastRemoteObject implements IKhachHang_DAO {

    public KhachHang_DAO_Impl() throws RemoteException {
        super();
    }

    // Cấu trúc chuỗi RETURN dùng chung để ánh xạ Record
    private final String RETURN_FIELDS = "kh.maKhachHang AS maKhachHang, kh.hoTen AS hoTen, kh.soDienThoai AS soDienThoai, " +
            "kh.email AS email, kh.diaChi AS diaChi, kh.ngaySinh AS ngaySinh, " +
            "kh.gioiTinh AS gioiTinh, kh.tichDiem AS tichDiem, kh.trangThai AS trangThai";

    // Ánh xạ Record của Neo4j sang đối tượng KhachHang
    private KhachHang mapKhachHang(Record r) {
        // 1. Xử lý các chuỗi String an toàn (chống null)
        String maKH = r.get("maKhachHang").isNull() ? "" : String.valueOf(r.get("maKhachHang").asObject());
        String hoTen = r.get("hoTen").isNull() ? "" : String.valueOf(r.get("hoTen").asObject());
        String sdt = r.get("soDienThoai").isNull() ? "" : String.valueOf(r.get("soDienThoai").asObject());
        String email = r.get("email").isNull() ? "" : String.valueOf(r.get("email").asObject());
        String diaChi = r.get("diaChi").isNull() ? "" : String.valueOf(r.get("diaChi").asObject());

        // 2. Xử lý Giới tính (Đọc được cả Boolean, String, Số)
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

        // 3. Xử lý Ngày Sinh (Đọc được LocalDate của Neo4j, String, Timestamp)
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
            } catch (Exception e) {
                System.err.println("Lỗi parse ngày sinh của Khách Hàng: " + maKH);
            }
        }

        // 4. Xử lý Điểm tích lũy (Ép kiểu an toàn từ Long/Int)
        int tichDiem = 0;
        if (!r.get("tichDiem").isNull()) {
            tichDiem = r.get("tichDiem").asInt(0); // Trả về 0 nếu có lỗi
        }

        // 5. Xử lý Trạng thái (1: Hoạt động, 0: Đã xóa)
        boolean trangThai = true;
        if (!r.get("trangThai").isNull()) {
            Object ttObj = r.get("trangThai").asObject();
            if (ttObj instanceof Boolean) {
                trangThai = (Boolean) ttObj;
            } else if (ttObj instanceof Number) {
                trangThai = ((Number) ttObj).intValue() == 1;
            }
        }

        // Lưu ý: Đảm bảo thứ tự truyền vào Constructor khớp với Entity KhachHang của bạn
        // KhachHang(maKH, hoTen, sdt, email, diaChi, ngaySinh, gioiTinh, tichDiem, trangThai)
        return new KhachHang(maKH, hoTen, sdt, email, diaChi, ngaySinh, gioiTinh, tichDiem, trangThai);
    }

    @Override
    public synchronized String phatSinhMaKhachHang() throws RemoteException {
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

    @Override
    public List<KhachHang> docDanhSachKhachHang() throws RemoteException {
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

    // ĐÂY LÀ HÀM BỊ THIẾU GÂY RA LỖI ĐỎ Ở HÌNH ẢNH TRƯỚC
    @Override
    public List<KhachHang> getKhachHangDaXoa() throws RemoteException {
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

    // (Dự phòng nếu Interface yêu cầu tên này)
    @Override
    public List<KhachHang> docDanhSachKhachHangDaXoa() throws RemoteException {
        return getKhachHangDaXoa();
    }

    @Override
    public synchronized boolean themKhachHang(KhachHang kh) throws RemoteException {
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

    @Override
    public synchronized boolean capNhatKhachHang(KhachHang kh) throws RemoteException {
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

    @Override
    public boolean xoaKhachHang(String ma) throws RemoteException {
        // Cập nhật trạng thái = 0 (xóa mềm)
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) SET kh.trangThai = 0";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", ma));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public boolean khoiPhucKhachHang(String ma) throws RemoteException {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) SET kh.trangThai = 1";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", ma));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public List<KhachHang> timKiemTheoMa(String ma) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) WHERE toUpper(kh.maKhachHang) = toUpper($ma) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ma", ma));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    @Override
    public List<KhachHang> timKiemTheoTen(String ten) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) WHERE toUpper(kh.hoTen) CONTAINS toUpper($ten) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ten", ten));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    @Override
    public List<KhachHang> timKiemTheoSDT(String sdt) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) WHERE kh.soDienThoai CONTAINS $sdt RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("sdt", sdt));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    @Override
    public KhachHang timKhachHangTheoSDT(String sdt) throws RemoteException {
        String cypher = "MATCH (kh:KhachHang {soDienThoai: $sdt, trangThai: 1}) RETURN " + RETURN_FIELDS + " LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("sdt", sdt));
            if (result.hasNext()) return mapKhachHang(result.next());
        }
        return null;
    }

    @Override
    public List<KhachHang> locKhachHangTheoGioiTinh(boolean gt) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {gioiTinh: $gt, trangThai: 1}) RETURN " + RETURN_FIELDS;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("gt", gt));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    @Override
    public List<KhachHang> sapXepTheoTen(boolean tangDan) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String order = tangDan ? "ASC" : "DESC";
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN " + RETURN_FIELDS + " ORDER BY kh.hoTen " + order;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    @Override
    public List<KhachHang> sapXepTheoDiem(boolean tangDan) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String order = tangDan ? "ASC" : "DESC";
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN " + RETURN_FIELDS + " ORDER BY kh.tichDiem " + order;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        }
        return ds;
    }

    @Override
    public synchronized boolean capNhatDiemTichLuy(String ma, int diem) throws RemoteException {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) SET kh.tichDiem = kh.tichDiem + $diem";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", ma, "diem", diem));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public int getTongSoKhachHang() throws RemoteException {
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN count(kh) AS tong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            if (result.hasNext()) return result.next().get("tong").asInt();
        }
        return 0;
    }

    @Override
    public int getTongSoKhachHang(Date tu, Date den) throws RemoteException {
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

    @Override
    public int getTongDiemTichLuy(Date tu, Date den) throws RemoteException {
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

    @Override
    public Map<String, Integer> getSoLuongKhachHangTheoGioiTinh() throws RemoteException {
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

    @Override
    public BigDecimal getTongChiTieuTatCaKhachHang(Date tu, Date den) throws RemoteException {
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

    @Override
    public Map<String, BigDecimal> getTopKhachHangTheoChiTieu(int topN) throws RemoteException {
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

    @Override
    public List<Object[]> getTopKhachHangDayDu(int topN, Date tu, Date den) throws RemoteException {
        List<Object[]> list = new ArrayList<>();
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
                // Đưa den về mốc 23:59:59 giống code SQL cũ
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.setTime(den); c.set(java.util.Calendar.HOUR_OF_DAY, 23); c.set(java.util.Calendar.MINUTE, 59); c.set(java.util.Calendar.SECOND, 59);
                result = session.run(cypher.toString(), Values.parameters("tu", tu.getTime(), "den", c.getTimeInMillis(), "top", topN));
            } else {
                result = session.run(cypher.toString(), Values.parameters("top", topN));
            }
            while (result.hasNext()) {
                Record r = result.next();
                list.add(new Object[]{
                        r.get("hoTen").asString(),
                        BigDecimal.valueOf(r.get("TongChiTieu").asDouble()),
                        r.get("tichDiem").asInt()
                });
            }
        }
        return list;
    }
}
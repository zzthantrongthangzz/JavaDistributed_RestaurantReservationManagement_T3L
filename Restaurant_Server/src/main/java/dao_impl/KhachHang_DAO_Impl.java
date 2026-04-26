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
import java.sql.Date;
import java.util.*;

public class KhachHang_DAO_Impl extends UnicastRemoteObject implements IKhachHang_DAO {

    public KhachHang_DAO_Impl() throws RemoteException {
        super();
    }

    // --- HELPER METHOD: Ánh xạ Record từ Neo4j sang Object KhachHang ---
    private KhachHang mapKhachHang(Record r) {
        return new KhachHang(
                r.get("maKhachHang").asString(),
                r.get("hoTen").asString(),
                r.get("soDienThoai").asString(),
                r.get("email").isNull() ? null : r.get("email").asString(),
                r.get("diaChi").isNull() ? null : r.get("diaChi").asString(),
                r.get("ngaySinh").isNull() ? null : new Date(r.get("ngaySinh").asLong()),
                r.get("gioiTinh").asBoolean(),
                r.get("tichDiem").asInt()
        );
    }

    private final String CYPHER_RETURN = "RETURN kh.maKhachHang AS maKhachHang, kh.hoTen AS hoTen, " +
            "kh.soDienThoai AS soDienThoai, kh.email AS email, kh.diaChi AS diaChi, " +
            "kh.ngaySinh AS ngaySinh, kh.gioiTinh AS gioiTinh, kh.tichDiem AS tichDiem ";

    // --- CÁC HÀM CRUD CƠ BẢN ---

    @Override
    public String phatSinhMaKhachHang() throws RemoteException {
        try (Session session = DBConnect.getSession()) {
            Result result = session.run("MATCH (kh:KhachHang) RETURN kh.maKhachHang AS ma ORDER BY ma DESC LIMIT 1");
            if (result.hasNext()) {
                String maxMa = result.next().get("ma").asString();
                if (maxMa != null && maxMa.startsWith("KH")) {
                    int soMoi = Integer.parseInt(maxMa.substring(2)) + 1;
                    return String.format("KH%06d", soMoi);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "KH000001";
    }

    @Override
    public List<KhachHang> docDanhSachKhachHang() throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    @Override
    public boolean themKhachHang(KhachHang kh) throws RemoteException {
        String cypher = "CREATE (kh:KhachHang {maKhachHang: $ma, hoTen: $ten, soDienThoai: $sdt, email: $email, " +
                "diaChi: $diaChi, ngaySinh: $ngaySinh, gioiTinh: $gioiTinh, tichDiem: $diem, trangThai: 1})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "ma", kh.getMaKhachHang(), "ten", kh.getHoTen(), "sdt", kh.getSoDienThoai(),
                    "email", kh.getEmail(), "diaChi", kh.getDiaChi(),
                    "ngaySinh", kh.getNgaySinh() != null ? kh.getNgaySinh().getTime() : null,
                    "gioiTinh", kh.isGioiTinh(), "diem", kh.getTichDiem()
            ));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean capNhatKhachHang(KhachHang kh) throws RemoteException {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) " +
                "SET kh.hoTen = $ten, kh.soDienThoai = $sdt, kh.email = $email, " +
                "kh.diaChi = $diaChi, kh.ngaySinh = $ngaySinh, kh.gioiTinh = $gioiTinh, kh.tichDiem = $diem";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "ma", kh.getMaKhachHang(), "ten", kh.getHoTen(), "sdt", kh.getSoDienThoai(),
                    "email", kh.getEmail(), "diaChi", kh.getDiaChi(),
                    "ngaySinh", kh.getNgaySinh() != null ? kh.getNgaySinh().getTime() : null,
                    "gioiTinh", kh.isGioiTinh(), "diem", kh.getTichDiem()
            ));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean xoaKhachHang(String ma) throws RemoteException {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) SET kh.trangThai = 0";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", ma));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // --- CÁC HÀM TÌM KIẾM, LỌC VÀ SẮP XẾP ---

    @Override
    public List<KhachHang> timKiemTheoMa(String ma) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) WHERE toUpper(kh.maKhachHang) = toUpper($ma) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ma", ma));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    @Override
    public List<KhachHang> timKiemTheoTen(String ten) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) WHERE toUpper(kh.hoTen) CONTAINS toUpper($ten) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ten", ten));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    @Override
    public List<KhachHang> timKiemTheoSDT(String sdt) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) WHERE kh.soDienThoai CONTAINS $sdt " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("sdt", sdt));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    @Override
    public KhachHang timKhachHangTheoSDT(String sdt) throws RemoteException {
        String cypher = "MATCH (kh:KhachHang {trangThai: 1, soDienThoai: $sdt}) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("sdt", sdt));
            if (result.hasNext()) return mapKhachHang(result.next());
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<KhachHang> locKhachHangTheoGioiTinh(boolean gt) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1, gioiTinh: $gt}) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("gt", gt));
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    @Override
    public List<KhachHang> sapXepTheoTen(boolean tangDan) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String sort = tangDan ? "ASC" : "DESC";
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) " + CYPHER_RETURN + " ORDER BY kh.hoTen " + sort;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    @Override
    public List<KhachHang> sapXepTheoDiem(boolean tangDan) throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String sort = tangDan ? "ASC" : "DESC";
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) " + CYPHER_RETURN + " ORDER BY kh.tichDiem " + sort;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    // --- CÁC HÀM XỬ LÝ NGHIỆP VỤ & THỐNG KÊ ---

    @Override
    public boolean capNhatDiemTichLuy(String ma, int diem) throws RemoteException {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) SET kh.tichDiem = kh.tichDiem + $diem";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", ma, "diem", diem));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    public int getTongSoKhachHang() throws RemoteException {
        try (Session session = DBConnect.getSession()) {
            Result result = session.run("MATCH (kh:KhachHang {trangThai: 1}) RETURN count(kh) AS tong");
            if (result.hasNext()) return result.next().get("tong").asInt();
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public int getTongDiemTichLuy(java.util.Date tu, java.util.Date den) throws RemoteException {
        String cypher;
        if (tu == null || den == null) {
            cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN sum(kh.tichDiem) AS tong";
        } else {
            cypher = "MATCH (hd:HoaDon) WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den AND hd.maKhachHang IS NOT NULL " +
                    "WITH DISTINCT hd.maKhachHang AS maKH " +
                    "MATCH (kh:KhachHang {maKhachHang: maKH, trangThai: 1}) " +
                    "RETURN sum(kh.tichDiem) AS tong";
        }
        try (Session session = DBConnect.getSession()) {
            Result result;
            if (tu != null && den != null) {
                long endOfDay = den.getTime() + (23 * 3600 + 59 * 60 + 59) * 1000L; // Lấy đến cuối ngày
                result = session.run(cypher, Values.parameters("tu", tu.getTime(), "den", endOfDay));
            } else {
                result = session.run(cypher);
            }
            if (result.hasNext()) return result.next().get("tong").asInt();
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public Map<String, Integer> getSoLuongKhachHangTheoGioiTinh() throws RemoteException {
        Map<String, Integer> map = new HashMap<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 1}) " +
                "RETURN CASE WHEN kh.gioiTinh = true THEN 'Nam' ELSE 'Nữ' END AS GioiTinh, count(kh) AS SoLuong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                Record r = result.next();
                map.put(r.get("GioiTinh").asString(), r.get("SoLuong").asInt());
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    @Override
    public BigDecimal getTongChiTieuTatCaKhachHang(java.util.Date tu, java.util.Date den) throws RemoteException {
        StringBuilder cypher = new StringBuilder(
                "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) WHERE hd.maKhachHang IS NOT NULL "
        );
        if (tu != null && den != null) cypher.append("AND hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den ");

        cypher.append("MATCH (kh:KhachHang {maKhachHang: hd.maKhachHang, trangThai: 1}) " +
                "MATCH (hd)-[c:BAO_GOM]->(m:MonAn) " +
                "RETURN sum(c.soLuong * c.donGia) AS tong");

        try (Session session = DBConnect.getSession()) {
            Result result;
            if (tu != null && den != null) {
                long endOfDay = den.getTime() + (23 * 3600 + 59 * 60 + 59) * 1000L;
                result = session.run(cypher.toString(), Values.parameters("tu", tu.getTime(), "den", endOfDay));
            } else {
                result = session.run(cypher.toString());
            }
            if (result.hasNext()) return BigDecimal.valueOf(result.next().get("tong").asDouble());
        } catch (Exception e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    @Override
    public Map<String, BigDecimal> getTopKhachHangTheoChiTieu(int topN) throws RemoteException {
        Map<String, BigDecimal> map = new LinkedHashMap<>(); // Giữ thứ tự Top
        String cypher = "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'})-[c:BAO_GOM]->(m:MonAn) " +
                "WHERE hd.maKhachHang IS NOT NULL " +
                "MATCH (kh:KhachHang {maKhachHang: hd.maKhachHang, trangThai: 1}) " +
                "RETURN kh.hoTen AS hoTen, sum(c.soLuong * c.donGia) AS TongChiTieu " +
                "ORDER BY TongChiTieu DESC LIMIT $topN";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("topN", topN));
            while (result.hasNext()) {
                Record r = result.next();
                map.put(r.get("hoTen").asString(), BigDecimal.valueOf(r.get("TongChiTieu").asDouble()));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    @Override
    public int getTongSoKhachHang(java.util.Date tu, java.util.Date den) throws RemoteException {
        String cypher;
        if (tu == null || den == null) {
            cypher = "MATCH (kh:KhachHang {trangThai: 1}) RETURN count(kh) AS tong";
        } else {
            cypher = "MATCH (hd:HoaDon) WHERE hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den AND hd.maKhachHang IS NOT NULL " +
                    "RETURN count(DISTINCT hd.maKhachHang) AS tong";
        }
        try (Session session = DBConnect.getSession()) {
            Result result;
            if (tu != null && den != null) {
                long endOfDay = den.getTime() + (23 * 3600 + 59 * 60 + 59) * 1000L;
                result = session.run(cypher, Values.parameters("tu", tu.getTime(), "den", endOfDay));
            } else {
                result = session.run(cypher);
            }
            if (result.hasNext()) return result.next().get("tong").asInt();
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public List<Object[]> getTopKhachHangDayDu(int topN, java.util.Date tu, java.util.Date den) throws RemoteException {
        List<Object[]> list = new ArrayList<>();
        StringBuilder cypher = new StringBuilder(
                "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'}) WHERE hd.maKhachHang IS NOT NULL "
        );
        if (tu != null && den != null) cypher.append("AND hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den ");

        cypher.append("MATCH (kh:KhachHang {maKhachHang: hd.maKhachHang, trangThai: 1}) " +
                "OPTIONAL MATCH (hd)-[c:BAO_GOM]->(m:MonAn) " +
                "RETURN kh.hoTen AS hoTen, sum(c.soLuong * c.donGia) AS TongChiTieu, kh.tichDiem AS tichDiem " +
                "ORDER BY TongChiTieu DESC LIMIT $topN");

        try (Session session = DBConnect.getSession()) {
            Result result;
            if (tu != null && den != null) {
                long endOfDay = den.getTime() + (23 * 3600 + 59 * 60 + 59) * 1000L;
                result = session.run(cypher.toString(), Values.parameters("tu", tu.getTime(), "den", endOfDay, "topN", topN));
            } else {
                result = session.run(cypher.toString(), Values.parameters("topN", topN));
            }
            while (result.hasNext()) {
                Record r = result.next();
                list.add(new Object[]{
                        r.get("hoTen").asString(),
                        BigDecimal.valueOf(r.get("TongChiTieu").asDouble()),
                        r.get("tichDiem").asInt()
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // --- CÁC HÀM XỬ LÝ KHÁCH HÀNG ĐÃ XÓA MỀM ---

    @Override
    public List<KhachHang> docDanhSachKhachHangDaXoa() throws RemoteException {
        List<KhachHang> ds = new ArrayList<>();
        String cypher = "MATCH (kh:KhachHang {trangThai: 0}) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(mapKhachHang(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    @Override
    public boolean khoiPhucKhachHang(String ma) throws RemoteException {
        String cypher = "MATCH (kh:KhachHang {maKhachHang: $ma}) SET kh.trangThai = 1";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", ma));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
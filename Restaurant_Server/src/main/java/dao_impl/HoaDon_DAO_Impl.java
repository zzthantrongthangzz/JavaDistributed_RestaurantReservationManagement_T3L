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
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO_Impl extends UnicastRemoteObject implements IHoaDon_DAO {

    public HoaDon_DAO_Impl() throws RemoteException {
        super();
    }

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
        try (Session session = DBConnect.getSession()) {
            Result result = session.run("MATCH (hd:HoaDon) RETURN hd.maHoaDon AS maHoaDon, hd.trangThai AS trangThai, " +
                    "hd.ngayLapHoaDon AS ngayLapHoaDon, hd.thue AS thue, hd.maNhanVien AS maNhanVien, " +
                    "hd.maPhieuDatBan AS maPhieuDatBan, hd.maKhachHang AS maKhachHang, hd.maKhuyenMai AS maKhuyenMai, " +
                    "hd.diaChi AS diaChi, hd.tienDatCoc AS tienDatCoc, hd.soTienKhachTra AS soTienKhachTra, hd.soTienThoi AS soTienThoi");
            while (result.hasNext()) {
                list.add(mapHoaDon(result.next()));
            }
        }
        return list;
    }

    // Yêu cầu phân tán: Sử dụng synchronized để tránh xung đột khi có nhiều luồng cùng thêm hóa đơn
    @Override
    public synchronized boolean themHoaDon(HoaDon hd) throws RemoteException {
        String cypher = "CREATE (hd:HoaDon {maHoaDon: $maHD, trangThai: $trangThai, ngayLapHoaDon: $ngayLap, " +
                "thue: $thue, maNhanVien: $maNV, maPhieuDatBan: $maPhieu, maKhachHang: $maKH, " +
                "maKhuyenMai: $maKM, diaChi: $diaChi, tienDatCoc: $coc, soTienKhachTra: $tra, soTienThoi: $thoi})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maHD", hd.getMaHoaDon(), "trangThai", hd.getTrangThai(),
                    "ngayLap", hd.getNgayLapHoaDon().getTime(), // Lưu thời gian dưới dạng Long Milliseconds
                    "thue", hd.getThue().doubleValue(),
                    "maNV", hd.getMaNhanVien(), "maPhieu", hd.getMaPhieuDatBan(), "maKH", hd.getMaKhachHang(),
                    "maKM", hd.getMaKhuyenMai(), "diaChi", hd.getDiaChi(),
                    "coc", hd.getTienDatCoc().doubleValue(), "tra", hd.getSoTienKhachTra().doubleValue(), "thoi", hd.getSoTienThoi().doubleValue()
            ));
            return true;
        } catch (Exception e) { return false; }
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
        } catch (Exception e) { return false; }
    }

    @Override
    public String sinhMaHoaDonTuDong() throws RemoteException {
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

    // Các hàm tìm kiếm, xóa làm tương tự như cấu trúc MATCH
}
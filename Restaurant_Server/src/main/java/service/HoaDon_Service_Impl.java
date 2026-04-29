package service;
import dao_impl.HoaDon_DAO;
import entity.HoaDon;
import rmi_interfaces.IHoaDon_Service;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HoaDon_Service_Impl extends UnicastRemoteObject implements IHoaDon_Service {
    private HoaDon_DAO dao;
    public HoaDon_Service_Impl() throws RemoteException {
        super();
        this.dao = new HoaDon_DAO();
    }
    @Override public List<HoaDon> getAllHoaDon() throws RemoteException { return dao.getAllHoaDon(); }
    @Override public boolean themHoaDon(HoaDon hd) throws RemoteException { return dao.themHoaDon(hd); }
    @Override public boolean capNhatHoaDon(HoaDon hd) throws RemoteException { return dao.capNhatHoaDon(hd); }
    @Override public boolean xoaHoaDon(String maHoaDon) throws RemoteException { return dao.xoaHoaDon(maHoaDon); }
    @Override public HoaDon timTheoMa(String maHoaDon) throws RemoteException { return dao.timTheoMa(maHoaDon); }
    @Override public List<HoaDon> timTheoNgay(Date tuNgay, Date denNgay) throws RemoteException { return dao.timTheoNgay(tuNgay, denNgay); }
    @Override public HoaDon timHoaDonChuaThanhToanTheoMaBan(String maBan) throws RemoteException { return dao.timHoaDonChuaThanhToanTheoMaBan(maBan); }
    @Override public String sinhMaHoaDonTuDong() throws RemoteException { return dao.sinhMaHoaDonTuDong(); }
    @Override public List<HoaDon> locTheoTrangThai(String trangThai) throws RemoteException { return dao.locTheoTrangThai(trangThai); }
    @Override public List<HoaDon> timKiemChung(String tuKhoa) throws RemoteException { return dao.timKiemChung(tuKhoa); }
    @Override public List<HoaDon> sapXep(String orderBy) throws RemoteException { return dao.sapXep(orderBy); }
    @Override public List<HoaDon> timKiemNangCao(String maHD, String maKH, String maNV, Date tuNgay, Date denNgay) throws RemoteException { return dao.timKiemNangCao(maHD, maKH, maNV, tuNgay, denNgay); }
    @Override public BigDecimal getTongDoanhThu(Date tuNgay, Date denNgay) throws RemoteException { return dao.getTongDoanhThu(tuNgay, denNgay); }
    @Override public int getTongSoHoaDon(Date tuNgay, Date denNgay) throws RemoteException { return dao.getTongSoHoaDon(tuNgay, denNgay); }
    @Override public Map<Date, BigDecimal> getDoanhThuTheoNgay(Date tuNgay, Date denNgay) throws RemoteException { return dao.getDoanhThuTheoNgay(tuNgay, denNgay); }
    @Override public String getTenKhachHangTheoBan(String maBan) throws RemoteException { return dao.getTenKhachHangTheoBan(maBan); }
    @Override public BigDecimal tinhTongTienCuaHoaDon(String maHoaDon) throws RemoteException { return dao.tinhTongTienCuaHoaDon(maHoaDon); }
    @Override public List<HoaDon> getDanhSachHoaDon(Date tuNgay, Date denNgay) throws RemoteException { return dao.getDanhSachHoaDon(tuNgay, denNgay); }
}
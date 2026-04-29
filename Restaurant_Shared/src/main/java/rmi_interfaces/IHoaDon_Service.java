package rmi_interfaces;

import entity.HoaDon;
import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IHoaDon_Service extends Remote {
    public List<HoaDon> getAllHoaDon() throws RemoteException;
    public boolean themHoaDon(HoaDon hd) throws RemoteException;
    public boolean capNhatHoaDon(HoaDon hd) throws RemoteException;
    public boolean xoaHoaDon(String maHoaDon) throws RemoteException;
    public HoaDon timTheoMa(String maHoaDon) throws RemoteException;
    public List<HoaDon> timTheoNgay(Date tuNgay, Date denNgay) throws RemoteException;
    public HoaDon timHoaDonChuaThanhToanTheoMaBan(String maBan) throws RemoteException;
    public String sinhMaHoaDonTuDong() throws RemoteException;
    public List<HoaDon> locTheoTrangThai(String trangThai) throws RemoteException;
    public List<HoaDon> timKiemChung(String tuKhoa) throws RemoteException;
    public List<HoaDon> sapXep(String orderBy) throws RemoteException;
    public List<HoaDon> timKiemNangCao(String maHD, String maKH, String maNV, Date tuNgay, Date denNgay) throws RemoteException;
    public BigDecimal getTongDoanhThu(Date tuNgay, Date denNgay) throws RemoteException;
    public int getTongSoHoaDon(Date tuNgay, Date denNgay) throws RemoteException;
    public Map<Date, BigDecimal> getDoanhThuTheoNgay(Date tuNgay, Date denNgay) throws RemoteException;
    public String getTenKhachHangTheoBan(String maBan) throws RemoteException;
    public BigDecimal tinhTongTienCuaHoaDon(String maHoaDon) throws RemoteException;
    public List<HoaDon> getDanhSachHoaDon(Date tuNgay, Date denNgay) throws RemoteException;
}
package rmi_interfaces;

import entity.KhachHang;
import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IKhachHang_DAO extends Remote {
    public String phatSinhMaKhachHang() throws RemoteException;
    public List<KhachHang> docDanhSachKhachHang() throws RemoteException;
    public boolean themKhachHang(KhachHang kh) throws RemoteException;
    public boolean capNhatKhachHang(KhachHang kh) throws RemoteException;
    public List<KhachHang> timKiemTheoMa(String ma) throws RemoteException;
    public List<KhachHang> timKiemTheoTen(String ten) throws RemoteException;
    public List<KhachHang> timKiemTheoSDT(String sdt) throws RemoteException;
    public List<KhachHang> locKhachHangTheoGioiTinh(boolean gt) throws RemoteException;
    public List<KhachHang> sapXepTheoTen(boolean tangDan) throws RemoteException;
    public List<KhachHang> sapXepTheoDiem(boolean tangDan) throws RemoteException;
    public boolean xoaKhachHang(String ma) throws RemoteException;
    public KhachHang timKhachHangTheoSDT(String sdt) throws RemoteException;
    public boolean capNhatDiemTichLuy(String ma, int diem) throws RemoteException;
    public int getTongSoKhachHang() throws RemoteException;
    public int getTongDiemTichLuy(Date tu, Date den) throws RemoteException;
    public Map<String, Integer> getSoLuongKhachHangTheoGioiTinh() throws RemoteException;
    public BigDecimal getTongChiTieuTatCaKhachHang(Date tu, Date den) throws RemoteException;
    public Map<String, BigDecimal> getTopKhachHangTheoChiTieu(int topN) throws RemoteException;
    public int getTongSoKhachHang(Date tu, Date den) throws RemoteException;
    public List<Object[]> getTopKhachHangDayDu(int topN, Date tu, Date den) throws RemoteException;
    public List<KhachHang> docDanhSachKhachHangDaXoa() throws RemoteException;
    public boolean khoiPhucKhachHang(String ma) throws RemoteException;
}
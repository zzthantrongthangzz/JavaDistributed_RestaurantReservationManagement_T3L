package rmi_interfaces;

import entity.KhuyenMai;
import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IKhuyenMai_DAO extends Remote {
    public List<KhuyenMai> getAllList() throws RemoteException;
    public List<KhuyenMai> getAllListSorted(String sortBy) throws RemoteException;
    public List<KhuyenMai> getByLoaiKhuyenMai(String loaiKM) throws RemoteException;
    public KhuyenMai getByMaKhuyenMai(String maKM) throws RemoteException;
    public boolean themKhuyenMai(KhuyenMai km) throws RemoteException;
    public boolean capNhatKhuyenMai(KhuyenMai km) throws RemoteException;
    public boolean anKhuyenMai(String maKM) throws RemoteException;
    public List<KhuyenMai> getKhuyenMaiHetHan() throws RemoteException;
    public List<KhuyenMai> timKiem(String tuKhoa) throws RemoteException;
    public String taoMaKhuyenMaiTuDong() throws RemoteException;
    public boolean kiemTraMaTonTai(String maKM) throws RemoteException;
    public int getTongLuotSuDung(Date tuNgay, Date denNgay) throws RemoteException;
    public BigDecimal getTongTienGiam(Date tuNgay, Date denNgay) throws RemoteException;
    public Map<Date, BigDecimal> getTienGiamTheoNgay(Date tuNgay, Date denNgay) throws RemoteException;
    public List<KhuyenMai> locDanhSach(String maKM, String tenKM, String loaiKM, String giaTriFilter, Date tuNgay, Date denNgay, String sapXep) throws RemoteException;
    public List<Object[]> getThongKeChiTietKhuyenMai(Date tuNgay, Date denNgay) throws RemoteException;
}
package rmi_interfaces;

import entity.LichSuGia;
import entity.MonAn;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface IMonAn_DAO extends Remote {
    public List<MonAn> docDanhSachMon() throws RemoteException;
    public List<MonAn> timKiemMonAn(String tuKhoa) throws RemoteException;
    public List<MonAn> timKiemTheoMa(String maMon) throws RemoteException;
    public List<MonAn> timKiemTheoTen(String tenMon) throws RemoteException;
    public List<MonAn> locMonAnTheoLoai(String tenLoai) throws RemoteException;
    public String sinhMaMonTuDong() throws RemoteException;
    public boolean themMonAn(MonAn monAn) throws RemoteException;
    public MonAn timMotMonTheoMa(String maMon) throws RemoteException;
    public boolean xoaMem(String maMon) throws RemoteException;
    public boolean capNhatMonAn(MonAn monAn, String maNhanVienThucHien) throws RemoteException;
    public List<Object[]> getThongKeMonAn(Date tuNgay, Date denNgay) throws RemoteException;
    public boolean themDanhSachMonAn(List<MonAn> danhSachMon) throws RemoteException;
    public List<LichSuGia> getLichSuGia(String maMon) throws RemoteException;
}
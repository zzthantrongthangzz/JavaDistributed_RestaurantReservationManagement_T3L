package rmi_interfaces;

import entity.BanAn;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface IBanAn_DAO extends Remote {
    public String layMaBanTiepTheo() throws RemoteException;
    public int laySoThuTuBanTiepTheo(String loaiBan) throws RemoteException;
    public boolean themBanMoi(BanAn banAn) throws RemoteException;
    public List<BanAn> docDanhSachBan() throws RemoteException;
    public List<BanAn> timKiemBan(String tuKhoa) throws RemoteException;
    public BanAn timBanAnTheoMa(String maBan) throws RemoteException;
    public List<BanAn> locTheoLoaiBan(String loaiBan) throws RemoteException;
    public List<BanAn> locTheoTrangThai(String trangThai) throws RemoteException;
    public List<String> docDanhSachTenTang() throws RemoteException;
    public List<String> docDanhSachTenKhuTheoTang(String tenTang) throws RemoteException;
    public List<BanAn> locBanAn(String tenTang, String tenKhu, String loaiBan) throws RemoteException;
    public boolean capNhatTrangThaiBan(String maBan, String trangThaiMoi) throws RemoteException;
    public boolean capNhatBan(BanAn banAn) throws RemoteException;
    public boolean xoaBan(String maBan) throws RemoteException;
    public BanAn timBanAnTheoTenHoacSDTKhachHang(String tuKhoa) throws RemoteException;
    public List<BanAn> layDanhSachBanTrongTheoNgay(Date ngayCanXem) throws RemoteException;
    public List<BanAn> timKiemBanTheoKhachHang(String tuKhoa) throws RemoteException;
}
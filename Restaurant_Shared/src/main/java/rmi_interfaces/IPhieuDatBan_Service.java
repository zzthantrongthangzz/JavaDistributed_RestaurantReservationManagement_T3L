package rmi_interfaces;

import entity.PhieuDatBan;
import entity.PhieuDatBan_Ban;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IPhieuDatBan_Service extends Remote {
    public List<PhieuDatBan> getPhieuDatBanChoHomNay() throws RemoteException;
    public Map<String, String> layThongTinBanDatVaTenKhach(Date ngayCanXem) throws RemoteException;
    public String sinhMaPhieuDatTuDong() throws RemoteException;
    public boolean themPhieuDatBan(PhieuDatBan phieu) throws RemoteException;
    public boolean themPhieuDatBan_Ban(PhieuDatBan_Ban phieuBan) throws RemoteException;
    public List<String> layDanhSachMaBanDaDatTheoNgay(Date ngayCanXem) throws RemoteException;
    public boolean huyDatBan(String maBan, Date ngayDat) throws RemoteException;
    public boolean chuyenBanDatTruoc(String maBanCu, String maBanMoi, Date ngayDat) throws RemoteException;
    public PhieuDatBan getPhieuDatBanTheoMa(String maPhieu) throws RemoteException;
    public String timMaPhieuDatDangChoTheoBan(String maBan, Date ngayDat) throws RemoteException;
    public boolean capNhatTrangThai(String maPhieu, String trangThaiMoi) throws RemoteException;
    public boolean capNhatTienCoc(String maPhieuDatBan, double tienDatCoc) throws RemoteException;
    public List<String> layDanhSachMaBanTheoPhieuDat(String maPhieu) throws RemoteException;
    public int huyPhieuDatQuaGio(int phutTreChoPhep) throws RemoteException;
}
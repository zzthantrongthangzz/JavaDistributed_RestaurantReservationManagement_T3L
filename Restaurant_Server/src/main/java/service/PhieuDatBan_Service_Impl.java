package service;
import dao_impl.PhieuDatBan_DAO;
import entity.PhieuDatBan;
import entity.PhieuDatBan_Ban;
import rmi_interfaces.IPhieuDatBan_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PhieuDatBan_Service_Impl extends UnicastRemoteObject implements IPhieuDatBan_Service {
    private PhieuDatBan_DAO dao;
    public PhieuDatBan_Service_Impl() throws RemoteException {
        super();
        this.dao = new PhieuDatBan_DAO();
    }
    @Override public List<PhieuDatBan> getPhieuDatBanChoHomNay() throws RemoteException { return dao.getPhieuDatBanChoHomNay(); }
    @Override public Map<String, String> layThongTinBanDatVaTenKhach(Date ngayCanXem) throws RemoteException { return dao.layThongTinBanDatVaTenKhach(ngayCanXem); }
    @Override public String sinhMaPhieuDatTuDong() throws RemoteException { return dao.sinhMaPhieuDatTuDong(); }
    @Override public boolean themPhieuDatBan(PhieuDatBan phieu) throws RemoteException { return dao.themPhieuDatBan(phieu); }
    @Override public boolean themPhieuDatBan_Ban(PhieuDatBan_Ban phieuBan) throws RemoteException { return dao.themPhieuDatBan_Ban(phieuBan); }
    @Override public List<String> layDanhSachMaBanDaDatTheoNgay(Date ngayCanXem) throws RemoteException { return dao.layDanhSachMaBanDaDatTheoNgay(ngayCanXem); }
    @Override public boolean huyDatBan(String maBan, Date ngayDat) throws RemoteException { return dao.huyDatBan(maBan, ngayDat); }
    @Override public boolean chuyenBanDatTruoc(String maBanCu, String maBanMoi, Date ngayDat) throws RemoteException { return dao.chuyenBanDatTruoc(maBanCu, maBanMoi, ngayDat); }
    @Override public PhieuDatBan getPhieuDatBanTheoMa(String maPhieu) throws RemoteException { return dao.getPhieuDatBanTheoMa(maPhieu); }
    @Override public String timMaPhieuDatDangChoTheoBan(String maBan, Date ngayDat) throws RemoteException { return dao.timMaPhieuDatDangChoTheoBan(maBan, ngayDat); }
    @Override public boolean capNhatTrangThai(String maPhieu, String trangThaiMoi) throws RemoteException { return dao.capNhatTrangThai(maPhieu, trangThaiMoi); }
    @Override public boolean capNhatTienCoc(String maPhieuDatBan, double tienDatCoc) throws RemoteException { return dao.capNhatTienCoc(maPhieuDatBan, tienDatCoc); }
    @Override public List<String> layDanhSachMaBanTheoPhieuDat(String maPhieu) throws RemoteException { return dao.layDanhSachMaBanTheoPhieuDat(maPhieu); }
    @Override public int huyPhieuDatQuaGio(int phutTreChoPhep) throws RemoteException { return dao.huyPhieuDatQuaGio(phutTreChoPhep); }
}
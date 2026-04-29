package service;
import dao_impl.BanAn_DAO;
import entity.BanAn;
import rmi_interfaces.IBanAn_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;

public class BanAn_Service_Impl extends UnicastRemoteObject implements IBanAn_Service {
    private BanAn_DAO dao;
    public BanAn_Service_Impl() throws RemoteException {
        super();
        this.dao = new BanAn_DAO();
    }
    @Override public List<BanAn> docDanhSachBan() throws RemoteException { return dao.docDanhSachBan(); }
    @Override public boolean themBanMoi(BanAn banAn) throws RemoteException { return dao.themBanMoi(banAn); }
    @Override public boolean capNhatTrangThaiBan(String maBan, String trangThaiMoi) throws RemoteException { return dao.capNhatTrangThaiBan(maBan, trangThaiMoi); }
    @Override public boolean capNhatBan(BanAn banAn) throws RemoteException { return dao.capNhatBan(banAn); }
    @Override public boolean xoaBan(String maBan) throws RemoteException { return dao.xoaBan(maBan); }
    @Override public BanAn timBanAnTheoTenHoacSDTKhachHang(String tuKhoa) throws RemoteException { return dao.timBanAnTheoTenHoacSDTKhachHang(tuKhoa); }
    @Override public List<BanAn> layDanhSachBanTrongTheoNgay(Date ngayCanXem) throws RemoteException { return dao.layDanhSachBanTrongTheoNgay(ngayCanXem); }
    @Override public List<BanAn> timKiemBanTheoKhachHang(String tuKhoa) throws RemoteException { return dao.timKiemBanTheoKhachHang(tuKhoa); }
    @Override public BanAn timBanAnTheoMa(String maBan) throws RemoteException { return dao.timBanAnTheoMa(maBan); }
    @Override public List<BanAn> locTheoLoaiBan(String loaiBan) throws RemoteException { return dao.locTheoLoaiBan(loaiBan); }
    @Override public List<BanAn> timKiemBan(String tuKhoa) throws RemoteException { return dao.timKiemBan(tuKhoa); }
    @Override public List<BanAn> locTheoTrangThai(String trangThai) throws RemoteException { return dao.locTheoTrangThai(trangThai); }
    @Override public List<String> docDanhSachTenTang() throws RemoteException { return dao.docDanhSachTenTang(); }
    @Override public List<String> docDanhSachTenKhuTheoTang(String tenTang) throws RemoteException { return dao.docDanhSachTenKhuTheoTang(tenTang); }
    @Override public List<BanAn> locBanAn(String tenTang, String tenKhu, String loaiBan) throws RemoteException { return dao.locBanAn(tenTang, tenKhu, loaiBan); }
    @Override public String layMaBanTiepTheo() throws RemoteException { return dao.layMaBanTiepTheo(); }
    @Override public int laySoThuTuBanTiepTheo(String loaiBan) throws RemoteException { return dao.laySoThuTuBanTiepTheo(loaiBan); }
}
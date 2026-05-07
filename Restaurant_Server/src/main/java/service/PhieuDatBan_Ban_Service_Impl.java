package service;
import dao_impl.PhieuDatBan_Ban_DAO;
import entity.BanAn;
import rmi_interfaces.IPhieuDatBan_Ban_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PhieuDatBan_Ban_Service_Impl extends UnicastRemoteObject implements IPhieuDatBan_Ban_Service {
    private PhieuDatBan_Ban_DAO dao;
    public PhieuDatBan_Ban_Service_Impl() throws RemoteException {
        super();
        this.dao = new PhieuDatBan_Ban_DAO();
    }
    @Override public List<BanAn> getDanhSachBanTheoPhieu(String maPhieu) throws RemoteException { return dao.getDanhSachBanTheoPhieu(maPhieu); }
    @Override public boolean themPhieuDatBan_Ban(String maPhieu, String maBan) throws RemoteException { return dao.themPhieuDatBan_Ban(maPhieu, maBan); }
    @Override public int demSoBanCuaPhieu(String maPhieu) throws RemoteException { return dao.demSoBanCuaPhieu(maPhieu); }
    @Override public boolean xoaBanKhoiPhieu(String maPhieu, String maBan) throws RemoteException { return dao.xoaBanKhoiPhieu(maPhieu, maBan); }
}
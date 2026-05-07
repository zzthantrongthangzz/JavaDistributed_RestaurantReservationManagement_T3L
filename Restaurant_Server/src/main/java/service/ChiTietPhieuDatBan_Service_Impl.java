package service;
import dao_impl.ChiTietPhieuDatBan_DAO;
import entity.ChiTietPhieuDatBan;
import rmi_interfaces.IChiTietPhieuDatBan_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ChiTietPhieuDatBan_Service_Impl extends UnicastRemoteObject implements IChiTietPhieuDatBan_Service {
    private ChiTietPhieuDatBan_DAO dao;
    public ChiTietPhieuDatBan_Service_Impl() throws RemoteException {
        super();
        this.dao = new ChiTietPhieuDatBan_DAO();
    }
    @Override public boolean themChiTietPhieuDat(ChiTietPhieuDatBan ct) throws RemoteException { return dao.themChiTietPhieuDat(ct); }
    @Override public List<ChiTietPhieuDatBan> getChiTietTheoPhieu(String maPhieuDatBan) throws RemoteException { return dao.getChiTietTheoPhieu(maPhieuDatBan); }
    @Override public boolean xoaChiTietTheoPhieu(String maPhieuDatBan) throws RemoteException { return dao.xoaChiTietTheoPhieu(maPhieuDatBan); }
    @Override public boolean capNhatChiTiet(ChiTietPhieuDatBan ct) throws RemoteException { return dao.capNhatChiTiet(ct); }
}
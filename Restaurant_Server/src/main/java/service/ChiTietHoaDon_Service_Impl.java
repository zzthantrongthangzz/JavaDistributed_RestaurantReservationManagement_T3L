package service;
import dao_impl.ChiTietHoaDon_DAO;
import entity.ChiTietHoaDon;
import rmi_interfaces.IChiTietHoaDon_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ChiTietHoaDon_Service_Impl extends UnicastRemoteObject implements IChiTietHoaDon_Service {
    private ChiTietHoaDon_DAO dao;
    public ChiTietHoaDon_Service_Impl() throws RemoteException {
        super();
        this.dao = new ChiTietHoaDon_DAO();
    }
    @Override public List<ChiTietHoaDon> getAllChiTietHoaDon() throws RemoteException { return dao.getAllChiTietHoaDon(); }
    @Override public List<ChiTietHoaDon> getChiTietTheoMaHoaDon(String maHoaDon) throws RemoteException { return dao.getChiTietTheoMaHoaDon(maHoaDon); }
    @Override public boolean themChiTietHoaDon(ChiTietHoaDon cthd) throws RemoteException { return dao.themChiTietHoaDon(cthd); }
    @Override public boolean capNhatChiTietHoaDon(ChiTietHoaDon cthd) throws RemoteException { return dao.capNhatChiTietHoaDon(cthd); }
    @Override public boolean xoaChiTietHoaDon(String maHoaDon, String maMon) throws RemoteException { return dao.xoaChiTietHoaDon(maHoaDon, maMon); }
    @Override public ChiTietHoaDon timChiTiet(String maHoaDon, String maMon) throws RemoteException { return dao.timChiTiet(maHoaDon, maMon); }
    @Override public boolean xoaChiTietTheoMaHoaDon(String maHoaDon) throws RemoteException { return dao.xoaChiTietTheoMaHoaDon(maHoaDon); }
}
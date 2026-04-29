package service;
import dao_impl.HoaDon_Ban_DAO;
import entity.HoaDon_Ban;
import rmi_interfaces.IHoaDon_Ban_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class HoaDon_Ban_Service_Impl extends UnicastRemoteObject implements IHoaDon_Ban_Service {
    private HoaDon_Ban_DAO dao;
    public HoaDon_Ban_Service_Impl() throws RemoteException {
        super();
        this.dao = new HoaDon_Ban_DAO();
    }
    @Override public boolean themHoaDon_Ban(String maHoaDon, String maBan) throws RemoteException { return dao.themHoaDon_Ban(maHoaDon, maBan); }
    @Override public boolean themHoaDon_Ban(HoaDon_Ban hdb) throws RemoteException { return dao.themHoaDon_Ban(hdb); }
    @Override public boolean chuyenBan(String maHoaDon, String maBanCu, String maBanMoi) throws RemoteException { return dao.chuyenBan(maHoaDon, maBanCu, maBanMoi); }
    @Override public List<String> layDanhSachMaBanTheoHoaDon(String maHoaDon) throws RemoteException { return dao.layDanhSachMaBanTheoHoaDon(maHoaDon); }
    @Override public boolean xoaHoaDon_Ban(String maHoaDon, String maBan) throws RemoteException { return dao.xoaHoaDon_Ban(maHoaDon, maBan); }
    @Override public boolean xoaTatCaBanCuaHoaDon(String maHoaDon) throws RemoteException { return dao.xoaTatCaBanCuaHoaDon(maHoaDon); }
}
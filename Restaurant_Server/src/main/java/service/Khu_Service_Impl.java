package service;
import dao_impl.Khu_DAO;
import entity.Khu;
import rmi_interfaces.IKhu_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Khu_Service_Impl extends UnicastRemoteObject implements IKhu_Service {
    private Khu_DAO dao;
    public Khu_Service_Impl() throws RemoteException {
        super();
        this.dao = new Khu_DAO();
    }
    @Override public Khu timKhuTheoTen(String tenKhu) throws RemoteException { return dao.timKhuTheoTen(tenKhu); }
    @Override public String layMaKhuTheoTen(String tenKhu) throws RemoteException { return dao.layMaKhuTheoTen(tenKhu); }
    @Override public String sinhMaKhuTuDong() throws RemoteException { return dao.sinhMaKhuTuDong(); }
    @Override public boolean themKhu(Khu khu) throws RemoteException { return dao.themKhu(khu); }
}
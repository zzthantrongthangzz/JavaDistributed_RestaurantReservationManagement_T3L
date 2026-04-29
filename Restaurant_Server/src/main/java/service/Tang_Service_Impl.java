package service;
import dao_impl.Tang_DAO;
import entity.Tang;
import rmi_interfaces.ITang_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Tang_Service_Impl extends UnicastRemoteObject implements ITang_Service {
    private Tang_DAO dao;
    public Tang_Service_Impl() throws RemoteException {
        super();
        this.dao = new Tang_DAO();
    }
    @Override public List<String> docDanhSachTenTang() throws RemoteException { return dao.docDanhSachTenTang(); }
    @Override public Tang timTangTheoTen(String tenTang) throws RemoteException { return dao.timTangTheoTen(tenTang); }
    @Override public String sinhMaTangTuDong() throws RemoteException { return dao.sinhMaTangTuDong(); }
    @Override public boolean themTang(Tang tang) throws RemoteException { return dao.themTang(tang); }
}
package service;
import dao_impl.LoaiMon_DAO;
import entity.LoaiMon;
import rmi_interfaces.ILoaiMon_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class LoaiMon_Service_Impl extends UnicastRemoteObject implements ILoaiMon_Service {
    private LoaiMon_DAO dao;
    public LoaiMon_Service_Impl() throws RemoteException {
        super();
        this.dao = new LoaiMon_DAO();
    }
    @Override public List<LoaiMon> docDanhSachLoaiMon() throws RemoteException { return dao.docDanhSachLoaiMon(); }
    @Override public boolean themLoaiMon(LoaiMon loaiMon) throws RemoteException { return dao.themLoaiMon(loaiMon); }
    @Override public String sinhMaLoaiTuDong() throws RemoteException { return dao.sinhMaLoaiTuDong(); }
    @Override public LoaiMon timLoaiTheoTen(String tenLoai) throws RemoteException { return dao.timLoaiTheoTen(tenLoai); }
}
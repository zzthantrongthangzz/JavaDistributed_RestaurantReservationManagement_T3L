package service;
import dao_impl.ChucVu_DAO;
import entity.ChucVu;
import rmi_interfaces.IChucVu_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ChucVu_Service_Impl extends UnicastRemoteObject implements IChucVu_Service {
    private ChucVu_DAO dao;
    public ChucVu_Service_Impl() throws RemoteException {
        super();
        this.dao = new ChucVu_DAO();
    }
    @Override public List<ChucVu> docDanhSachChucVu() throws RemoteException { return dao.docDanhSachChucVu(); }
    @Override public boolean themChucVu(ChucVu chucVu) throws RemoteException { return dao.themChucVu(chucVu); }
    @Override public ChucVu timChucVuTheoTen(String tenCV) throws RemoteException { return dao.timChucVuTheoTen(tenCV); }
    @Override public String sinhMaChucVuTuDong() throws RemoteException { return dao.sinhMaChucVuTuDong(); }
}
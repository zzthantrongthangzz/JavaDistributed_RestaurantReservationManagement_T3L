package service;
import dao_impl.LichSuHuyDatBan_DAO;
import entity.LichSuHuyDatBan;
import rmi_interfaces.ILichSuHuyDatBan_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class LichSuHuyDatBan_Service_Impl extends UnicastRemoteObject implements ILichSuHuyDatBan_Service {
    private LichSuHuyDatBan_DAO dao;
    public LichSuHuyDatBan_Service_Impl() throws RemoteException {
        super();
        this.dao = new LichSuHuyDatBan_DAO();
    }
    @Override public boolean ghiLogHuyDatBan(LichSuHuyDatBan log) throws RemoteException { return dao.ghiLogHuyDatBan(log); }
    @Override public List<LichSuHuyDatBan> layTatCaLichSu() throws RemoteException { return dao.layTatCaLichSu(); }
}
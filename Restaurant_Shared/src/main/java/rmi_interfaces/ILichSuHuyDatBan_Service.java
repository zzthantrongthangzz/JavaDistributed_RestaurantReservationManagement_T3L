package rmi_interfaces;

import entity.LichSuHuyDatBan;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ILichSuHuyDatBan_Service extends Remote {
    public boolean ghiLogHuyDatBan(LichSuHuyDatBan log) throws RemoteException;
    public List<LichSuHuyDatBan> layTatCaLichSu() throws RemoteException;
}
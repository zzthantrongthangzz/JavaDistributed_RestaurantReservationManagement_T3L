package rmi_interfaces;

import entity.Khu;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IKhu_DAO extends Remote {
    public String layMaKhuTheoTen(String tenKhu) throws RemoteException;
    public Khu timKhuTheoTen(String tenKhu) throws RemoteException;
    public String sinhMaKhuTuDong() throws RemoteException;
    public boolean themKhu(Khu khu) throws RemoteException;
}
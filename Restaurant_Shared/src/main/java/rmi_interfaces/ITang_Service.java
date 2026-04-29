package rmi_interfaces;

import entity.Tang;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ITang_Service extends Remote {
    public List<String> docDanhSachTenTang() throws RemoteException;
    public Tang timTangTheoTen(String tenTang) throws RemoteException;
    public String sinhMaTangTuDong() throws RemoteException;
    public boolean themTang(Tang tang) throws RemoteException;
}
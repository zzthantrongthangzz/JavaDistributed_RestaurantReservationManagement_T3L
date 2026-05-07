package rmi_interfaces;

import entity.ChucVu;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChucVu_Service extends Remote {
    public List<ChucVu> docDanhSachChucVu() throws RemoteException;
    public boolean themChucVu(ChucVu chucVu) throws RemoteException;
    public ChucVu timChucVuTheoTen(String tenCV) throws RemoteException;
    public String sinhMaChucVuTuDong() throws RemoteException;
}
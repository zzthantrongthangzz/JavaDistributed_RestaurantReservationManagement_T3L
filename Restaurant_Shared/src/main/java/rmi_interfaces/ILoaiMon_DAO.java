package rmi_interfaces;

import entity.LoaiMon;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ILoaiMon_DAO extends Remote {
    public List<LoaiMon> docDanhSachLoaiMon() throws RemoteException;
    public boolean themLoaiMon(LoaiMon loaiMon) throws RemoteException;
    public String sinhMaLoaiTuDong() throws RemoteException;
    public LoaiMon timLoaiTheoTen(String tenLoai) throws RemoteException;
}
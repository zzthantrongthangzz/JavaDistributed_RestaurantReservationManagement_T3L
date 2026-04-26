package rmi_interfaces;

import entity.BanAn;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IPhieuDatBan_Ban_DAO extends Remote {
    public List<BanAn> getDanhSachBanTheoPhieu(String maPhieu) throws RemoteException;
    public boolean themPhieuDatBan_Ban(String maPhieu, String maBan) throws RemoteException;
    public int demSoBanCuaPhieu(String maPhieu) throws RemoteException;
    public boolean xoaBanKhoiPhieu(String maPhieu, String maBan) throws RemoteException;
}
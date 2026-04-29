package rmi_interfaces;

import entity.ChiTietHoaDon;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChiTietHoaDon_Service extends Remote {
    public List<ChiTietHoaDon> getAllChiTietHoaDon() throws RemoteException;
    public List<ChiTietHoaDon> getChiTietTheoMaHoaDon(String maHoaDon) throws RemoteException;
    public boolean themChiTietHoaDon(ChiTietHoaDon cthd) throws RemoteException;
    public boolean capNhatChiTietHoaDon(ChiTietHoaDon cthd) throws RemoteException;
    public boolean xoaChiTietHoaDon(String maHoaDon, String maMon) throws RemoteException;
    public ChiTietHoaDon timChiTiet(String maHoaDon, String maMon) throws RemoteException;
    public boolean xoaChiTietTheoMaHoaDon(String maHoaDon) throws RemoteException;
}
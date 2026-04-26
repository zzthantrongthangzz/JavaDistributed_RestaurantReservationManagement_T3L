package rmi_interfaces;

import entity.HoaDon_Ban;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IHoaDon_Ban_DAO extends Remote {
    public boolean themHoaDon_Ban(String maHoaDon, String maBan) throws RemoteException;
    public boolean themHoaDon_Ban(HoaDon_Ban hdb) throws RemoteException;
    public boolean chuyenBan(String maHoaDon, String maBanCu, String maBanMoi) throws RemoteException;
    public List<String> layDanhSachMaBanTheoHoaDon(String maHoaDon) throws RemoteException;
    public boolean xoaHoaDon_Ban(String maHoaDon, String maBan) throws RemoteException;
    public boolean xoaTatCaBanCuaHoaDon(String maHoaDon) throws RemoteException;
}
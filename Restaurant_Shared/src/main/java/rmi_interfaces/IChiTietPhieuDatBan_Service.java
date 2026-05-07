package rmi_interfaces;

import entity.ChiTietPhieuDatBan;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChiTietPhieuDatBan_Service extends Remote {
    public boolean themChiTietPhieuDat(ChiTietPhieuDatBan ct) throws RemoteException;
    public List<ChiTietPhieuDatBan> getChiTietTheoPhieu(String maPhieuDatBan) throws RemoteException;
    public boolean xoaChiTietTheoPhieu(String maPhieuDatBan) throws RemoteException;
    public boolean capNhatChiTiet(ChiTietPhieuDatBan ct) throws RemoteException;
}
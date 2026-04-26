package rmi_interfaces;

import entity.NhanVien;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface INhanVien_DAO extends Remote {
    public ArrayList<NhanVien> getAllNhanVien() throws RemoteException;
    public ArrayList<NhanVien> getAllNhanVienDaNghi() throws RemoteException;
    public boolean khoiPhucNhanVien(String maNV) throws RemoteException;
    public NhanVien timMotNhanVienTheoMa(String maNhanVien) throws RemoteException;
    public ArrayList<NhanVien> timKiemNhanVienTheoMa(String maNhanVien) throws RemoteException;
    public ArrayList<NhanVien> timKiemNhanVienTheoTen(String hoTen) throws RemoteException;
    public ArrayList<NhanVien> timKiemNhanVienTheoSDT(String sdt) throws RemoteException;
    public boolean themNhanVien(NhanVien nv) throws RemoteException;
    public boolean capNhatNhanVien(NhanVien nv) throws RemoteException;
    public boolean xoaNhanVien(String maNV) throws RemoteException;
    public boolean xoaSachNhanVien(String maNV) throws RemoteException;
    public ArrayList<NhanVien> sapXepNhanVien(String orderBy) throws RemoteException;
    public ArrayList<NhanVien> locTheoGioiTinh(boolean gioiTinh) throws RemoteException;
    public String getMaNhanVienTiepTheo() throws RemoteException;
    public NhanVien xacThucDangNhap(String taiKhoan, String matKhau) throws RemoteException;
    public NhanVien getNhanVienByTaiKhoan(String taiKhoan) throws RemoteException;
}
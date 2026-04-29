package service;
import dao_impl.NhanVien_DAO;
import entity.NhanVien;
import rmi_interfaces.INhanVien_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class NhanVien_Service_Impl extends UnicastRemoteObject implements INhanVien_Service {
    private NhanVien_DAO dao;
    public NhanVien_Service_Impl() throws RemoteException {
        super();
        this.dao = new NhanVien_DAO();
    }
    @Override public ArrayList<NhanVien> getAllNhanVien() throws RemoteException { return dao.getAllNhanVien(); }
    @Override public ArrayList<NhanVien> getAllNhanVienDaNghi() throws RemoteException { return dao.getAllNhanVienDaNghi(); }
    @Override public boolean khoiPhucNhanVien(String maNV) throws RemoteException { return dao.khoiPhucNhanVien(maNV); }
    @Override public NhanVien timMotNhanVienTheoMa(String maNhanVien) throws RemoteException { return dao.timMotNhanVienTheoMa(maNhanVien); }
    @Override public ArrayList<NhanVien> timKiemNhanVienTheoMa(String maNhanVien) throws RemoteException { return dao.timKiemNhanVienTheoMa(maNhanVien); }
    @Override public ArrayList<NhanVien> timKiemNhanVienTheoTen(String hoTen) throws RemoteException { return dao.timKiemNhanVienTheoTen(hoTen); }
    @Override public ArrayList<NhanVien> timKiemNhanVienTheoSDT(String sdt) throws RemoteException { return dao.timKiemNhanVienTheoSDT(sdt); }
    @Override public boolean themNhanVien(NhanVien nv) throws RemoteException { return dao.themNhanVien(nv); }
    @Override public boolean capNhatNhanVien(NhanVien nv) throws RemoteException { return dao.capNhatNhanVien(nv); }
    @Override public boolean xoaNhanVien(String maNV) throws RemoteException { return dao.xoaNhanVien(maNV); }
    @Override public boolean xoaSachNhanVien(String maNV) throws RemoteException { return dao.xoaSachNhanVien(maNV); }
    @Override public ArrayList<NhanVien> sapXepNhanVien(String orderBy) throws RemoteException { return dao.sapXepNhanVien(orderBy); }
    @Override public ArrayList<NhanVien> locTheoGioiTinh(boolean gioiTinh) throws RemoteException { return dao.locTheoGioiTinh(gioiTinh); }
    @Override public String getMaNhanVienTiepTheo() throws RemoteException { return dao.getMaNhanVienTiepTheo(); }
    @Override public NhanVien xacThucDangNhap(String taiKhoan, String matKhau) throws RemoteException { return dao.xacThucDangNhap(taiKhoan, matKhau); }
    @Override public NhanVien getNhanVienByTaiKhoan(String taiKhoan) throws RemoteException { return dao.getNhanVienByTaiKhoan(taiKhoan); }
}
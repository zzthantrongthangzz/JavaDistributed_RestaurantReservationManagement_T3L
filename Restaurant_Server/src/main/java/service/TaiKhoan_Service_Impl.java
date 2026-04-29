package service;
import dao_impl.TaiKhoan_DAO;
import rmi_interfaces.ITaiKhoan_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class TaiKhoan_Service_Impl extends UnicastRemoteObject implements ITaiKhoan_Service {
    private TaiKhoan_DAO dao;
    public TaiKhoan_Service_Impl() throws RemoteException {
        super();
        this.dao = new TaiKhoan_DAO();
    }
    @Override public boolean themTaiKhoan(String maNhanVien, String tenDangNhap, String matKhau, String quyen) throws RemoteException { return dao.themTaiKhoan(maNhanVien, tenDangNhap, matKhau, quyen); }
    @Override public List<String> docDanhSachMaTaiKhoan() throws RemoteException { return dao.docDanhSachMaTaiKhoan(); }
    @Override public boolean kiemTraTenDangNhapTonTai(String tenDangNhap) throws RemoteException { return dao.kiemTraTenDangNhapTonTai(tenDangNhap); }
    @Override public String getMatKhauMaHoa(String tenDangNhap) throws RemoteException { return dao.getMatKhauMaHoa(tenDangNhap); }
    @Override public String layEmailTheoTenDangNhap(String tenDangNhap) throws RemoteException { return dao.layEmailTheoTenDangNhap(tenDangNhap); }
    @Override public String taoDuMaXacThuc() throws RemoteException { return dao.taoDuMaXacThuc(); }
    @Override public boolean guiEmailXacThuc(String email, String maXacThuc, String tenDangNhap) throws RemoteException { return dao.guiEmailXacThuc(email, maXacThuc, tenDangNhap); }
    @Override public boolean capNhatMatKhauMoi(String tenDangNhap, String matKhauMoi) throws RemoteException { return dao.capNhatMatKhauMoi(tenDangNhap, matKhauMoi); }
}
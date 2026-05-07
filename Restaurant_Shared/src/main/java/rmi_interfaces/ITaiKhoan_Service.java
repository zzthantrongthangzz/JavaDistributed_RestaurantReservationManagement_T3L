package rmi_interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ITaiKhoan_Service extends Remote {
    public boolean themTaiKhoan(String maNhanVien, String tenDangNhap, String matKhau, String quyen) throws RemoteException;
    public List<String> docDanhSachMaTaiKhoan() throws RemoteException;
    public boolean kiemTraTenDangNhapTonTai(String tenDangNhap) throws RemoteException;
    public String getMatKhauMaHoa(String tenDangNhap) throws RemoteException;
    public String layEmailTheoTenDangNhap(String tenDangNhap) throws RemoteException;
    public String taoDuMaXacThuc() throws RemoteException;
    public boolean guiEmailXacThuc(String email, String maXacThuc, String tenDangNhap) throws RemoteException;
    public boolean capNhatMatKhauMoi(String tenDangNhap, String matKhauMoi) throws RemoteException;
}
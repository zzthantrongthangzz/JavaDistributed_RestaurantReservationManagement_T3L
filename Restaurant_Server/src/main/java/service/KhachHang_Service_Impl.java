package service;
import dao_impl.KhachHang_DAO;
import entity.KhachHang;
import rmi_interfaces.IKhachHang_Service;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class KhachHang_Service_Impl extends UnicastRemoteObject implements IKhachHang_Service {
    private KhachHang_DAO dao;
    public KhachHang_Service_Impl() throws RemoteException {
        super();
        this.dao = new KhachHang_DAO();
    }
    @Override public String phatSinhMaKhachHang() throws RemoteException { return dao.phatSinhMaKhachHang(); }
    @Override public List<KhachHang> docDanhSachKhachHang() throws RemoteException { return dao.docDanhSachKhachHang(); }
    @Override public List<KhachHang> getKhachHangDaXoa() throws RemoteException { return dao.getKhachHangDaXoa(); }
    @Override public List<KhachHang> docDanhSachKhachHangDaXoa() throws RemoteException { return dao.docDanhSachKhachHangDaXoa(); }
    @Override public boolean themKhachHang(KhachHang kh) throws RemoteException { return dao.themKhachHang(kh); }
    @Override public boolean capNhatKhachHang(KhachHang kh) throws RemoteException { return dao.capNhatKhachHang(kh); }
    @Override public boolean xoaKhachHang(String ma) throws RemoteException { return dao.xoaKhachHang(ma); }
    @Override public boolean khoiPhucKhachHang(String ma) throws RemoteException { return dao.khoiPhucKhachHang(ma); }
    @Override public List<KhachHang> timKiemTheoMa(String ma) throws RemoteException { return dao.timKiemTheoMa(ma); }
    @Override public List<KhachHang> timKiemTheoTen(String ten) throws RemoteException { return dao.timKiemTheoTen(ten); }
    @Override public List<KhachHang> timKiemTheoSDT(String sdt) throws RemoteException { return dao.timKiemTheoSDT(sdt); }
    @Override public KhachHang timKhachHangTheoSDT(String sdt) throws RemoteException { return dao.timKhachHangTheoSDT(sdt); }
    @Override public List<KhachHang> locKhachHangTheoGioiTinh(boolean gt) throws RemoteException { return dao.locKhachHangTheoGioiTinh(gt); }
    @Override public List<KhachHang> sapXepTheoTen(boolean tangDan) throws RemoteException { return dao.sapXepTheoTen(tangDan); }
    @Override public List<KhachHang> sapXepTheoDiem(boolean tangDan) throws RemoteException { return dao.sapXepTheoDiem(tangDan); }
    @Override public boolean capNhatDiemTichLuy(String ma, int diem) throws RemoteException { return dao.capNhatDiemTichLuy(ma, diem); }
    @Override public int getTongSoKhachHang() throws RemoteException { return dao.getTongSoKhachHang(); }
    @Override public int getTongSoKhachHang(Date tu, Date den) throws RemoteException { return dao.getTongSoKhachHang(tu, den); }
    @Override public int getTongDiemTichLuy(Date tu, Date den) throws RemoteException { return dao.getTongDiemTichLuy(tu, den); }
    @Override public Map<String, Integer> getSoLuongKhachHangTheoGioiTinh() throws RemoteException { return dao.getSoLuongKhachHangTheoGioiTinh(); }
    @Override public BigDecimal getTongChiTieuTatCaKhachHang(Date tu, Date den) throws RemoteException { return dao.getTongChiTieuTatCaKhachHang(tu, den); }
    @Override public Map<String, BigDecimal> getTopKhachHangTheoChiTieu(int topN) throws RemoteException { return dao.getTopKhachHangTheoChiTieu(topN); }
    @Override public List<dto.ThongKeKhachHangDTO> getTopKhachHangDayDu(int topN, Date tu, Date den) throws RemoteException { return dao.getTopKhachHangDayDu(topN, tu, den); }
}
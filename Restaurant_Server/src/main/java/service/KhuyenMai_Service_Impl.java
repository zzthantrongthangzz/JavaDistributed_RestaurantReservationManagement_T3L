package service;
import dao_impl.KhuyenMai_DAO;
import entity.KhuyenMai;
import rmi_interfaces.IKhuyenMai_Service;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class KhuyenMai_Service_Impl extends UnicastRemoteObject implements IKhuyenMai_Service {
    private KhuyenMai_DAO dao;
    public KhuyenMai_Service_Impl() throws RemoteException {
        super();
        this.dao = new KhuyenMai_DAO();
    }
    @Override public List<KhuyenMai> getAllList() throws RemoteException { return dao.getAllList(); }
    @Override public List<KhuyenMai> getAllListSorted(String sortBy) throws RemoteException { return dao.getAllListSorted(sortBy); }
    @Override public List<KhuyenMai> getByLoaiKhuyenMai(String loaiKM) throws RemoteException { return dao.getByLoaiKhuyenMai(loaiKM); }
    @Override public KhuyenMai getByMaKhuyenMai(String maKM) throws RemoteException { return dao.getByMaKhuyenMai(maKM); }
    @Override public boolean themKhuyenMai(KhuyenMai km) throws RemoteException { return dao.themKhuyenMai(km); }
    @Override public boolean capNhatKhuyenMai(KhuyenMai km) throws RemoteException { return dao.capNhatKhuyenMai(km); }
    @Override public boolean anKhuyenMai(String maKM) throws RemoteException { return dao.anKhuyenMai(maKM); }
    @Override public List<KhuyenMai> getKhuyenMaiHetHan() throws RemoteException { return dao.getKhuyenMaiHetHan(); }
    @Override public List<KhuyenMai> timKiem(String tuKhoa) throws RemoteException { return dao.timKiem(tuKhoa); }
    @Override public String taoMaKhuyenMaiTuDong() throws RemoteException { return dao.taoMaKhuyenMaiTuDong(); }
    @Override public boolean kiemTraMaTonTai(String maKM) throws RemoteException { return dao.kiemTraMaTonTai(maKM); }
    @Override public int getTongLuotSuDung(Date tuNgay, Date denNgay) throws RemoteException { return dao.getTongLuotSuDung(tuNgay, denNgay); }
    @Override public BigDecimal getTongTienGiam(Date tuNgay, Date denNgay) throws RemoteException { return dao.getTongTienGiam(tuNgay, denNgay); }
    @Override public Map<Date, BigDecimal> getTienGiamTheoNgay(Date tuNgay, Date denNgay) throws RemoteException { return dao.getTienGiamTheoNgay(tuNgay, denNgay); }
    @Override public List<KhuyenMai> locDanhSach(String maKM, String tenKM, String loaiKM, String giaTriFilter, Date tuNgay, Date denNgay, String sapXep) throws RemoteException { return dao.locDanhSach(maKM, tenKM, loaiKM, giaTriFilter, tuNgay, denNgay, sapXep); }
    @Override public List<dto.ThongKeKhuyenMaiDTO> getThongKeChiTietKhuyenMai(Date tuNgay, Date denNgay) throws RemoteException { return dao.getThongKeChiTietKhuyenMai(tuNgay, denNgay); }
}
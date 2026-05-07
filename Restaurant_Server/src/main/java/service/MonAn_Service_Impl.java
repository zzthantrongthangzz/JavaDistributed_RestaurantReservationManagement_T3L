package service;
import dao_impl.MonAn_DAO;
import entity.LichSuGia;
import entity.MonAn;
import rmi_interfaces.IMonAn_Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;

public class MonAn_Service_Impl extends UnicastRemoteObject implements IMonAn_Service {
    private MonAn_DAO dao;
    public MonAn_Service_Impl() throws RemoteException {
        super();
        this.dao = new MonAn_DAO();
    }
    @Override public List<MonAn> docDanhSachMon() throws RemoteException { return dao.docDanhSachMon(); }
    @Override public List<MonAn> timKiemMonAn(String tuKhoa) throws RemoteException { return dao.timKiemMonAn(tuKhoa); }
    @Override public List<MonAn> timKiemTheoMa(String maMon) throws RemoteException { return dao.timKiemTheoMa(maMon); }
    @Override public List<MonAn> timKiemTheoTen(String tenMon) throws RemoteException { return dao.timKiemTheoTen(tenMon); }
    @Override public List<MonAn> locMonAnTheoLoai(String tenLoai) throws RemoteException { return dao.locMonAnTheoLoai(tenLoai); }
    @Override public String sinhMaMonTuDong() throws RemoteException { return dao.sinhMaMonTuDong(); }
    @Override public boolean themMonAn(MonAn monAn) throws RemoteException { return dao.themMonAn(monAn); }
    @Override public MonAn timMotMonTheoMa(String maMon) throws RemoteException { return dao.timMotMonTheoMa(maMon); }
    @Override public boolean xoaMem(String maMon) throws RemoteException { return dao.xoaMem(maMon); }
    @Override public boolean capNhatMonAn(MonAn monAn, String maNhanVienThucHien) throws RemoteException { return dao.capNhatMonAn(monAn, maNhanVienThucHien); }
    @Override
    public List<dto.ThongKeMonAnDTO> getThongKeMonAn(Date tuNgay, Date denNgay) throws RemoteException {
        return dao.getThongKeMonAn(tuNgay, denNgay);
    }    @Override public boolean themDanhSachMonAn(List<MonAn> danhSachMon) throws RemoteException { return dao.themDanhSachMonAn(danhSachMon); }
    @Override public List<LichSuGia> getLichSuGia(String maMon) throws RemoteException { return dao.getLichSuGia(maMon); }
}
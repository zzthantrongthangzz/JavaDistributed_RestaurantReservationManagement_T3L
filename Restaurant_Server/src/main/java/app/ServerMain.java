package app;

import dao_impl.*;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            // Mở cổng RMI 1099
            LocateRegistry.createRegistry(1099);

// ==========================================
// 1. Khởi tạo các DAO Impl
// ==========================================
            BanAn_DAO_Impl banAnDao = new BanAn_DAO_Impl();
            NhanVien_DAO_Impl nhanVienDAO = new NhanVien_DAO_Impl();
            ChucVu_DAO_Impl chucVuDao = new ChucVu_DAO_Impl();
            TaiKhoan_DAO_Impl taiKhoanDao = new TaiKhoan_DAO_Impl();
            KhuyenMai_DAO_Impl khuyenMaiDAO = new KhuyenMai_DAO_Impl();
            KhachHang_DAO_Impl khachHangDao = new KhachHang_DAO_Impl();
            HoaDon_DAO_Impl hoaDonDao = new HoaDon_DAO_Impl();
            HoaDon_Ban_DAO_Impl hoaDonBanDao = new HoaDon_Ban_DAO_Impl();
            ChiTietHoaDon_DAO_Impl chiTietHoaDonDao = new ChiTietHoaDon_DAO_Impl();
            MonAn_DAO_Impl monAnDao = new MonAn_DAO_Impl();

// ==========================================
// 2. Đăng ký các service lên mạng để Client tìm kiếm (Lookup)
// ==========================================
            Naming.rebind("rmi://localhost:1099/BanAn_DAO", banAnDao);
            Naming.rebind("rmi://localhost:1099/NhanVien_DAO", nhanVienDAO);
            Naming.rebind("rmi://localhost:1099/ChucVu_DAO", chucVuDao);
            Naming.rebind("rmi://localhost:1099/TaiKhoan_DAO", taiKhoanDao);
            Naming.rebind("rmi://localhost:1099/KhuyenMai_DAO", khuyenMaiDAO);
            Naming.rebind("rmi://localhost:1099/KhachHang_DAO", khachHangDao);
            Naming.rebind("rmi://localhost:1099/HoaDon_DAO", hoaDonDao);
            Naming.rebind("rmi://localhost:1099/HoaDon_Ban_DAO", hoaDonBanDao);
            Naming.rebind("rmi://localhost:1099/ChiTietHoaDon_DAO", chiTietHoaDonDao);
            Naming.rebind("rmi://localhost:1099/MonAn_DAO", monAnDao);



            System.out.println("RMI Server is running on port 1099...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
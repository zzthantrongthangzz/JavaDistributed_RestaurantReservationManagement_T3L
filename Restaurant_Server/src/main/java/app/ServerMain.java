package app;

import service.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServerMain {
    public static void main(String[] args) {
        try {

            LocateRegistry.createRegistry(1099);

            BanAn_Service_Impl banAnService = new BanAn_Service_Impl();
            ChiTietHoaDon_Service_Impl chiTietHoaDonService = new ChiTietHoaDon_Service_Impl();
            ChiTietPhieuDatBan_Service_Impl chiTietPhieuDatBanService = new ChiTietPhieuDatBan_Service_Impl();
            ChucVu_Service_Impl chucVuService = new ChucVu_Service_Impl();
            HoaDon_Ban_Service_Impl hoaDonBanService = new HoaDon_Ban_Service_Impl();
            HoaDon_Service_Impl hoaDonService = new HoaDon_Service_Impl();
            KhachHang_Service_Impl khachHangService = new KhachHang_Service_Impl();
            Khu_Service_Impl khuService = new Khu_Service_Impl();
            KhuyenMai_Service_Impl khuyenMaiService = new KhuyenMai_Service_Impl();
            LichSuHuyDatBan_Service_Impl lichSuHuyDatBanService = new LichSuHuyDatBan_Service_Impl();
            LoaiMon_Service_Impl loaiMonService = new LoaiMon_Service_Impl();
            MonAn_Service_Impl monAnService = new MonAn_Service_Impl();
            NhanVien_Service_Impl nhanVienService = new NhanVien_Service_Impl();
            PhieuDatBan_Ban_Service_Impl phieuDatBanBanService = new PhieuDatBan_Ban_Service_Impl();
            PhieuDatBan_Service_Impl phieuDatBanService = new PhieuDatBan_Service_Impl();
            TaiKhoan_Service_Impl taiKhoanService = new TaiKhoan_Service_Impl();
            Tang_Service_Impl tangService = new Tang_Service_Impl();

            Naming.rebind("rmi://localhost:1099/BanAnService", banAnService);
            Naming.rebind("rmi://localhost:1099/ChiTietHoaDonService", chiTietHoaDonService);
            Naming.rebind("rmi://localhost:1099/ChiTietPhieuDatBanService", chiTietPhieuDatBanService);
            Naming.rebind("rmi://localhost:1099/ChucVuService", chucVuService);
            Naming.rebind("rmi://localhost:1099/HoaDonBanService", hoaDonBanService);
            Naming.rebind("rmi://localhost:1099/HoaDonService", hoaDonService);
            Naming.rebind("rmi://localhost:1099/KhachHangService", khachHangService);
            Naming.rebind("rmi://localhost:1099/KhuService", khuService);
            Naming.rebind("rmi://localhost:1099/KhuyenMaiService", khuyenMaiService);
            Naming.rebind("rmi://localhost:1099/LichSuHuyDatBanService", lichSuHuyDatBanService);
            Naming.rebind("rmi://localhost:1099/LoaiMonService", loaiMonService);
            Naming.rebind("rmi://localhost:1099/MonAnService", monAnService);
            Naming.rebind("rmi://localhost:1099/NhanVienService", nhanVienService);
            Naming.rebind("rmi://localhost:1099/PhieuDatBanBanService", phieuDatBanBanService);
            Naming.rebind("rmi://localhost:1099/PhieuDatBanService", phieuDatBanService);
            Naming.rebind("rmi://localhost:1099/TaiKhoanService", taiKhoanService);
            Naming.rebind("rmi://localhost:1099/TangService", tangService);

            System.out.println("RMI Server is running on port 1099...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
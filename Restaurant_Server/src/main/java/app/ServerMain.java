package app;

import service.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.io.FileInputStream;
import java.util.Properties;

public class ServerMain {
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("config.properties")) {
                props.load(in);
            } catch (Exception e) {
                System.err.println("Cảnh báo: Không tìm thấy file config.properties, hệ thống sẽ dùng IP mặc định.");
            }

            String serverIp = props.getProperty("server.ip", "192.168.10.73");
            String serverPort = props.getProperty("server.port", "1099");

            System.setProperty("java.rmi.server.hostname", serverIp);

            LocateRegistry.createRegistry(Integer.parseInt(serverPort));

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

            String rmiURL = "rmi://" + serverIp + ":" + serverPort + "/";

            Naming.rebind(rmiURL + "BanAn_Service", banAnService);
            Naming.rebind(rmiURL + "ChiTietHoaDon_Service", chiTietHoaDonService);
            Naming.rebind(rmiURL + "ChiTietPhieuDatBan_Service", chiTietPhieuDatBanService);
            Naming.rebind(rmiURL + "ChucVu_Service", chucVuService);
            Naming.rebind(rmiURL + "HoaDon_Ban_Service", hoaDonBanService);
            Naming.rebind(rmiURL + "HoaDon_Service", hoaDonService);
            Naming.rebind(rmiURL + "KhachHang_Service", khachHangService);
            Naming.rebind(rmiURL + "Khu_Service", khuService);
            Naming.rebind(rmiURL + "KhuyenMai_Service", khuyenMaiService);
            Naming.rebind(rmiURL + "LichSuHuyDatBan_Service", lichSuHuyDatBanService);
            Naming.rebind(rmiURL + "LoaiMon_Service", loaiMonService);
            Naming.rebind(rmiURL + "MonAn_Service", monAnService);
            Naming.rebind(rmiURL + "NhanVien_Service", nhanVienService);
            Naming.rebind(rmiURL + "PhieuDatBan_Ban_Service", phieuDatBanBanService);
            Naming.rebind(rmiURL + "PhieuDatBan_Service", phieuDatBanService);
            Naming.rebind(rmiURL + "TaiKhoan_Service", taiKhoanService);
            Naming.rebind(rmiURL + "Tang_Service", tangService);

            System.out.println("✅ RMI Server is running successfully at: " + rmiURL);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi khởi động Server RMI!");
        }
    }
}
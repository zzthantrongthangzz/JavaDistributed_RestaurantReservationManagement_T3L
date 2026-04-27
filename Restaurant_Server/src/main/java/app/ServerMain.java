package app;

import dao_impl.BanAn_DAO_Impl;
import dao_impl.NhanVien_DAO_Impl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            // Mở cổng RMI 1099
            LocateRegistry.createRegistry(1099);

            // Khởi tạo các DAO Impl
            BanAn_DAO_Impl banAnService = new BanAn_DAO_Impl();
            NhanVien_DAO_Impl nhanVienDAO=new NhanVien_DAO_Impl();
            // Khởi tạo thêm NhanVien_DAO_Impl, MonAn_DAO_Impl...

            // Đăng ký các service lên mạng để Client tìm kiếm (Lookup)
            Naming.rebind("rmi://localhost:1099/BanAnService", banAnService);
            Naming.rebind("rmi://localhost:1099/NhanVien_DAO", nhanVienDAO);
            System.out.println("RMI Server is running on port 1099...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
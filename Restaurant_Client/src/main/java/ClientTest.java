import entity.BanAn;
import rmi_interfaces.IBanAn_Service;
import java.rmi.Naming;
import java.util.List;

public class ClientTest {
    public static void main(String[] args) {
        try {
            System.out.println("Đang kết nối đến Server...");

            // 1. Tìm kiếm dịch vụ BanAnService trên Server qua cổng 1099
            IBanAn_Service banAnService = (IBanAn_Service) Naming.lookup("rmi://localhost:1099/BanAnService");

            // 2. Gọi đúng tên hàm docDanhSachBan() có trong IBanAn_DAO / BanAn_DAO_Impl
            List<BanAn> danhSachBan = banAnService.docDanhSachBan();

            // 3. In kết quả ra màn hình
            System.out.println("Kết nối thành công! Đã lấy được " + danhSachBan.size() + " bàn ăn từ Neo4j.");
            for (BanAn b : danhSachBan) {
                System.out.println(" - " + b.getMaBan() + " | " + b.getTenBan() + " | " + b.getTrangThai());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
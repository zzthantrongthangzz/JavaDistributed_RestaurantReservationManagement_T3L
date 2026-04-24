package dao;

public class RunMigration {
    // Luồng thực thi chính để chuyển đổi mật khẩu hệ thống
    public static void main(String[] args) {
        TaiKhoan_DAO tk = new TaiKhoan_DAO();
        System.out.println("CẢNH BÁO: CHUẨN BỊ CHUYỂN ĐỔI MẬT KHẨU CSDL");
        
        try {
            Thread.sleep(5000); 
        } catch (InterruptedException e) {
            return;
        }
        
        try {
            tk.chuyenDoiTatCaMatKhau();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Script đã chạy xong.");
    }
}
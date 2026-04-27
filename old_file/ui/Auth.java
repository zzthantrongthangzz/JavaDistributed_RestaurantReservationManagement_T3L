package ui;

import entity.NhanVien;

public class Auth {
    private static NhanVien nhanVienDangNhap = null;

    // Xóa thông tin nhân viên đang đăng nhập (Đăng xuất)
    public static void logout() {
        Auth.nhanVienDangNhap = null;
    }

    // Lưu thông tin nhân viên vừa đăng nhập thành công
    public static void login(NhanVien nv) {
        Auth.nhanVienDangNhap = nv;
    }

    // Kiểm tra xem đã có nhân viên đăng nhập hay chưa
    public static boolean isLogin() {
        return Auth.nhanVienDangNhap != null;
    }

    // Lấy đối tượng nhân viên đang đăng nhập hiện tại
    public static NhanVien getCurrentNhanVien() {
        return Auth.nhanVienDangNhap;
    }
}
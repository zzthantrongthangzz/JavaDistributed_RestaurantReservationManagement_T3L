package ui;
import entity.NhanVien;
public class Auth {
    private static NhanVien nhanVienDangNhap = null;
    public static void logout() {
        Auth.nhanVienDangNhap = null;
    }
    public static void login(NhanVien nv) {
        Auth.nhanVienDangNhap = nv;
    }
    public static boolean isLogin() {
        return Auth.nhanVienDangNhap != null;
    }
    public static NhanVien getCurrentNhanVien() {
        return Auth.nhanVienDangNhap;
    }
}
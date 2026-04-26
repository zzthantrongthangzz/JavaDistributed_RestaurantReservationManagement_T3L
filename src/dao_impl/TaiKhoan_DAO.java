package dao_impl;

import connect.DBConnect;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.sql.Connection;

public class TaiKhoan_DAO {
    // Mã hóa mật khẩu với Salt và SHA-256
    public static String maHoaMatKhau(String matKhau) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(matKhau.getBytes());

            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);

            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi mã hoá mật khẩu: " + e.getMessage());
        }
    }

    // Kiểm tra mật khẩu nhập vào với mật khẩu đã mã hóa
    public static boolean kiemTraMatKhau(String matKhauNhap, String matKhauMaHoa) {
        try {
            byte[] saltAndHash = Base64.getDecoder().decode(matKhauMaHoa);
            byte[] salt = new byte[16];
            System.arraycopy(saltAndHash, 0, salt, 0, 16);

            byte[] hash = new byte[saltAndHash.length - 16];
            System.arraycopy(saltAndHash, 16, hash, 0, hash.length);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedInput = md.digest(matKhauNhap.getBytes());

            return MessageDigest.isEqual(hash, hashedInput);
        } catch (Exception e) {
            return false;
        }
    }

    // Thêm tài khoản mới vào hệ thống
    public boolean themTaiKhoan(String maNhanVien, String tenDangNhap, String matKhau, String quyen) {
        try {
            String matKhauMaHoa = maHoaMatKhau(matKhau);
            java.sql.Date ngayTaoTK = new java.sql.Date(System.currentTimeMillis());
            String sql = "INSERT INTO TaiKhoan (maNhanVien, taiKhoan, matKhau, ngayTaoTK) VALUES (?, ?, ?, ?)";
            
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, maNhanVien);
                pstmt.setString(2, tenDangNhap);
                pstmt.setString(3, matKhauMaHoa);
                pstmt.setDate(4, ngayTaoTK);
                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Lỗi thêm tài khoản: " + e.getMessage());
            return false;
        }
    }

    // Lấy danh sách mã tài khoản
    public List<String> docDanhSachMaTaiKhoan() {
        List<String> danhSach = new ArrayList<>();
        String sql = "SELECT maTK FROM TaiKhoan";
        try (Connection conn = DBConnect.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                danhSach.add(rs.getString("maTK"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi đọc danh sách tài khoản: " + e.getMessage());
        }
        return danhSach;
    }

    // Kiểm tra tên đăng nhập đã tồn tại chưa
    public boolean kiemTraTenDangNhapTonTai(String tenDangNhap) {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE taiKhoan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tenDangNhap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra tên đăng nhập: " + e.getMessage());
        }
        return false;
    }
    
    // Lấy chuỗi mật khẩu đã mã hóa từ database
    public String getMatKhauMaHoa(String tenDangNhap) {
        String sql = "SELECT matKhau FROM TaiKhoan WHERE taiKhoan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tenDangNhap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("matKhau");
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy mật khẩu mã hoá: " + e.getMessage());
        }
        return null;
    }

    // Quét và chuyển đổi toàn bộ mật khẩu cũ sang dạng mã hóa mới
    public static void chuyenDoiTatCaMatKhau() {
        String sqlSelect = "SELECT taiKhoan, matKhau FROM TaiKhoan";
        String sqlUpdate = "UPDATE TaiKhoan SET matKhau = ? WHERE taiKhoan = ?";
        java.util.Map<String, String> taiKhoanCanCapNhat = new java.util.HashMap<>();

        try (Connection conn = DBConnect.getConnection();
             Statement stmtSelect = conn.createStatement();
             ResultSet rs = stmtSelect.executeQuery(sqlSelect)) {

            while (rs.next()) {
                String tenDangNhap = rs.getString("taiKhoan");
                String matKhauHienTai = rs.getString("matKhau");
                if (matKhauHienTai == null || matKhauHienTai.isEmpty()) continue;

                boolean daMaHoa = false;
                try {
                    byte[] decoded = Base64.getDecoder().decode(matKhauHienTai);
                    if (decoded.length == 48) daMaHoa = true;
                } catch (IllegalArgumentException e) {
                    daMaHoa = false;
                }

                if (!daMaHoa) taiKhoanCanCapNhat.put(tenDangNhap, matKhauHienTai);
            }

            if (!taiKhoanCanCapNhat.isEmpty()) {
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    for (java.util.Map.Entry<String, String> entry : taiKhoanCanCapNhat.entrySet()) {
                        pstmtUpdate.setString(1, maHoaMatKhau(entry.getValue()));
                        pstmtUpdate.setString(2, entry.getKey());
                        pstmtUpdate.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("LỖI CHUYỂN ĐỔI: " + e.getMessage());
        }
    }
    
    // Lấy email nhân viên dựa trên tên đăng nhập
    public String layEmailTheoTenDangNhap(String tenDangNhap) {
        String sql = "SELECT n.email FROM NhanVien n INNER JOIN TaiKhoan t ON n.maNhanVien = t.maNhanVien WHERE t.taiKhoan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tenDangNhap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("email");
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy email: " + e.getMessage());
        }
        return null;
    }

    // Tạo mã xác thực ngẫu nhiên 6 chữ số
    public String taoDuMaXacThuc() {
        return String.format("%06d", new java.util.Random().nextInt(1000000));
    }

    // Gửi email chứa mã xác thực đặt lại mật khẩu
    public boolean guiEmailXacThuc(String email, String maXacThuc, String tenDangNhap) {
        try {
            String fromEmail = "nvtan107@gmail.com";
            String password = "occx ilgk angk gaqk";
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Mã xác thực đặt lại mật khẩu - Nhà Hàng T3L");
            message.setText("Tài khoản: " + tenDangNhap + "\nMã xác thực: " + maXacThuc);

            Transport.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi gửi email: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật mật khẩu mới (đã mã hóa) cho tài khoản
    public boolean capNhatMatKhauMoi(String tenDangNhap, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE taiKhoan = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maHoaMatKhau(matKhauMoi));
            pstmt.setString(2, tenDangNhap);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật mật khẩu: " + e.getMessage());
            return false;
        }
    }
}
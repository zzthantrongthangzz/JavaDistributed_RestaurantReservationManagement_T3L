package dao_impl;

import connect.DBConnect;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class TaiKhoan_DAO {

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
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }

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
        } catch (Exception e) { return false; }
    }

    public boolean themTaiKhoan(String maNhanVien, String tenDangNhap, String matKhau, String quyen) {
        String matKhauMaHoa = maHoaMatKhau(matKhau);
        long ngayTao = System.currentTimeMillis();
        String cypher = "MATCH (nv:NhanVien {maNhanVien: $maNV}) " +
                "CREATE (tk:TaiKhoan {taiKhoan: $tk, matKhau: $mk, ngayTaoTK: $ngay})<-[:CO_TAI_KHOAN]-(nv)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maNV", maNhanVien, "tk", tenDangNhap, "mk", matKhauMaHoa, "ngay", ngayTao));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<String> docDanhSachMaTaiKhoan() {
        List<String> ds = new ArrayList<>();
        String cypher = "MATCH (tk:TaiKhoan) RETURN tk.taiKhoan AS tk";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(result.next().get("tk").asString());
        }
        return ds;
    }

    public boolean kiemTraTenDangNhapTonTai(String tenDangNhap) {
        String cypher = "MATCH (tk:TaiKhoan {taiKhoan: $tk}) RETURN count(tk) AS sl";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tk", tenDangNhap));
            if (result.hasNext()) return result.next().get("sl").asInt() > 0;
        }
        return false;
    }

    public String getMatKhauMaHoa(String tenDangNhap) {
        String cypher = "MATCH (tk:TaiKhoan {taiKhoan: $tk}) RETURN tk.matKhau AS mk";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tk", tenDangNhap));
            if (result.hasNext()) return result.next().get("mk").asString();
        }
        return null;
    }

    public String layEmailTheoTenDangNhap(String tenDangNhap) {
        String cypher = "MATCH (nv:NhanVien)-[:CO_TAI_KHOAN]->(tk:TaiKhoan {taiKhoan: $tk}) RETURN nv.email AS email";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tk", tenDangNhap));
            if (result.hasNext()) return result.next().get("email").asString();
        }
        return null;
    }

    public String taoDuMaXacThuc() {
        return String.format("%06d", new java.util.Random().nextInt(1000000));
    }

    public boolean guiEmailXacThuc(String email, String maXacThuc, String tenDangNhap) {
        try {
            String fromEmail = "nvtan107@gmail.com";
            String password = "occx ilgk angk gaqk";
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            jakarta.mail.Session mailSession = jakarta.mail.Session.getInstance(props, new jakarta.mail.Authenticator() {
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(fromEmail, password);
                }
            });

            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Mã xác thực đặt lại mật khẩu - Nhà Hàng T3L");
            message.setText("Tài khoản: " + tenDangNhap + "\nMã xác thực: " + maXacThuc);

            Transport.send(message);
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean capNhatMatKhauMoi(String tenDangNhap, String matKhauMoi) {
        String cypher = "MATCH (tk:TaiKhoan {taiKhoan: $tk}) SET tk.matKhau = $mk";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("tk", tenDangNhap, "mk", maHoaMatKhau(matKhauMoi)));
            return true;
        } catch (Exception e) { return false; }
    }
}
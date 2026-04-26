package dao_impl;

import connect.DBConnect;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RunMigration {
    // Luồng thực thi chính để chuyển đổi mật khẩu hệ thống trên Neo4j
    public static void main(String[] args) {
        System.out.println("CẢNH BÁO: CHUẨN BỊ CHUYỂN ĐỔI MẬT KHẨU CSDL NEO4J");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            return;
        }

        chuyenDoiTatCaMatKhauNeo4j();
        System.out.println("Script đã chạy xong.");
    }

    public static void chuyenDoiTatCaMatKhauNeo4j() {
        String cypherSelect = "MATCH (tk:TaiKhoan) RETURN tk.taiKhoan AS tk, tk.matKhau AS mk";
        String cypherUpdate = "MATCH (tk:TaiKhoan {taiKhoan: $tk}) SET tk.matKhau = $mkCrypted";
        Map<String, String> taiKhoanCanCapNhat = new HashMap<>();

        try (Session session = DBConnect.getSession()) {
            Result rs = session.run(cypherSelect);
            while (rs.hasNext()) {
                Record record = rs.next();
                String tenDangNhap = record.get("tk").asString();
                String matKhauHienTai = record.get("mk").asString();

                if (matKhauHienTai == null || matKhauHienTai.isEmpty()) continue;

                boolean daMaHoa = false;
                try {
                    byte[] decoded = Base64.getDecoder().decode(matKhauHienTai);
                    if (decoded.length == 48) daMaHoa = true;
                } catch (IllegalArgumentException e) {
                    daMaHoa = false;
                }

                if (!daMaHoa) {
                    taiKhoanCanCapNhat.put(tenDangNhap, matKhauHienTai);
                }
            }

            if (!taiKhoanCanCapNhat.isEmpty()) {
                for (Map.Entry<String, String> entry : taiKhoanCanCapNhat.entrySet()) {
                    session.run(cypherUpdate, Values.parameters(
                            "tk", entry.getKey(),
                            "mkCrypted", TaiKhoan_DAO_Impl.maHoaMatKhau(entry.getValue())
                    ));
                }
                System.out.println("Đã mã hóa thành công " + taiKhoanCanCapNhat.size() + " tài khoản.");
            } else {
                System.out.println("Tất cả tài khoản đều đã được mã hóa chuẩn SHA-256.");
            }
        } catch (Exception e) {
            System.err.println("LỖI CHUYỂN ĐỔI: " + e.getMessage());
        }
    }
}
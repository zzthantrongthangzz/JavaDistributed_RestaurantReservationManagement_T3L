package dao_impl;

import connect.DBConnect;
import entity.Tang;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.List;

public class Tang_DAO {

    public List<String> docDanhSachTenTang() {
        List<String> danhSachTang = new ArrayList<>();
        danhSachTang.add("Tất cả");
        String cypher = "MATCH (t:Tang) RETURN DISTINCT t.tenTang AS tenTang ORDER BY tenTang";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                danhSachTang.add(result.next().get("tenTang").asString());
            }
        }
        return danhSachTang;
    }

    // ĐÃ FIX: Thêm trim() vào thuộc tính để so sánh chính xác
    public Tang timTangTheoTen(String tenTang) {
        String cypher = "MATCH (t:Tang) WHERE trim(t.tenTang) = trim($ten) RETURN t.maTang AS maTang, t.tenTang AS tenTang";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ten", tenTang));
            if (result.hasNext()) {
                Record r = result.next();
                return new Tang(r.get("maTang").asString(), r.get("tenTang").asString());
            }
        }
        return null;
    }

    public String sinhMaTangTuDong() {
        String cypher = "MATCH (t:Tang) RETURN count(t) + 1 AS soThuTu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            if (result.hasNext()) {
                return String.format("T%02d", result.next().get("soThuTu").asInt());
            }
        }
        return "T01";
    }

    public boolean themTang(Tang tang) {
        // ĐÃ FIX: Trim thuộc tính trước khi tạo mới
        String cypher = "CREATE (t:Tang {maTang: trim($ma), tenTang: trim($ten)})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", tang.getMaTang(), "ten", tang.getTenTang()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
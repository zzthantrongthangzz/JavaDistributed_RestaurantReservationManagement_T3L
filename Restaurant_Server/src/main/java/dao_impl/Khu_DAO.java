package dao_impl;

import connect.DBConnect;
import entity.Khu;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

public class Khu_DAO {

    // ĐÃ FIX: Truy vấn qua Mối quan hệ [:THUOC_TANG] để lấy mã Tầng
    public Khu timKhuTheoTen(String tenKhu) {
        String cypher = "MATCH (k:Khu)-[:THUOC_TANG]->(t:Tang) " +
                "WHERE trim(k.tenKhu) = trim($ten) " +
                "RETURN k.maKhu AS maKhu, k.tenKhu AS tenKhu, t.maTang AS maTang LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ten", tenKhu));
            if (result.hasNext()) {
                org.neo4j.driver.Record r = result.next();
                return new Khu(r.get("maKhu").asString(), r.get("tenKhu").asString(), r.get("maTang").asString());
            }
        }
        return null;
    }

    public String layMaKhuTheoTen(String tenKhu) {
        String cypher = "MATCH (k:Khu) WHERE trim(k.tenKhu) = trim($ten) RETURN k.maKhu AS maKhu LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ten", tenKhu));
            if (result.hasNext()) {
                return result.next().get("maKhu").asString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sinhMaKhuTuDong() {
        String cypher = "MATCH (k:Khu) RETURN count(k) + 1 AS soThuTu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            if (result.hasNext()) {
                return String.format("K%02d", result.next().get("soThuTu").asInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "K01";
    }

    public boolean themKhu(Khu khu) {
        // ĐÃ FIX: Chống khoảng trắng và tạo đường nối Tầng
        String cypher = "MATCH (t:Tang) WHERE trim(t.maTang) = trim($tang) " +
                "CREATE (k:Khu {maKhu: trim($ma), tenKhu: trim($ten)})-[:THUOC_TANG]->(t)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", khu.getMaKhu(), "ten", khu.getTenKhu(), "tang", khu.getMaTang()));
            return true;
        } catch (Exception e) { return false; }
    }
}
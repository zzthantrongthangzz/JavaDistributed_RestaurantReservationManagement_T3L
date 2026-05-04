package dao_impl;

import connect.DBConnect;
import entity.Khu;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

public class Khu_DAO {

    public Khu timKhuTheoTen(String tenKhu) {
        String cypher = "MATCH (k:Khu {tenKhu: $ten}) RETURN k.maKhu AS maKhu, k.tenKhu AS tenKhu, k.maTang AS maTang";
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
        String cypher = "MATCH (k:Khu {tenKhu: $ten}) RETURN k.maKhu AS maKhu";
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
        // Tự động tìm Tầng và nối relationship [:THUOC_TANG] vào Khu vực vừa tạo
        String cypher = "MATCH (t:Tang) WHERE trim(t.maTang) = trim($tang) " +
                "CREATE (k:Khu {maKhu: trim($ma), tenKhu: trim($ten), maTang: trim($tang)})-[:THUOC_TANG]->(t)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", khu.getMaKhu(), "ten", khu.getTenKhu(), "tang", khu.getMaTang()));
            return true;
        } catch (Exception e) { return false; }
    }
}
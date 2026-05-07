package dao_impl;

import connect.DBConnect;
import entity.ChucVu;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import java.util.ArrayList;
import java.util.List;

public class ChucVu_DAO {

    private ChucVu mapChucVu(Record r) {
        return new ChucVu(r.get("maChucVu").asString(), r.get("tenChucVu").asString());
    }

    public List<ChucVu> docDanhSachChucVu() {
        List<ChucVu> ds = new ArrayList<>();
        try (Session session = DBConnect.getSession()) {
            Result result = session.run("MATCH (c:ChucVu) RETURN c.maChucVu AS maChucVu, c.tenChucVu AS tenChucVu");
            while (result.hasNext()) {
                ds.add(mapChucVu(result.next()));
            }
        }
        return ds;
    }

    public boolean themChucVu(ChucVu chucVu) {
        String cypher = "CREATE (c:ChucVu {maChucVu: $ma, tenChucVu: $ten})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", chucVu.getMaChucVu(), "ten", chucVu.getTenChucVu()));
            return true;
        } catch (Exception e) { return false; }
    }

    public ChucVu timChucVuTheoTen(String tenCV) {
        String cypher = "MATCH (c:ChucVu {tenChucVu: $ten}) RETURN c.maChucVu AS maChucVu, c.tenChucVu AS tenChucVu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ten", tenCV));
            if (result.hasNext()) {
                return mapChucVu(result.next());
            }
        }
        return null;
    }

    public String sinhMaChucVuTuDong() {
        try (Session session = DBConnect.getSession()) {
            String cypher = "MATCH (c:ChucVu) RETURN c.maChucVu AS maxMa ORDER BY c.maChucVu DESC LIMIT 1";
            Result result = session.run(cypher);
            if (result.hasNext()) {
                String maxMa = result.next().get("maxMa").asString();
                if (maxMa != null && maxMa.startsWith("CV")) {
                    int soThuTu = Integer.parseInt(maxMa.substring(2)) + 1;
                    return String.format("CV%03d", soThuTu);
                }
            }
        }
        return "CV001";
    }
}
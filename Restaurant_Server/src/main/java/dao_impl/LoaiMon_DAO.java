package dao_impl;

import connect.DBConnect;
import entity.LoaiMon;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.List;

public class LoaiMon_DAO {

    private LoaiMon mapLoaiMon(Record r) {
        return new LoaiMon(r.get("maLoai").asString(), r.get("tenLoai").asString());
    }

    public List<LoaiMon> docDanhSachLoaiMon() {
        List<LoaiMon> dsLoai = new ArrayList<>();
        String cypher = "MATCH (l:LoaiMon) RETURN l.maLoai AS maLoai, l.tenLoai AS tenLoai";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) dsLoai.add(mapLoaiMon(result.next()));
        }
        return dsLoai;
    }

    public boolean themLoaiMon(LoaiMon loaiMon) {
        String cypher = "CREATE (l:LoaiMon {maLoai: $ma, tenLoai: $ten})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", loaiMon.getMaLoai(), "ten", loaiMon.getTenLoai()));
            return true;
        } catch (Exception e) { return false; }
    }

    public String sinhMaLoaiTuDong() {
        String cypher = "MATCH (l:LoaiMon) WHERE l.maLoai STARTS WITH 'LM' RETURN l.maLoai AS maxMa ORDER BY maxMa DESC LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            if (result.hasNext()) {
                String maxMa = result.next().get("maxMa").asString();
                int soThuTu = Integer.parseInt(maxMa.substring(2)) + 1;
                return String.format("LM%06d", soThuTu);
            }
        }
        return "LM000001";
    }

    public LoaiMon timLoaiTheoTen(String tenLoai) {
        String cypher = "MATCH (l:LoaiMon {tenLoai: $ten}) RETURN l.maLoai AS maLoai, l.tenLoai AS tenLoai";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ten", tenLoai));
            if (result.hasNext()) return mapLoaiMon(result.next());
        }
        return null;
    }
}
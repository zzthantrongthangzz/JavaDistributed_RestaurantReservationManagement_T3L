package dao_impl;

import connect.DBConnect;
import entity.ChiTietPhieuDatBan;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuDatBan_DAO {

    public boolean themChiTietPhieuDat(ChiTietPhieuDatBan ct) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu}), (m:Mon {maMon: $maMon}) MERGE (p)-[c:GOM_MON]->(m) " +
                "SET c.soLuong = $soLuong, c.donGia = $donGia";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maPhieu", ct.getMaPhieuDatBan(),
                    "maMon", ct.getMaMon(),
                    "soLuong", ct.getSoLuong(),
                    "donGia", ct.getDonGia().doubleValue()
            ));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<ChiTietPhieuDatBan> getChiTietTheoPhieu(String maPhieuDatBan) {
        List<ChiTietPhieuDatBan> list = new ArrayList<>();
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[c:GOM_MON]->(m:Mon) " +
                "RETURN p.maPhieuDatBan AS maPhieu, m.maMon AS maMon, c.soLuong AS soLuong, c.donGia AS donGia";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maPhieu", maPhieuDatBan));
            while (result.hasNext()) {
                Record r = result.next();
                list.add(new ChiTietPhieuDatBan(
                        r.get("maPhieu").asString(),
                        r.get("maMon").asString(),
                        r.get("soLuong").asInt(),
                        BigDecimal.valueOf(r.get("donGia").asDouble())
                ));
            }
        }
        return list;
    }

    public boolean xoaChiTietTheoPhieu(String maPhieuDatBan) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[c:GOM_MON]->(:Mon) DELETE c";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", maPhieuDatBan));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean capNhatChiTiet(ChiTietPhieuDatBan ct) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[c:GOM_MON]->(m:Mon {maMon: $maMon}) " +
                "SET c.soLuong = $soLuong, c.donGia = $donGia";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maPhieu", ct.getMaPhieuDatBan(),
                    "maMon", ct.getMaMon(),
                    "soLuong", ct.getSoLuong(),
                    "donGia", ct.getDonGia().doubleValue()
            ));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
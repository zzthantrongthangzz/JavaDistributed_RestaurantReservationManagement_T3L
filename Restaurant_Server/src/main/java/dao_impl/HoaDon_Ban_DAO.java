package dao_impl;

import connect.DBConnect;
import entity.HoaDon_Ban;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.List;

public class HoaDon_Ban_DAO {

    public boolean themHoaDon_Ban(String maHoaDon, String maBan) {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD}), (b:BanAn {maBan: $maBan}) " +
                "MERGE (hd)-[:SU_DUNG_BAN]->(b)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean themHoaDon_Ban(HoaDon_Ban hdb) {
        if (hdb != null) {
            return themHoaDon_Ban(hdb.getMaHoaDon(), hdb.getMaBan());
        }
        return false;
    }

    public boolean chuyenBan(String maHoaDon, String maBanCu, String maBanMoi) {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD})-[r:SU_DUNG_BAN]->(bCu:BanAn {maBan: $maBanCu}) " +
                "DELETE r " +
                "WITH hd " +
                "MATCH (bMoi:BanAn {maBan: $maBanMoi}) " +
                "MERGE (hd)-[:SU_DUNG_BAN]->(bMoi)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon, "maBanCu", maBanCu, "maBanMoi", maBanMoi));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<String> layDanhSachMaBanTheoHoaDon(String maHoaDon) {
        List<String> ds = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD})-[:SU_DUNG_BAN]->(b:BanAn) RETURN b.maBan AS maBan";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maHD", maHoaDon));
            while (result.hasNext()) {
                ds.add(result.next().get("maBan").asString());
            }
        }
        return ds;
    }

    public boolean xoaHoaDon_Ban(String maHoaDon, String maBan) {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD})-[r:SU_DUNG_BAN]->(b:BanAn {maBan: $maBan}) DELETE r";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean xoaTatCaBanCuaHoaDon(String maHoaDon) {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD})-[r:SU_DUNG_BAN]->(:BanAn) DELETE r";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon));
            return true;
        } catch (Exception e) { return false; }
    }
}
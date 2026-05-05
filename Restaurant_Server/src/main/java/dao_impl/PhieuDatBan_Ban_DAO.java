package dao_impl;

import connect.DBConnect;
import entity.BanAn;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.List;

public class PhieuDatBan_Ban_DAO {

    public List<BanAn> getDanhSachBanTheoPhieu(String maPhieu) {
        List<BanAn> list = new ArrayList<>();
        String cypher = "MATCH (p:PhieuDatBan)-[:GOM_BAN]->(b:BanAn) " +
                "WHERE trim(p.maPhieuDatBan) = trim($maPhieu) " +
                "OPTIONAL MATCH (b)-[:THUOC_KHU]->(k:Khu) " +
                "OPTIONAL MATCH (k)-[:THUOC_TANG]->(t:Tang) " +
                "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, " +
                "b.sucChua AS sucChua, b.trangThai AS trangThai, k.maKhu AS maKhu, " +
                "k.tenKhu AS tenKhu, t.tenTang AS tenTang";

        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maPhieu", maPhieu));
            while (result.hasNext()) {
                Record r = result.next();

                int sucChua = 4;
                if (!r.get("sucChua").isNull()) {
                    try { sucChua = r.get("sucChua").asInt(); }
                    catch (Exception e) { try { sucChua = Integer.parseInt(r.get("sucChua").asString()); } catch (Exception ex) {} }
                }

                String maKhu = (!r.get("maKhu").isNull()) ? r.get("maKhu").asString() : "K01";

                BanAn ban = new BanAn(
                        r.get("maBan").isNull() ? "" : r.get("maBan").asString(),
                        r.get("tenBan").isNull() ? "Chưa có tên" : r.get("tenBan").asString(),
                        r.get("loaiBan").isNull() ? "Bàn thường" : r.get("loaiBan").asString(),
                        sucChua,
                        r.get("trangThai").isNull() ? "Bàn đang trống" : r.get("trangThai").asString(),
                        maKhu
                );

                ban.setTenKhu(r.get("tenKhu").isNull() ? "Chưa phân khu" : r.get("tenKhu").asString());
                ban.setTenTang(r.get("tenTang").isNull() ? "Chưa phân tầng" : r.get("tenTang").asString());

                list.add(ban);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean themPhieuDatBan_Ban(String maPhieu, String maBan) {
        String cypher = "MATCH (p:PhieuDatBan), (b:BanAn) " +
                "WHERE trim(p.maPhieuDatBan) = trim($maPhieu) AND trim(b.maBan) = trim($maBan) " +
                "MERGE (p)-[:GOM_BAN]->(b)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", maPhieu, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }

    public int demSoBanCuaPhieu(String maPhieu) {
        String cypher = "MATCH (p:PhieuDatBan)-[:GOM_BAN]->(b:BanAn) WHERE trim(p.maPhieuDatBan) = trim($maPhieu) RETURN count(b) AS soLuong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maPhieu", maPhieu));
            if (result.hasNext()) return result.next().get("soLuong").asInt();
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public boolean xoaBanKhoiPhieu(String maPhieu, String maBan) {
        String cypher = "MATCH (p:PhieuDatBan)-[r:GOM_BAN]->(b:BanAn) " +
                "WHERE trim(p.maPhieuDatBan) = trim($maPhieu) AND trim(b.maBan) = trim($maBan) DELETE r";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", maPhieu, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }
}
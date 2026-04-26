package dao_impl;

import connect.DBConnect;
import entity.ChiTietPhieuDatBan;
import rmi_interfaces.IChiTietPhieuDatBan_DAO;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuDatBan_DAO_Impl extends UnicastRemoteObject implements IChiTietPhieuDatBan_DAO {

    public ChiTietPhieuDatBan_DAO_Impl() throws RemoteException {
        super();
    }

    @Override
    public boolean themChiTietPhieuDat(ChiTietPhieuDatBan ct) throws RemoteException {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu}), (m:MonAn {maMon: $maMon}) " +
                "MERGE (p)-[c:BAO_GOM]->(m) " +
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

    @Override
    public List<ChiTietPhieuDatBan> getChiTietTheoPhieu(String maPhieuDatBan) throws RemoteException {
        List<ChiTietPhieuDatBan> list = new ArrayList<>();
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[c:BAO_GOM]->(m:MonAn) " +
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

    @Override
    public boolean xoaChiTietTheoPhieu(String maPhieuDatBan) throws RemoteException {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[c:BAO_GOM]->(:MonAn) DELETE c";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", maPhieuDatBan));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public boolean capNhatChiTiet(ChiTietPhieuDatBan ct) throws RemoteException {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[c:BAO_GOM]->(m:MonAn {maMon: $maMon}) " +
                "SET c.soLuong = $soLuong, c.donGia = $donGia";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maPhieu", ct.getMaPhieuDatBan(),
                    "maMon", ct.getMaMon(),
                    "soLuong", ct.getSoLuong(),
                    "donGia", ct.getDonGia().doubleValue() // Chuyển BigDecimal sang Double cho Neo4j
            ));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
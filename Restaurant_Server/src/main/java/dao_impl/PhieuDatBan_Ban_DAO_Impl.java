package dao_impl;

import connect.DBConnect;
import entity.BanAn;
import rmi_interfaces.IPhieuDatBan_Ban_DAO;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatBan_Ban_DAO_Impl extends UnicastRemoteObject implements IPhieuDatBan_Ban_DAO {

    public PhieuDatBan_Ban_DAO_Impl() throws RemoteException {
        super();
    }

    @Override
    public List<BanAn> getDanhSachBanTheoPhieu(String maPhieu) throws RemoteException {
        List<BanAn> list = new ArrayList<>();
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[:DAT_BAN]->(b:BanAn) " +
                "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, " +
                "b.sucChua AS sucChua, b.trangThai AS trangThai, b.maKhu AS maKhu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maPhieu", maPhieu));
            while (result.hasNext()) {
                Record r = result.next();
                list.add(new BanAn(
                        r.get("maBan").asString(), r.get("tenBan").asString(), r.get("loaiBan").asString(),
                        r.get("sucChua").asInt(), r.get("trangThai").asString(), r.get("maKhu").isNull() ? null : r.get("maKhu").asString()
                ));
            }
        }
        return list;
    }

    @Override
    public boolean themPhieuDatBan_Ban(String maPhieu, String maBan) throws RemoteException {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu}), (b:BanAn {maBan: $maBan}) " +
                "MERGE (p)-[:DAT_BAN]->(b)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", maPhieu, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public int demSoBanCuaPhieu(String maPhieu) throws RemoteException {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[:DAT_BAN]->(b:BanAn) RETURN count(b) AS soLuong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maPhieu", maPhieu));
            if (result.hasNext()) return result.next().get("soLuong").asInt();
        }
        return 0;
    }

    @Override
    public boolean xoaBanKhoiPhieu(String maPhieu, String maBan) throws RemoteException {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[r:DAT_BAN]->(b:BanAn {maBan: $maBan}) DELETE r";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", maPhieu, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }
}
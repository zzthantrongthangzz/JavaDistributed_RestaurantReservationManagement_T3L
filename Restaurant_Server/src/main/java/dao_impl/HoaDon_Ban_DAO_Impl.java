package dao_impl;

import connect.DBConnect;
import entity.HoaDon_Ban;
import rmi_interfaces.IHoaDon_Ban_DAO;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_Ban_DAO_Impl extends UnicastRemoteObject implements IHoaDon_Ban_DAO {

    public HoaDon_Ban_DAO_Impl() throws RemoteException {
        super();
    }

    @Override
    public boolean themHoaDon_Ban(String maHoaDon, String maBan) throws RemoteException {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD}), (b:BanAn {maBan: $maBan}) " +
                "MERGE (hd)-[:SU_DUNG_BAN]->(b)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public boolean themHoaDon_Ban(HoaDon_Ban hdb) throws RemoteException {
        if (hdb != null) {
            return themHoaDon_Ban(hdb.getMaHoaDon(), hdb.getMaBan());
        }
        return false;
    }

    @Override
    public boolean chuyenBan(String maHoaDon, String maBanCu, String maBanMoi) throws RemoteException {
        // Xóa Relationship cũ và tạo Relationship mới
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

    @Override
    public List<String> layDanhSachMaBanTheoHoaDon(String maHoaDon) throws RemoteException {
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

    @Override
    public boolean xoaHoaDon_Ban(String maHoaDon, String maBan) throws RemoteException {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD})-[r:SU_DUNG_BAN]->(b:BanAn {maBan: $maBan}) DELETE r";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public boolean xoaTatCaBanCuaHoaDon(String maHoaDon) throws RemoteException {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD})-[r:SU_DUNG_BAN]->(:BanAn) DELETE r";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon));
            return true;
        } catch (Exception e) { return false; }
    }
}
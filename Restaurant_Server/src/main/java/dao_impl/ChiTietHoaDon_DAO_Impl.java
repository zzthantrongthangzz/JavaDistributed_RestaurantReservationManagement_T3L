package dao_impl;

import connect.DBConnect;
import entity.ChiTietHoaDon;
import rmi_interfaces.IChiTietHoaDon_DAO;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO_Impl extends UnicastRemoteObject implements IChiTietHoaDon_DAO {

    public ChiTietHoaDon_DAO_Impl() throws RemoteException {
        super();
    }

    @Override
    public List<ChiTietHoaDon> getAllChiTietHoaDon() throws RemoteException {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon)-[c:BAO_GOM]->(m:MonAn) " +
                "RETURN hd.maHoaDon AS maHoaDon, m.maMon AS maMon, c.soLuong AS soLuong, c.donGia AS donGia";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                Record r = result.next();
                list.add(new ChiTietHoaDon(
                        r.get("maHoaDon").asString(),
                        r.get("maMon").asString(),
                        r.get("soLuong").asInt(),
                        BigDecimal.valueOf(r.get("donGia").asDouble())
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<ChiTietHoaDon> getChiTietTheoMaHoaDon(String maHoaDon) throws RemoteException {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHoaDon})-[c:BAO_GOM]->(m:MonAn) " +
                "RETURN hd.maHoaDon AS maHoaDon, m.maMon AS maMon, c.soLuong AS soLuong, c.donGia AS donGia";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maHoaDon", maHoaDon));
            while (result.hasNext()) {
                Record r = result.next();
                list.add(new ChiTietHoaDon(
                        r.get("maHoaDon").asString(),
                        r.get("maMon").asString(),
                        r.get("soLuong").asInt(),
                        BigDecimal.valueOf(r.get("donGia").asDouble())
                ));
            }
        }
        return list;
    }

    @Override
    public boolean themChiTietHoaDon(ChiTietHoaDon cthd) throws RemoteException {
        // MATCH Hóa Đơn và Món Ăn có sẵn, sau đó tạo Relationship nối 2 Node lại
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHoaDon}), (m:MonAn {maMon: $maMon}) " +
                "MERGE (hd)-[c:BAO_GOM]->(m) " +
                "SET c.soLuong = $soLuong, c.donGia = $donGia";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maHoaDon", cthd.getMaHoaDon(),
                    "maMon", cthd.getMaMon(),
                    "soLuong", cthd.getSoLuong(),
                    "donGia", cthd.getDonGia().doubleValue()
            ));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public boolean capNhatChiTietHoaDon(ChiTietHoaDon cthd) throws RemoteException {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHoaDon})-[c:BAO_GOM]->(m:MonAn {maMon: $maMon}) " +
                "SET c.soLuong = $soLuong, c.donGia = $donGia";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maHoaDon", cthd.getMaHoaDon(),
                    "maMon", cthd.getMaMon(),
                    "soLuong", cthd.getSoLuong(),
                    "donGia", cthd.getDonGia().doubleValue()
            ));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public boolean xoaChiTietHoaDon(String maHoaDon, String maMon) throws RemoteException {
        // Xóa Relationship thay vì xóa Node
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHoaDon})-[c:BAO_GOM]->(m:MonAn {maMon: $maMon}) DELETE c";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHoaDon", maHoaDon, "maMon", maMon));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public ChiTietHoaDon timChiTiet(String maHoaDon, String maMon) throws RemoteException {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD})-[c:BAO_GOM]->(m:MonAn {maMon: $maMon}) " +
                "RETURN hd.maHoaDon AS maHoaDon, m.maMon AS maMon, c.soLuong AS soLuong, c.donGia AS donGia";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maHD", maHoaDon, "maMon", maMon));
            if (result.hasNext()) {
                Record r = result.next();
                return new ChiTietHoaDon(
                        r.get("maHoaDon").asString(),
                        r.get("maMon").asString(),
                        r.get("soLuong").asInt(),
                        BigDecimal.valueOf(r.get("donGia").asDouble())
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean xoaChiTietTheoMaHoaDon(String maHoaDon) throws RemoteException {
        String cypher = "MATCH (hd:HoaDon {maHoaDon: $maHD})-[c:BAO_GOM]->(:MonAn) DELETE c";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maHD", maHoaDon));
            return true;
        } catch (Exception e) { return false; }
    }
}
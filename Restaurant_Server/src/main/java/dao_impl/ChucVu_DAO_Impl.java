package dao_impl;

import connect.DBConnect;
import entity.ChucVu;
import rmi_interfaces.IChucVu_DAO;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChucVu_DAO_Impl extends UnicastRemoteObject implements IChucVu_DAO {

    public ChucVu_DAO_Impl() throws RemoteException {
        super();
    }

    private ChucVu mapChucVu(Record r) {
        return new ChucVu(r.get("maChucVu").asString(), r.get("tenChucVu").asString());
    }

    @Override
    public List<ChucVu> docDanhSachChucVu() throws RemoteException {
        List<ChucVu> ds = new ArrayList<>();
        try (Session session = DBConnect.getSession()) {
            Result result = session.run("MATCH (c:ChucVu) RETURN c.maChucVu AS maChucVu, c.tenChucVu AS tenChucVu");
            while (result.hasNext()) {
                ds.add(mapChucVu(result.next()));
            }
        }
        return ds;
    }

    @Override
    public boolean themChucVu(ChucVu chucVu) throws RemoteException {
        String cypher = "CREATE (c:ChucVu {maChucVu: $ma, tenChucVu: $ten})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("ma", chucVu.getMaChucVu(), "ten", chucVu.getTenChucVu()));
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    public ChucVu timChucVuTheoTen(String tenCV) throws RemoteException {
        String cypher = "MATCH (c:ChucVu {tenChucVu: $ten}) RETURN c.maChucVu AS maChucVu, c.tenChucVu AS tenChucVu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("ten", tenCV));
            if (result.hasNext()) {
                return mapChucVu(result.next());
            }
        }
        return null;
    }

    @Override
    public String sinhMaChucVuTuDong() throws RemoteException {
        try (Session session = DBConnect.getSession()) {
            // Sắp xếp mã chức vụ giảm dần và lấy cái đầu tiên để sinh mã mới
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
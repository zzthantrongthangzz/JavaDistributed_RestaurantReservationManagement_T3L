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
        // SỬA LỖI: Thêm OPTIONAL MATCH để truy xuất đến node Khu lấy maKhu
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[:DAT_BAN]->(b:BanAn) " +
                "OPTIONAL MATCH (b)-[:THUOC_KHU]->(k:Khu) " +
                "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, " +
                "b.sucChua AS sucChua, b.trangThai AS trangThai, k.maKhu AS maKhu";

        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maPhieu", maPhieu));
            while (result.hasNext()) {
                Record r = result.next();

                // 1. Xử lý an toàn chống lỗi ép kiểu Sức chứa
                int sucChua = 4;
                if (!r.get("sucChua").isNull()) {
                    try {
                        sucChua = r.get("sucChua").asInt();
                    } catch (Exception e) {
                        try { sucChua = Integer.parseInt(r.get("sucChua").asString()); } catch (Exception ex) {}
                    }
                }

                // 2. Xử lý an toàn chống lỗi Mã khu Null làm sập Entity
                String maKhu = "K01"; // Mặc định nếu DB bị lủng dữ liệu
                if (!r.get("maKhu").isNull()) {
                    maKhu = r.get("maKhu").asString();
                }
                if (maKhu == null || maKhu.trim().isEmpty()) {
                    maKhu = "K01";
                }

                list.add(new BanAn(
                        r.get("maBan").isNull() ? "" : r.get("maBan").asString(),
                        r.get("tenBan").isNull() ? "Chưa có tên" : r.get("tenBan").asString(),
                        r.get("loaiBan").isNull() ? "Bàn thường" : r.get("loaiBan").asString(),
                        sucChua,
                        r.get("trangThai").isNull() ? "Bàn đang trống" : r.get("trangThai").asString(),
                        maKhu
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean themPhieuDatBan_Ban(String maPhieu, String maBan) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu}), (b:BanAn {maBan: $maBan}) " +
                "MERGE (p)-[:DAT_BAN]->(b)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", maPhieu, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }

    public int demSoBanCuaPhieu(String maPhieu) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[:DAT_BAN]->(b:BanAn) RETURN count(b) AS soLuong";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maPhieu", maPhieu));
            if (result.hasNext()) return result.next().get("soLuong").asInt();
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public boolean xoaBanKhoiPhieu(String maPhieu, String maBan) {
        String cypher = "MATCH (p:PhieuDatBan {maPhieuDatBan: $maPhieu})-[r:DAT_BAN]->(b:BanAn {maBan: $maBan}) DELETE r";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maPhieu", maPhieu, "maBan", maBan));
            return true;
        } catch (Exception e) { return false; }
    }
}
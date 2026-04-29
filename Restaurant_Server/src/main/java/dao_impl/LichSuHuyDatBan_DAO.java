package dao_impl;

import connect.DBConnect;
import entity.LichSuHuyDatBan;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LichSuHuyDatBan_DAO {

    public boolean ghiLogHuyDatBan(LichSuHuyDatBan log) {
        String cypher = "CREATE (l:LichSuHuyDatBan {maPhieuDatBan: $maPhieu, tenBan: $tenBan, " +
                "tenKhachHang: $tenKH, sdtKhachHang: $sdt, maNhanVien: $maNV, " +
                "tenNhanVien: $tenNV, thoiGianHuy: $thoiGian, lyDoHuy: $lyDo})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maPhieu", log.getMaPhieuDatBan(), "tenBan", log.getTenBan(),
                    "tenKH", log.getTenKhachHang(), "sdt", log.getSdtKhachHang(),
                    "maNV", log.getMaNhanVien(), "tenNV", log.getTenNhanVien(),
                    "thoiGian", log.getThoiGianHuy().getTime(),
                    "lyDo", log.getLyDoHuy()
            ));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LichSuHuyDatBan> layTatCaLichSu() {
        List<LichSuHuyDatBan> dsLog = new ArrayList<>();
        String cypher = "MATCH (l:LichSuHuyDatBan) RETURN l ORDER BY l.thoiGianHuy DESC";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                Record r = result.next();
                var node = r.get("l");

                LichSuHuyDatBan log = new LichSuHuyDatBan();
                log.setMaPhieuDatBan(node.get("maPhieuDatBan").asString());
                log.setTenBan(node.get("tenBan").asString());
                log.setTenKhachHang(node.get("tenKhachHang").asString());
                log.setSdtKhachHang(node.get("sdtKhachHang").asString());
                log.setMaNhanVien(node.get("maNhanVien").asString());
                log.setTenNhanVien(node.get("tenNhanVien").asString());
                log.setThoiGianHuy(new Timestamp(node.get("thoiGianHuy").asLong()));
                log.setLyDoHuy(node.get("lyDoHuy").asString());

                dsLog.add(log);
            }
        }
        return dsLog;
    }
}
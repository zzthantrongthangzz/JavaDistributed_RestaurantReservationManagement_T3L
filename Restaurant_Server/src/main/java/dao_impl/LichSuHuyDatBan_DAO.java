package dao_impl;

import connect.DBConnect;
import entity.LichSuHuyDatBan;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.Value;

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
                    "maPhieu", log.getMaPhieuDatBan() != null ? log.getMaPhieuDatBan() : "",
                    "tenBan", log.getTenBan() != null ? log.getTenBan() : "",
                    "tenKH", log.getTenKhachHang() != null ? log.getTenKhachHang() : "",
                    "sdt", log.getSdtKhachHang() != null ? log.getSdtKhachHang() : "",
                    "maNV", log.getMaNhanVien() != null ? log.getMaNhanVien() : "",
                    "tenNV", log.getTenNhanVien() != null ? log.getTenNhanVien() : "",
                    "thoiGian", log.getThoiGianHuy() != null ? log.getThoiGianHuy().getTime() : System.currentTimeMillis(),
                    "lyDo", log.getLyDoHuy() != null ? log.getLyDoHuy() : ""
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
                Value node = r.get("l");

                LichSuHuyDatBan log = new LichSuHuyDatBan();

                // KIỂM TRA BẢO MẬT NULL TRƯỚC KHI ÉP KIỂU TỪ NEO4J
                log.setMaPhieuDatBan(node.get("maPhieuDatBan").isNull() ? "" : node.get("maPhieuDatBan").asString());
                log.setTenBan(node.get("tenBan").isNull() ? "" : node.get("tenBan").asString());
                log.setTenKhachHang(node.get("tenKhachHang").isNull() ? "Khách vãng lai" : node.get("tenKhachHang").asString());
                log.setSdtKhachHang(node.get("sdtKhachHang").isNull() ? "" : node.get("sdtKhachHang").asString());
                log.setMaNhanVien(node.get("maNhanVien").isNull() ? "" : node.get("maNhanVien").asString());
                log.setTenNhanVien(node.get("tenNhanVien").isNull() ? "" : node.get("tenNhanVien").asString());

                // Xử lý an toàn cho Thời gian (asLong)
                long thoiGian = System.currentTimeMillis();
                if (!node.get("thoiGianHuy").isNull()) {
                    try {
                        thoiGian = node.get("thoiGianHuy").asLong();
                    } catch (Exception ex) {
                        // Nếu do nhầm lẫn lưu string vào db thì cố ép ngược lại
                        try { thoiGian = Long.parseLong(node.get("thoiGianHuy").asString()); } catch(Exception ignored){}
                    }
                }
                log.setThoiGianHuy(new Timestamp(thoiGian));

                log.setLyDoHuy(node.get("lyDoHuy").isNull() ? "Không có lý do" : node.get("lyDoHuy").asString());

                dsLog.add(log);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return dsLog;
    }
}
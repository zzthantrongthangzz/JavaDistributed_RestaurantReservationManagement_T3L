package dao_impl;

import connect.DBConnect;
import entity.BanAn;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import java.util.*;

public class BanAn_DAO {

    private BanAn mapBanAn(Record record) {
        BanAn banAn = new BanAn(
                record.get("maBan").asString(),
                record.get("tenBan").asString(),
                record.get("loaiBan").asString(),
                record.get("sucChua").asInt(),
                record.get("trangThai").asString(),
                record.get("maKhu").isNull() ? null : record.get("maKhu").asString()
        );
        banAn.setTenKhachHang(record.get("tenKhachHang").isNull() ? null : record.get("tenKhachHang").asString());
        banAn.setTenKhu(record.get("tenKhu").isNull() ? null : record.get("tenKhu").asString());
        banAn.setTenTang(record.get("tenTang").isNull() ? null : record.get("tenTang").asString());
        return banAn;
    }

    private final String CYPHER_BASE_MATCH =
            "MATCH (b:BanAn) " +
                    "OPTIONAL MATCH (b)-[:THUOC_KHU]->(k:Khu)-[:THUOC_TANG]->(t:Tang) " +
                    "OPTIONAL MATCH (b)-[:CO_HOA_DON]->(hd:HoaDon {trangThai: 'Chưa thanh toán'})-[:CUA_KHACH]->(kh:KhachHang) ";

    private final String CYPHER_RETURN =
            "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, b.sucChua AS sucChua, " +
                    "b.trangThai AS trangThai, b.maKhu AS maKhu, k.tenKhu AS tenKhu, t.tenTang AS tenTang, kh.hoTen AS tenKhachHang ";

    public List<BanAn> docDanhSachBan() {
        List<BanAn> danhSachBan = new ArrayList<>();
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(CYPHER_BASE_MATCH + CYPHER_RETURN);
            while (result.hasNext()) {
                danhSachBan.add(mapBanAn(result.next()));
            }
        }
        return danhSachBan;
    }

    public boolean themBanMoi(BanAn banAn) {
        String cypher = "CREATE (b:BanAn {maBan: $maBan, tenBan: $tenBan, loaiBan: $loaiBan, " +
                "sucChua: $sucChua, trangThai: $trangThai, maKhu: $maKhu})";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maBan", banAn.getMaBan(),
                    "tenBan", banAn.getTenBan(),
                    "loaiBan", banAn.getLoaiBan(),
                    "sucChua", banAn.getSucChua(),
                    "trangThai", banAn.getTrangThai(),
                    "maKhu", banAn.getMaKhu()
            ));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
        String cypher = "MATCH (b:BanAn {maBan: $maBan}) SET b.trangThai = $trangThaiMoi";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBan", maBan, "trangThaiMoi", trangThaiMoi));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatBan(BanAn banAn) {
        String cypher = "MATCH (b:BanAn {maBan: $maBan}) " +
                "SET b.tenBan = $tenBan, b.loaiBan = $loaiBan, b.sucChua = $sucChua, b.maKhu = $maKhu";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maBan", banAn.getMaBan(),
                    "tenBan", banAn.getTenBan(),
                    "loaiBan", banAn.getLoaiBan(),
                    "sucChua", banAn.getSucChua(),
                    "maKhu", banAn.getMaKhu()
            ));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoaBan(String maBan) {
        String cypher = "MATCH (b:BanAn {maBan: $maBan}) DETACH DELETE b";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maBan", maBan));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public BanAn timBanAnTheoTenHoacSDTKhachHang(String tuKhoa) {
        String cypher = CYPHER_BASE_MATCH +
                "WHERE toUpper(kh.hoTen) CONTAINS toUpper($tuKhoa) OR kh.soDienThoai CONTAINS $tuKhoa " +
                CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            if (result.hasNext()) return mapBanAn(result.next());
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<BanAn> layDanhSachBanTrongTheoNgay(Date ngayCanXem) {
        List<BanAn> list = new ArrayList<>();
        Date homNay = new Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        boolean isHomNay = sdf.format(ngayCanXem).equals(sdf.format(homNay));

        long start = atStartOfDay(ngayCanXem).getTime();
        long end = atEndOfDay(ngayCanXem).getTime();

        StringBuilder cypher = new StringBuilder(CYPHER_BASE_MATCH);
        cypher.append("WHERE NOT EXISTS { MATCH (p:PhieuDatBan {trangThai: 'Đang chờ'})-[:DAT_BAN]->(b) ");
        cypher.append("WHERE p.thoiGianDat >= $start AND p.thoiGianDat <= $end } ");

        if (isHomNay) {
            cypher.append("AND b.trangThai = 'Bàn đang trống' ");
        }
        cypher.append(CYPHER_RETURN);

        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher.toString(), Values.parameters("start", start, "end", end));
            while (result.hasNext()) {
                BanAn ban = mapBanAn(result.next());
                if (!isHomNay) ban.setTrangThai("Bàn đang trống");
                list.add(ban);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<BanAn> timKiemBanTheoKhachHang(String tuKhoa) {
        List<BanAn> list = new ArrayList<>();
        String cypher =
                "MATCH (b:BanAn) " +
                        "OPTIONAL MATCH (b)-[:THUOC_KHU]->(k:Khu)-[:THUOC_TANG]->(t:Tang) " +
                        "OPTIONAL MATCH (b)-[:CO_HOA_DON]->(hd:HoaDon {trangThai: 'Chưa thanh toán'})-[:CUA_KHACH]->(khHD:KhachHang) " +
                        "OPTIONAL MATCH (pdb:PhieuDatBan {trangThai: 'Đang chờ'})-[:DAT_BAN]->(b) " +
                        "OPTIONAL MATCH (pdb)-[:CUA_KHACH]->(khPDB:KhachHang) " +
                        "WHERE toUpper(khHD.hoTen) CONTAINS toUpper($tuKhoa) OR khHD.soDienThoai CONTAINS $tuKhoa " +
                        "OR toUpper(khPDB.hoTen) CONTAINS toUpper($tuKhoa) OR khPDB.soDienThoai CONTAINS $tuKhoa " +
                        "RETURN b.maBan AS maBan, b.tenBan AS tenBan, b.loaiBan AS loaiBan, b.sucChua AS sucChua, " +
                        "CASE WHEN hd IS NOT NULL THEN 'Bàn đang phục vụ' WHEN pdb IS NOT NULL THEN 'Bàn đang chờ' ELSE b.trangThai END AS trangThai, " +
                        "b.maKhu AS maKhu, k.tenKhu AS tenKhu, t.tenTang AS tenTang, " +
                        "COALESCE(khHD.hoTen, khPDB.hoTen) AS tenKhachHang";

        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            while (result.hasNext()) {
                list.add(mapBanAn(result.next()));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public BanAn timBanAnTheoMa(String maBan) {
        try (Session session = DBConnect.getSession()) {
            String cypher = CYPHER_BASE_MATCH + "WHERE b.maBan = $maBan " + CYPHER_RETURN;
            Result result = session.run(cypher, Values.parameters("maBan", maBan));
            if (result.hasNext()) {
                return mapBanAn(result.next());
            }
        }
        return null;
    }

    public List<BanAn> locTheoLoaiBan(String loaiBan) {
        List<BanAn> list = new ArrayList<>();
        String cypher = CYPHER_BASE_MATCH + "WHERE b.loaiBan = $loaiBan " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("loaiBan", loaiBan));
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<BanAn> timKiemBan(String tuKhoa) {
        List<BanAn> list = new ArrayList<>();
        try (Session session = DBConnect.getSession()) {
            String cypher = CYPHER_BASE_MATCH +
                    "WHERE toUpper(b.maBan) = toUpper($tuKhoa) OR toUpper(b.tenBan) CONTAINS toUpper($tuKhoa) " +
                    CYPHER_RETURN;
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            while (result.hasNext()) {
                list.add(mapBanAn(result.next()));
            }
        }
        return list;
    }

    public List<BanAn> locTheoTrangThai(String trangThai) {
        List<BanAn> list = new ArrayList<>();
        try (Session session = DBConnect.getSession()) {
            String cypher = CYPHER_BASE_MATCH + "WHERE b.trangThai = $trangThai " + CYPHER_RETURN;
            Result result = session.run(cypher, Values.parameters("trangThai", trangThai));
            while (result.hasNext()) {
                list.add(mapBanAn(result.next()));
            }
        }
        return list;
    }

    public List<String> docDanhSachTenTang() {
        List<String> list = new ArrayList<>();
        list.add("Tất cả");
        String cypher = "MATCH (t:Tang) RETURN DISTINCT t.tenTang AS tenTang ORDER BY tenTang";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) list.add(result.next().get("tenTang").asString());
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<String> docDanhSachTenKhuTheoTang(String tenTang) {
        List<String> list = new ArrayList<>();
        list.add("Tất cả");
        String cypher = (tenTang == null || "Tất cả".equals(tenTang)) ?
                "MATCH (k:Khu) RETURN DISTINCT k.tenKhu AS tenKhu ORDER BY tenKhu" :
                "MATCH (k:Khu)-[:THUOC_TANG]->(t:Tang {tenTang: $tenTang}) RETURN DISTINCT k.tenKhu AS tenKhu ORDER BY tenKhu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tenTang", tenTang));
            while (result.hasNext()) list.add(result.next().get("tenKhu").asString());
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<BanAn> locBanAn(String tenTang, String tenKhu, String loaiBan) {
        List<BanAn> list = new ArrayList<>();
        StringBuilder cypher = new StringBuilder(CYPHER_BASE_MATCH + "WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (tenTang != null && !"Tất cả".equals(tenTang)) {
            cypher.append("AND t.tenTang = $tenTang ");
            params.put("tenTang", tenTang);
        }
        if (tenKhu != null && !"Tất cả".equals(tenKhu)) {
            cypher.append("AND k.tenKhu = $tenKhu ");
            params.put("tenKhu", tenKhu);
        }
        if (loaiBan != null && !"Tất cả".equals(loaiBan)) {
            cypher.append("AND b.loaiBan = $loaiBan ");
            params.put("loaiBan", loaiBan);
        }

        cypher.append(CYPHER_RETURN);
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher.toString(), params);
            while (result.hasNext()) list.add(mapBanAn(result.next()));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public String layMaBanTiepTheo() {
        try (Session session = DBConnect.getSession()) {
            Result result = session.run("MATCH (b:BanAn) RETURN count(b) + 1 AS soThuTu");
            if (result.hasNext()) {
                long count = result.next().get("soThuTu").asLong();
                return String.format("MB%06d", count);
            }
        }
        return "MB000001";
    }

    public int laySoThuTuBanTiepTheo(String loaiBan) {
        String cypher = "MATCH (b:BanAn {loaiBan: $loaiBan}) RETURN count(b) + 1 AS soThuTu";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("loaiBan", loaiBan));
            if (result.hasNext()) return result.next().get("soThuTu").asInt();
        } catch (Exception e) { e.printStackTrace(); }
        return 1;
    }

    private Date atStartOfDay(Date date) {
        Calendar c = Calendar.getInstance(); c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private Date atEndOfDay(Date date) {
        Calendar c = Calendar.getInstance(); c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59); c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
}
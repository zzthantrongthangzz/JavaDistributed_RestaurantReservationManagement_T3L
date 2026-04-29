package dao_impl;

import connect.DBConnect;
import entity.LichSuGia;
import entity.LoaiMon;
import entity.MonAn;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.Transaction;

import java.sql.Timestamp;
import java.util.*;

public class MonAn_DAO {

    private MonAn mapMonAn(Record r) {
        LoaiMon loaiMon = new LoaiMon(r.get("maLoai").asString(), r.get("tenLoai").asString());
        return new MonAn(
                r.get("maMon").asString(), r.get("tenMon").asString(), r.get("duongDanAnh").asString(),
                r.get("gia").asDouble(), r.get("tinhTrang").asString(), r.get("moTa").asString(),
                r.get("donVi").asString(), loaiMon
        );
    }

    private final String CYPHER_SELECT_BASE = "MATCH (m:MonAn)-[:THUOC_LOAI]->(l:LoaiMon) ";
    private final String CYPHER_RETURN = "RETURN m.maMon AS maMon, m.tenMon AS tenMon, m.duongDanAnh AS duongDanAnh, " +
            "m.gia AS gia, m.tinhTrang AS tinhTrang, m.moTa AS moTa, m.donVi AS donVi, " +
            "l.maLoai AS maLoai, l.tenLoai AS tenLoai ";

    public List<MonAn> docDanhSachMon() {
        List<MonAn> ds = new ArrayList<>();
        String cypher = CYPHER_SELECT_BASE + "WHERE m.tinhTrang = 'Đang kinh doanh' " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) ds.add(mapMonAn(result.next()));
        }
        return ds;
    }

    public List<MonAn> timKiemMonAn(String tuKhoa) {
        List<MonAn> ds = new ArrayList<>();
        String cypher = CYPHER_SELECT_BASE + "WHERE m.tinhTrang = 'Đang kinh doanh' AND " +
                "(toUpper(m.tenMon) CONTAINS toUpper($tuKhoa) OR toUpper(m.maMon) CONTAINS toUpper($tuKhoa)) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            while (result.hasNext()) ds.add(mapMonAn(result.next()));
        }
        return ds;
    }

    public List<MonAn> timKiemTheoMa(String maMon) {
        List<MonAn> ds = new ArrayList<>();
        String cypher = CYPHER_SELECT_BASE + "WHERE m.tinhTrang = 'Đang kinh doanh' AND toUpper(m.maMon) = toUpper($maMon) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maMon", maMon));
            while (result.hasNext()) ds.add(mapMonAn(result.next()));
        }
        return ds;
    }

    public List<MonAn> timKiemTheoTen(String tenMon) {
        List<MonAn> ds = new ArrayList<>();
        String cypher = CYPHER_SELECT_BASE + "WHERE m.tinhTrang = 'Đang kinh doanh' AND toUpper(m.tenMon) CONTAINS toUpper($tenMon) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tenMon", tenMon));
            while (result.hasNext()) ds.add(mapMonAn(result.next()));
        }
        return ds;
    }

    public List<MonAn> locMonAnTheoLoai(String tenLoai) {
        List<MonAn> ds = new ArrayList<>();
        String cypher = CYPHER_SELECT_BASE + "WHERE m.tinhTrang = 'Đang kinh doanh' AND l.tenLoai = $tenLoai " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tenLoai", tenLoai));
            while (result.hasNext()) ds.add(mapMonAn(result.next()));
        }
        return ds;
    }

    public String sinhMaMonTuDong() {
        String cypher = "MATCH (m:MonAn) WHERE m.maMon STARTS WITH 'MM' RETURN m.maMon AS maxMa ORDER BY maxMa DESC LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            if (result.hasNext()) {
                String maxMa = result.next().get("maxMa").asString();
                int soThuTu = Integer.parseInt(maxMa.substring(2)) + 1;
                return String.format("MM%06d", soThuTu);
            }
        }
        return "MM000001";
    }

    public boolean themMonAn(MonAn monAn) {
        String cypher = "MATCH (l:LoaiMon {maLoai: $maLoai}) " +
                "CREATE (m:MonAn {maMon: $maMon, tenMon: $tenMon, gia: $gia, duongDanAnh: $anh, " +
                "tinhTrang: $tinhTrang, moTa: $moTa, donVi: $donVi})-[:THUOC_LOAI]->(l)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maLoai", monAn.getLoaiMon().getMaLoai(), "maMon", monAn.getMaMon(), "tenMon", monAn.getTenMon(),
                    "gia", monAn.getGia(), "anh", monAn.getDuongDanAnh(), "tinhTrang", monAn.getTinhTrang(),
                    "moTa", monAn.getMoTa(), "donVi", monAn.getDonVi()
            ));
            return true;
        } catch (Exception e) { return false; }
    }

    public MonAn timMotMonTheoMa(String maMon) {
        String cypher = CYPHER_SELECT_BASE + "WHERE toUpper(m.maMon) = toUpper($maMon) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maMon", maMon));
            if (result.hasNext()) return mapMonAn(result.next());
        }
        return null;
    }

    public boolean xoaMem(String maMon) {
        String cypher = "MATCH (m:MonAn {maMon: $maMon}) SET m.tinhTrang = 'Ngừng kinh doanh'";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maMon", maMon));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean capNhatMonAn(MonAn monAn, String maNhanVienThucHien) {
        MonAn monCu = timMotMonTheoMa(monAn.getMaMon());
        if (monCu == null) return false;

        double giaCu = monCu.getGia();
        long thoiGianHienTai = System.currentTimeMillis();

        String updateCypher = "MATCH (m:MonAn {maMon: $maMon})-[r:THUOC_LOAI]->() " +
                "DELETE r WITH m MATCH (l:LoaiMon {maLoai: $maLoai}) " +
                "MERGE (m)-[:THUOC_LOAI]->(l) " +
                "SET m.tenMon = $tenMon, m.gia = $gia, m.duongDanAnh = $anh, " +
                "m.tinhTrang = $tinhTrang, m.moTa = $moTa, m.donVi = $donVi";

        String logCypher = "CREATE (ls:LichSuGia {maMon: $maMon, giaCu: $giaCu, giaMoi: $giaMoi, " +
                "ngayThayDoi: $ngay, maNhanVien: $maNV})";

        try (Session session = DBConnect.getSession()) {
            try (Transaction tx = session.beginTransaction()) {
                tx.run(updateCypher, Values.parameters(
                        "maMon", monAn.getMaMon(), "maLoai", monAn.getLoaiMon().getMaLoai(),
                        "tenMon", monAn.getTenMon(), "gia", monAn.getGia(), "anh", monAn.getDuongDanAnh(),
                        "tinhTrang", monAn.getTinhTrang(), "moTa", monAn.getMoTa(), "donVi", monAn.getDonVi()
                ));
                if (Math.abs(monAn.getGia() - giaCu) > 0.001) {
                    tx.run(logCypher, Values.parameters(
                            "maMon", monAn.getMaMon(), "giaCu", giaCu, "giaMoi", monAn.getGia(),
                            "ngay", thoiGianHienTai, "maNV", maNhanVienThucHien
                    ));
                }
                tx.commit();
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<dto.ThongKeMonAnDTO> getThongKeMonAn(java.util.Date tuNgay, java.util.Date denNgay) {
        List<dto.ThongKeMonAnDTO> ketQua = new ArrayList<>();
        StringBuilder cypher = new StringBuilder(
                "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'})-[c:BAO_GOM]->(m:MonAn) WHERE 1=1 "
        );
        Map<String, Object> params = new HashMap<>();

        if (tuNgay != null && denNgay != null) {
            cypher.append("AND hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den ");
            params.put("tu", tuNgay.getTime());
            Calendar cal = Calendar.getInstance();
            cal.setTime(denNgay); cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
            params.put("den", cal.getTimeInMillis());
        }

        cypher.append("RETURN m.maMon AS maMon, m.tenMon AS tenMon, m.gia AS gia, SUM(c.soLuong) AS TongSoLuong ORDER BY TongSoLuong DESC");

        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher.toString(), params);
            while (result.hasNext()) {
                Record r = result.next();
                ketQua.add(new dto.ThongKeMonAnDTO(
                        r.get("maMon").asString(), r.get("tenMon").asString(),
                        r.get("gia").asDouble(), r.get("TongSoLuong").asInt()
                ));
            }
        }
        return ketQua;
    }

    public boolean themDanhSachMonAn(List<MonAn> danhSachMon) {
        List<Map<String, Object>> listData = new ArrayList<>();
        for (MonAn mon : danhSachMon) {
            Map<String, Object> map = new HashMap<>();
            map.put("maMon", mon.getMaMon()); map.put("tenMon", mon.getTenMon());
            map.put("gia", mon.getGia()); map.put("anh", mon.getDuongDanAnh() != null ? mon.getDuongDanAnh() : "/img/default_food.png");
            map.put("tinhTrang", "Đang kinh doanh"); map.put("moTa", mon.getMoTa());
            map.put("donVi", mon.getDonVi()); map.put("maLoai", mon.getLoaiMon().getMaLoai());
            listData.add(map);
        }

        String cypher = "UNWIND $dsMon AS mon " +
                "MATCH (l:LoaiMon {maLoai: mon.maLoai}) " +
                "CREATE (m:MonAn {maMon: mon.maMon, tenMon: mon.tenMon, gia: mon.gia, " +
                "duongDanAnh: mon.anh, tinhTrang: mon.tinhTrang, moTa: mon.moTa, donVi: mon.donVi})-[:THUOC_LOAI]->(l)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("dsMon", listData));
            return true;
        } catch (Exception e) { return false; }
    }

    public List<LichSuGia> getLichSuGia(String maMon) {
        List<LichSuGia> list = new ArrayList<>();
        String cypher = "MATCH (ls:LichSuGia {maMon: $maMon}) " +
                "OPTIONAL MATCH (nv:NhanVien {maNhanVien: ls.maNhanVien}) " +
                "RETURN ls.maMon AS maMon, ls.giaCu AS giaCu, ls.giaMoi AS giaMoi, " +
                "ls.ngayThayDoi AS ngayThayDoi, nv.hoTen AS hoTen ORDER BY ls.ngayThayDoi DESC";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maMon", maMon));
            int fakeId = 1;
            while (result.hasNext()) {
                Record r = result.next();
                list.add(new LichSuGia(
                        fakeId++, r.get("maMon").asString(), r.get("giaCu").asDouble(), r.get("giaMoi").asDouble(),
                        new Timestamp(r.get("ngayThayDoi").asLong()), r.get("hoTen").isNull() ? null : r.get("hoTen").asString()
                ));
            }
        }
        return list;
    }
}
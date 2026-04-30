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

    // Hàm mapping SIÊU CẤP: Chống mọi lỗi ép kiểu và dữ liệu null từ Neo4j
    private MonAn mapMonAn(Record r) {
        String maLoai = r.get("maLoai").isNull() ? "" : r.get("maLoai").asString();
        String tenLoai = r.get("tenLoai").isNull() ? "Chưa phân loại" : r.get("tenLoai").asString();
        LoaiMon loaiMon = new LoaiMon(maLoai, tenLoai);

        double giaMon = 0.0;
        if (!r.get("gia").isNull()) {
            try {
                giaMon = r.get("gia").asDouble();
            } catch (Exception e) {
                try {
                    // Cứu cánh nếu lỡ lưu giá trị 'gia' là Chuỗi ("50000") trong Neo4j
                    giaMon = Double.parseDouble(r.get("gia").asString());
                } catch (Exception ex) {
                    giaMon = 0.0;
                }
            }
        }

        return new MonAn(
                r.get("maMon").isNull() ? "" : r.get("maMon").asString(),
                r.get("tenMon").isNull() ? "Món chưa có tên" : r.get("tenMon").asString(),
                r.get("duongDanAnh").isNull() ? "/img/default_food.png" : r.get("duongDanAnh").asString(),
                giaMon,
                r.get("tinhTrang").isNull() ? "Đang kinh doanh" : r.get("tinhTrang").asString(),
                r.get("moTa").isNull() ? "" : r.get("moTa").asString(),
                r.get("donVi").isNull() ? "" : r.get("donVi").asString(),
                loaiMon
        );
    }

    private final String CYPHER_RETURN = "RETURN m.maMon AS maMon, m.tenMon AS tenMon, m.duongDanAnh AS duongDanAnh, " +
            "m.gia AS gia, m.tinhTrang AS tinhTrang, m.moTa AS moTa, m.donVi AS donVi, " +
            "l.maLoai AS maLoai, l.tenLoai AS tenLoai ";

    public List<MonAn> docDanhSachMon() {
        List<MonAn> ds = new ArrayList<>();
        // Sử dụng OPTIONAL MATCH để không làm mất món ăn nếu chúng chưa có Loại Món
        String cypher = "MATCH (m:Mon) WHERE m.tinhTrang = 'Đang kinh doanh' " +
                "OPTIONAL MATCH (m)-[:THUOC_LOAI]->(l:LoaiMon) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            while (result.hasNext()) {
                try {
                    ds.add(mapMonAn(result.next()));
                } catch (Exception e) {
                    System.err.println("Lỗi parse một dòng món ăn: " + e.getMessage());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public List<MonAn> timKiemMonAn(String tuKhoa) {
        List<MonAn> ds = new ArrayList<>();
        String cypher = "MATCH (m:Mon) WHERE m.tinhTrang = 'Đang kinh doanh' AND " +
                "(toUpper(m.tenMon) CONTAINS toUpper($tuKhoa) OR toUpper(m.maMon) CONTAINS toUpper($tuKhoa)) " +
                "OPTIONAL MATCH (m)-[:THUOC_LOAI]->(l:LoaiMon) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tuKhoa", tuKhoa));
            while (result.hasNext()) {
                try { ds.add(mapMonAn(result.next())); } catch (Exception e) {}
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public List<MonAn> timKiemTheoMa(String maMon) {
        List<MonAn> ds = new ArrayList<>();
        String cypher = "MATCH (m:Mon) WHERE m.tinhTrang = 'Đang kinh doanh' AND toUpper(m.maMon) = toUpper($maMon) " +
                "OPTIONAL MATCH (m)-[:THUOC_LOAI]->(l:LoaiMon) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maMon", maMon));
            while (result.hasNext()) {
                try { ds.add(mapMonAn(result.next())); } catch (Exception e) {}
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public List<MonAn> timKiemTheoTen(String tenMon) {
        List<MonAn> ds = new ArrayList<>();
        String cypher = "MATCH (m:Mon) WHERE m.tinhTrang = 'Đang kinh doanh' AND toUpper(m.tenMon) CONTAINS toUpper($tenMon) " +
                "OPTIONAL MATCH (m)-[:THUOC_LOAI]->(l:LoaiMon) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tenMon", tenMon));
            while (result.hasNext()) {
                try { ds.add(mapMonAn(result.next())); } catch (Exception e) {}
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public List<MonAn> locMonAnTheoLoai(String tenLoai) {
        List<MonAn> ds = new ArrayList<>();
        // Khi lọc theo loại thì bắt buộc phải MATCH chặt chẽ với Loại Món
        String cypher = "MATCH (m:Mon)-[:THUOC_LOAI]->(l:LoaiMon) " +
                "WHERE m.tinhTrang = 'Đang kinh doanh' AND l.tenLoai = $tenLoai " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("tenLoai", tenLoai));
            while (result.hasNext()) {
                try { ds.add(mapMonAn(result.next())); } catch (Exception e) {}
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public String sinhMaMonTuDong() {
        String cypher = "MATCH (m:Mon) WHERE m.maMon STARTS WITH 'MM' RETURN m.maMon AS maxMa ORDER BY maxMa DESC LIMIT 1";
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher);
            if (result.hasNext()) {
                String maxMa = result.next().get("maxMa").asString();
                int soThuTu = Integer.parseInt(maxMa.substring(2)) + 1;
                return String.format("MM%06d", soThuTu);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "MM000001";
    }

    public boolean themMonAn(MonAn monAn) {
        String cypher = "MATCH (l:LoaiMon {maLoai: $maLoai}) " +
                "CREATE (m:Mon {maMon: $maMon, tenMon: $tenMon, gia: $gia, duongDanAnh: $anh, " +
                "tinhTrang: $tinhTrang, moTa: $moTa, donVi: $donVi})-[:THUOC_LOAI]->(l)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters(
                    "maLoai", monAn.getLoaiMon().getMaLoai(), "maMon", monAn.getMaMon(), "tenMon", monAn.getTenMon(),
                    "gia", monAn.getGia(), "anh", monAn.getDuongDanAnh(), "tinhTrang", monAn.getTinhTrang(),
                    "moTa", monAn.getMoTa(), "donVi", monAn.getDonVi()
            ));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public MonAn timMotMonTheoMa(String maMon) {
        String cypher = "MATCH (m:Mon) WHERE toUpper(m.maMon) = toUpper($maMon) " +
                "OPTIONAL MATCH (m)-[:THUOC_LOAI]->(l:LoaiMon) " + CYPHER_RETURN;
        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher, Values.parameters("maMon", maMon));
            if (result.hasNext()) return mapMonAn(result.next());
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean xoaMem(String maMon) {
        String cypher = "MATCH (m:Mon {maMon: $maMon}) SET m.tinhTrang = 'Ngừng kinh doanh'";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("maMon", maMon));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean capNhatMonAn(MonAn monAn, String maNhanVienThucHien) {
        MonAn monCu = timMotMonTheoMa(monAn.getMaMon());
        if (monCu == null) return false;

        double giaCu = monCu.getGia();
        long thoiGianHienTai = System.currentTimeMillis();

        // Sử dụng OPTIONAL MATCH r để phòng trường hợp món ăn bị lỗi mồ côi loại
        String updateCypher = "MATCH (m:Mon {maMon: $maMon}) " +
                "OPTIONAL MATCH (m)-[r:THUOC_LOAI]->() " +
                "DELETE r " +
                "WITH m " +
                "MATCH (l:LoaiMon {maLoai: $maLoai}) " +
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

        // ĐÃ SỬA LỖI: Đổi (m:MonAn) thành (m:Mon)
        StringBuilder cypher = new StringBuilder(
                "MATCH (hd:HoaDon {trangThai: 'Đã thanh toán'})-[c:BAO_GOM]->(m:Mon) WHERE 1=1 "
        );
        Map<String, Object> params = new HashMap<>();

        if (tuNgay != null && denNgay != null) {
            cypher.append("AND hd.ngayLapHoaDon >= $tu AND hd.ngayLapHoaDon <= $den ");
            params.put("tu", tuNgay.getTime());

            // Lấy đến cuối ngày của mốc 'đến ngày'
            Calendar cal = Calendar.getInstance();
            cal.setTime(denNgay);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            params.put("den", cal.getTimeInMillis());
        }

        cypher.append("RETURN m.maMon AS maMon, m.tenMon AS tenMon, m.gia AS gia, SUM(c.soLuong) AS TongSoLuong ORDER BY TongSoLuong DESC");

        try (Session session = DBConnect.getSession()) {
            Result result = session.run(cypher.toString(), params);
            while (result.hasNext()) {
                try {
                    Record r = result.next();
                    // Đọc an toàn chống lỗi ép kiểu dữ liệu từ Neo4j
                    double gia = 0.0;
                    try { gia = r.get("gia").asDouble(); } catch (Exception e) {
                        try { gia = Double.parseDouble(r.get("gia").asString()); } catch (Exception ex) {}
                    }

                    ketQua.add(new dto.ThongKeMonAnDTO(
                            r.get("maMon").asString(),
                            r.get("tenMon").asString(),
                            gia,
                            r.get("TongSoLuong").asInt()
                    ));
                } catch (Exception e) {
                    System.err.println("Lỗi đọc dữ liệu thống kê: " + e.getMessage());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
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
                "CREATE (m:Mon {maMon: mon.maMon, tenMon: mon.tenMon, gia: mon.gia, " +
                "duongDanAnh: mon.anh, tinhTrang: mon.tinhTrang, moTa: mon.moTa, donVi: mon.donVi})-[:THUOC_LOAI]->(l)";
        try (Session session = DBConnect.getSession()) {
            session.run(cypher, Values.parameters("dsMon", listData));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
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
                try {
                    Record r = result.next();
                    list.add(new LichSuGia(
                            fakeId++, r.get("maMon").asString(), r.get("giaCu").asDouble(), r.get("giaMoi").asDouble(),
                            new Timestamp(r.get("ngayThayDoi").asLong()), r.get("hoTen").isNull() ? null : r.get("hoTen").asString()
                    ));
                } catch (Exception e) {}
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
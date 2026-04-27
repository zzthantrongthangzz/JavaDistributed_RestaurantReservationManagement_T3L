package entity;

import java.util.Objects;

public class MonAn {
    private String maMon;
    private String tenMon;
    private String duongDanAnh;
    private double gia;
    private String tinhTrang;
    private String moTa;
    private String donVi;
    private LoaiMon loaiMon;

    public MonAn() {
    }

    public MonAn(String maMon, String tenMon, String duongDanAnh, double gia, String tinhTrang, String moTa,
                 String donVi, LoaiMon loaiMon) { 
        setMaMon(maMon);
        setTenMon(tenMon);
        setDuongDanAnh(duongDanAnh);
        setGia(gia);
        setTinhTrang(tinhTrang);
        setMoTa(moTa);
        setDonVi(donVi);
        setLoaiMon(loaiMon); 
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        if (maMon == null || maMon.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã món không được để trống.");
        }
        if (!maMon.matches("^MM\\d{6}$")) {
            throw new IllegalArgumentException("Mã món không hợp lệ (phải có dạng MMxxxxxx, ví dụ: MM000001).");
        }
        this.maMon = maMon.trim();
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        if (tenMon == null || tenMon.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên món không được để trống.");
        }
        String t = tenMon.trim();
        this.tenMon = t;
    }

    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        if (duongDanAnh == null || duongDanAnh.trim().isEmpty()) {
             this.duongDanAnh = "/img/default_food.png";
        } else {
            String path = duongDanAnh.trim();
            if (path.length() > 255) {
                throw new IllegalArgumentException("Đường dẫn ảnh quá dài (tối đa 255 ký tự).");
            }
            this.duongDanAnh = path;
        }
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        if (gia < 0) {
            throw new IllegalArgumentException("Giá bán không được âm.");
        }
        if (gia > 9999999.999) {
            throw new IllegalArgumentException("Giá bán vượt quá giới hạn lưu trữ (tối đa khoảng 10 triệu).");
        }
        this.gia = gia;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        if (tinhTrang == null || tinhTrang.trim().isEmpty()) {
            throw new IllegalArgumentException("Tình trạng không được để trống.");
        }
        String tt = tinhTrang.trim();
        if (!tt.equals("Đang kinh doanh") && !tt.equals("Ngừng kinh doanh")) {
            throw new IllegalArgumentException("Tình trạng không hợp lệ. Chỉ chấp nhận 'Đang kinh doanh' hoặc 'Ngừng kinh doanh'.");
        }
        
        this.tinhTrang = tt;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        if (moTa == null) {
            this.moTa = "";
        } else {
            this.moTa = moTa;
        }
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        if (donVi == null || donVi.trim().isEmpty()) {
            throw new IllegalArgumentException("Đơn vị tính không được để trống.");
        }
        String d = donVi.trim();
        this.donVi = d;
    }

    public LoaiMon getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(LoaiMon loaiMon) { 
        if (loaiMon != null) {
            this.loaiMon = loaiMon;
        } else {
            throw new IllegalArgumentException("Loại món không được để trống.");
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(maMon);
    }

    @Override
    public String toString() {
        return tenMon;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        MonAn other = (MonAn) obj;
        return Objects.equals(maMon, other.maMon);
    }
}
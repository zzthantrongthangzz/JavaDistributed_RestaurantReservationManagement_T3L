package entity;

import java.math.BigDecimal;

public class ChiTietHoaDon {
    private String maHoaDon;
    private String maMon;
    private int soLuong;
    private BigDecimal donGia;
    // private BigDecimal chietKhau;
    // private BigDecimal soTienKhachTra; 
    // private BigDecimal soTienThoi; 

    // ===== Constructors =====
    public ChiTietHoaDon() {}

    // Constructor đã cập nhật
    public ChiTietHoaDon(String maHoaDon, String maMon, int soLuong, BigDecimal donGia) {
        this.maHoaDon = maHoaDon;
        this.maMon = maMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    // ===== Getters & Setters =====
    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }


    // ===== Phương thức tính toán phụ trợ (tuỳ chọn) =====
    public BigDecimal tinhThanhTien() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", maMon='" + maMon + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                '}';
    }
}
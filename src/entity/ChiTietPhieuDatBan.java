package entity;

import java.math.BigDecimal;

public class ChiTietPhieuDatBan {
    private String maPhieuDatBan; 
    private String maMon;
    private int soLuong;
    private BigDecimal donGia;
    // private BigDecimal chietKhau;

    // Constructors
    public ChiTietPhieuDatBan() {
    }

    // Constructor đã cập nhật
    public ChiTietPhieuDatBan(String maPhieuDatBan, String maMon, int soLuong, BigDecimal donGia) {
        this.maPhieuDatBan = maPhieuDatBan;
        this.maMon = maMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    // Getters và Setters
    public String getMaPhieuDatBan() {
        return maPhieuDatBan;
    }

    public void setMaPhieuDatBan(String maPhieuDatBan) {
        this.maPhieuDatBan = maPhieuDatBan;
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
    

    @Override
    public String toString() {
        return "ChiTietPhieuDatBan{" + "maPhieuDatBan=" + maPhieuDatBan + ", maMon=" + maMon + ", soLuong=" + soLuong + ", donGia=" + donGia + '}';
    }
    
    public BigDecimal tinhThanhTien() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }
}
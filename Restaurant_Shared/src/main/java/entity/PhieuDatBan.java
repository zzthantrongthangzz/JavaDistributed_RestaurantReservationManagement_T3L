package entity;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class PhieuDatBan implements Serializable{
    private static final long serialVersionUID = 1L;
    private String maPhieuDatBan;
    private Date thoiGianDat;
    private String trangThai;
    private String maKhachHang;
    private String maNhanVien;
    private double tienDatCoc; 
    private String ghiChu;

    public PhieuDatBan() {
    }

    public PhieuDatBan(String maPhieuDatBan, Date thoiGianDat, String trangThai, 
                       String maKhachHang, String maNhanVien, double tienDatCoc, String ghiChu) {
        this.maPhieuDatBan = maPhieuDatBan;
        this.thoiGianDat = thoiGianDat;
        this.trangThai = trangThai;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.tienDatCoc = tienDatCoc;
        this.ghiChu = ghiChu;
    }

    // ========== GETTERS AND SETTERS ==========
    public String getMaPhieuDatBan() {
        return maPhieuDatBan;
    }

    public void setMaPhieuDatBan(String maPhieuDatBan) {
        this.maPhieuDatBan = maPhieuDatBan;
    }

    public Date getThoiGianDat() {
        return thoiGianDat;
    }

    public void setThoiGianDat(Date thoiGianDat) {
        this.thoiGianDat = thoiGianDat;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public double getTienDatCoc() {
        return tienDatCoc;
    }

    public void setTienDatCoc(double tienDatCoc) {
        this.tienDatCoc = tienDatCoc;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuDatBan);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhieuDatBan other = (PhieuDatBan) obj;
        return Objects.equals(maPhieuDatBan, other.maPhieuDatBan);
    }
}
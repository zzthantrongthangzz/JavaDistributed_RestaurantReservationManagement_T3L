package entity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class HoaDon implements Serializable{
    private static final long serialVersionUID = 1L;
    private String maHoaDon;
    private String trangThai;
    private Date ngayLapHoaDon;
    private BigDecimal thue; 
    private String maNhanVien;
    private String maPhieuDatBan;
    private String maKhachHang;
    private String maKhuyenMai;
    private String ghiChu;
    private String diaChi;
    private BigDecimal tienDatCoc;
    private BigDecimal soTienKhachTra; 
    private BigDecimal soTienThoi; 
    // ===== Constructors =====
    public HoaDon() {
    }

    // Constructor đã cập nhật
    public HoaDon(String maHoaDon, String trangThai, Date ngayLapHoaDon, BigDecimal thue, 
                  String maNhanVien, String maPhieuDatBan, String maKhachHang, 
                  String maKhuyenMai, String diaChi, BigDecimal tienDatCoc, 
                  BigDecimal soTienKhachTra, BigDecimal soTienThoi) {
        this.maHoaDon = maHoaDon;
        this.trangThai = trangThai;
        this.ngayLapHoaDon = ngayLapHoaDon;
        this.thue = thue;
        this.maNhanVien = maNhanVien;
        this.maPhieuDatBan = maPhieuDatBan;
        this.maKhachHang = maKhachHang;
        this.maKhuyenMai = maKhuyenMai;
        this.diaChi = diaChi;
        this.tienDatCoc = tienDatCoc;
        this.soTienKhachTra = soTienKhachTra;
        this.soTienThoi = soTienThoi;
    }

    // Getters & Setters
    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Date getNgayLapHoaDon() {
        return ngayLapHoaDon;
    }

    public void setNgayLapHoaDon(Date ngayLapHoaDon) {
        this.ngayLapHoaDon = ngayLapHoaDon;
    }
    

    public BigDecimal getThue() {
        return thue;
    }

    public void setThue(BigDecimal thue) { 
        this.thue = thue;
    }


    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getMaPhieuDatBan() {
        return maPhieuDatBan;
    }

    public void setMaPhieuDatBan(String maPhieuDatBan) {
        this.maPhieuDatBan = maPhieuDatBan;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }
    
    public String getGhiChu() { 
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    
    public BigDecimal getTienDatCoc() { 
        return tienDatCoc;
    }

    public void setTienDatCoc(BigDecimal tienDatCoc) { 
        this.tienDatCoc = tienDatCoc;
    }

    public BigDecimal getSoTienKhachTra() { 
        return soTienKhachTra;
    }

    public void setSoTienKhachTra(BigDecimal soTienKhachTra) { 
        this.soTienKhachTra = soTienKhachTra;
    }

    public BigDecimal getSoTienThoi() {
        return soTienThoi;
    }

    public void setSoTienThoi(BigDecimal soTienThoi) {
        this.soTienThoi = soTienThoi;
    }
    // ===== toString =====
    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", ngayLapHoaDon=" + ngayLapHoaDon +
                ", thue=" + thue +
                ", maNhanVien='" + maNhanVien + '\'' +
                ", maPhieuDatBan='" + maPhieuDatBan + '\'' +
                ", maKhachHang='" + maKhachHang + '\'' +
                ", maKhuyenMai='" + maKhuyenMai + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", tienDatCoc=" + tienDatCoc +
                ", soTienKhachTra=" + soTienKhachTra +
                ", soTienThoi=" + soTienThoi +
                '}';
    }
}
package entity;
import java.io.Serializable;
import java.util.Date;

public class LichSuHuyDatBan implements Serializable{
    private static final long serialVersionUID = 1L;
    private int maLog;
    private String maPhieuDatBan;
    private String tenBan;          
    private String tenKhachHang;    
    private String sdtKhachHang;    
    private String maNhanVien;
    private String tenNhanVien;     
    private Date thoiGianHuy;
    private String lyDoHuy;

    public LichSuHuyDatBan() {
    }

    // Constructor đầy đủ
    public LichSuHuyDatBan(String maPhieuDatBan, String tenBan, String tenKhachHang, 
                           String sdtKhachHang, String maNhanVien, String tenNhanVien, String lyDoHuy) {
        this.maPhieuDatBan = maPhieuDatBan;
        this.tenBan = tenBan;
        this.tenKhachHang = tenKhachHang;
        this.sdtKhachHang = sdtKhachHang;
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.lyDoHuy = lyDoHuy;
        this.thoiGianHuy = new Date(); 
    }

    // Getters and Setters
    public int getMaLog() { return maLog; }
    public void setMaLog(int maLog) { this.maLog = maLog; }
    
    public String getMaPhieuDatBan() { return maPhieuDatBan; }
    public void setMaPhieuDatBan(String maPhieuDatBan) { this.maPhieuDatBan = maPhieuDatBan; }
    
    public String getTenBan() { return tenBan; }
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }
    
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    
    public String getSdtKhachHang() { return sdtKhachHang; }
    public void setSdtKhachHang(String sdtKhachHang) { this.sdtKhachHang = sdtKhachHang; }
    
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    
    public Date getThoiGianHuy() { return thoiGianHuy; }
    public void setThoiGianHuy(Date thoiGianHuy) { this.thoiGianHuy = thoiGianHuy; }
    
    public String getLyDoHuy() { return lyDoHuy; }
    public void setLyDoHuy(String lyDoHuy) { this.lyDoHuy = lyDoHuy; }
}
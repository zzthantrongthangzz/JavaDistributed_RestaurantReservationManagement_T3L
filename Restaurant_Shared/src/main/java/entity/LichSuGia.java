package entity;
import java.io.Serializable;
import java.util.Date;

public class LichSuGia implements Serializable{
    private static final long serialVersionUID = 1L;
    private int maLog;
    private String maMon;
    private double giaCu;
    private double giaMoi;
    private Date ngayThayDoi;
    private String tenNhanVien;

    public LichSuGia(int maLog, String maMon, double giaCu, double giaMoi, Date ngayThayDoi, String tenNhanVien) {
        this.maLog = maLog;
        this.maMon = maMon;
        this.giaCu = giaCu;
        this.giaMoi = giaMoi;
        this.ngayThayDoi = ngayThayDoi;
        this.tenNhanVien = tenNhanVien;
    }

    // Getter và Setter
    public int getMaLog() { return maLog; }
    public String getMaMon() { return maMon; }
    public double getGiaCu() { return giaCu; }
    public double getGiaMoi() { return giaMoi; }
    public Date getNgayThayDoi() { return ngayThayDoi; }
    public String getTenNhanVien() { return tenNhanVien; }
}
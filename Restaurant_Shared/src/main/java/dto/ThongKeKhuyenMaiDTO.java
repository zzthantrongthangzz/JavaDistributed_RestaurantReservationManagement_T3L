package dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ThongKeKhuyenMaiDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private int soLuotSuDung;
    private BigDecimal tongTienGiam;
    private Date ngayBatDau;
    private Date ngayKetThuc;

    public ThongKeKhuyenMaiDTO(String maKhuyenMai, String tenKhuyenMai, int soLuotSuDung, BigDecimal tongTienGiam, Date ngayBatDau, Date ngayKetThuc) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.soLuotSuDung = soLuotSuDung;
        this.tongTienGiam = tongTienGiam;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public String getTenKhuyenMai() { return tenKhuyenMai; }
    public int getSoLuotSuDung() { return soLuotSuDung; }
    public BigDecimal getTongTienGiam() { return tongTienGiam; }
    public Date getNgayBatDau() { return ngayBatDau; }
    public Date getNgayKetThuc() { return ngayKetThuc; }
}
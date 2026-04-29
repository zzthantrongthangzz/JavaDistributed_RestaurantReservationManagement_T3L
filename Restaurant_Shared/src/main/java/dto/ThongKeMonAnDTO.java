package dto;
import java.io.Serializable;

public class ThongKeMonAnDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maMon;
    private String tenMon;
    private double gia;
    private int tongSoLuong;

    public ThongKeMonAnDTO(String maMon, String tenMon, double gia, int tongSoLuong) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.gia = gia;
        this.tongSoLuong = tongSoLuong;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public int getTongSoLuong() {
        return tongSoLuong;
    }

    public void setTongSoLuong(int tongSoLuong) {
        this.tongSoLuong = tongSoLuong;
    }
}
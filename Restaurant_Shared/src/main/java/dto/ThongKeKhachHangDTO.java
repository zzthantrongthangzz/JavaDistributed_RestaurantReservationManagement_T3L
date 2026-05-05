package dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ThongKeKhachHangDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private BigDecimal tongChiTieu;
    // Bỏ thuộc tính soLuotMua theo yêu cầu
    private int tichDiem;

    // Cập nhật lại constructor
    public ThongKeKhachHangDTO(String maKhachHang, String hoTen, String soDienThoai, BigDecimal tongChiTieu, int soLuotMua_BaoLuu, int tichDiem) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.tongChiTieu = tongChiTieu;
        // Biến soLuotMua_BaoLuu vẫn nhận vào từ DAO nhưng không lưu lại để tránh lỗi tham số
        this.tichDiem = tichDiem;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public BigDecimal getTongChiTieu() {
        return tongChiTieu;
    }

    public void setTongChiTieu(BigDecimal tongChiTieu) {
        this.tongChiTieu = tongChiTieu;
    }

    public int getTichDiem() {
        return tichDiem;
    }

    public void setTichDiem(int tichDiem) {
        this.tichDiem = tichDiem;
    }
}
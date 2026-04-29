package dto;
import java.io.Serializable;
import java.math.BigDecimal;

public class ThongKeKhachHangDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String hoTen;
    private BigDecimal tongChiTieu;
    private int tichDiem;

    public ThongKeKhachHangDTO(String hoTen, BigDecimal tongChiTieu, int tichDiem) {
        this.hoTen = hoTen;
        this.tongChiTieu = tongChiTieu;
        this.tichDiem = tichDiem;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
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
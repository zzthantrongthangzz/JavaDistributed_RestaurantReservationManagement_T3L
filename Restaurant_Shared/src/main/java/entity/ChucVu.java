package entity;
import java.io.Serializable;
import java.util.Objects;

public class ChucVu implements Serializable{
    private static final long serialVersionUID = 1L;
    private String maChucVu; 
    private String tenChucVu;
    public ChucVu() {
    }

    public ChucVu(String maChucVu) {
        setMaChucVu(maChucVu);
    }

    public ChucVu(String maChucVu, String tenChucVu) {
        setMaChucVu(maChucVu);
        setTenChucVu(tenChucVu);
    }

    public String getMaChucVu() {
        return maChucVu;
    }

    public void setMaChucVu(String maChucVu) {
        if (maChucVu == null || maChucVu.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chức vụ không được để trống.");
        }
        String ma = maChucVu.trim();
        this.maChucVu = ma;
    }

    public String getTenChucVu() {
        return tenChucVu;
    }

    public void setTenChucVu(String tenChucVu) {
        if (tenChucVu == null || tenChucVu.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chức vụ không được để trống.");
        }
        String ten = tenChucVu.trim();
        this.tenChucVu = ten;
    }

    @Override
    public String toString() {
        return tenChucVu;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChucVu other = (ChucVu) obj;
        return Objects.equals(maChucVu, other.maChucVu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChucVu);
    }
}
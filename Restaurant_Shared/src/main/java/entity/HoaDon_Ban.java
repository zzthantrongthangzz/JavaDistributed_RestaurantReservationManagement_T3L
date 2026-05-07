package entity;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity ánh xạ bảng HoaDon_Ban, chứa khóa chính tổng hợp (maHoaDon, maBan)
 */
public class HoaDon_Ban implements Serializable{
    private static final long serialVersionUID = 1L;
    private String maHoaDon; 
    private String maBan;   

    public HoaDon_Ban() {
    }

    public HoaDon_Ban(String maHoaDon, String maBan) {
        this.maHoaDon = maHoaDon;
        this.maBan = maBan;
    }

    // ========== GETTERS AND SETTERS ==========

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }
    
    // ========== HASHCODE & EQUALS ==========

    @Override
    public int hashCode() {
        // Hàm băm dựa trên cả hai thuộc tính
        return Objects.hash(maHoaDon, maBan);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final HoaDon_Ban other = (HoaDon_Ban) obj;
        // So sánh bằng nhau dựa trên cả hai thuộc tính
        return Objects.equals(this.maHoaDon, other.maHoaDon) && 
               Objects.equals(this.maBan, other.maBan);
    }

    @Override
    public String toString() {
        return "HoaDon_Ban{" + "maHoaDon=" + maHoaDon + ", maBan=" + maBan + '}';
    }
}
package entity;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity ánh xạ bảng PhieuDatBan_Ban, chứa khóa chính tổng hợp (maPhieuDatBan, maBan)
 */
public class PhieuDatBan_Ban implements Serializable{
    private static final long serialVersionUID = 1L;
    private String maPhieuDatBan; 
    private String maBan;      

    public PhieuDatBan_Ban() {
    }

    public PhieuDatBan_Ban(String maPhieuDatBan, String maBan) {
        this.maPhieuDatBan = maPhieuDatBan;
        this.maBan = maBan;
    }

    // ========== GETTERS AND SETTERS ==========

    public String getMaPhieuDatBan() {
        return maPhieuDatBan;
    }

    public void setMaPhieuDatBan(String maPhieuDatBan) {
        this.maPhieuDatBan = maPhieuDatBan;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }
    
    // ========== HASHCODE & EQUALS  ==========

    @Override
    public int hashCode() {
        // Hàm băm dựa trên cả hai thuộc tính
        return Objects.hash(maPhieuDatBan, maBan);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PhieuDatBan_Ban other = (PhieuDatBan_Ban) obj;
        // So sánh bằng nhau dựa trên cả hai thuộc tính
        return Objects.equals(this.maPhieuDatBan, other.maPhieuDatBan) && 
               Objects.equals(this.maBan, other.maBan);
    }

    @Override
    public String toString() {
        return "PhieuDatBan_Ban{" + "maPhieuDatBan=" + maPhieuDatBan + ", maBan=" + maBan + '}';
    }
}
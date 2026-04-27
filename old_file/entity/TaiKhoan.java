package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TaiKhoan {
    private String maNhanVien;
    private String taiKhoan;
    private String matKhau;
    private LocalDate ngayTaoTK;

    public TaiKhoan() {
    }

    public TaiKhoan(String maNhanVien, String taiKhoan, String matKhau, LocalDate ngayTaoTK) {
        setMaNhanVien(maNhanVien);
        setTaiKhoan(taiKhoan);
        setMatKhau(matKhau);
        setNgayTaoTK(ngayTaoTK);
    }

    // --- GETTER & SETTER ---

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được rỗng.");
        }
        this.maNhanVien = maNhanVien;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        if (taiKhoan == null || taiKhoan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tài khoản không được rỗng.");
        }
        this.taiKhoan = taiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được rỗng.");
        }
        this.matKhau = matKhau;
    }

    public LocalDate getNgayTaoTK() {
        return ngayTaoTK;
    }

    public void setNgayTaoTK(LocalDate ngayTaoTK) {
        if (ngayTaoTK == null) {
            throw new IllegalArgumentException("Ngày tạo tài khoản không được để trống.");
        }
        this.ngayTaoTK = ngayTaoTK;
    }

    // --- HashCode, Equals, ToString ---

    @Override
    public int hashCode() {
        return Objects.hash(taiKhoan);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaiKhoan other = (TaiKhoan) obj;
        return Objects.equals(taiKhoan, other.taiKhoan);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "TaiKhoan [user=" + taiKhoan + ", nv=" + maNhanVien + ", ngayTao=" 
               + (ngayTaoTK != null ? ngayTaoTK.format(formatter) : "null") + "]";
    }
}
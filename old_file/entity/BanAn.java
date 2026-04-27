package entity;

import java.util.Objects;

public class BanAn {

    private String maBan;      
    private String tenBan;     
    private String loaiBan;    
    private int sucChua;       
    private String trangThai;  
    private String maKhu;      
    private String tenKhu;
    private String tenTang;
    private String tenKhachHang; 

    public BanAn() {
    }

    public BanAn(String maBan) {
        setMaBan(maBan);
    }

    public BanAn(String maBan, String tenBan, String loaiBan, int sucChua, String trangThai, String maKhu) {
        setMaBan(maBan);
        setTenBan(tenBan);
        setLoaiBan(loaiBan);
        setSucChua(sucChua);
        setTrangThai(trangThai);
        setMaKhu(maKhu);
    }

    public BanAn(String maBan, String tenBan, String loaiBan, int sucChua, String trangThai, String maKhu, String tenKhu, String tenTang) {
        this(maBan, tenBan, loaiBan, sucChua, trangThai, maKhu);
        this.tenKhu = tenKhu;
        this.tenTang = tenTang;
    }
    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        if (maBan == null || maBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bàn không được để trống.");
        }
        if (maBan.trim().length() > 8) {
            throw new IllegalArgumentException("Mã bàn tối đa 8 ký tự.");
        }
        this.maBan = maBan.trim();
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên bàn không được để trống.");
        }
        if (tenBan.trim().length() > 30) {
            throw new IllegalArgumentException("Tên bàn tối đa 30 ký tự.");
        }
        this.tenBan = tenBan.trim();
    }

    public String getLoaiBan() {
        return loaiBan;
    }

    public void setLoaiBan(String loaiBan) {
        if (loaiBan == null || loaiBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại bàn không được để trống.");
        }
        if (loaiBan.trim().length() > 20) {
            throw new IllegalArgumentException("Loại bàn tối đa 20 ký tự.");
        }
        this.loaiBan = loaiBan.trim();
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        if (sucChua <= 0) {
            throw new IllegalArgumentException("Sức chứa phải lớn hơn 0.");
        }
        this.sucChua = sucChua;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống.");
        }
        if (trangThai.trim().length() > 30) {
            throw new IllegalArgumentException("Trạng thái tối đa 30 ký tự.");
        }
        this.trangThai = trangThai.trim();
    }

    public String getMaKhu() {
        return maKhu;
    }

    public void setMaKhu(String maKhu) {
        if (maKhu == null || maKhu.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khu không được để trống.");
        }
        if (maKhu.trim().length() > 8) {
            throw new IllegalArgumentException("Mã khu tối đa 8 ký tự.");
        }
        this.maKhu = maKhu.trim();
    }

    public String getTenKhu() { return tenKhu; }
    public void setTenKhu(String tenKhu) { this.tenKhu = tenKhu; }

    public String getTenTang() { return tenTang; }
    public void setTenTang(String tenTang) { this.tenTang = tenTang; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    @Override
    public int hashCode() {
        return Objects.hash(maBan);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BanAn other = (BanAn) obj;
        return Objects.equals(maBan, other.maBan);
    }

    @Override
    public String toString() {
        return tenBan;
    }
}
package entity;

import java.util.Objects;

public class Khu {
    private String maKhu;
    private String tenKhu;
    private String maTang;

    public Khu() {
    }

    public Khu(String maKhu, String tenKhu, String maTang) {
        setMaKhu(maKhu);
        setTenKhu(tenKhu);
        setMaTang(maTang);
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

    public String getTenKhu() {
        return tenKhu;
    }

    public void setTenKhu(String tenKhu) {
        if (tenKhu == null || tenKhu.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khu không được để trống.");
        }
        String k = tenKhu.trim();
        if (!k.matches("^(Khu\\s)?[A-Z]$")) {
            throw new IllegalArgumentException("Tên khu không hợp lệ. Phải là chữ cái in hoa (VD: 'A', 'B') hoặc 'Khu' + chữ cái (VD: 'Khu A').");
        }
        this.tenKhu = k;
    }

    public String getMaTang() {
        return maTang;
    }

    public void setMaTang(String maTang) {
        if (maTang == null || maTang.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tầng không được để trống.");
        }
        this.maTang = maTang.trim();
    }

    @Override
    public String toString() {
        return tenKhu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Khu khu = (Khu) o;
        return Objects.equals(maKhu, khu.maKhu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhu);
    }
}
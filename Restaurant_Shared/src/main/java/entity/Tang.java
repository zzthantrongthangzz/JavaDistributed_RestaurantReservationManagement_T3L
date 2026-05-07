package entity;
import java.io.Serializable;
import java.util.Objects;

public class Tang implements Serializable{
    private static final long serialVersionUID = 1L;
    private String maTang;
    private String tenTang;
    public Tang() {
    }

    public Tang(String maTang, String tenTang) {
        setMaTang(maTang);
        setTenTang(tenTang);
    }

    public String getMaTang() {
        return maTang;
    }

    public void setMaTang(String maTang) {
        if (maTang == null || maTang.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tầng không được để trống.");
        }
        if (maTang.trim().length() > 8) {
            throw new IllegalArgumentException("Mã tầng tối đa 8 ký tự.");
        }
        this.maTang = maTang.trim();
    }

    public String getTenTang() {
        return tenTang;
    }

    public void setTenTang(String tenTang) {
        if (tenTang == null || tenTang.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tầng không được để trống.");
        }
        
        String t = tenTang.trim();
        if (!t.matches("^(Tầng\\s)?\\d+$")) {
            throw new IllegalArgumentException("Tên tầng không hợp lệ. Phải là số (VD: '1', '2') hoặc 'Tầng' + số (VD: 'Tầng 1').");
        }
        this.tenTang = t;
    }

    @Override
    public String toString() {
        return tenTang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tang tang = (Tang) o;
        return Objects.equals(maTang, tang.maTang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTang);
    }
}
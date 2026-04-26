package entity;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

public class KhachHang implements Serializable{
    private static final long serialVersionUID = 1L;
    private String maKhachHang;
    private String hoTen;   
    private String soDienThoai;
    private String email;      
    private String diaChi;     
    private Date ngaySinh;   
    private boolean gioiTinh;  
    private int tichDiem;     
    private boolean trangThai; 

    public KhachHang() {
    }
    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, boolean gioiTinh, int tichDiem) {
        setMaKhachHang(maKhachHang);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setGioiTinh(gioiTinh);
        setTichDiem(tichDiem);
        this.trangThai = true; 
    }

    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, String email, String diaChi, Date ngaySinh, boolean gioiTinh, int tichDiem) {
        setMaKhachHang(maKhachHang);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        setDiaChi(diaChi);
        setNgaySinh(ngaySinh);
        setGioiTinh(gioiTinh);
        setTichDiem(tichDiem);
        this.trangThai = true;
    }
    
    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, String email, String diaChi, Date ngaySinh, boolean gioiTinh, int tichDiem, boolean trangThai) {
        setMaKhachHang(maKhachHang);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        setDiaChi(diaChi);
        setNgaySinh(ngaySinh);
        setGioiTinh(gioiTinh);
        setTichDiem(tichDiem);
        setTrangThai(trangThai);
    }


    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khách hàng không được để trống.");
        }
        String ma = maKhachHang.trim();
        this.maKhachHang = ma;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        String ten = hoTen.trim();
        this.hoTen = ten;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        String sdt = soDienThoai.trim();
        this.soDienThoai = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            this.email = null;
        } else {
            String em = email.trim();
            if (em.length() > 50) {
                throw new IllegalArgumentException("Email quá dài (tối đa 50 ký tự).");
            }
            this.email = em;
        }
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty()) {
            this.diaChi = null;
        } else {
            String dc = diaChi.trim();
            if (dc.length() > 100) {
                throw new IllegalArgumentException("Địa chỉ quá dài (tối đa 100 ký tự).");
            }
            this.diaChi = dc;
        }
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public int getTichDiem() {
        return tichDiem;
    }

    public void setTichDiem(int tichDiem) {
        if (tichDiem < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm.");
        }
        this.tichDiem = tichDiem;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public String getGioiTinhString() {
        return gioiTinh ? "Nam" : "Nữ";
    }

    @Override
    public String toString() {
        return hoTen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang khachHang = (KhachHang) o;
        return Objects.equals(maKhachHang, khachHang.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }
}
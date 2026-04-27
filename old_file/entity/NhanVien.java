package entity;

import java.sql.Date;
import java.util.Objects;
import java.util.regex.Pattern;

public class NhanVien {
    private String maNhanVien;
    private String hoTen;     
    private boolean gioiTinh; 
    private String soDienThoai; 
    private String email;       
    private Date ngaySinh;    
    private String diaChi;      
    private ChucVu chucVu;      
    private String trangThai;

    public NhanVien() {
    }

    public NhanVien(String maNhanVien, String hoTen, boolean gioiTinh, 
                    String soDienThoai, String email, Date ngaySinh, 
                    String diaChi, ChucVu chucVu) {
        setMaNhanVien(maNhanVien);
        setHoTen(hoTen);
        setGioiTinh(gioiTinh);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        setNgaySinh(ngaySinh);
        setDiaChi(diaChi);
        setChucVu(chucVu);
    }

    public NhanVien(String maNhanVien, String hoTen, ChucVu chucVu) { 
        setMaNhanVien(maNhanVien);
        setHoTen(hoTen);
        setChucVu(chucVu); 
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống.");
        }
        String ma = maNhanVien.trim();
        if (ma.length() > 8) {
            throw new IllegalArgumentException("Mã nhân viên quá dài (tối đa 8 ký tự).");
        }
        this.maNhanVien = ma;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        String ten = hoTen.trim();
        if (ten.length() > 40) {
            throw new IllegalArgumentException("Họ tên quá dài (tối đa 40 ký tự).");
        }
        this.hoTen = ten;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        String sdt = soDienThoai.trim();
        // SQL: nvarchar(20)
        if (sdt.length() > 20) {
            throw new IllegalArgumentException("Số điện thoại quá dài (tối đa 20 ký tự).");
        }
        // Kiểm tra phải là số
        if (!sdt.matches("^\\d+$")) {
            throw new IllegalArgumentException("Số điện thoại chỉ được chứa ký tự số.");
        }
        this.soDienThoai = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }
        String em = email.trim();
        if (em.length() > 40) {
            throw new IllegalArgumentException("Email quá dài (tối đa 40 ký tự).");
        }       
        this.email = em;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        if (ngaySinh == null) {
            throw new IllegalArgumentException("Ngày sinh không được để trống.");
        }
        this.ngaySinh = ngaySinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được để trống.");
        }
        String dc = diaChi.trim();
        if (dc.length() > 50) {
            throw new IllegalArgumentException("Địa chỉ quá dài (tối đa 50 ký tự).");
        }
        this.diaChi = dc;
    }

    public ChucVu getChucVu() {
        return chucVu;
    }

    public void setChucVu(ChucVu chucVu) {
        if (chucVu == null) {
            throw new IllegalArgumentException("Chức vụ không được để trống.");
        }
        this.chucVu = chucVu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return hoTen;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NhanVien other = (NhanVien) obj;
        // So sánh dựa trên Mã Nhân Viên
        return maNhanVien != null && maNhanVien.equals(other.maNhanVien);
    }

    @Override
    public int hashCode() {
        return maNhanVien != null ? maNhanVien.hashCode() : 0;
    }
    
}
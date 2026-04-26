package entity;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class KhuyenMai implements Serializable{
    private static final long serialVersionUID = 1L;
    private String maKhuyenMai;
    private String tenKhuyenMai; 
    private String loaiKhuyenMai;
    private Date ngayBatDau;     
    private Date ngayKetThuc;     
    private double giaTriGiam;  
    private int hienThi;       

    public KhuyenMai() {
    }

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, String loaiKhuyenMai, 
                     Date ngayBatDau, Date ngayKetThuc, double giaTriGiam, int hienThi) {
        setMaKhuyenMai(maKhuyenMai);
        setTenKhuyenMai(tenKhuyenMai);
        setLoaiKhuyenMai(loaiKhuyenMai);
        setNgayBatDau(ngayBatDau);
        setNgayKetThuc(ngayKetThuc);
        setGiaTriGiam(giaTriGiam);
        setHienThi(hienThi);
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        if (maKhuyenMai == null || maKhuyenMai.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mãi không được rỗng.");
        }
        if (maKhuyenMai.trim().length() > 8) {
            throw new IllegalArgumentException("Mã khuyến mãi tối đa 8 ký tự.");
        }
        this.maKhuyenMai = maKhuyenMai.trim();
    }

    public String getTenKhuyenMai() {
        return tenKhuyenMai;
    }

    public void setTenKhuyenMai(String tenKhuyenMai) {
        if (tenKhuyenMai == null || tenKhuyenMai.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khuyến mãi không được rỗng.");
        }
        this.tenKhuyenMai = tenKhuyenMai.trim();
    }

    public String getLoaiKhuyenMai() {
        return loaiKhuyenMai;
    }

    public void setLoaiKhuyenMai(String loaiKhuyenMai) {
        if (loaiKhuyenMai == null || loaiKhuyenMai.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại khuyến mãi không được rỗng.");
        }
        this.loaiKhuyenMai = loaiKhuyenMai.trim();
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        if (ngayBatDau == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống.");
        }
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        if (ngayKetThuc == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được để trống.");
        }
        if (this.ngayBatDau != null && ngayKetThuc.before(this.ngayBatDau)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
        }
        this.ngayKetThuc = ngayKetThuc;
    }

    public double getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(double giaTriGiam) {
        if (giaTriGiam <= 0) {
            throw new IllegalArgumentException("Giá trị giảm phải lớn hơn 0.");
        }
        this.giaTriGiam = giaTriGiam;
    }

    public int getHienThi() {
        return hienThi;
    }

    public void setHienThi(int hienThi) {
        if (hienThi != 0 && hienThi != 1) {
            throw new IllegalArgumentException("Trạng thái hiển thị chỉ được là 1 (Hiện) hoặc 0 (Ẩn).");
        }
        this.hienThi = hienThi;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhuyenMai);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KhuyenMai other = (KhuyenMai) obj;
        return Objects.equals(maKhuyenMai, other.maKhuyenMai);
    }

    @Override
    public String toString() {
        return tenKhuyenMai;
    }
}
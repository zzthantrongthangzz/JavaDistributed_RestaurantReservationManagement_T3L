package entity;
import java.io.Serializable;
import java.util.Objects;

public class LoaiMon implements Serializable{
    private static final long serialVersionUID = 1L;
    private String maLoai;
    private String tenLoai;


    public LoaiMon() {
    }

    public LoaiMon(String maLoai, String tenLoai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
    }
    
    public LoaiMon(String maLoai) {
        this.maLoai = maLoai;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
        this.maLoai = maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }



    @Override
    public String toString() {
        return tenLoai; 
    }

    /**
     * Ghi đè phương thức equals để so sánh các đối tượng LoaiMon
     * Hai loại món được coi là giống nhau nếu có cùng mã loại (maLoai)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LoaiMon loaiMon = (LoaiMon) obj;
        return Objects.equals(maLoai, loaiMon.maLoai);
    }


    @Override
    public int hashCode() {
        return Objects.hash(maLoai);
    }
}
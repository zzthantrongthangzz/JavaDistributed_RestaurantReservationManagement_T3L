package ui.nhanvien;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.rmi.Naming;
import rmi_interfaces.INhanVien_Service;
import rmi_interfaces.IChucVu_Service;
import entity.NhanVien;
import entity.ChucVu;

public class ThemNhanVien_UI extends JPanel {
    private JTextField txtHoTen, txtSDT, txtEmail, txtDiaChi;
    private JComboBox<String> cmbGioiTinh;
    private JComboBox<ChucVu> cmbChucVu;
    private INhanVien_Service nhanVienService;
    private IChucVu_Service chucVuService;

    public ThemNhanVien_UI() {
        initRMI();
        setLayout(new BorderLayout());
        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 10, 10));
        pnlForm.setBorder(new EmptyBorder(30, 100, 30, 100));

        pnlForm.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField(); pnlForm.add(txtHoTen);
        pnlForm.add(new JLabel("SĐT:"));
        txtSDT = new JTextField(); pnlForm.add(txtSDT);
        pnlForm.add(new JLabel("Email:"));
        txtEmail = new JTextField(); pnlForm.add(txtEmail);
        pnlForm.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField(); pnlForm.add(txtDiaChi);
        pnlForm.add(new JLabel("Giới tính:"));
        cmbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"}); pnlForm.add(cmbGioiTinh);
        pnlForm.add(new JLabel("Chức vụ:"));
        cmbChucVu = new JComboBox<>(); pnlForm.add(cmbChucVu);

        JButton btnThem = new JButton("Thêm nhân viên");
        add(pnlForm, BorderLayout.CENTER);
        add(btnThem, BorderLayout.SOUTH);

        btnThem.addActionListener(e -> xuLyThem());
        loadChucVu();
    }

    private void initRMI() {
        try {
            nhanVienService = (INhanVien_Service) Naming.lookup("rmi://localhost:1099/NhanVienService");
            chucVuService = (IChucVu_Service) Naming.lookup("rmi://localhost:1099/ChucVuService");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadChucVu() {
        new SwingWorker<List<ChucVu>, Void>() {
            @Override protected List<ChucVu> doInBackground() throws Exception { return chucVuService.docDanhSachChucVu(); }
            @Override protected void done() {
                try { List<ChucVu> ds = get(); for(ChucVu cv : ds) cmbChucVu.addItem(cv); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    private void xuLyThem() {
        NhanVien nv = new NhanVien();
        nv.setHoTen(txtHoTen.getText());
        nv.setSoDienThoai(txtSDT.getText());
        nv.setEmail(txtEmail.getText());
        nv.setDiaChi(txtDiaChi.getText());
        nv.setGioiTinh(cmbGioiTinh.getSelectedIndex() == 0);
        nv.setChucVu((ChucVu) cmbChucVu.getSelectedItem());

        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception {
                String ma = nhanVienService.getMaNhanVienTiepTheo();
                nv.setMaNhanVien(ma);
                if(nhanVienService.themNhanVien(nv)) return ma;
                return null;
            }
            @Override protected void done() {
                try {
                    String ma = get();
                    if(ma != null) {
                        int opt = JOptionPane.showConfirmDialog(null, "Thêm thành công. Tạo tài khoản ngay?");
                        if(opt == JOptionPane.YES_OPTION) {
                            new ThemTaiKhoan_UI(null, ma, nv.getChucVu().getTenChucVu()).setVisible(true);
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }
}
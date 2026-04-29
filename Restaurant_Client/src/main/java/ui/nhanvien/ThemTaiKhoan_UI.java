package ui.nhanvien;
import rmi_interfaces.ITaiKhoan_Service;
import java.rmi.Naming;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThemTaiKhoan_UI extends JDialog {
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau, txtXacNhan;
    private ITaiKhoan_Service taiKhoanService;
    private String maNV, tenCV;

    public ThemTaiKhoan_UI(JFrame parent, String maNV, String tenCV) {
        super(parent, "Tạo tài khoản", true);
        this.maNV = maNV;
        this.tenCV = tenCV;
        initRMI();
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Mã NV:"));
        add(new JLabel(maNV));
        add(new JLabel("Tên đăng nhập:"));
        txtTenDangNhap = new JTextField(); add(txtTenDangNhap);
        add(new JLabel("Mật khẩu:"));
        txtMatKhau = new JPasswordField(); add(txtMatKhau);
        add(new JLabel("Xác nhận MK:"));
        txtXacNhan = new JPasswordField(); add(txtXacNhan);

        JButton btnTao = new JButton("Tạo tài khoản");
        add(new JLabel());
        add(btnTao);

        btnTao.addActionListener(e -> taoTaiKhoan());
    }

    private void initRMI() {
        try { taiKhoanService = (ITaiKhoan_Service) Naming.lookup("rmi://localhost:1099/TaiKhoanService"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void taoTaiKhoan() {
        String user = txtTenDangNhap.getText();
        String pass = new String(txtMatKhau.getPassword());
        if(!pass.equals(new String(txtXacNhan.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không khớp"); return;
        }
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                return taiKhoanService.themTaiKhoan(maNV, user, pass, tenCV);
            }
            @Override protected void done() {
                try { if(get()) { JOptionPane.showMessageDialog(null, "Đã tạo tài khoản"); dispose(); } }
                catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }
}
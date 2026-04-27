package ui.nhanvien;

import rmi_interfaces.ITaiKhoan_DAO;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThemTaiKhoan_UI extends JDialog {

    private JTextField txtMaNhanVien;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JPasswordField txtXacNhanMatKhau;
    private ITaiKhoan_DAO taiKhoanDAO;
    private Runnable onAccountAdded;
    private String maNhanVienTruyen;
    private String tenChucVuTruyen;
    private boolean themThanhCong = false;

    private final Color MAU_NEN = new Color(48, 52, 56);
    private final Color MAU_NEN_FORM = new Color(48, 52, 56);
    private final Color MAU_NEN_INPUT = new Color(60, 64, 68);
    private final Color MAU_VIEN_INPUT = new Color(70, 72, 87);
    private final Color MAU_CHU_TRANG = Color.WHITE;
    private final Color MAU_O_Nhap = Color.WHITE;
    private final Color MAU_NUT_THEM = new Color(76, 175, 80);
    private final Color MAU_NUT_THEM_HOVER = new Color(39, 174, 96);
    private final Color MAU_NUT_HUY = new Color(231, 76, 60);
    private final Color MAU_NUT_HUY_HOVER = new Color(192, 57, 43);
    private final Color MAU_VIEN_DUOI = new Color(70, 72, 87);

    private final Dimension KICH_THUOC_O_NHAP = new Dimension(400, 40);
    private final Dimension KICH_THUOC_NUT = new Dimension(150, 45);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 16);
    private JTextField txtChucVu;
    private JButton btnThem;

    public ThemTaiKhoan_UI(Frame parent, String maNhanVien, String tenChucVu) {
        super(parent, "Thêm tài khoản", true);
        this.maNhanVienTruyen = maNhanVien;
        this.tenChucVuTruyen = tenChucVu;

        try {
            taiKhoanDAO = (ITaiKhoan_DAO) Naming.lookup("rmi://localhost:1099/TaiKhoan_DAO");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        khoiTaoGiaoDien();
        setSize(550, 700);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void khoiTaoGiaoDien() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN);
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        mainPanel.add(taoPanelTieuDe(), BorderLayout.NORTH);
        mainPanel.add(taoPanelForm(), BorderLayout.CENTER);
        mainPanel.add(taoPanelNut(), BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
    }

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(MAU_NEN);
        JLabel lblTieuDe = new JLabel("THÊM TÀI KHOẢN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        panel.add(lblTieuDe);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN_DUOI),
                new EmptyBorder(0, 0, 15, 0)
        ));
        return panel;
    }

    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        txtMaNhanVien = taoFieldCoNhan(panel, "Mã nhân viên:", false, "");
        txtMaNhanVien.setEditable(false);
        txtMaNhanVien.setBackground(MAU_NEN_INPUT.darker());
        if (maNhanVienTruyen != null) {
            txtMaNhanVien.setText(maNhanVienTruyen);
        }

        txtTenDangNhap = taoFieldCoNhan(panel, "Tên đăng nhập:", true, "Nhập tên đăng nhập");
        if (maNhanVienTruyen != null) {
            txtTenDangNhap.setText(maNhanVienTruyen);
            txtTenDangNhap.setEditable(true);
            txtTenDangNhap.setBackground(MAU_NEN_INPUT.darker());
        }
        txtTenDangNhap.setBackground(MAU_NEN_INPUT);

        txtMatKhau = taoPasswordFieldCoNhan(panel, "Mật khẩu:", true, "Nhập mật khẩu");

        txtXacNhanMatKhau = taoPasswordFieldCoNhan(panel, "Xác nhận mật khẩu:", true, "Nhập lại mật khẩu");
        txtXacNhanMatKhau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnThem.doClick();
            }
        });

        txtChucVu = taoTextFieldChucVu(panel, tenChucVuTruyen);

        return panel;
    }

    private JTextField taoFieldCoNhan(JPanel panel, String nhan, boolean batBuoc, String placeholder) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(MAU_NEN_FORM);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(nhan);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_O_Nhap);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField textField = taoTextField(placeholder);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(textField);
        fieldPanel.add(Box.createVerticalStrut(8));

        panel.add(fieldPanel);
        return textField;
    }

    private JPasswordField taoPasswordFieldCoNhan(JPanel panel, String nhan, boolean batBuoc, String placeholder) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(MAU_NEN_FORM);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(nhan);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_O_Nhap);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passwordField = taoPasswordField(placeholder);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(passwordField);
        fieldPanel.add(Box.createVerticalStrut(8));

        panel.add(fieldPanel);
        return passwordField;
    }

    private JTextField taoTextFieldChucVu(JPanel panel, String chucVuTruyen) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(MAU_NEN_FORM);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel("Chức vụ:");
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_O_Nhap);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField TextField = new JTextField(chucVuTruyen);
        TextField.setEditable(false);
        TextField.setFont(FONT_O_NHAP);
        TextField.setBackground(MAU_NEN_INPUT);
        TextField.setForeground(MAU_CHU_TRANG);
        TextField.setCaretColor(MAU_CHU_TRANG);
        TextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        TextField.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(TextField);
        fieldPanel.add(Box.createVerticalStrut(8));

        panel.add(fieldPanel);
        return TextField;
    }

    private JTextField taoTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setPreferredSize(KICH_THUOC_O_NHAP);
        textField.setMaximumSize(KICH_THUOC_O_NHAP);
        textField.setFont(FONT_O_NHAP);
        textField.setBackground(MAU_NEN_INPUT);
        textField.setForeground(MAU_CHU_TRANG);
        textField.setCaretColor(MAU_CHU_TRANG);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JPasswordField taoPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(KICH_THUOC_O_NHAP);
        passwordField.setMaximumSize(KICH_THUOC_O_NHAP);
        passwordField.setFont(FONT_O_NHAP);
        passwordField.setBackground(MAU_NEN_INPUT);
        passwordField.setForeground(MAU_CHU_TRANG);
        passwordField.setCaretColor(MAU_CHU_TRANG);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return passwordField;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(MAU_NEN);

        btnThem = taoNut("Thêm", MAU_NUT_THEM, MAU_NUT_THEM_HOVER);
        JButton btnHuy = taoNut("Hủy", MAU_NUT_HUY, MAU_NUT_HUY_HOVER);

        btnThem.addActionListener(e -> xuLyThemTaiKhoan());
        btnHuy.addActionListener(e -> dispose());

        panel.add(btnThem);
        panel.add(btnHuy);

        return panel;
    }

    private JButton taoNut(String text, Color mauNen, Color mauHover) {
        JButton button = new JButton(text);
        button.setPreferredSize(KICH_THUOC_NUT);
        button.setFont(FONT_NUT);
        button.setBackground(mauNen);
        button.setForeground(MAU_CHU_TRANG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(mauHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(mauNen);
            }
        });

        return button;
    }

    private void xuLyThemTaiKhoan() {
        if (!kiemTraDuLieu()) {
            return;
        }

        try {
            String maNhanVien = txtMaNhanVien.getText().trim();
            String tenDangNhap = txtTenDangNhap.getText().trim();
            String matKhau = new String(txtMatKhau.getPassword());
            String chucVu = txtChucVu.getText().trim();

            boolean thanhCong = taiKhoanDAO.themTaiKhoan(maNhanVien, tenDangNhap, matKhau, chucVu);

            if (thanhCong) {
                this.themThanhCong=true;
                JOptionPane.showMessageDialog(this,
                        "Thêm tài khoản thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);

                if (onAccountAdded != null) {
                    onAccountAdded.run();
                }

                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể thêm tài khoản. Vui lòng kiểm tra lại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (RemoteException re) {
            re.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối máy chủ!", "Lỗi Mạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi thêm tài khoản: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean kiemTraDuLieu() {
        String matKhau = new String(txtMatKhau.getPassword());
        if (matKhau.isEmpty()) {
            hienThiLoi("Vui lòng nhập mật khẩu!");
            txtMatKhau.requestFocus();
            return false;
        }

        if (matKhau.length() < 6) {
            hienThiLoi("Mật khẩu phải có ít nhất 6 ký tự!");
            txtMatKhau.requestFocus();
            return false;
        }

        String xacNhanMatKhau = new String(txtXacNhanMatKhau.getPassword());
        if (!matKhau.equals(xacNhanMatKhau)) {
            hienThiLoi("Mật khẩu xác nhận không khớp!");
            txtXacNhanMatKhau.requestFocus();
            return false;
        }
        return true;
    }

    private void hienThiLoi(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Lỗi nhập liệu",
                JOptionPane.WARNING_MESSAGE);
    }

    public boolean isThemThanhCong() {
        return this.themThanhCong;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JFrame parentFrame = new JFrame();
                parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                String testMaNV = "NV001";
                String testChucVu = "Quản lý";
                ThemTaiKhoan_UI dialog = new ThemTaiKhoan_UI(parentFrame, testMaNV, testChucVu);
                dialog.setVisible(true);
            }
        });
    }
}
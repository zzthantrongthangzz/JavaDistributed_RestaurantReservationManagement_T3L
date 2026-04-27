package ui.khachhang;

import com.toedter.calendar.JDateChooser;
import rmi_interfaces.IKhachHang_DAO;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class ThemKhachHang_UI extends JDialog {

    private JTextField txtMaKH;
    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JComboBox<String> cmbGioiTinh;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JDateChooser txtNgaySinh;

    private IKhachHang_DAO khachHangDAO;
    private Runnable onCustomerAdded;
    private String soDienThoaiBanDau;

    private final Color MAU_NEN = new Color(48, 52, 56);
    private final Color MAU_NEN_FORM = new Color(48, 52, 56);
    private final Color MAU_NEN_INPUT = new Color(60, 64, 68);
    private final Color MAU_VIEN_INPUT = new Color(70, 72, 87);
    private final Color MAU_CHU_TRANG = Color.WHITE;
    private final Color MAU_NUT_THEM = new Color(76, 175, 80);
    private final Color MAU_NUT_THEM_HOVER = new Color(39, 174, 96);
    private final Color MAU_NUT_HUY = new Color(231, 76, 60);
    private final Color MAU_NUT_HUY_HOVER = new Color(192, 57, 43);

    private final Dimension KICH_THUOC_O_NHAP = new Dimension(400, 40);
    private final Dimension KICH_THUOC_NUT = new Dimension(150, 45);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 16);

    public ThemKhachHang_UI(Frame parent, String sdtBanDau, Runnable onCustomerAdded) {
        super(parent, "Thêm khách hàng", true);
        this.soDienThoaiBanDau = sdtBanDau;
        this.onCustomerAdded = onCustomerAdded;

        try {
            khachHangDAO = (IKhachHang_DAO) Naming.lookup("rmi://localhost:1099/KhachHang_DAO");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        khoiTaoGiaoDien();

        setSize(550, 750);
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

        JLabel lblTieuDe = new JLabel("THÊM KHÁCH HÀNG");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(MAU_CHU_TRANG);

        panel.add(lblTieuDe);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 72, 87)),
                new EmptyBorder(0, 0, 15, 0)
        ));

        return panel;
    }

    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));

        txtMaKH = taoTextField(false);
        txtMaKH.setBackground(MAU_NEN_INPUT.darker());

        try {
            if (khachHangDAO != null) {
                txtMaKH.setText(khachHangDAO.phatSinhMaKhachHang());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        txtHoTen = taoTextField(true);
        txtSoDienThoai = taoTextField(true);
        if (soDienThoaiBanDau != null && !soDienThoaiBanDau.isEmpty()) {
            txtSoDienThoai.setText(soDienThoaiBanDau);
        }

        cmbGioiTinh = taoComboBox(new String[]{"Nam", "Nữ"});

        txtEmail = taoTextField(true);
        txtDiaChi = taoTextField(true);
        txtNgaySinh = taoDateChooser();

        panel.add(taoPanelInput("Mã khách hàng:", txtMaKH));
        panel.add(Box.createVerticalStrut(15));
        panel.add(taoPanelInput("Họ tên *:", txtHoTen));
        panel.add(Box.createVerticalStrut(15));
        panel.add(taoPanelInput("Số điện thoại *:", txtSoDienThoai));
        panel.add(Box.createVerticalStrut(15));
        panel.add(taoPanelInput("Giới tính:", cmbGioiTinh));
        panel.add(Box.createVerticalStrut(15));
        panel.add(taoPanelInput("Ngày sinh:", txtNgaySinh));
        panel.add(Box.createVerticalStrut(15));
        panel.add(taoPanelInput("Email:", txtEmail));
        panel.add(Box.createVerticalStrut(15));
        panel.add(taoPanelInput("Địa chỉ:", txtDiaChi));

        return panel;
    }

    private JPanel taoPanelInput(String labelText, Component inputComponent) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_CHU_TRANG);

        panel.add(label, BorderLayout.NORTH);
        panel.add(inputComponent, BorderLayout.CENTER);

        return panel;
    }

    private JTextField taoTextField(boolean editable) {
        JTextField textField = new JTextField();
        textField.setPreferredSize(KICH_THUOC_O_NHAP);
        textField.setFont(FONT_O_NHAP);
        textField.setBackground(MAU_NEN_INPUT);
        textField.setForeground(MAU_CHU_TRANG);
        textField.setCaretColor(MAU_CHU_TRANG);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        textField.setEditable(editable);
        return textField;
    }

    private JComboBox<String> taoComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(KICH_THUOC_O_NHAP);
        comboBox.setFont(FONT_O_NHAP);
        comboBox.setBackground(MAU_NEN_INPUT);
        comboBox.setForeground(MAU_CHU_TRANG);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(2, 5, 2, 5)
        ));

        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(MAU_NEN_INPUT);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });

        return comboBox;
    }

    private JDateChooser taoDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(KICH_THUOC_O_NHAP);
        dateChooser.setFont(FONT_O_NHAP);
        dateChooser.setBackground(MAU_NEN_INPUT);
        dateChooser.setForeground(MAU_CHU_TRANG);

        JTextField dateEditor = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateEditor.setBackground(MAU_NEN_INPUT);
        dateEditor.setForeground(MAU_CHU_TRANG);
        dateEditor.setCaretColor(MAU_CHU_TRANG);
        dateEditor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton calendarButton = dateChooser.getCalendarButton();
        calendarButton.setBackground(MAU_NEN_INPUT);
        calendarButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return dateChooser;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(MAU_NEN);

        JButton btnThem = taoNut("Thêm", MAU_NUT_THEM, MAU_NUT_THEM_HOVER);
        JButton btnLamMoi = taoNut("Làm mới", new Color(33, 150, 243), new Color(30, 136, 229));
        JButton btnHuy = taoNut("Hủy", MAU_NUT_HUY, MAU_NUT_HUY_HOVER);

        btnThem.addActionListener(e -> themKhachHang());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnHuy.addActionListener(e -> dispose());

        panel.add(btnThem);
        panel.add(btnLamMoi);
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

    private void themKhachHang() {
        if (khachHangDAO == null) {
            JOptionPane.showMessageDialog(this, "Chưa kết nối máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!kiemTraDuLieu()) {
            return;
        }

        try {
            String maKH = txtMaKH.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String sdt = txtSoDienThoai.getText().trim();
            boolean gioiTinh = cmbGioiTinh.getSelectedItem().toString().equals("Nam");
            String email = txtEmail.getText().trim();
            String diaChi = txtDiaChi.getText().trim();

            java.util.Date utilDate = txtNgaySinh.getDate();
            Date sqlDate = null;
            if (utilDate != null) {
                sqlDate = new Date(utilDate.getTime());
            }

            // Thay dòng: if (khachHangDAO.kiemTraSoDienThoaiTonTai(sdt)) {
            if (khachHangDAO.timKhachHangTheoSDT(sdt) != null) {
                hienThiLoi("Số điện thoại này đã được đăng ký!");
                txtSoDienThoai.requestFocus();
                return;
            }

            KhachHang kh = new KhachHang(maKH, hoTen, sdt,  email, diaChi, sqlDate, gioiTinh, 0 , true);

            boolean ketQua = khachHangDAO.themKhachHang(kh);

            if (ketQua) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                if (onCustomerAdded != null) {
                    onCustomerAdded.run();
                }
                dispose();
            } else {
                hienThiLoi("Thêm khách hàng thất bại!");
            }
        } catch (RemoteException re) {
            re.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối máy chủ!", "Lỗi Mạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            hienThiLoi("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void lamMoi() {
        try {
            if (khachHangDAO != null) {
                txtMaKH.setText(khachHangDAO.phatSinhMaKhachHang());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        txtHoTen.setText("");
        txtSoDienThoai.setText(soDienThoaiBanDau != null ? soDienThoaiBanDau : "");
        cmbGioiTinh.setSelectedIndex(0);
        txtEmail.setText("");
        txtDiaChi.setText("");
        txtNgaySinh.setDate(null);
        txtHoTen.requestFocus();
    }

    private boolean kiemTraDuLieu() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        java.util.Date utilDate = txtNgaySinh.getDate();

        if (hoTen.isEmpty()) {
            hienThiLoi("Vui lòng nhập họ tên!");
            txtHoTen.requestFocus();
            return false;
        }

        if (!hoTen.matches("^[A-ZÀ-Ỹ][a-zà-ỹ]*(\\s[A-ZÀ-Ỹ][a-zà-ỹ]*)*$")) {
            hienThiLoi("Họ tên không hợp lệ (Phải viết hoa chữ cái đầu, không chứa số/kí tự đặc biệt).");
            txtHoTen.requestFocus();
            return false;
        }

        if (sdt.isEmpty()) {
            hienThiLoi("Vui lòng nhập số điện thoại!");
            txtSoDienThoai.requestFocus();
            return false;
        }

        if (!sdt.matches("^0\\d{9}$")) {
            hienThiLoi("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)!");
            txtSoDienThoai.requestFocus();
            return false;
        }

        if (!email.isEmpty()) {
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                hienThiLoi("Địa chỉ email không hợp lệ (ví dụ: example@gmail.com).");
                txtEmail.requestFocus();
                return false;
            }
        }

        if (utilDate != null) {
            LocalDate ngaySinh = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate homNay = LocalDate.now();

            if (ngaySinh.plusYears(16).isAfter(homNay)) {
                hienThiLoi("Khách hàng phải đủ 16 tuổi!");
                return false;
            }
        }

        return true;
    }

    private void hienThiLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }
}
package ui.khachhang;

import com.toedter.calendar.JDateChooser;
import rmi_interfaces.IKhachHang_Service;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.rmi.Naming;

public class ThemKhachHang_UI extends JDialog {

    private JTextField txtMaKH;
    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JComboBox<String> cmbGioiTinh;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JDateChooser txtNgaySinh;
    private JButton btnThem;

    private IKhachHang_Service khachHangDAO;
    private Runnable onCustomerAdded;
    private String soDienThoaiBanDau;

    private final Color MAU_NEN = new Color(48, 52, 56);
    private final Color MAU_NEN_FORM = new Color(48, 52, 56);
    private final Color MAU_NEN_INPUT = new Color(60, 64, 68);
    private final Color MAU_VIEN_INPUT = new Color(70, 72, 87);
    private final Color MAU_CHU_TRANG = Color.WHITE;

    public ThemKhachHang_UI(JFrame parent, String soDienThoai) {
        super(parent, "Thêm Khách Hàng Mới", true);
        this.soDienThoaiBanDau = soDienThoai;
        try {
            khachHangDAO = (IKhachHang_Service) Naming.lookup("rmi://localhost:1099/KhachHangService");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        khoiTaoGiaoDien();
        tuDongSinhMaKhachHang();
    }

    public void setOnCustomerAdded(Runnable callback) {
        this.onCustomerAdded = callback;
    }

    private void khoiTaoGiaoDien() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(MAU_NEN);
        pnlHeader.setBorder(new EmptyBorder(20, 0, 20, 0));
        JLabel lblTitle = new JLabel("THÊM KHÁCH HÀNG MỚI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(MAU_CHU_TRANG);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(4, 2, 20, 20));
        pnlCenter.setBackground(MAU_NEN_FORM);
        pnlCenter.setBorder(new EmptyBorder(20, 40, 20, 40));

        txtMaKH = taoO_Nhap("Mã Khách Hàng:", pnlCenter);
        txtMaKH.setEditable(false);
        txtHoTen = taoO_Nhap("Họ Tên (*):", pnlCenter);
        txtSoDienThoai = taoO_Nhap("Số Điện Thoại (*):", pnlCenter);
        if (soDienThoaiBanDau != null && !soDienThoaiBanDau.isEmpty()) {
            txtSoDienThoai.setText(soDienThoaiBanDau);
        }

        JPanel pnlGioiTinh = new JPanel(new BorderLayout());
        pnlGioiTinh.setBackground(MAU_NEN_FORM);
        JLabel lblGioiTinh = new JLabel("Giới Tính (*):");
        lblGioiTinh.setForeground(MAU_CHU_TRANG);
        lblGioiTinh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cmbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cmbGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbGioiTinh.setBackground(MAU_NEN_INPUT);
        cmbGioiTinh.setForeground(MAU_CHU_TRANG);
        pnlGioiTinh.add(lblGioiTinh, BorderLayout.NORTH);
        pnlGioiTinh.add(cmbGioiTinh, BorderLayout.CENTER);
        pnlCenter.add(pnlGioiTinh);

        JPanel pnlNgaySinh = new JPanel(new BorderLayout());
        pnlNgaySinh.setBackground(MAU_NEN_FORM);
        JLabel lblNgaySinh = new JLabel("Ngày Sinh:");
        lblNgaySinh.setForeground(MAU_CHU_TRANG);
        lblNgaySinh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtNgaySinh = new JDateChooser();
        txtNgaySinh.setDateFormatString("dd/MM/yyyy");
        pnlNgaySinh.add(lblNgaySinh, BorderLayout.NORTH);
        pnlNgaySinh.add(txtNgaySinh, BorderLayout.CENTER);
        pnlCenter.add(pnlNgaySinh);

        txtEmail = taoO_Nhap("Email:", pnlCenter);
        txtDiaChi = taoO_Nhap("Địa Chỉ:", pnlCenter);
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlFooter.setBackground(MAU_NEN);
        pnlFooter.setBorder(new EmptyBorder(10, 0, 20, 40));

        btnThem = new JButton("THÊM");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(new Color(76, 175, 80));
        btnThem.setForeground(MAU_CHU_TRANG);
        btnThem.setPreferredSize(new Dimension(120, 40));
        btnThem.setFocusPainted(false);
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnHuy = new JButton("HỦY");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuy.setBackground(new Color(244, 67, 54));
        btnHuy.setForeground(MAU_CHU_TRANG);
        btnHuy.setPreferredSize(new Dimension(120, 40));
        btnHuy.setFocusPainted(false);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnThem.addActionListener(e -> themKhachHang());
        btnHuy.addActionListener(e -> dispose());

        pnlFooter.add(btnHuy);
        pnlFooter.add(btnThem);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private JTextField taoO_Nhap(String labelText, JPanel parentPanel) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(MAU_NEN_FORM);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(MAU_CHU_TRANG);
        panel.add(label, BorderLayout.NORTH);

        JTextField txtField = new JTextField();
        txtField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtField.setBackground(MAU_NEN_INPUT);
        txtField.setForeground(MAU_CHU_TRANG);
        txtField.setCaretColor(MAU_CHU_TRANG);
        txtField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(5, 10, 5, 10)));
        panel.add(txtField, BorderLayout.CENTER);

        parentPanel.add(panel);
        return txtField;
    }

    private void tuDongSinhMaKhachHang() {
        if (khachHangDAO == null) return;
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return khachHangDAO.phatSinhMaKhachHang();
            }
            @Override
            protected void done() {
                try {
                    txtMaKH.setText(get());
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void themKhachHang() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        java.util.Date utilDate = txtNgaySinh.getDate();

        if (!hoTen.matches("^[A-ZÀ-Ỹ][a-zà-ỹ]*(\\s[A-ZÀ-Ỹ][a-zà-ỹ]*)*$")) {
            hienThiLoi("Họ tên không hợp lệ (Phải viết hoa chữ cái đầu).");
            return;
        }
        if (sdt.isEmpty() || !sdt.matches("^0\\d{9}$")) {
            hienThiLoi("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)!");
            return;
        }

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(txtMaKH.getText());
        kh.setHoTen(hoTen);
        kh.setSoDienThoai(sdt);
        kh.setEmail(email);
        kh.setDiaChi(txtDiaChi.getText().trim());
        kh.setGioiTinh(cmbGioiTinh.getSelectedIndex() == 0);
        if (utilDate != null) kh.setNgaySinh(new Date(utilDate.getTime()));
        kh.setTichDiem(0);

        btnThem.setEnabled(false);
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                KhachHang checkKH = khachHangDAO.timKhachHangTheoSDT(sdt);
                if (checkKH != null) throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
                return khachHangDAO.themKhachHang(kh);
            }
            @Override
            protected void done() {
                btnThem.setEnabled(true);
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(ThemKhachHang_UI.this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        if (onCustomerAdded != null) onCustomerAdded.run();
                        dispose();
                    } else {
                        hienThiLoi("Lỗi khi thêm khách hàng vào cơ sở dữ liệu!");
                    }
                } catch (Exception e) {
                    hienThiLoi(e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void hienThiLoi(String tb) {
        JOptionPane.showMessageDialog(this, tb, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }
}
package ui.khuyenmai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Date;
import java.rmi.Naming;
import java.rmi.RemoteException;

import rmi_interfaces.IKhuyenMai_Service;
import entity.KhuyenMai;

public class ThemKhuyenMai_UI extends JPanel {

    private JTextField txtMaKhuyenMai;
    private JTextField txtTenKhuyenMai;
    private JTextField txtGiaTri;
    private JComboBox<String> cmbLoaiKhuyenMai;
    private JDateChooser dateNgayBatDau;
    private JDateChooser dateNgayKetThuc;
    private Image backgroundImage;
    private IKhuyenMai_Service khuyenMaiService;
    private JButton btnThem;

    private final Color bgColor = new Color(48, 52, 56);
    private final Color componentColor = new Color(124, 124, 124);
    private final Color textColor = Color.WHITE;

    public ThemKhuyenMai_UI() {
        try {
            khuyenMaiService = (IKhuyenMai_Service) Naming.lookup("rmi://localhost:1099/KhuyenMai_Service");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        setBackground(bgColor);
        setLayout(null);

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/img/vipbackground2.png")).getImage();
        } catch (Exception e) {
            backgroundImage = null;
        }

        JLabel lblTitle = new JLabel("THÊM KHUYẾN MÃI");
        lblTitle.setForeground(textColor);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblTitle.setBounds(650, 34, 450, 62);
        add(lblTitle);

        JLabel lblMaKhuyenMai = createStyledLabel("Mã khuyến mãi:");
        lblMaKhuyenMai.setBounds(400, 150, 220, 34);
        add(lblMaKhuyenMai);
        txtMaKhuyenMai = createStyledTextField();
        txtMaKhuyenMai.setBounds(620, 147, 500, 45);
        txtMaKhuyenMai.setEditable(false);
        taoMaKhuyenMaiTuDong();
        add(txtMaKhuyenMai);

        JLabel lblTenKhuyenMai = createStyledLabel("Tên khuyến mãi:");
        lblTenKhuyenMai.setBounds(400, 230, 220, 34);
        add(lblTenKhuyenMai);
        txtTenKhuyenMai = createStyledTextField();
        txtTenKhuyenMai.setBounds(620, 227, 500, 45);
        add(txtTenKhuyenMai);

        JLabel lblLoaiKhuyenMai = createStyledLabel("Loại khuyến mãi:");
        lblLoaiKhuyenMai.setBounds(400, 310, 220, 34);
        add(lblLoaiKhuyenMai);
        cmbLoaiKhuyenMai = createStyledComboBox(new String[]{"Giảm %", "Giảm tiền"});
        cmbLoaiKhuyenMai.setSelectedIndex(-1);
        cmbLoaiKhuyenMai.setBounds(620, 307, 500, 45);
        add(cmbLoaiKhuyenMai);

        JLabel lblGiaTri = createStyledLabel("Giá trị:");
        lblGiaTri.setBounds(400, 390, 220, 34);
        add(lblGiaTri);
        txtGiaTri = createStyledTextField();
        txtGiaTri.setBounds(620, 387, 500, 45);
        add(txtGiaTri);

        JLabel lblNgayBatDau = createStyledLabel("Ngày bắt đầu:");
        lblNgayBatDau.setBounds(400, 470, 220, 34);
        add(lblNgayBatDau);
        dateNgayBatDau = createStyledDateChooser();
        dateNgayBatDau.setBounds(620, 467, 500, 45);
        add(dateNgayBatDau);

        JLabel lblNgayKetThuc = createStyledLabel("Ngày kết thúc:");
        lblNgayKetThuc.setBounds(400, 550, 220, 34);
        add(lblNgayKetThuc);
        dateNgayKetThuc = createStyledDateChooser();
        dateNgayKetThuc.setBounds(620, 547, 500, 45);
        add(dateNgayKetThuc);

        btnThem = createStyledButton(" Thêm", "/IMG/add_32px.png");
        btnThem.setBounds(600, 650, 150, 40);
        btnThem.addActionListener(e -> themKhuyenMai());
        add(btnThem);

        JButton btnLamMoi = createStyledButton(" Làm mới", "/IMG/refresh_32px.png");
        btnLamMoi.setBounds(850, 650, 150, 40);
        add(btnLamMoi);
        btnLamMoi.addActionListener(e -> lamMoi());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Segoe UI", Font.BOLD, 25));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(componentColor);
        textField.setForeground(textColor);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        textField.setBorder(new EmptyBorder(5, 10, 5, 10));
        textField.setCaretColor(Color.WHITE);
        return textField;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(componentColor);
        comboBox.setForeground(textColor);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        comboBox.setBorder(BorderFactory.createEmptyBorder());
        comboBox.setFocusable(false);
        return comboBox;
    }

    private JDateChooser createStyledDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setBackground(componentColor);
        dateChooser.setForeground(textColor);
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JButton calendarButton = dateChooser.getCalendarButton();
        calendarButton.setBackground(componentColor);
        calendarButton.setBorder(BorderFactory.createEmptyBorder());
        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTextField dateTextField = ((JTextField) dateChooser.getDateEditor().getUiComponent());
        dateTextField.setBackground(componentColor);
        dateTextField.setForeground(textColor);
        dateTextField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateTextField.setBorder(new EmptyBorder(5, 10, 5, 10));
        dateTextField.setCaretColor(Color.WHITE);

        JTextField dateEditor = (JTextField) dateChooser.getDateEditor().getUiComponent();

        dateEditor.setForeground(Color.WHITE);
        dateEditor.setCaretColor(Color.WHITE);
        dateEditor.setDisabledTextColor(Color.WHITE);
        dateEditor.setSelectedTextColor(Color.WHITE);
        dateEditor.setOpaque(true);

        dateEditor.addPropertyChangeListener(evt -> {
            if ("foreground".equals(evt.getPropertyName()) ||
                    "disabledTextColor".equals(evt.getPropertyName()) ||
                    "enabled".equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(() -> {
                    dateEditor.setForeground(Color.WHITE);
                    dateEditor.setDisabledTextColor(Color.WHITE);
                    dateEditor.setCaretColor(Color.WHITE);
                });
            }
        });

        dateEditor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                dateEditor.setForeground(Color.WHITE);
                dateEditor.setCaretColor(Color.WHITE);
            }
            @Override
            public void focusLost(FocusEvent e) {
                dateEditor.setForeground(Color.WHITE);
            }
        });

        dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(() -> {
                    dateEditor.setForeground(Color.WHITE);
                });
            }
        });
        return dateChooser;
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(componentColor);
        button.setForeground(textColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledIcon = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledIcon));
        } catch (Exception e) {}
        return button;
    }

    private void lamMoi() {
        taoMaKhuyenMaiTuDong();
        txtTenKhuyenMai.setText("");
        txtGiaTri.setText("");
        cmbLoaiKhuyenMai.setSelectedIndex(-1);
        dateNgayBatDau.setDate(null);
        dateNgayKetThuc.setDate(null);
        txtTenKhuyenMai.requestFocus();
    }

    private void taoMaKhuyenMaiTuDong() {
        if (khuyenMaiService == null) return;
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return khuyenMaiService.taoMaKhuyenMaiTuDong();
            }
            @Override
            protected void done() {
                try {
                    txtMaKhuyenMai.setText(get());
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void themKhuyenMai() {
        String maKM = txtMaKhuyenMai.getText().trim();
        String tenKM = txtTenKhuyenMai.getText().trim();
        String giaTriStr = txtGiaTri.getText().trim();
        String loaiKM = (String) cmbLoaiKhuyenMai.getSelectedItem();
        Date ngayBatDau = dateNgayBatDau.getDate();
        Date ngayKetThuc = dateNgayKetThuc.getDate();

        if (tenKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khuyến mãi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtTenKhuyenMai.requestFocus();
            return;
        }

        if (!tenKM.matches("^[A-ZÀ-Ỹ].*$")) {
            JOptionPane.showMessageDialog(this, "Vui lòng viết hoa chữ cái đầu tiên.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtTenKhuyenMai.requestFocus();
            return;
        }

        if (giaTriStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị khuyến mãi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtGiaTri.requestFocus();
            return;
        }

        double giaTri;
        try {
            giaTri = Double.parseDouble(giaTriStr);
            if (giaTri <= 0) {
                JOptionPane.showMessageDialog(this, "Giá trị phải lớn hơn 0!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá trị không hợp lệ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (loaiKM == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại khuyến mãi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (loaiKM.equals("Giảm %") && giaTri > 100) {
            JOptionPane.showMessageDialog(this, "Loại 'Giảm %' giá trị không vượt quá 100!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ngayBatDau == null || ngayKetThuc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ ngày bắt đầu và kết thúc!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ngayKetThuc.before(ngayBatDau)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn thêm khuyến mãi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            btnThem.setEnabled(false);
            KhuyenMai km = new KhuyenMai(maKM, tenKM, loaiKM, ngayBatDau, ngayKetThuc, giaTri, 1);
            SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
                @Override
                protected Object[] doInBackground() throws Exception {
                    if (khuyenMaiService == null) return new Object[]{false, "error"};
                    if (khuyenMaiService.kiemTraMaTonTai(maKM)) return new Object[]{false, "exists"};
                    boolean result = khuyenMaiService.themKhuyenMai(km);
                    return new Object[]{result, "success"};
                }
                @Override
                protected void done() {
                    btnThem.setEnabled(true);
                    try {
                        Object[] res = get();
                        boolean isSuccess = (Boolean) res[0];
                        String status = (String) res[1];

                        if (status.equals("exists")) {
                            JOptionPane.showMessageDialog(ThemKhuyenMai_UI.this, "Mã khuyến mãi đã tồn tại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        } else if (isSuccess) {
                            JOptionPane.showMessageDialog(ThemKhuyenMai_UI.this, "Thêm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            lamMoi();
                        } else {
                            JOptionPane.showMessageDialog(ThemKhuyenMai_UI.this, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {}
                }
            };
            worker.execute();
        }
    }
}
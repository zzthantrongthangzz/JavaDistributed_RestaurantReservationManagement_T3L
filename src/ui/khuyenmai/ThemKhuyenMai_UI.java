package ui.khuyenmai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;

import connect.DBConnect;
import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.util.Date;

public class ThemKhuyenMai_UI extends JPanel {

    private JTextField txtMaKhuyenMai;
    private JTextField txtTenKhuyenMai;
    private JTextField txtGiaTri;
    private JComboBox<String> cmbLoaiKhuyenMai;
    private JDateChooser dateNgayBatDau;
    private JDateChooser dateNgayKetThuc;
    private Image backgroundImage;
    private KhuyenMai_DAO khuyenMaiDAO;

    private final Color bgColor = new Color(48, 52, 56);
    private final Color componentColor = new Color(124, 124, 124);
    private final Color textColor = Color.WHITE;

    // Khởi tạo giao diện
    public ThemKhuyenMai_UI() {
    	try {
            khuyenMaiDAO = new KhuyenMai_DAO(DBConnect.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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

        JButton btnThem = createStyledButton(" Thêm", "/IMG/add_32px.png");
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

    // Tạo label theo style
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Segoe UI", Font.BOLD, 25));
        return label;
    }

    // Tạo text field theo style
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(componentColor);
        textField.setForeground(textColor);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        textField.setBorder(new EmptyBorder(5, 10, 5, 10));
        textField.setCaretColor(Color.WHITE);
        return textField;
    }

    // Tạo combo box theo style
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(componentColor);
        comboBox.setForeground(textColor);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        comboBox.setBorder(BorderFactory.createEmptyBorder());
        comboBox.setFocusable(false);   
        return comboBox;
    }

    // Tạo date chooser theo style
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

    // Tạo button theo style
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
        } catch (Exception e) {
            System.err.println("Không tìm thấy icon: " + iconPath);
        }

        return button;
    }

    // Làm mới form nhập liệu
    private void lamMoi() {
        taoMaKhuyenMaiTuDong();
        txtTenKhuyenMai.setText("");
        txtGiaTri.setText("");
        cmbLoaiKhuyenMai.setSelectedIndex(0);
        dateNgayBatDau.setDate(null);
        dateNgayKetThuc.setDate(null);
        txtTenKhuyenMai.requestFocus();
        cmbLoaiKhuyenMai.setSelectedItem(null);
        
    }

    // Tạo mã khuyến mãi tự động từ database
    private void taoMaKhuyenMaiTuDong() {
        if (khuyenMaiDAO != null) {
            String maKM = khuyenMaiDAO.taoMaKhuyenMaiTuDong();
            txtMaKhuyenMai.setText(maKM);
        }
    }

    // Xử lý thêm khuyến mãi mới
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
        
        String regex = "^[A-ZÀ-Ỹ].*$";

        if (!tenKM.matches(regex)) {
            JOptionPane.showMessageDialog(this, 
                    "Tên khuyến mãi không hợp lệ!\n" +
                    "Vui lòng viết hoa chữ cái đầu tiên.", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Giá trị khuyến mãi phải lớn hơn 0!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtGiaTri.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá trị khuyến mãi không hợp lệ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtGiaTri.requestFocus();
            return;
        }
        
        if (loaiKM == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại khuyến mãi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Nếu là giảm % thì không quá 100
        if (loaiKM.equals("Giảm %") && giaTri > 100) {
            JOptionPane.showMessageDialog(this, 
                "Loại giảm giá là 'Giảm %' thì giá trị chỉ được từ 1-100!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtGiaTri.requestFocus();
            return;
        }
        
        if (ngayBatDau == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (ngayKetThuc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày kết thúc!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (ngayKetThuc.before(ngayBatDau)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (khuyenMaiDAO.kiemTraMaTonTai(maKM)) {
            JOptionPane.showMessageDialog(this, "Mã khuyến mãi đã tồn tại! Vui lòng nhập mã khác.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtMaKhuyenMai.requestFocus();
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn thêm khuyến mãi này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            KhuyenMai km = new KhuyenMai(maKM, tenKM, loaiKM, ngayBatDau, ngayKetThuc, giaTri, 1);
            
            if (khuyenMaiDAO.themKhuyenMai(km)) {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String getMaKhuyenMai() { return txtMaKhuyenMai.getText(); }
    public String getTenKhuyenMai() { return txtTenKhuyenMai.getText(); }
    public String getGiaTri() { return txtGiaTri.getText(); }
    public String getLoaiKhuyenMai() { return (String) cmbLoaiKhuyenMai.getSelectedItem(); }
    public Date getNgayBatDau() { return dateNgayBatDau.getDate(); }
    public Date getNgayKetThuc() { return dateNgayKetThuc.getDate(); }
    
}
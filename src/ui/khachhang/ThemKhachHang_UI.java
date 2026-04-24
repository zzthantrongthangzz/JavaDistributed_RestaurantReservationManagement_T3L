package ui.khachhang;

import com.toedter.calendar.JDateChooser; 
import dao.KhachHang_DAO;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.SimpleDateFormat; 
import java.util.List;
import java.time.LocalDate;     
import java.time.ZoneId;

public class ThemKhachHang_UI extends JDialog {

    private JTextField txtMaKH;
    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JComboBox<String> cmbGioiTinh;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JDateChooser txtNgaySinh; 

    private KhachHang_DAO khachHangDAO;
    private Runnable onCustomerAdded;
    private String soDienThoaiBanDau;

    private final Color MAU_NEN = new Color(48, 52, 56);
    private final Color MAU_NEN_FORM = new Color(48, 52, 56);
    private final Color MAU_NEN_INPUT = new Color(60, 64, 68);
    private final Color MAU_VIEN_INPUT = new Color(70, 72, 87);
    private final Color MAU_CHU_TRANG = Color.WHITE;
    private final Color MAU_O_NHAP = Color.WHITE;
    private final Color MAU_NUT_THEM = new Color(76, 175, 80);
    private final Color MAU_NUT_THEM_HOVER = new Color(39, 174, 96);
    private final Color MAU_NUT_HUY = new Color(231, 76, 60);
    private final Color MAU_NUT_HUY_HOVER = new Color(192, 57, 43);
    private final Color MAU_VIEN_DUOI = new Color(70, 72, 87);

    private final Dimension KICH_THUOC_O_NHAP = new Dimension(400, 40);
    private final Dimension KICH_THUOC_GIOI_TINH = new Dimension(120, 40);
    private final Dimension KICH_THUOC_NUT = new Dimension(150, 45);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 16);

    // Constructor mặc định
    public ThemKhachHang_UI() {
        this(null, null, null);
    }

    // Constructor có frame cha
    public ThemKhachHang_UI(Frame parent) {
        this(parent, null, null);
    }
    
    // Constructor có callback khi thêm thành công
    public ThemKhachHang_UI(Frame parent, Runnable onCustomerAdded) {
        this(parent, null, onCustomerAdded);
    }
    
    // Constructor đầy đủ
    public ThemKhachHang_UI(Frame parent, String soDienThoai, Runnable onCustomerAdded) {
        super(parent, "Thêm khách hàng", true);
        this.onCustomerAdded = onCustomerAdded;
        this.soDienThoaiBanDau = soDienThoai;
        khachHangDAO = new KhachHang_DAO();
        
        khoiTaoGiaoDien();
        taoMaKhachHangTuDong();
        
        if (this.soDienThoaiBanDau != null && !this.soDienThoaiBanDau.isEmpty()) {
            txtSoDienThoai.setText(this.soDienThoaiBanDau);
        }
        
        setSize(580, 700); 
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // Khởi tạo giao diện chính
    private void khoiTaoGiaoDien() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        mainPanel.add(taoPanelTieuDe(), BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(taoPanelForm());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tuyChinhScrollBar(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(taoPanelNut(), BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }
    
    // Tùy chỉnh thanh cuộn
    private void tuyChinhScrollBar(JScrollPane s) {
		s.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		s.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
		s.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(100, 105, 120);
				this.trackColor = MAU_NEN_INPUT;
			}

			protected JButton createDecreaseButton(int o) {
				return new JButton() {
					{
						setPreferredSize(new Dimension(0, 0));
					}
				};
			}

			protected JButton createIncreaseButton(int o) {
				return new JButton() {
					{
						setPreferredSize(new Dimension(0, 0));
					}
				};
			}
		});
	}

    // Tạo panel tiêu đề
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(MAU_NEN);

        JLabel lblTieuDe = new JLabel("THÊM KHÁCH HÀNG");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        panel.add(lblTieuDe);

        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN_DUOI),
            new EmptyBorder(0, 0, 15, 0)
        ));

        return panel;
    }
    
    // Tạo panel chứa form nhập liệu
    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtMaKH = taoFieldCoNhan(panel, "Mã khách hàng:", false, "");
        txtMaKH.setEditable(false);
        txtMaKH.setBackground(MAU_NEN_INPUT.darker());
        
        txtHoTen = taoFieldCoNhan(panel, "Họ tên (*):", true, "Nhập họ tên đầy đủ");
        
        txtSoDienThoai = taoFieldCoNhan(panel, "Số điện thoại (*):", true, "Nhập số điện thoại");
        
        cmbGioiTinh = taoComboBoxCoNhan(panel, "Giới tính:", new String[]{"Nam", "Nữ"});
        
        txtEmail = taoFieldCoNhan(panel, "Email:", false, "Nhập email (tùy chọn)");
        
        txtDiaChi = taoFieldCoNhan(panel, "Địa chỉ:", false, "Nhập địa chỉ (tùy chọn)");
        
        txtNgaySinh = taoDateChooserCoNhan(panel, "Ngày sinh:", "dd/MM/yyyy");

        return panel;
    }

    // Tạo DateChooser có nhãn
    private JDateChooser taoDateChooserCoNhan(JPanel panel, String nhan, String dateFormat) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(MAU_NEN_FORM);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel label = new JLabel(nhan);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_O_NHAP);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString(dateFormat); 
        dateChooser.setPreferredSize(KICH_THUOC_O_NHAP);
        dateChooser.setMaximumSize(KICH_THUOC_O_NHAP);
        dateChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dateChooser.getCalendarButton().setBackground(MAU_NEN_INPUT);
        dateChooser.getCalendarButton().setBorder(BorderFactory.createEmptyBorder());
        dateChooser.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTextField dateEditor = (JTextField) dateChooser.getDateEditor().getUiComponent();
        dateEditor.setBackground(MAU_NEN_INPUT);
        dateEditor.setForeground(MAU_CHU_TRANG);
        dateEditor.setFont(FONT_O_NHAP);
        dateEditor.setCaretColor(MAU_CHU_TRANG);
        dateEditor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(dateChooser);
        fieldPanel.add(Box.createVerticalStrut(10));

        panel.add(fieldPanel);
        return dateChooser;
    }

    // Tạo textfield có nhãn
    private JTextField taoFieldCoNhan(JPanel panel, String nhan, boolean batBuoc, String placeholder) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(MAU_NEN_FORM);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel label = new JLabel(nhan);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_O_NHAP);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField textField = taoTextField(placeholder);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(textField);
        fieldPanel.add(Box.createVerticalStrut(10));

        panel.add(fieldPanel);
        return textField;
    }

    // Tạo textfield custom
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

    // Tạo combobox có nhãn
    private JComboBox<String> taoComboBoxCoNhan(JPanel panel, String nhan, String[] items) {
        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.Y_AXIS));
        comboPanel.setBackground(MAU_NEN_FORM);
        comboPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel label = new JLabel(nhan);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_CHU_TRANG);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> comboBox = taoComboBox(items);
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboPanel.add(label);
        comboPanel.add(Box.createVerticalStrut(5));
        comboPanel.add(comboBox);
        comboPanel.add(Box.createVerticalStrut(10));

        panel.add(comboPanel);
        return comboBox;
    }

    // Tạo combobox custom
    private JComboBox<String> taoComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(KICH_THUOC_GIOI_TINH);
        comboBox.setMaximumSize(KICH_THUOC_GIOI_TINH);
        comboBox.setFont(FONT_O_NHAP);
        comboBox.setBackground(MAU_NEN_INPUT);
        comboBox.setForeground(MAU_CHU_TRANG);
        comboBox.setBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1));
        return comboBox;
    }

    // Tạo panel chứa các nút
    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(MAU_NEN);

        JButton btnThem = taoNut("Thêm", MAU_NUT_THEM, MAU_NUT_THEM_HOVER);
        JButton btnHuy = taoNut("Hủy", MAU_NUT_HUY, MAU_NUT_HUY_HOVER);

        btnThem.addActionListener(e -> xuLyThemKhachHang());
        btnHuy.addActionListener(e -> dispose());

        panel.add(btnThem);
        panel.add(btnHuy);

        return panel;
    }

    // Tạo nút custom
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

    // Xử lý logic thêm khách hàng
    private void xuLyThemKhachHang() {
        if (!kiemTraDuLieuBatBuoc()) {
            return;
        }

        try {
            String maKH = khachHangDAO.phatSinhMaKhachHang(); 

            String hoTen = txtHoTen.getText().trim();
            String soDienThoai = txtSoDienThoai.getText().trim();
            boolean gioiTinh = cmbGioiTinh.getSelectedItem().equals("Nam");
            
            String email = txtEmail.getText().trim();
            if (email.isEmpty()) email = null;
            
            String diaChi = txtDiaChi.getText().trim();
            if (diaChi.isEmpty()) diaChi = null;
            
            Date ngaySinh = null;
            java.util.Date dateUtil = txtNgaySinh.getDate(); 
            if (dateUtil != null) {
                ngaySinh = new Date(dateUtil.getTime()); 
            }
            
            int tichDiem = 0;

            KhachHang khachHang = new KhachHang(maKH, hoTen, soDienThoai, email, diaChi, ngaySinh, gioiTinh, tichDiem);

            boolean thanhCong = khachHangDAO.themKhachHang(khachHang);

            if (thanhCong) {
                JOptionPane.showMessageDialog(this,
                    "Thêm thành công! Mã KH mới là: " + maKH, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                if (onCustomerAdded != null) {
                    onCustomerAdded.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể thêm khách hàng. Vui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi thêm: " + e.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Kiểm tra dữ liệu đầu vào bắt buộc
    private boolean kiemTraDuLieuBatBuoc() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        java.util.Date utilDate = txtNgaySinh.getDate();
        
        if (hoTen.isEmpty()) {
            hienThiLoi("Vui lòng nhập họ tên!");
            txtHoTen.requestFocus();
            return false;
        }
        if (!hoTen.matches("^[A-ZÀ-Ỹ][a-zà-ỹ]*(\\s[A-ZÀ-Ỹ][a-zà-ỹ]*)*$")) {
            hienThiLoi("Họ tên phải viết hoa chữ cái đầu mỗi từ. VD: Nguyễn Văn A");
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

    // Hiển thị dialog thông báo lỗi
    private void hienThiLoi(String message) {
        JOptionPane.showMessageDialog(this,
            message, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    // Tự động phát sinh mã khách hàng mới
    private void taoMaKhachHangTuDong() {
        String maMoi = khachHangDAO.phatSinhMaKhachHang();
        txtMaKH.setText(maMoi);
    }
}
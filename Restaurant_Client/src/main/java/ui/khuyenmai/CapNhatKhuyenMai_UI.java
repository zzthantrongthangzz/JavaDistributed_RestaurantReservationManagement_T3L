package ui.khuyenmai;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

import com.toedter.calendar.JDateChooser;

import entity.KhuyenMai;
import dao_impl.KhuyenMai_DAO;
import connect.DBConnect;

public class CapNhatKhuyenMai_UI extends JPanel {

    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
    private final Color MAU_CHU_LABEL = new Color(220, 220, 220);
    private final Color MAU_NUT_HET_HAN = new Color(76, 175, 80);    private final Color MAU_NUT_SUA = new Color(255, 193, 7);
    private final Color MAU_NUT_XOA = new Color(244, 67, 54);
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    private final Color MAU_THANH_CUON_THUMB = new Color(100, 104, 124);
    private final Color MAU_THANH_CUON_TRACK = new Color(31, 32, 44);

    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 70;
    private final int CHIEU_CAO_HEADER_BANG = 45;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(400, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(180, 40);
    private final Dimension KICH_THUOC_NUT_CHUC_NANG = new Dimension(115, 40);
    private final Dimension KICH_THUOC_O_NHAP = new Dimension(200, 40);
    private final Dimension KICH_THUOC_DATE_PICKER = new Dimension(200, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 13);

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private JComboBox<String> cmbSapXep;
    private JComboBox<String> cmbLocLoai;
    private JTextField txtMaKhuyenMai;
    private JTextField txtTenKhuyenMai;
    private JTextField txtGiaTri;
    private JComboBox<String> cmbLoaiKhuyenMai;
    private JDateChooser dateNgayBatDau;
    private JDateChooser dateNgayKetThuc;
    private JPanel panelChinh;
    private KhuyenMai_DAO khuyenMaiDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private JTextField txt;

    // Khởi tạo giao diện
    public CapNhatKhuyenMai_UI() {
        try {
            khuyenMaiDAO = new KhuyenMai_DAO(DBConnect.getConnection());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        khoiTaoGiaoDien();
        docDuLieuTuSQL();
    }

    // Thiết lập bố cục và panel chính
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB);

        panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(20, 25, 20, 25));
        panelChinh.setFocusable(true);
        panelChinh.add(taoPanelTieuDe(), BorderLayout.NORTH);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout(0, 15));
        wrapperPanel.setBackground(MAU_NEN_TAB);
        wrapperPanel.add(taoPanelDieuKhien(), BorderLayout.NORTH);
        wrapperPanel.add(taoPanelNoiDung(), BorderLayout.CENTER);
        
        panelChinh.add(wrapperPanel, BorderLayout.CENTER);
        panelChinh.requestFocusInWindow();

        add(panelChinh, BorderLayout.CENTER);
    }
    
    // Tạo panel tiêu đề
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 90));
        panel.setBorder(new EmptyBorder(5, 0, 20, 0));
        
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setBackground(MAU_NEN_TAB);
        
        JLabel lblTieuDe = new JLabel("CẬP NHẬT KHUYẾN MÃI");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTieuDe.setForeground(Color.WHITE);
        lblTieuDe.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(100, 104, 124)),
            new EmptyBorder(15, 30, 15, 30)
        ));
        
        contentPanel.add(lblTieuDe);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    // Tạo panel chứa các nút điều khiển và bộ lọc
    private JPanel taoPanelDieuKhien() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 50));

        JPanel panelTimKiem = new JPanel();
        panelTimKiem.setLayout(new BoxLayout(panelTimKiem, BoxLayout.X_AXIS));
        panelTimKiem.setBackground(MAU_NEN_TAB);

        txtTimKiem = taoTextFieldTimKiem();
        
        cmbSapXep = taoComboBox(new String[]{"Sắp xếp tên", "Tên A-Z", "Tên Z-A", "Giá trị cao-thấp", "Giá trị thấp-cao", "Ngày bắt đầu mới-cũ", "Ngày bắt đầu cũ-mới"}, KICH_THUOC_COMBO_BOX);
        cmbSapXep.addActionListener(e -> {
            int selectedIndex = cmbSapXep.getSelectedIndex();
            if (selectedIndex == 0) {
                lamMoiGiaoDien();
            } else {
                thucHienSapXep(selectedIndex); 
            }
        });
        
        cmbLocLoai = taoComboBox(new String[]{"Lọc theo loại", "Giảm %", "Giảm tiền"}, KICH_THUOC_COMBO_BOX);
        cmbLocLoai.addActionListener(e -> {
            thucHienLoc(); 
        });
                
        panelTimKiem.add(txtTimKiem);
        panelTimKiem.add(Box.createRigidArea(new Dimension(20, 0)));
        panelTimKiem.add(cmbSapXep);
        panelTimKiem.add(Box.createRigidArea(new Dimension(20, 0)));
        panelTimKiem.add(cmbLocLoai);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelNut.setBackground(MAU_NEN_TAB);
        
        JButton btnSua = taoNutChucNang("Cập nhật", MAU_NUT_SUA);
        btnSua.addActionListener(e -> capNhatKhuyenMai());
        
        JButton btnXoa = taoNutChucNang("Xóa", MAU_NUT_XOA);
        btnXoa.addActionListener(e -> xoaKhuyenMai());
        
        JButton btnLamMoi = taoNutChucNang("Làm mới", new Color(33, 150, 243));
        btnLamMoi.addActionListener(e -> lamMoiGiaoDien());

        JButton btnKMHetHan = taoNutChucNang("KM hết hạn", MAU_NUT_HET_HAN);
        btnKMHetHan.addActionListener(e-> hienThiDialogKhuyenMaiHetHan());
        
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        panelNut.add(btnKMHetHan);

        panel.add(panelTimKiem, BorderLayout.WEST);
        panel.add(panelNut, BorderLayout.EAST);

        return panel;
    }

    // Tạo ô tìm kiếm
    private JTextField taoTextFieldTimKiem() {
        txt = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/IMG/search.png"));
                    Image img = icon.getImage().getScaledInstance(KICH_THUOC_ICON, KICH_THUOC_ICON, Image.SCALE_SMOOTH);
                    Icon searchIcon = new ImageIcon(img);
                    int y = (getHeight() - searchIcon.getIconHeight()) / 2;
                    int x = getWidth() - searchIcon.getIconWidth() - 10;
                    searchIcon.paintIcon(this, g, x, y);
                } catch (Exception e) {
                    System.err.println("Lỗi tải icon tìm kiếm: " + e.getMessage());
                }
            }
        };

        txt.setText("Tìm kiếm khuyến mãi. . .");
        txt.setForeground(MAU_PLACEHOLDER);
        txt.setBackground(MAU_THANH_TIM_KIEM);
        txt.setCaretColor(MAU_CHU_CHUNG);
        txt.setFont(FONT_TEXTFIELD);
        txt.setPreferredSize(KICH_THUOC_THANH_TIM_KIEM);
        txt.setMaximumSize(KICH_THUOC_THANH_TIM_KIEM);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
            new EmptyBorder(8, 15, 8, 40)
        ));

        txt.addActionListener(e -> timKiemKhuyenMai());

        txt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int iconX = txt.getWidth() - KICH_THUOC_ICON - 10;
                Rectangle iconBounds = new Rectangle(iconX, 0, KICH_THUOC_ICON + 10, txt.getHeight());
                if (iconBounds.contains(e.getPoint())) {
                    timKiemKhuyenMai();
                }
            }
        });

        txt.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int iconX = txt.getWidth() - KICH_THUOC_ICON - 10;
                Rectangle iconBounds = new Rectangle(iconX, 0, KICH_THUOC_ICON + 10, txt.getHeight());
                txt.setCursor(iconBounds.contains(e.getPoint()) ? 
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : 
                    Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }
        });

        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().equals("Tìm kiếm khuyến mãi. . .")) {
                    txt.setText("");
                    txt.setForeground(MAU_CHU_CHUNG);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) {
                    txt.setText("Tìm kiếm khuyến mãi. . .");
                    txt.setForeground(MAU_PLACEHOLDER);
                }
            }
        });

        return txt;
    }

    // Tạo combo box tùy chỉnh
    private JComboBox<String> taoComboBox(String[] items, Dimension size) {
        JComboBox<String> cmb = new JComboBox<>(items);
        cmb.setFont(FONT_NHAN);
        cmb.setBackground(MAU_THANH_TIM_KIEM);
        cmb.setForeground(MAU_CHU_CHUNG);
        cmb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmb.setFocusable(false);
        cmb.setPreferredSize(size);
        cmb.setMaximumSize(size);
        cmb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
            new EmptyBorder(0, 15, 0, 5)
        ));

        cmb.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/IMG/muitenxuong_32px.png"));
                    Image img = icon.getImage().getScaledInstance(KICH_THUOC_ICON, KICH_THUOC_ICON, Image.SCALE_SMOOTH);
                    JButton btn = new JButton(new ImageIcon(img));
                    btn.setBackground(MAU_THANH_TIM_KIEM);
                    btn.setOpaque(true);
                    btn.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                    return btn;
                } catch (Exception e) {
                    JButton btn = new JButton("▼");
                    btn.setBackground(MAU_THANH_TIM_KIEM);
                    return btn;
                }
            }
        });

        return cmb;
    }

    // Tạo nút chức năng
    private JButton taoNutChucNang(String text, Color mauNen) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_NHAN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(mauNen);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setPreferredSize(KICH_THUOC_NUT_CHUC_NANG);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(mauNen.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(mauNen); }
        });

        return btn;
    }
    
    // Tạo panel nội dung chính
    private JPanel taoPanelNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(MAU_NEN_TAB);

        JPanel panelForm = taoPanelForm();
        panel.add(panelForm, BorderLayout.NORTH);

        JPanel panelBang = taoPanelBang();
        panel.add(panelBang, BorderLayout.CENTER);

        return panel;
    }

    // Tạo form nhập liệu
    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(MAU_NEN_TAB);

        txtMaKhuyenMai = taoFieldCoNhan(panel, "Mã khuyến mãi:", false, "");
        txtTenKhuyenMai = taoFieldCoNhan(panel, "Tên khuyến mãi:", true, "Nhập tên khuyến mãi...");
        cmbLoaiKhuyenMai = taoComboBoxCoNhan(panel, "Loại khuyến mãi:", 
            new String[]{"Giảm %", "Giảm tiền"});
        txtGiaTri = taoFieldCoNhan(panel, "Giá trị:", true, "Nhập giá trị...");
        dateNgayBatDau = taoDateChooserCoNhan(panel, "Ngày bắt đầu:");
        dateNgayKetThuc = taoDateChooserCoNhan(panel, "Ngày kết thúc:");

        return panel;
    }

    // Tạo trường nhập liệu có nhãn
    private JTextField taoFieldCoNhan(JPanel parent, String labelText, boolean enabled, String placeholder) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);

        JLabel label = new JLabel(labelText);
        label.setForeground(MAU_CHU_LABEL);
        label.setFont(FONT_NHAN);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        JTextField txt = new JTextField();
        txt.setEnabled(enabled);
        txt.setCaretColor(MAU_CHU_CHUNG);
        txt.setFont(FONT_TEXTFIELD);
        txt.setBackground(enabled ? MAU_THANH_TIM_KIEM : new Color(50, 52, 60));
        txt.setPreferredSize(KICH_THUOC_O_NHAP);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));

        if (placeholder != null && !placeholder.isEmpty()) {
            txt.setText(placeholder);
            txt.setForeground(MAU_PLACEHOLDER);
            txt.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (txt.getText().equals(placeholder)) {
                        txt.setText("");
                        txt.setForeground(MAU_CHU_CHUNG);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (txt.getText().isEmpty()) {
                        txt.setText(placeholder);
                        txt.setForeground(MAU_PLACEHOLDER);
                    }
                }
            });
        } else {
            txt.setForeground(MAU_CHU_CHUNG);
        }

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(txt, BorderLayout.CENTER);

        if (parent.getComponentCount() > 0) {
            parent.add(Box.createRigidArea(new Dimension(15, 0)));
        }
        parent.add(wrapper);

        return txt;
    }

    // Tạo combo box có nhãn
    private JComboBox<String> taoComboBoxCoNhan(JPanel parent, String labelText, String[] items) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);

        JLabel label = new JLabel(labelText);
        label.setForeground(MAU_CHU_LABEL);
        label.setFont(FONT_NHAN);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        JComboBox<String> cmb = taoComboBox(items, KICH_THUOC_O_NHAP);

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(cmb, BorderLayout.CENTER);

        if (parent.getComponentCount() > 0) {
            parent.add(Box.createRigidArea(new Dimension(15, 0)));
        }
        parent.add(wrapper);

        return cmb;
    }

    // Tạo date chooser có nhãn
    private JDateChooser taoDateChooserCoNhan(JPanel parent, String labelText) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);

        JLabel label = new JLabel(labelText);
        label.setForeground(MAU_CHU_LABEL);
        label.setFont(FONT_NHAN);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setBackground(MAU_THANH_TIM_KIEM);
        dateChooser.setForeground(MAU_CHU_CHUNG);
        dateChooser.setFont(FONT_TEXTFIELD);
        dateChooser.setPreferredSize(KICH_THUOC_DATE_PICKER);
        
        JButton calendarButton = dateChooser.getCalendarButton();
        calendarButton.setBackground(MAU_THANH_TIM_KIEM); 
        calendarButton.setBorder(BorderFactory.createEmptyBorder());
        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JTextField dateTextField = ((JTextField) dateChooser.getDateEditor().getUiComponent());
        dateTextField.setBackground(MAU_THANH_TIM_KIEM);
        dateTextField.setForeground(MAU_CHU_CHUNG);
        dateTextField.setFont(FONT_TEXTFIELD);
        dateTextField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        dateTextField.setCaretColor(Color.WHITE);

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(dateChooser, BorderLayout.CENTER);

        if (parent.getComponentCount() > 0) {
            parent.add(Box.createRigidArea(new Dimension(15, 0)));
        }
        parent.add(wrapper);
        
        JTextField dateEditor = (JTextField) dateChooser.getDateEditor().getUiComponent();

        dateEditor.setBackground(MAU_THANH_TIM_KIEM);
        dateEditor.setForeground(Color.WHITE);
        dateEditor.setCaretColor(Color.WHITE);
        dateEditor.setDisabledTextColor(Color.WHITE);
        dateEditor.setSelectedTextColor(Color.WHITE);
        dateEditor.setSelectionColor(MAU_VIEN_THANH_TIM_KIEM);
        dateEditor.setFont(FONT_TEXTFIELD);
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

    // Tạo bảng hiển thị danh sách
    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã khuyến mãi", "Tên khuyến mãi", "Loại", "Giá trị", "Ngày bắt đầu", "Ngày kết thúc"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setBackground(MAU_NEN_ITEM);
        table.setForeground(MAU_CHU_CHUNG);
        table.setGridColor(MAU_LUOI_BANG);
        table.setRowHeight(CHIEU_CAO_HANG_BANG);
        table.setFont(FONT_BANG);
        table.setSelectionBackground(MAU_CHON_HANG);
        table.setSelectionForeground(MAU_CHU_CHUNG);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String maKM = tableModel.getValueAt(selectedRow, 0).toString();
                    String tenKM = tableModel.getValueAt(selectedRow, 1).toString();
                    String loaiKM = tableModel.getValueAt(selectedRow, 2).toString();
                    String giaTri = tableModel.getValueAt(selectedRow, 3).toString();
                    String ngayBatDau = tableModel.getValueAt(selectedRow, 4).toString();
                    String ngayKetThuc = tableModel.getValueAt(selectedRow, 5).toString();
                    
                    txtMaKhuyenMai.setText(maKM);
                    txtMaKhuyenMai.setForeground(MAU_CHU_CHUNG);
                    
                    txtTenKhuyenMai.setText(tenKM);
                    txtTenKhuyenMai.setForeground(MAU_CHU_CHUNG);
                    
                    cmbLoaiKhuyenMai.setSelectedItem(loaiKM);
                    
                    txtGiaTri.setText(giaTri);
                    txtGiaTri.setForeground(MAU_CHU_CHUNG);
                    
                    try {
                        dateNgayBatDau.setDate(dateFormat.parse(ngayBatDau));
                        dateNgayKetThuc.setDate(dateFormat.parse(ngayKetThuc));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(MAU_NEN_ITEM);
        centerRenderer.setForeground(MAU_CHU_CHUNG);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_ITEM);
        header.setForeground(MAU_CHU_CHUNG);
        header.setFont(FONT_HEADER_BANG);
        header.setPreferredSize(new Dimension(0, CHIEU_CAO_HEADER_BANG));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_LUOI_BANG));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(FONT_HEADER_BANG);
                label.setForeground(MAU_CHU_CHUNG);
                label.setBackground(MAU_NEN_ITEM);
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, MAU_LUOI_BANG),
                    new EmptyBorder(10, 5, 10, 5)
                ));
                return label;
            }
        });

        int[] widths = {120, 200, 150, 100, 120, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        scrollPane.getViewport().setBackground(MAU_NEN_ITEM);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = MAU_THANH_CUON_THUMB;
                trackColor = MAU_THANH_CUON_TRACK;
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Đọc dữ liệu từ database
    private void docDuLieuTuSQL() {
        try {
            List<KhuyenMai> danhSach = khuyenMaiDAO.getAllList();
            hienThiDanhSach(danhSach);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi đọc dữ liệu từ database: " + e.getMessage(),
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Hiển thị danh sách lên bảng
    private void hienThiDanhSach(List<KhuyenMai> danhSach) {
        tableModel.setRowCount(0);
        for (KhuyenMai km : danhSach) {
            tableModel.addRow(new Object[]{
                km.getMaKhuyenMai(),
                km.getTenKhuyenMai(),
                km.getLoaiKhuyenMai(),
                km.getGiaTriGiam(),
                dateFormat.format(km.getNgayBatDau()),
                dateFormat.format(km.getNgayKetThuc())
            });
        }
    }

    // Thực hiện sắp xếp danh sách
    private void thucHienSapXep(int loaiSapXep) {
        List<KhuyenMai> ketQuaSapXep;
        
        switch (loaiSapXep) {
            case 1: 
                ketQuaSapXep = khuyenMaiDAO.getAllListSorted("Tên");
                break;
            case 2: 
                ketQuaSapXep = khuyenMaiDAO.getAllListSorted("Tên");
                java.util.Collections.reverse(ketQuaSapXep);
                break;
            case 3: 
                ketQuaSapXep = khuyenMaiDAO.getAllListSorted("Giá trị");
                break;
            case 4: 
                ketQuaSapXep = khuyenMaiDAO.getAllListSorted("Giá trị");
                java.util.Collections.reverse(ketQuaSapXep);
                break;
            case 5: 
                ketQuaSapXep = khuyenMaiDAO.getAllListSorted("Ngày bắt đầu");
                break;
            case 6: 
                ketQuaSapXep = khuyenMaiDAO.getAllListSorted("Ngày bắt đầu");
                java.util.Collections.reverse(ketQuaSapXep);
                break;
            default:
                ketQuaSapXep = khuyenMaiDAO.getAllList();
                break;
        }
        
        hienThiDanhSach(ketQuaSapXep);
        panelChinh.requestFocusInWindow();
    }

    // Thực hiện lọc danh sách
    private void thucHienLoc() {
        String loaiDuocChon = (String) cmbLocLoai.getSelectedItem();
        List<KhuyenMai> ketQuaLoc;
        
        if (loaiDuocChon.equals("Loại khuyến mãi")) {
            ketQuaLoc = khuyenMaiDAO.getAllList();
        } else {
            ketQuaLoc = khuyenMaiDAO.getByLoaiKhuyenMai(loaiDuocChon);
        }
        
        hienThiDanhSach(ketQuaLoc);
        panelChinh.requestFocusInWindow();
    }
    
    // Làm mới giao diện
    private void lamMoiGiaoDien() {
        cmbSapXep.setSelectedIndex(0);
        cmbLocLoai.setSelectedIndex(0);
        txtTimKiem.setText("Tìm kiếm khuyến mãi. . .");
        txtTimKiem.setForeground(MAU_PLACEHOLDER);
        txtMaKhuyenMai.setText("");
        txtTenKhuyenMai.setText("Nhập tên khuyến mãi...");
        txtTenKhuyenMai.setForeground(MAU_PLACEHOLDER);
        txtGiaTri.setText("Nhập giá trị...");
        txtGiaTri.setForeground(MAU_PLACEHOLDER);
        cmbLoaiKhuyenMai.setSelectedIndex(0);
        dateNgayBatDau.setDate(null);
        dateNgayKetThuc.setDate(null);
     
        docDuLieuTuSQL();
        
        panelChinh.requestFocusInWindow();
    }
    
    // Làm mới form nhập liệu
    private void lamMoiForm() {
        txtMaKhuyenMai.setText("");
        txtTenKhuyenMai.setText("Nhập tên khuyến mãi...");
        txtTenKhuyenMai.setForeground(MAU_PLACEHOLDER);
        txtGiaTri.setText("Nhập giá trị...");
        txtGiaTri.setForeground(MAU_PLACEHOLDER);
        cmbLoaiKhuyenMai.setSelectedIndex(-1);
        dateNgayBatDau.setDate(null);
        dateNgayKetThuc.setDate(null);
        table.clearSelection();
    }
    
    // Tìm kiếm khuyến mãi
    private void timKiemKhuyenMai() {
        String tuKhoa = txtTimKiem.getText().trim();
        
        if (tuKhoa.isEmpty() || tuKhoa.equals("Tìm kiếm khuyến mãi. . .")) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập từ khóa tìm kiếm!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<KhuyenMai> ketQua = khuyenMaiDAO.timKiem(tuKhoa);
        
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy khuyến mãi với từ khóa: " + tuKhoa, 
                "Kết quả tìm kiếm", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        hienThiDanhSach(ketQua);
        panelChinh.requestFocusInWindow();
    }

    // Cập nhật thông tin khuyến mãi
    private void capNhatKhuyenMai() {
        String maKM = txtMaKhuyenMai.getText().trim();
        String tenKM = txtTenKhuyenMai.getText().trim();
        String giaTriStr = txtGiaTri.getText().trim();
        String loaiKM = (String) cmbLoaiKhuyenMai.getSelectedItem();
        Date ngayBatDau = dateNgayBatDau.getDate();
        Date ngayKetThuc = dateNgayKetThuc.getDate();
        
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (tenKM.isEmpty() || tenKM.equals("Nhập tên khuyến mãi...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khuyến mãi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtTenKhuyenMai.requestFocus();
            return;
        }
        
        if (giaTriStr.isEmpty() || giaTriStr.equals("Nhập giá trị...")) {
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
        
        if (loaiKM.equals("Giảm %") && giaTri > 100) {
            JOptionPane.showMessageDialog(this, 
                "Loại giảm giá là 'Giảm %' thì giá trị không được vượt quá 100!", 
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
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn cập nhật khuyến mãi này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            KhuyenMai km = new KhuyenMai(maKM, tenKM, loaiKM, ngayBatDau, ngayKetThuc, giaTri, 1);
            
            if (khuyenMaiDAO.capNhatKhuyenMai(km)) {
                JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lamMoiGiaoDien();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Xóa/Ẩn khuyến mãi
    private void xoaKhuyenMai() {
        String maKM = txtMaKhuyenMai.getText().trim();
        
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn ẩn khuyến mãi này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (khuyenMaiDAO.anKhuyenMai(maKM)) {
                JOptionPane.showMessageDialog(this, "Ẩn khuyến mãi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lamMoiGiaoDien();
            } else {
                JOptionPane.showMessageDialog(this, "Ẩn khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Hiển thị danh sách các khuyến mãi đã hết hạn
    private void hienThiDialogKhuyenMaiHetHan() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Danh sách khuyến mãi đã hết hạn", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(950, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã KM", "Tên khuyến mãi", "Loại", "Giá trị", "Ngày bắt đầu", "Ngày kết thúc"};
        DefaultTableModel modelDialog = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        List<KhuyenMai> dsHetHan = khuyenMaiDAO.getKhuyenMaiHetHan();
        if (dsHetHan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hiện tại không có khuyến mãi nào hết hạn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return; 
        }

        for (KhuyenMai km : dsHetHan) {
            modelDialog.addRow(new Object[]{
                km.getMaKhuyenMai(),
                km.getTenKhuyenMai(),
                km.getLoaiKhuyenMai(),
                km.getGiaTriGiam(),
                dateFormat.format(km.getNgayBatDau()),
                dateFormat.format(km.getNgayKetThuc())
            });
        }

        JTable tableDialog = new JTable(modelDialog);

        tableDialog.setBackground(MAU_NEN_ITEM);
        tableDialog.setForeground(MAU_CHU_CHUNG);
        tableDialog.setGridColor(MAU_LUOI_BANG);
        tableDialog.setRowHeight(CHIEU_CAO_HANG_BANG);
        tableDialog.setFont(FONT_BANG);
        tableDialog.setSelectionBackground(MAU_CHON_HANG);
        tableDialog.setSelectionForeground(MAU_CHU_CHUNG);
        
        tableDialog.setShowVerticalLines(true);
        tableDialog.setShowHorizontalLines(true);
        tableDialog.setIntercellSpacing(new Dimension(1, 1)); 

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(MAU_NEN_ITEM);
        centerRenderer.setForeground(MAU_CHU_CHUNG);
        for (int i = 0; i < tableDialog.getColumnCount(); i++) {
            tableDialog.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = tableDialog.getTableHeader();
        header.setBackground(MAU_NEN_ITEM);
        header.setForeground(MAU_CHU_CHUNG);
        header.setFont(FONT_HEADER_BANG);
        header.setPreferredSize(new Dimension(0, CHIEU_CAO_HEADER_BANG));
        
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_LUOI_BANG));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(FONT_HEADER_BANG);
                label.setForeground(MAU_CHU_CHUNG);
                label.setBackground(MAU_NEN_ITEM);
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, MAU_LUOI_BANG),
                    new EmptyBorder(10, 5, 10, 5)
                ));
                return label;
            }
        });

        int[] widths = {100, 200, 120, 100, 120, 120};
        for (int i = 0; i < widths.length && i < tableDialog.getColumnCount(); i++) {
            tableDialog.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(tableDialog);
        scroll.getViewport().setBackground(MAU_NEN_ITEM);
        scroll.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        
        JPanel corner = new JPanel();
        corner.setBackground(MAU_NEN_ITEM);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        
        scroll.getVerticalScrollBar().setBackground(MAU_NEN_ITEM);
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = MAU_THANH_CUON_THUMB;
                this.trackColor = MAU_NEN_ITEM;
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                return btn;
            }
        });

        dialog.add(scroll, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBottom.setBackground(MAU_NEN_TAB);

        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(FONT_NHAN);
        btnDong.setBackground(MAU_NUT_XOA);
        btnDong.setForeground(Color.WHITE);
        btnDong.setPreferredSize(new Dimension(100, 40));
        btnDong.setFocusPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnDong.addActionListener(e -> dialog.dispose());

        pnlBottom.add(btnDong);
        dialog.add(pnlBottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
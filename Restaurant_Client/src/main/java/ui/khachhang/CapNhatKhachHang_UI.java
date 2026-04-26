package ui.khachhang;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import java.time.LocalDate;     
import java.time.ZoneId;

import entity.KhachHang;
import dao_impl.KhachHang_DAO;

public class CapNhatKhachHang_UI extends JPanel {

    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
    private final Color MAU_CHU_LABEL = new Color(220, 220, 220);
    
    private final Color MAU_NUT_THEM = new Color(76, 175, 80);
    private final Color MAU_NUT_SUA = new Color(255, 193, 7);
    private final Color MAU_NUT_XOA = new Color(244, 67, 54);
    
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    private final Color MAU_THANH_CUON_THUMB = new Color(100, 104, 124);
    private final Color MAU_THANH_CUON_TRACK = new Color(31, 32, 44);

    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 50;
    private final int CHIEU_CAO_HEADER_BANG = 40;
    private final int CHIEU_CAO_INPUT = 40; 
    
    private final Dimension DIM_MA = new Dimension(155, CHIEU_CAO_INPUT);
    private final Dimension DIM_TEN = new Dimension(225, CHIEU_CAO_INPUT);
    private final Dimension DIM_SDT = new Dimension(165, CHIEU_CAO_INPUT);
    private final Dimension DIM_GT = new Dimension(150, CHIEU_CAO_INPUT);
    private final Dimension DIM_EMAIL = new Dimension(227, CHIEU_CAO_INPUT);
    private final Dimension DIM_DIACHI = new Dimension(275, CHIEU_CAO_INPUT);
    private final Dimension DIM_NGAY = new Dimension(180, CHIEU_CAO_INPUT);
    private final Dimension DIM_DIEM = new Dimension(150, CHIEU_CAO_INPUT);
    
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(300, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(150, 40);
    private final Dimension KICH_THUOC_NUT_CHUC_NANG = new Dimension(115, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14); 
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 13);

    private JTable table;
    private DefaultTableModel tableModel;
    
    private JTextField txtTimKiem;
    private JComboBox<String> cmbSapXep;
    private JComboBox<String> cmbGioiTinh;
    
    private JTextField txtMaKH;
    private JTextField txtTenKH;
    private JTextField txtSDT;
    private JTextField txtDiem;
    private JComboBox<String> cmbGioiTinhForm;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JDateChooser txtNgaySinh;
    
    private JPanel panelChinh;
    private KhachHang_DAO khachHangDAO;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    // Constructor khởi tạo
    public CapNhatKhachHang_UI() {
        khachHangDAO = new KhachHang_DAO();
        khoiTaoGiaoDien();
        docDuLieuTuSQL();
    }

    // Thiết lập cấu trúc giao diện chính
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB);

        panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(10, 25, 20, 25));
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
    
    // Tạo tiêu đề trang
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 90)); 
        panel.setBorder(new EmptyBorder(5, 0, 20, 0)); 
        
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setBackground(MAU_NEN_TAB);
        
        JLabel lblTieuDe = new JLabel("CẬP NHẬT KHÁCH HÀNG");
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

    // Tạo panel chứa các nút điều khiển và tìm kiếm
    private JPanel taoPanelDieuKhien() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 50));

        JPanel panelTimKiem = new JPanel();
        panelTimKiem.setLayout(new BoxLayout(panelTimKiem, BoxLayout.X_AXIS));
        panelTimKiem.setBackground(MAU_NEN_TAB);

        txtTimKiem = taoTextFieldTimKiem();
        
        cmbSapXep = taoComboBox(new String[]{"Sắp xếp", "Tên A-Z", "Tên Z-A", "Điểm cao-thấp", "Điểm thấp-cao"}, KICH_THUOC_COMBO_BOX);
        cmbSapXep.addActionListener(e -> {
            int selectedIndex = cmbSapXep.getSelectedIndex();
            if (selectedIndex == 0) lamMoiGiaoDien();
            else thucHienSapXep(selectedIndex);
        });
        
        cmbGioiTinh = taoComboBox(new String[]{"Giới tính", "Nam", "Nữ"}, KICH_THUOC_COMBO_BOX);
        cmbGioiTinh.addActionListener(e -> {
            int selectedIndex = cmbGioiTinh.getSelectedIndex();
            if (selectedIndex == 0) lamMoiGiaoDien();
            else thucHienLoc("gioitinh");
        });

        panelTimKiem.add(txtTimKiem);
        panelTimKiem.add(Box.createRigidArea(new Dimension(20, 0)));
        panelTimKiem.add(cmbSapXep);
        panelTimKiem.add(Box.createRigidArea(new Dimension(20, 0)));
        panelTimKiem.add(cmbGioiTinh);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelNut.setBackground(MAU_NEN_TAB);
        
        JButton btnSua = taoNutChucNang("Sửa", MAU_NUT_SUA);
        btnSua.addActionListener(e -> xuLySuaKhachHang());
        
        JButton btnXoa = taoNutChucNang("Xóa", MAU_NUT_XOA);
        btnXoa.addActionListener(e-> xoaKhachHang());
        
        JButton btnLamMoi = taoNutChucNang("Làm mới", new Color(33, 150, 243));
        btnLamMoi.addActionListener(e -> lamMoiGiaoDien());

        JButton btnDaXoa = taoNutChucNang("Xem KH đã xóa", new Color(100, 100, 100)); 
        btnDaXoa.setPreferredSize(new Dimension(140, 40));
        btnDaXoa.addActionListener(e -> hienThiDialogKhachHangDaXoa());
        
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        panelNut.add(btnDaXoa);

        panel.add(panelTimKiem, BorderLayout.WEST);
        panel.add(panelNut, BorderLayout.EAST);

        return panel;
    }

    // Tạo text field tìm kiếm
    private JTextField taoTextFieldTimKiem() {
        JTextField txt = new JTextField("Tìm kiếm khách hàng. . .");
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
        
        txt.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if(txt.getText().equals("Tìm kiếm khách hàng. . .")) { txt.setText(""); txt.setForeground(MAU_CHU_CHUNG); }
            }
            @Override public void focusLost(FocusEvent e) {
                if(txt.getText().isEmpty()) { txt.setText("Tìm kiếm khách hàng. . ."); txt.setForeground(MAU_PLACEHOLDER); }
            }
        });
        txt.addActionListener(e -> timKiemKhachHang());
        return txt;
    }
    
    // Tạo combobox custom
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
                    btn.setForeground(Color.WHITE);
                    btn.setBorder(BorderFactory.createEmptyBorder());
                    return btn;
                }
            }
        });

        return cmb;
    }
    
    // Tạo nút chức năng custom
    private JButton taoNutChucNang(String text, Color mauNen) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_NHAN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(mauNen);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(KICH_THUOC_NUT_CHUC_NANG);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(mauNen.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(mauNen); }
        });
        return btn;
    }
    
    // Tạo panel chứa form nhập liệu và bảng
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
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(MAU_NEN_TAB);

        txtMaKH = taoFieldCoNhan(panel, "Mã KH:", false, "", DIM_MA);
        txtTenKH = taoFieldCoNhan(panel, "Tên KH:", true, "Nhập tên...", DIM_TEN);
        txtSDT = taoFieldCoNhan(panel, "SĐT:", true, "Nhập SĐT...", DIM_SDT);
        cmbGioiTinhForm = taoComboBoxCoNhan(panel, "Giới tính:", new String[]{"Nam", "Nữ"}, DIM_GT);
        
        txtEmail = taoFieldCoNhan(panel, "Email:", true, "Nhập email...", DIM_EMAIL);
        txtDiaChi = taoFieldCoNhan(panel, "Địa chỉ:", true, "Nhập địa chỉ...", DIM_DIACHI);
        txtNgaySinh = taoDateChooserCoNhan(panel, "Ngày sinh:", DIM_NGAY);
        txtDiem = taoFieldCoNhan(panel, "Điểm:", false, "", DIM_DIEM);

        return panel;
    }

    // Tạo text field có nhãn
    private JTextField taoFieldCoNhan(JPanel parent, String labelText, boolean enabled, String placeholder, Dimension size) {
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
        txt.setPreferredSize(size);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));

        if (placeholder != null && !placeholder.isEmpty()) {
            txt.setText(placeholder);
            txt.setForeground(MAU_PLACEHOLDER);
            txt.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (txt.getText().equals(placeholder)) { txt.setText(""); txt.setForeground(MAU_CHU_CHUNG); }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (txt.getText().isEmpty()) { txt.setText(placeholder); txt.setForeground(MAU_PLACEHOLDER); }
                }
            });
        } else {
            txt.setForeground(MAU_CHU_CHUNG);
        }

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(txt, BorderLayout.CENTER);
        parent.add(wrapper);
        return txt;
    }

    // Tạo combobox có nhãn
    private JComboBox<String> taoComboBoxCoNhan(JPanel parent, String labelText, String[] items, Dimension size) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);

        JLabel label = new JLabel(labelText);
        label.setForeground(MAU_CHU_LABEL);
        label.setFont(FONT_NHAN);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        JComboBox<String> cmb = taoComboBox(items, size);
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(cmb, BorderLayout.CENTER);
        parent.add(wrapper);
        return cmb;
    }
    
    // Tạo date chooser có nhãn
    private JDateChooser taoDateChooserCoNhan(JPanel parent, String labelText, Dimension size) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);

        JLabel label = new JLabel(labelText);
        label.setForeground(MAU_CHU_LABEL);
        label.setFont(FONT_NHAN);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        JDateChooser dateChooser = new JDateChooser();
		dateChooser.setPreferredSize(new Dimension(140, 40));
		dateChooser.setDateFormatString("dd/MM/yyyy");
		dateChooser.setDate(new Date());
		dateChooser.setFont(FONT_TEXTFIELD);
		dateChooser.setBackground(MAU_THANH_TIM_KIEM);
		dateChooser.setForeground(MAU_CHU_CHUNG);
		dateChooser.getCalendarButton().setBackground(MAU_THANH_TIM_KIEM);
		dateChooser.getCalendarButton().setBorder(BorderFactory.createEmptyBorder());
		dateChooser.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
		dateChooser.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) dateChooser.getDateEditor().getUiComponent();
        dateEditor.setBackground(MAU_THANH_TIM_KIEM);
        dateEditor.setForeground(Color.WHITE);
        dateEditor.setCaretColor(Color.WHITE);
        dateEditor.setSelectedTextColor(Color.WHITE);
        dateEditor.setSelectionColor(MAU_VIEN_THANH_TIM_KIEM);
        dateEditor.setFont(FONT_TEXTFIELD);
        dateEditor.setBorder(new EmptyBorder(0, 8, 0, 0));
        dateEditor.setOpaque(true);
        dateEditor.setEditable(false); 
        
        dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(() -> dateEditor.setForeground(Color.WHITE));
            }
        });
        dateEditor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { dateEditor.setForeground(Color.WHITE); }
            @Override
            public void focusLost(FocusEvent e) { dateEditor.setForeground(Color.WHITE); }
        });


        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(dateChooser, BorderLayout.CENTER);
        parent.add(wrapper);
        return dateChooser;
    }

    // Tạo bảng hiển thị dữ liệu
    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã KH", "Họ tên", "Số điện thoại", "Giới tính", "Email", "Địa chỉ", "Ngày sinh", "Tích điểm"};
        
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
        
        int[] widths = {80, 150, 100, 70, 150, 200, 100, 80}; 
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    txtMaKH.setText(getValue(selectedRow, 0));
                    txtTenKH.setText(getValue(selectedRow, 1));
                    txtSDT.setText(getValue(selectedRow, 2));
                    cmbGioiTinhForm.setSelectedItem(getValue(selectedRow, 3));
                    txtEmail.setText(getValue(selectedRow, 4));
                    txtDiaChi.setText(getValue(selectedRow, 5));
                    
                    try {
                        String dateStr = getValue(selectedRow, 6);
                        if(dateStr != null && !dateStr.isEmpty()) {
                            Date date = sdf.parse(dateStr);
                            txtNgaySinh.setDate(date);
                        } else {
                            txtNgaySinh.setDate(null);
                        }
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    txtDiem.setText(getValue(selectedRow, 7));
                    
                    txtMaKH.setForeground(MAU_CHU_CHUNG);
                    txtTenKH.setForeground(MAU_CHU_CHUNG);
                    txtSDT.setForeground(MAU_CHU_CHUNG);
                    txtEmail.setForeground(MAU_CHU_CHUNG);
                    txtDiaChi.setForeground(MAU_CHU_CHUNG);
                    txtDiem.setForeground(MAU_CHU_CHUNG);
                }
            }
        });
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(MAU_NEN_ITEM);
        centerRenderer.setForeground(MAU_CHU_CHUNG);
        for(int i=0; i<table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

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

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        scrollPane.getViewport().setBackground(MAU_NEN_ITEM);
        scrollPane.setBackground(MAU_NEN_ITEM);
        
        tuyChinhScrollBar(scrollPane);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    // Tùy chỉnh thanh cuộn
    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setPreferredSize(new Dimension(8, 0));
        vertical.setBackground(MAU_NEN_TAB);
        vertical.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { thumbColor = MAU_THANH_CUON_THUMB; trackColor = MAU_THANH_CUON_TRACK; }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        });
        
        JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
        horizontal.setPreferredSize(new Dimension(0, 8));
        horizontal.setBackground(MAU_NEN_TAB);
        horizontal.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { thumbColor = MAU_THANH_CUON_THUMB; trackColor = MAU_THANH_CUON_TRACK; }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        });
    }
    
    // Tạo nút ẩn cho thanh cuộn
    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }
    
    // Lấy giá trị từ bảng
    private String getValue(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    // Xử lý logic xóa khách hàng
    private void xoaKhachHang() {
        if (txtMaKH.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn 'Xóa' khách hàng này?", 
            "Xác nhận xoá mềm", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangDAO.xoaKhachHang(txtMaKH.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                lamMoiGiaoDien();
            } else {
                JOptionPane.showMessageDialog(this, "Thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Xử lý logic cập nhật khách hàng
    private void xuLySuaKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa.", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!kiemTraDuLieu()) return;

        String hoTen = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        boolean gioiTinh = cmbGioiTinhForm.getSelectedItem().toString().equals("Nam");
        
        String email = txtEmail.getText().trim();
        if(email.equals("Nhập email...")) email = "";
        if(email.isEmpty()) email = null;
        
        String diaChi = txtDiaChi.getText().trim();
        if(diaChi.equals("Nhập địa chỉ...")) diaChi = "";
        if(diaChi.isEmpty()) diaChi = null;
        
        java.sql.Date sqlNgaySinh = null;
        if(txtNgaySinh.getDate() != null) {
            sqlNgaySinh = new java.sql.Date(txtNgaySinh.getDate().getTime());
        }
        
        int diem = 0;
        try { diem = Integer.parseInt(txtDiem.getText().trim()); } catch (Exception e) {}

        KhachHang kh = new KhachHang(maKH, hoTen, sdt, email, diaChi, sqlNgaySinh, gioiTinh, diem);

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận cập nhật thông tin?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangDAO.capNhatKhachHang(kh)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lamMoiGiaoDien();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Kiểm tra tính hợp lệ của dữ liệu đầu vào
    private boolean kiemTraDuLieu() {
        String hoTen = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        Date utilDate = txtNgaySinh.getDate();

        if (hoTen.isEmpty() || hoTen.equals("Nhập họ tên...")) {
            hienThiLoi("Vui lòng nhập họ tên!");
            return false;
        }
        
        if (sdt.isEmpty() || sdt.equals("Nhập SĐT...")) {
            hienThiLoi("Vui lòng nhập số điện thoại!");
            return false;
        }
        
     
        if (!email.isEmpty() && !email.equals("Nhập email...")) {
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
    
    // Hiển thị thông báo lỗi
    private void hienThiLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    // Đọc danh sách khách hàng từ DB
    private void docDuLieuTuSQL() {
        try {
            List<KhachHang> danhSach = khachHangDAO.docDanhSachKhachHang();
            hienThiDanhSach(danhSach);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Hiển thị danh sách lên bảng
    private void hienThiDanhSach(List<KhachHang> danhSach) {
        tableModel.setRowCount(0);
        for (KhachHang kh : danhSach) {
            String email = kh.getEmail() == null ? "" : kh.getEmail();
            String diaChi = kh.getDiaChi() == null ? "" : kh.getDiaChi();
            String ngaySinh = "";
            if(kh.getNgaySinh() != null) ngaySinh = sdf.format(kh.getNgaySinh());
            
            tableModel.addRow(new Object[]{
                kh.getMaKhachHang(),
                kh.getHoTen(),
                kh.getSoDienThoai(),
                kh.getGioiTinhString(),
                email,
                diaChi,
                ngaySinh,
                kh.getTichDiem(),
            });
        }
    }
    
    // Xử lý sắp xếp dữ liệu
    private void thucHienSapXep(int loaiSapXep) {
        List<KhachHang> ketQuaSapXep;
        switch (loaiSapXep) {
            case 1: ketQuaSapXep = khachHangDAO.sapXepTheoTen(true); break;
            case 2: ketQuaSapXep = khachHangDAO.sapXepTheoTen(false); break;
            case 3: ketQuaSapXep = khachHangDAO.sapXepTheoDiem(false); break;
            case 4: ketQuaSapXep = khachHangDAO.sapXepTheoDiem(true); break;
            default: ketQuaSapXep = khachHangDAO.docDanhSachKhachHang(); break;
        }
        hienThiDanhSach(ketQuaSapXep);
        panelChinh.requestFocusInWindow();
    }

    // Xử lý lọc dữ liệu
    private void thucHienLoc(String loaiLoc) {
        List<KhachHang> ketQuaLoc;
        if (loaiLoc.equals("gioitinh")) {
            String gioiTinhDuocChon = (String) cmbGioiTinh.getSelectedItem();
            if (gioiTinhDuocChon.equals("Nam")) {
                ketQuaLoc = khachHangDAO.locKhachHangTheoGioiTinh(true);
            } else {
                ketQuaLoc = khachHangDAO.locKhachHangTheoGioiTinh(false);
            }
        } else {
            ketQuaLoc = khachHangDAO.docDanhSachKhachHang();
        }
        hienThiDanhSach(ketQuaLoc);
        panelChinh.requestFocusInWindow();
    }

    // Xử lý tìm kiếm khách hàng
    private void timKiemKhachHang() {
        String tuKhoa = txtTimKiem.getText().trim();
        if (tuKhoa.isEmpty() || tuKhoa.equals("Tìm kiếm khách hàng. . .")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<KhachHang> ketQua;
        if (tuKhoa.toUpperCase().startsWith("KH")) {
            ketQua = khachHangDAO.timKiemTheoMa(tuKhoa);
        } else {
            ketQua = khachHangDAO.timKiemTheoTen(tuKhoa);
        }
        if(ketQua.isEmpty()) JOptionPane.showMessageDialog(this, "Không tìm thấy!", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
        hienThiDanhSach(ketQua);
        panelChinh.requestFocusInWindow();
    }

    // Hiển thị dialog danh sách khách hàng đã xóa để khôi phục
    private void hienThiDialogKhachHangDaXoa() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Danh sách khách hàng đã xóa", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã KH", "Họ tên", "SĐT", "Giới tính", "Email", "Địa chỉ", "Ngày sinh", "Điểm"};
        DefaultTableModel modelDialog = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

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

        int[] widths = {80, 150, 100, 70, 150, 200, 100, 80};
        for (int i = 0; i < widths.length && i < tableDialog.getColumnCount(); i++) {
            tableDialog.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(tableDialog);
        scroll.getViewport().setBackground(MAU_NEN_ITEM);
        scroll.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        
        scroll.getVerticalScrollBar().setBackground(MAU_NEN_TAB);
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { thumbColor = MAU_THANH_CUON_THUMB; trackColor = MAU_THANH_CUON_TRACK; }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        });
        
        dialog.add(scroll, BorderLayout.CENTER);

        List<KhachHang> listDaXoa = khachHangDAO.docDanhSachKhachHangDaXoa();
        for (KhachHang kh : listDaXoa) {
            String ngaySinhStr = (kh.getNgaySinh() != null) ? sdf.format(kh.getNgaySinh()) : "";
            modelDialog.addRow(new Object[]{
                kh.getMaKhachHang(), kh.getHoTen(), kh.getSoDienThoai(),
                kh.isGioiTinh() ? "Nam" : "Nữ", kh.getEmail(), kh.getDiaChi(),
                ngaySinhStr, kh.getTichDiem()
            });
        }

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBottom.setBackground(MAU_NEN_TAB);

        JButton btnKhoiPhuc = new JButton("Khôi phục");
        btnKhoiPhuc.setFont(FONT_NHAN);
        btnKhoiPhuc.setBackground(new Color(76, 175, 80)); 
        btnKhoiPhuc.setForeground(Color.WHITE);
        btnKhoiPhuc.setPreferredSize(new Dimension(120, 45));
        btnKhoiPhuc.setFocusPainted(false);
        btnKhoiPhuc.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(FONT_NHAN);
        btnDong.setBackground(MAU_NUT_XOA);
        btnDong.setForeground(Color.WHITE);
        btnDong.setPreferredSize(new Dimension(100, 45));
        btnDong.setFocusPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlBottom.add(btnKhoiPhuc);
        pnlBottom.add(btnDong);
        dialog.add(pnlBottom, BorderLayout.SOUTH);

        btnDong.addActionListener(e -> dialog.dispose());
        
        btnKhoiPhuc.addActionListener(e -> {
            int row = tableDialog.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách hàng để khôi phục!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String maKH = modelDialog.getValueAt(row, 0).toString();
            String tenKH = modelDialog.getValueAt(row, 1).toString();
            
            int confirm = JOptionPane.showConfirmDialog(dialog, 
                "Khôi phục khách hàng [" + tenKH + "]?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (khachHangDAO.khoiPhucKhachHang(maKH)) {
                    JOptionPane.showMessageDialog(dialog, "Đã khôi phục thành công!");
                    modelDialog.removeRow(row); 
                    lamMoiGiaoDien(); 
                } else {
                    JOptionPane.showMessageDialog(dialog, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.setVisible(true);
    }
    
    // Reset lại giao diện
    private void lamMoiGiaoDien() {
        docDuLieuTuSQL();
        txtMaKH.setText("");
        txtTenKH.setText("Nhập tên..."); txtTenKH.setForeground(MAU_PLACEHOLDER);
        txtSDT.setText("Nhập SĐT..."); txtSDT.setForeground(MAU_PLACEHOLDER);
        txtEmail.setText("Nhập email..."); txtEmail.setForeground(MAU_PLACEHOLDER);
        txtDiaChi.setText("Nhập địa chỉ..."); txtDiaChi.setForeground(MAU_PLACEHOLDER);
        txtNgaySinh.setDate(null);
        txtDiem.setText("");
        
        txtTimKiem.setText("Tìm kiếm khách hàng. . .");
        txtTimKiem.setForeground(MAU_PLACEHOLDER);
        cmbSapXep.setSelectedIndex(0);
        
        table.clearSelection();
        panelChinh.requestFocusInWindow();
    }
}
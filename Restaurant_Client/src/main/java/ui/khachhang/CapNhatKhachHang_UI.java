package ui.khachhang;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.rmi.Naming;

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
import rmi_interfaces.IKhachHang_DAO;

public class CapNhatKhachHang_UI extends JPanel {

    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
    private final Color MAU_CHU_LABEL = new Color(220, 220, 220);
    private final Color MAU_NUT_PHUC_HOI = new Color(76, 175, 80);
    private final Color MAU_NUT_SUA = new Color(255, 193, 7);
    private final Color MAU_NUT_XOA = new Color(244, 67, 54);
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    private final Color MAU_THANH_CUON_THUMB = new Color(100, 104, 124);
    private final Color MAU_THANH_CUON_TRACK = new Color(31, 32, 44);

    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 70;
    private final int CHIEU_CAO_HEADER_BANG = 45;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(220, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(160, 40);
    private final Dimension KICH_THUOC_NUT_CHUC_NANG = new Dimension(115, 40);
    private final Dimension KICH_THUOC_O_NHAP = new Dimension(150, 40);
    private final Dimension KICH_THUOC_DATE_PICKER = new Dimension(150, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 13);

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private JComboBox<String> cmbSapXep;
    private JComboBox<String> cmbLocGioiTinh;
    private JTextField txtMaKH;
    private JTextField txtTenKH;
    private JTextField txtSDT;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JComboBox<String> cmbGioiTinh;
    private JDateChooser txtNgaySinh;
    private JTextField txtDiem;
    private JPanel panelChinh;
    private IKhachHang_DAO khachHangDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private JButton btnSua;
    private JButton btnXoa;

    public CapNhatKhachHang_UI() {
        try {
            khachHangDAO = (IKhachHang_DAO) Naming.lookup("rmi://localhost:1099/KhachHang_DAO");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }
        khoiTaoGiaoDien();
        docDuLieuTuSQL();
    }

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

    private JPanel taoPanelDieuKhien() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 50));

        JPanel panelTimKiem = new JPanel();
        panelTimKiem.setLayout(new BoxLayout(panelTimKiem, BoxLayout.X_AXIS));
        panelTimKiem.setBackground(MAU_NEN_TAB);

        txtTimKiem = taoTextFieldTimKiem();

        cmbSapXep = taoComboBox(new String[]{"Sắp xếp tên", "Tên A-Z", "Tên Z-A", "Điểm cao-thấp", "Điểm thấp-cao"}, KICH_THUOC_COMBO_BOX);
        cmbSapXep.addActionListener(e -> {
            int selectedIndex = cmbSapXep.getSelectedIndex();
            if (selectedIndex == 0) {
                lamMoiGiaoDien();
            } else {
                thucHienSapXep(selectedIndex);
            }
        });

        cmbLocGioiTinh = taoComboBox(new String[]{"Lọc giới tính", "Nam", "Nữ"}, KICH_THUOC_COMBO_BOX);
        cmbLocGioiTinh.addActionListener(e -> {
            thucHienLoc();
        });

        panelTimKiem.add(txtTimKiem);
        panelTimKiem.add(Box.createRigidArea(new Dimension(20, 0)));
        panelTimKiem.add(cmbSapXep);
        panelTimKiem.add(Box.createRigidArea(new Dimension(20, 0)));
        panelTimKiem.add(cmbLocGioiTinh);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelNut.setBackground(MAU_NEN_TAB);

        btnSua = taoNutChucNang("Cập nhật", MAU_NUT_SUA);
        btnSua.addActionListener(e -> capNhatKhachHang());

        btnXoa = taoNutChucNang("Xóa", MAU_NUT_XOA);
        btnXoa.addActionListener(e -> xoaKhachHang());

        JButton btnLamMoi = taoNutChucNang("Làm mới", new Color(33, 150, 243));
        btnLamMoi.addActionListener(e -> lamMoiGiaoDien());

        JButton btnKhachHangDaXoa = taoNutChucNang("Phục hồi", MAU_NUT_PHUC_HOI);
        btnKhachHangDaXoa.addActionListener(e-> hienThiDialogKhachHangDaXoa());

        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        panelNut.add(btnKhachHangDaXoa);

        panel.add(panelTimKiem, BorderLayout.WEST);
        panel.add(panelNut, BorderLayout.EAST);

        return panel;
    }

    private JTextField taoTextFieldTimKiem() {
        JTextField txt = new JTextField() {
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
                } catch (Exception e) {}
            }
        };

        txt.setText("Tìm kiếm khách hàng. . .");
        txt.setForeground(MAU_PLACEHOLDER);
        txt.setBackground(MAU_THANH_TIM_KIEM);
        txt.setCaretColor(MAU_CHU_CHUNG);
        txt.setFont(FONT_TEXTFIELD);
        txt.setPreferredSize(new Dimension(300, 40));
        txt.setMaximumSize(new Dimension(300, 40));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
                new EmptyBorder(8, 15, 8, 40)
        ));

        txt.addActionListener(e -> timKiemKhachHang());

        txt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int iconX = txt.getWidth() - KICH_THUOC_ICON - 10;
                Rectangle iconBounds = new Rectangle(iconX, 0, KICH_THUOC_ICON + 10, txt.getHeight());
                if (iconBounds.contains(e.getPoint())) timKiemKhachHang();
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
                if (txt.getText().equals("Tìm kiếm khách hàng. . .")) {
                    txt.setText("");
                    txt.setForeground(MAU_CHU_CHUNG);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) {
                    txt.setText("Tìm kiếm khách hàng. . .");
                    txt.setForeground(MAU_PLACEHOLDER);
                }
            }
        });

        return txt;
    }

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

    private JPanel taoPanelNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(MAU_NEN_TAB);

        JPanel panelForm = taoPanelForm();
        panel.add(panelForm, BorderLayout.NORTH);

        JPanel panelBang = taoPanelBang();
        panel.add(panelBang, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(MAU_NEN_TAB);

        txtMaKH = taoFieldCoNhan(panel, "Mã khách hàng:", false, "");
        txtTenKH = taoFieldCoNhan(panel, "Tên khách hàng:", true, "Nhập tên...");
        txtSDT = taoFieldCoNhan(panel, "Số điện thoại:", true, "Nhập SĐT...");
        cmbGioiTinh = taoComboBoxCoNhan(panel, "Giới tính:", new String[]{"Nam", "Nữ"});
        txtNgaySinh = taoDateChooserCoNhan(panel, "Ngày sinh:");
        txtEmail = taoFieldCoNhan(panel, "Email:", true, "Nhập email...");
        txtDiaChi = taoFieldCoNhan(panel, "Địa chỉ:", true, "Nhập địa chỉ...");
        txtDiem = taoFieldCoNhan(panel, "Điểm tích lũy:", false, "0");

        return panel;
    }

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

    private JComboBox<String> taoComboBoxCoNhan(JPanel parent, String labelText, String[] items) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);

        JLabel label = new JLabel(labelText);
        label.setForeground(MAU_CHU_LABEL);
        label.setFont(FONT_NHAN);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        JComboBox<String> cmb = taoComboBox(items, new Dimension(100, 40));

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(cmb, BorderLayout.CENTER);

        if (parent.getComponentCount() > 0) {
            parent.add(Box.createRigidArea(new Dimension(15, 0)));
        }
        parent.add(wrapper);

        return cmb;
    }

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

    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã KH", "Tên KH", "SĐT", "Giới tính", "Ngày sinh", "Email", "Địa chỉ", "Điểm"};
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
                    hienThiThongTinKhachHang(selectedRow);
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

        int[] widths = {100, 150, 120, 80, 120, 150, 150, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        scrollPane.getViewport().setBackground(MAU_NEN_ITEM);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        tuyChinhScrollBar(scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void docDuLieuTuSQL() {
        if (khachHangDAO == null) return;
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<List<KhachHang>, Void>() {
            @Override
            protected List<KhachHang> doInBackground() throws Exception {
                return khachHangDAO.docDanhSachKhachHang();
            }
            @Override
            protected void done() {
                try {
                    hienThiDanhSach(get());
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void hienThiDanhSach(List<KhachHang> danhSach) {
        tableModel.setRowCount(0);
        for (KhachHang kh : danhSach) {
            String gioiTinhStr = kh.isGioiTinh() ? "Nam" : "Nữ";
            String ngaySinhStr = (kh.getNgaySinh() != null) ? dateFormat.format(kh.getNgaySinh()) : "";
            tableModel.addRow(new Object[]{
                    kh.getMaKhachHang(),
                    kh.getHoTen(),
                    kh.getSoDienThoai(),
                    gioiTinhStr,
                    ngaySinhStr,
                    kh.getEmail(),
                    kh.getDiaChi(),
                    kh.getTichDiem()
            });
        }
    }

    private void hienThiThongTinKhachHang(int row) {
        txtMaKH.setText(tableModel.getValueAt(row, 0).toString());
        txtTenKH.setText(tableModel.getValueAt(row, 1).toString());
        txtTenKH.setForeground(MAU_CHU_CHUNG);
        txtSDT.setText(tableModel.getValueAt(row, 2).toString());
        txtSDT.setForeground(MAU_CHU_CHUNG);

        String gioiTinh = tableModel.getValueAt(row, 3).toString();
        cmbGioiTinh.setSelectedItem(gioiTinh);

        String ngaySinhStr = tableModel.getValueAt(row, 4).toString();
        try {
            if (!ngaySinhStr.isEmpty()) {
                Date date = dateFormat.parse(ngaySinhStr);
                txtNgaySinh.setDate(date);
            } else {
                txtNgaySinh.setDate(null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtEmail.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
        txtEmail.setForeground(MAU_CHU_CHUNG);
        txtDiaChi.setText(tableModel.getValueAt(row, 6) != null ? tableModel.getValueAt(row, 6).toString() : "");
        txtDiaChi.setForeground(MAU_CHU_CHUNG);
        txtDiem.setText(tableModel.getValueAt(row, 7).toString());
        txtDiem.setForeground(MAU_CHU_CHUNG);
    }

    private void timKiemKhachHang() {
        if (khachHangDAO == null) return;
        String tuKhoa = txtTimKiem.getText().trim();
        if (tuKhoa.isEmpty() || tuKhoa.equals("Tìm kiếm khách hàng. . .")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<List<KhachHang>, Void>() {
            @Override
            protected List<KhachHang> doInBackground() throws Exception {
                if (tuKhoa.toUpperCase().startsWith("KH")) {
                    return khachHangDAO.timKiemTheoMa(tuKhoa);
                } else if (tuKhoa.matches("\\d+")) {
                    return khachHangDAO.timKiemTheoSDT(tuKhoa);
                } else {
                    return khachHangDAO.timKiemTheoTen(tuKhoa);
                }
            }
            @Override
            protected void done() {
                try {
                    List<KhachHang> ketQua = get();
                    if (ketQua == null || ketQua.isEmpty()) {
                        JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Không tìm thấy khách hàng: " + tuKhoa, "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                        tableModel.setRowCount(0);
                    } else {
                        hienThiDanhSach(ketQua);
                    }
                    panelChinh.requestFocusInWindow();
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void capNhatKhachHang() {
        if (khachHangDAO == null) return;
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tenKH = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        boolean gioiTinh = cmbGioiTinh.getSelectedItem().equals("Nam");
        Date utilDate = txtNgaySinh.getDate();
        java.sql.Date sqlDate = null;

        if (tenKH.isEmpty() || tenKH.equals("Nhập tên...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtTenKH.requestFocus();
            return;
        }

        if (sdt.isEmpty() || sdt.equals("Nhập SĐT...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return;
        }

        if (!sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return;
        }

        if (!email.isEmpty() && !email.equals("Nhập email...")) {
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "Địa chỉ email không hợp lệ.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtEmail.requestFocus();
                return;
            }
        } else {
            email = "";
        }

        if (diaChi.equals("Nhập địa chỉ...")) diaChi = "";

        if (utilDate != null) {
            LocalDate ngaySinh = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate homNay = LocalDate.now();
            if (ngaySinh.plusYears(16).isAfter(homNay)) {
                JOptionPane.showMessageDialog(this, "Khách hàng phải đủ 16 tuổi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            sqlDate = new java.sql.Date(utilDate.getTime());
        }

        int diemTichLuy = 0;
        try {
            diemTichLuy = Integer.parseInt(txtDiem.getText().trim());
        } catch (NumberFormatException e) {
            diemTichLuy = 0;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn cập nhật khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            btnSua.setEnabled(false);
            KhachHang kh = new KhachHang(maKH, tenKH, sdt, email, diaChi, sqlDate, gioiTinh, diemTichLuy, true);
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return khachHangDAO.capNhatKhachHang(kh);
                }
                @Override
                protected void done() {
                    btnSua.setEnabled(true);
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            lamMoiGiaoDien();
                        } else {
                            JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {}
                }
            };
            worker.execute();
        }
    }

    private void xoaKhachHang() {
        if (khachHangDAO == null) return;
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa (ẩn) khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            btnXoa.setEnabled(false);
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return khachHangDAO.xoaKhachHang(maKH);
                }
                @Override
                protected void done() {
                    btnXoa.setEnabled(true);
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            lamMoiGiaoDien();
                        } else {
                            JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {}
                }
            };
            worker.execute();
        }
    }

    private void thucHienSapXep(int loaiSapXep) {
        if (khachHangDAO == null) return;
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<List<KhachHang>, Void>() {
            @Override
            protected List<KhachHang> doInBackground() throws Exception {
                switch (loaiSapXep) {
                    case 1: return khachHangDAO.sapXepTheoTen(true);
                    case 2: return khachHangDAO.sapXepTheoTen(false);
                    case 3: return khachHangDAO.sapXepTheoDiem(false);
                    case 4: return khachHangDAO.sapXepTheoDiem(true);
                    default: return khachHangDAO.docDanhSachKhachHang();
                }
            }
            @Override
            protected void done() {
                try {
                    List<KhachHang> ketQuaSapXep = get();
                    if (ketQuaSapXep != null) hienThiDanhSach(ketQuaSapXep);
                } catch (Exception e) {}
                panelChinh.requestFocusInWindow();
            }
        };
        worker.execute();
    }

    private void thucHienLoc() {
        if (khachHangDAO == null) return;
        String gioiTinhStr = (String) cmbLocGioiTinh.getSelectedItem();
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<List<KhachHang>, Void>() {
            @Override
            protected List<KhachHang> doInBackground() throws Exception {
                if (gioiTinhStr.equals("Lọc giới tính")) {
                    return khachHangDAO.docDanhSachKhachHang();
                } else {
                    boolean isNam = gioiTinhStr.equals("Nam");
                    return khachHangDAO.locKhachHangTheoGioiTinh(isNam);
                }
            }
            @Override
            protected void done() {
                try {
                    hienThiDanhSach(get());
                } catch (Exception e) {}
                panelChinh.requestFocusInWindow();
            }
        };
        worker.execute();
    }

    private void hienThiDialogKhachHangDaXoa() {
        if (khachHangDAO == null) return;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Danh sách khách hàng đã xóa", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(950, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã KH", "Họ tên", "SĐT", "Giới tính", "Ngày sinh", "Email", "Điểm"};
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

        int[] widths = {100, 200, 120, 80, 120, 150, 80};
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
            @Override protected JButton createDecreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn;}
            @Override protected JButton createIncreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn;}
        });

        dialog.add(scroll, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBottom.setBackground(MAU_NEN_TAB);

        JButton btnPhucHoi = new JButton("Khôi phục");
        btnPhucHoi.setFont(FONT_NHAN);
        btnPhucHoi.setBackground(MAU_NUT_PHUC_HOI);
        btnPhucHoi.setForeground(Color.WHITE);
        btnPhucHoi.setPreferredSize(new Dimension(120, 40));
        btnPhucHoi.setFocusPainted(false);
        btnPhucHoi.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(FONT_NHAN);
        btnDong.setBackground(MAU_NUT_XOA);
        btnDong.setForeground(Color.WHITE);
        btnDong.setPreferredSize(new Dimension(100, 40));
        btnDong.setFocusPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlBottom.add(btnPhucHoi);
        pnlBottom.add(btnDong);
        dialog.add(pnlBottom, BorderLayout.SOUTH);

        btnDong.addActionListener(e -> dialog.dispose());

        btnPhucHoi.addActionListener(e -> {
            int row = tableDialog.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách hàng cần khôi phục!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String maKH = modelDialog.getValueAt(row, 0).toString();
            String tenKH = modelDialog.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(dialog, "Khôi phục khách hàng [" + tenKH + "]?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                btnPhucHoi.setEnabled(false);
                SwingWorker<Boolean, Void> resWorker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return khachHangDAO.khoiPhucKhachHang(maKH);
                    }
                    @Override
                    protected void done() {
                        btnPhucHoi.setEnabled(true);
                        try {
                            if (get()) {
                                JOptionPane.showMessageDialog(dialog, "Đã khôi phục thành công!");
                                modelDialog.removeRow(row);
                                lamMoiGiaoDien();
                            } else {
                                JOptionPane.showMessageDialog(dialog, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {}
                    }
                };
                resWorker.execute();
            }
        });

        SwingWorker<List<KhachHang>, Void> loadWorker = new SwingWorker<List<KhachHang>, Void>() {
            @Override
            protected List<KhachHang> doInBackground() throws Exception {
                return khachHangDAO.getKhachHangDaXoa();
            }
            @Override
            protected void done() {
                try {
                    List<KhachHang> dsDaXoa = get();
                    if (dsDaXoa.isEmpty()) {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Không có khách hàng nào trong thùng rác!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    for (KhachHang kh : dsDaXoa) {
                        modelDialog.addRow(new Object[]{
                                kh.getMaKhachHang(),
                                kh.getHoTen(),
                                kh.getSoDienThoai(),
                                kh.isGioiTinh() ? "Nam" : "Nữ",
                                (kh.getNgaySinh() != null) ? dateFormat.format(kh.getNgaySinh()) : "",
                                kh.getEmail(),
                                kh.getTichDiem()
                        });
                    }
                } catch (Exception ex) {}
            }
        };
        loadWorker.execute();

        dialog.setVisible(true);
    }

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
        cmbLocGioiTinh.setSelectedIndex(0);
        cmbGioiTinh.setSelectedIndex(0);

        table.clearSelection();
        panelChinh.requestFocusInWindow();
    }

    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setBackground(MAU_NEN_ITEM);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = MAU_THANH_CUON_THUMB;
                this.trackColor = MAU_NEN_ITEM;
            }
            @Override protected JButton createDecreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn; }
            @Override protected JButton createIncreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn; }
        });

        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setPreferredSize(new Dimension(0, 8));
        horizontalScrollBar.setBackground(MAU_NEN_ITEM);
        horizontalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = MAU_THANH_CUON_THUMB;
                this.trackColor = MAU_NEN_ITEM;
            }
            @Override protected JButton createDecreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn; }
            @Override protected JButton createIncreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn; }
        });
    }
}
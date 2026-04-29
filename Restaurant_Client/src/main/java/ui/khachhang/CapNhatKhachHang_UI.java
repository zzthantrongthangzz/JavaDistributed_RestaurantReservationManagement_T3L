package ui.khachhang;

import java.awt.*;
import java.awt.event.*;
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

import entity.KhachHang;
import rmi_interfaces.IKhachHang_Service;

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
    private final int CHIEU_CAO_HANG_BANG = 40;
    private final int CHIEU_CAO_HEADER_BANG = 45;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(400, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(180, 40);
    private final Dimension KICH_THUOC_NUT_CHUC_NANG = new Dimension(115, 40);
    private final Dimension KICH_THUOC_O_NHAP = new Dimension(200, 40);
    private final Dimension KICH_THUOC_DATE_PICKER = new Dimension(200, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 14);

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private JComboBox<String> cmbSapXep;
    private JTextField txtMaKH, txtHoTen, txtSdt, txtEmail, txtDiaChi;
    private JComboBox<String> cmbGioiTinh;
    private JDateChooser dateNgaySinh;
    private JPanel panelChinh;
    private IKhachHang_Service khachHangDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private JButton btnSua, btnXoa;

    public CapNhatKhachHang_UI() {
        try {
            khachHangDAO = (IKhachHang_Service) Naming.lookup("rmi://localhost:1099/KhachHangService");
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
        cmbSapXep = taoComboBox(new String[]{"Sắp xếp", "Tên A-Z", "Tên Z-A", "Điểm cao-thấp", "Điểm thấp-cao"}, KICH_THUOC_COMBO_BOX);
        cmbSapXep.addActionListener(e -> thucHienSapXep());

        panelTimKiem.add(txtTimKiem);
        panelTimKiem.add(Box.createRigidArea(new Dimension(20, 0)));
        panelTimKiem.add(cmbSapXep);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelNut.setBackground(MAU_NEN_TAB);

        btnSua = taoNutChucNang("Cập nhật", MAU_NUT_SUA);
        btnSua.addActionListener(e -> capNhatKhachHang());

        btnXoa = taoNutChucNang("Xóa", MAU_NUT_XOA);
        btnXoa.addActionListener(e -> xoaKhachHang());

        JButton btnLamMoi = taoNutChucNang("Làm mới", new Color(33, 150, 243));
        btnLamMoi.addActionListener(e -> lamMoiGiaoDien());

        JButton btnKHDaXoa = taoNutChucNang("KH đã xóa", MAU_NUT_PHUC_HOI);
        btnKHDaXoa.addActionListener(e -> hienThiDialogKhachHangDaXoa());

        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        panelNut.add(btnKHDaXoa);

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

        txt.setText("Tìm kiếm số điện thoại...");
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

        txt.addActionListener(e -> timKiemKhachHang());
        txt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int iconX = txt.getWidth() - KICH_THUOC_ICON - 10;
                Rectangle iconBounds = new Rectangle(iconX, 0, KICH_THUOC_ICON + 10, txt.getHeight());
                if (iconBounds.contains(e.getPoint())) {
                    timKiemKhachHang();
                }
            }
        });
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().equals("Tìm kiếm số điện thoại...")) {
                    txt.setText("");
                    txt.setForeground(MAU_CHU_CHUNG);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) {
                    txt.setText("Tìm kiếm số điện thoại...");
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
        panel.add(taoPanelForm(), BorderLayout.NORTH);
        panel.add(taoPanelBang(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(MAU_NEN_TAB);

        txtMaKH = taoFieldCoNhan(panel, "Mã khách hàng:", false);
        txtHoTen = taoFieldCoNhan(panel, "Họ và tên:", true);
        txtSdt = taoFieldCoNhan(panel, "Số điện thoại:", true);
        cmbGioiTinh = taoComboBoxCoNhan(panel, "Giới tính:", new String[]{"Nam", "Nữ"});
        dateNgaySinh = taoDateChooserCoNhan(panel, "Ngày sinh:");
        txtEmail = taoFieldCoNhan(panel, "Email:", true);
        txtDiaChi = taoFieldCoNhan(panel, "Địa chỉ:", true);

        return panel;
    }

    private JTextField taoFieldCoNhan(JPanel parent, String labelText, boolean enabled) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);
        JLabel label = new JLabel(labelText);
        label.setForeground(MAU_CHU_LABEL);
        label.setFont(FONT_NHAN);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        JTextField txtf = new JTextField();
        txtf.setEnabled(enabled);
        txtf.setCaretColor(MAU_CHU_CHUNG);
        txtf.setForeground(MAU_CHU_CHUNG);
        txtf.setFont(FONT_TEXTFIELD);
        txtf.setBackground(enabled ? MAU_THANH_TIM_KIEM : new Color(50, 52, 60));
        txtf.setPreferredSize(KICH_THUOC_O_NHAP);
        txtf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
                new EmptyBorder(8, 15, 8, 15)
        ));

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(txtf, BorderLayout.CENTER);

        if (parent.getComponentCount() > 0) parent.add(Box.createRigidArea(new Dimension(10, 0)));
        parent.add(wrapper);
        return txtf;
    }

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
        if (parent.getComponentCount() > 0) parent.add(Box.createRigidArea(new Dimension(10, 0)));
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
        dateTextField.setForeground(Color.WHITE);
        dateTextField.setFont(FONT_TEXTFIELD);
        dateTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
                new EmptyBorder(8, 15, 8, 15)
        ));
        dateTextField.setCaretColor(Color.WHITE);
        dateTextField.setOpaque(true);

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(dateChooser, BorderLayout.CENTER);
        if (parent.getComponentCount() > 0) parent.add(Box.createRigidArea(new Dimension(10, 0)));
        parent.add(wrapper);
        return dateChooser;
    }

    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã KH", "Họ tên", "Số điện thoại", "Email", "Địa chỉ", "Ngày sinh", "Giới tính", "Điểm"};
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
                    txtMaKH.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtHoTen.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtSdt.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtEmail.setText(tableModel.getValueAt(selectedRow, 3) != null ? tableModel.getValueAt(selectedRow, 3).toString() : "");
                    txtDiaChi.setText(tableModel.getValueAt(selectedRow, 4) != null ? tableModel.getValueAt(selectedRow, 4).toString() : "");
                    try {
                        String ns = tableModel.getValueAt(selectedRow, 5).toString();
                        if (!ns.equals("N/A")) dateNgaySinh.setDate(dateFormat.parse(ns));
                        else dateNgaySinh.setDate(null);
                    } catch (Exception ex) {}
                    cmbGioiTinh.setSelectedItem(tableModel.getValueAt(selectedRow, 6).toString());
                }
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(MAU_NEN_ITEM);
        centerRenderer.setForeground(MAU_CHU_CHUNG);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 4) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_ITEM);
        header.setForeground(MAU_CHU_CHUNG);
        header.setFont(FONT_HEADER_BANG);
        header.setPreferredSize(new Dimension(0, CHIEU_CAO_HEADER_BANG));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_LUOI_BANG));

        int[] widths = {80, 200, 120, 180, 250, 100, 80, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        scrollPane.getViewport().setBackground(MAU_NEN_ITEM);
        tuyChinhScrollBar(scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setBackground(MAU_NEN_ITEM);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = MAU_THANH_CUON_THUMB; this.trackColor = MAU_NEN_ITEM; }
            @Override protected JButton createDecreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn; }
            @Override protected JButton createIncreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn; }
        });

        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setPreferredSize(new Dimension(0, 8));
        horizontalScrollBar.setBackground(MAU_NEN_ITEM);
        horizontalScrollBar.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = MAU_THANH_CUON_THUMB; this.trackColor = MAU_NEN_ITEM; }
            @Override protected JButton createDecreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn; }
            @Override protected JButton createIncreaseButton(int orientation) { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0,0)); return btn; }
        });
    }

    private void docDuLieuTuSQL() {
        if (khachHangDAO == null) return;
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
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
            String ngaySinhStr = (kh.getNgaySinh() != null) ? dateFormat.format(kh.getNgaySinh()) : "N/A";
            tableModel.addRow(new Object[]{
                    kh.getMaKhachHang(),
                    kh.getHoTen(),
                    kh.getSoDienThoai(),
                    kh.getEmail() != null ? kh.getEmail() : "",
                    kh.getDiaChi() != null ? kh.getDiaChi() : "",
                    ngaySinhStr,
                    kh.isGioiTinh() ? "Nam" : "Nữ",
                    kh.getTichDiem()
            });
        }
    }

    private void capNhatKhachHang() {
        if (khachHangDAO == null) return;
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật!");
            return;
        }

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(txtMaKH.getText());
        kh.setHoTen(txtHoTen.getText());
        kh.setSoDienThoai(txtSdt.getText());
        kh.setEmail(txtEmail.getText());
        kh.setDiaChi(txtDiaChi.getText());
        kh.setGioiTinh(cmbGioiTinh.getSelectedIndex() == 0);
        kh.setTichDiem(Integer.parseInt(tableModel.getValueAt(row, 7).toString()));

        Date ns = dateNgaySinh.getDate();
        if (ns != null) {
            kh.setNgaySinh(new java.sql.Date(ns.getTime()));
        }

        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn cập nhật?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            btnSua.setEnabled(false);
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return khachHangDAO.capNhatKhachHang(kh);
                }
                @Override
                protected void done() {
                    btnSua.setEnabled(true);
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Cập nhật thành công!");
                            lamMoiGiaoDien();
                        } else {
                            JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Cập nhật thất bại!");
                        }
                    } catch (Exception e) {}
                }
            };
            worker.execute();
        }
    }

    private void xoaKhachHang() {
        if (khachHangDAO == null) return;
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!");
            return;
        }
        String ma = txtMaKH.getText();
        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            btnXoa.setEnabled(false);
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return khachHangDAO.xoaKhachHang(ma);
                }
                @Override
                protected void done() {
                    btnXoa.setEnabled(true);
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Xóa thành công!");
                            lamMoiGiaoDien();
                        } else {
                            JOptionPane.showMessageDialog(CapNhatKhachHang_UI.this, "Xóa thất bại!");
                        }
                    } catch (Exception e) {}
                }
            };
            worker.execute();
        }
    }

    private void timKiemKhachHang() {
        if (khachHangDAO == null) return;
        String sdt = txtTimKiem.getText().trim();
        if (sdt.isEmpty() || sdt.equals("Tìm kiếm số điện thoại...")) {
            docDuLieuTuSQL();
            return;
        }
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<KhachHang> doInBackground() throws Exception {
                return khachHangDAO.timKiemTheoSDT(sdt);
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

    private void thucHienSapXep() {
        if (khachHangDAO == null) return;
        int index = cmbSapXep.getSelectedIndex();
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<KhachHang> doInBackground() throws Exception {
                if (index == 1) return khachHangDAO.sapXepTheoTen(true);
                if (index == 2) return khachHangDAO.sapXepTheoTen(false);
                if (index == 3) return khachHangDAO.sapXepTheoDiem(false);
                if (index == 4) return khachHangDAO.sapXepTheoDiem(true);
                return khachHangDAO.docDanhSachKhachHang();
            }
            @Override
            protected void done() {
                try { hienThiDanhSach(get()); } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void hienThiDialogKhachHangDaXoa() {
        if (khachHangDAO == null) return;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Khách hàng đã xóa", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);

        DefaultTableModel modelDialog = new DefaultTableModel(new String[]{"Mã KH", "Họ tên", "SĐT"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tableDialog = new JTable(modelDialog);
        dialog.add(new JScrollPane(tableDialog), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel();
        JButton btnKhoiPhuc = new JButton("Khôi phục");
        pnlBottom.add(btnKhoiPhuc);
        dialog.add(pnlBottom, BorderLayout.SOUTH);

        btnKhoiPhuc.addActionListener(e -> {
            int row = tableDialog.getSelectedRow();
            if (row != -1) {
                String ma = modelDialog.getValueAt(row, 0).toString();
                SwingWorker<Boolean, Void> w = new SwingWorker<>() {
                    @Override protected Boolean doInBackground() throws Exception { return khachHangDAO.khoiPhucKhachHang(ma); }
                    @Override protected void done() {
                        try {
                            if (get()) {
                                JOptionPane.showMessageDialog(dialog, "Khôi phục thành công!");
                                dialog.dispose();
                                lamMoiGiaoDien();
                            }
                        } catch (Exception ex) {}
                    }
                };
                w.execute();
            }
        });

        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override protected List<KhachHang> doInBackground() throws Exception { return khachHangDAO.getKhachHangDaXoa(); }
            @Override protected void done() {
                try {
                    List<KhachHang> ds = get();
                    for(KhachHang kh : ds) {
                        modelDialog.addRow(new Object[]{kh.getMaKhachHang(), kh.getHoTen(), kh.getSoDienThoai()});
                    }
                } catch (Exception e) {}
            }
        };
        worker.execute();
        dialog.setVisible(true);
    }

    private void lamMoiGiaoDien() {
        txtMaKH.setText("");
        txtHoTen.setText("");
        txtSdt.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        cmbGioiTinh.setSelectedIndex(0);
        dateNgaySinh.setDate(null);
        txtTimKiem.setText("Tìm kiếm số điện thoại...");
        txtTimKiem.setForeground(MAU_PLACEHOLDER);
        cmbSapXep.setSelectedIndex(0);
        docDuLieuTuSQL();
    }
}
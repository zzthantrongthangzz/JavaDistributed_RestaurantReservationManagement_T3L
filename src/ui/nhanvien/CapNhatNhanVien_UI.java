package ui.nhanvien;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter; 
import java.time.format.DateTimeParseException; 
import java.util.ArrayList;
import java.util.List;

import dao.NhanVien_DAO;
import dao.ChucVu_DAO;
import entity.NhanVien;
import entity.ChucVu;
import ui.TrangChu_UI;

public class CapNhatNhanVien_UI extends JPanel {

    // Khai báo các hằng số màu sắc
    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
    private final Color MAU_CHU_LABEL = new Color(220, 220, 220);
    private final Color MAU_NUT_DA_NGHI = new Color(76, 175, 80);
    private final Color MAU_NUT_SUA = new Color(255, 193, 7); 
    private final Color MAU_NUT_XOA = new Color(244, 67, 54); 
	private final Color MAU_NUT_KHOI_PHUC = new Color(107, 107, 107);

    private final Color MAU_NUT_LAM_MOI = new Color(30, 144, 255); 
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    private final Color MAU_THANH_CUON_THUMB = new Color(100, 104, 124);

    // Khai báo kích thước
    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 70;
    private final int CHIEU_CAO_HEADER_BANG = 45;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(190, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(170, 40);
    private final Dimension KICH_THUOC_NUT_CHUC_NANG = new Dimension(115, 40);
    private final Dimension KICH_THUOC_O_NHAP = new Dimension(150, 40);

    // Khai báo font chữ
    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 13);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Khai báo component
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private JComboBox<String> cmbSapXep;
    private JComboBox<String> cmbGioiTinh;
    private JPanel panelChinh;
    
    private JTextField txtMaNV, txtHoTen, txtSDT, txtEmail, txtDiaChi, txtNgaySinh;
    private JComboBox<String> cboGioiTinhForm;
    private JComboBox<ChucVu> cboChucVu;
    
    private NhanVien_DAO nhanVienDAO;
    private ChucVu_DAO chucVuDAO;
	private JButton btnCapNhat;
	private JButton btnXoa;
	private JButton btnLamMoi;
	private JButton btnNVDaNghi;
	private JButton btnKhoiPhuc;
	private JPanel wrapperTimKiem;

    public CapNhatNhanVien_UI() {
        nhanVienDAO = new NhanVien_DAO();
        chucVuDAO = new ChucVu_DAO();
        khoiTaoGiaoDien();
        taiDuLieuChucVu();
        loadDataToTable();
    }

    // Khởi tạo giao diện chính
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB);

        panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(20, 25, 20, 25));
        panelChinh.setFocusable(true);
        panelChinh.add(taoPanelDieuKhien(), BorderLayout.NORTH);
        panelChinh.add(taoPanelNoiDung(), BorderLayout.CENTER);
        panelChinh.requestFocusInWindow();

        add(panelChinh, BorderLayout.CENTER);
    }

    // Tạo panel chứa các nút điều khiển và tìm kiếm
    private JPanel taoPanelDieuKhien() {
        JPanel panel = new JPanel(new BorderLayout(30, 0));
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 50));

        JPanel panelTimKiem = new JPanel();
        panelTimKiem.setLayout(new BoxLayout(panelTimKiem, BoxLayout.X_AXIS));
        panelTimKiem.setBackground(MAU_NEN_TAB);

        wrapperTimKiem = taoWrapperTimKiemCoNhan("Tìm kiếm:", "Tìm kiếm nhân viên...");
        
        txtTimKiem = (JTextField) wrapperTimKiem.getComponent(1);
        
        int chuanChieuCao = 40;
        Dimension maxSize = new Dimension(500, chuanChieuCao);
        wrapperTimKiem.setMaximumSize(maxSize);
        
        cmbSapXep = taoComboBox(new String[]{"Sắp xếp", "Tên A-Z", "Tên Z-A", "Chức vụ"}, KICH_THUOC_COMBO_BOX);
        cmbSapXep.addActionListener(e -> {
            int selectedIndex = cmbSapXep.getSelectedIndex();
            if (selectedIndex == 0) {
                loadDataToTable();
            } else {
                thucHienSapXep(selectedIndex);
            }
        });
        
        cmbGioiTinh = taoComboBox(new String[]{"Giới tính", "Nam", "Nữ"}, KICH_THUOC_COMBO_BOX);
        cmbGioiTinh.addActionListener(e -> {
            int selectedIndex = cmbGioiTinh.getSelectedIndex();
            if (selectedIndex == 0) {
                loadDataToTable();
            } else {
                thucHienLoc("gioitinh");
            }
        });

        panelTimKiem.add(wrapperTimKiem);
        panelTimKiem.add(Box.createRigidArea(new Dimension(20, 0)));
        panelTimKiem.add(cmbSapXep);
        panelTimKiem.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiem.add(cmbGioiTinh);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 4));
        panelNut.setBackground(MAU_NEN_TAB);

        btnCapNhat = taoNutChucNang("Cập nhật", MAU_NUT_SUA);
        btnCapNhat.addActionListener(e -> capNhatNhanVien());
        
        btnXoa = taoNutChucNang("Xóa", MAU_NUT_XOA);
        btnXoa.addActionListener(e -> xoaNhanVien());
        
        btnLamMoi = taoNutChucNang("Làm mới", MAU_NUT_LAM_MOI);
        btnLamMoi.addActionListener(e -> lamMoiGiaoDien());

        btnNVDaNghi =taoNutChucNang("NV đã nghỉ", MAU_NUT_DA_NGHI);
        btnNVDaNghi.addActionListener(e-> hienThiDialogNVDaNghi());
        
        btnKhoiPhuc=taoNutChucNang("Khôi phục", MAU_NUT_KHOI_PHUC);
        btnKhoiPhuc.addActionListener(e-> khoiPhucNhanVien());
        
        panelNut.add(btnCapNhat);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        panelNut.add(btnNVDaNghi);
        
        panel.add(panelTimKiem, BorderLayout.WEST);
        panel.add(panelNut, BorderLayout.EAST);

        return panel;
    }

    // Khôi phục nhân viên đã xóa
    private void khoiPhucNhanVien() {
        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần khôi phục!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn khôi phục nhân viên " + txtHoTen.getText() + " làm việc lại?", 
            "Xác nhận khôi phục", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (nhanVienDAO.khoiPhucNhanVien(txtMaNV.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Khôi phục nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                hienThiDialogNVDaNghi();
            } else {
                JOptionPane.showMessageDialog(this, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Hiển thị dialog danh sách nhân viên đã nghỉ việc
    private void hienThiDialogNVDaNghi() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Danh sách nhân viên đã nghỉ việc", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(1100, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã NV", "Họ tên", "Giới tính", "SĐT", "Email", "Ngày sinh", "Địa chỉ", "Chức vụ"};
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

        int[] widths = {100, 150, 80, 120, 180, 100, 150, 100};
        for (int i = 0; i < widths.length && i < tableDialog.getColumnCount(); i++) {
            tableDialog.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(tableDialog);
        scroll.getViewport().setBackground(MAU_NEN_ITEM);
        
        scroll.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        
        JPanel corner = new JPanel();
        corner.setBackground(MAU_NEN_ITEM);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        
        tuyChinhScrollBar(scroll);
        
        dialog.add(scroll, BorderLayout.CENTER);

        List<NhanVien> listDaNghi = nhanVienDAO.getAllNhanVienDaNghi();
        for (NhanVien nv : listDaNghi) {
            modelDialog.addRow(new Object[]{
                nv.getMaNhanVien(), nv.getHoTen(), nv.isGioiTinh() ? "Nam" : "Nữ",
                nv.getSoDienThoai(), nv.getEmail(), formatNgaySinh(nv.getNgaySinh()), 
                nv.getDiaChi(), nv.getChucVu().getTenChucVu()
            });
        }

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBottom.setBackground(MAU_NEN_TAB);

        JButton btnKhoiPhucDialog = new JButton("Khôi phục nhân viên");
        btnKhoiPhucDialog.setFont(FONT_NHAN);
        btnKhoiPhucDialog.setBackground(MAU_NUT_DA_NGHI); 
        btnKhoiPhucDialog.setForeground(Color.WHITE);
        btnKhoiPhucDialog.setPreferredSize(new Dimension(200, 45));
        btnKhoiPhucDialog.setFocusPainted(false);
        btnKhoiPhucDialog.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(FONT_NHAN);
        btnDong.setBackground(MAU_NUT_XOA);
        btnDong.setForeground(Color.WHITE);
        btnDong.setPreferredSize(new Dimension(100, 45));
        btnDong.setFocusPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlBottom.add(btnKhoiPhucDialog);
        pnlBottom.add(btnDong);
        dialog.add(pnlBottom, BorderLayout.SOUTH);

        btnDong.addActionListener(e -> dialog.dispose());
        btnKhoiPhucDialog.addActionListener(e -> {
            int row = tableDialog.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn nhân viên để khôi phục!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String maNV = modelDialog.getValueAt(row, 0).toString();
            String tenNV = modelDialog.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(dialog, "Khôi phục nhân viên [" + tenNV + "]?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (nhanVienDAO.khoiPhucNhanVien(maNV)) {
                    JOptionPane.showMessageDialog(dialog, "Đã khôi phục thành công!");
                    modelDialog.removeRow(row);
                    lamMoiGiaoDien(); 
                }
            }
        });

        dialog.setVisible(true);
    }

	private JPanel taoWrapperTimKiemCoNhan(String labelText, String placeholder) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_NHAN);
        label.setForeground(Color.WHITE);
        label.setBackground(new Color(124, 124, 124));
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JTextField txt = taoTextFieldTimKiem(placeholder);

        wrapper.add(label, BorderLayout.WEST);
        wrapper.add(txt, BorderLayout.CENTER);

        return wrapper;
    }

    // Tạo TextField tìm kiếm tùy chỉnh
    private JTextField taoTextFieldTimKiem(String holder) {
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

        txt.setText(holder);
        txt.setForeground(MAU_PLACEHOLDER);
        txt.setBackground(MAU_THANH_TIM_KIEM);
        txt.setCaretColor(MAU_CHU_CHUNG);
        txt.setFont(FONT_TEXTFIELD);
        txt.setPreferredSize(KICH_THUOC_THANH_TIM_KIEM);
        txt.setMaximumSize(new Dimension(400, 40));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
            new EmptyBorder(8, 15, 8, 40)
        ));

        txt.addActionListener(e -> timKiemNhanVien());
        txt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int iconX = txt.getWidth() - KICH_THUOC_ICON - 10;
                Rectangle iconBounds = new Rectangle(iconX, 0, KICH_THUOC_ICON + 10, txt.getHeight());
                if (iconBounds.contains(e.getPoint())) timKiemNhanVien();
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
                if (txt.getText().equals(holder)) {
                    txt.setText("");
                    txt.setForeground(MAU_CHU_CHUNG);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) {
                    txt.setText(holder);
                    txt.setForeground(MAU_PLACEHOLDER);
                }
            }
        });
        return txt;
    }

    // Tạo ComboBox tùy chỉnh
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
                    btn.setForeground(MAU_CHU_CHUNG);
                    return btn;
                }
            }
        });
        return cmb;
    }

    // Tạo nút chức năng tùy chỉnh
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

    // Tạo bảng hiển thị dữ liệu
    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã NV", "Họ tên", "Giới tính", "SĐT", "Email", "Ngày sinh", "Địa chỉ", "Chức vụ"};
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
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) hienThiThongTinNhanVien(row);
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

        int[] widths = {100, 150, 80, 120, 180, 100, 150, 100};
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
    
    // Tạo form nhập liệu
    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(MAU_NEN_TAB);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        txtMaNV = taoFieldCoNhan(panel, "Mã nhân viên:", false, "");
        txtHoTen = taoFieldCoNhan(panel, "Họ tên:", true, "Nhập họ và tên...");
        cboGioiTinhForm = taoComboBoxCoNhan(panel, "Giới tính:", new String[]{"Nam", "Nữ"});
        txtSDT = taoFieldCoNhan(panel, "Số điện thoại:", true, "Nhập SĐT...");
        txtEmail = taoFieldCoNhan(panel, "Email:", true, "Nhập email...");
        
        txtNgaySinh = taoFieldCoNhan(panel, "Ngày sinh:", true, "dd-MM-yyyy");
        
        txtDiaChi = taoFieldCoNhan(panel, "Địa chỉ:", true, "Nhập địa chỉ...");
        
        JPanel chucVuWrapper = taoComboBoxChucVuCoNhan(panel, "Chức vụ:");
        cboChucVu = (JComboBox<ChucVu>) ((JPanel)chucVuWrapper.getComponent(1)).getComponent(0);
        
        return panel;
    }

    // Tạo ô nhập liệu có nhãn
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

    // Tạo ComboBox có nhãn
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
    
    // Tạo ComboBox chức vụ có nhãn
    private JPanel taoComboBoxChucVuCoNhan(JPanel parent, String labelText) {
        JPanel wrapperLabel = new JPanel(new BorderLayout());
        wrapperLabel.setBackground(MAU_NEN_TAB);

        JLabel label = new JLabel(labelText);
        label.setForeground(MAU_CHU_LABEL);
        label.setFont(FONT_NHAN);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        JPanel wrapperComboButton = new JPanel(new BorderLayout(5, 0));
        wrapperComboButton.setOpaque(false);

        JComboBox<ChucVu> cmb = new JComboBox<>();
        setupComboBoxUI(cmb);
        cmb.setPreferredSize(KICH_THUOC_O_NHAP);

        wrapperComboButton.add(cmb, BorderLayout.CENTER);

        wrapperLabel.add(label, BorderLayout.NORTH);
        wrapperLabel.add(wrapperComboButton, BorderLayout.CENTER);

        if (parent.getComponentCount() > 0) {
            parent.add(Box.createRigidArea(new Dimension(15, 0)));
        }
        parent.add(wrapperLabel);

        return wrapperLabel;
    }
    
    private <T> void setupComboBoxUI(JComboBox<T> cmb) {
        cmb.setFont(FONT_NHAN);
        cmb.setBackground(MAU_THANH_TIM_KIEM);
        cmb.setForeground(MAU_CHU_CHUNG);
        cmb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmb.setFocusable(false);
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
                    btn.setForeground(MAU_CHU_CHUNG);
                    return btn;
                }
            }
        });
    }
    
    // Tạo nút bấm có icon
    private JButton createIconButton(String iconPath, Color backgroundColor, int iconSize) {
        JButton button = new JButton();
        button.setBackground(backgroundColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(5, 5, 5, 5));
        button.setFocusPainted(false);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledIcon = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledIcon));
        } catch (Exception e) {
            System.err.println("Không tìm thấy icon: " + iconPath);
            button.setText("+"); 
        }
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(backgroundColor.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(backgroundColor); }
        });
        return button;
    }

    // Tải dữ liệu từ database vào bảng
    private void loadDataToTable() {
        tableModel.setRowCount(0);
        ArrayList<NhanVien> danhSach = nhanVienDAO.getAllNhanVien();
        
        for (NhanVien nv : danhSach) {
            String gioiTinh = nv.isGioiTinh() ? "Nam" : "Nữ";
            tableModel.addRow(new Object[]{
                nv.getMaNhanVien(),
                nv.getHoTen(),
                gioiTinh,
                nv.getSoDienThoai(),
                nv.getEmail(),
                formatNgaySinh(nv.getNgaySinh()), 
                nv.getDiaChi(),
                nv.getChucVu() != null ? nv.getChucVu().getTenChucVu() : "N/A" 
            });
        }
    }

    // Hiển thị thông tin chi tiết nhân viên khi chọn hàng
    private void hienThiThongTinNhanVien(int row) {
        txtMaNV.setText(tableModel.getValueAt(row, 0).toString());
        txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
        txtHoTen.setForeground(MAU_CHU_CHUNG);
        cboGioiTinhForm.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        txtSDT.setText(tableModel.getValueAt(row, 3).toString());
        txtSDT.setForeground(MAU_CHU_CHUNG);
        txtEmail.setText(tableModel.getValueAt(row, 4).toString());
        txtEmail.setForeground(MAU_CHU_CHUNG);
        
        String ngaySinh_ddMMyyyy = tableModel.getValueAt(row, 5).toString();
        txtNgaySinh.setText(ngaySinh_ddMMyyyy);
        txtNgaySinh.setForeground(MAU_CHU_CHUNG);
        
        txtDiaChi.setText(tableModel.getValueAt(row, 6).toString());
        txtDiaChi.setForeground(MAU_CHU_CHUNG);
        
        String tenChucVuTrongBang = tableModel.getValueAt(row, 7).toString();
        for (int i = 0; i < cboChucVu.getItemCount(); i++) {
            if (cboChucVu.getItemAt(i).getTenChucVu().equals(tenChucVuTrongBang)) {
                cboChucVu.setSelectedIndex(i);
                break;
            }
        }
    }

    // Xử lý tìm kiếm nhân viên
    private void timKiemNhanVien() {
        String keyword = txtTimKiem.getText().trim();
        
        if (keyword.isEmpty() || keyword.equals("Tìm kiếm nhân viên...")) {
            loadDataToTable();
            return;
        }
        
        tableModel.setRowCount(0);
        ArrayList<NhanVien> danhSach = new ArrayList<>();
        
        
        if (keyword.toUpperCase().startsWith("NV")) {
            danhSach = nhanVienDAO.timKiemNhanVienTheoMa(keyword);
        } 
        else if (keyword.matches("\\d+")) {
            danhSach = nhanVienDAO.timKiemNhanVienTheoSDT(keyword);
        } 
        else if (!keyword.matches(".*\\d.*")) {
            danhSach = nhanVienDAO.timKiemNhanVienTheoTen(keyword);
        } 

        for (NhanVien nv : danhSach) {
            String gioiTinh = nv.isGioiTinh() ? "Nam" : "Nữ";
            tableModel.addRow(new Object[]{
                nv.getMaNhanVien(),
                nv.getHoTen(),
                gioiTinh,
                nv.getSoDienThoai(),
                nv.getEmail(),
                formatNgaySinh(nv.getNgaySinh()), 
                nv.getDiaChi(),
                nv.getChucVu() != null ? nv.getChucVu().getTenChucVu() : "N/A"
            });
        }
    }

    // Cập nhật thông tin nhân viên
    private void capNhatNhanVien() {
        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String ngaySinhText = txtNgaySinh.getText().trim();
        ChucVu chucVu = (ChucVu) cboChucVu.getSelectedItem();
        LocalDate ngaySinh; 

        if (hoTen.isEmpty() || hoTen.equals("Nhập họ và tên...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }
        if (!hoTen.matches("^[A-ZÀ-Ỹ][a-zà-ỹ]*(\\s[A-ZÀ-Ỹ][a-zà-ỹ]*)*$")) {
            JOptionPane.showMessageDialog(this, "Tên nhân viên chỉ chứa chữ cái, phải viết hoa chữ cái đầu mỗi từ. Ví dụ: Nguyễn Văn A", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }
        
        if (sdt.isEmpty() || sdt.equals("Nhập SĐT...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return;
        }
        if (!sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! (Phải có 10 số và bắt đầu bằng số 0)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return;
        }

        if (email.isEmpty() || email.equals("Nhập email...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Địa chỉ email không hợp lệ (ví dụ: example@gmail.com).", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        
        if (ngaySinhText.isEmpty() || ngaySinhText.equals("dd-MM-yyyy")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày sinh!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtNgaySinh.requestFocus();
            return;
        }
        try {
            ngaySinh = LocalDate.parse(ngaySinhText, DATE_FORMATTER); 
            if (ngaySinh.plusYears(18).isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 18 tuổi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtNgaySinh.requestFocus();
                return;
            }
        } catch (DateTimeParseException e) { 
            JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày sinh. Vui lòng nhập theo dd-MM-yyyy", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtNgaySinh.requestFocus();
            return;
        }
        
        if (diaChi.isEmpty() || diaChi.equals("Nhập địa chỉ...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtDiaChi.requestFocus();
            return;
        }

        if (chucVu == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean gioiTinh = cboGioiTinhForm.getSelectedItem().equals("Nam");
            Date ngaySinhSQL = Date.valueOf(ngaySinh); 

            NhanVien nv = new NhanVien(
                txtMaNV.getText().trim(),
                hoTen,
                gioiTinh,
                sdt,
                email,
                ngaySinhSQL,
                diaChi,
                chucVu
            );
            
            if (nhanVienDAO.capNhatNhanVien(nv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lamMoiGiaoDien();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Xóa nhân viên (chuyển trạng thái nghỉ việc)
    private void xoaNhanVien() {
        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn 'Xóa' nhân viên này?", 
            "Xác nhận xoá", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (nhanVienDAO.xoaNhanVien(txtMaNV.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Đã chuyển trạng thái nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lamMoiGiaoDien();
            } else {
                JOptionPane.showMessageDialog(this, "Chuyển trạng thái thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Thực hiện sắp xếp danh sách
    private void thucHienSapXep(int loaiSapXep) {
        String orderBy = "";
        switch (loaiSapXep) {
            case 1: orderBy = "nv.hoTen ASC"; break;
            case 2: orderBy = "nv.hoTen DESC"; break;
            case 3: orderBy = "cv.tenChucVu ASC, nv.hoTen ASC"; break;
            default:
                loadDataToTable();
                return;
        }
        
        tableModel.setRowCount(0);
        ArrayList<NhanVien> danhSach = nhanVienDAO.sapXepNhanVien(orderBy);
        
        for (NhanVien nv : danhSach) {
            String gioiTinh = nv.isGioiTinh() ? "Nam" : "Nữ";
            tableModel.addRow(new Object[]{
                nv.getMaNhanVien(),
                nv.getHoTen(),
                gioiTinh,
                nv.getSoDienThoai(),
                nv.getEmail(),
                formatNgaySinh(nv.getNgaySinh()), 
                nv.getDiaChi(),
                nv.getChucVu() != null ? nv.getChucVu().getTenChucVu() : "N/A"
            });
        }
        
        panelChinh.requestFocusInWindow();
    }

    // Thực hiện lọc danh sách theo giới tính
    private void thucHienLoc(String loaiLoc) {
        if (!loaiLoc.equals("gioitinh")) return;
        
        String selectedGender = (String) cmbGioiTinh.getSelectedItem();
        if (selectedGender.equals("Giới tính")) {
            loadDataToTable();
            return;
        }
        
        boolean gioiTinhValue = selectedGender.equals("Nam");
        
        tableModel.setRowCount(0);
        ArrayList<NhanVien> danhSach = nhanVienDAO.locTheoGioiTinh(gioiTinhValue);
        
        for (NhanVien nv : danhSach) {
            String gioiTinh = nv.isGioiTinh() ? "Nam" : "Nữ";
            tableModel.addRow(new Object[]{
                nv.getMaNhanVien(),
                nv.getHoTen(),
                gioiTinh,
                nv.getSoDienThoai(),
                nv.getEmail(),
                formatNgaySinh(nv.getNgaySinh()), 
                nv.getDiaChi(),
                nv.getChucVu() != null ? nv.getChucVu().getTenChucVu() : "N/A"
            });
        }
        
        panelChinh.requestFocusInWindow();
    }

    // Làm mới toàn bộ giao diện
    private void lamMoiGiaoDien() {
        loadDataToTable();
        
        txtTimKiem.setText("Tìm kiếm nhân viên...");
        txtTimKiem.setForeground(MAU_PLACEHOLDER);
        
        cmbSapXep.setSelectedIndex(0);
        cmbGioiTinh.setSelectedIndex(0);
        
        txtMaNV.setText("");
        txtHoTen.setText("Nhập họ và tên...");
        txtHoTen.setForeground(MAU_PLACEHOLDER);
        txtSDT.setText("Nhập SĐT...");
        txtSDT.setForeground(MAU_PLACEHOLDER);
        txtEmail.setText("Nhập email...");
        txtEmail.setForeground(MAU_PLACEHOLDER);
        
        txtNgaySinh.setText("dd-MM-yyyy");
        txtNgaySinh.setForeground(MAU_PLACEHOLDER);
        
        txtDiaChi.setText("Nhập địa chỉ...");
        txtDiaChi.setForeground(MAU_PLACEHOLDER);
        
        cboGioiTinhForm.setSelectedIndex(0);
        if (cboChucVu.getItemCount() > 0) cboChucVu.setSelectedIndex(0);
        wrapperTimKiem.setVisible(true);
        txtTimKiem.setVisible(true);
        cmbGioiTinh.setVisible(true);
        cmbSapXep.setVisible(true);
        btnKhoiPhuc.setVisible(false);
        btnCapNhat.setVisible(true);
        btnNVDaNghi.setVisible(true);

        btnXoa.setVisible(true);
        table.clearSelection();
        panelChinh.requestFocusInWindow();
    }
    
    // Tải dữ liệu chức vụ lên ComboBox form
    private void taiDuLieuChucVu() {
        try {
            List<ChucVu> dsChucVu = chucVuDAO.docDanhSachChucVu(); 
            cboChucVu.removeAllItems();
            for (ChucVu cv : dsChucVu) {
                cboChucVu.addItem(cv);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách chức vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Hiển thị dialog thêm chức vụ mới
    private void hienThiDialogThemChucVu() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Thêm chức vụ", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 350); 
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(MAU_NEN_TAB);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlHienCo = new JPanel(new BorderLayout(0, 5));
        pnlHienCo.setOpaque(false);
        JLabel lblHienCo = new JLabel("Chức vụ hiện có:");
        lblHienCo.setForeground(MAU_CHU_LABEL);
        lblHienCo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        DefaultListModel<ChucVu> listModel = new DefaultListModel<>();
        for (int i = 0; i < cboChucVu.getItemCount(); i++) {
            listModel.addElement(cboChucVu.getItemAt(i));
        }
        
        JList<ChucVu> listHienCo = new JList<>(listModel);
        listHienCo.setBackground(MAU_THANH_TIM_KIEM);
        listHienCo.setForeground(MAU_CHU_CHUNG);
        listHienCo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        listHienCo.setSelectionBackground(MAU_NUT_SUA.darker());
        listHienCo.setSelectionForeground(Color.WHITE);
        listHienCo.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        JScrollPane scrollPane = new JScrollPane(listHienCo);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
        tuyChinhScrollBar(scrollPane);
        
        pnlHienCo.add(lblHienCo, BorderLayout.NORTH);
        pnlHienCo.add(scrollPane, BorderLayout.CENTER);
        pnlHienCo.setPreferredSize(new Dimension(0, 150)); 

        JPanel pnlThemMoi = new JPanel(new BorderLayout(0, 5));
        pnlThemMoi.setOpaque(false);
        JLabel lblThemMoi = new JLabel("Tên chức vụ mới:");
        lblThemMoi.setForeground(MAU_CHU_LABEL);
        lblThemMoi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JTextField txtTenChucVuMoi = taoTextFieldTimKiem("");
        txtTenChucVuMoi.setPreferredSize(new Dimension(100, 40));

        pnlThemMoi.add(lblThemMoi, BorderLayout.NORTH);
        pnlThemMoi.add(txtTenChucVuMoi, BorderLayout.CENTER);

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlButton.setOpaque(false);
        
        JButton btnThem = new JButton("Thêm");
        styleDialogButton(btnThem, MAU_NUT_SUA);

        JButton btnHuy = new JButton("Hủy");
        styleDialogButton(btnHuy, MAU_NUT_XOA);

        pnlButton.add(btnThem);
        pnlButton.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlButton.add(btnHuy);

        mainPanel.add(pnlHienCo, BorderLayout.NORTH);
        mainPanel.add(pnlThemMoi, BorderLayout.CENTER);
        mainPanel.add(pnlButton, BorderLayout.SOUTH);

        btnHuy.addActionListener(e -> dialog.dispose());

        btnThem.addActionListener(e -> {
            String tenMoi = txtTenChucVuMoi.getText().trim();
            if (tenMoi.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tên chức vụ không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ChucVu cvTonTai = chucVuDAO.timChucVuTheoTen(tenMoi);

            if (cvTonTai != null) {
                JOptionPane.showMessageDialog(dialog, "Chức vụ này đã tồn tại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    String maMoi = chucVuDAO.sinhMaChucVuTuDong();
                    ChucVu cvMoi = new ChucVu(maMoi, tenMoi);
                    
                    boolean themThanhCong = chucVuDAO.themChucVu(cvMoi);
                    
                    if(themThanhCong) {
                        cboChucVu.addItem(cvMoi);
                        cboChucVu.setSelectedItem(cvMoi);
                        listModel.addElement(cvMoi);
                        listHienCo.ensureIndexIsVisible(listModel.getSize() - 1); 
                        
                        JOptionPane.showMessageDialog(dialog, "Đã thêm chức vụ mới!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        
                        txtTenChucVuMoi.setText("");
                        txtTenChucVuMoi.requestFocus();
                    } else {
                         JOptionPane.showMessageDialog(dialog, "Thêm chức vụ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                     JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm chức vụ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    // Định dạng nút bấm trong dialog
    private void styleDialogButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 25, 8, 25));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
    }

    // Tùy chỉnh thanh cuộn ScrollBar
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
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !verticalScrollBar.isEnabled()) return;
                g.setColor(this.thumbColor);
                g.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 4, 4);
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(this.trackColor);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
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
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                 JButton button = new JButton();
                 button.setPreferredSize(new Dimension(0, 0));
                 return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !horizontalScrollBar.isEnabled()) return;
                g.setColor(this.thumbColor);
                g.fillRoundRect(thumbBounds.x, thumbBounds.y + 2, thumbBounds.width, thumbBounds.height - 4, 4, 4);
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(this.trackColor);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
        });
    }

    // Định dạng ngày sinh hiển thị
    private String formatNgaySinh(Date ngaySinh) {
        if (ngaySinh == null) {
            return "N/A";
        }
        LocalDate localDate = ngaySinh.toLocalDate();
        return localDate.format(DATE_FORMATTER);
    }
}
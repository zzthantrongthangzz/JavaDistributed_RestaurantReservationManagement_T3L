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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.rmi.Naming;
import java.rmi.RemoteException;

import rmi_interfaces.INhanVien_DAO;
import rmi_interfaces.IChucVu_DAO;
import entity.NhanVien;
import entity.ChucVu;

public class CapNhatNhanVien_UI extends JPanel {

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

    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 70;
    private final int CHIEU_CAO_HEADER_BANG = 45;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(190, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(170, 40);
    private final Dimension KICH_THUOC_NUT_CHUC_NANG = new Dimension(115, 40);
    private final Dimension KICH_THUOC_O_NHAP = new Dimension(150, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 13);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private JComboBox<String> cmbSapXep;
    private JComboBox<String> cmbGioiTinh;
    private JPanel panelChinh;

    private JTextField txtMaNV, txtHoTen, txtSDT, txtEmail, txtDiaChi, txtNgaySinh;
    private JComboBox<String> cboGioiTinhForm;
    private JComboBox<ChucVu> cboChucVu;

    private INhanVien_DAO nhanVienDAO;
    private IChucVu_DAO chucVuDAO;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnNVDaNghi;
    private JButton btnKhoiPhuc;
    private JPanel wrapperTimKiem;

    public CapNhatNhanVien_UI() {
        try {
            nhanVienDAO = (INhanVien_DAO) Naming.lookup("rmi://localhost:1099/NhanVien_DAO");
            chucVuDAO = (IChucVu_DAO) Naming.lookup("rmi://localhost:1099/ChucVu_DAO");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }
        khoiTaoGiaoDien();
        taiDuLieuChucVu();
        loadDataToTable();
    }

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

        btnNVDaNghi = taoNutChucNang("NV đã nghỉ", MAU_NUT_DA_NGHI);
        btnNVDaNghi.addActionListener(e -> hienThiDialogNVDaNghi());

        btnKhoiPhuc = taoNutChucNang("Khôi phục", MAU_NUT_KHOI_PHUC);
        btnKhoiPhuc.addActionListener(e -> khoiPhucNhanVien());

        panelNut.add(btnCapNhat);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        panelNut.add(btnNVDaNghi);

        panel.add(panelTimKiem, BorderLayout.WEST);
        panel.add(panelNut, BorderLayout.EAST);

        return panel;
    }

    private void khoiPhucNhanVien() {
        final String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần khôi phục!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn khôi phục nhân viên " + txtHoTen.getText() + " làm việc lại?",
                "Xác nhận khôi phục",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            btnKhoiPhuc.setEnabled(false);
            btnKhoiPhuc.setText("Đang xử lý...");

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return nhanVienDAO.khoiPhucNhanVien(maNV);
                }

                @Override
                protected void done() {
                    btnKhoiPhuc.setEnabled(true);
                    btnKhoiPhuc.setText("Khôi phục");
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this, "Khôi phục nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            hienThiDialogNVDaNghi();
                        } else {
                            JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this, "Lỗi kết nối máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

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

        // Nạp dữ liệu bằng SwingWorker
        SwingWorker<List<NhanVien>, Void> loadWorker = new SwingWorker<List<NhanVien>, Void>() {
            @Override
            protected List<NhanVien> doInBackground() throws Exception {
                if (nhanVienDAO == null) return new ArrayList<>();
                return nhanVienDAO.getAllNhanVienDaNghi();
            }

            @Override
            protected void done() {
                try {
                    List<NhanVien> listDaNghi = get();
                    modelDialog.setRowCount(0);
                    for (NhanVien nv : listDaNghi) {
                        modelDialog.addRow(new Object[]{
                                nv.getMaNhanVien(), nv.getHoTen(), nv.isGioiTinh() ? "Nam" : "Nữ",
                                nv.getSoDienThoai(), nv.getEmail(), formatNgaySinh(nv.getNgaySinh()),
                                nv.getDiaChi(), nv.getChucVu() != null ? nv.getChucVu().getTenChucVu() : "N/A"
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        loadWorker.execute();

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
            final String maNV = modelDialog.getValueAt(row, 0).toString();
            final String tenNV = modelDialog.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(dialog, "Khôi phục nhân viên [" + tenNV + "]?", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                btnKhoiPhucDialog.setEnabled(false);
                btnKhoiPhucDialog.setText("Đang xử lý...");

                SwingWorker<Boolean, Void> restoreWorker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return nhanVienDAO.khoiPhucNhanVien(maNV);
                    }
                    @Override
                    protected void done() {
                        btnKhoiPhucDialog.setEnabled(true);
                        btnKhoiPhucDialog.setText("Khôi phục nhân viên");
                        try {
                            if (get()) {
                                JOptionPane.showMessageDialog(dialog, "Đã khôi phục thành công!");
                                modelDialog.removeRow(row);
                                lamMoiGiaoDien();
                            } else {
                                JOptionPane.showMessageDialog(dialog, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception re) {
                            re.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, "Lỗi kết nối máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                restoreWorker.execute();
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

        JComboBox<String> cmb = taoComboBox(items, KICH_THUOC_O_NHAP);

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(cmb, BorderLayout.CENTER);

        if (parent.getComponentCount() > 0) {
            parent.add(Box.createRigidArea(new Dimension(15, 0)));
        }
        parent.add(wrapper);

        return cmb;
    }

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

    private void loadDataToTable() {
        if (nhanVienDAO == null) return;
        SwingWorker<List<NhanVien>, Void> worker = new SwingWorker<List<NhanVien>, Void>() {
            @Override
            protected List<NhanVien> doInBackground() throws Exception {
                return nhanVienDAO.getAllNhanVien();
            }

            @Override
            protected void done() {
                try {
                    List<NhanVien> danhSach = get();
                    tableModel.setRowCount(0);
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
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Lỗi khi tải dữ liệu nhân viên lên bảng.");
                }
            }
        };
        worker.execute();
    }

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

    private void timKiemNhanVien() {
        if (nhanVienDAO == null) return;
        String keywordRaw = txtTimKiem.getText().trim();

        if (keywordRaw.isEmpty() || keywordRaw.equals("Tìm kiếm nhân viên...")) {
            loadDataToTable();
            return;
        }

        final String keyword = keywordRaw;

        SwingWorker<List<NhanVien>, Void> searchWorker = new SwingWorker<List<NhanVien>, Void>() {
            @Override
            protected List<NhanVien> doInBackground() throws Exception {
                if (keyword.toUpperCase().startsWith("NV")) {
                    return nhanVienDAO.timKiemNhanVienTheoMa(keyword);
                } else if (keyword.matches("\\d+")) {
                    return nhanVienDAO.timKiemNhanVienTheoSDT(keyword);
                } else {
                    return nhanVienDAO.timKiemNhanVienTheoTen(keyword);
                }
            }

            @Override
            protected void done() {
                try {
                    List<NhanVien> danhSach = get();
                    tableModel.setRowCount(0);
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
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this, "Lỗi khi tìm kiếm nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        searchWorker.execute();
    }

    private void capNhatNhanVien() {
        final String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần cập nhật từ danh sách!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hoTenRaw = txtHoTen.getText().trim();
        String sdtRaw = txtSDT.getText().trim();
        String emailRaw = txtEmail.getText().trim();
        String ngaySinhRaw = txtNgaySinh.getText().trim();
        String diaChiRaw = txtDiaChi.getText().trim();
        final ChucVu chucVu = (ChucVu) cboChucVu.getSelectedItem();
        final boolean gioiTinh = cboGioiTinhForm.getSelectedItem().equals("Nam");

        if (hoTenRaw.isEmpty() || hoTenRaw.equals("Nhập họ và tên...")) {
            JOptionPane.showMessageDialog(this, "Họ tên không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }
        if (!hoTenRaw.matches("^[A-ZÀ-Ỹ][a-zà-ỹ]*(\\s[A-ZÀ-Ỹ][a-zà-ỹ]*)*$")) {
            JOptionPane.showMessageDialog(this, "Họ tên không hợp lệ! (Phải viết hoa chữ cái đầu mỗi từ)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }

        if (sdtRaw.isEmpty() || sdtRaw.equals("Nhập SĐT...")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return;
        }
        if (!sdtRaw.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return;
        }

        if (emailRaw.isEmpty() || emailRaw.equals("Nhập email...")) {
            JOptionPane.showMessageDialog(this, "Email không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        if (!emailRaw.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Định dạng Email không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        if (ngaySinhRaw.isEmpty() || ngaySinhRaw.equals("dd-MM-yyyy")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày sinh!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtNgaySinh.requestFocus();
            return;
        }

        final java.sql.Date sqlNgaySinh;
        try {
            LocalDate ngaySinhLD = LocalDate.parse(ngaySinhRaw, DATE_FORMATTER);
            if (ngaySinhLD.plusYears(18).isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Nhân viên phải từ đủ 18 tuổi trở lên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtNgaySinh.requestFocus();
                return;
            }
            sqlNgaySinh = java.sql.Date.valueOf(ngaySinhLD);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày sinh không đúng (dd-MM-yyyy)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtNgaySinh.requestFocus();
            return;
        }

        if (diaChiRaw.isEmpty() || diaChiRaw.equals("Nhập địa chỉ...")) {
            JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtDiaChi.requestFocus();
            return;
        }

        if (chucVu == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ cho nhân viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String hoTen = hoTenRaw;
        final String sdt = sdtRaw;
        final String email = emailRaw;
        final String diaChi = diaChiRaw;

        final NhanVien nvCapNhat = new NhanVien(maNV, hoTen, gioiTinh, sdt, email, sqlNgaySinh, diaChi, chucVu);

        btnCapNhat.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return nhanVienDAO.capNhatNhanVien(nvCapNhat);
            }

            @Override
            protected void done() {
                btnCapNhat.setEnabled(true);
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this,
                                "Cập nhật thông tin nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        loadDataToTable();
                    } else {
                        JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this,
                                "Cập nhật thất bại. Vui lòng kiểm tra lại dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this,
                            "Mất kết nối với Server. Không thể thực hiện cập nhật!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void xoaNhanVien() {
        final String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn 'Xóa' nhân viên này?",
                "Xác nhận xoá",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            btnXoa.setEnabled(false);
            btnXoa.setText("Đang xóa...");

            SwingWorker<Boolean, Void> deleteWorker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (nhanVienDAO == null) return false;
                    return nhanVienDAO.xoaNhanVien(maNV);
                }

                @Override
                protected void done() {
                    btnXoa.setEnabled(true);
                    btnXoa.setText("Xóa");
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this, "Đã chuyển trạng thái nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            lamMoiGiaoDien();
                        } else {
                            JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this, "Chuyển trạng thái thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this, "Lỗi kết nối máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            deleteWorker.execute();
        }
    }

    private void thucHienSapXep(int loaiSapXep) {
        String orderByTemp = "";
        switch (loaiSapXep) {
            case 1: orderByTemp = "nv.hoTen ASC"; break;
            case 2: orderByTemp = "nv.hoTen DESC"; break;
            case 3: orderByTemp = "cv.tenChucVu ASC, nv.hoTen ASC"; break;
            default:
                loadDataToTable();
                return;
        }

        final String orderBy = orderByTemp;

        SwingWorker<List<NhanVien>, Void> sortWorker = new SwingWorker<List<NhanVien>, Void>() {
            @Override
            protected List<NhanVien> doInBackground() throws Exception {
                if (nhanVienDAO == null) return new ArrayList<>();
                return nhanVienDAO.sapXepNhanVien(orderBy);
            }

            @Override
            protected void done() {
                try {
                    List<NhanVien> danhSach = get();
                    tableModel.setRowCount(0);
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
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this, "Lỗi khi sắp xếp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                panelChinh.requestFocusInWindow();
            }
        };
        sortWorker.execute();
    }

    private void thucHienLoc(String loaiLoc) {
        if (!loaiLoc.equals("gioitinh")) return;

        String selectedGender = (String) cmbGioiTinh.getSelectedItem();
        if (selectedGender.equals("Giới tính")) {
            loadDataToTable();
            return;
        }

        final boolean gioiTinhValue = selectedGender.equals("Nam");

        SwingWorker<List<NhanVien>, Void> filterWorker = new SwingWorker<List<NhanVien>, Void>() {
            @Override
            protected List<NhanVien> doInBackground() throws Exception {
                if (nhanVienDAO == null) return new ArrayList<>();
                return nhanVienDAO.locTheoGioiTinh(gioiTinhValue);
            }

            @Override
            protected void done() {
                try {
                    List<NhanVien> danhSach = get();
                    tableModel.setRowCount(0);
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
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CapNhatNhanVien_UI.this, "Lỗi khi lọc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                panelChinh.requestFocusInWindow();
            }
        };
        filterWorker.execute();
    }

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

    private void taiDuLieuChucVu() {
        SwingWorker<List<ChucVu>, Void> worker = new SwingWorker<List<ChucVu>, Void>() {
            @Override
            protected List<ChucVu> doInBackground() throws Exception {
                if (chucVuDAO == null) return new ArrayList<>();
                return chucVuDAO.docDanhSachChucVu();
            }

            @Override
            protected void done() {
                try {
                    List<ChucVu> dsChucVu = get();
                    cboChucVu.removeAllItems();
                    for (ChucVu cv : dsChucVu) {
                        cboChucVu.addItem(cv);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

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

        JButton btnThemDialog = new JButton("Thêm");
        styleDialogButton(btnThemDialog, MAU_NUT_SUA);

        JButton btnHuy = new JButton("Hủy");
        styleDialogButton(btnHuy, MAU_NUT_XOA);

        pnlButton.add(btnThemDialog);
        pnlButton.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlButton.add(btnHuy);

        mainPanel.add(pnlHienCo, BorderLayout.NORTH);
        mainPanel.add(pnlThemMoi, BorderLayout.CENTER);
        mainPanel.add(pnlButton, BorderLayout.SOUTH);

        btnHuy.addActionListener(e -> dialog.dispose());

        btnThemDialog.addActionListener(e -> {
            String tenMoi = txtTenChucVuMoi.getText().trim();
            if (tenMoi.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tên chức vụ không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnThemDialog.setEnabled(false);
            btnThemDialog.setText("Đang thêm...");

            SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
                @Override
                protected Object[] doInBackground() throws Exception {
                    if (chucVuDAO == null) return new Object[]{false, "error", null};
                    ChucVu cvTonTai = chucVuDAO.timChucVuTheoTen(tenMoi);
                    if (cvTonTai != null) {
                        return new Object[]{false, "exists", null};
                    } else {
                        String maMoi = chucVuDAO.sinhMaChucVuTuDong();
                        ChucVu cvMoi = new ChucVu(maMoi, tenMoi);
                        boolean ok = chucVuDAO.themChucVu(cvMoi);
                        return new Object[]{ok, "added", cvMoi};
                    }
                }

                @Override
                protected void done() {
                    btnThemDialog.setEnabled(true);
                    btnThemDialog.setText("Thêm");
                    try {
                        Object[] res = get();
                        boolean ok = (Boolean) res[0];
                        String status = (String) res[1];
                        ChucVu cvMoi = (ChucVu) res[2];

                        if (status.equals("exists")) {
                            JOptionPane.showMessageDialog(dialog, "Chức vụ này đã tồn tại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                        } else if (ok) {
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
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm chức vụ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

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

    private String formatNgaySinh(Date ngaySinh) {
        if (ngaySinh == null) {
            return "N/A";
        }
        LocalDate localDate = ngaySinh.toLocalDate();
        return localDate.format(DATE_FORMATTER);
    }
}
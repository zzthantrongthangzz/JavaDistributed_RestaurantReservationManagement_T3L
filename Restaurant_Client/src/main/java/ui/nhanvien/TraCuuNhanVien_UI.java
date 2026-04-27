package ui.nhanvien;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.SimpleDateFormat;

import entity.NhanVien;
import rmi_interfaces.INhanVien_DAO;

public class TraCuuNhanVien_UI extends JPanel {

    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
    private final Color MAU_NUT_SUA = new Color(255, 193, 7);
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    private final Color MAU_THANH_CUON_THUMB = new Color(100, 104, 124);
    private final Color MAU_THANH_CUON_TRACK = new Color(31, 32, 44);

    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 70;
    private final int CHIEU_CAO_HEADER_BANG = 45;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(190, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(170, 40);
    private final Dimension KICH_THUOC_NUT_CHUC_NANG = new Dimension(115, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 13);

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiemMa;
    private JTextField txtTimKiemTen;
    private JTextField txtTimKiemSDT;
    private JComboBox<String> cmbSapXep;
    private JComboBox<String> cmbGioiTinh;
    private JPanel panelChinh;
    private INhanVien_DAO nhanVienDAO;

    public TraCuuNhanVien_UI() {
        try {
            nhanVienDAO = (INhanVien_DAO) Naming.lookup("rmi://localhost:1099/NhanVien_DAO");
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

        JPanel wrapperNoiDung = new JPanel(new BorderLayout(0, 15));
        wrapperNoiDung.setBackground(MAU_NEN_TAB);
        wrapperNoiDung.add(taoPanelDieuKhien(), BorderLayout.NORTH);
        wrapperNoiDung.add(taoPanelNoiDung(), BorderLayout.CENTER);

        panelChinh.add(wrapperNoiDung, BorderLayout.CENTER);
        panelChinh.requestFocusInWindow();

        add(panelChinh, BorderLayout.CENTER);
    }

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 90));
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setBackground(MAU_NEN_TAB);

        JLabel lblTieuDe = new JLabel("TRA CỨU NHÂN VIÊN");
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
        JPanel panel = new JPanel(new BorderLayout(30, 0));
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 50));

        JPanel panelTimKiem = new JPanel();
        panelTimKiem.setLayout(new BoxLayout(panelTimKiem, BoxLayout.X_AXIS));
        panelTimKiem.setBackground(MAU_NEN_TAB);

        JPanel wrapperMa = taoWrapperTimKiemCoNhan("Mã NV:", "Tìm theo mã...");
        txtTimKiemMa = (JTextField) wrapperMa.getComponent(1);

        JPanel wrapperTen = taoWrapperTimKiemCoNhan("Tên NV:", "Tìm theo tên...");
        txtTimKiemTen = (JTextField) wrapperTen.getComponent(1);

        JPanel wrapperSDT = taoWrapperTimKiemCoNhan("SĐT:", "Tìm theo SĐT...");
        txtTimKiemSDT = (JTextField) wrapperSDT.getComponent(1);

        int chuanChieuCao = 40;
        Dimension maxSize = new Dimension(450, chuanChieuCao);
        wrapperMa.setMaximumSize(maxSize);
        wrapperTen.setMaximumSize(maxSize);
        wrapperSDT.setMaximumSize(maxSize);

        cmbSapXep = taoComboBox(new String[]{"Sắp xếp", "Tên A-Z", "Tên Z-A", "Chức vụ"}, KICH_THUOC_COMBO_BOX);
        cmbSapXep.addActionListener(e -> {
            int selectedIndex = cmbSapXep.getSelectedIndex();
            if (selectedIndex == 0) {
                lamMoiGiaoDien();
            } else {
                thucHienSapXep(selectedIndex);
            }
        });

        cmbGioiTinh = taoComboBox(new String[]{"Giới tính", "Nam", "Nữ"}, KICH_THUOC_COMBO_BOX);
        cmbGioiTinh.addActionListener(e -> {
            int selectedIndex = cmbGioiTinh.getSelectedIndex();
            if (selectedIndex == 0) {
                lamMoiGiaoDien();
            } else {
                thucHienLoc("gioitinh");
            }
        });

        panelTimKiem.add(wrapperMa);
        panelTimKiem.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiem.add(wrapperTen);
        panelTimKiem.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiem.add(wrapperSDT);
        panelTimKiem.add(Box.createRigidArea(new Dimension(20, 0)));
        panelTimKiem.add(cmbSapXep);
        panelTimKiem.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiem.add(cmbGioiTinh);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 4));
        panelNut.setBackground(MAU_NEN_TAB);

        JButton btnLamMoi = taoNutChucNang("Làm mới", new Color(33, 150, 243));
        btnLamMoi.addActionListener(e -> lamMoiGiaoDien());

        panelNut.add(btnLamMoi);

        panel.add(panelTimKiem, BorderLayout.WEST);
        panel.add(panelNut, BorderLayout.EAST);

        return panel;
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
                if (iconBounds.contains(e.getPoint())) {
                    timKiemNhanVien();
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

    private void docDuLieuTuSQL() {
        try {
            List<NhanVien> danhSach = nhanVienDAO.getAllNhanVien();
            hienThiDanhSach(danhSach);
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiDanhSach(List<NhanVien> danhSach) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (NhanVien nv : danhSach) {
            String ngaySinh = sdf.format(nv.getNgaySinh());
            String gioiTinh = nv.isGioiTinh() ? "Nam" : "Nữ";
            tableModel.addRow(new Object[]{
                    nv.getMaNhanVien(),
                    nv.getHoTen(),
                    gioiTinh,
                    nv.getSoDienThoai(),
                    nv.getEmail(),
                    ngaySinh,
                    nv.getDiaChi(),
                    nv.getChucVu()
            });
        }
    }

    private void thucHienSapXep(int loaiSapXep) {
        List<NhanVien> ketQuaSapXep = null;
        try {
            switch (loaiSapXep) {
                case 1:
                    ketQuaSapXep = nhanVienDAO.sapXepNhanVien("hoTen ASC");
                    break;
                case 2:
                    ketQuaSapXep = nhanVienDAO.sapXepNhanVien("hoTen DESC");
                    break;
                case 3:
                    ketQuaSapXep = nhanVienDAO.sapXepNhanVien("chucVu ASC, hoTen ASC");
                    break;
                default:
                    ketQuaSapXep = nhanVienDAO.getAllNhanVien();
                    break;
            }
            if (ketQuaSapXep != null) {
                hienThiDanhSach(ketQuaSapXep);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        panelChinh.requestFocusInWindow();
    }

    private void thucHienLoc(String loaiLoc) {
        List<NhanVien> ketQuaLoc = null;

        try {
            if (loaiLoc.equals("gioitinh")) {
                String gioiTinhDuocChon = (String) cmbGioiTinh.getSelectedItem();
                if (gioiTinhDuocChon.equals("Giới tính")) {
                    ketQuaLoc = nhanVienDAO.getAllNhanVien();
                } else if (gioiTinhDuocChon.equals("Nam")) {
                    ketQuaLoc = nhanVienDAO.locTheoGioiTinh(true);
                } else {
                    ketQuaLoc = nhanVienDAO.locTheoGioiTinh(false);
                }
            }
            else {
                ketQuaLoc = nhanVienDAO.getAllNhanVien();
            }
            if (ketQuaLoc != null) {
                hienThiDanhSach(ketQuaLoc);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        panelChinh.requestFocusInWindow();
    }

    private void lamMoiGiaoDien() {
        docDuLieuTuSQL();

        txtTimKiemMa.setText("Tìm theo mã...");
        txtTimKiemMa.setForeground(MAU_PLACEHOLDER);
        txtTimKiemTen.setText("Tìm theo tên...");
        txtTimKiemTen.setForeground(MAU_PLACEHOLDER);
        txtTimKiemSDT.setText("Tìm theo SĐT...");
        txtTimKiemSDT.setForeground(MAU_PLACEHOLDER);

        cmbSapXep.setSelectedIndex(0);
        cmbGioiTinh.setSelectedIndex(0);
        panelChinh.requestFocusInWindow();
    }

    public void lamMoiBang() {
        docDuLieuTuSQL();
    }

    private void timKiemNhanVien() {
        String tuKhoaMa = txtTimKiemMa.getText().trim();
        String tuKhoaTen = txtTimKiemTen.getText().trim();
        String tuKhoaSDT = txtTimKiemSDT.getText().trim();

        if (tuKhoaMa.equals("Tìm theo mã...")) tuKhoaMa = "";
        if (tuKhoaTen.equals("Tìm theo tên...")) tuKhoaTen = "";
        if (tuKhoaSDT.equals("Tìm theo SĐT...")) tuKhoaSDT = "";

        if (tuKhoaMa.isEmpty() && tuKhoaTen.isEmpty() && tuKhoaSDT.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<NhanVien> ketQua = null;

        try {
            if (!tuKhoaMa.isEmpty()) {
                ketQua = nhanVienDAO.timKiemNhanVienTheoMa(tuKhoaMa);
            }

            if (!tuKhoaTen.isEmpty()) {
                List<NhanVien> listTheoTen = nhanVienDAO.timKiemNhanVienTheoTen(tuKhoaTen);

                if (ketQua == null) {
                    ketQua = listTheoTen;
                } else {
                    ketQua.retainAll(listTheoTen);
                }
            }

            if (!tuKhoaSDT.isEmpty()) {
                List<NhanVien> listTheoSDT = nhanVienDAO.timKiemNhanVienTheoSDT(tuKhoaSDT);

                if (ketQua == null) {
                    ketQua = listTheoSDT;
                } else {
                    ketQua.retainAll(listTheoSDT);
                }
            }

            if (ketQua == null || ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên nào!", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                tableModel.setRowCount(0);
            } else {
                hienThiDanhSach(ketQua);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Nhân viên");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1550, 950);
            frame.setLocationRelativeTo(null);
            frame.add(new TraCuuNhanVien_UI());
            frame.setVisible(true);
        });
    }
}
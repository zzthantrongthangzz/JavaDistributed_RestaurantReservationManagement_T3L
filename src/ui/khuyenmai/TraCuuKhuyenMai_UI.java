package ui.khuyenmai;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
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
import java.text.SimpleDateFormat;
import connect.DBConnect;
import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import ui.TrangChu_UI;

public class TraCuuKhuyenMai_UI extends JPanel {
    
	private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
    private final Color MAU_CHU_LABEL = new Color(220, 220, 220);
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    private final Color MAU_THANH_CUON_THUMB = new Color(100, 104, 124);
    private final Color MAU_THANH_CUON_TRACK = new Color(31, 32, 44);

    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 70;
    private final int CHIEU_CAO_HEADER_BANG = 45;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(200, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(165, 40);
    private final Dimension KICH_THUOC_NUT_CHUC_NANG = new Dimension(115, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 13);

    private final String PLACEHOLDER_MA = "Tìm theo mã...";
    private final String PLACEHOLDER_TEN = "Tìm theo tên...";

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiemMa, txtTimKiemTen;
    private JComboBox<String> cmbTimKiemLoai; 
    private JDateChooser dateLocBatDau, dateLocKetThuc;
    private JComboBox<String> cmbSapXep, cmbLocTheoGiaTri;
    private JButton btnLamMoi;
    private JPanel panelChinh;
    private KhuyenMai_DAO kmDAO;

    // Khởi tạo giao diện và kết nối database
    public TraCuuKhuyenMai_UI() {
        try {
            kmDAO = new KhuyenMai_DAO(DBConnect.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        khoiTaoGiaoDien();
        docDuLieuTuSQL();
    }

    // Thiết lập bố cục chính
    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB);

        panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(20, 25, 20, 25));
        panelChinh.setFocusable(true);
        panelChinh.add(taoPanelDieuKhien(), BorderLayout.NORTH);
        panelChinh.add(taoPanelNoiDung(), BorderLayout.CENTER);
        
        add(panelChinh, BorderLayout.CENTER);
        panelChinh.requestFocusInWindow();
    }

    // Tạo panel điều khiển chứa bộ lọc và nút
    private JPanel taoPanelDieuKhien() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 150));

        JPanel containerTimKiemVaLoc = new JPanel();
        containerTimKiemVaLoc.setLayout(new BoxLayout(containerTimKiemVaLoc, BoxLayout.Y_AXIS));
        containerTimKiemVaLoc.setBackground(MAU_NEN_TAB);

        JPanel panelTimKiem = new JPanel();
        panelTimKiem.setLayout(new BoxLayout(panelTimKiem, BoxLayout.X_AXIS));
        panelTimKiem.setBackground(MAU_NEN_TAB);
        
        JPanel wrapperMa = taoWrapperTimKiemCoNhan("Mã KM:", PLACEHOLDER_MA);
        txtTimKiemMa = (JTextField) wrapperMa.getComponent(1);
        
        JPanel wrapperTen = taoWrapperTimKiemCoNhan("Tên KM:", PLACEHOLDER_TEN);
        txtTimKiemTen = (JTextField) wrapperTen.getComponent(1);
        
        JPanel panelLoaiKM = new JPanel(new BorderLayout(5, 0));
        panelLoaiKM.setBackground(MAU_NEN_TAB);
        JLabel lblLoaiKM = new JLabel("Loại KM:");
        lblLoaiKM.setFont(FONT_NHAN);
        lblLoaiKM.setForeground(MAU_CHU_LABEL);
        lblLoaiKM.setBackground(new Color(124, 124, 124)); 
        lblLoaiKM.setOpaque(true);
        lblLoaiKM.setHorizontalAlignment(SwingConstants.CENTER);
        lblLoaiKM.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        cmbTimKiemLoai = taoComboBox(new String[]{"Tất cả loại", "Giảm %", "Giảm tiền"}, KICH_THUOC_THANH_TIM_KIEM);
        cmbTimKiemLoai.addActionListener(e -> apDungTatCaBoLoc()); 
        
        panelLoaiKM.add(lblLoaiKM, BorderLayout.WEST);
        panelLoaiKM.add(cmbTimKiemLoai, BorderLayout.CENTER);
        
        int chuanChieuCao = 40;
        Dimension maxSize = new Dimension(450, chuanChieuCao);
        wrapperMa.setMaximumSize(maxSize);
        wrapperTen.setMaximumSize(maxSize);
        panelLoaiKM.setMaximumSize(maxSize);

        panelTimKiem.add(wrapperMa);
        panelTimKiem.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiem.add(wrapperTen);
        panelTimKiem.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiem.add(panelLoaiKM);
        panelTimKiem.add(Box.createHorizontalGlue());

        JPanel panelLoc = new JPanel();
        panelLoc.setLayout(new BoxLayout(panelLoc, BoxLayout.X_AXIS));
        panelLoc.setBackground(MAU_NEN_TAB);
        
        cmbLocTheoGiaTri = taoComboBox(new String[]{"Lọc theo giá trị", "Dưới 50K", "50K-100K", "100K-200K", "Trên 200K"}, 
            new Dimension(165, 40));
        cmbLocTheoGiaTri.addActionListener(e -> apDungTatCaBoLoc());
        
        cmbSapXep = taoComboBox(new String[]{"Sắp xếp", "Tên A-Z", "Tên Z-A", "Giá trị cao-thấp", "Giá trị thấp-cao"}, KICH_THUOC_COMBO_BOX);
        cmbSapXep.addActionListener(e -> apDungTatCaBoLoc());
        
        JPanel panelNgayBatDau = new JPanel(new BorderLayout(5, 0));
        panelNgayBatDau.setBackground(MAU_NEN_TAB);
        JLabel lblNgayBD = new JLabel("Từ ngày:");
        lblNgayBD.setFont(FONT_NHAN);
        lblNgayBD.setForeground(MAU_CHU_LABEL);
        dateLocBatDau = taoDateChooser();
        dateLocBatDau.addPropertyChangeListener("date", e -> apDungTatCaBoLoc());
        panelNgayBatDau.add(lblNgayBD, BorderLayout.WEST);
        panelNgayBatDau.add(dateLocBatDau, BorderLayout.CENTER);
        panelNgayBatDau.setMaximumSize(new Dimension(200, 40));
        
        JPanel panelNgayKetThuc = new JPanel(new BorderLayout(5, 0));
        panelNgayKetThuc.setBackground(MAU_NEN_TAB);
        JLabel lblNgayKT = new JLabel("Đến ngày:");
        lblNgayKT.setFont(FONT_NHAN);
        lblNgayKT.setForeground(MAU_CHU_LABEL);
        dateLocKetThuc = taoDateChooser();
        dateLocKetThuc.addPropertyChangeListener("date", e -> apDungTatCaBoLoc());
        panelNgayKetThuc.add(lblNgayKT, BorderLayout.WEST);
        panelNgayKetThuc.add(dateLocKetThuc, BorderLayout.CENTER);
        panelNgayKetThuc.setMaximumSize(new Dimension(200, 40));
        
        panelLoc.add(cmbLocTheoGiaTri);
        panelLoc.add(Box.createRigidArea(new Dimension(10, 0)));
        panelLoc.add(cmbSapXep);
        panelLoc.add(Box.createRigidArea(new Dimension(10, 0)));
        panelLoc.add(panelNgayBatDau);
        panelLoc.add(Box.createRigidArea(new Dimension(10, 0)));
        panelLoc.add(panelNgayKetThuc);
        panelLoc.add(Box.createHorizontalGlue());

        containerTimKiemVaLoc.add(panelTimKiem);
        containerTimKiemVaLoc.add(Box.createRigidArea(new Dimension(0, 10)));
        containerTimKiemVaLoc.add(panelLoc);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelNut.setBackground(MAU_NEN_TAB);
                
        btnLamMoi = taoNutChucNang("Làm mới", new Color(33, 150, 243));
        btnLamMoi.addActionListener(e -> lamMoiGiaoDien());
        
        panelNut.add(Box.createVerticalStrut(50));
        panelNut.add(btnLamMoi);
        
        JLabel lblTieuDe = new JLabel("TRA CỨU KHUYẾN MÃI", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 35));
        lblTieuDe.setForeground(Color.WHITE);
        panel.add(lblTieuDe, BorderLayout.NORTH);
        
        panel.add(containerTimKiemVaLoc, BorderLayout.WEST);
        panel.add(panelNut, BorderLayout.EAST);

        return panel;
    }

    // Tạo wrapper tìm kiếm có nhãn
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

    // Tạo text field tìm kiếm
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
                } catch (Exception e) {
                    System.err.println("Lỗi tải icon: " + e.getMessage());
                }
            }
        };

        txt.setText(holder);
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

        txt.addActionListener(e -> apDungTatCaBoLoc());

        txt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int iconX = txt.getWidth() - KICH_THUOC_ICON - 10;
                Rectangle iconBounds = new Rectangle(iconX, 0, KICH_THUOC_ICON + 10, txt.getHeight());
                if (iconBounds.contains(e.getPoint())) {
                    apDungTatCaBoLoc();
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

    // Tạo combo box
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

    // Tạo panel nội dung
    private JPanel taoPanelNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(MAU_NEN_TAB);
        panel.add(taoPanelBang(), BorderLayout.CENTER);

        return panel;
    }

    // Tạo panel chứa bảng dữ liệu
    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);

        String[] columnNames = {"Mã khuyến mãi", "Tên khuyến mãi", "Loại khuyến mãi", "Giá trị giảm", "Ngày bắt đầu", "Ngày kết thúc"};
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

        int[] widths = {120, 180, 130, 100, 120, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tuyChinhScrollBar(scrollPane);
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
            List<KhuyenMai> danhSach = kmDAO.getAllList();
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (KhuyenMai km : danhSach) {
        	String ngayBatDau = sdf.format(km.getNgayBatDau());
            String ngayKetThuc = sdf.format(km.getNgayKetThuc());
            tableModel.addRow(new Object[]{
                km.getMaKhuyenMai(),
                km.getTenKhuyenMai(),
                km.getLoaiKhuyenMai(),
                km.getGiaTriGiam(),
                ngayBatDau,
                ngayKetThuc
            });
        }
    }
    
    // Áp dụng các bộ lọc tìm kiếm
    private void apDungTatCaBoLoc() {
        String tuKhoaMa = txtTimKiemMa.getText().trim();
        String tuKhoaTen = txtTimKiemTen.getText().trim();

        if (tuKhoaMa.equals(PLACEHOLDER_MA)) tuKhoaMa = "";
        if (tuKhoaTen.equals(PLACEHOLDER_TEN)) tuKhoaTen = "";

        String loaiKM = (String) cmbTimKiemLoai.getSelectedItem();
        if (loaiKM.equals("Tất cả loại")) loaiKM = "";

        String giaTriFilter = (String) cmbLocTheoGiaTri.getSelectedItem();
        if (giaTriFilter.equals("Lọc theo giá trị")) giaTriFilter = "";

        String sapXep = (String) cmbSapXep.getSelectedItem();
        if (sapXep.equals("Sắp xếp")) sapXep = "";

        Date tuNgay = dateLocBatDau.getDate();
        Date denNgay = dateLocKetThuc.getDate();

        List<KhuyenMai> ketQua = kmDAO.locDanhSach(
            tuKhoaMa, tuKhoaTen, loaiKM, giaTriFilter, tuNgay, denNgay, sapXep
        );

        hienThiDanhSach(ketQua);

        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy khuyến mãi phù hợp.", 
                "Kết quả tìm kiếm", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        panelChinh.requestFocusInWindow();
    }

    // Tạo date chooser
    private JDateChooser taoDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(140, 40));
        dateChooser.setBackground(MAU_THANH_TIM_KIEM);
        dateChooser.setForeground(MAU_CHU_CHUNG);
        
        dateChooser.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
        
        JButton calendarButton = dateChooser.getCalendarButton();
        calendarButton.setBackground(MAU_THANH_TIM_KIEM);
        calendarButton.setForeground(MAU_CHU_CHUNG);
        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calendarButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Padding nhẹ cho icon
               
        JTextField dateEditor = (JTextField) dateChooser.getDateEditor().getUiComponent();

        dateEditor.setBorder(new EmptyBorder(0, 10, 0, 0));
        
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

    // Làm mới toàn bộ giao diện
    private void lamMoiGiaoDien() {
        txtTimKiemMa.setText(PLACEHOLDER_MA);
        txtTimKiemMa.setForeground(MAU_PLACEHOLDER);
        txtTimKiemTen.setText(PLACEHOLDER_TEN);
        txtTimKiemTen.setForeground(MAU_PLACEHOLDER);
        
        cmbTimKiemLoai.setSelectedIndex(0);
        
        dateLocBatDau.setDate(null);
        dateLocKetThuc.setDate(null);
        
        cmbSapXep.setSelectedIndex(0);
        cmbLocTheoGiaTri.setSelectedIndex(0);
        
        docDuLieuTuSQL();
        
        panelChinh.requestFocusInWindow();
    }
    
    // Tùy chỉnh thanh cuộn
    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setBackground(MAU_NEN_INPUT);
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120);
                this.trackColor = MAU_NEN_INPUT;
                this.thumbDarkShadowColor = new Color(80, 85, 100);
                this.thumbHighlightColor = new Color(120, 125, 140);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !verticalScrollBar.isEnabled()) {
                    return;
                }
                g.setColor(new Color(100, 105, 120));
                g.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 4, 4);
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(MAU_NEN_INPUT);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
        });

        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setPreferredSize(new Dimension(0, 8));
        horizontalScrollBar.setBackground(MAU_NEN_INPUT);
        horizontalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120);
                this.trackColor = MAU_NEN_INPUT;
                this.thumbDarkShadowColor = new Color(80, 85, 100);
                this.thumbHighlightColor = new Color(120, 125, 140);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !horizontalScrollBar.isEnabled()) {
                    return;
                }
                g.setColor(new Color(100, 105, 120));
                g.fillRoundRect(thumbBounds.x, thumbBounds.y + 2, thumbBounds.width, thumbBounds.height - 4, 4, 4);
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(MAU_NEN_INPUT);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Khuyến mãi");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1550, 950);
            frame.setLocationRelativeTo(null);
            frame.add(new TraCuuKhuyenMai_UI());
            frame.setVisible(true);
        });
    }
}
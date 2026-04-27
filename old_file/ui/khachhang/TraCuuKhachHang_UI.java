package ui.khachhang;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

import entity.KhachHang;
import dao_impl.KhachHang_DAO;

public class TraCuuKhachHang_UI extends JPanel {

    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    
    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 70; 
    private final int CHIEU_CAO_HEADER_BANG = 45;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(190, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(170, 40);
    private final Dimension KICH_THUOC_NUT_CHUC_NANG = new Dimension(115, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 16); 
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 13);

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiemMa;
    private JTextField txtTimKiemTen;
    private JTextField txtTimKiemSDT;
    private JComboBox<String> cmbSapXep;
    private JComboBox<String> cmbGioiTinh;
    private JPanel panelChinh;
    private KhachHang_DAO khachHangDAO;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Constructor khởi tạo giao diện và load dữ liệu
    public TraCuuKhachHang_UI() {
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
    
    // Tạo panel tiêu đề
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 90));
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setBackground(MAU_NEN_TAB);
        
        JLabel lblTieuDe = new JLabel("TRA CỨU KHÁCH HÀNG");
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

    // Tạo panel điều khiển chứa các ô tìm kiếm và lọc
    private JPanel taoPanelDieuKhien() {
        JPanel panel = new JPanel(new BorderLayout(30, 0));
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 50));

        JPanel panelTimKiem = new JPanel();
        panelTimKiem.setLayout(new BoxLayout(panelTimKiem, BoxLayout.X_AXIS));
        panelTimKiem.setBackground(MAU_NEN_TAB);

        JPanel wrapperMa = taoWrapperTimKiemCoNhan("Mã KH:", "Tìm theo mã...");
        txtTimKiemMa = (JTextField) wrapperMa.getComponent(1);
        
        JPanel wrapperTen = taoWrapperTimKiemCoNhan("Tên KH:", "Tìm theo tên...");
        txtTimKiemTen = (JTextField) wrapperTen.getComponent(1);
        
        JPanel wrapperSDT = taoWrapperTimKiemCoNhan("SĐT:", "Tìm theo SĐT...");
        txtTimKiemSDT = (JTextField) wrapperSDT.getComponent(1);
        
        int chuanChieuCao = 40;
        Dimension maxSize = new Dimension(450, chuanChieuCao);
        wrapperMa.setMaximumSize(maxSize);
        wrapperTen.setMaximumSize(maxSize);
        wrapperSDT.setMaximumSize(maxSize);
        
        cmbSapXep = taoComboBox(new String[]{"Sắp xếp", "Tên A-Z", "Tên Z-A", "Điểm cao-thấp", "Điểm thấp-cao"}, KICH_THUOC_COMBO_BOX);
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
    
    // Tạo wrapper cho ô tìm kiếm có nhãn tiêu đề
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
    
    // Tạo ô nhập liệu tìm kiếm với icon và sự kiện focus
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
                }
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

    // Tạo combobox tùy chỉnh giao diện
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

    // Tạo nút chức năng với màu sắc tùy chỉnh
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

    // Tạo panel chứa nội dung bảng
    private JPanel taoPanelNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(MAU_NEN_TAB);
        panel.add(taoPanelBang(), BorderLayout.CENTER);
        return panel;
    }

    // Thiết lập và cấu hình bảng hiển thị dữ liệu
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
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        table.getTableHeader().setReorderingAllowed(false);

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

        int[] widths = {80, 150, 100, 70, 150, 200, 100, 80}; 
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        tuyChinhScrollBar(scrollPane);
        
        scrollPane.getViewport().setBackground(MAU_NEN_ITEM); 
        scrollPane.setBackground(MAU_NEN_ITEM);
        
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Đọc danh sách khách hàng từ Database
    private void docDuLieuTuSQL() {
        try {
            List<KhachHang> danhSach = khachHangDAO.docDanhSachKhachHang();
            hienThiDanhSach(danhSach);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi đọc dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Hiển thị danh sách khách hàng lên bảng
    private void hienThiDanhSach(List<KhachHang> danhSach) {
        tableModel.setRowCount(0);
        for (KhachHang kh : danhSach) {
            
            String email = (kh.getEmail() != null) ? kh.getEmail() : "";
            String diaChi = (kh.getDiaChi() != null) ? kh.getDiaChi() : "";
            
            String ngaySinhStr = "";
            if (kh.getNgaySinh() != null) {
                ngaySinhStr = dateFormat.format(kh.getNgaySinh());
            }

            tableModel.addRow(new Object[]{
                kh.getMaKhachHang(),
                kh.getHoTen(),
                kh.getSoDienThoai(),
                kh.getGioiTinhString(),
                email,       
                diaChi,      
                ngaySinhStr, 
                kh.getTichDiem(),
            });
        }
    }

    // Thực hiện sắp xếp danh sách theo tiêu chí
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

    // Thực hiện lọc danh sách theo giới tính
    private void thucHienLoc(String loaiLoc) {
        List<KhachHang> ketQuaLoc;

        if (loaiLoc.equals("gioitinh")) {
            String gioiTinhDuocChon = (String) cmbGioiTinh.getSelectedItem();
            if (gioiTinhDuocChon.equals("Nam")) {
                ketQuaLoc = khachHangDAO.locKhachHangTheoGioiTinh(true);
            } else {
                ketQuaLoc = khachHangDAO.locKhachHangTheoGioiTinh(false);
            }
        }
        else {
            ketQuaLoc = khachHangDAO.docDanhSachKhachHang();
        }

        hienThiDanhSach(ketQuaLoc);
        panelChinh.requestFocusInWindow();
    }

    // Reset giao diện và tải lại dữ liệu
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
    
    // Refresh bảng dữ liệu
    public void lamMoiBang() {
        docDuLieuTuSQL();
    }
    
    // Logic tìm kiếm khách hàng
    private void timKiemKhachHang() {
        String tuKhoaMa = txtTimKiemMa.getText().trim();
        String tuKhoaTen = txtTimKiemTen.getText().trim();
        String tuKhoaSDT = txtTimKiemSDT.getText().trim();
        
        if (tuKhoaMa.equals("Tìm theo mã...")) tuKhoaMa = "";
        if (tuKhoaTen.equals("Tìm theo tên...")) tuKhoaTen = "";
        if (tuKhoaSDT.equals("Tìm theo SĐT...")) tuKhoaSDT = "";
        
        if (tuKhoaMa.isEmpty() && tuKhoaTen.isEmpty() && tuKhoaSDT.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập ít nhất một từ khóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<KhachHang> ketQua = khachHangDAO.docDanhSachKhachHang();
        
        if (!tuKhoaMa.isEmpty()) {
            List<KhachHang> ketQuaMa = khachHangDAO.timKiemTheoMa(tuKhoaMa);
            ketQua.retainAll(ketQuaMa); 
        }
        
        if (!tuKhoaTen.isEmpty()) {
            List<KhachHang> ketQuaTen = khachHangDAO.timKiemTheoTen(tuKhoaTen);
            ketQua.retainAll(ketQuaTen);
        }
        
        if (!tuKhoaSDT.isEmpty()) {
            List<KhachHang> ketQuaSDT = khachHangDAO.timKiemTheoSDT(tuKhoaSDT);
            ketQua.retainAll(ketQuaSDT);
        }
        
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy khách hàng phù hợp!", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
        } else {
            hienThiDanhSach(ketQua);
        }
        
        panelChinh.requestFocusInWindow();
    }
    
    // Tùy chỉnh thanh cuộn
    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setBackground(MAU_NEN_INPUT);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120);
                this.trackColor = MAU_NEN_INPUT;
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty()) return;
                g.setColor(thumbColor);
                g.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 4, 4);
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(trackColor);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
        });

        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setPreferredSize(new Dimension(0, 8));
        horizontalScrollBar.setBackground(MAU_NEN_INPUT);
        horizontalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120);
                this.trackColor = MAU_NEN_INPUT;
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty()) return;
                g.setColor(thumbColor);
                g.fillRoundRect(thumbBounds.x, thumbBounds.y + 2, thumbBounds.width, thumbBounds.height - 4, 4, 4);
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(trackColor);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
        });
    }
}
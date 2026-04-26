package ui.banan;

import dao_impl.LichSuHuyDatBan_DAO;
import entity.LichSuHuyDatBan;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LichSuHuyDatBan_UI extends JPanel {

    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_XANH_DUONG = new Color(30, 144, 255);
    private final Color MAU_NEN_BANG = new Color(31, 32, 44);
    private final Color MAU_KE_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);

    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 17);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);

    private JTable tblLichSu;
    private DefaultTableModel modelLichSu;
    private LichSuHuyDatBan_DAO logDAO;

    private JTextField txtTimKiemTen;
    private JTextField txtTimKiemMa;
    private JDateChooser dateChooserNgayHuy;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Khởi tạo giao diện lịch sử hủy đặt bàn
    public LichSuHuyDatBan_UI() {
        logDAO = new LichSuHuyDatBan_DAO();

        setLayout(new BorderLayout(0, 15));
        setBackground(MAU_NEN_TAB);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel pnlTop = new JPanel(new BorderLayout(0, 20));
        pnlTop.setBackground(MAU_NEN_TAB);

        JLabel lblTitle = new JLabel("LỊCH SỬ HỦY ĐẶT BÀN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(MAU_CHU_CHUNG);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlDieuKhien = new JPanel(new BorderLayout());
        pnlDieuKhien.setBackground(MAU_NEN_TAB);
        pnlDieuKhien.setPreferredSize(new Dimension(0, 50));

        JPanel pnlTimKiemWrapper = new JPanel();
        pnlTimKiemWrapper.setLayout(new BoxLayout(pnlTimKiemWrapper, BoxLayout.X_AXIS));
        pnlTimKiemWrapper.setBackground(MAU_NEN_TAB);

        JPanel pnlSearchMa = taoCumTimKiem("Mã phiếu:", "Nhập mã...", 1);
        JPanel pnlSearchTen = taoCumTimKiem("Khách hàng/SĐT:", "Nhập tên, sdt...", 2);
        JPanel pnlSearchDate = taoCumTimKiemNgay("Ngày hủy:");

        pnlTimKiemWrapper.add(pnlSearchMa);
        pnlTimKiemWrapper.add(Box.createRigidArea(new Dimension(20, 0)));
        pnlTimKiemWrapper.add(pnlSearchTen);
        pnlTimKiemWrapper.add(Box.createRigidArea(new Dimension(20, 0)));
        pnlTimKiemWrapper.add(pnlSearchDate);

        JPanel pnlNutBam = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        pnlNutBam.setBackground(MAU_NEN_TAB);

        JButton btnLamMoi = taoNut("Làm mới danh sách", MAU_XANH_DUONG);
        btnLamMoi.setPreferredSize(new Dimension(180, 40));

        btnLamMoi.addActionListener(e -> {
            txtTimKiemMa.setText("Nhập mã...");
            txtTimKiemMa.setForeground(new Color(150, 150, 160));

            txtTimKiemTen.setText("Nhập tên, sdt...");
            txtTimKiemTen.setForeground(new Color(150, 150, 160));

            if (dateChooserNgayHuy != null) {
                dateChooserNgayHuy.setDate(null);
            }

            if (rowSorter != null) {
                rowSorter.setRowFilter(null);
            }

            taiDuLieuLenBang();
        });
        pnlNutBam.add(btnLamMoi);

        pnlDieuKhien.add(pnlTimKiemWrapper, BorderLayout.WEST);
        pnlDieuKhien.add(pnlNutBam, BorderLayout.EAST);

        pnlTop.add(pnlDieuKhien, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);

        String[] cols = {
                "STT", "Mã Phiếu", "Bàn Hủy", "Khách Hàng", "SĐT Khách",
                "Nhân Viên Hủy", "Thời Gian Hủy", "Lý Do Hủy"
        };
        modelLichSu = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblLichSu = new JTable(modelLichSu);

        rowSorter = new TableRowSorter<>(modelLichSu);
        tblLichSu.setRowSorter(rowSorter);

        tuyChinhBang(tblLichSu);

        JScrollPane scrollPane = new JScrollPane(tblLichSu);
        tuyChinhScrollBar(scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_KE_BANG, 1));

        scrollPane.getViewport().setBackground(MAU_NEN_TAB);

        add(scrollPane, BorderLayout.CENTER);

        taiDuLieuLenBang();
    }

    // Tải dữ liệu lịch sử từ CSDL lên bảng
    private void taiDuLieuLenBang() {
        modelLichSu.setRowCount(0);
        List<LichSuHuyDatBan> list = logDAO.layTatCaLichSu();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        int stt = 1;
        for (LichSuHuyDatBan log : list) {
            modelLichSu.addRow(new Object[] {
                    stt++,
                    log.getMaPhieuDatBan(),
                    log.getTenBan(),
                    log.getTenKhachHang() == null ? "Vãng lai" : log.getTenKhachHang(),
                    log.getSdtKhachHang() == null ? "" : log.getSdtKhachHang(),
                    log.getTenNhanVien(),
                    sdf.format(log.getThoiGianHuy()),
                    log.getLyDoHuy()
            });
        }
    }

    // Lọc dữ liệu bảng theo các tiêu chí nhập vào
    private void thucHienTimKiem() {
        String textMa = txtTimKiemMa.getText().trim();
        String textTen = txtTimKiemTen.getText().trim();
        Date dateSel = dateChooserNgayHuy.getDate();

        if (textMa.equals("Nhập mã..."))
            textMa = "";
        if (textTen.equals("Nhập tên, sdt..."))
            textTen = "";

        List<RowFilter<DefaultTableModel, Object>> filters = new java.util.ArrayList<>();

        if (!textMa.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + textMa, 1));
        }

        if (!textTen.isEmpty()) {
            RowFilter<DefaultTableModel, Object> tenFilter = RowFilter.regexFilter("(?i)" + textTen, 3);
            RowFilter<DefaultTableModel, Object> sdtFilter = RowFilter.regexFilter("(?i)" + textTen, 4);
            filters.add(RowFilter.orFilter(java.util.Arrays.asList(tenFilter, sdtFilter)));
        }

        if (dateSel != null) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = sdfDate.format(dateSel);
            filters.add(RowFilter.regexFilter("^" + dateString, 6));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    // Tạo Cụm Tìm Kiếm NGÀY
    private JPanel taoCumTimKiemNgay(String labelText) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(new Color(124, 124, 124));
        lbl.setOpaque(true);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        this.dateChooserNgayHuy = taoDateChooser();

        wrapper.add(lbl, BorderLayout.WEST);
        wrapper.add(dateChooserNgayHuy, BorderLayout.CENTER);

        wrapper.setMaximumSize(new Dimension(400, 40));
        wrapper.setPreferredSize(new Dimension(300, 40));

        return wrapper;
    }

    // Hàm tạo JDateChooser
    private JDateChooser taoDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(140, 40));
        dateChooser.setDateFormatString("dd/MM/yyyy");
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

        dateChooser.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(() -> {
                    dateEditor.setForeground(Color.WHITE);
                    thucHienTimKiem();
                });
            }
        });

        dateEditor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                dateEditor.setForeground(Color.WHITE);
            }

            @Override
            public void focusLost(FocusEvent e) {
                dateEditor.setForeground(Color.WHITE);
            }
        });

        return dateChooser;
    }

    // Tạo thành phần giao diện tìm kiếm (Label + TextField)
    private JPanel taoCumTimKiem(String labelText, String placeholder, int loai) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAU_NEN_TAB);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(new Color(124, 124, 124));
        lbl.setOpaque(true);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JTextField txtField = taoThanhTimKiem(placeholder);

        if (loai == 1)
            this.txtTimKiemMa = txtField;
        else
            this.txtTimKiemTen = txtField;

        wrapper.add(lbl, BorderLayout.WEST);
        wrapper.add(txtField, BorderLayout.CENTER);

        wrapper.setMaximumSize(new Dimension(400, 40));
        wrapper.setPreferredSize(new Dimension(300, 40));

        return wrapper;
    }

    // Tạo TextField tìm kiếm với icon kính lúp
    private JTextField taoThanhTimKiem(String placeholder) {
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/IMG/search.png"));
                    Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    Icon searchIcon = new ImageIcon(img);
                    int y = (getHeight() - searchIcon.getIconHeight()) / 2;
                    int x = getWidth() - searchIcon.getIconWidth() - 10;
                    searchIcon.paintIcon(this, g, x, y);
                } catch (Exception e) {
                }
            }
        };

        textField.setText(placeholder);
        textField.setForeground(new Color(150, 150, 160));
        textField.setBackground(MAU_THANH_TIM_KIEM);
        textField.setCaretColor(MAU_CHU_CHUNG);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 72, 87), 1),
                new EmptyBorder(8, 10, 8, 35)));
        textField.setFont(FONT_TEXTFIELD);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(MAU_CHU_CHUNG);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(new Color(150, 150, 160));
                }
            }
        });

        textField.addActionListener(e -> thucHienTimKiem());

        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int iconWidth = 24;
                int iconX = textField.getWidth() - iconWidth - 10;
                if (e.getX() >= iconX) {
                    thucHienTimKiem();
                }
            }
        });

        textField.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int iconWidth = 24;
                int iconX = textField.getWidth() - iconWidth - 10;
                if (e.getX() >= iconX) {
                    textField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    textField.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }
            }
        });

        return textField;
    }

    private JButton taoNut(String text, Color mauNen) {
        JButton btn = new JButton(text);
        btn.setBackground(mauNen);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Cấu hình giao diện, font chữ, màu sắc cho bảng
    private void tuyChinhBang(JTable table) {
        table.setFont(FONT_TEXT);
        table.setRowHeight(40);
        table.setGridColor(MAU_KE_BANG);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.setBackground(MAU_NEN_BANG);
        table.setForeground(MAU_CHU_CHUNG);

        table.setSelectionBackground(MAU_CHON_HANG);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_BANG);
        header.setForeground(MAU_CHU_CHUNG);
        header.setFont(FONT_HEADER);
        header.setPreferredSize(new Dimension(0, 40));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setFont(FONT_HEADER);
                label.setForeground(MAU_CHU_CHUNG);
                label.setBackground(MAU_NEN_BANG);
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, MAU_KE_BANG),
                        new EmptyBorder(10, 5, 10, 5)));
                return label;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(250);
    }

    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100);
                this.trackColor = MAU_NEN_INPUT;
            }
        });
    }
}
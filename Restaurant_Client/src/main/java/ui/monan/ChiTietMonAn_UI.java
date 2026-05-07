package ui.monan;

import connect.ConfigManager;
import rmi_interfaces.IMonAn_Service;
import entity.LichSuGia;
import entity.MonAn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ChiTietMonAn_UI extends JDialog {

    private final MonAn monAnDuocChon;
    private IMonAn_Service monAnDAO;

    private final DecimalFormat currencyFormatter = new DecimalFormat("#,##0");

    private final Color COLOR_DARK = new Color(48, 52, 56);
    private final Color COLOR_TEXT_RED = new Color(255, 87, 87);
    private final Color COLOR_HEADER = new Color(40, 44, 48);
    private final Color COLOR_GRID = new Color(80, 80, 80);
    private final Color COLOR_TEXT_WHITE = Color.WHITE;
    private final Color COLOR_TEXT_GRAY = new Color(150, 150, 150);
    private final Color COLOR_BUTTON_UPDATE = new Color(76, 175, 80);
    private final Color COLOR_BUTTON_UPDATE_HOVER = new Color(56, 142, 60);
    private final Color COLOR_BUTTON_CLOSE = new Color(244, 67, 54);
    private final Color COLOR_BUTTON_CLOSE_HOVER = new Color(211, 47, 47);

    private final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 30);
    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 20);
    private final Font FONT_TIEU_DE_PHU = new Font("Segoe UI", Font.PLAIN, 19);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 19);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 19);

    public ChiTietMonAn_UI(Frame parent, MonAn monAn) {
        super(parent, "Chi tiết món ăn", true);
        this.monAnDuocChon = monAn;

        try {
            String url = ConfigManager.getRmiUrl();

            this.monAnDAO = (IMonAn_Service) Naming.lookup(url +"MonAn_Service");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        khoiTaoGiaoDien();

        setSize(900, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void khoiTaoGiaoDien() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_DARK);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(COLOR_DARK);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        mainPanel.add(taoPanelTieuDe(), BorderLayout.NORTH);
        mainPanel.add(taoPanelNoiDung(), BorderLayout.CENTER);
        mainPanel.add(taoPanelNut(), BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_HEADER);
        panel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel lblTieuDe = new JLabel("Chi Tiết Món Ăn");
        lblTieuDe.setFont(FONT_TIEU_DE);
        lblTieuDe.setForeground(COLOR_TEXT_WHITE);
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTieuDe);
        return panel;
    }

    private JPanel taoPanelNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(COLOR_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pLeft = taoPanelThongTin();
        JPanel pRight = taoPanelHinhAnhMoTa();

        panel.add(pLeft, BorderLayout.WEST);
        panel.add(pRight, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoPanelThongTin() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_DARK);
        panel.setBorder(new LineBorder(COLOR_GRID));
        panel.setPreferredSize(new Dimension(380, 0));

        JLabel lblTieuDe = new JLabel("Thông tin món ăn");
        lblTieuDe.setFont(FONT_NHAN);
        lblTieuDe.setForeground(COLOR_TEXT_WHITE);
        lblTieuDe.setBorder(new EmptyBorder(10, 15, 0, 15));

        JPanel pNoiDungThongTin = new JPanel();
        pNoiDungThongTin.setLayout(new BoxLayout(pNoiDungThongTin, BoxLayout.Y_AXIS));
        pNoiDungThongTin.setBackground(COLOR_DARK);
        pNoiDungThongTin.setBorder(new EmptyBorder(15, 15, 15, 15));

        pNoiDungThongTin.add(taoHangThongTin("Mã món:", monAnDuocChon.getMaMon()));
        pNoiDungThongTin.add(Box.createVerticalStrut(15));
        pNoiDungThongTin.add(taoHangThongTin("Tên món:", monAnDuocChon.getTenMon()));
        pNoiDungThongTin.add(Box.createVerticalStrut(15));
        pNoiDungThongTin.add(taoHangThongTin("Loại món:", monAnDuocChon.getLoaiMon().getTenLoai()));
        pNoiDungThongTin.add(Box.createVerticalStrut(15));
        pNoiDungThongTin.add(taoHangThongTin("Giá bán:", currencyFormatter.format(monAnDuocChon.getGia()) + " VND"));
        pNoiDungThongTin.add(Box.createVerticalStrut(15));
        pNoiDungThongTin.add(taoHangThongTin("Đơn vị:", monAnDuocChon.getDonVi()));
        pNoiDungThongTin.add(Box.createVerticalStrut(15));
        pNoiDungThongTin.add(taoHangThongTin("Tình trạng:", monAnDuocChon.getTinhTrang()));
        pNoiDungThongTin.add(Box.createVerticalStrut(15));

        panel.add(lblTieuDe, BorderLayout.NORTH);
        panel.add(pNoiDungThongTin, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoPanelHinhAnhMoTa() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_DARK);
        panel.setBorder(new LineBorder(COLOR_GRID));

        JPanel pImage = new JPanel(new BorderLayout());
        pImage.setOpaque(false);
        pImage.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblHinhAnh = new JLabel();
        try {
            String imagePath = monAnDuocChon.getDuongDanAnh();
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
                if (icon.getImage() != null) {
                    Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                    lblHinhAnh.setIcon(new ImageIcon(img));
                } else {
                    lblHinhAnh.setText("Không tìm thấy ảnh tại: " + imagePath);
                }
            } else {
                lblHinhAnh.setText("Không có đường dẫn ảnh");
            }
        } catch (Exception e) {
            lblHinhAnh.setText("Lỗi tải ảnh");
            System.err.println("Lỗi tải ảnh món ăn: " + e.getMessage());
        }
        lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setVerticalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setForeground(COLOR_TEXT_GRAY);
        pImage.add(lblHinhAnh, BorderLayout.CENTER);

        JPanel pMoTa = new JPanel(new BorderLayout(0, 5));
        pMoTa.setOpaque(false);
        pMoTa.setBorder(new EmptyBorder(0, 15, 15, 15));

        JLabel lblTieuDeMoTa = new JLabel("Mô tả");
        lblTieuDeMoTa.setFont(FONT_NHAN);
        lblTieuDeMoTa.setForeground(COLOR_TEXT_WHITE);
        lblTieuDeMoTa.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtMoTa = new JTextArea(monAnDuocChon.getMoTa());
        txtMoTa.setFont(FONT_O_NHAP);
        txtMoTa.setBackground(COLOR_DARK);
        txtMoTa.setForeground(COLOR_TEXT_GRAY);
        txtMoTa.setEditable(false);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBorder(null);

        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setBorder(null);
        scrollMoTa.getViewport().setBackground(COLOR_DARK);
        scrollMoTa.setPreferredSize(new Dimension(100, 80));

        pMoTa.add(lblTieuDeMoTa, BorderLayout.NORTH);
        pMoTa.add(scrollMoTa, BorderLayout.CENTER);

        panel.add(pImage);
        panel.add(pMoTa);

        return panel;
    }

    private JPanel taoHangThongTin(String nhan, String gia_tri) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblNhan = new JLabel(nhan);
        lblNhan.setFont(FONT_NHAN);
        lblNhan.setForeground(COLOR_TEXT_WHITE);

        JLabel lblGiaTri = new JLabel(gia_tri);
        lblGiaTri.setFont(FONT_O_NHAP);
        lblGiaTri.setForeground(COLOR_TEXT_GRAY);
        lblGiaTri.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(lblNhan, BorderLayout.WEST);
        panel.add(lblGiaTri, BorderLayout.EAST);

        return panel;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(COLOR_DARK);
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JButton btnSua = taoNut("Sửa thông tin", COLOR_BUTTON_UPDATE, COLOR_BUTTON_UPDATE_HOVER);
        btnSua.setPreferredSize(new Dimension(190, 40));
        btnSua.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);

            if (parentWindow instanceof ui.TrangChu_UI) {
                ui.TrangChu_UI trangChu = (ui.TrangChu_UI) parentWindow;
                trangChu.hienThiTrangCapNhatMon(monAnDuocChon);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy cửa sổ Trang Chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnLichSu = taoNut("Lịch sử giá", new Color(33, 150, 243), new Color(30, 136, 229));
        btnLichSu.setPreferredSize(new Dimension(160, 40));
        btnLichSu.addActionListener(e -> hienThiLichSuGia());

        JButton btnDong = taoNut("Đóng", COLOR_BUTTON_CLOSE, COLOR_BUTTON_CLOSE_HOVER);
        btnDong.setPreferredSize(new Dimension(160, 40));
        btnDong.addActionListener(e -> dispose());

        panel.add(btnSua);
        panel.add(btnLichSu);
        panel.add(btnDong);

        return panel;
    }

    private JButton taoNut(String text, Color mauNen, Color mauHover) {
        JButton button = new JButton(text);
        button.setFont(FONT_NUT);
        button.setBackground(mauNen);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(mauHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(mauNen);
            }
        });

        return button;
    }

    private void hienThiLichSuGia() {
        Color MAU_NEN_DARK = new Color(48, 52, 56);
        Color MAU_NEN_ITEM = new Color(31, 32, 44);
        Color MAU_LUOI_BANG = new Color(60, 62, 77);
        Color MAU_CHU_CHUNG = Color.WHITE;
        Color MAU_HEADER_BORDER = MAU_LUOI_BANG;
        Color MAU_CHON_HANG = new Color(70, 72, 90);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Lịch sử thay đổi giá: " + monAnDuocChon.getTenMon(), ModalityType.APPLICATION_MODAL);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(MAU_NEN_DARK);

        String[] headers = {"Thời gian thay đổi", "Giá cũ", "Giá mới", "Người thực hiện"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setBackground(MAU_NEN_ITEM);
        table.setForeground(MAU_CHU_CHUNG);
        table.setGridColor(MAU_LUOI_BANG);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setSelectionBackground(MAU_CHON_HANG);
        table.setSelectionForeground(MAU_CHU_CHUNG);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_ITEM);
        header.setForeground(MAU_CHU_CHUNG);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_HEADER_BORDER));

        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setForeground(MAU_CHU_CHUNG);
                label.setBackground(MAU_NEN_ITEM);
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, MAU_HEADER_BORDER),
                        new EmptyBorder(10, 5, 10, 5)
                ));
                return label;
            }
        });

        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(MAU_NEN_ITEM);
        centerRenderer.setForeground(MAU_CHU_CHUNG);

        for(int i=0; i<table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(MAU_NEN_ITEM);
        scroll.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));

        JPanel corner = new JPanel();
        corner.setBackground(MAU_NEN_ITEM);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);

        scroll.getVerticalScrollBar().setBackground(MAU_NEN_ITEM);
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 104, 124);
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

        JPanel pTable = new JPanel(new BorderLayout());
        pTable.setBackground(MAU_NEN_DARK);
        pTable.setBorder(new EmptyBorder(10, 10, 10, 10));
        pTable.add(scroll, BorderLayout.CENTER);

        dialog.add(pTable, BorderLayout.CENTER);

        JPanel pBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBot.setBackground(MAU_NEN_DARK);
        pBot.setBorder(new EmptyBorder(0, 10, 15, 15));

        JButton btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 40));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(244, 67, 54));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dialog.dispose());

        pBot.add(btnClose);
        dialog.add(pBot, BorderLayout.SOUTH);

        // BỌC SWINGWORKER
        SwingWorker<List<LichSuGia>, Void> worker = new SwingWorker<List<LichSuGia>, Void>() {
            @Override
            protected List<LichSuGia> doInBackground() throws Exception {
                if (monAnDAO == null) throw new Exception("Mất kết nối máy chủ");
                return monAnDAO.getLichSuGia(monAnDuocChon.getMaMon());
            }

            @Override
            protected void done() {
                try {
                    List<LichSuGia> list = get();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
                    for (entity.LichSuGia ls : list) {
                        model.addRow(new Object[]{
                                sdf.format(ls.getNgayThayDoi()),
                                currencyFormatter.format(ls.getGiaCu()) + " VNĐ",
                                currencyFormatter.format(ls.getGiaMoi()) + " VNĐ",
                                ls.getTenNhanVien() == null ? "Hệ thống" : ls.getTenNhanVien()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(dialog, "Lỗi tải lịch sử giá!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();

        dialog.setVisible(true);
    }
}
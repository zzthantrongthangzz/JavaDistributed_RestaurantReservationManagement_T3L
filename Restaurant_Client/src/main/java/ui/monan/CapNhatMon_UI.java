package ui.monan;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import connect.ConfigManager;
import rmi_interfaces.IMonAn_Service;
import rmi_interfaces.ILoaiMon_Service;
import entity.MonAn;
import ui.Auth;
import entity.LoaiMon;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;

public class CapNhatMon_UI extends JPanel {

    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color bgColor = new Color(48, 52, 56);
    private final Color componentColor = new Color(124, 124, 124);
    private final Color textColor = Color.WHITE;
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
    private final Color MAU_NUT_CAP_NHAT = new Color(255, 193, 7);
    private final Color MAU_NUT_XOA = new Color(244, 67, 54);
    private final Color MAU_NUT_LAM_MOI = new Color(30, 144, 255);
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    private final Color MAU_THANH_CUON_THUMB = new Color(100, 104, 124);
    private final Color MAU_THANH_CUON_TRACK = new Color(31, 32, 44);

    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 60;
    private final int CHIEU_CAO_HEADER_BANG = 40;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(270, 40);
    private final Dimension KICH_THUOC_COMBO_BOX = new Dimension(150, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 16);

    private JTextField txtMaMon;
    private JTextField txtTenMon;
    private JTextField txtGia;
    private JTextField txtDonVi;
    private JComboBox<String> cmbTinhTrang;
    private JComboBox<LoaiMon> cmbLoai;
    private JTextArea txtMoTa;
    private File selectedFile;
    private ImagePreviewPanel pnlImagePreview;
    private Image backgroundImage;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiemMa;
    private JTextField txtTimKiemTen;
    private JComboBox<Object> cmbBoLoc;
    private JPanel panelChinh;

    private IMonAn_Service monAnDAO;
    private ILoaiMon_Service loaiMonDAO;
    private List<MonAn> danhSachMonAnHienThi;

    private JButton btnCapNhat;
    private JButton btnXoa;

    public CapNhatMon_UI() {
        try {
            String url = ConfigManager.getRmiUrl();
            monAnDAO = (IMonAn_Service) Naming.lookup(url +"MonAn_Service");
            loaiMonDAO = (ILoaiMon_Service) Naming.lookup(url +"LoaiMon_Service");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        setBackground(bgColor);
        setLayout(new BorderLayout());

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/img/vipbackground2.png")).getImage();
        } catch (Exception e) {
            backgroundImage = null;
        }

        panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(bgColor);
        panelChinh.setBorder(new EmptyBorder(15, 20, 15, 20));
        panelChinh.setFocusable(true);

        JPanel panelTop = taoPanelNhapLieu();
        panelChinh.add(panelTop, BorderLayout.NORTH);

        JPanel panelBottom = taoPanelTimKiemVaBang();
        panelChinh.add(panelBottom, BorderLayout.CENTER);

        add(panelChinh, BorderLayout.CENTER);

        taiDuLieuLoaiMon();
        docDuLieuTuSQL();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private JPanel taoPanelNhapLieu() {
        JPanel panel = new JPanel(null);
        panel.setBackground(bgColor);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 350));

        JLabel lblTitle = new JLabel("CẬP NHẬT MÓN");
        lblTitle.setForeground(textColor);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setBounds(650, 15, 300, 40);
        panel.add(lblTitle);

        pnlImagePreview = new ImagePreviewPanel();
        pnlImagePreview.setBounds(160, 80, 200, 200);
        panel.add(pnlImagePreview);

        JButton btnChonAnh = createStyledButton("Chọn ảnh", "/IMG/folder_32px.png", componentColor);
        btnChonAnh.setBounds(195, 300, 130, 40);
        panel.add(btnChonAnh);
        btnChonAnh.addActionListener(e -> chonAnh());

        JLabel lblMaMon = createStyledLabel("Mã món:");
        lblMaMon.setBounds(420, 80, 100, 38);
        panel.add(lblMaMon);
        txtMaMon = createStyledTextField();
        txtMaMon.setBounds(510, 80, 280, 38);
        txtMaMon.setEditable(false);
        txtMaMon.setBackground(MAU_NEN_INPUT);
        panel.add(txtMaMon);

        JLabel lblTenMon = createStyledLabel("Tên món:");
        lblTenMon.setBounds(420, 130, 100, 38);
        panel.add(lblTenMon);
        txtTenMon = createStyledTextField();
        txtTenMon.setBounds(510, 130, 280, 38);
        panel.add(txtTenMon);

        JLabel lblDonVi = createStyledLabel("Đơn vị:");
        lblDonVi.setBounds(420, 180, 100, 38);
        panel.add(lblDonVi);
        txtDonVi = createStyledTextField();
        txtDonVi.setBounds(510, 180, 280, 38);
        panel.add(txtDonVi);

        JLabel lblGia = createStyledLabel("Giá:");
        lblGia.setBounds(420, 230, 100, 38);
        panel.add(lblGia);
        txtGia = createStyledTextField();
        txtGia.setBounds(510, 230, 280, 38);
        panel.add(txtGia);

        JLabel lblTinhTrang = createStyledLabel("Tình trạng:");
        lblTinhTrang.setBounds(840, 80, 100, 38);
        panel.add(lblTinhTrang);
        cmbTinhTrang = createStyledComboBox(new String[]{"Đang kinh doanh", "Ngừng kinh doanh"});
        cmbTinhTrang.setBounds(940, 80, 280, 38);
        panel.add(cmbTinhTrang);

        JLabel lblLoai = createStyledLabel("Loại:");
        lblLoai.setBounds(840, 130, 100, 38);
        panel.add(lblLoai);

        cmbLoai = new JComboBox<LoaiMon>();
        setupComboBoxUI(cmbLoai);
        cmbLoai.setBounds(940, 130, 280, 38);
        panel.add(cmbLoai);

        JLabel lblMoTa = createStyledLabel("Mô tả:");
        lblMoTa.setBounds(840, 180, 100, 38);
        panel.add(lblMoTa);

        txtMoTa = new JTextArea();
        txtMoTa.setBackground(componentColor);
        txtMoTa.setForeground(textColor);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMoTa.setBorder(new EmptyBorder(8, 8, 8, 8));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);

        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        tuyChinhScrollBar(scrollMoTa);
        scrollMoTa.setBorder(null);
        scrollMoTa.setBounds(940, 180, 280, 88);
        panel.add(scrollMoTa);

        btnCapNhat = taoNutChucNang("Cập nhật", MAU_NUT_CAP_NHAT);
        btnCapNhat.setBounds(1260, 130, 140, 40);
        panel.add(btnCapNhat);
        btnCapNhat.addActionListener(e -> capNhatMonAn());

        btnXoa = taoNutChucNang("Xoá", MAU_NUT_XOA);
        btnXoa.setBounds(1260, 180, 140, 40);
        panel.add(btnXoa);
        btnXoa.addActionListener(e -> xoaMonAn());

        return panel;
    }

    private JPanel taoPanelTimKiemVaBang() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(bgColor);
        panel.setOpaque(false);

        JPanel panelTimKiem = taoPanelTimKiem();
        panel.add(panelTimKiem, BorderLayout.NORTH);
        JPanel panelBang = taoPanelBang();
        panel.add(panelBang, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoPanelTimKiem() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(bgColor);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 50));

        JPanel panelTimKiemLoc = new JPanel();
        panelTimKiemLoc.setLayout(new BoxLayout(panelTimKiemLoc, BoxLayout.X_AXIS));
        panelTimKiemLoc.setBackground(bgColor);
        panelTimKiemLoc.setOpaque(false);

        JLabel lblMaMon = new JLabel("Mã món: ");
        lblMaMon.setFont(FONT_NHAN);
        lblMaMon.setForeground(Color.WHITE);
        lblMaMon.setBackground(componentColor);
        lblMaMon.setOpaque(true);
        lblMaMon.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaMon.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        txtTimKiemMa = taoThanhTimKiem("Nhập mã món. . .");
        JPanel searchWrapper1 = new JPanel(new BorderLayout());
        searchWrapper1.setBackground(bgColor);
        searchWrapper1.setOpaque(false);
        searchWrapper1.add(lblMaMon, BorderLayout.WEST);
        searchWrapper1.add(txtTimKiemMa, BorderLayout.CENTER);
        searchWrapper1.setMaximumSize(new Dimension(400, 40));

        JLabel lblTenMon = new JLabel("Tên món:");
        lblTenMon.setFont(FONT_NHAN);
        lblTenMon.setForeground(Color.WHITE);
        lblTenMon.setBackground(componentColor);
        lblTenMon.setOpaque(true);
        lblTenMon.setHorizontalAlignment(SwingConstants.CENTER);
        lblTenMon.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        txtTimKiemTen = taoThanhTimKiem("Nhập tên món. . .");
        JPanel searchWrapper2 = new JPanel(new BorderLayout());
        searchWrapper2.setBackground(bgColor);
        searchWrapper2.setOpaque(false);
        searchWrapper2.add(lblTenMon, BorderLayout.WEST);
        searchWrapper2.add(txtTimKiemTen, BorderLayout.CENTER);
        searchWrapper2.setMaximumSize(new Dimension(400, 40));

        JLabel lblLoai = new JLabel("Loại");
        lblLoai.setFont(FONT_NHAN);
        lblLoai.setForeground(Color.WHITE);
        lblLoai.setBackground(componentColor);
        lblLoai.setOpaque(true);
        lblLoai.setHorizontalAlignment(SwingConstants.CENTER);
        lblLoai.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        cmbBoLoc = new JComboBox<Object>();
        cmbBoLoc.setFont(FONT_NHAN);
        cmbBoLoc.setBackground(MAU_THANH_TIM_KIEM);
        cmbBoLoc.setForeground(textColor);
        cmbBoLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmbBoLoc.setFocusable(false);
        cmbBoLoc.addActionListener(e -> thucHienLoc());

        cmbBoLoc.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/IMG/muitenxuong_32px.png"));
                    Image img = icon.getImage().getScaledInstance(KICH_THUOC_ICON, KICH_THUOC_ICON, Image.SCALE_SMOOTH);
                    JButton button = new JButton(new ImageIcon(img));
                    button.setBackground(MAU_THANH_TIM_KIEM);
                    button.setOpaque(true);
                    button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                    return button;
                } catch (Exception e) {
                    JButton button = new JButton("▼");
                    button.setBackground(MAU_THANH_TIM_KIEM);
                    return button;
                }
            }
        });
        cmbBoLoc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
                new EmptyBorder(0, 15, 0, 5)
        ));

        JPanel comboWrapper = new JPanel(new BorderLayout());
        comboWrapper.setBackground(bgColor);
        comboWrapper.setOpaque(false);
        comboWrapper.add(lblLoai, BorderLayout.WEST);
        comboWrapper.add(cmbBoLoc, BorderLayout.CENTER);
        comboWrapper.setMaximumSize(new Dimension(250, 40));

        panelTimKiemLoc.add(searchWrapper1);
        panelTimKiemLoc.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiemLoc.add(searchWrapper2);
        panelTimKiemLoc.add(Box.createRigidArea(new Dimension(10, 0)));
        panelTimKiemLoc.add(comboWrapper);

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelNut.setBackground(bgColor);
        panelNut.setOpaque(false);

        JButton btnLamMoi = taoNutChucNang("Làm mới", MAU_NUT_LAM_MOI);
        btnLamMoi.setPreferredSize(new Dimension(115, 40));
        btnLamMoi.addActionListener(e -> lamMoi());
        panelNut.add(btnLamMoi);

        panel.add(panelTimKiemLoc, BorderLayout.WEST);
        panel.add(panelNut, BorderLayout.EAST);

        return panel;
    }

    private JTextField taoThanhTimKiem(String placeholder) {
        JTextField textField = new JTextField() {
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

        textField.setText(placeholder);
        textField.setForeground(MAU_PLACEHOLDER);
        textField.setBackground(MAU_THANH_TIM_KIEM);
        textField.setCaretColor(textColor);
        textField.setFont(FONT_TEXTFIELD);
        textField.setPreferredSize(KICH_THUOC_THANH_TIM_KIEM);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
                new EmptyBorder(8, 15, 8, 40)
        ));

        textField.addActionListener(e -> {
            if (textField == txtTimKiemMa) {
                thucHienTimKiemTheoMa();
            } else if (textField == txtTimKiemTen) {
                thucHienTimKiemTheoTen();
            }
        });

        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int iconX = textField.getWidth() - KICH_THUOC_ICON - 10;
                Rectangle iconBounds = new Rectangle(iconX, 0, KICH_THUOC_ICON + 10, textField.getHeight());
                if (iconBounds.contains(e.getPoint())) {
                    if (textField == txtTimKiemMa) {
                        thucHienTimKiemTheoMa();
                    } else if (textField == txtTimKiemTen) {
                        thucHienTimKiemTheoTen();
                    }
                }
            }
        });

        textField.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int iconX = textField.getWidth() - KICH_THUOC_ICON - 10;
                Rectangle iconBounds = new Rectangle(iconX, 0, KICH_THUOC_ICON + 10, textField.getHeight());
                textField.setCursor(iconBounds.contains(e.getPoint()) ?
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) :
                        Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }
        });

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(textColor);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(MAU_PLACEHOLDER);
                }
            }
        });

        return textField;
    }

    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setOpaque(false);

        String[] columnNames = {"Mã món", "Tên món", "Giá", "Đơn vị", "Tình trạng", "Loại", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setBackground(MAU_NEN_ITEM);
        table.setForeground(textColor);
        table.setGridColor(MAU_LUOI_BANG);
        table.setRowHeight(CHIEU_CAO_HANG_BANG);
        table.setFont(FONT_BANG);
        table.setSelectionBackground(MAU_CHON_HANG);
        table.setSelectionForeground(textColor);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    hienThiDuLieuLenForm(selectedRow);
                }
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(MAU_NEN_ITEM);
        centerRenderer.setForeground(textColor);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_ITEM);
        header.setForeground(textColor);
        header.setFont(FONT_HEADER_BANG);
        header.setPreferredSize(new Dimension(0, CHIEU_CAO_HEADER_BANG));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_LUOI_BANG));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(FONT_HEADER_BANG);
                label.setForeground(textColor);
                label.setBackground(MAU_NEN_ITEM);
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, MAU_LUOI_BANG),
                        new EmptyBorder(8, 5, 8, 5)
                ));
                return label;
            }
        });

        int[] widths = {100, 150, 100, 80, 120, 120, 200};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tuyChinhScrollBar(scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        JPanel corner = new JPanel();
        corner.setBackground(MAU_NEN_INPUT);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private class ImagePreviewPanel extends JPanel {
        private Image image;

        public ImagePreviewPanel() {
            setBackground(bgColor);
            setOpaque(false);
        }

        public void setImage(Image image) {
            this.image = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int diameter = Math.min(getWidth(), getHeight());
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            Ellipse2D.Double circle = new Ellipse2D.Double(x, y, diameter, diameter);

            if (image != null) {
                g2d.setClip(circle);
                g2d.drawImage(image, x, y, diameter, diameter, this);
            } else {
                g2d.setColor(componentColor);
                g2d.fill(circle);
            }
            g2d.dispose();
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(FONT_NHAN);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(componentColor);
        textField.setForeground(textColor);
        textField.setFont(FONT_TEXTFIELD);
        textField.setBorder(new EmptyBorder(5, 10, 5, 10));
        textField.setCaretColor(Color.WHITE);
        return textField;
    }

    private <T> void setupComboBoxUI(JComboBox<T> comboBox) {
        comboBox.setBackground(componentColor);
        comboBox.setForeground(textColor);
        comboBox.setFont(FONT_TEXTFIELD);
        comboBox.setFocusable(false);

        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(componentColor);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });

        Border border = BorderFactory.createLineBorder(componentColor, 2);
        Border padding = new EmptyBorder(5, 10, 5, 10);
        comboBox.setBorder(BorderFactory.createCompoundBorder(border, padding));
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        setupComboBoxUI(comboBox);
        return comboBox;
    }

    private JButton createStyledButton(String text, String iconPath, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledIcon = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledIcon));
        } catch (Exception e) {
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private JButton taoNutChucNang(String text, Color mauNen) {
        JButton button = new JButton(text);
        button.setFont(FONT_NHAN);
        button.setForeground(Color.WHITE);
        button.setBackground(mauNen);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(mauNen.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(mauNen); }
        });

        return button;
    }

    private void taiDuLieuLoaiMon() {
        if (loaiMonDAO == null) return;
        SwingWorker<List<LoaiMon>, Void> worker = new SwingWorker<List<LoaiMon>, Void>() {
            @Override
            protected List<LoaiMon> doInBackground() throws Exception {
                return loaiMonDAO.docDanhSachLoaiMon();
            }
            @Override
            protected void done() {
                try {
                    List<LoaiMon> dsLoai = get();
                    cmbLoai.removeAllItems();
                    cmbBoLoc.removeAllItems();
                    cmbBoLoc.addItem("Tất cả");

                    for (LoaiMon loai : dsLoai) {
                        cmbLoai.addItem(loai);
                        cmbBoLoc.addItem(loai);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh món ăn");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "png", "gif", "jpeg"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            try {
                ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                pnlImagePreview.setImage(imageIcon.getImage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể tải ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void docDuLieuTuSQL() {
        if (monAnDAO == null) return;
        SwingWorker<List<MonAn>, Void> worker = new SwingWorker<List<MonAn>, Void>() {
            @Override
            protected List<MonAn> doInBackground() throws Exception {
                return monAnDAO.docDanhSachMon();
            }
            @Override
            protected void done() {
                try {
                    hienThiDanhSach(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void hienThiDanhSach(List<MonAn> danhSach) {
        danhSachMonAnHienThi = danhSach;
        tableModel.setRowCount(0);

        if(danhSachMonAnHienThi != null) {
            for (MonAn mon : danhSachMonAnHienThi) {
                tableModel.addRow(new Object[]{
                        mon.getMaMon(),
                        mon.getTenMon(),
                        String.format("%,.0f", mon.getGia()),
                        mon.getDonVi(),
                        mon.getTinhTrang(),
                        mon.getLoaiMon().getTenLoai(),
                        mon.getMoTa()
                });
            }
        }
    }

    private void hienThiDuLieuLenForm(int row) {
        MonAn monAn = danhSachMonAnHienThi.get(row);

        txtMaMon.setText(monAn.getMaMon());
        txtTenMon.setText(monAn.getTenMon());
        String giaStr = String.format("%.0f", monAn.getGia());
        txtGia.setText(giaStr);
        txtDonVi.setText(monAn.getDonVi());
        cmbTinhTrang.setSelectedItem(monAn.getTinhTrang());

        for(int i = 0; i < cmbLoai.getItemCount(); i++) {
            if(cmbLoai.getItemAt(i).getMaLoai().equals(monAn.getLoaiMon().getMaLoai())) {
                cmbLoai.setSelectedIndex(i);
                break;
            }
        }

        txtMoTa.setText(monAn.getMoTa());

        try {
            ImageIcon imageIcon = new ImageIcon(getClass().getResource(monAn.getDuongDanAnh()));
            pnlImagePreview.setImage(imageIcon.getImage());
        } catch (Exception e) {
            pnlImagePreview.setImage(null);
        }

        selectedFile = null;
    }

    private void capNhatMonAn() {
        String maMon = txtMaMon.getText().trim();
        if (maMon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần cập nhật từ bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!kiemTraDuLieuForm()) {
            return;
        }

        String tenMon = txtTenMon.getText().trim();
        double gia = Double.parseDouble(txtGia.getText().trim());
        String donVi = txtDonVi.getText().trim();
        String tinhTrang = cmbTinhTrang.getSelectedItem().toString();
        LoaiMon loaiChon = (LoaiMon) cmbLoai.getSelectedItem();
        String moTa = txtMoTa.getText().trim();

        String maNhanVien = "NV_Unknown";
        if (Auth.getCurrentNhanVien() != null) {
            maNhanVien = Auth.getCurrentNhanVien().getMaNhanVien();
        }

        final String finalMaNhanVien = maNhanVien;
        btnCapNhat.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                MonAn monAnCu = monAnDAO.timMotMonTheoMa(maMon);
                if (monAnCu == null) {
                    throw new Exception("Không tìm thấy món ăn gốc để cập nhật!");
                }

                String duongDanAnh = monAnCu.getDuongDanAnh();

                if (selectedFile != null) {
                    String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                    String tenFileAnh = "mon_" + chuyenTenMonThanhTenFile(tenMon) + extension;
                    duongDanAnh = "/img/" + tenFileAnh;

                    java.net.URL resourceUrl = getClass().getResource("/img");
                    if (resourceUrl == null) {
                        File outputDir = new File("bin/img");
                        if (!outputDir.exists()) outputDir.mkdirs();
                        resourceUrl = outputDir.toURI().toURL();
                    }
                    File destFile = new File(new java.net.URI(resourceUrl.toString() + "/" + tenFileAnh));
                    Files.copy(selectedFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                MonAn monAnMoi = new MonAn(maMon, tenMon, duongDanAnh, gia, tinhTrang, moTa, donVi, loaiChon);
                return monAnDAO.capNhatMonAn(monAnMoi, finalMaNhanVien);
            }

            @Override
            protected void done() {
                btnCapNhat.setEnabled(true);
                try {
                    boolean ketQua = get();
                    if (ketQua) {
                        JOptionPane.showMessageDialog(CapNhatMon_UI.this, "Cập nhật món ăn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        docDuLieuTuSQL();
                        lamMoiForm();
                    } else {
                        JOptionPane.showMessageDialog(CapNhatMon_UI.this, "Cập nhật món ăn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CapNhatMon_UI.this, "Có lỗi xảy ra: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void xoaMonAn() {
        String maMon = txtMaMon.getText().trim();
        if (maMon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa món ăn này?\n(Hành động này sẽ đổi trạng thái thành 'Ngừng kinh doanh')",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            btnXoa.setEnabled(false);
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return monAnDAO.xoaMem(maMon);
                }

                @Override
                protected void done() {
                    btnXoa.setEnabled(true);
                    try {
                        boolean ketQua = get();
                        if(ketQua) {
                            JOptionPane.showMessageDialog(CapNhatMon_UI.this, "Xóa món ăn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(CapNhatMon_UI.this, "Xóa món ăn thất bại!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        }
                        lamMoiForm();
                        docDuLieuTuSQL();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    private void thucHienTimKiemTheoMa() {
        if (monAnDAO == null) return;
        String tuKhoa = txtTimKiemMa.getText().trim();
        String placeholder = "Nhập mã món. . .";

        SwingWorker<List<MonAn>, Void> worker = new SwingWorker<List<MonAn>, Void>() {
            @Override
            protected List<MonAn> doInBackground() throws Exception {
                if (tuKhoa.isEmpty() || tuKhoa.equals(placeholder)) {
                    return monAnDAO.docDanhSachMon();
                } else {
                    return monAnDAO.timKiemTheoMa(tuKhoa);
                }
            }

            @Override
            protected void done() {
                try {
                    List<MonAn> ketQua = get();
                    if (!tuKhoa.isEmpty() && !tuKhoa.equals(placeholder) && ketQua.isEmpty()) {
                        JOptionPane.showMessageDialog(CapNhatMon_UI.this, "Không tìm thấy món ăn nào với mã: \"" + tuKhoa + "\"", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                    hienThiDanhSach(ketQua);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void thucHienTimKiemTheoTen() {
        if (monAnDAO == null) return;
        String tuKhoa = txtTimKiemTen.getText().trim();
        String placeholder = "Nhập tên món. . .";

        SwingWorker<List<MonAn>, Void> worker = new SwingWorker<List<MonAn>, Void>() {
            @Override
            protected List<MonAn> doInBackground() throws Exception {
                if (tuKhoa.isEmpty() || tuKhoa.equals(placeholder)) {
                    return monAnDAO.docDanhSachMon();
                } else {
                    return monAnDAO.timKiemTheoTen(tuKhoa);
                }
            }

            @Override
            protected void done() {
                try {
                    List<MonAn> ketQua = get();
                    if (!tuKhoa.isEmpty() && !tuKhoa.equals(placeholder) && ketQua.isEmpty()) {
                        JOptionPane.showMessageDialog(CapNhatMon_UI.this, "Không tìm thấy món ăn nào với tên: \"" + tuKhoa + "\"", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                    hienThiDanhSach(ketQua);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void thucHienLoc() {
        if (monAnDAO == null) return;
        Object itemDuocChon = cmbBoLoc.getSelectedItem();

        if (itemDuocChon == null) {
            return;
        }

        SwingWorker<List<MonAn>, Void> worker = new SwingWorker<List<MonAn>, Void>() {
            @Override
            protected List<MonAn> doInBackground() throws Exception {
                if (itemDuocChon instanceof String && itemDuocChon.equals("Tất cả")) {
                    return monAnDAO.docDanhSachMon();
                } else if (itemDuocChon instanceof LoaiMon) {
                    String tenLoai = ((LoaiMon) itemDuocChon).getTenLoai();
                    return monAnDAO.locMonAnTheoLoai(tenLoai);
                } else {
                    return monAnDAO.docDanhSachMon();
                }
            }

            @Override
            protected void done() {
                try {
                    List<MonAn> ketQuaLoc = get();
                    hienThiDanhSach(ketQuaLoc);

                    txtTimKiemMa.setForeground(MAU_PLACEHOLDER);
                    txtTimKiemMa.setText("Nhập mã món. . .");
                    txtTimKiemTen.setForeground(MAU_PLACEHOLDER);
                    txtTimKiemTen.setText("Nhập tên món. . .");
                    panelChinh.requestFocusInWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void lamMoi() {
        lamMoiForm();
        if (monAnDAO == null) return;

        SwingWorker<List<MonAn>, Void> worker = new SwingWorker<List<MonAn>, Void>() {
            @Override
            protected List<MonAn> doInBackground() throws Exception {
                return monAnDAO.docDanhSachMon();
            }

            @Override
            protected void done() {
                try {
                    List<MonAn> toanBoMonAn = get();
                    hienThiDanhSach(toanBoMonAn);
                    txtTimKiemMa.setForeground(MAU_PLACEHOLDER);
                    txtTimKiemMa.setText("Nhập mã món. . .");
                    txtTimKiemTen.setForeground(MAU_PLACEHOLDER);
                    txtTimKiemTen.setText("Nhập tên món. . .");

                    cmbBoLoc.setSelectedIndex(0);
                    panelChinh.requestFocusInWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void lamMoiForm() {
        txtMaMon.setText("");
        txtTenMon.setText("");
        txtGia.setText("");
        txtDonVi.setText("");
        txtMoTa.setText("");
        if(cmbLoai.getItemCount() > 0) cmbLoai.setSelectedIndex(0);
        cmbTinhTrang.setSelectedIndex(0);
        pnlImagePreview.setImage(null);
        selectedFile = null;
        table.clearSelection();
    }

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
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !verticalScrollBar.isEnabled()) {
                    return;
                }
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
        horizontalScrollBar.setBackground(MAU_NEN_INPUT);
        horizontalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120);
                this.trackColor = MAU_NEN_INPUT;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !horizontalScrollBar.isEnabled()) {
                    return;
                }
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

    public void chonMonAnDeCapNhat(String maMon) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).toString().equals(maMon)) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                hienThiDuLieuLenForm(i);
                return;
            }
        }
    }

    private boolean kiemTraDuLieuForm() {
        String tenMon = txtTenMon.getText().trim();
        String giaStr = txtGia.getText().trim();
        String donVi = txtDonVi.getText().trim();

        if (tenMon.isEmpty()) {
            showValidationError("Vui lòng nhập tên món!", txtTenMon);
            return false;
        }
        if (!tenMon.matches("^[A-ZÀ-Ỹ][a-zA-Zà-ỹÀ-Ỹ\\s]*$")) {
            showValidationError(
                    "Tên món phải bắt đầu bằng chữ hoa và chỉ chứa chữ cái, khoảng trắng.\n" +
                            "Ví dụ: Gà rán, Phở Bò Tái, Lẩu nấm",
                    txtTenMon);
            return false;
        }

        if (giaStr.isEmpty()) {
            showValidationError("Vui lòng nhập giá món!", txtGia);
            return false;
        }
        try {
            double gia = Double.parseDouble(giaStr);
            if (gia <= 0) {
                showValidationError("Giá phải là một số dương (lớn hơn 0)!", txtGia);
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Giá phải là số hợp lệ! (Ví dụ: 50000 hoặc 50000.5)", txtGia);
            return false;
        }

        if (donVi.isEmpty()) {
            showValidationError("Vui lòng nhập đơn vị!", txtDonVi);
            return false;
        }
        if (!donVi.matches("^[A-ZÀ-Ỹ][a-zA-Zà-ỹÀ-Ỹ\\s]*$")) {
            showValidationError(
                    "Đơn vị phải bắt đầu bằng chữ hoa và chỉ chứa chữ cái, khoảng trắng.\n" + "Ví dụ: Dĩa, Ly, Phần, Tô",txtDonVi);
            return false;
        }

        if (cmbLoai.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại món!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private String chuyenTenMonThanhTenFile(String tenMon) {
        String result = tenMon.toLowerCase();
        result = result.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        result = result.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        result = result.replaceAll("[ìíịỉĩ]", "i");
        result = result.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        result = result.replaceAll("[ùúụủũưừứựửữ]", "u");
        result = result.replaceAll("[ỳýỵỷỹ]", "y");
        result = result.replaceAll("đ", "d");
        result = result.replaceAll("[^a-z0-9]", "");
        return result;
    }

    private void showValidationError(String message, JComponent component) {
        JOptionPane.showMessageDialog(this, message, "Lỗi Nhập Liệu", JOptionPane.WARNING_MESSAGE);
        component.requestFocus();
    }
}
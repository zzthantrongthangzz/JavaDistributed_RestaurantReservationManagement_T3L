package ui.banan;

import connect.ConfigManager;
import rmi_interfaces.IBanAn_Service;
import entity.BanAn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.util.List;

public class CapNhatBan_UI extends JPanel {

    private final Color bgColor = new Color(48, 52, 56);
    private final Color componentColor = new Color(124, 124, 124);
    private final Color textColor = Color.WHITE;
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);

    private final Color MAU_NUT_CAP_NHAT = new Color(255, 193, 7);
    private final Color MAU_NUT_XOA = new Color(244, 67, 54);
    private final Color MAU_NUT_LAM_MOI = new Color(30, 144, 255);
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);
    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 16);

    private JTextField txtMaBan;
    private JTextField txtTenBan;
    private JTextField txtSucChua;
    private JTextField txtTrangThai;
    private JComboBox<String> cmbLoaiBan;
    private JTable table;
    private DefaultTableModel tableModel;

    // Đã nâng lên thành biến toàn cục để điều khiển trạng thái Enable/Disable
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoi;

    // CHUẨN HÓA RMI SERVICE
    private IBanAn_Service banAnService;
    private List<BanAn> danhSachBanHienThi;

    public CapNhatBan_UI() {
        try {
            String url = ConfigManager.getRmiUrl();
            banAnService = (IBanAn_Service) Naming.lookup(url +"BanAn_Service");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ RMI!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        setBackground(bgColor);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel panelTop = taoPanelNhapLieu();
        add(panelTop, BorderLayout.NORTH);

        JPanel panelBottom = taoPanelBang();
        add(panelBottom, BorderLayout.CENTER);

        taiDuLieuVaoBang();
    }

    private JPanel taoPanelNhapLieu() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(componentColor),
                "Thông tin bàn ăn",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                FONT_NHAN,
                textColor));

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 4, 15, 15));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(createStyledLabel("Mã bàn:"));
        txtMaBan = createStyledTextField();
        txtMaBan.setEditable(false);
        fieldsPanel.add(txtMaBan);

        fieldsPanel.add(createStyledLabel("Tên bàn:"));
        txtTenBan = createStyledTextField();
        fieldsPanel.add(txtTenBan);

        fieldsPanel.add(createStyledLabel("Loại bàn:"));
        cmbLoaiBan = new JComboBox<>(new String[] { "Bàn nhỏ", "Bàn vừa", "Bàn lớn", "Phòng VIP" });
        cmbLoaiBan.setFont(FONT_TEXTFIELD);
        cmbLoaiBan.setBackground(componentColor);
        cmbLoaiBan.setForeground(textColor);
        fieldsPanel.add(cmbLoaiBan);

        fieldsPanel.add(createStyledLabel("Sức chứa:"));
        txtSucChua = createStyledTextField();
        fieldsPanel.add(txtSucChua);

        fieldsPanel.add(createStyledLabel("Trạng thái:"));
        txtTrangThai = createStyledTextField();
        txtTrangThai.setEditable(false);
        fieldsPanel.add(txtTrangThai);

        fieldsPanel.add(new JLabel());
        fieldsPanel.add(new JLabel());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        btnCapNhat = createStyledButton("Cập nhật", MAU_NUT_CAP_NHAT);
        btnCapNhat.addActionListener(e -> xuLyCapNhatBan());

        btnXoa = createStyledButton("Xoá", MAU_NUT_XOA);
        btnXoa.addActionListener(e -> xuLyXoaBan());

        btnLamMoi = createStyledButton("Làm mới", MAU_NUT_LAM_MOI);
        btnLamMoi.addActionListener(e -> lamMoiForm());

        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoPanelBang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        String[] columnNames = { "Mã bàn", "Tên bàn", "Khu vực", "Tầng", "Loại bàn", "Sức chứa", "Trạng thái" };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        setupTableStyle();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    hienThiDuLieuLenForm(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);

        tuyChinhScrollBar(scrollPane);

        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
        scrollPane.getViewport().setBackground(bgColor);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
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
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty())
                    return;
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
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty())
                    return;
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

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(FONT_NHAN);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(color.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(color);
                }
            }
        });
        return button;
    }

    private void setupTableStyle() {
        table.setBackground(MAU_NEN_ITEM);
        table.setForeground(textColor);
        table.setGridColor(MAU_LUOI_BANG);
        table.setRowHeight(40);
        table.setFont(FONT_BANG);
        table.setSelectionBackground(MAU_CHON_HANG);
        table.setSelectionForeground(textColor);

        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_ITEM);
        header.setForeground(textColor);
        header.setFont(FONT_HEADER_BANG);
        header.setPreferredSize(new Dimension(0, 40));

        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_LUOI_BANG));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setFont(FONT_HEADER_BANG);
                label.setForeground(textColor);
                label.setBackground(MAU_NEN_ITEM);
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);

                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, MAU_LUOI_BANG),
                        new EmptyBorder(10, 5, 10, 5)));
                return label;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // =========================================================================================
    // SWING WORKER: TẢI DỮ LIỆU VÀO BẢNG
    // =========================================================================================
    private void taiDuLieuVaoBang() {
        if (banAnService == null) return;

        // Hiển thị trạng thái đang tải
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"Đang tải dữ liệu...", "", "", "", "", "", ""});

        SwingWorker<List<BanAn>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BanAn> doInBackground() throws Exception {
                return banAnService.docDanhSachBan();
            }

            @Override
            protected void done() {
                try {
                    danhSachBanHienThi = get();
                    tableModel.setRowCount(0);
                    if (danhSachBanHienThi != null) {
                        for (BanAn ban : danhSachBanHienThi) {
                            tableModel.addRow(new Object[] {
                                    ban.getMaBan(),
                                    ban.getTenBan(),
                                    ban.getTenKhu(),
                                    ban.getTenTang(),
                                    ban.getLoaiBan(),
                                    ban.getSucChua(),
                                    ban.getTrangThai()
                            });
                        }
                    }
                } catch (Exception e) {
                    tableModel.setRowCount(0);
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CapNhatBan_UI.this, "Lỗi khi tải dữ liệu bàn ăn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void hienThiDuLieuLenForm(int row) {
        txtMaBan.setText(tableModel.getValueAt(row, 0).toString());
        txtTenBan.setText(tableModel.getValueAt(row, 1).toString());
        cmbLoaiBan.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        txtSucChua.setText(tableModel.getValueAt(row, 5).toString());
        txtTrangThai.setText(tableModel.getValueAt(row, 6).toString());
    }

    private void lamMoiForm() {
        txtMaBan.setText("");
        txtTenBan.setText("");
        txtSucChua.setText("");
        txtTrangThai.setText("");
        cmbLoaiBan.setSelectedIndex(0);
        table.clearSelection();
    }

    // =========================================================================================
    // SWING WORKER: CẬP NHẬT BÀN
    // =========================================================================================
    private void xuLyCapNhatBan() {
        String maBan = txtMaBan.getText().trim();
        if (maBan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tenBan = txtTenBan.getText().trim();
        String loaiBan = cmbLoaiBan.getSelectedItem() != null ? cmbLoaiBan.getSelectedItem().toString() : "";
        String sucChuaStr = txtSucChua.getText().trim();
        String trangThai = txtTrangThai.getText().trim();

        String maKhu = "K01";
        if (loaiBan.equals("Bàn nhỏ")) maKhu = "K01";
        else if (loaiBan.equals("Bàn vừa")) maKhu = "K02";
        else if (loaiBan.equals("Bàn lớn")) maKhu = "K03";
        else if (loaiBan.equals("Phòng VIP")) maKhu = "K03";

        try {
            int sucChua = Integer.parseInt(sucChuaStr);
            if (sucChua <= 0) throw new IllegalArgumentException("Sức chứa phải lớn hơn 0");

            BanAn banCapNhat = new BanAn(maBan, tenBan, loaiBan, sucChua, trangThai, maKhu);

            btnCapNhat.setEnabled(false);
            btnCapNhat.setText("Đang xử lý...");

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return banAnService.capNhatBan(banCapNhat);
                }

                @Override
                protected void done() {
                    btnCapNhat.setEnabled(true);
                    btnCapNhat.setText("Cập nhật");
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(CapNhatBan_UI.this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            taiDuLieuVaoBang();
                            lamMoiForm();
                        } else {
                            JOptionPane.showMessageDialog(CapNhatBan_UI.this, "Cập nhật thất bại (Lỗi Database).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(CapNhatBan_UI.this, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Sức chứa phải là số nguyên hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtSucChua.requestFocus();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            txtSucChua.requestFocus();
        }
    }

    // =========================================================================================
    // SWING WORKER: XÓA BÀN
    // =========================================================================================
    private void xuLyXoaBan() {
        String maBan = txtMaBan.getText().trim();
        if (maBan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!txtTrangThai.getText().equals("Bàn đang trống")) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể xóa bàn đang ở trạng thái 'Bàn đang trống'.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa '" + txtTenBan.getText() + "' không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            btnXoa.setEnabled(false);
            btnXoa.setText("Đang xóa...");

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return banAnService.xoaBan(maBan);
                }

                @Override
                protected void done() {
                    btnXoa.setEnabled(true);
                    btnXoa.setText("Xoá");
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(CapNhatBan_UI.this, "Xóa bàn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            taiDuLieuVaoBang();
                            lamMoiForm();
                        } else {
                            JOptionPane.showMessageDialog(CapNhatBan_UI.this, "Xóa bàn thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(CapNhatBan_UI.this, "Lỗi kết nối khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    public void chonBanDeCapNhat(String maBan) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).toString().equals(maBan)) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                hienThiDuLieuLenForm(i);
                return;
            }
        }
    }
}
package ui.nhanvien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.rmi.Naming;
import java.rmi.RemoteException;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import connect.ConfigManager;
import rmi_interfaces.INhanVien_Service;
import rmi_interfaces.IChucVu_Service;
import entity.NhanVien;
import entity.ChucVu;

public class ThemNhanVien_UI extends JPanel {

    private JTextField txtHoTen;
    private JTextField txtSDT;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JDateChooser dateChooserNgaySinh;
    private JComboBox<String> cmbGioiTinh;
    private JComboBox<ChucVu> cmbChucVu;
    private Image backgroundImage;
    private INhanVien_Service nhanVienService;
    private IChucVu_Service chucVuService;
    private JButton btnThemNV;
    private final Color bgColor = new Color(48, 52, 56);
    private final Color componentColor = new Color(124, 124, 124);
    private final Color textColor = Color.WHITE;

    private final Color MAU_NUT_CAP_NHAT = new Color(255, 193, 7);
    private final Color MAU_NUT_XOA = new Color(244, 67, 54);

    public ThemNhanVien_UI() {
        try {
            String url = ConfigManager.getRmiUrl();
            nhanVienService = (INhanVien_Service) Naming.lookup(url +"NhanVien_Service");
            chucVuService = (IChucVu_Service) Naming.lookup(url +"ChucVu_Service");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        setBackground(bgColor);
        setLayout(null);

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/img/vipbackground2.png")).getImage();
        } catch (Exception e) {
            backgroundImage = null;
        }

        JLabel lblTitle = new JLabel("THÊM NHÂN VIÊN");
        lblTitle.setForeground(textColor);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblTitle.setBounds(650, 34, 400, 62);
        add(lblTitle);

        JLabel lblHoTen = createStyledLabel("Họ tên:");
        lblHoTen.setBounds(200, 200, 160, 34);
        add(lblHoTen);
        txtHoTen = createStyledTextField();
        txtHoTen.setBounds(370, 197, 365, 45);
        add(txtHoTen);

        JLabel lblGioiTinh = createStyledLabel("Giới tính:");
        lblGioiTinh.setBounds(200, 280, 160, 34);
        add(lblGioiTinh);
        cmbGioiTinh = createStyledComboBox(new String[]{"Nam", "Nữ"});
        cmbGioiTinh.setBounds(370, 277, 365, 45);
        add(cmbGioiTinh);

        JLabel lblSDT = createStyledLabel("Số điện thoại:");
        lblSDT.setBounds(200, 360, 200, 34);
        add(lblSDT);
        txtSDT = createStyledTextField();
        txtSDT.setBounds(370, 357, 365, 45);
        add(txtSDT);

        JLabel lblEmail = createStyledLabel("Email:");
        lblEmail.setBounds(200, 440, 160, 34);
        add(lblEmail);
        txtEmail = createStyledTextField();
        txtEmail.setBounds(370, 437, 365, 45);
        add(txtEmail);

        JLabel lblNgaySinh = createStyledLabel("Ngày sinh:");
        lblNgaySinh.setBounds(850, 200, 160, 34);
        add(lblNgaySinh);
        dateChooserNgaySinh = new JDateChooser();
        dateChooserNgaySinh.setBounds(1020, 197, 365, 45);
        dateChooserNgaySinh.setDateFormatString("dd-MM-yyyy");
        dateChooserNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JButton calendarButton = dateChooserNgaySinh.getCalendarButton();
        calendarButton.setBackground(componentColor);
        calendarButton.setBorder(BorderFactory.createEmptyBorder());
        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTextField dateEditor = (JTextField) dateChooserNgaySinh.getDateEditor().getUiComponent();
        dateEditor.setBackground(componentColor);
        dateEditor.setBorder(new EmptyBorder(5, 10, 5, 10));
        dateEditor.setForeground(Color.WHITE);
        dateEditor.setCaretColor(Color.WHITE);
        dateEditor.setDisabledTextColor(Color.WHITE);
        dateEditor.setSelectedTextColor(Color.WHITE);
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

        dateChooserNgaySinh.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(() -> {
                    dateEditor.setForeground(Color.WHITE);
                });
            }
        });

        add(dateChooserNgaySinh);

        JLabel lblDiaChi = createStyledLabel("Địa chỉ:");
        lblDiaChi.setBounds(850, 280, 160, 34);
        add(lblDiaChi);
        txtDiaChi = createStyledTextField();
        txtDiaChi.setBounds(1020, 277, 365, 45);
        add(txtDiaChi);

        JLabel lblChucVu = createStyledLabel("Chức vụ:");
        lblChucVu.setBounds(850, 360, 160, 34);
        add(lblChucVu);

        cmbChucVu = new JComboBox<ChucVu>();
        setupComboBoxUI(cmbChucVu);
        cmbChucVu.setBounds(1020, 357, 315, 45);
        add(cmbChucVu);

        JButton btnThemChucVu = createIconButton("/img/add_32px.png", componentColor, 24);
        btnThemChucVu.setBounds(1020 + 315 + 5, 357, 45, 45);
        btnThemChucVu.addActionListener(e -> hienThiDialogThemChucVu());
        add(btnThemChucVu);

        btnThemNV = createStyledButton(" Thêm", "/img/add_32px.png");
        btnThemNV.setBounds(650, 550, 150, 40);
        add(btnThemNV);
        btnThemNV.addActionListener(e -> themNhanVien());

        JButton btnLamMoi = createStyledButton(" Làm mới", "/img/refresh_32px.png");
        btnLamMoi.setBounds(850, 550, 150, 40);
        add(btnLamMoi);
        btnLamMoi.addActionListener(e -> lamMoi());

        taiDuLieuChucVu();
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

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Segoe UI", Font.BOLD, 25));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(componentColor);
        textField.setForeground(textColor);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        textField.setBorder(new EmptyBorder(5, 10, 5, 10));
        textField.setCaretColor(Color.WHITE);
        textField.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        return textField;
    }

    private <T> void setupComboBoxUI(JComboBox<T> comboBox) {
        comboBox.setBackground(componentColor);
        comboBox.setForeground(textColor);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(componentColor);
                button.setForeground(Color.white);
                button.setContentAreaFilled(false);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(componentColor, 2),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        setupComboBoxUI(comboBox);
        return comboBox;
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(componentColor);
        button.setForeground(textColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledIcon = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledIcon));
        } catch (Exception e) {}

        return button;
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

    private void taiDuLieuChucVu() {
        if (chucVuService == null) {
            System.err.println("Không thể tải danh sách Chức vụ do chucVuService bị null.");
            return;
        }
        new SwingWorker<List<ChucVu>, Void>() {
            @Override
            protected List<ChucVu> doInBackground() throws Exception {
                return chucVuService.docDanhSachChucVu();
            }

            @Override
            protected void done() {
                try {
                    List<ChucVu> ds = get();
                    cmbChucVu.removeAllItems();
                    for (ChucVu cv : ds) cmbChucVu.addItem(cv);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    private void hienThiDialogThemChucVu() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Thêm chức vụ", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlHienCo = new JPanel(new BorderLayout(0, 5));
        pnlHienCo.setOpaque(false);
        JLabel lblHienCo = createStyledLabel("Chức vụ hiện có:");
        lblHienCo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        DefaultListModel<ChucVu> listModel = new DefaultListModel<>();
        for (int i = 0; i < cmbChucVu.getItemCount(); i++) {
            listModel.addElement(cmbChucVu.getItemAt(i));
        }

        JList<ChucVu> listHienCo = new JList<>(listModel);
        listHienCo.setBackground(componentColor);
        listHienCo.setForeground(textColor);
        listHienCo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        listHienCo.setSelectionBackground(MAU_NUT_CAP_NHAT.darker());
        listHienCo.setSelectionForeground(Color.WHITE);
        listHienCo.setBorder(new EmptyBorder(5, 10, 5, 10));

        JScrollPane scrollPane = new JScrollPane(listHienCo);
        scrollPane.setBorder(BorderFactory.createLineBorder(componentColor, 1));

        pnlHienCo.add(lblHienCo, BorderLayout.NORTH);
        pnlHienCo.add(scrollPane, BorderLayout.CENTER);
        pnlHienCo.setPreferredSize(new Dimension(0, 150));

        JPanel pnlThemMoi = new JPanel(new BorderLayout(0, 5));
        pnlThemMoi.setOpaque(false);
        JLabel lblThemMoi = createStyledLabel("Tên chức vụ mới:");
        lblThemMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JTextField txtTenChucVuMoi = createStyledTextField();

        pnlThemMoi.add(lblThemMoi, BorderLayout.NORTH);
        pnlThemMoi.add(txtTenChucVuMoi, BorderLayout.CENTER);

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlButton.setOpaque(false);

        JButton btnThemDialog = new JButton("Thêm");
        btnThemDialog.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnThemDialog.setBackground(MAU_NUT_CAP_NHAT);
        btnThemDialog.setForeground(textColor);
        btnThemDialog.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemDialog.setBorder(new EmptyBorder(8, 25, 8, 25));
        btnThemDialog.setFocusPainted(false);
        btnThemDialog.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnThemDialog.setBackground(MAU_NUT_CAP_NHAT.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btnThemDialog.setBackground(MAU_NUT_CAP_NHAT); }
        });

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnHuy.setBackground(MAU_NUT_XOA);
        btnHuy.setForeground(textColor);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.setBorder(new EmptyBorder(8, 25, 8, 25));
        btnHuy.setFocusPainted(false);
        btnHuy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnHuy.setBackground(MAU_NUT_XOA.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btnHuy.setBackground(MAU_NUT_XOA); }
        });

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
                    ChucVu cvTonTai = chucVuService.timChucVuTheoTen(tenMoi);
                    if (cvTonTai != null) {
                        return new Object[]{false, "exists", null};
                    } else {
                        String maMoi = chucVuService.sinhMaChucVuTuDong();
                        ChucVu cvMoi = new ChucVu(maMoi, tenMoi);
                        boolean themThanhCong = chucVuService.themChucVu(cvMoi);
                        return new Object[]{themThanhCong, "added", cvMoi};
                    }
                }

                @Override
                protected void done() {
                    btnThemDialog.setEnabled(true);
                    btnThemDialog.setText("Thêm");
                    try {
                        Object[] result = get();
                        boolean success = (Boolean) result[0];
                        String status = (String) result[1];
                        ChucVu cvMoi = (ChucVu) result[2];

                        if (status.equals("exists")) {
                            JOptionPane.showMessageDialog(dialog, "Chức vụ này đã tồn tại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                        } else if (success) {
                            cmbChucVu.addItem(cvMoi);
                            cmbChucVu.setSelectedItem(cvMoi);
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
            };
            worker.execute();
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void themNhanVien() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        java.util.Date utilDate = dateChooserNgaySinh.getDate();
        ChucVu chucVu = (ChucVu) cmbChucVu.getSelectedItem();

        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }

        if (!hoTen.matches("^[A-ZÀ-Ỹ][a-zà-ỹ]*(\\s[A-ZÀ-Ỹ][a-zà-ỹ]*)*$")) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng chỉ chứa chữ cái, phải viết hoa chữ cái đầu mỗi từ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }

        if (utilDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate ngaySinh = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (ngaySinh.plusYears(18).isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 18 tuổi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return;
        }

        if (!sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this," Địa chỉ email không hợp lệ.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        if (diaChi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtDiaChi.requestFocus();
            return;
        }

        if (chucVu == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final boolean gioiTinh = cmbGioiTinh.getSelectedItem().equals("Nam");
        final Date ngaySinhSQL = Date.valueOf(ngaySinh);
        final String finalHoTen = hoTen;
        final String finalSdt = sdt;
        final String finalEmail = email;
        final String finalDiaChi = diaChi;
        final ChucVu finalChucVu = chucVu;

        btnThemNV.setEnabled(false);
        btnThemNV.setText(" Đang thêm...");

        SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                String maNV_Moi = nhanVienService.getMaNhanVienTiepTheo();
                NhanVien nv = new NhanVien(maNV_Moi, finalHoTen, gioiTinh, finalSdt, finalEmail, ngaySinhSQL, finalDiaChi, finalChucVu);
                boolean isSuccess = nhanVienService.themNhanVien(nv);
                return new Object[]{isSuccess, maNV_Moi};
            }

            @Override
            protected void done() {
                btnThemNV.setEnabled(true);
                btnThemNV.setText(" Thêm");

                try {
                    Object[] result = get();
                    boolean isSuccess = (Boolean) result[0];
                    String maNV_Moi = (String) result[1];

                    if (isSuccess) {
                        JOptionPane.showMessageDialog(ThemNhanVien_UI.this,
                                "Thêm nhân viên '" + finalHoTen + "' (Mã: " + maNV_Moi + ") thành công!",
                                "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                        lamMoi();

                        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(ThemNhanVien_UI.this);
                        String tenChucVu = finalChucVu.getTenChucVu();
                        ThemTaiKhoan_UI themTaiKhoanDialog = new ThemTaiKhoan_UI(parentFrame, maNV_Moi, tenChucVu);
                        themTaiKhoanDialog.setVisible(true);

                        if (!themTaiKhoanDialog.isThemThanhCong()) {
                            xoaNhanVienChayNen(maNV_Moi);
                        }

                    } else {
                        JOptionPane.showMessageDialog(ThemNhanVien_UI.this, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ThemNhanVien_UI.this, "Đã xảy ra lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void xoaNhanVienChayNen(String maNV_Xoa) {
        SwingWorker<Boolean, Void> deleteWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return nhanVienService.xoaSachNhanVien(maNV_Xoa);
            }
            @Override
            protected void done() {
                try {
                    boolean xoaThanhCong = get();
                    if (xoaThanhCong) {
                        JOptionPane.showMessageDialog(ThemNhanVien_UI.this, "Đã tự xoá nhân viên khi tài khoản không được thêm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(ThemNhanVien_UI.this, "Lỗi! Không thể xoá nhân viên, vui lòng liên hệ quản trị viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ThemNhanVien_UI.this, "Lỗi! Không thể xoá nhân viên, vui lòng liên hệ quản trị viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        deleteWorker.execute();
    }

    private void lamMoi() {
        txtHoTen.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        dateChooserNgaySinh.setDate(null);
        cmbGioiTinh.setSelectedIndex(0);
        if (cmbChucVu.getItemCount() > 0) cmbChucVu.setSelectedIndex(0);
        txtHoTen.requestFocus();
    }
}
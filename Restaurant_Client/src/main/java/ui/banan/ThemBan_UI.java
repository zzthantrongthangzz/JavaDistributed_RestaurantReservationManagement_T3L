package ui.banan;

import rmi_interfaces.IBanAn_DAO;
import rmi_interfaces.IKhu_DAO;
import rmi_interfaces.ITang_DAO;
import entity.BanAn;
import entity.Khu;
import entity.Tang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.util.List;

public class ThemBan_UI extends JDialog {

    private JTextField txtMaBan;
    private JTextField txtTenBan;
    private JComboBox<String> cmbLoaiBan;
    private JTextField txtSucChua;
    private JComboBox<String> cmbTang;
    private JComboBox<String> cmbKhu;

    private IBanAn_DAO banAnDAO;
    private IKhu_DAO khuDAO;
    private ITang_DAO tangDAO;
    private Runnable onTableAdded;

    private final Color MAU_NEN = new Color(48, 52, 56);
    private final Color MAU_NEN_FORM = new Color(48, 52, 56);
    private final Color MAU_NEN_INPUT = new Color(60, 64, 68);
    private final Color MAU_VIEN_INPUT = new Color(70, 72, 87);
    private final Color MAU_CHU_TRANG = Color.WHITE;
    private final Color MAU_O_Nhap = Color.WHITE;
    private final Color MAU_NUT_THEM = new Color(76, 175, 80);
    private final Color MAU_NUT_THEM_HOVER = new Color(39, 174, 96);
    private final Color MAU_NUT_HUY = new Color(231, 76, 60);
    private final Color MAU_NUT_HUY_HOVER = new Color(192, 57, 43);
    private final Color MAU_VIEN_DUOI = new Color(70, 72, 87);

    private final Color MAU_NUT_CAP_NHAT = new Color(255, 193, 7);
    private final Color MAU_NUT_XOA = new Color(244, 67, 54);

    private final Dimension KICH_THUOC_O_NHAP = new Dimension(400, 40);
    private final Dimension KICH_THUOC_LOAI_BAN = new Dimension(400, 40);
    private final Dimension KICH_THUOC_NUT = new Dimension(150, 45);

    private final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 32);
    private final Font FONT_NHAN = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 16);

    public ThemBan_UI(Frame parent, Runnable onTableAdded) {
        super(parent, "Thêm bàn ăn", true);
        this.onTableAdded = onTableAdded;

        try {
            banAnDAO = (IBanAn_DAO) Naming.lookup("rmi://localhost:1099/BanAn_DAO");
            khuDAO = (IKhu_DAO) Naming.lookup("rmi://localhost:1099/Khu_DAO");
            tangDAO = (ITang_DAO) Naming.lookup("rmi://localhost:1099/Tang_DAO");
        } catch (Exception e) {
            e.printStackTrace();
        }

        khoiTaoGiaoDien();

        capNhatTenBanVaSucChua();
        loadDuLieuTang();

        setSize(550, 680);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    public ThemBan_UI() { this(null, null); }
    public ThemBan_UI(Frame parent) { this(parent, null); }

    private void khoiTaoGiaoDien() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        mainPanel.add(taoPanelTieuDe(), BorderLayout.NORTH);
        mainPanel.add(taoPanelForm(), BorderLayout.CENTER);
        mainPanel.add(taoPanelNut(), BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(MAU_NEN);

        JLabel lblTieuDe = new JLabel("THÊM BÀN ĂN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        panel.add(lblTieuDe);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN_DUOI),
                new EmptyBorder(0, 0, 15, 0)
        ));

        return panel;
    }

    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        txtMaBan = taoFieldCoNhan(panel, "Mã bàn:", false, "");
        txtMaBan.setEditable(false);
        txtMaBan.setFocusable(false);
        txtMaBan.setBackground(MAU_NEN_INPUT.darker());
        taoMaBanTuDong();

        panel.add(taoPanelComboVoiNut("Tầng:",
                cmbTang = taoComboBox(new String[]{"Đang tải..."}),
                e -> hienThiDialogThemTang()
        ));
        cmbTang.addActionListener(e -> capNhatDanhSachKhu());

        panel.add(taoPanelComboVoiNut("Khu:",
                cmbKhu = taoComboBox(new String[]{"Vui lòng chọn tầng"}),
                e -> hienThiDialogThemKhu()
        ));

        cmbLoaiBan = taoComboBoxCoNhan(panel, "Loại bàn:", new String[]{"Bàn nhỏ", "Bàn vừa", "Bàn lớn", "Phòng VIP"});
        cmbLoaiBan.addActionListener(e -> capNhatTenBanVaSucChua());

        txtTenBan = taoFieldCoNhan(panel, "Tên bàn:", false, "");
        txtTenBan.setEditable(false);
        txtTenBan.setFocusable(false);
        txtTenBan.setBackground(MAU_NEN_INPUT.darker());

        txtSucChua = taoFieldCoNhan(panel, "Sức chứa:", false, "");
        txtSucChua.setEditable(false);
        txtSucChua.setFocusable(false);
        txtSucChua.setBackground(MAU_NEN_INPUT.darker());

        return panel;
    }

    private JPanel taoPanelComboVoiNut(String nhan, JComboBox<String> comboBox, java.awt.event.ActionListener buttonAction) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(nhan);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_O_Nhap);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        innerPanel.setBackground(MAU_NEN_FORM);
        innerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        innerPanel.add(comboBox);
        innerPanel.add(Box.createHorizontalStrut(5));

        JButton button = createIconButton("/img/add_32px.png", MAU_NEN_INPUT, 24);
        button.setPreferredSize(new Dimension(40, 40));
        button.setMaximumSize(new Dimension(40, 40));
        button.addActionListener(buttonAction);
        innerPanel.add(button);

        panel.add(innerPanel);
        panel.add(Box.createVerticalStrut(8));

        return panel;
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

    private JTextField taoFieldCoNhan(JPanel panel, String nhan, boolean batBuoc, String placeholder) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(MAU_NEN_FORM);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(nhan);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_O_Nhap);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField textField = taoTextField(placeholder);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(textField);
        fieldPanel.add(Box.createVerticalStrut(8));

        panel.add(fieldPanel);
        return textField;
    }

    private JTextField taoTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setPreferredSize(KICH_THUOC_O_NHAP);
        textField.setMaximumSize(KICH_THUOC_O_NHAP);
        textField.setFont(FONT_O_NHAP);
        textField.setBackground(MAU_NEN_INPUT);
        textField.setForeground(MAU_CHU_TRANG);
        textField.setCaretColor(MAU_CHU_TRANG);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JComboBox<String> taoComboBoxCoNhan(JPanel panel, String nhan, String[] items) {
        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.Y_AXIS));
        comboPanel.setBackground(MAU_NEN_FORM);
        comboPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(nhan);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_CHU_TRANG);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> comboBox = taoComboBox(items);
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboPanel.add(label);
        comboPanel.add(Box.createVerticalStrut(5));
        comboPanel.add(comboBox);
        comboPanel.add(Box.createVerticalStrut(8));

        panel.add(comboPanel);
        return comboBox;
    }

    private JComboBox<String> taoComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(FONT_O_NHAP);
        comboBox.setBackground(MAU_NEN_INPUT);
        comboBox.setForeground(MAU_CHU_TRANG);
        comboBox.setBorder(BorderFactory.createLineBorder(MAU_NEN_INPUT, 1));
        return comboBox;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(MAU_NEN);

        JButton btnThem = taoNut("Thêm", MAU_NUT_THEM, MAU_NUT_THEM_HOVER);
        JButton btnHuy = taoNut("Hủy", MAU_NUT_HUY, MAU_NUT_HUY_HOVER);

        btnThem.addActionListener(e -> xuLyThemBan());
        btnHuy.addActionListener(e -> dispose());

        panel.add(btnThem);
        panel.add(btnHuy);

        return panel;
    }

    private JButton taoNut(String text, Color mauNen, Color mauHover) {
        JButton button = new JButton(text);
        button.setPreferredSize(KICH_THUOC_NUT);
        button.setFont(FONT_NUT);
        button.setBackground(mauNen);
        button.setForeground(MAU_CHU_TRANG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(mauHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(mauNen);
            }
        });
        return button;
    }

    private void loadDuLieuTang() {
        try {
            List<String> dsTang = tangDAO.docDanhSachTenTang();
            dsTang.remove("Tất cả");

            cmbTang.setModel(new DefaultComboBoxModel<>(dsTang.toArray(new String[0])));
            capNhatDanhSachKhu();
        } catch (Exception e) {
            cmbTang.setModel(new DefaultComboBoxModel<>(new String[]{"Lỗi tải tầng"}));
        }
    }

    private void capNhatDanhSachKhu() {
        String tenTang = (String) cmbTang.getSelectedItem();
        if (tenTang == null || tenTang.equals("Lỗi tải tầng")) {
            cmbKhu.setModel(new DefaultComboBoxModel<>(new String[]{"Vui lòng chọn tầng"}));
            return;
        }

        try {
            List<String> dsKhu = banAnDAO.docDanhSachTenKhuTheoTang(tenTang);
            dsKhu.remove("Tất cả");

            if (dsKhu.isEmpty()) {
                cmbKhu.setModel(new DefaultComboBoxModel<>(new String[]{"Không có khu"}));
            } else {
                cmbKhu.setModel(new DefaultComboBoxModel<>(dsKhu.toArray(new String[0])));
            }
        } catch (Exception e) {
            cmbKhu.setModel(new DefaultComboBoxModel<>(new String[]{"Lỗi tải khu"}));
        }
    }

    private void xuLyThemBan() {
        try {
            String maBan = txtMaBan.getText().trim();
            String tenBan = txtTenBan.getText().trim();
            String loaiBan = (String) cmbLoaiBan.getSelectedItem();
            String sucChuaStr = txtSucChua.getText().trim();
            String trangThai = "Bàn đang trống";

            String tenKhu = (String) cmbKhu.getSelectedItem();
            if (tenKhu == null || tenKhu.startsWith("Vui lòng") || tenKhu.startsWith("Không có")) {
                throw new IllegalArgumentException("Vui lòng chọn Khu vực hợp lệ.");
            }
            String maKhu = khuDAO.layMaKhuTheoTen(tenKhu);
            if (maKhu == null) throw new IllegalArgumentException("Khu vực không tồn tại.");

            int sucChua;
            try {
                sucChua = Integer.parseInt(sucChuaStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Sức chứa phải là số nguyên.");
            }

            BanAn ban = new BanAn(maBan, tenBan, loaiBan, sucChua, trangThai, maKhu);

            if (banAnDAO.themBanMoi(ban)) {
                JOptionPane.showMessageDialog(this, "Thêm bàn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                if (onTableAdded != null) onTableAdded.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng mã hoặc lỗi DB).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Dữ liệu sai", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void taoMaBanTuDong() {
        try {
            List<BanAn> danhSachBan = banAnDAO.docDanhSachBan();
            int soThuTu = 1;

            if (!danhSachBan.isEmpty()) {
                for (BanAn ban : danhSachBan) {
                    String maBan = ban.getMaBan();
                    if (maBan.startsWith("MB")) {
                        try {
                            int so = Integer.parseInt(maBan.substring(2));
                            if (so >= soThuTu) {
                                soThuTu = so + 1;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
            String maBan = String.format("MB%06d", soThuTu);
            txtMaBan.setText(maBan);
        } catch (Exception e) {
            txtMaBan.setText("MB000001");
        }
    }

    private void capNhatTenBanVaSucChua() {
        try {
            String loaiBanDuocChon = (String) cmbLoaiBan.getSelectedItem();
            if (loaiBanDuocChon == null) {
                return;
            }

            List<BanAn> danhSachBan = banAnDAO.docDanhSachBan();
            int soPhongVipHienCo = 0;
            int soBanThuongHienCo = 0;

            for (BanAn ban : danhSachBan) {
                String loaiBanHienTai = ban.getLoaiBan();
                if ("Phòng VIP".equals(loaiBanHienTai)) {
                    soPhongVipHienCo++;
                } else if ("Bàn nhỏ".equals(loaiBanHienTai) ||
                        "Bàn vừa".equals(loaiBanHienTai) ||
                        "Bàn lớn".equals(loaiBanHienTai)) {
                    soBanThuongHienCo++;
                }
            }

            String tenBan;
            if ("Phòng VIP".equals(loaiBanDuocChon)) {
                int soThuTuVIP = soPhongVipHienCo + 1;
                tenBan = String.format("Phòng VIP %02d", soThuTuVIP);
            } else {
                int soThuTuBanThuong = soBanThuongHienCo + 1;
                tenBan = String.format("Bàn %03d", soThuTuBanThuong);
            }

            txtTenBan.setText(tenBan);

            int sucChua;
            switch (loaiBanDuocChon) {
                case "Bàn nhỏ":
                    sucChua = 4;
                    break;
                case "Bàn vừa":
                    sucChua = 8;
                    break;
                case "Bàn lớn":
                    sucChua = 15;
                    break;
                case "Phòng VIP":
                    sucChua = 30;
                    break;
                default:
                    sucChua = 0;
            }
            txtSucChua.setText(String.valueOf(sucChua));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi cập nhật tên bàn và sức chứa: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiDialogThemTang() {
        JDialog dialog = new JDialog(this, "Thêm Tầng Mới", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(MAU_NEN);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlForm = new JPanel();
        pnlForm.setOpaque(false);
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));

        JLabel lblDsTang = new JLabel("Danh sách tầng hiện có:");
        lblDsTang.setFont(FONT_NHAN);
        lblDsTang.setForeground(MAU_CHU_TRANG);
        lblDsTang.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblDsTang);
        pnlForm.add(Box.createVerticalStrut(5));

        DefaultListModel<String> modelList = new DefaultListModel<>();
        ComboBoxModel<String> modelCombo = cmbTang.getModel();
        for (int i = 0; i < modelCombo.getSize(); i++) {
            modelList.addElement(modelCombo.getElementAt(i));
        }

        JList<String> listTangHienCo = new JList<>(modelList);
        listTangHienCo.setBackground(MAU_NEN_INPUT);
        listTangHienCo.setForeground(MAU_CHU_TRANG);
        listTangHienCo.setFont(FONT_O_NHAP);
        listTangHienCo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(listTangHienCo);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.getViewport().setBackground(MAU_NEN_INPUT);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT));

        pnlForm.add(scrollPane);
        pnlForm.add(Box.createVerticalStrut(20));
        JPanel pnlThemMoi = new JPanel(new BorderLayout(0, 5));
        pnlThemMoi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        pnlThemMoi.setOpaque(false);
        pnlThemMoi.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblThemMoi = new JLabel("Tên Tầng Mới:");
        lblThemMoi.setFont(FONT_NHAN);
        lblThemMoi.setForeground(MAU_CHU_TRANG);
        JTextField txtTenTangMoi = taoTextField("");

        pnlThemMoi.add(lblThemMoi, BorderLayout.NORTH);
        pnlThemMoi.add(txtTenTangMoi, BorderLayout.CENTER);

        pnlForm.add(pnlThemMoi);
        JPanel pnlButton = taoPanelNutDialog(
                e -> {
                    String tenMoi = txtTenTangMoi.getText().trim();
                    if (tenMoi.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Tên tầng không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        if (tangDAO.timTangTheoTen(tenMoi) != null) {
                            JOptionPane.showMessageDialog(dialog, "Tầng này đã tồn tại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                        } else {
                            String maMoi = tangDAO.sinhMaTangTuDong();
                            Tang tangMoi = new Tang(maMoi, tenMoi);

                            if(tangDAO.themTang(tangMoi)) {
                                loadDuLieuTang();
                                cmbTang.setSelectedItem(tenMoi);
                                JOptionPane.showMessageDialog(dialog, "Đã thêm tầng mới!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                                dialog.dispose();
                            } else {
                                JOptionPane.showMessageDialog(dialog, "Thêm tầng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm tầng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                },
                e -> dialog.dispose()
        );
        mainPanel.add(pnlForm, BorderLayout.CENTER);
        mainPanel.add(pnlButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void hienThiDialogThemKhu() {
        JDialog dialog = new JDialog(this, "Thêm Khu Mới", true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(MAU_NEN);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlForm = new JPanel();
        pnlForm.setOpaque(false);
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));

        JLabel lblChonTang = new JLabel("Thêm khu vào tầng:");
        lblChonTang.setFont(FONT_NHAN);
        lblChonTang.setForeground(MAU_CHU_TRANG);
        lblChonTang.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblChonTang);

        JComboBox<String> cmbChonTang = taoComboBox(new String[]{});
        DefaultComboBoxModel<String> modelTang = new DefaultComboBoxModel<>();
        for (int i = 0; i < cmbTang.getItemCount(); i++) {
            modelTang.addElement(cmbTang.getItemAt(i));
        }
        cmbChonTang.setModel(modelTang);
        cmbChonTang.setSelectedItem(cmbTang.getSelectedItem());

        cmbChonTang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cmbChonTang.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlForm.add(cmbChonTang);
        pnlForm.add(Box.createVerticalStrut(15));

        JLabel lblThemMoi = new JLabel("Tên Khu Mới:");
        lblThemMoi.setFont(FONT_NHAN);
        lblThemMoi.setForeground(MAU_CHU_TRANG);
        lblThemMoi.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtTenKhuMoi = taoTextField("");
        txtTenKhuMoi.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlForm.add(lblThemMoi);
        pnlForm.add(txtTenKhuMoi);

        JPanel pnlButton = taoPanelNutDialog(
                e -> {
                    String tenKhuMoi = txtTenKhuMoi.getText().trim();
                    String tenTangDuocChon = (String) cmbChonTang.getSelectedItem();

                    if (tenKhuMoi.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Tên khu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (tenTangDuocChon == null) {
                        JOptionPane.showMessageDialog(dialog, "Bạn phải chọn một tầng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        if (khuDAO.timKhuTheoTen(tenKhuMoi) != null) {
                            JOptionPane.showMessageDialog(dialog, "Tên khu này đã tồn tại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                        } else {
                            Tang tang = tangDAO.timTangTheoTen(tenTangDuocChon);
                            if (tang == null) {
                                JOptionPane.showMessageDialog(dialog, "Không tìm thấy tầng đã chọn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            String maKhuMoi = khuDAO.sinhMaKhuTuDong();
                            Khu khuMoi = new Khu(maKhuMoi, tenKhuMoi, tang.getMaTang());

                            if(khuDAO.themKhu(khuMoi)) {
                                capNhatDanhSachKhu();
                                cmbKhu.setSelectedItem(tenKhuMoi);
                                JOptionPane.showMessageDialog(dialog, "Đã thêm khu mới!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                                dialog.dispose();
                            } else {
                                JOptionPane.showMessageDialog(dialog, "Thêm khu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm khu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                },
                e -> dialog.dispose()
        );

        mainPanel.add(pnlForm, BorderLayout.CENTER);
        mainPanel.add(pnlButton, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel taoPanelNutDialog(java.awt.event.ActionListener themAction, java.awt.event.ActionListener huyAction) {
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlButton.setOpaque(false);

        JButton btnThem = new JButton("Thêm");
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnThem.setBackground(MAU_NUT_THEM);
        btnThem.setForeground(MAU_CHU_TRANG);
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.setBorder(new EmptyBorder(8, 25, 8, 25));
        btnThem.setFocusPainted(false);
        btnThem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnThem.setBackground(MAU_NUT_CAP_NHAT.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btnThem.setBackground(MAU_NUT_CAP_NHAT); }
        });

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnHuy.setBackground(MAU_NUT_XOA);
        btnHuy.setForeground(MAU_CHU_TRANG);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.setBorder(new EmptyBorder(8, 25, 8, 25));
        btnHuy.setFocusPainted(false);
        btnHuy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnHuy.setBackground(MAU_NUT_XOA.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btnHuy.setBackground(MAU_NUT_XOA); }
        });

        btnThem.addActionListener(themAction);
        btnHuy.addActionListener(huyAction);

        pnlButton.add(btnThem);
        pnlButton.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlButton.add(btnHuy);

        return pnlButton;
    }
}
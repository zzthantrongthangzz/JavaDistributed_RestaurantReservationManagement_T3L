package ui.banan;

import dao_impl.HoaDon_DAO;
import dao_impl.HoaDon_Ban_DAO;
import dao_impl.KhachHang_DAO;
import entity.BanAn;
import entity.HoaDon;
import entity.KhachHang;
import ui.Auth;
import entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ui.khachhang.ThemKhachHang_UI;

public class DatBanNgay_UI extends JDialog {

    private JTextField txtSoDienThoai;
    private JTextField txtMaKH;
    private JTextField txtHoTen;
    private JTextField txtGioiTinh;
    private JTextField txtTichDiem;
    private JButton btnKiemTra;
    private JButton btnDatBan;
    private JButton btnHuy;

    private final KhachHang_DAO khachHangDAO;
    private final HoaDon_DAO hoaDonDAO;
    private final HoaDon_Ban_DAO hoaDonBanDAO;

    private final List<BanAn> danhSachBanChon;

    private KhachHang khachHangHienTai;
    private final Frame parentFrame;

    private final Color MAU_NEN = new Color(26, 28, 32);
    private final Color MAU_NEN_FORM = new Color(32, 35, 40);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_NEN_INPUT_DISABLED = new Color(38, 41, 47);
    private final Color MAU_VIEN_INPUT = new Color(60, 65, 73);
    private final Color MAU_VIEN_INPUT_FOCUS = new Color(79, 134, 247);
    private final Color MAU_CHU_TRANG = new Color(240, 242, 245);
    private final Color MAU_CHU_XAM = new Color(155, 160, 170);
    private final Color MAU_CHU_XAM_NHAT = new Color(115, 120, 130);
    private final Color MAU_NUT_KIEM_TRA = new Color(79, 134, 247);
    private final Color MAU_NUT_KIEM_TRA_HOVER = new Color(65, 115, 220);
    private final Color MAU_NUT_DAT_BAN = new Color(34, 197, 94);
    private final Color MAU_NUT_DAT_BAN_HOVER = new Color(22, 163, 74);
    private final Color MAU_NUT_HUY = new Color(239, 68, 68);
    private final Color MAU_NUT_HUY_HOVER = new Color(220, 38, 38);
    private final Color MAU_VIEN_DUOI = new Color(50, 54, 61);
    private final Color MAU_ACCENT = new Color(79, 134, 247);

    private final Dimension KICH_THUOC_NUT = new Dimension(160, 44);

    private final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 28);
    private final Font FONT_TIEU_DE_PHU = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 17);

    // Khởi tạo giao diện đặt bàn ngay
    public DatBanNgay_UI(Frame parent, List<BanAn> dsBan) {
        super(parent, "Đặt bàn ngay", true);
        this.parentFrame = parent;
        this.danhSachBanChon = dsBan;
        this.khachHangDAO = new KhachHang_DAO();
        this.hoaDonDAO = new HoaDon_DAO();
        this.hoaDonBanDAO = new HoaDon_Ban_DAO();
        this.khachHangHienTai = null;

        khoiTaoGiaoDien();
        setSize(620, 820);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    public DatBanNgay_UI(Frame parent, BanAn ban) {
        this(parent, new ArrayList<>(Arrays.asList(ban)));
    }

    // Thiết lập bố cục giao diện
    private void khoiTaoGiaoDien() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        mainPanel.add(taoPanelTieuDe(), BorderLayout.NORTH);
        mainPanel.add(taoPanelForm(), BorderLayout.CENTER);
        mainPanel.add(taoPanelNut(), BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    // Tạo tiêu đề hiển thị danh sách bàn được chọn
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN);
        panel.setBorder(new EmptyBorder(35, 50, 25, 50));

        JLabel lblTieuDe = new JLabel("ĐẶT BÀN NGAY");
        lblTieuDe.setFont(FONT_TIEU_DE);
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        StringBuilder tenBanSb = new StringBuilder();
        for (BanAn b : danhSachBanChon) {
            if (tenBanSb.length() > 0)
                tenBanSb.append(", ");

            String tenRutGon = b.getTenBan().replace("Bàn", "").trim();
            tenBanSb.append(tenRutGon);
        }

        String strHienThi = tenBanSb.toString();
        if (strHienThi.length() > 45) {
            strHienThi = strHienThi.substring(0, 42) + "...";
        }

        JLabel lblThongTinBan = new JLabel("Bàn: " + strHienThi);
        lblThongTinBan.setFont(FONT_TIEU_DE_PHU);
        lblThongTinBan.setForeground(MAU_CHU_XAM);
        lblThongTinBan.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTieuDe);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblThongTinBan);

        return panel;
    }

    // Tạo form nhập liệu và hiển thị thông tin khách hàng
    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel pTimKiem = taoPanelTimKiem();
        panel.add(pTimKiem);
        panel.add(Box.createVerticalStrut(25));

        JSeparator separator = new JSeparator();
        separator.setForeground(MAU_VIEN_DUOI);
        separator.setBackground(MAU_VIEN_DUOI);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(25));

        JLabel lblThongTin = new JLabel("Thông tin khách hàng");
        lblThongTin.setFont(FONT_SECTION);
        lblThongTin.setForeground(MAU_CHU_TRANG);
        lblThongTin.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblThongTin);
        panel.add(Box.createVerticalStrut(18));

        txtMaKH = taoFieldCoNhan(panel, "Mã khách hàng", false);
        txtMaKH.setEditable(false);
        txtMaKH.setBackground(MAU_NEN_INPUT_DISABLED);
        txtMaKH.setForeground(MAU_CHU_XAM);

        txtHoTen = taoFieldCoNhan(panel, "Họ và tên", false);
        txtHoTen.setEditable(false);
        txtHoTen.setBackground(MAU_NEN_INPUT_DISABLED);
        txtHoTen.setForeground(MAU_CHU_XAM);

        txtGioiTinh = taoFieldCoNhan(panel, "Giới tính", false);
        txtGioiTinh.setEditable(false);
        txtGioiTinh.setBackground(MAU_NEN_INPUT_DISABLED);
        txtGioiTinh.setForeground(MAU_CHU_XAM);

        txtTichDiem = taoFieldCoNhan(panel, "Điểm tích lũy", false);
        txtTichDiem.setEditable(false);
        txtTichDiem.setBackground(MAU_NEN_INPUT_DISABLED);
        txtTichDiem.setForeground(MAU_CHU_XAM);

        return panel;
    }

    // Tạo panel tìm kiếm khách hàng theo số điện thoại
    private JPanel taoPanelTimKiem() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("Số điện thoại khách hàng");
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_CHU_TRANG);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputPanel = new JPanel(new BorderLayout(12, 0));
        inputPanel.setBackground(MAU_NEN_FORM);
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtSoDienThoai = taoTextField("Nhập số điện thoại (10 chữ số)");

        btnKiemTra = taoNut("Kiểm tra", MAU_NUT_KIEM_TRA, MAU_NUT_KIEM_TRA_HOVER);
        btnKiemTra.setPreferredSize(new Dimension(130, 42));
        btnKiemTra.addActionListener(e -> xuLyKiemTraKhachHang());
        txtSoDienThoai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnKiemTra.doClick();
            }
        });

        inputPanel.add(txtSoDienThoai, BorderLayout.CENTER);
        inputPanel.add(btnKiemTra, BorderLayout.EAST);

        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(12));

        return panel;
    }

    private JTextField taoFieldCoNhan(JPanel panel, String nhan, boolean batBuoc) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(MAU_NEN_FORM);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(nhan);
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_CHU_TRANG);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField textField = taoTextField("");
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(7));
        fieldPanel.add(textField);
        fieldPanel.add(Box.createVerticalStrut(12));

        panel.add(fieldPanel);
        return textField;
    }

    private JTextField taoTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(400, 42));
        textField.setFont(FONT_O_NHAP);
        textField.setBackground(MAU_NEN_INPUT);
        textField.setForeground(MAU_CHU_TRANG);
        textField.setCaretColor(MAU_ACCENT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true),
                new EmptyBorder(8, 14, 8, 14)));

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MAU_VIEN_INPUT_FOCUS, 2, true),
                        new EmptyBorder(7, 13, 7, 13)));
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true),
                        new EmptyBorder(8, 14, 8, 14)));
            }
        });

        return textField;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(MAU_NEN);
        panel.setBorder(new EmptyBorder(25, 50, 35, 50));

        btnDatBan = taoNut("Đặt bàn", MAU_NUT_DAT_BAN, MAU_NUT_DAT_BAN_HOVER);
        btnDatBan.setEnabled(false);
        btnDatBan.addActionListener(e -> xuLyDatBan());

        btnHuy = taoNut("Hủy", MAU_NUT_HUY, MAU_NUT_HUY_HOVER);
        btnHuy.addActionListener(e -> dispose());

        panel.add(btnDatBan);
        panel.add(btnHuy);

        return panel;
    }

    private JButton taoNut(String text, Color mauNen, Color mauHover) {
        JButton button = new JButton(text);
        button.setPreferredSize(KICH_THUOC_NUT);
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
                if (button.isEnabled()) {
                    button.setBackground(mauHover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(mauNen);
                } else {
                    button.setBackground(new Color(60, 65, 73));
                }
            }
        });

        button.addPropertyChangeListener("enabled", evt -> {
            if (!button.isEnabled()) {
                button.setBackground(new Color(60, 65, 73));
                button.setForeground(MAU_CHU_XAM_NHAT);
            } else {
                button.setBackground(mauNen);
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }

    // Kiểm tra sự tồn tại của khách hàng trong hệ thống
    private void xuLyKiemTraKhachHang() {
        String soDienThoai = txtSoDienThoai.getText().trim();

        if (soDienThoai.isEmpty()) {
            hienThiLoi("Vui lòng nhập số điện thoại!");
            return;
        }

        if (!soDienThoai.matches("^0\\d{9}$")) {
            hienThiLoi("Số điện thoại không hợp lệ!\nPhải có 10 chữ số và bắt đầu bằng 0");
            return;
        }

        try {
            KhachHang kh = khachHangDAO.timKhachHangTheoSDT(soDienThoai);

            if (kh != null) {
                khachHangHienTai = kh;
                hienThiThongTinKhachHang(kh);
                btnDatBan.setEnabled(true);
                hienThiThanhCong("Tìm thấy khách hàng!");
            } else {
                xoaThongTinKhachHang();
                btnDatBan.setEnabled(false);
                hienThiCanhBao("Không tìm thấy SĐT. Vui lòng thêm khách hàng mới.");
                xuLyThemKhachHangMoi(soDienThoai);
            }
        } catch (Exception e) {
            hienThiLoi("Lỗi khi tìm kiếm khách hàng:\n" + e.getMessage());
        }
    }

    // Điền thông tin khách hàng vào các trường text
    private void hienThiThongTinKhachHang(KhachHang kh) {
        txtMaKH.setText(kh.getMaKhachHang());
        txtHoTen.setText(kh.getHoTen());
        txtGioiTinh.setText(kh.isGioiTinh() ? "Nam" : "Nữ");
        txtTichDiem.setText(String.valueOf(kh.getTichDiem()));
    }

    private void xoaThongTinKhachHang() {
        txtMaKH.setText("");
        txtHoTen.setText("");
        txtGioiTinh.setText("");
        txtTichDiem.setText("");
        khachHangHienTai = null;
    }

    // Mở giao diện thêm khách hàng mới nếu chưa tồn tại
    private void xuLyThemKhachHangMoi(String soDienThoai) {
        ThemKhachHang_UI themKhachHangUI = new ThemKhachHang_UI(
                (Frame) SwingUtilities.getWindowAncestor(this),
                soDienThoai,
                () -> {
                    txtSoDienThoai.setText(soDienThoai);
                    xuLyKiemTraKhachHang();
                });
        themKhachHangUI.setVisible(true);
    }

    // Xử lý logic tạo hóa đơn và đặt bàn cho khách
    private void xuLyDatBan() {
        if (khachHangHienTai == null) {
            hienThiLoi("Vui lòng kiểm tra thông tin khách hàng trước!");
            return;
        }

        StringBuilder tenBanSb = new StringBuilder();
        for (BanAn b : danhSachBanChon) {
            if (tenBanSb.length() > 0)
                tenBanSb.append(", ");
            tenBanSb.append(b.getTenBan());
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Xác nhận đặt %d bàn (%s) cho khách hàng %s?",
                        danhSachBanChon.size(), tenBanSb.toString(), khachHangHienTai.getHoTen()),
                "Xác nhận đặt bàn",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            NhanVien nhanVienHienTai = Auth.getCurrentNhanVien();
            if (nhanVienHienTai == null) {
                hienThiLoi("Lỗi: Không tìm thấy thông tin nhân viên đang đăng nhập.\nVui lòng đăng nhập lại.");
                return;
            }

            String maHoaDonMoi = hoaDonDAO.sinhMaHoaDonTuDong();

            HoaDon hoaDonMoi = new HoaDon(
                    maHoaDonMoi,
                    "Chưa thanh toán",
                    new Date(),
                    BigDecimal.ZERO,
                    nhanVienHienTai.getMaNhanVien(),
                    null,
                    khachHangHienTai.getMaKhachHang(),
                    null,
                    null,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO);

            boolean themThanhCong = hoaDonDAO.themHoaDon(hoaDonMoi);

            if (themThanhCong) {
                for (BanAn ban : danhSachBanChon) {
                    hoaDonBanDAO.themHoaDon_Ban(maHoaDonMoi, ban.getMaBan());
                }

                hienThiThanhCong("Đặt bàn thành công! Vui lòng thêm món.");

                this.dispose();

                DatMonChoBan_UI themMonUI = new DatMonChoBan_UI(
                        parentFrame,
                        danhSachBanChon,
                        hoaDonMoi);
                themMonUI.setVisible(true);

            } else {
                hienThiLoi("Đã xảy ra lỗi khi tạo hóa đơn. Vui lòng thử lại.");
            }
        }
    }

    private void hienThiLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void hienThiCanhBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    private void hienThiThanhCong(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
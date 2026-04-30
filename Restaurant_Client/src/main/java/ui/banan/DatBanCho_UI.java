package ui.banan;

import entity.*;
import rmi_interfaces.IBanAn_Service;
import rmi_interfaces.IKhachHang_Service;
import ui.khachhang.ThemKhachHang_UI;
import rmi_interfaces.IHoaDon_Service;
import ui.Auth;
import ui.PhieuDatBanPDF;
import rmi_interfaces.IPhieuDatBan_Service;
import rmi_interfaces.IPhieuDatBan_Ban_Service;
import rmi_interfaces.IChiTietPhieuDatBan_Service;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.rmi.Naming;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;

public class DatBanCho_UI extends JDialog {

    private JTextField txtSoDienThoai;
    private JTextField txtMaKH;
    private JTextField txtHoTen;
    private JTextField txtGioiTinh;
    private JTextField txtTichDiem;
    private JTextField txtGhiChu;
    private JButton btnKiemTra;
    private JButton btnDatBan;
    private JButton btnHuy;
    private Date ngayNhanTuTrangChu;
    private JTextField txtNgayNhan;
    private JSpinner spinGio;
    private JSpinner spinPhut;

    private IKhachHang_Service khachHangDAO;
    private IHoaDon_Service hoaDonDAO;
    private IBanAn_Service banAnDAO;
    private List<BanAn> danhSachBanChon;
    private KhachHang khachHangHienTai;

    private final Frame parentFrame;

    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
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
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Dimension KICH_THUOC_NUT = new Dimension(160, 44);

    private final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 28);
    private final Font FONT_TIEU_DE_PHU = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 17);

    public DatBanCho_UI(Frame parent, List<BanAn> dsBan, Date ngayChon) {
        super(parent, "Đặt bàn chờ", true);
        this.parentFrame = parent;
        this.danhSachBanChon = dsBan;
        this.ngayNhanTuTrangChu = ngayChon;
        this.khachHangHienTai = null;

        try {
            this.khachHangDAO = (IKhachHang_Service) Naming.lookup("rmi://localhost:1099/KhachHang_Service");
            this.hoaDonDAO = (IHoaDon_Service) Naming.lookup("rmi://localhost:1099/HoaDon_Service");
            this.banAnDAO = (IBanAn_Service) Naming.lookup("rmi://localhost:1099/BanAn_Service");
        } catch (Exception e) {
            e.printStackTrace();
        }

        khoiTaoGiaoDien();
        setSize(620, 950);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

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

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN);
        panel.setBorder(new EmptyBorder(35, 50, 25, 50));
        JLabel lblTieuDe = new JLabel("ĐẶT BÀN CHỜ");
        lblTieuDe.setFont(FONT_TIEU_DE);
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);
        StringBuilder tencacBan = new StringBuilder();
        for (BanAn b : danhSachBanChon) {
            if (tencacBan.length() > 0)
                tencacBan.append(", ");
            tencacBan.append(b.getTenBan());
        }
        String hienThi = tencacBan.toString();
        if (hienThi.length() > 40)
            hienThi = hienThi.substring(0, 37) + "...";

        JLabel lblThongTinBan = new JLabel(hienThi);
        lblThongTinBan.setFont(FONT_TIEU_DE_PHU);
        lblThongTinBan.setForeground(MAU_CHU_XAM);
        lblThongTinBan.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTieuDe);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblThongTinBan);
        return panel;
    }

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
        JPanel pNgayGioNhan = new JPanel();
        pNgayGioNhan.setLayout(new BoxLayout(pNgayGioNhan, BoxLayout.Y_AXIS));
        pNgayGioNhan.setBackground(MAU_NEN_FORM);
        pNgayGioNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel pNgay = new JPanel();
        pNgay.setLayout(new BoxLayout(pNgay, BoxLayout.X_AXIS));
        pNgay.setBackground(MAU_NEN_FORM);
        pNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblNgayNhan = new JLabel("Ngày nhận bàn:");
        lblNgayNhan.setFont(FONT_NHAN);
        lblNgayNhan.setForeground(MAU_CHU_TRANG);
        lblNgayNhan.setPreferredSize(new Dimension(130, 42));
        txtNgayNhan = new JTextField();
        txtNgayNhan.setPreferredSize(new Dimension(140, 40));
        txtNgayNhan.setFont(FONT_TEXTFIELD);
        txtNgayNhan.setBackground(MAU_THANH_TIM_KIEM);
        txtNgayNhan.setForeground(Color.WHITE);
        txtNgayNhan.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
                new EmptyBorder(0, 10, 0, 0)));
        txtNgayNhan.setEditable(false);
        txtNgayNhan.setFocusable(false);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String strNgay = "";
        if (this.ngayNhanTuTrangChu != null) {
            strNgay = sdf.format(this.ngayNhanTuTrangChu);
        } else {
            strNgay = sdf.format(new Date());
        }
        txtNgayNhan.setText(strNgay);

        pNgay.add(lblNgayNhan);
        pNgay.add(Box.createHorizontalStrut(10));
        pNgay.add(txtNgayNhan);
        pNgay.add(Box.createHorizontalStrut(10));
        JPanel pGioPhut = new JPanel();
        pGioPhut.setLayout(new BoxLayout(pGioPhut, BoxLayout.X_AXIS));
        pGioPhut.setBackground(MAU_NEN_FORM);
        pGioPhut.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblGio = new JLabel("Giờ:");
        lblGio.setFont(FONT_NHAN);
        lblGio.setForeground(MAU_CHU_TRANG);
        SpinnerNumberModel gioModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), 0, 23, 1);
        spinGio = new JSpinner(gioModel);
        styleSpinner(spinGio);
        JLabel lblPhut = new JLabel("Phút:");
        lblPhut.setFont(FONT_NHAN);
        lblPhut.setForeground(MAU_CHU_TRANG);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        int roundedMinute = (currentMinute / 15) * 15;
        SpinnerNumberModel phutModel = new SpinnerNumberModel(roundedMinute, 0, 59, 15);
        spinPhut = new JSpinner(phutModel);
        styleSpinner(spinPhut);
        pGioPhut.add(Box.createHorizontalStrut(140));
        pGioPhut.add(lblGio);
        pGioPhut.add(Box.createHorizontalStrut(10));
        pGioPhut.add(spinGio);
        pGioPhut.add(Box.createHorizontalStrut(20));
        pGioPhut.add(lblPhut);
        pGioPhut.add(Box.createHorizontalStrut(10));
        pGioPhut.add(spinPhut);
        pGioPhut.add(Box.createHorizontalGlue());
        pNgayGioNhan.add(pNgay);
        pNgayGioNhan.add(Box.createVerticalStrut(10));
        pNgayGioNhan.add(pGioPhut);
        pNgayGioNhan.add(Box.createVerticalStrut(12));
        panel.add(pNgayGioNhan);
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
        txtGhiChu = taoFieldCoNhan(panel, "Ghi chú đặt bàn", false);
        txtGhiChu.setEditable(true);
        txtGhiChu.setBackground(MAU_NEN_INPUT);
        txtGhiChu.setForeground(MAU_CHU_TRANG);
        return panel;
    }

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
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MAU_VIEN_INPUT_FOCUS, 2, true), new EmptyBorder(7, 13, 7, 13)));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true), new EmptyBorder(8, 14, 8, 14)));
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

    // ---------------------------------------------------------
    // SWING WORKER: KIỂM TRA KHÁCH HÀNG
    // ---------------------------------------------------------
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

        btnKiemTra.setEnabled(false);
        btnKiemTra.setText("Đang tìm...");

        SwingWorker<KhachHang, Void> worker = new SwingWorker<KhachHang, Void>() {
            @Override
            protected KhachHang doInBackground() throws Exception {
                return khachHangDAO.timKhachHangTheoSDT(soDienThoai);
            }

            @Override
            protected void done() {
                btnKiemTra.setEnabled(true);
                btnKiemTra.setText("Kiểm tra");
                try {
                    KhachHang kh = get();
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
        };
        worker.execute();
    }

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

    // ĐÃ FIX: Truyền đủ 3 tham số gồm Runnable callback.
    private void xuLyThemKhachHangMoi(String soDienThoai) {
        ThemKhachHang_UI themKhachHangUI = new ThemKhachHang_UI(
                this.parentFrame,
                soDienThoai,
                () -> {
                    txtSoDienThoai.setText(soDienThoai);
                    xuLyKiemTraKhachHang();
                }
        );
        themKhachHangUI.setVisible(true);
    }

    // ---------------------------------------------------------
    // SWING WORKER: ĐẶT BÀN & RMI
    // ---------------------------------------------------------
    private void xuLyDatBan() {
        if (khachHangHienTai == null) {
            hienThiLoi("Vui lòng kiểm tra thông tin khách hàng trước!");
            return;
        }
        Date selectedDate = this.ngayNhanTuTrangChu;
        if (selectedDate == null) {
            hienThiLoi("Lỗi: Không xác định được ngày nhận bàn!");
            return;
        }

        Integer selectedHour = (Integer) spinGio.getValue();
        Integer selectedMinute = (Integer) spinPhut.getValue();

        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        cal.set(Calendar.HOUR_OF_DAY, selectedHour);
        cal.set(Calendar.MINUTE, selectedMinute);
        cal.set(Calendar.SECOND, 0);
        Date gioNhanDuKien = cal.getTime();

        Calendar nowPlus1Min = Calendar.getInstance();
        nowPlus1Min.add(Calendar.MINUTE, 1);
        if (gioNhanDuKien.before(nowPlus1Min.getTime())) {
            hienThiLoi("Giờ nhận bàn dự kiến phải sau thời điểm hiện tại ít nhất 1 phút!");
            return;
        }

        btnDatBan.setEnabled(false);
        btnDatBan.setText("Đang kiểm tra...");

        SwingWorker<java.util.Map<String, String>, Void> checkWorker = new SwingWorker<>() {
            @Override
            protected java.util.Map<String, String> doInBackground() throws Exception {
                IPhieuDatBan_Service phieuDAO_Check = (IPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBan_Service");
                return phieuDAO_Check.layThongTinBanDatVaTenKhach(selectedDate);
            }

            @Override
            protected void done() {
                try {
                    java.util.Map<String, String> mapBanDaDat = get();
                    StringBuilder trungBanMsg = new StringBuilder();
                    boolean coTrungLap = false;
                    for (BanAn banMuonDat : danhSachBanChon) {
                        if (mapBanDaDat.containsKey(banMuonDat.getMaBan())) {
                            coTrungLap = true;
                            trungBanMsg.append(banMuonDat.getTenBan()).append(", ");
                        }
                    }
                    if (coTrungLap) {
                        if (trungBanMsg.length() > 2) {
                            trungBanMsg.setLength(trungBanMsg.length() - 2);
                        }
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        hienThiLoi("Không thể đặt bàn!\n Bàn: " + trungBanMsg.toString() +
                                "\nđã được đặt trước vào ngày " + sdf.format(selectedDate) + " rồi.");
                        btnDatBan.setEnabled(true);
                        btnDatBan.setText("Đặt bàn");
                        return;
                    }

                    xacNhanVaLuuDatBan(gioNhanDuKien);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    hienThiLoi("Lỗi kết nối RMI khi kiểm tra thông tin trùng lặp!");
                    btnDatBan.setEnabled(true);
                    btnDatBan.setText("Đặt bàn");
                }
            }
        };
        checkWorker.execute();
    }

    private void xacNhanVaLuuDatBan(Date gioNhanDuKien) {
        StringBuilder sbTenBan = new StringBuilder();
        for (BanAn b : danhSachBanChon) sbTenBan.append(b.getTenBan()).append(", ");
        String strTenBan = sbTenBan.length() > 2 ? sbTenBan.substring(0, sbTenBan.length() - 2) : sbTenBan.toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Xác nhận đặt bàn chờ cho các bàn: %s\nKhách hàng: %s?",
                        strTenBan, khachHangHienTai.getHoTen()),
                "Xác nhận đặt bàn", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            btnDatBan.setEnabled(true);
            btnDatBan.setText("Đặt bàn");
            return;
        }

        NhanVien nhanVienHienTai = Auth.getCurrentNhanVien();
        if (nhanVienHienTai == null) {
            hienThiLoi("Lỗi: Không tìm thấy thông tin nhân viên.");
            btnDatBan.setEnabled(true);
            btnDatBan.setText("Đặt bàn");
            return;
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        boolean isDatChoHomNay = sdf.format(gioNhanDuKien).equals(sdf.format(new Date()));

        btnDatBan.setText("Đang lưu...");

        SwingWorker<PhieuDatBan, Void> saveWorker = new SwingWorker<>() {
            @Override
            protected PhieuDatBan doInBackground() throws Exception {
                IPhieuDatBan_Service phieuDAO = (IPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBan_Service");
                IPhieuDatBan_Ban_Service phieuBanDAO = (IPhieuDatBan_Ban_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBan_Ban_Service");

                String maPhieuDat = phieuDAO.sinhMaPhieuDatTuDong();
                String ghiChu = txtGhiChu.getText().trim();

                PhieuDatBan phieuMoi = new PhieuDatBan(
                        maPhieuDat, gioNhanDuKien, "Đang chờ",
                        khachHangHienTai.getMaKhachHang(), nhanVienHienTai.getMaNhanVien(),
                        0.0, ghiChu);

                if (!phieuDAO.themPhieuDatBan(phieuMoi)) {
                    throw new Exception("Thêm phiếu đặt bàn thất bại trên Database!");
                }

                for (BanAn ban : danhSachBanChon) {
                    phieuBanDAO.themPhieuDatBan_Ban(maPhieuDat, ban.getMaBan());
                    if (isDatChoHomNay) {
                        banAnDAO.capNhatTrangThaiBan(ban.getMaBan(), "Bàn đang chờ");
                    }
                }
                return phieuMoi;
            }

            @Override
            protected void done() {
                try {
                    PhieuDatBan phieuMoi = get();
                    tienHanhDatMonVaTinhCoc(phieuMoi);
                } catch (Exception e) {
                    e.printStackTrace();
                    hienThiLoi("Lỗi lưu phiếu đặt bàn: " + e.getMessage());
                    btnDatBan.setEnabled(true);
                    btnDatBan.setText("Đặt bàn");
                }
            }
        };
        saveWorker.execute();
    }

    private void tienHanhDatMonVaTinhCoc(PhieuDatBan phieuMoi) {
        int datMonConfirm = JOptionPane.showConfirmDialog(this, "Bạn có muốn đặt món trước cho nhóm này không?",
                "Đặt món trước", JOptionPane.YES_NO_OPTION);

        if (datMonConfirm == JOptionPane.YES_OPTION) {
            DatMonChoBan_UI themMonUI = new DatMonChoBan_UI(
                    parentFrame, danhSachBanChon, phieuMoi.getMaPhieuDatBan());
            themMonUI.setVisible(true);
        }

        SwingWorker<Double, Void> calcDepositWorker = new SwingWorker<>() {
            List<ChiTietPhieuDatBan> danhSachChiTietThucTe = new ArrayList<>();

            @Override
            protected Double doInBackground() throws Exception {
                IChiTietPhieuDatBan_Service ctPhieuDao = (IChiTietPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/ChiTietPhieuDatBan_Service");
                danhSachChiTietThucTe = ctPhieuDao.getChiTietTheoPhieu(phieuMoi.getMaPhieuDatBan());

                double tienDatCocSauCung = tinhTienDatCoc(danhSachBanChon, danhSachChiTietThucTe);
                if (tienDatCocSauCung > 0) {
                    IPhieuDatBan_Service phieuDAO = (IPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBan_Service");
                    phieuMoi.setTienDatCoc(tienDatCocSauCung);
                    phieuDAO.capNhatTienCoc(phieuMoi.getMaPhieuDatBan(), tienDatCocSauCung);
                }
                return tienDatCocSauCung;
            }

            @Override
            protected void done() {
                try {
                    double tienDatCocSauCung = get();
                    dispose();

                    String message = String.format(
                            "Đặt bàn thành công!\nSố tiền cọc cần thanh toán: %,.0f VNĐ.\n\nBạn có muốn xuất phiếu đặt bàn (PDF) không?",
                            tienDatCocSauCung);

                    int confirmPDF = JOptionPane.showConfirmDialog(parentFrame,
                            message, "Thành công", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if (confirmPDF == JOptionPane.YES_OPTION) {
                        String tenNV = Auth.getCurrentNhanVien() != null ? Auth.getCurrentNhanVien().getHoTen() : "Không xác định";
                        String tenKH = khachHangHienTai != null ? khachHangHienTai.getHoTen() : "Khách lẻ";

                        List<String> dsTenBan = new ArrayList<>();
                        for(BanAn b : danhSachBanChon) {
                            dsTenBan.add(b.getTenBan());
                        }

                        PhieuDatBanPDF.xuatPhieuDatBan(phieuMoi, danhSachChiTietThucTe, tenNV, tenKH, dsTenBan);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hienThiLoi("Lỗi khi tính toán tiền cọc!");
                }
            }
        };
        calcDepositWorker.execute();
    }

    private double tinhTienDatCoc(List<BanAn> dsBan, List<ChiTietPhieuDatBan> dsChiTiet) {
        double tongTienCocCoBan = 0.0;
        for (BanAn ban : dsBan) {
            switch (ban.getLoaiBan()) {
                case "Phòng VIP":
                    tongTienCocCoBan += 500000.0;
                    break;
                case "Bàn lớn":
                    tongTienCocCoBan += 300000.0;
                    break;
                default:
                    tongTienCocCoBan += 0.0;
                    break;
            }
        }

        double tongTienMonDatTruoc = 0.0;
        if (dsChiTiet != null && !dsChiTiet.isEmpty()) {
            for (ChiTietPhieuDatBan ct : dsChiTiet) {
                tongTienMonDatTruoc += ct.getDonGia().doubleValue() * ct.getSoLuong();
            }
        }

        if (tongTienMonDatTruoc > 0) {
            double tienCocTheoPhanTram = tongTienMonDatTruoc * 0.3;
            return Math.max(tienCocTheoPhanTram, tongTienCocCoBan);
        } else {
            return tongTienCocCoBan;
        }
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(FONT_O_NHAP);
        spinner.setPreferredSize(new Dimension(80, 42));
        spinner.setMaximumSize(new Dimension(100, 42));
        JComponent editor = spinner.getEditor();
        if (editor != null && editor.getComponentCount() > 0) {
            Component editorComponent = editor.getComponent(0);
            if (editorComponent instanceof JTextField) {
                JTextField textField = (JTextField) editorComponent;
                textField.setBackground(MAU_NEN_INPUT);
                textField.setForeground(MAU_CHU_TRANG);
                textField.setCaretColor(MAU_CHU_TRANG);
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
            } else {
                editorComponent.setBackground(MAU_NEN_INPUT);
                editorComponent.setForeground(MAU_CHU_TRANG);
            }
        }
        if (editor instanceof JPanel) {
            ((JPanel) editor).setBackground(MAU_NEN_INPUT);
        }
        for (Component comp : spinner.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(MAU_NEN_INPUT);
                button.setBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1));
                button.setFocusPainted(false);
            }
        }
        spinner.setBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true));
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
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

    private JTextField txtSoDienThoai, txtMaKH, txtHoTen, txtGioiTinh, txtTichDiem, txtGhiChu, txtNgayNhan;
    private JButton btnKiemTra, btnDatBan, btnHuy;
    private Date ngayNhanTuTrangChu;
    private JSpinner spinGio, spinPhut;

    private IKhachHang_Service khachHangDAO;
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
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_ACCENT = new Color(79, 134, 247);

    public DatBanCho_UI(Frame parent, List<BanAn> dsBan, Date ngayChon) {
        super(parent, "Đặt bàn chờ", true);
        this.parentFrame = parent;
        this.danhSachBanChon = dsBan;
        this.ngayNhanTuTrangChu = ngayChon;

        try {
            this.khachHangDAO = (IKhachHang_Service) Naming.lookup("rmi://localhost:1099/KhachHangService");
            this.banAnDAO = (IBanAn_Service) Naming.lookup("rmi://localhost:1099/BanAnService");
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
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTieuDe);
        return panel;
    }

    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel inputPanel = new JPanel(new BorderLayout(12, 0));
        inputPanel.setBackground(MAU_NEN_FORM);
        txtSoDienThoai = taoTextField("Nhập SĐT");
        btnKiemTra = taoNut("Kiểm tra", MAU_NUT_KIEM_TRA, MAU_NUT_KIEM_TRA_HOVER);
        btnKiemTra.addActionListener(e -> xuLyKiemTraKhachHang());
        inputPanel.add(txtSoDienThoai, BorderLayout.CENTER);
        inputPanel.add(btnKiemTra, BorderLayout.EAST);
        panel.add(inputPanel);

        panel.add(Box.createVerticalStrut(20));

        txtMaKH = taoFieldCoNhan(panel, "Mã khách hàng");
        txtHoTen = taoFieldCoNhan(panel, "Họ và tên");
        txtGioiTinh = taoFieldCoNhan(panel, "Giới tính");
        txtTichDiem = taoFieldCoNhan(panel, "Điểm tích lũy");

        JPanel pGioPhut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pGioPhut.setBackground(MAU_NEN_FORM);
        spinGio = new JSpinner(new SpinnerNumberModel(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), 0, 23, 1));
        spinPhut = new JSpinner(new SpinnerNumberModel(0, 0, 59, 15));
        pGioPhut.add(new JLabel("Giờ: ")); pGioPhut.add(spinGio);
        pGioPhut.add(new JLabel(" Phút: ")); pGioPhut.add(spinPhut);
        panel.add(pGioPhut);

        txtGhiChu = taoTextField("Ghi chú");
        panel.add(txtGhiChu);

        return panel;
    }

    private JTextField taoFieldCoNhan(JPanel panel, String nhan) {
        JTextField textField = taoTextField("");
        textField.setEditable(false);
        textField.setBackground(MAU_NEN_INPUT_DISABLED);
        panel.add(new JLabel(nhan));
        panel.add(textField);
        return textField;
    }

    private JTextField taoTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setBackground(MAU_NEN_INPUT);
        textField.setForeground(MAU_CHU_TRANG);
        return textField;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(MAU_NEN);
        btnDatBan = taoNut("Đặt bàn", MAU_NUT_DAT_BAN, MAU_NUT_DAT_BAN_HOVER);
        btnDatBan.setEnabled(false);
        btnDatBan.addActionListener(e -> xuLyDatBan());
        btnHuy = taoNut("Hủy", MAU_NUT_HUY, MAU_NUT_HUY_HOVER);
        btnHuy.addActionListener(e -> dispose());
        panel.add(btnDatBan);
        panel.add(btnHuy);
        return panel;
    }

    private JButton taoNut(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private void xuLyKiemTraKhachHang() {
        String soDienThoai = txtSoDienThoai.getText().trim();
        if (soDienThoai.isEmpty()) return;

        btnKiemTra.setEnabled(false);
        SwingWorker<KhachHang, Void> worker = new SwingWorker<>() {
            @Override
            protected KhachHang doInBackground() throws Exception {
                return khachHangDAO.timKhachHangTheoSDT(soDienThoai);
            }
            @Override
            protected void done() {
                btnKiemTra.setEnabled(true);
                try {
                    KhachHang kh = get();
                    if (kh != null) {
                        khachHangHienTai = kh;
                        txtMaKH.setText(kh.getMaKhachHang());
                        txtHoTen.setText(kh.getHoTen());
                        btnDatBan.setEnabled(true);
                    } else {
                        khachHangHienTai = null;
                        btnDatBan.setEnabled(false);
                        new ThemKhachHang_UI(parentFrame, soDienThoai, () -> {
                            txtSoDienThoai.setText(soDienThoai);
                            xuLyKiemTraKhachHang();
                        }).setVisible(true);
                    }
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void xuLyDatBan() {
        if (khachHangHienTai == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(ngayNhanTuTrangChu != null ? ngayNhanTuTrangChu : new Date());
        cal.set(Calendar.HOUR_OF_DAY, (Integer) spinGio.getValue());
        cal.set(Calendar.MINUTE, (Integer) spinPhut.getValue());
        Date gioNhanDuKien = cal.getTime();

        btnDatBan.setEnabled(false);
        btnDatBan.setText("Đang xử lý...");

        SwingWorker<PhieuDatBan, Void> worker = new SwingWorker<>() {
            @Override
            protected PhieuDatBan doInBackground() throws Exception {
                IPhieuDatBan_Service phieuDAO = (IPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBanService");
                IPhieuDatBan_Ban_Service phieuBanDAO = (IPhieuDatBan_Ban_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBan_BanService");

                String maPhieuDat = phieuDAO.sinhMaPhieuDatTuDong();
                PhieuDatBan phieuMoi = new PhieuDatBan(maPhieuDat, gioNhanDuKien, "Đang chờ",
                        khachHangHienTai.getMaKhachHang(), Auth.getCurrentNhanVien().getMaNhanVien(), 0.0, txtGhiChu.getText());

                if (phieuDAO.themPhieuDatBan(phieuMoi)) {
                    for (BanAn ban : danhSachBanChon) {
                        phieuBanDAO.themPhieuDatBan_Ban(maPhieuDat, ban.getMaBan());
                    }
                    return phieuMoi;
                }
                return null;
            }

            @Override
            protected void done() {
                btnDatBan.setEnabled(true);
                btnDatBan.setText("Đặt bàn");
                try {
                    PhieuDatBan p = get();
                    if (p != null) {
                        JOptionPane.showMessageDialog(DatBanCho_UI.this, "Tạo phiếu chờ thành công!");
                        dispose();
                        new DatMonChoBan_UI(parentFrame, danhSachBanChon, p.getMaPhieuDatBan()).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(DatBanCho_UI.this, "Lỗi tạo phiếu chờ!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
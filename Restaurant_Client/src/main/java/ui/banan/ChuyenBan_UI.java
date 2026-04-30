package ui.banan;

import rmi_interfaces.IBanAn_Service;
import rmi_interfaces.IHoaDon_Service;
import rmi_interfaces.IHoaDon_Ban_Service;
import rmi_interfaces.IPhieuDatBan_Service;
import rmi_interfaces.IPhieuDatBan_Ban_Service;

import entity.BanAn;
import entity.HoaDon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

public class ChuyenBan_UI extends JDialog {

    private JTextField txtTimKiem;
    private JTable tblBanTrong;
    private DefaultTableModel modelBanTrong;
    private JLabel lblBanHienTai;
    private JButton btnChuyenBan;
    private JButton btnHuy;
    private JButton btnLamMoi;

    private IBanAn_Service banAnService;
    private IHoaDon_Service hoaDonService;
    private IHoaDon_Ban_Service hoaDonBanService;
    private IPhieuDatBan_Service phieuDatBanService;
    private IPhieuDatBan_Ban_Service phieuDatBanBanService;

    private final BanAn banHienTai;
    private java.util.Date ngayChuyen;
    private BanAn banDuocChon = null;
    private List<BanAn> danhSachBanTrong;

    private final Color MAU_NEN = new Color(26, 28, 32);
    private final Color MAU_NEN_FORM = new Color(32, 35, 40);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_VIEN_INPUT = new Color(60, 65, 73);
    private final Color MAU_CHU_TRANG = new Color(240, 242, 245);
    private final Color MAU_CHU_XAM = new Color(155, 160, 170);
    private final Color MAU_NUT_CHUYEN = new Color(79, 134, 247);
    private final Color MAU_NUT_CHUYEN_HOVER = new Color(65, 115, 220);
    private final Color MAU_NUT_HUY = new Color(239, 68, 68);
    private final Color MAU_NUT_HUY_HOVER = new Color(220, 38, 38);
    private final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 15);

    public ChuyenBan_UI(Frame parent, BanAn banHienTai, java.util.Date ngayChuyen) {
        super(parent, "Chuyển Bàn", true);
        this.banHienTai = banHienTai;
        this.ngayChuyen = ngayChuyen;
        this.danhSachBanTrong = new ArrayList<>();

        try {
            // Nâng cấp kết nối RMI: Khai báo toàn bộ các Service cần thiết
            this.banAnService = (IBanAn_Service) Naming.lookup("rmi://localhost:1099/BanAn_Service");
            this.hoaDonService = (IHoaDon_Service) Naming.lookup("rmi://localhost:1099/HoaDon_Service");
            this.hoaDonBanService = (IHoaDon_Ban_Service) Naming.lookup("rmi://localhost:1099/HoaDon_Ban_Service");
            this.phieuDatBanService = (IPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBan_Service");
            this.phieuDatBanBanService = (IPhieuDatBan_Ban_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBan_Ban_Service");
        } catch (Exception e) {
            e.printStackTrace();
            hienThiLoi("Lỗi kết nối đến máy chủ RMI!");
        }

        khoiTaoGiaoDien();
        setSize(700, 650);
        setLocationRelativeTo(parent);
        setResizable(false);

        taiDuLieuBanTrong();
    }

    private void khoiTaoGiaoDien() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        mainPanel.add(taoPanelHeader(), BorderLayout.NORTH);
        mainPanel.add(taoPanelDanhSach(), BorderLayout.CENTER);
        mainPanel.add(taoPanelNutChucNang(), BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private JPanel taoPanelHeader() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MAU_NEN);

        JLabel lblTieuDe = new JLabel("CHUYỂN BÀN");
        lblTieuDe.setFont(FONT_TIEU_DE);
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        lblTieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTieuDe, BorderLayout.NORTH);

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlInfo.setBackground(MAU_NEN_FORM);
        pnlInfo.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblBanHienTai = new JLabel("Bàn hiện tại: " + banHienTai.getTenBan() + " - " + banHienTai.getTrangThai());
        lblBanHienTai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBanHienTai.setForeground(new Color(255, 193, 7));
        pnlInfo.add(lblBanHienTai);

        JLabel lblHuongDan = new JLabel("Vui lòng chọn một bàn trống bên dưới để chuyển đến.");
        lblHuongDan.setFont(FONT_CHU);
        lblHuongDan.setForeground(MAU_CHU_XAM);
        pnlInfo.add(lblHuongDan);

        panel.add(pnlInfo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelDanhSach() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(MAU_NEN);

        JPanel pnlTimKiem = new JPanel(new BorderLayout(10, 0));
        pnlTimKiem.setBackground(MAU_NEN);

        txtTimKiem = new JTextField();
        txtTimKiem.setFont(FONT_CHU);
        txtTimKiem.setBackground(MAU_NEN_INPUT);
        txtTimKiem.setForeground(MAU_CHU_TRANG);
        txtTimKiem.setCaretColor(MAU_CHU_TRANG);
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(8, 15, 8, 15)
        ));
        txtTimKiem.addActionListener(e -> xuLyTimKiem());

        JButton btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(MAU_NEN_INPUT);
        btnTimKiem.setForeground(MAU_CHU_TRANG);
        btnTimKiem.setFont(FONT_NUT);
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.addActionListener(e -> xuLyTimKiem());

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setBackground(new Color(60, 65, 73));
        btnLamMoi.setForeground(MAU_CHU_TRANG);
        btnLamMoi.setFont(FONT_NUT);
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.addActionListener(e -> xuLyLamMoi());

        JPanel pnlNutTimKiem = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlNutTimKiem.setBackground(MAU_NEN);
        pnlNutTimKiem.add(btnTimKiem);
        pnlNutTimKiem.add(btnLamMoi);

        pnlTimKiem.add(txtTimKiem, BorderLayout.CENTER);
        pnlTimKiem.add(pnlNutTimKiem, BorderLayout.EAST);

        panel.add(pnlTimKiem, BorderLayout.NORTH);

        modelBanTrong = new DefaultTableModel(new Object[]{"Mã Bàn", "Tên Bàn", "Sức Chứa", "Khu Vực"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblBanTrong = new JTable(modelBanTrong);
        tblBanTrong.setBackground(MAU_NEN_FORM);
        tblBanTrong.setForeground(MAU_CHU_TRANG);
        tblBanTrong.setFont(FONT_CHU);
        tblBanTrong.setRowHeight(35);
        tblBanTrong.setSelectionBackground(MAU_NUT_CHUYEN);
        tblBanTrong.setSelectionForeground(Color.WHITE);
        tblBanTrong.setGridColor(MAU_VIEN_INPUT);
        tblBanTrong.setShowVerticalLines(true);
        tblBanTrong.setShowHorizontalLines(true);

        JTableHeader header = tblBanTrong.getTableHeader();
        header.setBackground(MAU_NEN_INPUT);
        header.setForeground(MAU_CHU_TRANG);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblBanTrong.getColumnCount(); i++) {
            tblBanTrong.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tblBanTrong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblBanTrong.getSelectedRow();
                if (selectedRow >= 0) {
                    banDuocChon = danhSachBanTrong.get(selectedRow);
                    capNhatLabelBanHienTai();
                    btnChuyenBan.setEnabled(true);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblBanTrong);
        scrollPane.getViewport().setBackground(MAU_NEN_FORM);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1));
        tuyChinhScrollBar(scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelNutChucNang() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(MAU_NEN);

        btnChuyenBan = taoNut("Xác Nhận Chuyển", MAU_NUT_CHUYEN, MAU_NUT_CHUYEN_HOVER);
        btnChuyenBan.setEnabled(false);
        btnChuyenBan.addActionListener(e -> xuLyChuyenBan());

        btnHuy = taoNut("Hủy", MAU_NUT_HUY, MAU_NUT_HUY_HOVER);
        btnHuy.addActionListener(e -> dispose());

        panel.add(btnChuyenBan);
        panel.add(btnHuy);
        return panel;
    }

    private JButton taoNut(String text, Color mauNen, Color mauHover) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 45));
        button.setFont(FONT_NUT);
        button.setBackground(mauNen);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    private void capNhatLabelBanHienTai() {
        if (banDuocChon != null) {
            lblBanHienTai.setText("Chuyển từ: " + banHienTai.getTenBan() + " ➔ Đến: " + banDuocChon.getTenBan());
            lblBanHienTai.setForeground(new Color(76, 175, 80));
        } else {
            lblBanHienTai.setText("Bàn hiện tại: " + banHienTai.getTenBan() + " - " + banHienTai.getTrangThai());
            lblBanHienTai.setForeground(new Color(255, 193, 7));
        }
    }

    // -------------------------------------------------------------
    // SWING WORKER: Tải dữ liệu bàn trống
    // -------------------------------------------------------------
    private void taiDuLieuBanTrong() {
        if (banAnService == null) return;
        modelBanTrong.setRowCount(0);

        SwingWorker<List<BanAn>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BanAn> doInBackground() throws Exception {
                List<BanAn> allTables = banAnService.docDanhSachBan();
                List<BanAn> emptyTables = new ArrayList<>();
                for (BanAn b : allTables) {
                    if ("Bàn đang trống".equals(b.getTrangThai())) {
                        emptyTables.add(b);
                    }
                }
                return emptyTables;
            }

            @Override
            protected void done() {
                try {
                    danhSachBanTrong = get();
                    for (BanAn b : danhSachBanTrong) {
                        String tenKhu = b.getTenKhu() != null ? b.getTenKhu() : "N/A";
                        modelBanTrong.addRow(new Object[]{b.getMaBan(), b.getTenBan(), b.getSucChua(), tenKhu});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hienThiLoi("Lỗi khi tải danh sách bàn trống!");
                }
            }
        };
        worker.execute();
    }

    // -------------------------------------------------------------
    // SWING WORKER: Tìm kiếm bàn trống theo tên
    // -------------------------------------------------------------
    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            taiDuLieuBanTrong();
            return;
        }

        SwingWorker<List<BanAn>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BanAn> doInBackground() throws Exception {
                List<BanAn> allTables = banAnService.docDanhSachBan();
                List<BanAn> filteredTables = new ArrayList<>();
                for (BanAn b : allTables) {
                    if ("Bàn đang trống".equals(b.getTrangThai()) &&
                            b.getTenBan().toLowerCase().contains(keyword)) {
                        filteredTables.add(b);
                    }
                }
                return filteredTables;
            }

            @Override
            protected void done() {
                try {
                    danhSachBanTrong = get();
                    modelBanTrong.setRowCount(0);
                    for (BanAn b : danhSachBanTrong) {
                        String tenKhu = b.getTenKhu() != null ? b.getTenKhu() : "N/A";
                        modelBanTrong.addRow(new Object[]{b.getMaBan(), b.getTenBan(), b.getSucChua(), tenKhu});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // -------------------------------------------------------------
    // SWING WORKER: Xử lý chuyển bàn đa năng (Đang phục vụ / Đang chờ)
    // -------------------------------------------------------------
    private void xuLyChuyenBan() {
        if (banDuocChon == null) {
            hienThiLoi("Vui lòng chọn một bàn trống để chuyển đến!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận chuyển từ " + banHienTai.getTenBan() + " sang " + banDuocChon.getTenBan() + "?",
                "Xác nhận chuyển bàn", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        btnChuyenBan.setEnabled(false);
        btnChuyenBan.setText("Đang chuyển...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                String trangThaiHienTai = banHienTai.getTrangThai();

                // Trường hợp 1: Chuyển bàn đang có khách (Chuyển Hóa Đơn)
                if ("Bàn đang phục vụ".equals(trangThaiHienTai)) {

                    HoaDon hdCanTim = null;
                    try {
                        // SỬ DỤNG VÒNG LẶP ĐỂ VƯỢT QUA LỖI NEO4J TRÊN SERVER
                        List<HoaDon> dsChuaThanhToan = hoaDonService.locTheoTrangThai("Chưa thanh toán");
                        if (dsChuaThanhToan != null) {
                            for (HoaDon hd : dsChuaThanhToan) {
                                // Lấy danh sách mã bàn của từng hóa đơn để kiểm tra
                                List<String> dsBanCuaHD = hoaDonBanService.layDanhSachMaBanTheoHoaDon(hd.getMaHoaDon());
                                if (dsBanCuaHD != null && dsBanCuaHD.contains(banHienTai.getMaBan())) {
                                    hdCanTim = hd;
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }

                    if (hdCanTim == null) {
                        throw new Exception("Không tìm thấy hóa đơn chưa thanh toán của bàn này!");
                    }

                    // Tận dụng hàm chuyenBan có sẵn trong DAO của bạn
                    hoaDonBanService.chuyenBan(hdCanTim.getMaHoaDon(), banHienTai.getMaBan(), banDuocChon.getMaBan());

                    // Cập nhật lại trạng thái 2 bàn
                    banAnService.capNhatTrangThaiBan(banHienTai.getMaBan(), "Bàn đang trống");
                    banAnService.capNhatTrangThaiBan(banDuocChon.getMaBan(), "Bàn đang phục vụ");

                    // Trường hợp 2: Chuyển bàn khách đã đặt trước (Chuyển Phiếu Đặt Bàn)
                } else if ("Bàn đang chờ".equals(trangThaiHienTai)) {
                    if (ngayChuyen == null) throw new Exception("Không xác định được ngày lọc để lấy thông tin đặt bàn!");

                    // Hàm này trong DAO của bạn nhận 2 tham số (maBan, Date)
                    String maPhieu = phieuDatBanService.timMaPhieuDatDangChoTheoBan(banHienTai.getMaBan(), ngayChuyen);
                    if (maPhieu == null) throw new Exception("Không tìm thấy phiếu đặt bàn liên kết với bàn này!");

                    // Xóa bàn cũ, thêm bàn mới vào phiếu đặt
                    phieuDatBanBanService.xoaBanKhoiPhieu(maPhieu, banHienTai.getMaBan());
                    phieuDatBanBanService.themPhieuDatBan_Ban(maPhieu, banDuocChon.getMaBan());

                    // Cập nhật lại trạng thái 2 bàn
                    banAnService.capNhatTrangThaiBan(banHienTai.getMaBan(), "Bàn đang trống");
                    banAnService.capNhatTrangThaiBan(banDuocChon.getMaBan(), "Bàn đang chờ");

                } else {
                    throw new Exception("Trạng thái hiện tại không hợp lệ để chuyển bàn: " + trangThaiHienTai);
                }

                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        hienThiThanhCong("Chuyển bàn thành công!");
                        dispose(); // Đóng form sau khi thành công
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    hienThiLoi("Lỗi chuyển bàn: " + e.getMessage());
                    btnChuyenBan.setEnabled(true);
                    btnChuyenBan.setText("Xác Nhận Chuyển");
                }
            }
        };
        worker.execute();
    }

    private void tuyChinhScrollBar(JScrollPane s) {
        s.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        s.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
        s.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120);
                this.trackColor = MAU_NEN_INPUT;
            }
            @Override
            protected JButton createDecreaseButton(int o) {
                JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0, 0)); return btn;
            }
            @Override
            protected JButton createIncreaseButton(int o) {
                JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0, 0)); return btn;
            }
        });
    }

    private void xuLyLamMoi() {
        txtTimKiem.setText("");
        tblBanTrong.clearSelection();
        banDuocChon = null;
        capNhatLabelBanHienTai();
        btnChuyenBan.setEnabled(false);
        taiDuLieuBanTrong();
    }

    private void hienThiLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void hienThiThanhCong(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
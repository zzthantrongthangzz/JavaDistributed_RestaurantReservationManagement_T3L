package ui.banan;

import rmi_interfaces.*;
import entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChiTietBan_UI extends JDialog {

    private JTable tableChiTiet;
    private JLabel lblTienDichVuValue;
    private JLabel lblThueVATValue;
    private JLabel lblTongCongValue;
    private JLabel lblTienCocValue;
    private JLabel lblTamTinhValue;

    // Các Label hiển thị thông tin động
    private JLabel valSDT;
    private JLabel valMaHD;
    private JLabel valTenKhach;
    private JLabel valGioVao;
    private JLabel valNhanVien;
    private JLabel valThoiLuong;

    private final BanAn banDuocChon;
    private HoaDon hoaDonHienTai;
    private String maPhieuDatBan;
    private KhachHang khachHangHienTai;
    private NhanVien nhanVienHienTai;

    // CHUẨN HÓA SANG _SERVICE
    private IMonAn_Service monAnService;
    private IChiTietHoaDon_Service chiTietHoaDonService;
    private IKhachHang_Service khachHangService;
    private INhanVien_Service nhanVienService;
    private IPhieuDatBan_Service phieuDatBanService;
    private IChiTietPhieuDatBan_Service chiTietPhieuService;
    private IHoaDon_Service hoaDonService;
    private IHoaDon_Ban_Service hoaDonBanService;

    private List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();
    private List<MonAn> danhSachMonAn = new ArrayList<>();
    private boolean isCheDoPhieuDat = false;
    private final DecimalFormat currencyFormatter = new DecimalFormat("#,##0");
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("HH:mm - dd/MM/yyyy");

    private final Color COLOR_DARK = new Color(48, 52, 56);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color COLOR_TEXT_RED = new Color(255, 87, 87);
    private final Color COLOR_TEXT_GREEN = new Color(76, 175, 80);
    private final Color COLOR_HEADER = new Color(40, 44, 48);
    private final Color COLOR_GRID = new Color(80, 80, 80);
    private final Color COLOR_TEXT_WHITE = Color.WHITE;
    private final Color COLOR_TEXT_GRAY = new Color(150, 150, 150);
    private final Color COLOR_BUTTON_CLOSE = new Color(244, 67, 54);
    private final Color COLOR_BUTTON_CLOSE_HOVER = new Color(211, 47, 47);

    private final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TIEU_DE_PHU = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_MONEY_BOLD_RED = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_MONEY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_MONEY_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);

    public ChiTietBan_UI(Frame parent, BanAn ban, HoaDon hoaDon) {
        super(parent, "Chi tiết bàn (Hóa đơn)", true);
        this.banDuocChon = ban;
        this.hoaDonHienTai = hoaDon;
        this.isCheDoPhieuDat = false;
        khoiTao(parent);
    }

    public ChiTietBan_UI(Frame parent, BanAn ban, String maPhieuDatBan) {
        super(parent, "Chi tiết bàn (Phiếu đặt)", true);
        this.banDuocChon = ban;
        this.maPhieuDatBan = maPhieuDatBan;
        this.isCheDoPhieuDat = true;
        khoiTao(parent);
    }

    private void khoiTao(Frame parent) {
        ketNoiRMI();
        khoiTaoGiaoDien();
        configWindow(parent);
        taiDuLieuBanDau();
    }

    private void ketNoiRMI() {
        try {
            this.monAnService = (IMonAn_Service) Naming.lookup("rmi://localhost:1099/MonAnService");
            this.chiTietHoaDonService = (IChiTietHoaDon_Service) Naming.lookup("rmi://localhost:1099/ChiTietHoaDonService");
            this.khachHangService = (IKhachHang_Service) Naming.lookup("rmi://localhost:1099/KhachHangService");
            this.nhanVienService = (INhanVien_Service) Naming.lookup("rmi://localhost:1099/NhanVienService");
            this.phieuDatBanService = (IPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBanService");
            this.chiTietPhieuService = (IChiTietPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/ChiTietPhieuDatBanService");
            this.hoaDonService = (IHoaDon_Service) Naming.lookup("rmi://localhost:1099/HoaDonService");
            this.hoaDonBanService = (IHoaDon_Ban_Service) Naming.lookup("rmi://localhost:1099/HoaDonBanService");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối Máy chủ RMI!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configWindow(Frame parent) {
        setSize(1200, 700);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // ====================================================================
    // SWING WORKER: TẢI DỮ LIỆU ĐA LUỒNG & CHỐNG KHOẢNG TRẮNG
    // ====================================================================

    private void taiDuLieuBanDau() {
        if (monAnService == null) return;

        valSDT.setText("Đang tải...");
        valTenKhach.setText("Đang tải...");
        valMaHD.setText("Đang tải...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                danhSachMonAn = monAnService.docDanhSachMon();

                if (!isCheDoPhieuDat) {
                    // Xóa triệt để khoảng trắng bằng trim() để không bị miss hóa đơn
                    if (hoaDonHienTai == null || hoaDonHienTai.getMaHoaDon() == null || hoaDonHienTai.getMaHoaDon().trim().isEmpty()) {
                        String maBanCanTim = banDuocChon.getMaBan().trim();
                        boolean timThay = false;

                        // BƯỚC 1: Quét Hóa Đơn Chưa Thanh Toán trước
                        try {
                            List<HoaDon> dsChuaThanhToan = hoaDonService.locTheoTrangThai("Chưa thanh toán");
                            if (dsChuaThanhToan != null) {
                                for (HoaDon hd : dsChuaThanhToan) {
                                    List<String> dsBan = hoaDonBanService.layDanhSachMaBanTheoHoaDon(hd.getMaHoaDon());
                                    // Dùng trim().equalsIgnoreCase() để miễn nhiễm với khoảng trắng dư thừa trong DB
                                    if (dsBan != null && dsBan.stream().anyMatch(b -> b.trim().equalsIgnoreCase(maBanCanTim))) {
                                        hoaDonHienTai = hd;
                                        timThay = true;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {}

                        // BƯỚC 2: NẾU KHÔNG THẤY -> Mò sang Hóa Đơn Đã Thanh Toán
                        if (!timThay) {
                            try {
                                List<HoaDon> dsDaThanhToan = hoaDonService.locTheoTrangThai("Đã thanh toán");
                                if (dsDaThanhToan != null) {
                                    for (HoaDon hd : dsDaThanhToan) {
                                        List<String> dsBan = hoaDonBanService.layDanhSachMaBanTheoHoaDon(hd.getMaHoaDon());
                                        if (dsBan != null && dsBan.stream().anyMatch(b -> b.trim().equalsIgnoreCase(maBanCanTim))) {
                                            hoaDonHienTai = hd;
                                            timThay = true;
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {}
                        }
                    }

                    // Tải dữ liệu Khách Hàng, Nhân Viên, Chi Tiết
                    if (hoaDonHienTai != null && hoaDonHienTai.getMaHoaDon() != null) {
                        List<ChiTietHoaDon> dsDaGoi = chiTietHoaDonService.getChiTietTheoMaHoaDon(hoaDonHienTai.getMaHoaDon());
                        if (dsDaGoi != null) danhSachChiTiet.addAll(dsDaGoi);

                        if (hoaDonHienTai.getMaKhachHang() != null && !hoaDonHienTai.getMaKhachHang().trim().isEmpty()) {
                            List<KhachHang> khs = khachHangService.timKiemTheoMa(hoaDonHienTai.getMaKhachHang());
                            if(khs != null && !khs.isEmpty()) khachHangHienTai = khs.get(0);
                        }
                        if (hoaDonHienTai.getMaNhanVien() != null && !hoaDonHienTai.getMaNhanVien().trim().isEmpty()) {
                            nhanVienHienTai = nhanVienService.timMotNhanVienTheoMa(hoaDonHienTai.getMaNhanVien());
                        }
                    }
                } else { // Chế độ Phiếu Đặt Chờ
                    PhieuDatBan phieu = phieuDatBanService.getPhieuDatBanTheoMa(maPhieuDatBan);
                    if (phieu != null) {
                        hoaDonHienTai = new HoaDon(maPhieuDatBan, "Phiếu đặt chờ", phieu.getThoiGianDat(),
                                BigDecimal.ZERO, phieu.getMaNhanVien(), null, phieu.getMaKhachHang(),
                                null, null, BigDecimal.valueOf(phieu.getTienDatCoc()), BigDecimal.ZERO, BigDecimal.ZERO);

                        if (phieu.getMaKhachHang() != null && !phieu.getMaKhachHang().trim().isEmpty()) {
                            List<KhachHang> khs = khachHangService.timKiemTheoMa(phieu.getMaKhachHang());
                            if(khs != null && !khs.isEmpty()) khachHangHienTai = khs.get(0);
                        }
                        if (phieu.getMaNhanVien() != null && !phieu.getMaNhanVien().trim().isEmpty()) {
                            nhanVienHienTai = nhanVienService.timMotNhanVienTheoMa(phieu.getMaNhanVien());
                        }

                        List<ChiTietPhieuDatBan> listPhieu = chiTietPhieuService.getChiTietTheoPhieu(maPhieuDatBan);
                        if (listPhieu != null) {
                            for (ChiTietPhieuDatBan item : listPhieu) {
                                danhSachChiTiet.add(new ChiTietHoaDon(maPhieuDatBan, item.getMaMon(), item.getSoLuong(), item.getDonGia()));
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (hoaDonHienTai == null) {
                        JOptionPane.showMessageDialog(ChiTietBan_UI.this,
                                "Không thể tìm thấy thông tin Hóa Đơn/Phiếu Đặt cho bàn này!",
                                "Thông báo dữ liệu", JOptionPane.WARNING_MESSAGE);
                        dispose();
                        return;
                    }
                    capNhatThongTinHeader();
                    capNhatBangChiTiet();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ChiTietBan_UI.this, "Lỗi tải dữ liệu chi tiết!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void capNhatThongTinHeader() {
        valSDT.setText(khachHangHienTai != null ? khachHangHienTai.getSoDienThoai() : "Khách lẻ");
        valMaHD.setText(hoaDonHienTai.getMaHoaDon());
        valTenKhach.setText(khachHangHienTai != null ? khachHangHienTai.getHoTen() : "Khách lẻ");

        String gioVao = hoaDonHienTai.getNgayLapHoaDon() != null ? dateTimeFormatter.format(hoaDonHienTai.getNgayLapHoaDon()) : "";
        valGioVao.setText(gioVao);

        valNhanVien.setText(nhanVienHienTai != null ? nhanVienHienTai.getHoTen() : "Hệ thống");

        if (!isCheDoPhieuDat) {
            if (hoaDonHienTai.getNgayLapHoaDon() != null) {
                long diff = new Date().getTime() - hoaDonHienTai.getNgayLapHoaDon().getTime();
                if (diff < 0) diff = 0;
                valThoiLuong.setText((diff / (60 * 1000)) + " Phút");
            } else {
                valThoiLuong.setText("0 Phút");
            }
        } else {
            valThoiLuong.setText("");
        }
    }

    // ====================================================================
    // GIAO DIỆN
    // ====================================================================

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

        String title = isCheDoPhieuDat ? "Chi tiết phiếu đặt bàn" : "Chi tiết hóa đơn bàn";
        JLabel lblTieuDe = new JLabel(title);
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
        JPanel pLeft = taoPanelThongTinPhong();
        JPanel pRight = taoPanelHoaDonTam();
        panel.add(pLeft, BorderLayout.WEST);
        panel.add(pRight, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelThongTinPhong() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_DARK);
        panel.setBorder(new LineBorder(COLOR_GRID));
        panel.setPreferredSize(new Dimension(350, 0));
        JLabel lblTieuDe = new JLabel("Thông tin bàn");
        lblTieuDe.setFont(FONT_NHAN);
        lblTieuDe.setForeground(COLOR_TEXT_WHITE);
        lblTieuDe.setBorder(new EmptyBorder(10, 15, 0, 15));
        JPanel pNoiDungThongTin = new JPanel(new BorderLayout(0, 15));
        pNoiDungThongTin.setBackground(COLOR_DARK);
        pNoiDungThongTin.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel lblHinhAnh = new JLabel();
        try {
            String imagePath = layDuongDanAnhBan(banDuocChon);
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            lblHinhAnh.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblHinhAnh.setText("Ảnh Lỗi");
            lblHinhAnh.setForeground(COLOR_TEXT_GRAY);
        }
        lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblHinhAnh.setBorder(new EmptyBorder(10, 0, 20, 0));
        pNoiDungThongTin.add(lblHinhAnh, BorderLayout.NORTH);
        JPanel pThongTin = new JPanel();
        pThongTin.setLayout(new BoxLayout(pThongTin, BoxLayout.Y_AXIS));
        pThongTin.setOpaque(false);
        pThongTin.add(taoHangThongTin("Mã bàn:", banDuocChon.getMaBan()));
        pThongTin.add(Box.createVerticalStrut(12));
        pThongTin.add(taoHangThongTin("Tên bàn:", banDuocChon.getTenBan()));
        pThongTin.add(Box.createVerticalStrut(12));
        pThongTin.add(taoHangThongTin("Loại bàn:", banDuocChon.getLoaiBan()));
        pThongTin.add(Box.createVerticalStrut(12));
        pThongTin.add(taoHangThongTin("Trạng thái:", banDuocChon.getTrangThai()));
        pThongTin.add(Box.createVerticalStrut(12));
        pThongTin.add(taoHangThongTin("Sức chứa:", String.valueOf(banDuocChon.getSucChua()) + " người"));
        pThongTin.add(Box.createVerticalStrut(12));
        pNoiDungThongTin.add(pThongTin, BorderLayout.CENTER);
        panel.add(lblTieuDe, BorderLayout.NORTH);
        panel.add(pNoiDungThongTin, BorderLayout.CENTER);
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

    private JPanel taoPanelHoaDonTam() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_DARK);
        panel.setBorder(new LineBorder(COLOR_GRID));
        JLabel lblTieuDe = new JLabel(isCheDoPhieuDat ? "Thông tin phiếu đặt" : "Hóa đơn tạm");
        lblTieuDe.setFont(FONT_NHAN);
        lblTieuDe.setForeground(COLOR_TEXT_WHITE);
        lblTieuDe.setBorder(new EmptyBorder(10, 15, 0, 15));
        JPanel pThongTinKhach = taoPanelThongTinKhachHang();
        pThongTinKhach.setBorder(new EmptyBorder(10, 15, 10, 15));
        JPanel pBang = taoPanelBangChiTiet();
        pBang.setBorder(new EmptyBorder(0, 15, 0, 15));
        JPanel pTomTat = taoPanelTomTat();
        pTomTat.setBorder(new EmptyBorder(10, 15, 15, 15));
        JPanel pTop = new JPanel(new BorderLayout());
        pTop.setOpaque(false);
        pTop.add(lblTieuDe, BorderLayout.NORTH);
        pTop.add(pThongTinKhach, BorderLayout.CENTER);
        panel.add(pTop, BorderLayout.NORTH);
        panel.add(pBang, BorderLayout.CENTER);
        panel.add(pTomTat, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel taoPanelThongTinKhachHang() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 15, 10));
        panel.setOpaque(false);

        JLabel lblSDT = new JLabel("SDT Khách:");
        lblSDT.setFont(FONT_TIEU_DE_PHU);
        lblSDT.setForeground(COLOR_TEXT_WHITE);
        valSDT = new JLabel("Đang tải...");
        valSDT.setFont(FONT_O_NHAP);
        valSDT.setForeground(COLOR_TEXT_GRAY);

        JLabel lblMaHD = new JLabel(isCheDoPhieuDat ? "Mã phiếu:" : "Mã hóa đơn:");
        lblMaHD.setFont(FONT_TIEU_DE_PHU);
        lblMaHD.setForeground(COLOR_TEXT_WHITE);
        valMaHD = new JLabel("Đang tải...");
        valMaHD.setFont(FONT_O_NHAP);
        valMaHD.setForeground(COLOR_TEXT_GRAY);

        JLabel lblTenKhach = new JLabel("Tên khách:");
        lblTenKhach.setFont(FONT_TIEU_DE_PHU);
        lblTenKhach.setForeground(COLOR_TEXT_WHITE);
        valTenKhach = new JLabel("Đang tải...");
        valTenKhach.setFont(FONT_O_NHAP);
        valTenKhach.setForeground(COLOR_TEXT_GRAY);

        JLabel lblGioVao = new JLabel(isCheDoPhieuDat ? "Giờ đặt:" : "Giờ vào:");
        lblGioVao.setFont(FONT_TIEU_DE_PHU);
        lblGioVao.setForeground(COLOR_TEXT_WHITE);
        valGioVao = new JLabel("Đang tải...");
        valGioVao.setFont(FONT_O_NHAP);
        valGioVao.setForeground(COLOR_TEXT_GRAY);

        JLabel lblNhanVien = new JLabel(isCheDoPhieuDat ? "Người lập:" : "Nhân viên:");
        lblNhanVien.setFont(FONT_TIEU_DE_PHU);
        lblNhanVien.setForeground(COLOR_TEXT_WHITE);
        valNhanVien = new JLabel("Đang tải...");
        valNhanVien.setFont(FONT_O_NHAP);
        valNhanVien.setForeground(COLOR_TEXT_GRAY);

        JLabel lblThoiLuong = new JLabel("Thời gian chờ:");
        lblThoiLuong.setFont(FONT_TIEU_DE_PHU);
        lblThoiLuong.setForeground(COLOR_TEXT_WHITE);
        valThoiLuong = new JLabel("Đang tải...");
        valThoiLuong.setFont(FONT_O_NHAP);
        valThoiLuong.setForeground(COLOR_TEXT_GRAY);

        panel.add(lblSDT);
        panel.add(valSDT);
        panel.add(lblMaHD);
        panel.add(valMaHD);
        panel.add(lblTenKhach);
        panel.add(valTenKhach);
        panel.add(lblGioVao);
        panel.add(valGioVao);
        panel.add(lblNhanVien);
        panel.add(valNhanVien);

        if (!isCheDoPhieuDat) {
            panel.add(lblThoiLuong);
            panel.add(valThoiLuong);
        } else {
            panel.add(new JLabel(""));
            panel.add(new JLabel(""));
        }

        return panel;
    }

    private JPanel taoPanelBangChiTiet() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        String[] columnNames = { "Tên dịch vụ", "Đơn giá", "Số lượng", "Thành tiền" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableChiTiet = new JTable(model);
        tableChiTiet.setGridColor(COLOR_GRID);
        tableChiTiet.setBackground(COLOR_DARK);
        tableChiTiet.setForeground(COLOR_TEXT_WHITE);
        tableChiTiet.setFont(FONT_O_NHAP);
        tableChiTiet.setRowHeight(28);
        tableChiTiet.setIntercellSpacing(new Dimension(0, 0));
        tableChiTiet.getTableHeader().setBackground(COLOR_HEADER);
        tableChiTiet.getTableHeader().setForeground(COLOR_TEXT_WHITE);
        tableChiTiet.getTableHeader().setFont(FONT_NHAN);
        tableChiTiet.getTableHeader().setPreferredSize(new Dimension(0, 30));
        tableChiTiet.getTableHeader().setBorder(null);
        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.getViewport().setBackground(COLOR_DARK);
        scrollPane.setBorder(new LineBorder(COLOR_GRID));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        tuyChinhScrollBar(scrollPane);
        JPanel corner = new JPanel();
        corner.setBackground(MAU_NEN_INPUT);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void capNhatBangChiTiet() {
        DefaultTableModel model = (DefaultTableModel) tableChiTiet.getModel();
        model.setRowCount(0);
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            String tenMon = danhSachMonAn.stream()
                    .filter(mon -> mon.getMaMon().equals(ct.getMaMon()))
                    .map(MonAn::getTenMon)
                    .findFirst()
                    .orElse("Không tìm thấy");
            double thanhTien = ct.getDonGia().doubleValue() * ct.getSoLuong();
            model.addRow(new Object[] {
                    tenMon,
                    currencyFormatter.format(ct.getDonGia().doubleValue()) + " đ",
                    ct.getSoLuong(),
                    currencyFormatter.format(thanhTien) + " đ"
            });
        }
        capNhatTongTien();
    }

    private JPanel taoPanelTomTat() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel pGrid = new JPanel(new GridLayout(3, 2, 30, 12));
        pGrid.setOpaque(false);
        pGrid.setBorder(new EmptyBorder(10, 0, 10, 0));

        lblTienDichVuValue = new JLabel("0 VND");
        lblTienDichVuValue.setFont(FONT_MONEY_PLAIN);
        lblTienDichVuValue.setForeground(COLOR_TEXT_GRAY);

        lblThueVATValue = new JLabel("10.0% (+0 VND)");
        lblThueVATValue.setFont(FONT_MONEY_PLAIN);
        lblThueVATValue.setForeground(COLOR_TEXT_GRAY);

        lblTongCongValue = new JLabel("0 VND");
        lblTongCongValue.setFont(FONT_MONEY_BOLD);
        lblTongCongValue.setForeground(COLOR_TEXT_WHITE);

        lblTienCocValue = new JLabel("0 VND");
        lblTienCocValue.setFont(FONT_MONEY_BOLD);
        lblTienCocValue.setForeground(COLOR_TEXT_GREEN);

        lblTamTinhValue = new JLabel("0 VND");
        lblTamTinhValue.setFont(FONT_MONEY_BOLD_RED);
        lblTamTinhValue.setForeground(COLOR_TEXT_RED);

        pGrid.add(taoHangTomTat("Tiền dịch vụ:", lblTienDichVuValue));
        pGrid.add(taoHangTomTat("Tổng thành tiền:", lblTongCongValue));
        pGrid.add(taoHangTomTat("Thuế VAT:", lblThueVATValue));
        pGrid.add(taoHangTomTat("Đã đặt cọc:", lblTienCocValue));
        pGrid.add(new JLabel(""));
        pGrid.add(taoHangTomTat("CẦN THANH TOÁN:", lblTamTinhValue));
        panel.add(pGrid, BorderLayout.CENTER);
        return panel;
    }

    private void capNhatTongTien() {
        double tienDichVu = tinhTongTienDichVu();

        // KIỂM TRA BẢO MẬT TRƯỚC KHI ÉP KIỂU ĐỂ CHỐNG LỖI NULLPOINTER
        double tienCoc = 0.0;
        if (hoaDonHienTai != null && hoaDonHienTai.getTienDatCoc() != null) {
            try {
                tienCoc = hoaDonHienTai.getTienDatCoc().doubleValue();
            } catch (Exception e) {}
        }

        double thueVATPercent = 0.1;
        double tongChuaThue = tienDichVu;
        double tienThue = tongChuaThue * thueVATPercent;
        double tongCong = tongChuaThue + tienThue;
        double khachCanTra = tongCong - tienCoc;

        lblTienDichVuValue.setText(currencyFormatter.format(tienDichVu) + " VND");
        lblTongCongValue.setText(currencyFormatter.format(tongCong) + " VND");
        lblTienCocValue.setText(currencyFormatter.format(tienCoc) + " VND");
        lblThueVATValue
                .setText(String.format("%.1f%% (+%s VND)", thueVATPercent * 100, currencyFormatter.format(tienThue)));
        lblTamTinhValue.setText(currencyFormatter.format(khachCanTra) + " VND");
    }

    private double tinhTongTienDichVu() {
        double tong = 0;
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            tong += ct.getDonGia().doubleValue() * ct.getSoLuong();
        }
        return tong;
    }

    private JPanel taoHangTomTat(String nhan, JLabel lblGiaTri) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel lblNhan = new JLabel(nhan);
        lblNhan.setFont(FONT_TIEU_DE_PHU);
        lblNhan.setForeground(COLOR_TEXT_WHITE);
        lblGiaTri.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblNhan, BorderLayout.WEST);
        panel.add(lblGiaTri, BorderLayout.EAST);
        return panel;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(COLOR_DARK);
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JButton btnDong = taoNut("Đóng", COLOR_BUTTON_CLOSE, COLOR_BUTTON_CLOSE_HOVER);
        btnDong.setPreferredSize(new Dimension(160, 40));
        btnDong.addActionListener(e -> dispose());

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
                if (button.isEnabled())
                    button.setBackground(mauHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled())
                    button.setBackground(mauNen);
            }
        });
        return button;
    }

    private String layDuongDanAnhBan(BanAn ban) {
        String loaiBan = ban.getLoaiBan();
        String trangThai = ban.getTrangThai();
        if ("Phòng VIP".equals(loaiBan)) {
            if ("Bàn đang chờ".equals(trangThai))
                return "/IMG/banVang_vip.png";
            else if ("Bàn đang trống".equals(trangThai))
                return "/IMG/banXam_vip.png";
            else
                return "/IMG/banDo_vip.png";
        } else {
            if ("Bàn đang chờ".equals(trangThai))
                return "/IMG/banVang_thuong.png";
            else if ("Bàn đang trống".equals(trangThai))
                return "/IMG/banXam_thuong.png";
            else
                return "/IMG/banDo_thuong.png";
        }
    }

    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setBackground(MAU_NEN_INPUT);
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
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
                return new JButton() {
                    {
                        setPreferredSize(new Dimension(0, 0));
                    }
                };
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
                    return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 4, 4);
                g2.dispose();
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
        horizontalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
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
                return new JButton() {
                    {
                        setPreferredSize(new Dimension(0, 0));
                    }
                };
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
                    return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y + 2, thumbBounds.width, thumbBounds.height - 4, 4, 4);
                g2.dispose();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                g.setColor(trackColor);
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            }
        });
    }
}
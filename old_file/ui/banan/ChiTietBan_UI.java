package ui.banan;

import dao_impl.*;
import entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
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
    private final BanAn banDuocChon;
    private HoaDon hoaDonHienTai;
    private KhachHang khachHangHienTai;
    private NhanVien nhanVienHienTai;

    private final MonAn_DAO monAnDAO;
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO;
    private final KhachHang_DAO khachHangDAO;
    private final NhanVien_DAO nhanVienDAO;
    private final PhieuDatBan_DAO phieuDatBanDAO;
    private final ChiTietPhieuDatBan_DAO chiTietPhieuDAO;
    private List<ChiTietHoaDon> danhSachChiTiet;
    private List<MonAn> danhSachMonAn;
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

    // Khởi tạo dialog xem chi tiết bàn theo hóa đơn
    public ChiTietBan_UI(Frame parent, BanAn ban, HoaDon hoaDon) {
        super(parent, "Chi tiết bàn (Hóa đơn)", true);
        this.banDuocChon = ban;
        this.hoaDonHienTai = hoaDon;
        this.isCheDoPhieuDat = false;

        this.monAnDAO = new MonAn_DAO();
        this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
        this.khachHangDAO = new KhachHang_DAO();
        this.nhanVienDAO = new NhanVien_DAO();
        this.phieuDatBanDAO = new PhieuDatBan_DAO();
        this.chiTietPhieuDAO = new ChiTietPhieuDatBan_DAO();

        this.danhSachMonAn = monAnDAO.docDanhSachMon();
        this.danhSachChiTiet = chiTietHoaDonDAO.getChiTietTheoMaHoaDon(hoaDon.getMaHoaDon());

        loadKhachHangVaNhanVien();
        khoiTaoGiaoDien();
        capNhatBangChiTiet();

        configWindow(parent);
    }

    // Khởi tạo dialog xem chi tiết bàn theo phiếu đặt bàn
    public ChiTietBan_UI(Frame parent, BanAn ban, String maPhieuDatBan) {
        super(parent, "Chi tiết bàn (Phiếu đặt)", true);
        this.banDuocChon = ban;
        this.isCheDoPhieuDat = true;

        this.monAnDAO = new MonAn_DAO();
        this.chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
        this.khachHangDAO = new KhachHang_DAO();
        this.nhanVienDAO = new NhanVien_DAO();
        this.phieuDatBanDAO = new PhieuDatBan_DAO();
        this.chiTietPhieuDAO = new ChiTietPhieuDatBan_DAO();
        this.danhSachMonAn = monAnDAO.docDanhSachMon();
        PhieuDatBan phieu = phieuDatBanDAO.getPhieuDatBanTheoMa(maPhieuDatBan);

        this.hoaDonHienTai = new HoaDon(
                maPhieuDatBan,
                "Phiếu đặt chờ",
                phieu.getThoiGianDat(),
                BigDecimal.ZERO,
                phieu.getMaNhanVien(),
                null,
                phieu.getMaKhachHang(),
                null, null,
                BigDecimal.valueOf(phieu.getTienDatCoc()),
                BigDecimal.ZERO, BigDecimal.ZERO);

        this.danhSachChiTiet = new ArrayList<>();
        List<ChiTietPhieuDatBan> listPhieu = chiTietPhieuDAO.getChiTietTheoPhieu(maPhieuDatBan);
        if (listPhieu != null) {
            for (ChiTietPhieuDatBan item : listPhieu) {
                danhSachChiTiet.add(new ChiTietHoaDon(
                        maPhieuDatBan, item.getMaMon(), item.getSoLuong(), item.getDonGia()));
            }
        }

        loadKhachHangVaNhanVien();
        khoiTaoGiaoDien();
        capNhatBangChiTiet();

        configWindow(parent);
    }

    // Cấu hình cửa sổ dialog
    private void configWindow(Frame parent) {
        setSize(1200, 700);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // Tải thông tin khách hàng và nhân viên liên quan
    private void loadKhachHangVaNhanVien() {
        try {
            if (hoaDonHienTai.getMaKhachHang() != null) {
                List<KhachHang> khList = khachHangDAO.timKiemTheoMa(hoaDonHienTai.getMaKhachHang());
                if (khList != null && !khList.isEmpty()) {
                    khachHangHienTai = khList.get(0);
                }
            }
            if (hoaDonHienTai.getMaNhanVien() != null) {
                nhanVienHienTai = nhanVienDAO.timMotNhanVienTheoMa(hoaDonHienTai.getMaNhanVien());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Khởi tạo các thành phần giao diện chính
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

    // Tạo panel tiêu đề
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

    // Tạo panel nội dung chính (thông tin bàn + hóa đơn)
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

    // Tạo panel hiển thị thông tin bàn (bên trái)
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

    // Tạo dòng hiển thị nhãn và giá trị
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

    // Tạo panel hóa đơn tạm (bên phải)
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

    // Tạo panel thông tin khách hàng và phiếu
    private JPanel taoPanelThongTinKhachHang() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 15, 10));
        panel.setOpaque(false);

        JLabel lblSDT = new JLabel("SDT Khách:");
        lblSDT.setFont(FONT_TIEU_DE_PHU);
        lblSDT.setForeground(COLOR_TEXT_WHITE);
        String sdt = khachHangHienTai != null ? khachHangHienTai.getSoDienThoai() : "Khách lẻ";
        JLabel valSDT = new JLabel(sdt);
        valSDT.setFont(FONT_O_NHAP);
        valSDT.setForeground(COLOR_TEXT_GRAY);

        JLabel lblMaHD = new JLabel(isCheDoPhieuDat ? "Mã phiếu:" : "Mã hóa đơn:");
        lblMaHD.setFont(FONT_TIEU_DE_PHU);
        lblMaHD.setForeground(COLOR_TEXT_WHITE);
        JLabel valMaHD = new JLabel(hoaDonHienTai.getMaHoaDon());
        valMaHD.setFont(FONT_O_NHAP);
        valMaHD.setForeground(COLOR_TEXT_GRAY);

        JLabel lblTenKhach = new JLabel("Tên khách:");
        lblTenKhach.setFont(FONT_TIEU_DE_PHU);
        lblTenKhach.setForeground(COLOR_TEXT_WHITE);
        String tenKhach = khachHangHienTai != null ? khachHangHienTai.getHoTen() : "Khách lẻ";
        JLabel valTenKhach = new JLabel(tenKhach);
        valTenKhach.setFont(FONT_O_NHAP);
        valTenKhach.setForeground(COLOR_TEXT_GRAY);

        JLabel lblGioVao = new JLabel(isCheDoPhieuDat ? "Giờ đặt:" : "Giờ vào:");
        lblGioVao.setFont(FONT_TIEU_DE_PHU);
        lblGioVao.setForeground(COLOR_TEXT_WHITE);
        String gioVao = hoaDonHienTai.getNgayLapHoaDon() != null
                ? dateTimeFormatter.format(hoaDonHienTai.getNgayLapHoaDon())
                : "";
        JLabel valGioVao = new JLabel(gioVao);
        valGioVao.setFont(FONT_O_NHAP);
        valGioVao.setForeground(COLOR_TEXT_GRAY);

        JLabel lblNhanVien = new JLabel(isCheDoPhieuDat ? "Người lập:" : "Nhân viên:");
        lblNhanVien.setFont(FONT_TIEU_DE_PHU);
        lblNhanVien.setForeground(COLOR_TEXT_WHITE);
        String tenNhanVien = nhanVienHienTai != null ? nhanVienHienTai.getHoTen() : "N/A";
        JLabel valNhanVien = new JLabel(tenNhanVien);
        valNhanVien.setFont(FONT_O_NHAP);
        valNhanVien.setForeground(COLOR_TEXT_GRAY);

        JLabel lblThoiLuong = new JLabel("Thời gian chờ:");
        lblThoiLuong.setFont(FONT_TIEU_DE_PHU);
        lblThoiLuong.setForeground(COLOR_TEXT_WHITE);
        String thoiLuong = "0 Phút";
        if (hoaDonHienTai.getNgayLapHoaDon() != null) {
            long diff = new Date().getTime() - hoaDonHienTai.getNgayLapHoaDon().getTime();
            if (diff < 0)
                diff = 0;
            long diffMinutes = diff / (60 * 1000);
            thoiLuong = diffMinutes + " Phút";
        }
        JLabel valThoiLuong = new JLabel(thoiLuong);
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

    // Tạo bảng hiển thị chi tiết món ăn
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

    // Cập nhật dữ liệu vào bảng chi tiết
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

    // Tạo panel tổng hợp các loại tiền
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

    // Tính toán và hiển thị tổng tiền
    private void capNhatTongTien() {
        double tienDichVu = tinhTongTienDichVu();
        double tienCoc = 0.0;
        if (hoaDonHienTai.getTienDatCoc() != null) {
            tienCoc = hoaDonHienTai.getTienDatCoc().doubleValue();
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

    // Tính tổng tiền dịch vụ
    private double tinhTongTienDichVu() {
        double tong = 0;
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            tong += ct.getDonGia().doubleValue() * ct.getSoLuong();
        }
        return tong;
    }

    // Tạo dòng hiển thị tổng tiền
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

    // Tạo panel nút đóng
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

    // Tạo nút bấm tùy chỉnh
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

    // Lấy đường dẫn ảnh dựa vào trạng thái bàn
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

    // Tùy chỉnh thanh cuộn
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
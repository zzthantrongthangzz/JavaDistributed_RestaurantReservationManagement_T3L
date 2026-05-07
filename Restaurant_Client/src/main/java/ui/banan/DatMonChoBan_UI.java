package ui.banan;

import connect.ConfigManager;
import rmi_interfaces.IBanAn_Service;
import rmi_interfaces.IHoaDon_Service;
import rmi_interfaces.IHoaDon_Ban_Service;
import rmi_interfaces.ILoaiMon_Service;
import rmi_interfaces.IMonAn_Service;
import rmi_interfaces.IChiTietHoaDon_Service;
import rmi_interfaces.IChiTietPhieuDatBan_Service;
import entity.BanAn;
import entity.HoaDon;
import entity.MonAn;
import entity.LoaiMon;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuDatBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.rmi.Naming;

public class DatMonChoBan_UI extends JDialog {

    private JTable tableMonAn;
    private JTable tableChiTiet;
    private JTextField txtSoLuong;
    private JButton btnThemMon;
    private JButton btnXoaMon;
    private JButton btnHoanTat;
    private JButton btnHuy;
    private JLabel lblTongTien;
    private JLabel lblThongTinBan; // Đã biến thành biến toàn cục để cập nhật
    private JComboBox<Object> cmbLoaiMon;
    private JTextField txtTimMaMon;
    private JTextField txtTimTenMon;

    // ĐÃ SỬA THÀNH CHUẨN _SERVICE VÀ THÊM SERVICE HÓA ĐƠN
    private IMonAn_Service monAnService;
    private IBanAn_Service banAnService;
    private ILoaiMon_Service loaiMonService;
    private IChiTietHoaDon_Service chiTietHoaDonService;
    private IChiTietPhieuDatBan_Service chiTietPhieuService;
    private IHoaDon_Service hoaDonService;
    private IHoaDon_Ban_Service hoaDonBanService;

    private final List<BanAn> danhSachBan;

    private boolean isDatBanCho = false;
    private HoaDon hoaDonHienTai;
    private String maPhieuDatBan;

    private List<ChiTietHoaDon> danhSachChiTietTam;
    private List<MonAn> danhSachMonAn;
    private List<MonAn> danhSachMonAnHienThi;
    private MonAn monAnDuocChon;

    private final Color MAU_NEN = new Color(48, 52, 56);
    private final Color MAU_NEN_FORM = new Color(48, 52, 56);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_VIEN_INPUT = new Color(60, 65, 73);
    private final Color MAU_CHU_TRANG = new Color(240, 242, 245);
    private final Color MAU_CHU_XAM = new Color(155, 160, 170);
    private final Color MAU_NUT_THEM = new Color(34, 197, 94);
    private final Color MAU_NUT_THEM_HOVER = new Color(22, 163, 74);
    private final Color MAU_NUT_XOA = new Color(239, 68, 68);
    private final Color MAU_NUT_XOA_HOVER = new Color(220, 38, 38);
    private final Color MAU_NUT_HOAN_TAT = new Color(79, 134, 247);
    private final Color MAU_NUT_HOAN_TAT_HOVER = new Color(65, 115, 220);
    private final Color MAU_NUT_HUY = new Color(100, 100, 100);
    private final Color MAU_NUT_HUY_HOVER = new Color(80, 80, 80);
    private final Color MAU_ACCENT = new Color(79, 134, 247);
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);

    private final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 28);
    private final Font FONT_TIEU_DE_PHU = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 20);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 17);

    // ====================================================================
    // CONSTRUCTORS
    // ====================================================================

    public DatMonChoBan_UI(Frame parent, List<BanAn> dsBan, HoaDon hoaDon) {
        super(parent, "Đặt món cho bàn", true);
        this.danhSachBan = dsBan;
        this.hoaDonHienTai = hoaDon;
        this.isDatBanCho = false;

        ketNoiRMI();
        khoiTaoGiaoDien();
        configWindow(parent);
        taiDuLieuBanDau(); // Tải bằng SwingWorker
    }

    public DatMonChoBan_UI(Frame parent, BanAn ban, HoaDon hoaDon) {
        this(parent, new ArrayList<>(Arrays.asList(ban)), hoaDon);
    }

    public DatMonChoBan_UI(Frame parent, List<BanAn> dsBan, String maPhieuDatBan) {
        super(parent, "Đặt món trước (Đặt chờ)", true);
        this.danhSachBan = dsBan;
        this.maPhieuDatBan = maPhieuDatBan;
        this.isDatBanCho = true;

        ketNoiRMI();
        khoiTaoGiaoDien();
        configWindow(parent);
        taiDuLieuBanDau(); // Tải bằng SwingWorker
    }

    public DatMonChoBan_UI(Frame parent, BanAn ban, String maPhieuDatBan) {
        this(parent, new ArrayList<>(Arrays.asList(ban)), maPhieuDatBan);
    }

    // ====================================================================
    // INIT & RMI
    // ====================================================================

    private void ketNoiRMI() {
        try {
            String url = ConfigManager.getRmiUrl();
            this.monAnService = (IMonAn_Service) Naming.lookup(url +"MonAn_Service");
            this.banAnService = (IBanAn_Service) Naming.lookup(url +"BanAn_Service");
            this.loaiMonService = (ILoaiMon_Service) Naming.lookup(url +"LoaiMon_Service");
            this.chiTietHoaDonService = (IChiTietHoaDon_Service) Naming.lookup(url +"ChiTietHoaDon_Service");
            this.chiTietPhieuService = (IChiTietPhieuDatBan_Service) Naming.lookup(url +"ChiTietPhieuDatBan_Service");
            // KẾT NỐI THÊM ĐỂ TỰ ĐỘNG TÌM HÓA ĐƠN
            this.hoaDonService = (IHoaDon_Service) Naming.lookup(url +"HoaDon_Service");
            this.hoaDonBanService = (IHoaDon_Ban_Service) Naming.lookup(url +"HoaDon_Ban_Service");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối Máy chủ RMI!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configWindow(Frame parent) {
        setSize(1800, 950);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // ====================================================================
    // SWING WORKER: TẢI DỮ LIỆU BAN ĐẦU VÀ TỰ ĐỘNG TÌM HÓA ĐƠN
    // ====================================================================

    private void taiDuLieuBanDau() {
        if (monAnService == null) return;

        SwingWorker<Object[], Void> worker = new SwingWorker<>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                // 1. Tải danh sách Món & Loại
                List<MonAn> dsMon = monAnService.docDanhSachMon();
                List<LoaiMon> dsLoai = loaiMonService.docDanhSachLoaiMon();

                // 2. Tải danh sách chi tiết (Món đã gọi)
                List<ChiTietHoaDon> dsChiTietTam = new ArrayList<>();
                if (!isDatBanCho) {
                    // THUẬT TOÁN TỰ ĐỘNG TÌM HÓA ĐƠN AN TOÀN (VƯỢT LỖI NEO4J)
                    if ((hoaDonHienTai == null || hoaDonHienTai.getMaHoaDon() == null) && danhSachBan != null && !danhSachBan.isEmpty()) {
                        List<HoaDon> dsChuaThanhToan = hoaDonService.locTheoTrangThai("Chưa thanh toán");
                        if (dsChuaThanhToan != null) {
                            for (HoaDon hd : dsChuaThanhToan) {
                                List<String> dsBanCuaHD = hoaDonBanService.layDanhSachMaBanTheoHoaDon(hd.getMaHoaDon());
                                if (dsBanCuaHD != null && dsBanCuaHD.contains(danhSachBan.get(0).getMaBan())) {
                                    hoaDonHienTai = hd;
                                    break;
                                }
                            }
                        }
                    }

                    // Nếu đã tìm thấy thì lấy chi tiết món
                    if (hoaDonHienTai != null && hoaDonHienTai.getMaHoaDon() != null) {
                        List<ChiTietHoaDon> dsDaGoi = chiTietHoaDonService.getChiTietTheoMaHoaDon(hoaDonHienTai.getMaHoaDon());
                        if (dsDaGoi != null) dsChiTietTam.addAll(dsDaGoi);
                    }
                } else if (isDatBanCho && maPhieuDatBan != null) {
                    List<ChiTietPhieuDatBan> dsDaGoi = chiTietPhieuService.getChiTietTheoPhieu(maPhieuDatBan);
                    if (dsDaGoi != null) {
                        for(ChiTietPhieuDatBan ct : dsDaGoi) {
                            dsChiTietTam.add(new ChiTietHoaDon(maPhieuDatBan, ct.getMaMon(), ct.getSoLuong(), ct.getDonGia()));
                        }
                    }
                }
                return new Object[]{dsMon, dsLoai, dsChiTietTam};
            }

            @Override
            protected void done() {
                try {
                    Object[] res = get();
                    danhSachMonAn = (List<MonAn>) res[0];
                    danhSachMonAnHienThi = new ArrayList<>(danhSachMonAn);

                    List<LoaiMon> dsLoai = (List<LoaiMon>) res[1];
                    cmbLoaiMon.removeAllItems();
                    cmbLoaiMon.addItem("Tất cả");
                    for (LoaiMon loai : dsLoai) cmbLoaiMon.addItem(loai);

                    danhSachChiTietTam = (List<ChiTietHoaDon>) res[2];

                    // Cập nhật lại nhãn Hóa Đơn nếu vừa mò tìm thấy
                    if (!isDatBanCho && hoaDonHienTai != null && hoaDonHienTai.getMaHoaDon() != null) {
                        StringBuilder tenBanSb = new StringBuilder();
                        for (BanAn b : danhSachBan) {
                            if (tenBanSb.length() > 0) tenBanSb.append(", ");
                            tenBanSb.append(b.getTenBan().replace("Bàn", "").trim());
                        }
                        String dsBanStr = tenBanSb.length() > 60 ? tenBanSb.substring(0, 57) + "..." : tenBanSb.toString();
                        lblThongTinBan.setText(String.format("Hóa đơn: %s • Bàn: %s", hoaDonHienTai.getMaHoaDon(), dsBanStr));
                    }

                    capNhatBangMonAn(danhSachMonAnHienThi);
                    capNhatBangChiTiet();
                } catch (Exception e) {
                    e.printStackTrace();
                    hienThiLoi("Lỗi khi tải dữ liệu từ máy chủ!");
                }
            }
        };
        worker.execute();
    }

    // ====================================================================
    // GIAO DIỆN
    // ====================================================================

    private void khoiTaoGiaoDien() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        mainPanel.add(taoPanelTieuDe(), BorderLayout.NORTH);
        mainPanel.add(taoPanelNoiDung(), BorderLayout.CENTER);
        mainPanel.add(taoPanelNut(), BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN);
        panel.setBorder(new EmptyBorder(25, 50, 20, 50));

        String titleText = isDatBanCho ? "ĐẶT MÓN TRƯỚC (ĐẶT CHỜ)" : "GỌI MÓN (ĐẶT NGAY)";
        JLabel lblTieuDe = new JLabel(titleText);
        lblTieuDe.setFont(FONT_TIEU_DE);
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        StringBuilder tenBanSb = new StringBuilder();
        for (BanAn b : danhSachBan) {
            if (tenBanSb.length() > 0) tenBanSb.append(", ");
            tenBanSb.append(b.getTenBan().replace("Bàn", "").trim());
        }
        String danhSachBanStr = tenBanSb.toString();
        if (danhSachBanStr.length() > 60) danhSachBanStr = danhSachBanStr.substring(0, 57) + "...";

        String subInfo;
        if (isDatBanCho) {
            subInfo = String.format("Mã Phiếu: %s • Bàn: %s", maPhieuDatBan, danhSachBanStr);
        } else {
            String maHD = (hoaDonHienTai != null && hoaDonHienTai.getMaHoaDon() != null) ? hoaDonHienTai.getMaHoaDon() : "Đang tìm...";
            subInfo = String.format("Hóa đơn: %s • Bàn: %s", maHD, danhSachBanStr);
        }

        lblThongTinBan = new JLabel(subInfo);
        lblThongTinBan.setFont(FONT_TIEU_DE_PHU);
        lblThongTinBan.setForeground(MAU_CHU_XAM);
        lblThongTinBan.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTieuDe);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblThongTinBan);

        return panel;
    }

    private JPanel taoPanelNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(MAU_NEN_FORM);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel pLeft = taoPanelDanhSachMon();
        JPanel pRight = taoPanelChiTietHoaDon();
        pRight.setBorder(BorderFactory.createEmptyBorder(58, 0, 0, 0));

        panel.add(pLeft, BorderLayout.CENTER);
        panel.add(pRight, BorderLayout.EAST);

        return panel;
    }

    private JPanel taoPanelDanhSachMon() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(MAU_NEN_FORM);

        JLabel lblTieuDe = new JLabel("Danh sách món ăn");
        lblTieuDe.setFont(FONT_SECTION);
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        lblTieuDe.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel pFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pFilter.setBackground(MAU_NEN_FORM);

        JLabel lblMaMon = new JLabel("Mã món:");
        lblMaMon.setFont(FONT_NHAN);
        lblMaMon.setForeground(MAU_CHU_TRANG);
        pFilter.add(lblMaMon);

        txtTimMaMon = taoTextField("");
        txtTimMaMon.setPreferredSize(new Dimension(120, 35));
        pFilter.add(txtTimMaMon);

        JLabel lblTenMon = new JLabel("Tên món:");
        lblTenMon.setFont(FONT_NHAN);
        lblTenMon.setForeground(MAU_CHU_TRANG);
        pFilter.add(lblTenMon);

        txtTimTenMon = taoTextField("");
        txtTimTenMon.setPreferredSize(new Dimension(120, 35));
        pFilter.add(txtTimTenMon);

        JLabel lblLoai = new JLabel("Loại món:");
        lblLoai.setFont(FONT_NHAN);
        lblLoai.setForeground(MAU_CHU_TRANG);
        pFilter.add(lblLoai);

        JPanel pComboBox = taoComboBoxLoaiMon();
        pFilter.add(pComboBox);

        DocumentListener searchListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { locVaTimKiemMonAn(); }
            public void removeUpdate(DocumentEvent e) { locVaTimKiemMonAn(); }
            public void changedUpdate(DocumentEvent e) { locVaTimKiemMonAn(); }
        };
        txtTimMaMon.getDocument().addDocumentListener(searchListener);
        txtTimTenMon.getDocument().addDocumentListener(searchListener);

        String[] columnNames = { "Mã", "Tên món", "Giá", "Đơn vị" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableMonAn = new JTable(model);
        setupTableStyle(tableMonAn);

        tableMonAn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableMonAn.getSelectedRow();
                if (row < 0) return;
                String maMon = tableMonAn.getValueAt(row, 0).toString();
                monAnDuocChon = danhSachMonAnHienThi.stream().filter(mon -> mon.getMaMon().equals(maMon)).findFirst().orElse(null);
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && monAnDuocChon != null) {
                    themMonVaoChiTiet(monAnDuocChon, 1);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableMonAn);
        tuyChinhScrollBar(scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        scrollPane.setBackground(MAU_NEN_FORM);
        scrollPane.getViewport().setBackground(MAU_NEN_INPUT);

        JPanel pSoLuong = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pSoLuong.setBackground(MAU_NEN_FORM);
        JLabel lblSoLuong = new JLabel("Số lượng:");
        lblSoLuong.setFont(FONT_NHAN);
        lblSoLuong.setForeground(MAU_CHU_TRANG);
        txtSoLuong = taoTextField("1");
        txtSoLuong.setPreferredSize(new Dimension(100, 35));

        btnThemMon = taoNut("Thêm món", MAU_NUT_THEM, MAU_NUT_THEM_HOVER);
        btnThemMon.setPreferredSize(new Dimension(120, 35));
        btnThemMon.addActionListener(e -> xuLyThemMon());

        pSoLuong.add(lblSoLuong);
        pSoLuong.add(txtSoLuong);
        pSoLuong.add(btnThemMon);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(MAU_NEN_FORM);
        topPanel.add(lblTieuDe, BorderLayout.NORTH);
        topPanel.add(pFilter, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(pSoLuong, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoComboBoxLoaiMon() {
        JPanel pComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pComboBox.setBackground(MAU_NEN_FORM);
        this.cmbLoaiMon = new JComboBox<Object>();
        cmbLoaiMon.addItem("Đang tải...");

        cmbLoaiMon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cmbLoaiMon.setBackground(MAU_THANH_TIM_KIEM);
        cmbLoaiMon.setForeground(MAU_CHU_TRANG);
        cmbLoaiMon.setFocusable(false);
        cmbLoaiMon.setUI(new BasicComboBoxUI() {
            protected JButton createArrowButton() {
                return new JButton() {
                    {
                        setBackground(MAU_THANH_TIM_KIEM);
                        setBorder(BorderFactory.createEmptyBorder());
                    }
                };
            }
        });
        cmbLoaiMon.setBorder(BorderFactory.createLineBorder(new Color(70, 72, 87), 1));
        cmbLoaiMon.setPreferredSize(new Dimension(150, 40));
        cmbLoaiMon.addActionListener(e -> locVaTimKiemMonAn());
        pComboBox.add(cmbLoaiMon);
        return pComboBox;
    }

    private JPanel taoPanelChiTietHoaDon() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(MAU_NEN_FORM);
        panel.setPreferredSize(new Dimension(800, 0));

        JLabel lblTieuDe = new JLabel("Chi tiết gọi món");
        lblTieuDe.setFont(FONT_SECTION);
        lblTieuDe.setForeground(MAU_CHU_TRANG);

        String[] columnNames = { "Tên món", "SL", "Giá", "Thành tiền" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableChiTiet = new JTable(model);
        setupTableStyle(tableChiTiet);

        tableChiTiet.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tableChiTiet.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tableChiTiet.setRowSelectionInterval(row, row);
                        xuLyXoaMon();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        tuyChinhScrollBar(scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        scrollPane.setBackground(MAU_NEN_FORM);
        scrollPane.getViewport().setBackground(MAU_NEN_INPUT);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(MAU_NEN_FORM);

        JPanel pNutXoa = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pNutXoa.setBackground(MAU_NEN_FORM);
        btnXoaMon = taoNut("Xóa món", MAU_NUT_XOA, MAU_NUT_XOA_HOVER);
        btnXoaMon.addActionListener(e -> xuLyXoaMon());
        pNutXoa.add(btnXoaMon);

        JPanel pTongTien = new JPanel(new BorderLayout());
        pTongTien.setBackground(MAU_NEN_FORM);
        pTongTien.setBorder(new EmptyBorder(10, 0, 0, 0));
        JLabel lblTongTienLabel = new JLabel("Tổng tiền:");
        lblTongTienLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTienLabel.setForeground(MAU_CHU_TRANG);
        lblTongTien = new JLabel("0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongTien.setForeground(new Color(34, 197, 94));
        lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);

        pTongTien.add(lblTongTienLabel, BorderLayout.WEST);
        pTongTien.add(lblTongTien, BorderLayout.CENTER);

        southPanel.add(pTongTien, BorderLayout.NORTH);
        southPanel.add(pNutXoa, BorderLayout.SOUTH);

        panel.add(lblTieuDe, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(MAU_NEN);
        panel.setBorder(new EmptyBorder(20, 50, 30, 50));

        btnHoanTat = taoNut("Hoàn tất", MAU_NUT_HOAN_TAT, MAU_NUT_HOAN_TAT_HOVER);
        btnHoanTat.setPreferredSize(new Dimension(150, 44));
        btnHoanTat.addActionListener(e -> xuLyHoanTat());

        btnHuy = taoNut("Hủy", MAU_NUT_HUY, MAU_NUT_HUY_HOVER);
        btnHuy.setPreferredSize(new Dimension(150, 44));
        btnHuy.addActionListener(e -> dispose());

        panel.add(btnHoanTat);
        panel.add(btnHuy);
        return panel;
    }

    // ====================================================================
    // LOGIC NGHIỆP VỤ & LỌC
    // ====================================================================

    private void locVaTimKiemMonAn() {
        if (danhSachMonAn == null) return;

        String ma = txtTimMaMon.getText().trim().toLowerCase();
        String ten = txtTimTenMon.getText().trim().toLowerCase();
        Object loaiSel = cmbLoaiMon.getSelectedItem();

        danhSachMonAnHienThi = danhSachMonAn.stream().filter(m -> {
            boolean kMa = ma.isEmpty() || m.getMaMon().toLowerCase().contains(ma);
            boolean kTen = ten.isEmpty() || m.getTenMon().toLowerCase().contains(ten);
            boolean kLoai = true;
            if (loaiSel instanceof LoaiMon) {
                kLoai = m.getLoaiMon().getMaLoai().equals(((LoaiMon) loaiSel).getMaLoai());
            }
            return kMa && kTen && kLoai;
        }).collect(Collectors.toList());

        capNhatBangMonAn(danhSachMonAnHienThi);
    }

    private void capNhatBangMonAn(List<MonAn> ds) {
        if (ds == null) return;
        DefaultTableModel m = (DefaultTableModel) tableMonAn.getModel();
        m.setRowCount(0);
        for (MonAn x : ds) {
            m.addRow(new Object[] { x.getMaMon(), x.getTenMon(), String.format("%,.0f đ", x.getGia()), x.getDonVi() });
        }
    }

    private void capNhatBangChiTiet() {
        if (danhSachChiTietTam == null) return;
        DefaultTableModel model = (DefaultTableModel) tableChiTiet.getModel();
        model.setRowCount(0);
        double tongTien = 0;

        for (ChiTietHoaDon ct : danhSachChiTietTam) {
            String tenMon = "Unknown";
            if (danhSachMonAn != null) {
                tenMon = danhSachMonAn.stream()
                        .filter(mon -> mon.getMaMon().equals(ct.getMaMon()))
                        .map(MonAn::getTenMon).findFirst().orElse("Unknown");
            }

            double thanhTien = ct.getDonGia().doubleValue() * ct.getSoLuong();
            model.addRow(new Object[] { tenMon, ct.getSoLuong(), String.format("%,.0f đ", ct.getDonGia().doubleValue()),
                    String.format("%,.0f đ", thanhTien) });
            tongTien += thanhTien;
        }
        lblTongTien.setText(String.format("%,.0f đ", tongTien));
    }

    private void themMonVaoChiTiet(MonAn monThem, int soLuong) {
        if (monThem == null) return;

        ChiTietHoaDon chiTietHienCo = danhSachChiTietTam.stream()
                .filter(ct -> ct.getMaMon().equals(monThem.getMaMon()))
                .findFirst().orElse(null);

        if (chiTietHienCo != null) {
            chiTietHienCo.setSoLuong(chiTietHienCo.getSoLuong() + soLuong);
        } else {
            // Nếu là Hóa đơn nhưng chưa có mã (lỗi hờ), tạo tạm chuỗi để lưu list
            String tempID = isDatBanCho ? maPhieuDatBan : (hoaDonHienTai != null ? hoaDonHienTai.getMaHoaDon() : "TEMP");
            ChiTietHoaDon chiTietMoi = new ChiTietHoaDon(tempID, monThem.getMaMon(), soLuong, BigDecimal.valueOf(monThem.getGia()));
            danhSachChiTietTam.add(chiTietMoi);
        }
        capNhatBangChiTiet();
    }

    private void xuLyThemMon() {
        if (monAnDuocChon == null) {
            hienThiLoi("Vui lòng chọn một món ăn ở danh sách bên trái!");
            return;
        }
        try {
            int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong <= 0) {
                hienThiLoi("Số lượng phải lớn hơn 0!");
                return;
            }
            themMonVaoChiTiet(monAnDuocChon, soLuong);
            txtSoLuong.setText("1");
            tableMonAn.clearSelection();
            monAnDuocChon = null;
        } catch (NumberFormatException e) {
            hienThiLoi("Số lượng phải là số nguyên hợp lệ!");
        }
    }

    private void xuLyXoaMon() {
        int row = tableChiTiet.getSelectedRow();
        if (row < 0) {
            hienThiLoi("Chọn dòng bên bảng Món đã gọi để xóa!");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa món này khỏi thực đơn?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            danhSachChiTietTam.remove(row);
            capNhatBangChiTiet();
        }
    }

    // ====================================================================
    // SWING WORKER: LƯU DATABASE
    // ====================================================================

    private void xuLyHoanTat() {
        if (danhSachChiTietTam.isEmpty()) {
            hienThiLoi("Vui lòng chọn ít nhất một món để thêm vào đơn!");
            return;
        }

        String message = isDatBanCho ? "Xác nhận lưu thực đơn cho phiếu đặt bàn này?" : "Xác nhận gọi món cho bàn này?";
        int xacNhan = JOptionPane.showConfirmDialog(this, message, "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (xacNhan == JOptionPane.YES_OPTION) {
            btnHoanTat.setEnabled(false);
            btnHoanTat.setText("Đang lưu...");

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (isDatBanCho) {
                        chiTietPhieuService.xoaChiTietTheoPhieu(maPhieuDatBan);
                        for (ChiTietHoaDon item : danhSachChiTietTam) {
                            ChiTietPhieuDatBan ctPhieu = new ChiTietPhieuDatBan(maPhieuDatBan, item.getMaMon(), item.getSoLuong(), item.getDonGia());
                            chiTietPhieuService.themChiTietPhieuDat(ctPhieu);
                        }
                    } else {
                        if (hoaDonHienTai == null || hoaDonHienTai.getMaHoaDon() == null) {
                            throw new Exception("Lỗi: Không tìm thấy hóa đơn của bàn này (Vui lòng kiểm tra đã Mở Bàn chưa)!");
                        }
                        chiTietHoaDonService.xoaChiTietTheoMaHoaDon(hoaDonHienTai.getMaHoaDon());
                        for (ChiTietHoaDon item : danhSachChiTietTam) {
                            item.setMaHoaDon(hoaDonHienTai.getMaHoaDon());
                            chiTietHoaDonService.themChiTietHoaDon(item);
                        }
                        for (BanAn b : danhSachBan) {
                            if (!b.getTrangThai().equals("Bàn đang phục vụ")) {
                                banAnService.capNhatTrangThaiBan(b.getMaBan(), "Bàn đang phục vụ");
                            }
                        }
                    }
                    return true;
                }

                @Override
                protected void done() {
                    btnHoanTat.setEnabled(true);
                    btnHoanTat.setText("Hoàn tất");
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(DatMonChoBan_UI.this, "Đã lưu thực đơn thành công!");
                            dispose();
                        }
                    } catch (Exception e) {
                        hienThiLoi(e.getMessage());
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    // ====================================================================
    // HELPER METHODS
    // ====================================================================

    private void hienThiLoi(String m) {
        JOptionPane.showMessageDialog(this, m, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private JTextField taoTextField(String p) {
        JTextField t = new JTextField(p);
        t.setFont(FONT_O_NHAP);
        t.setBackground(MAU_NEN_INPUT);
        t.setForeground(MAU_CHU_TRANG);
        t.setCaretColor(MAU_ACCENT);
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        return t;
    }

    private JButton taoNut(String t, Color bg, Color h) {
        JButton b = new JButton(t);
        b.setFont(FONT_NUT);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(h); }
            public void mouseExited(MouseEvent e) { if (b.isEnabled()) b.setBackground(bg); }
        });
        return b;
    }

    private void setupTableStyle(JTable t) {
        t.setBackground(MAU_NEN_INPUT);
        t.setForeground(MAU_CHU_TRANG);
        t.setFont(FONT_O_NHAP);
        t.setRowHeight(30);
        t.getTableHeader().setBackground(MAU_NEN_INPUT);
        t.getTableHeader().setForeground(MAU_CHU_TRANG);
        t.getTableHeader().setFont(FONT_NHAN);
        t.setSelectionBackground(MAU_ACCENT);
        t.setSelectionForeground(Color.WHITE);
    }

    private void tuyChinhScrollBar(JScrollPane s) {
        s.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        s.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
        s.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120);
                this.trackColor = MAU_NEN_INPUT;
            }
            protected JButton createDecreaseButton(int o) { return new JButton() {{ setPreferredSize(new Dimension(0, 0)); }}; }
            protected JButton createIncreaseButton(int o) { return new JButton() {{ setPreferredSize(new Dimension(0, 0)); }}; }
        });
    }
}
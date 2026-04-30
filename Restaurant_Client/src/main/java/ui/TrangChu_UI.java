package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import entity.MonAn;
import entity.BanAn;
import ui.khachhang.ThongKeKhachHang_UI;
import ui.khuyenmai.ThongKeKhuyenMai_UI;
import ui.hoadon.ThongKeHoaDon_UI;
import ui.hoadon.TraCuuHoaDon_UI;
import ui.banan.CapNhatBan_UI;
import ui.banan.LichSuHuyDatBan_UI;
import ui.banan.QuanLyDatBan_UI;
import ui.banan.ThemBan_UI;
import ui.khachhang.CapNhatKhachHang_UI;
import ui.khachhang.TraCuuKhachHang_UI;
import ui.khachhang.ThemKhachHang_UI;
import ui.khuyenmai.CapNhatKhuyenMai_UI;
import ui.khuyenmai.TraCuuKhuyenMai_UI;
import ui.khuyenmai.ThemKhuyenMai_UI;
import ui.monan.CapNhatMon_UI;
import ui.monan.TraCuuMonAn_UI;
import ui.monan.ThemMonAn_UI;
import ui.monan.ThongKeMonAn_UI;
import ui.nhanvien.CapNhatNhanVien_UI;
import ui.nhanvien.ThemNhanVien_UI;
import ui.nhanvien.TraCuuNhanVien_UI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import entity.NhanVien;

public class TrangChu_UI extends JFrame {

    private JPanel menuPanel;
    private JPanel mainContentPanel;
    private final List<JPanel> tatCaCacItemMenuCoTheClick;
    private JPanel itemMenuDangDuocChon;
    private final List<JPanel> cacMenuCha;
    private JPanel menuDangMoRong = null;
    private String tenTabHienTai = "Hệ thống";

    private final Color MAU_NEN_MENU_MAC_DINH = new Color(31, 32, 44);
    private final Color MAU_NEN_MENU_HOVER = new Color(70, 70, 70);
    private final Color MAU_NEN_MENU_DUOC_CHON = new Color(241, 121, 104);
    private final Color MAU_NEN_MENU_CON_HOVER = new Color(80, 80, 80);
    private final Color MAU_CHU_TRANG = Color.WHITE;
    private final Color MAU_NEN_DEN = Color.BLACK;

    private final Dimension KICH_THUOC_MENU = new Dimension(250, 0);
    private final Dimension KICH_THUOC_LOGO_PANEL = new Dimension(250, 150);
    private final Dimension KICH_THUOC_NGAY_GIO_PANEL = new Dimension(250, 60);
    private final Dimension KICH_THUOC_ITEM_MENU = new Dimension(250, 50);
    private final Dimension KICH_THUOC_ITEM_MENU_CON = new Dimension(250, 40);
    private final Dimension KICH_THUOC_PANEL_DUOI = new Dimension(250, 60);
    private final Dimension KICH_THUOC_TOI_THIEU = new Dimension(1200, 700);
    private final Dimension KICH_THUOC_CUA_SO = new Dimension(1800, 950);

    private final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 20);
    private final Font FONT_NGAY = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_MENU_CHA = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_MENU_CON = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_NGUOI_DUNG = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_NOI_DUNG = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_XIN_CHAO = new Font("Segoe UI", Font.BOLD, 100);

    private final int KICH_THUOC_ICON_MENU = 24;
    private final int KICH_THUOC_ICON_DANG_XUAT = 24;
    private final int KICH_THUOC_LOGO = 170;

    private NhanVien nhanVienHienTai;
    private boolean isQuanLy = false;

    // Constructor khởi tạo giao diện chính và phân quyền
    public TrangChu_UI() {
        nhanVienHienTai = Auth.getCurrentNhanVien();
        if (nhanVienHienTai != null && nhanVienHienTai.getChucVu() != null) {
            if (nhanVienHienTai.getChucVu().getMaChucVu().equals("CV000001")) {
                this.isQuanLy = true;
            }
        }

        khoiTaoCuaSo();
        tatCaCacItemMenuCoTheClick = new ArrayList<>();
        cacMenuCha = new ArrayList<>();

        JPanel menuContainerPanel = taoMenuContainerPanel();
        add(menuContainerPanel, BorderLayout.WEST);

        mainContentPanel = taoMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);

        hienThiNoiDung("Hệ thống");
    }

    // Thiết lập các thuộc tính cơ bản cho cửa sổ JFrame
    private void khoiTaoCuaSo() {
        setTitle("Hệ thống Quản lý Đặt Bàn Nhà hàng T3L");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(KICH_THUOC_CUA_SO);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(KICH_THUOC_TOI_THIEU);
        setLayout(new BorderLayout());
    }

    // Tạo panel chứa toàn bộ menu bên trái
    private JPanel taoMenuContainerPanel() {
        JPanel menuContainerPanel = new JPanel(new BorderLayout());
        menuContainerPanel.setPreferredSize(new Dimension(KICH_THUOC_MENU.width, getHeight()));
        menuContainerPanel.setBackground(MAU_NEN_MENU_MAC_DINH);

        taoMenuPanel();
        JScrollPane menuScrollPane = taoMenuScrollPane();
        menuContainerPanel.add(menuScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = taoPanelDuoi();
        menuContainerPanel.add(bottomPanel, BorderLayout.SOUTH);

        return menuContainerPanel;
    }

    // Tạo panel chứa danh sách các item menu
    private void taoMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setBackground(MAU_NEN_MENU_MAC_DINH);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        menuPanel.add(taoLogoPanel());
        menuPanel.add(taoNgayGioPanel());
        menuPanel.add(Box.createVerticalStrut(10));

        themCacMenuItem();

        menuPanel.add(Box.createVerticalGlue());
    }

    // Tạo panel hiển thị logo nhà hàng
    private JPanel taoLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(MAU_NEN_MENU_MAC_DINH);
        logoPanel.setMaximumSize(KICH_THUOC_LOGO_PANEL);
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/IMG/t3LLogo_200px.png"));
            Image logoImage = logoIcon.getImage().getScaledInstance(KICH_THUOC_LOGO, KICH_THUOC_LOGO, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
            logoPanel.add(logoLabel);
        } catch (Exception e) {
            logoPanel.add(new JLabel("Không tìm thấy logo!"));
            System.err.println("Error loading logo: " + e.getMessage());
        }

        return logoPanel;
    }

    // Tạo panel hiển thị ngày giờ hiện tại
    private JPanel taoNgayGioPanel() {
        JPanel dateTimePanel = new JPanel();
        dateTimePanel.setBackground(MAU_NEN_MENU_MAC_DINH);
        dateTimePanel.setMaximumSize(KICH_THUOC_NGAY_GIO_PANEL);
        dateTimePanel.setLayout(new BoxLayout(dateTimePanel, BoxLayout.Y_AXIS));
        dateTimePanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel timeLabel = taoNhanGio();
        JLabel dateLabel = taoNhanNgay();

        capNhatNgayGio(timeLabel, dateLabel);
        new Timer(1000, e -> capNhatNgayGio(timeLabel, dateLabel)).start();

        dateTimePanel.add(timeLabel);
        dateTimePanel.add(dateLabel);

        return dateTimePanel;
    }

    // Tạo nhãn hiển thị giờ
    private JLabel taoNhanGio() {
        JLabel timeLabel = new JLabel();
        timeLabel.setForeground(MAU_CHU_TRANG);
        timeLabel.setFont(FONT_LOGO);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return timeLabel;
    }

    // Tạo nhãn hiển thị ngày
    private JLabel taoNhanNgay() {
        JLabel dateLabel = new JLabel();
        dateLabel.setForeground(MAU_CHU_TRANG);
        dateLabel.setFont(FONT_NGAY);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return dateLabel;
    }

    // Thêm các mục menu chức năng vào panel menu
    private void themCacMenuItem() {
        themMenuItem("Hệ thống", "/IMG/setting_32px_v2.png", null, true);

        themMenuItem("Món ăn", "/IMG/monan_32px_v2.png",
                List.of("Tra cứu món ăn", "Thêm món ăn", "Cập nhật món ăn", "Thống kê món"), false);

        List<String> menuBanAn;
        if (isQuanLy) {
            menuBanAn = List.of("Quản lý đặt bàn", "Thêm bàn", "Cập nhật bàn", "Lịch sử hủy đặt bàn");
        } else {
            menuBanAn = List.of("Quản lý đặt bàn");
        }
        themMenuItem("Bàn ăn", "/IMG/banan_32px_v2.png", menuBanAn, false);

        List<String> menuNhanVien;
        if (isQuanLy) {
            menuNhanVien = List.of("Tra cứu nhân viên", "Thêm nhân viên", "Cập nhật nhân viên");
        } else {
            menuNhanVien = List.of("Tra cứu nhân viên");
        }
        themMenuItem("Nhân viên", "/IMG/nhanvien_32px_v2.png", menuNhanVien, false);

        themMenuItem("Khách hàng", "/IMG/khachhang_32px_v2.png",
                List.of("Tra cứu khách hàng", "Thêm khách hàng", "Cập nhật khách hàng", "Thống kê khách hàng"), false);

        themMenuItem("Hóa đơn", "/IMG/hoadon_32px_v2.png",
                List.of("Tra cứu hóa đơn", "Thống kê hóa đơn"), false);

        List<String> menuKhuyenMai;
        if (isQuanLy) {
            menuKhuyenMai = List.of("Tra cứu khuyến mãi", "Thêm khuyến mãi", "Cập nhật khuyến mãi","Thống kê khuyến mãi");
        } else {
            menuKhuyenMai = List.of("Tra cứu khuyến mãi", "Thống kê khuyến mãi");
        }
        themMenuItem("Khuyến mãi", "/IMG/khuyenmai_32px_v2.png", menuKhuyenMai, false);
    }

    // Tạo thanh cuộn cho menu
    private JScrollPane taoMenuScrollPane() {
        JScrollPane menuScrollPane = new JScrollPane(menuPanel);
        menuScrollPane.setBorder(null);
        menuScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return menuScrollPane;
    }

    // Tạo panel dưới cùng chứa thông tin user và nút đăng xuất
    private JPanel taoPanelDuoi() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(MAU_NEN_MENU_MAC_DINH);
        bottomPanel.setPreferredSize(KICH_THUOC_PANEL_DUOI);
        bottomPanel.setLayout(new BorderLayout(5, 0));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 15, 15));

        JLabel userLabel = taoNhanNguoiDung();
        bottomPanel.add(userLabel, BorderLayout.WEST);

        JLabel logoutButton = taoNutDangXuat();
        if (logoutButton != null) {
            bottomPanel.add(logoutButton, BorderLayout.EAST);
        }

        return bottomPanel;
    }

    // Tạo nhãn hiển thị tên nhân viên đang đăng nhập
    private JLabel taoNhanNguoiDung() {
        NhanVien nv = Auth.getCurrentNhanVien();
        String tenHienThi = "T3L Team";

        if (nv != null) {
            tenHienThi = nv.getHoTen();
        }

        JLabel userLabel = new JLabel(tenHienThi);
        userLabel.setForeground(MAU_CHU_TRANG);
        userLabel.setFont(FONT_NGUOI_DUNG);
        return userLabel;
    }

    // Tạo nút icon đăng xuất
    private JLabel taoNutDangXuat() {
        try {
            ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/IMG/dangxuat_64px.png"));
            Image logoutImage = logoutIcon.getImage().getScaledInstance(
                    KICH_THUOC_ICON_DANG_XUAT, KICH_THUOC_ICON_DANG_XUAT, Image.SCALE_SMOOTH);
            JLabel logoutButton = new JLabel(new ImageIcon(logoutImage));

            caiDatNutDangXuat(logoutButton);

            return logoutButton;
        } catch (Exception e) {
            System.err.println("Error loading logout icon: " + e.getMessage());
            return null;
        }
    }

    // Cài đặt sự kiện click cho nút đăng xuất
    private void caiDatNutDangXuat(JLabel logoutButton) {
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setToolTipText("Đăng xuất");
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                xuLyDangXuat();
            }
        });
    }

    // Xử lý logic khi người dùng chọn đăng xuất
    private void xuLyDangXuat() {
        int confirm = JOptionPane.showConfirmDialog(
                TrangChu_UI.this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            new DangNhap_UI().setVisible(true);
            dispose();
        }
    }

    // Thêm một item menu chính (cha) và các item con (nếu có)
    private void themMenuItem(String text, String iconPath, List<String> cacItemCon, boolean duocChon) {
        JPanel itemMenuCha = taoItemMenuCha(text, iconPath);
        menuPanel.add(itemMenuCha);
        tatCaCacItemMenuCoTheClick.add(itemMenuCha);

        JPanel subMenuPanel = taoSubMenuPanel(cacItemCon, itemMenuCha);
        menuPanel.add(subMenuPanel);

        ganSuKienChoItemMenuCha(itemMenuCha, text, cacItemCon, subMenuPanel);

        if (duocChon) {
            chonMenuItem(itemMenuCha);
        }
    }

    // Tạo giao diện cho item menu cha
    private JPanel taoItemMenuCha(String text, String iconPath) {
        JPanel itemPanel = new JPanel();
        itemPanel.setBackground(MAU_NEN_MENU_MAC_DINH);
        itemPanel.setMaximumSize(KICH_THUOC_ITEM_MENU);
        itemPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 25));
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        themIconVaoPanel(itemPanel, iconPath);
        themTextVaoPanel(itemPanel, text, FONT_MENU_CHA);

        return itemPanel;
    }

    // Thêm icon vào panel của item menu
    private void themIconVaoPanel(JPanel panel, String iconPath) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image image = icon.getImage().getScaledInstance(
                    KICH_THUOC_ICON_MENU, KICH_THUOC_ICON_MENU, Image.SCALE_SMOOTH);
            panel.add(new JLabel(new ImageIcon(image)));
        } catch (Exception e) {
            panel.add(new JLabel("!"));
            System.err.println("Error loading icon " + iconPath + ": " + e.getMessage());
        }
    }

    // Thêm text vào panel của item menu
    private void themTextVaoPanel(JPanel panel, String text, Font font) {
        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(MAU_CHU_TRANG);
        textLabel.setFont(font);
        panel.add(textLabel);
    }

    // Tạo panel chứa các item con (submenu)
    private JPanel taoSubMenuPanel(List<String> cacItemCon, JPanel itemMenuCha) {
        JPanel subMenuPanel = new JPanel();
        subMenuPanel.setBackground(MAU_NEN_MENU_MAC_DINH);
        subMenuPanel.setLayout(new BoxLayout(subMenuPanel, BoxLayout.Y_AXIS));
        subMenuPanel.setMaximumSize(new Dimension(
                KICH_THUOC_MENU.width,
                cacItemCon == null ? 0 : cacItemCon.size() * KICH_THUOC_ITEM_MENU_CON.height
        ));
        subMenuPanel.setVisible(false);

        if (cacItemCon != null && !cacItemCon.isEmpty()) {
            cacMenuCha.add(itemMenuCha);
            themCacItemCon(subMenuPanel, cacItemCon);
        }

        return subMenuPanel;
    }

    // Thêm danh sách item con vào submenu panel
    private void themCacItemCon(JPanel subMenuPanel, List<String> cacItemCon) {
        for (int i = 0; i < cacItemCon.size(); i++) {
            String textItemCon = cacItemCon.get(i);
            boolean laItemCuoi = (i == cacItemCon.size() - 1);

            JPanel itemConPanel = taoItemMenuCon(textItemCon, laItemCuoi);
            subMenuPanel.add(itemConPanel);
            tatCaCacItemMenuCoTheClick.add(itemConPanel);

            ganSuKienChoItemMenuCon(itemConPanel, textItemCon);
        }
    }

    // Tạo giao diện cho item menu con
    private JPanel taoItemMenuCon(String text, boolean laItemCuoi) {
        JPanel subItemPanel = new SubMenuItemPanel(laItemCuoi);
        subItemPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 45, 8));
        subItemPanel.setBackground(MAU_NEN_MENU_MAC_DINH);
        subItemPanel.setMaximumSize(KICH_THUOC_ITEM_MENU_CON);
        subItemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        themTextVaoPanel(subItemPanel, text, FONT_MENU_CON);

        return subItemPanel;
    }

    // Gắn sự kiện click và hover cho item menu cha
    private void ganSuKienChoItemMenuCha(JPanel itemMenuCha, String text,
                                         List<String> cacItemCon, JPanel subMenuPanel) {
        itemMenuCha.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cacItemCon == null || cacItemCon.isEmpty()) {
                    chonMenuItem(itemMenuCha);
                    hienThiNoiDung(text);
                    dongTatCaMenuDangMo();
                } else {
                    batTatSubMenu(itemMenuCha, subMenuPanel);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (itemMenuCha != itemMenuDangDuocChon) {
                    itemMenuCha.setBackground(MAU_NEN_MENU_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (itemMenuCha != itemMenuDangDuocChon) {
                    itemMenuCha.setBackground(MAU_NEN_MENU_MAC_DINH);
                }
            }
        });
    }

    // Gắn sự kiện click và hover cho item menu con
    private void ganSuKienChoItemMenuCon(JPanel itemMenuCon, String text) {
        itemMenuCon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chonMenuItem(itemMenuCon);
                hienThiNoiDung(text);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (itemMenuCon != itemMenuDangDuocChon) {
                    itemMenuCon.setBackground(MAU_NEN_MENU_CON_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (itemMenuCon != itemMenuDangDuocChon) {
                    itemMenuCon.setBackground(MAU_NEN_MENU_MAC_DINH);
                }
            }
        });
    }

    // Bật hoặc tắt hiển thị submenu
    private void batTatSubMenu(JPanel itemMenuCha, JPanel subMenu) {
        if (subMenu.isVisible()) {
            subMenu.setVisible(false);
            menuDangMoRong = null;
        } else {
            dongTatCaMenuDangMo();
            subMenu.setVisible(true);
            menuDangMoRong = itemMenuCha;
        }
        menuPanel.revalidate();
        menuPanel.repaint();
    }

    // Đóng tất cả các submenu đang mở
    private void dongTatCaMenuDangMo() {
        if (menuDangMoRong != null) {
            JPanel subMenuTuongUng = laySubMenuPanelCho(menuDangMoRong);
            if (subMenuTuongUng != null) {
                subMenuTuongUng.setVisible(false);
            }
            menuDangMoRong = null;
        }
    }

    // Tìm submenu panel tương ứng với menu cha
    private JPanel laySubMenuPanelCho(JPanel menuChaPanel) {
        int index = menuPanel.getComponentZOrder(menuChaPanel);
        if (index != -1 && index + 1 < menuPanel.getComponentCount()) {
            return (JPanel) menuPanel.getComponent(index + 1);
        }
        return null;
    }

    // Đánh dấu item menu đang được chọn (thay đổi màu nền)
    private void chonMenuItem(JPanel panelDuocChon) {
        if (itemMenuDangDuocChon != null) {
            if (cacMenuCha.contains(itemMenuDangDuocChon)) {
                itemMenuDangDuocChon.setBackground(MAU_NEN_MENU_MAC_DINH);
            } else {
                itemMenuDangDuocChon.setBackground(MAU_NEN_MENU_MAC_DINH);
            }
        }

        itemMenuDangDuocChon = panelDuocChon;
        itemMenuDangDuocChon.setBackground(MAU_NEN_MENU_DUOC_CHON);
    }

    // Cập nhật nhãn thời gian thực
    private void capNhatNgayGio(JLabel timeLabel, JLabel dateLabel) {
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        dateLabel.setText(now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    // Tạo panel chứa nội dung chính ở giữa màn hình
    private JPanel taoMainContentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(MAU_NEN_DEN);
        panel.setLayout(new BorderLayout());
        return panel;
    }

    // Chuyển đổi panel hiển thị dựa trên tên menu được chọn
    public void hienThiNoiDung(String tenTab) {
        chonMenuTheoTen(tenTab);

        if (tenTab.equals("Thêm khách hàng")) {
            chonMenuTheoTen(tenTab);

            Runnable refreshCallback = () -> {
                Component currentPanel = mainContentPanel.getComponent(0);
                if (currentPanel instanceof TraCuuKhachHang_UI) {
                    ((TraCuuKhachHang_UI) currentPanel).lamMoiGiaoDien();
                }
            };
            new ThemKhachHang_UI(TrangChu_UI.this, "", refreshCallback).setVisible(true);

            chonMenuTheoTen(tenTabHienTai);
            return;
        }

        if (tenTab.equals("Thêm bàn")) {
            chonMenuTheoTen(tenTab);

            new ThemBan_UI(this, () -> {
                if (tenTabHienTai.equals("Quản lý đặt bàn")) {
                    hienThiNoiDung("Quản lý đặt bàn");
                }
            }).setVisible(true);

            chonMenuTheoTen(tenTabHienTai);
            return;
        }
        chonMenuTheoTen(tenTab);
        this.tenTabHienTai = tenTab;
        mainContentPanel.removeAll();

        switch (tenTab) {
            case "Hệ thống":
                BackgroundPanel homePanel = new BackgroundPanel("/img/vipbackground.jpg");
                mainContentPanel.add(homePanel, BorderLayout.CENTER);
                break;

            case "Tra cứu món ăn":
                mainContentPanel.add(new TraCuuMonAn_UI(), BorderLayout.CENTER);
                break;

            case "Thêm món ăn":
                mainContentPanel.add(new ThemMonAn_UI(), BorderLayout.CENTER);
                break;

            case "Cập nhật món ăn":
                mainContentPanel.add(new CapNhatMon_UI(), BorderLayout.CENTER);
                break;

            case "Thống kê món":
                mainContentPanel.add(new ThongKeMonAn_UI(), BorderLayout.CENTER);
                break;

            case "Quản lý đặt bàn":
                hienThiLoading();

                SwingWorker<QuanLyDatBan_UI, Void> worker = new SwingWorker<QuanLyDatBan_UI, Void>() {
                    @Override
                    protected QuanLyDatBan_UI doInBackground() throws Exception {
                        return new QuanLyDatBan_UI();
                    }

                    @Override
                    protected void done() {
                        try {
                            QuanLyDatBan_UI quanLyDatBanUI = get();
                            mainContentPanel.removeAll();
                            mainContentPanel.add(quanLyDatBanUI, BorderLayout.CENTER);
                            mainContentPanel.revalidate();
                            mainContentPanel.repaint();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mainContentPanel.removeAll();
                            mainContentPanel.add(new JLabel("Lỗi tải giao diện!", SwingConstants.CENTER));
                            mainContentPanel.revalidate();
                            mainContentPanel.repaint();
                        }
                    }
                };
                worker.execute();
                return;

            case "Cập nhật bàn":
                mainContentPanel.add(new CapNhatBan_UI(), BorderLayout.CENTER);
                break;

            case "Lịch sử hủy đặt bàn":
                mainContentPanel.add(new LichSuHuyDatBan_UI(), BorderLayout.CENTER);
                break;

            case "Tra cứu nhân viên":
                mainContentPanel.add(new TraCuuNhanVien_UI(), BorderLayout.CENTER);
                break;

            case "Thêm nhân viên":
                mainContentPanel.add(new ThemNhanVien_UI(), BorderLayout.CENTER);
                break;

            case "Cập nhật nhân viên":
                mainContentPanel.add(new CapNhatNhanVien_UI(), BorderLayout.CENTER);
                break;

            case "Tra cứu khách hàng":
                mainContentPanel.add(new TraCuuKhachHang_UI(), BorderLayout.CENTER);
                break;

            case "Cập nhật khách hàng":
                mainContentPanel.add(new CapNhatKhachHang_UI(), BorderLayout.CENTER);
                break;

            case "Thống kê khách hàng":
                mainContentPanel.add(new ThongKeKhachHang_UI(), BorderLayout.CENTER);
                break;

            case "Tra cứu hóa đơn":
                mainContentPanel.add(new TraCuuHoaDon_UI(), BorderLayout.CENTER);
                break;

            case "Thống kê hóa đơn":
                mainContentPanel.add(new ThongKeHoaDon_UI(), BorderLayout.CENTER);
                break;

            case "Tra cứu khuyến mãi":
                mainContentPanel.add(new TraCuuKhuyenMai_UI(), BorderLayout.CENTER);
                break;

            case "Thêm khuyến mãi":
                mainContentPanel.add(new ThemKhuyenMai_UI(), BorderLayout.CENTER);
                break;

            case "Cập nhật khuyến mãi":
                mainContentPanel.add(new CapNhatKhuyenMai_UI(), BorderLayout.CENTER);
                break;

            case "Thống kê khuyến mãi":
                mainContentPanel.add(new ThongKeKhuyenMai_UI(), BorderLayout.CENTER);
                break;

            default:
                JLabel contentLabel = new JLabel("Nội dung cho: " + tenTab, SwingConstants.CENTER);
                contentLabel.setForeground(MAU_CHU_TRANG);
                contentLabel.setFont(FONT_NOI_DUNG);
                mainContentPanel.add(contentLabel, BorderLayout.CENTER);
                break;
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // Chọn menu dựa trên tên (String)
    private void chonMenuTheoTen(String tenMenu) {
        for (JPanel menuItem : tatCaCacItemMenuCoTheClick) {
            for (Component comp : menuItem.getComponents()) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    if (label.getText() != null && label.getText().equals(tenMenu)) {
                        chonMenuItem(menuItem);
                        return;
                    }
                }
            }
        }
    }

    // Inner class vẽ hình nền cho màn hình chính
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            setLayout(new BorderLayout());
            setBackground(MAU_NEN_DEN);

            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
                this.backgroundImage = icon.getImage();
            } catch (Exception e) {
                System.err.println("Error loading background image: " + e.getMessage());
                this.backgroundImage = null;
            }

            JLabel lblXinChao = taoNhanXinChao();
            add(lblXinChao, BorderLayout.CENTER);
        }
        private JLabel taoNhanXinChao() {
            JLabel lblXinChao = new JLabel("Xin Chào!", SwingConstants.CENTER);
            lblXinChao.setForeground(MAU_CHU_TRANG);
            lblXinChao.setFont(FONT_XIN_CHAO);
            return lblXinChao;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                g2d.drawImage(backgroundImage, 0, 0, w, h, this);
            }
        }
    }

    // Inner class vẽ đường kẻ cho item menu con
    private class SubMenuItemPanel extends JPanel {
        private final boolean laItemCuoi;
        private final Color MAU_DUONG_KE = new Color(90, 90, 90);
        private final int VI_TRI_X_DUONG_KE = 30;
        private final int VI_TRI_X_DUONG_NGANG = 40;

        public SubMenuItemPanel(boolean laItemCuoi) {
            this.laItemCuoi = laItemCuoi;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            veHinhDuongKe(g);
        }
        private void veHinhDuongKe(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(MAU_DUONG_KE);
            g2d.setStroke(new BasicStroke(1));

            int midY = getHeight() / 2;

            g2d.drawLine(VI_TRI_X_DUONG_KE, midY, VI_TRI_X_DUONG_NGANG, midY);
            if (laItemCuoi) {
                g2d.drawLine(VI_TRI_X_DUONG_KE, 0, VI_TRI_X_DUONG_KE, midY);
            } else {
                g2d.drawLine(VI_TRI_X_DUONG_KE, 0, VI_TRI_X_DUONG_KE, getHeight());
            }
        }
    }

    // Chuyển hướng đến màn hình cập nhật món ăn
    public void hienThiTrangCapNhatMon(MonAn monAn) {
        chonMenuTheoTen("Cập nhật món ăn");
        mainContentPanel.removeAll();
        CapNhatMon_UI panelCapNhat = new CapNhatMon_UI();
        panelCapNhat.chonMonAnDeCapNhat(monAn.getMaMon());
        mainContentPanel.add(panelCapNhat, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // Chuyển hướng đến màn hình cập nhật bàn ăn
    public void hienThiTrangCapNhatBan(BanAn ban) {

        mainContentPanel.removeAll();

        CapNhatBan_UI panelCapNhat = new CapNhatBan_UI();
        panelCapNhat.chonBanDeCapNhat(ban.getMaBan());

        mainContentPanel.add(panelCapNhat, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // Hiển thị panel loading khi tải dữ liệu nặng
    private void hienThiLoading() {
        mainContentPanel.removeAll();

        JPanel loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setBackground(MAU_NEN_DEN);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 30));

        progressBar.setForeground(MAU_NEN_MENU_DUOC_CHON);
        progressBar.setBackground(new Color(50, 50, 50));

        JLabel loadingLabel = new JLabel("Đang tải bàn ăn...");
        loadingLabel.setForeground(MAU_CHU_TRANG);
        loadingLabel.setFont(FONT_MENU_CHA);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);

        gbc.gridy = 0;
        loadingPanel.add(loadingLabel, gbc);

        gbc.gridy = 1;
        loadingPanel.add(progressBar, gbc);

        mainContentPanel.add(loadingPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    // Phương thức main khởi chạy ứng dụng
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TrangChu_UI().setVisible(true));
    }
}
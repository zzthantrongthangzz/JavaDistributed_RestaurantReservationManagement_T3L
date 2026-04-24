package ui.banan;

import dao.BanAn_DAO;
import entity.BanAn;
import dao.HoaDon_DAO;
import dao.HoaDon_Ban_DAO;
import entity.HoaDon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ChuyenBan_UI extends JDialog {
    private java.util.Date ngayChuyen;

    private JTextField txtTimKiem;
    private JTable tblBanTrong;
    private DefaultTableModel modelBanTrong;
    private JLabel lblBanHienTai;
    private JButton btnChuyenBan;
    private JButton btnHuy;
    private JButton btnLamMoi;

    private final BanAn_DAO banAnDAO;
    private final HoaDon_DAO hoaDonDAO;
    private final HoaDon_Ban_DAO hoaDonBanDAO;

    private final BanAn banHienTai;
    private BanAn banDuocChon = null;
    private List<BanAn> danhSachBanTrong;

    private final Color MAU_NEN = new Color(26, 28, 32);
    private final Color MAU_NEN_FORM = new Color(32, 35, 40);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_VIEN_INPUT = new Color(60, 65, 73);
    private final Color MAU_VIEN_INPUT_FOCUS = new Color(79, 134, 247);
    private final Color MAU_CHU_TRANG = new Color(240, 242, 245);
    private final Color MAU_CHU_XAM_NHAT = new Color(115, 120, 130);
    private final Color MAU_NUT_CHUYENBAN = new Color(34, 197, 94);
    private final Color MAU_NUT_CHUYENBAN_HOVER = new Color(22, 163, 74);
    private final Color MAU_NUT_HUY = new Color(239, 68, 68);
    private final Color MAU_NUT_HUY_HOVER = new Color(220, 38, 38);
    private final Color MAU_VIEN_DUOI = new Color(50, 54, 61);
    private final Color MAU_ACCENT = new Color(79, 134, 247);

    private final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 28);
    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_O_NHAP = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_NUT = new Font("Segoe UI", Font.BOLD, 15);
    private final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 14);

    // Khởi tạo giao diện chuyển bàn
    public ChuyenBan_UI(Frame parent, BanAn ban, java.util.Date ngayChuyen) {
        super(parent, "Chuyển bàn", true);
        this.banHienTai = ban;
        this.ngayChuyen = ngayChuyen;
        this.banAnDAO = new BanAn_DAO();
        this.hoaDonDAO = new HoaDon_DAO();
        this.hoaDonBanDAO = new HoaDon_Ban_DAO();

        khoiTaoGiaoDien();
        setSize(900, 750);
        setLocationRelativeTo(parent);
        setResizable(false);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        setTitle("Chuyển bàn - Ngày: " + sdf.format(ngayChuyen));

        taiDuLieuBanTrong();
    }

    // Thiết lập cấu trúc giao diện chính
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

    // Tạo panel hiển thị tiêu đề
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN);
        panel.setBorder(new EmptyBorder(35, 50, 25, 50));

        JLabel lblTieuDe = new JLabel("CHUYỂN BÀN");
        lblTieuDe.setFont(FONT_TIEU_DE);
        lblTieuDe.setForeground(MAU_CHU_TRANG);
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lblTieuDe);

        return panel;
    }

    // Tạo panel chứa form tìm kiếm và danh sách bàn
    private JPanel taoPanelForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel pBanHienTai = taoPanelBanHienTai();
        panel.add(pBanHienTai);
        panel.add(Box.createVerticalStrut(25));

        JSeparator separator = new JSeparator();
        separator.setForeground(MAU_VIEN_DUOI);
        separator.setBackground(MAU_VIEN_DUOI);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(25));

        JPanel pTimKiem = taoPanelTimKiem();
        panel.add(pTimKiem);
        panel.add(Box.createVerticalStrut(20));

        String[] cols = { "Mã bàn", "Tên bàn", "Loại bàn", "Sức chứa", "Trạng thái" };
        modelBanTrong = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBanTrong = new JTable(modelBanTrong);
        setupTableStyle(tblBanTrong);

        tblBanTrong.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblBanTrong.getSelectedRow();
                if (selectedRow != -1) {
                    String maBan = tblBanTrong.getValueAt(selectedRow, 0).toString();
                    banDuocChon = timBanTheoMa(maBan);
                    capNhatLabelBanHienTai();
                    btnChuyenBan.setEnabled(true);
                } else {
                    banDuocChon = null;
                    capNhatLabelBanHienTai();
                    btnChuyenBan.setEnabled(false);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblBanTrong);
        tuyChinhScrollBar(scrollPane);
        scrollPane.getViewport().setBackground(MAU_NEN_INPUT);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        JPanel corner = new JPanel();
        corner.setBackground(MAU_NEN_INPUT);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300));
        panel.add(scrollPane);
        return panel;
    }

    // Tạo panel hiển thị thông tin bàn hiện tại
    private JPanel taoPanelBanHienTai() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLabel = new JLabel("Bàn hiện tại:");
        lblLabel.setFont(FONT_NHAN);
        lblLabel.setForeground(MAU_CHU_TRANG);

        lblBanHienTai = new JLabel(banHienTai.getTenBan());
        lblBanHienTai.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBanHienTai.setForeground(MAU_ACCENT);

        panel.add(lblLabel);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(lblBanHienTai);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    // Tạo panel chức năng tìm kiếm
    private JPanel taoPanelTimKiem() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(MAU_NEN_FORM);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("Tìm kiếm:");
        label.setFont(FONT_NHAN);
        label.setForeground(MAU_CHU_TRANG);

        txtTimKiem = taoTextField("Nhập mã bàn hoặc tên bàn");
        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                locVaHienThiBanTrong();
            }
        });

        JButton btnTim = taoNut("Tìm", MAU_ACCENT, new Color(65, 115, 220));
        btnTim.setPreferredSize(new Dimension(100, 42));
        btnTim.addActionListener(e -> locVaHienThiBanTrong());

        panel.add(label);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(txtTimKiem);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(btnTim);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    // Tạo ô nhập liệu với style tùy chỉnh
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

    // Tạo panel chứa các nút tác vụ
    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(MAU_NEN);
        panel.setBorder(new EmptyBorder(25, 50, 35, 50));

        btnLamMoi = taoNut("Làm mới", new Color(100, 100, 100), new Color(120, 120, 120));
        btnLamMoi.addActionListener(e -> xuLyLamMoi());

        btnChuyenBan = taoNut("Chuyển", MAU_NUT_CHUYENBAN, MAU_NUT_CHUYENBAN_HOVER);
        btnChuyenBan.setEnabled(false);
        btnChuyenBan.addActionListener(e -> xuLyChuyenBan());

        btnHuy = taoNut("Hủy", MAU_NUT_HUY, MAU_NUT_HUY_HOVER);
        btnHuy.addActionListener(e -> dispose());

        panel.add(btnLamMoi);
        panel.add(btnChuyenBan);
        panel.add(btnHuy);

        return panel;
    }

    // Tạo nút bấm với màu sắc và sự kiện hover
    private JButton taoNut(String text, Color mauNen, Color mauHover) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(160, 44));
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

    // Thiết lập giao diện cho bảng danh sách
    private void setupTableStyle(JTable t) {
        t.setRowHeight(40);
        t.setFont(FONT_TABLE);
        t.setBackground(MAU_NEN_INPUT);
        t.setForeground(MAU_CHU_TRANG);
        t.getTableHeader().setBackground(MAU_NEN_INPUT);
        t.getTableHeader().setForeground(MAU_CHU_TRANG);
        t.getTableHeader().setFont(FONT_TABLE_HEADER);
        t.getTableHeader().setPreferredSize(new Dimension(0, 45));
        t.setSelectionBackground(MAU_ACCENT);
        t.setSelectionForeground(Color.WHITE);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // Tải danh sách bàn trống từ cơ sở dữ liệu
    private void taiDuLieuBanTrong() {
        danhSachBanTrong = banAnDAO.layDanhSachBanTrongTheoNgay(this.ngayChuyen);
        hienThiBanTrong(danhSachBanTrong);
    }

    // Hiển thị danh sách bàn lên bảng
    private void hienThiBanTrong(List<BanAn> danhSach) {
        modelBanTrong.setRowCount(0);
        for (BanAn ban : danhSach) {
            Object[] row = { ban.getMaBan(), ban.getTenBan(), ban.getLoaiBan(), ban.getSucChua(), ban.getTrangThai() };
            modelBanTrong.addRow(row);
        }
    }

    // Lọc danh sách bàn theo từ khóa tìm kiếm
    private void locVaHienThiBanTrong() {
        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        List<BanAn> danhSachLoc = new ArrayList<>();
        for (BanAn ban : danhSachBanTrong) {
            if (tuKhoa.isEmpty() || ban.getMaBan().toLowerCase().contains(tuKhoa)
                    || ban.getTenBan().toLowerCase().contains(tuKhoa)) {
                danhSachLoc.add(ban);
            }
        }
        hienThiBanTrong(danhSachLoc);
    }

    // Tìm đối tượng bàn theo mã bàn
    private BanAn timBanTheoMa(String maBan) {
        for (BanAn ban : danhSachBanTrong) {
            if (ban.getMaBan().equals(maBan)) {
                return ban;
            }
        }
        return null;
    }

    // Cập nhật nhãn hiển thị quá trình chuyển
    private void capNhatLabelBanHienTai() {
        if (banDuocChon != null) {
            lblBanHienTai.setText(banHienTai.getTenBan() + " >> " + banDuocChon.getTenBan());
        } else {
            lblBanHienTai.setText(banHienTai.getTenBan() + " >> ");
        }
    }

    // Xử lý logic khi nhấn nút chuyển bàn
    private void xuLyChuyenBan() {
        if (banDuocChon == null) {
            hienThiLoi("Vui lòng chọn bàn đích!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Xác nhận chuyển từ %s sang %s?", banHienTai.getTenBan(), banDuocChon.getTenBan()),
                "Xác nhận chuyển bàn", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        String trangThaiHienTai = banHienTai.getTrangThai();

        if (trangThaiHienTai.equals("Bàn đang phục vụ")) {
            HoaDon hoaDon = hoaDonDAO.timHoaDonChuaThanhToanTheoMaBan(banHienTai.getMaBan());
            if (hoaDon != null) {
                if (!hoaDonBanDAO.chuyenBan(hoaDon.getMaHoaDon(), banHienTai.getMaBan(), banDuocChon.getMaBan())) {
                    hienThiLoi("Lỗi cập nhật hóa đơn (chuyển bàn)!");
                    return;
                }
                banAnDAO.capNhatTrangThaiBan(banHienTai.getMaBan(), "Bàn đang trống");
                banAnDAO.capNhatTrangThaiBan(banDuocChon.getMaBan(), "Bàn đang phục vụ");

                hienThiThanhCong("Chuyển bàn thành công!");
                dispose();
            } else {
                hienThiLoi("Lỗi: Bàn đang phục vụ nhưng không tìm thấy hóa đơn!");
            }
        } else if (trangThaiHienTai.equals("Bàn đang chờ")) {
            dao.PhieuDatBan_DAO phieuDAO = new dao.PhieuDatBan_DAO();
            boolean ketQua = phieuDAO.chuyenBanDatTruoc(banHienTai.getMaBan(), banDuocChon.getMaBan(), ngayChuyen);

            if (ketQua) {
                java.util.Date homNay = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");

                boolean isHomNay = sdf.format(ngayChuyen).equals(sdf.format(homNay));
                if (isHomNay) {
                    banAnDAO.capNhatTrangThaiBan(banHienTai.getMaBan(), "Bàn đang trống");
                    banAnDAO.capNhatTrangThaiBan(banDuocChon.getMaBan(), "Bàn đang chờ");
                }
                hienThiThanhCong("Chuyển bàn đặt trước thành công!");
                dispose();
            } else {
                hienThiLoi("Không tìm thấy phiếu đặt hoặc lỗi cập nhật! Hãy kiểm tra lại ngày xem.");
            }
        }
    }

    // Tùy chỉnh thanh cuộn cho bảng
    private void tuyChinhScrollBar(JScrollPane s) {
        s.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        s.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
        s.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 105, 120);
                this.trackColor = MAU_NEN_INPUT;
            }

            protected JButton createDecreaseButton(int o) {
                return new JButton() {
                    {
                        setPreferredSize(new Dimension(0, 0));
                    }
                };
            }

            protected JButton createIncreaseButton(int o) {
                return new JButton() {
                    {
                        setPreferredSize(new Dimension(0, 0));
                    }
                };
            }
        });
    }

    // Làm mới form về trạng thái ban đầu
    private void xuLyLamMoi() {
        txtTimKiem.setText("");
        tblBanTrong.clearSelection();
        banDuocChon = null;
        capNhatLabelBanHienTai();
        btnChuyenBan.setEnabled(false);
        taiDuLieuBanTrong();
    }

    // Hiển thị hộp thoại báo lỗi
    private void hienThiLoi(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // Hiển thị hộp thoại thông báo thành công
    private void hienThiThanhCong(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
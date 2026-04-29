package ui;
import java.awt.*;
import java.rmi.Naming;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import rmi_interfaces.*;
import entity.BanAn;

public class TrangChu_UI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane, pnlMain;
    private IBanAn_Service banAnService;
    private JLabel lblNhanVien;

    public TrangChu_UI() {
        initRMI();
        setTitle("Hệ thống quản lý nhà hàng T3L");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        JPanel pnlMenu = new JPanel();
        pnlMenu.setBackground(new Color(45, 52, 54));
        pnlMenu.setPreferredSize(new Dimension(250, 0));
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        contentPane.add(pnlMenu, BorderLayout.WEST);

        lblNhanVien = new JLabel("Chào: " + (Auth.isLogin() ? Auth.getCurrentNhanVien().getHoTen() : "Guest"));
        lblNhanVien.setForeground(Color.WHITE);
        lblNhanVien.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlMenu.add(lblNhanVien);

        String[] menuItems = {"Sơ đồ bàn", "Quản lý món ăn", "Quản lý khách hàng", "Lập hóa đơn", "Thống kê", "Đăng xuất"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(250, 50));
            btn.setBackground(new Color(45, 52, 54));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
            btn.addActionListener(e -> handleMenu(item));
            pnlMenu.add(btn);
        }

        pnlMain = new JPanel(new BorderLayout());
        pnlMain.setBackground(Color.WHITE);
        contentPane.add(pnlMain, BorderLayout.CENTER);

        loadBanAn();
    }

    private void initRMI() {
        try {
            banAnService = (IBanAn_Service) Naming.lookup("rmi://localhost:1099/BanAnService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối Server!");
        }
    }

    private void handleMenu(String item) {
        if (item.equals("Đăng xuất")) {
            Auth.logout();
            new DangNhap_UI().setVisible(true);
            dispose();
        } else if (item.equals("Sơ đồ bàn")) {
            loadBanAn();
        }
    }

    private void loadBanAn() {
        pnlMain.removeAll();
        pnlMain.add(new JLabel("Đang tải dữ liệu...", SwingConstants.CENTER), BorderLayout.CENTER);
        pnlMain.revalidate();
        pnlMain.repaint();

        SwingWorker<List<BanAn>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<BanAn> doInBackground() throws Exception {
                return banAnService.docDanhSachBan();
            }

            @Override
            protected void done() {
                try {
                    List<BanAn> ds = get();
                    pnlMain.removeAll();
                    JPanel pnlGrid = new JPanel(new GridLayout(0, 5, 15, 15));
                    pnlGrid.setBorder(new EmptyBorder(20, 20, 20, 20));
                    for (BanAn b : ds) {
                        JButton btnBan = new JButton(b.getTenBan());
                        btnBan.setPreferredSize(new Dimension(100, 100));
                        btnBan.setBackground(b.getTrangThai().equals("Bàn đang trống") ? Color.GREEN : Color.RED);
                        pnlGrid.add(btnBan);
                    }
                    JScrollPane scroll = new JScrollPane(pnlGrid);
                    pnlMain.add(scroll, BorderLayout.CENTER);
                    pnlMain.revalidate();
                    pnlMain.repaint();
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }
}
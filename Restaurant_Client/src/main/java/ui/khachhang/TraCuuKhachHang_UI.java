package ui.khachhang;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.rmi.Naming;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

import entity.KhachHang;
import rmi_interfaces.IKhachHang_Service;

public class TraCuuKhachHang_UI extends JPanel {

    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
    private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
    private final Color MAU_CHON_HANG = new Color(70, 72, 90);

    private final int KICH_THUOC_ICON = 24;
    private final int CHIEU_CAO_HANG_BANG = 40;
    private final int CHIEU_CAO_HEADER_BANG = 45;
    private final Dimension KICH_THUOC_THANH_TIM_KIEM = new Dimension(200, 40);

    private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 14);

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiemMa, txtTimKiemTen, txtTimKiemSDT;
    private JComboBox<String> cmbTimKiemGioiTinh;
    private JPanel panelChinh;
    private IKhachHang_Service khachHangDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public TraCuuKhachHang_UI() {
        try {
            khachHangDAO = (IKhachHang_Service) Naming.lookup("rmi://localhost:1099/KhachHangService");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }
        khoiTaoGiaoDien();
        docDuLieuTuSQL();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB);

        panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(20, 25, 20, 25));
        panelChinh.setFocusable(true);

        panelChinh.add(taoPanelDieuKhien(), BorderLayout.NORTH);
        panelChinh.add(taoPanelNoiDung(), BorderLayout.CENTER);

        add(panelChinh, BorderLayout.CENTER);
        panelChinh.requestFocusInWindow();
    }

    private JPanel taoPanelDieuKhien() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 100));

        JPanel containerTimKiem = new JPanel();
        containerTimKiem.setLayout(new BoxLayout(containerTimKiem, BoxLayout.X_AXIS));
        containerTimKiem.setBackground(MAU_NEN_TAB);

        txtTimKiemMa = taoTextFieldTimKiem("Tìm theo mã KH...");
        txtTimKiemTen = taoTextFieldTimKiem("Tìm theo tên...");
        txtTimKiemSDT = taoTextFieldTimKiem("Tìm theo SĐT...");

        cmbTimKiemGioiTinh = taoComboBox(new String[]{"Tất cả", "Nam", "Nữ"}, new Dimension(150, 40));
        cmbTimKiemGioiTinh.addActionListener(e -> apDungTatCaBoLoc());

        containerTimKiem.add(taoWrapper("Mã KH:", txtTimKiemMa));
        containerTimKiem.add(Box.createRigidArea(new Dimension(10, 0)));
        containerTimKiem.add(taoWrapper("Họ Tên:", txtTimKiemTen));
        containerTimKiem.add(Box.createRigidArea(new Dimension(10, 0)));
        containerTimKiem.add(taoWrapper("SĐT:", txtTimKiemSDT));
        containerTimKiem.add(Box.createRigidArea(new Dimension(10, 0)));
        containerTimKiem.add(taoWrapper("Giới Tính:", cmbTimKiemGioiTinh));

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelNut.setBackground(MAU_NEN_TAB);
        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setFont(FONT_NHAN);
        btnLamMoi.setBackground(new Color(33, 150, 243));
        btnLamMoi.setForeground(Color.WHITE);
        btnLamMoi.setPreferredSize(new Dimension(115, 40));
        btnLamMoi.addActionListener(e -> lamMoiGiaoDien());
        panelNut.add(btnLamMoi);

        JLabel lblTieuDe = new JLabel("TRA CỨU KHÁCH HÀNG", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 35));
        lblTieuDe.setForeground(Color.WHITE);
        panel.add(lblTieuDe, BorderLayout.NORTH);

        panel.add(containerTimKiem, BorderLayout.WEST);
        panel.add(panelNut, BorderLayout.EAST);
        return panel;
    }

    private JPanel taoWrapper(String lblText, JComponent comp) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(MAU_NEN_TAB);
        JLabel lbl = new JLabel(lblText);
        lbl.setFont(FONT_NHAN);
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(new Color(124, 124, 124));
        lbl.setOpaque(true);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        wrap.add(lbl, BorderLayout.WEST);
        wrap.add(comp, BorderLayout.CENTER);
        wrap.setMaximumSize(new Dimension(300, 40));
        return wrap;
    }

    private JTextField taoTextFieldTimKiem(String placeholder) {
        JTextField txt = new JTextField(placeholder);
        txt.setForeground(MAU_PLACEHOLDER);
        txt.setBackground(MAU_THANH_TIM_KIEM);
        txt.setCaretColor(Color.WHITE);
        txt.setFont(FONT_TEXTFIELD);
        txt.setPreferredSize(KICH_THUOC_THANH_TIM_KIEM);
        txt.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        txt.addActionListener(e -> apDungTatCaBoLoc());
        txt.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txt.getText().equals(placeholder)) { txt.setText(""); txt.setForeground(Color.WHITE); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txt.getText().isEmpty()) { txt.setText(placeholder); txt.setForeground(MAU_PLACEHOLDER); }
            }
        });
        return txt;
    }

    private JComboBox<String> taoComboBox(String[] items, Dimension size) {
        JComboBox<String> cmb = new JComboBox<>(items);
        cmb.setFont(FONT_NHAN);
        cmb.setBackground(MAU_THANH_TIM_KIEM);
        cmb.setForeground(Color.WHITE);
        cmb.setPreferredSize(size);
        cmb.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        return cmb;
    }

    private JPanel taoPanelNoiDung() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);

        String[] cols = {"Mã KH", "Họ tên", "Số điện thoại", "Email", "Địa chỉ", "Ngày sinh", "Giới tính", "Điểm"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setBackground(MAU_NEN_ITEM);
        table.setForeground(Color.WHITE);
        table.setRowHeight(CHIEU_CAO_HANG_BANG);
        table.setFont(FONT_BANG);
        table.setGridColor(MAU_LUOI_BANG);

        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_ITEM);
        header.setForeground(Color.WHITE);
        header.setFont(FONT_HEADER_BANG);
        header.setPreferredSize(new Dimension(0, CHIEU_CAO_HEADER_BANG));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(MAU_NEN_ITEM);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void docDuLieuTuSQL() {
        if(khachHangDAO == null) return;
        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override protected List<KhachHang> doInBackground() throws Exception {
                return khachHangDAO.docDanhSachKhachHang();
            }
            @Override protected void done() {
                try { hienThiDanhSach(get()); } catch(Exception e) {}
            }
        };
        worker.execute();
    }

    private void hienThiDanhSach(List<KhachHang> ds) {
        tableModel.setRowCount(0);
        for(KhachHang kh : ds) {
            tableModel.addRow(new Object[]{
                    kh.getMaKhachHang(), kh.getHoTen(), kh.getSoDienThoai(),
                    kh.getEmail(), kh.getDiaChi(),
                    kh.getNgaySinh() != null ? dateFormat.format(kh.getNgaySinh()) : "N/A",
                    kh.isGioiTinh() ? "Nam" : "Nữ", kh.getTichDiem()
            });
        }
    }

    private void lamMoiGiaoDien() {
        txtTimKiemMa.setText("Tìm theo mã KH..."); txtTimKiemMa.setForeground(MAU_PLACEHOLDER);
        txtTimKiemTen.setText("Tìm theo tên..."); txtTimKiemTen.setForeground(MAU_PLACEHOLDER);
        txtTimKiemSDT.setText("Tìm theo SĐT..."); txtTimKiemSDT.setForeground(MAU_PLACEHOLDER);
        cmbTimKiemGioiTinh.setSelectedIndex(0);
        docDuLieuTuSQL();
    }

    private void apDungTatCaBoLoc() {
        if(khachHangDAO == null) return;
        String ma = txtTimKiemMa.getText().trim().equals("Tìm theo mã KH...") ? "" : txtTimKiemMa.getText().trim();
        String ten = txtTimKiemTen.getText().trim().equals("Tìm theo tên...") ? "" : txtTimKiemTen.getText().trim();
        String sdt = txtTimKiemSDT.getText().trim().equals("Tìm theo SĐT...") ? "" : txtTimKiemSDT.getText().trim();
        int gtIndex = cmbTimKiemGioiTinh.getSelectedIndex();

        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override protected List<KhachHang> doInBackground() throws Exception {
                List<KhachHang> kq = null;
                if(!ma.isEmpty()) kq = khachHangDAO.timKiemTheoMa(ma);
                else if(!ten.isEmpty()) kq = khachHangDAO.timKiemTheoTen(ten);
                else if(!sdt.isEmpty()) kq = khachHangDAO.timKiemTheoSDT(sdt);
                else if(gtIndex == 1) kq = khachHangDAO.locKhachHangTheoGioiTinh(true);
                else if(gtIndex == 2) kq = khachHangDAO.locKhachHangTheoGioiTinh(false);
                else kq = khachHangDAO.docDanhSachKhachHang();
                return kq;
            }
            @Override protected void done() {
                try {
                    List<KhachHang> ds = get();
                    hienThiDanhSach(ds);
                    if(ds.isEmpty()) JOptionPane.showMessageDialog(TraCuuKhachHang_UI.this, "Không tìm thấy khách hàng phù hợp!");
                } catch(Exception e) {}
            }
        };
        worker.execute();
    }
}
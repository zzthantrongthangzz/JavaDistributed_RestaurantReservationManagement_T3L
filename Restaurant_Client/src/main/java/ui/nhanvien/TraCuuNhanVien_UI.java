package ui.nhanvien;
import java.awt.*;
import java.util.List;
import java.rmi.Naming;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import entity.NhanVien;
import rmi_interfaces.INhanVien_Service;

public class TraCuuNhanVien_UI extends JPanel {
    private INhanVien_Service nhanVienService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;

    public TraCuuNhanVien_UI() {
        initRMI();
        setLayout(new BorderLayout());
        JPanel pnlTop = new JPanel();
        pnlTop.add(new JLabel("Nhập tên hoặc mã:"));
        txtTimKiem = new JTextField(20);
        pnlTop.add(txtTimKiem);
        JButton btnTim = new JButton("Tìm kiếm");
        pnlTop.add(btnTim);
        add(pnlTop, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã NV", "Họ tên", "SĐT", "Email", "Chức vụ"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnTim.addActionListener(e -> xuLyTim());
    }

    private void initRMI() {
        try { nhanVienService = (INhanVien_Service) Naming.lookup("rmi://localhost:1099/NhanVienService"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void xuLyTim() {
        String key = txtTimKiem.getText().trim();
        new SwingWorker<List<NhanVien>, Void>() {
            @Override protected List<NhanVien> doInBackground() throws Exception {
                if(key.isEmpty()) return nhanVienService.getAllNhanVien();
                return nhanVienService.timKiemNhanVienTheoTen(key);
            }
            @Override protected void done() {
                try {
                    List<NhanVien> ds = get();
                    tableModel.setRowCount(0);
                    for(NhanVien nv : ds) {
                        tableModel.addRow(new Object[]{nv.getMaNhanVien(), nv.getHoTen(), nv.getSoDienThoai(), nv.getEmail(), nv.getChucVu().getTenChucVu()});
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }
}
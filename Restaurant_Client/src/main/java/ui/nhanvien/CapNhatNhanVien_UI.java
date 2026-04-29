package ui.nhanvien;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.util.List;
import java.rmi.Naming;
import rmi_interfaces.INhanVien_Service;
import rmi_interfaces.IChucVu_Service;
import entity.NhanVien;
import entity.ChucVu;

public class CapNhatNhanVien_UI extends JPanel {
    private INhanVien_Service nhanVienService;
    private IChucVu_Service chucVuService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtHoTen, txtSDT, txtEmail, txtDiaChi;
    private JComboBox<ChucVu> cmbChucVu;
    private JComboBox<String> cmbGioiTinh;

    public CapNhatNhanVien_UI() {
        initRMI();
        setLayout(new BorderLayout());

        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 10, 10));
        pnlInput.setBorder(new EmptyBorder(10, 10, 10, 10));

        pnlInput.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        pnlInput.add(txtHoTen);

        pnlInput.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        pnlInput.add(txtSDT);

        pnlInput.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        pnlInput.add(txtEmail);

        pnlInput.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        pnlInput.add(txtDiaChi);

        pnlInput.add(new JLabel("Chức vụ:"));
        cmbChucVu = new JComboBox<>();
        pnlInput.add(cmbChucVu);

        pnlInput.add(new JLabel("Giới tính:"));
        cmbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        pnlInput.add(cmbGioiTinh);

        add(pnlInput, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã NV", "Họ tên", "Giới tính", "SĐT", "Email", "Chức vụ"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlAction = new JPanel();
        JButton btnCapNhat = new JButton("Cập nhật");
        JButton btnXoa = new JButton("Xóa (Nghỉ việc)");
        pnlAction.add(btnCapNhat);
        pnlAction.add(btnXoa);
        add(pnlAction, BorderLayout.SOUTH);

        btnCapNhat.addActionListener(e -> capNhatNhanVien());
        btnXoa.addActionListener(e -> xoaNhanVien());

        loadData();
    }

    private void initRMI() {
        try {
            nhanVienService = (INhanVien_Service) Naming.lookup("rmi://localhost:1099/NhanVienService");
            chucVuService = (IChucVu_Service) Naming.lookup("rmi://localhost:1099/ChucVuService");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadData() {
        new SwingWorker<List<NhanVien>, Void>() {
            @Override protected List<NhanVien> doInBackground() throws Exception {
                List<ChucVu> dsCV = chucVuService.docDanhSachChucVu();
                SwingUtilities.invokeLater(() -> {
                    cmbChucVu.removeAllItems();
                    for(ChucVu cv : dsCV) cmbChucVu.addItem(cv);
                });
                return nhanVienService.getAllNhanVien();
            }
            @Override protected void done() {
                try {
                    List<NhanVien> ds = get();
                    tableModel.setRowCount(0);
                    for(NhanVien nv : ds) {
                        tableModel.addRow(new Object[]{nv.getMaNhanVien(), nv.getHoTen(), nv.isGioiTinh() ? "Nam" : "Nữ", nv.getSoDienThoai(), nv.getEmail(), nv.getChucVu().getTenChucVu()});
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    private void capNhatNhanVien() {
        int row = table.getSelectedRow();
        if(row == -1) return;
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(tableModel.getValueAt(row, 0).toString());
        nv.setHoTen(txtHoTen.getText());
        nv.setSoDienThoai(txtSDT.getText());
        nv.setEmail(txtEmail.getText());
        nv.setDiaChi(txtDiaChi.getText());
        nv.setGioiTinh(cmbGioiTinh.getSelectedIndex() == 0);
        nv.setChucVu((ChucVu) cmbChucVu.getSelectedItem());

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception { return nhanVienService.capNhatNhanVien(nv); }
            @Override protected void done() {
                try { if(get()) { JOptionPane.showMessageDialog(null, "Cập nhật thành công"); loadData(); } }
                catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    private void xoaNhanVien() {
        int row = table.getSelectedRow();
        if(row == -1) return;
        String ma = tableModel.getValueAt(row, 0).toString();
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception { return nhanVienService.xoaNhanVien(ma); }
            @Override protected void done() {
                try { if(get()) { JOptionPane.showMessageDialog(null, "Đã cho nhân viên nghỉ việc"); loadData(); } }
                catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }
}
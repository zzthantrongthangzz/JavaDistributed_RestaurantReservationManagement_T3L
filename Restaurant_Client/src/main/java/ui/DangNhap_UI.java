package ui;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import rmi_interfaces.INhanVien_Service;
import entity.NhanVien;

public class DangNhap_UI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private INhanVien_Service nhanVienService;
	private JButton btnDangNhap;
	private JTextField txtTaiKhoan;
	private JPasswordField txtMatKhau;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				DangNhap_UI frame = new DangNhap_UI();
				frame.setVisible(true);
				frame.setLocationRelativeTo(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public DangNhap_UI() {
		try {
			nhanVienService = (INhanVien_Service) Naming.lookup("rmi://localhost:1099/NhanVienService");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Không thể kết nối đến Server!", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
		}

		setTitle("Đăng nhập - Nhà hàng T3L");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 20, 414, 30);
		contentPane.add(lblTitle);

		JLabel lblUser = new JLabel("Tên đăng nhập:");
		lblUser.setBounds(50, 80, 100, 25);
		contentPane.add(lblUser);

		txtTaiKhoan = new JTextField();
		txtTaiKhoan.setBounds(160, 80, 200, 25);
		contentPane.add(txtTaiKhoan);

		JLabel lblPass = new JLabel("Mật khẩu:");
		lblPass.setBounds(50, 120, 100, 25);
		contentPane.add(lblPass);

		txtMatKhau = new JPasswordField();
		txtMatKhau.setBounds(160, 120, 200, 25);
		contentPane.add(txtMatKhau);

		btnDangNhap = new JButton("Đăng nhập");
		btnDangNhap.setBounds(160, 170, 100, 30);
		contentPane.add(btnDangNhap);

		btnDangNhap.addActionListener(e -> xuLyDangNhap());
	}

	private void xuLyDangNhap() {
		String user = txtTaiKhoan.getText().trim();
		String pass = new String(txtMatKhau.getPassword());

		if (user.isEmpty() || pass.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
			return;
		}

		btnDangNhap.setEnabled(false);
		btnDangNhap.setText("Đang xử lý...");

		SwingWorker<NhanVien, Void> worker = new SwingWorker<>() {
			@Override
			protected NhanVien doInBackground() throws Exception {
				return nhanVienService.xacThucDangNhap(user, pass);
			}

			@Override
			protected void done() {
				try {
					NhanVien nv = get();
					if (nv != null) {
						Auth.login(nv);
						new TrangChu_UI().setVisible(true);
						dispose();
					} else {
						JOptionPane.showMessageDialog(DangNhap_UI.this, "Tài khoản hoặc mật khẩu sai!");
						txtMatKhau.setText("");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					btnDangNhap.setEnabled(true);
					btnDangNhap.setText("Đăng nhập");
				}
			}
		};
		worker.execute();
	}
}
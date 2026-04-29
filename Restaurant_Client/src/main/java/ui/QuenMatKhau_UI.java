package ui;
import java.awt.*;
import java.rmi.Naming;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import rmi_interfaces.ITaiKhoan_Service;

public class QuenMatKhau_UI extends JDialog {
	private static final long serialVersionUID = 1L;
	private ITaiKhoan_Service taiKhoanService;
	private JTextField txtTaiKhoan, txtMaXacThuc;
	private JPasswordField txtMatKhauMoi, txtNhapLai;
	private JButton btnGuiMa, btnXacNhan;
	private String maHeThong = "";

	public QuenMatKhau_UI(JFrame parent) {
		super(parent, "Quên mật khẩu", true);
		try {
			taiKhoanService = (ITaiKhoan_Service) Naming.lookup("rmi://localhost:1099/TaiKhoanService");
		} catch (Exception e) {
			e.printStackTrace();
		}

		setSize(400, 350);
		setLocationRelativeTo(parent);
		JPanel content = new JPanel();
		content.setBorder(new EmptyBorder(20, 20, 20, 20));
		content.setLayout(new GridLayout(6, 2, 10, 10));
		setContentPane(content);

		content.add(new JLabel("Tên đăng nhập:"));
		txtTaiKhoan = new JTextField();
		content.add(txtTaiKhoan);

		content.add(new JLabel(""));
		btnGuiMa = new JButton("Gửi mã qua Email");
		content.add(btnGuiMa);

		content.add(new JLabel("Mã xác thực:"));
		txtMaXacThuc = new JTextField();
		content.add(txtMaXacThuc);

		content.add(new JLabel("Mật khẩu mới:"));
		txtMatKhauMoi = new JPasswordField();
		content.add(txtMatKhauMoi);

		content.add(new JLabel("Nhập lại MK:"));
		txtNhapLai = new JPasswordField();
		content.add(txtNhapLai);

		content.add(new JLabel(""));
		btnXacNhan = new JButton("Đổi mật khẩu");
		content.add(btnXacNhan);

		btnGuiMa.addActionListener(e -> guiMaXacThuc());
		btnXacNhan.addActionListener(e -> doiMatKhau());
	}

	private void guiMaXacThuc() {
		String tk = txtTaiKhoan.getText().trim();
		if (tk.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập tên tài khoản!");
			return;
		}

		btnGuiMa.setEnabled(false);
		btnGuiMa.setText("Đang gửi...");

		SwingWorker<String, Void> worker = new SwingWorker<>() {
			@Override
			protected String doInBackground() throws Exception {
				String email = taiKhoanService.layEmailTheoTenDangNhap(tk);
				if (email == null) return null;
				String ma = taiKhoanService.taoDuMaXacThuc();
				if (taiKhoanService.guiEmailXacThuc(email, ma, tk)) return ma;
				return "";
			}

			@Override
			protected void done() {
				try {
					String result = get();
					if (result == null) JOptionPane.showMessageDialog(QuenMatKhau_UI.this, "Tài khoản không tồn tại!");
					else if (result.isEmpty()) JOptionPane.showMessageDialog(QuenMatKhau_UI.this, "Lỗi gửi Email!");
					else {
						maHeThong = result;
						JOptionPane.showMessageDialog(QuenMatKhau_UI.this, "Mã xác thực đã được gửi vào Email của bạn.");
					}
				} catch (Exception e) { e.printStackTrace(); }
				btnGuiMa.setEnabled(true);
				btnGuiMa.setText("Gửi mã qua Email");
			}
		};
		worker.execute();
	}

	private void doiMatKhau() {
		String maKhach = txtMaXacThuc.getText().trim();
		String mk = new String(txtMatKhauMoi.getPassword());
		String reMk = new String(txtNhapLai.getPassword());

		if (!maKhach.equals(maHeThong) || maHeThong.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Mã xác thực không đúng!");
			return;
		}
		if (mk.length() < 6 || !mk.equals(reMk)) {
			JOptionPane.showMessageDialog(this, "Mật khẩu không hợp lệ hoặc không trùng khớp!");
			return;
		}

		SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				return taiKhoanService.capNhatMatKhauMoi(txtTaiKhoan.getText().trim(), mk);
			}

			@Override
			protected void done() {
				try {
					if (get()) {
						JOptionPane.showMessageDialog(QuenMatKhau_UI.this, "Đổi mật khẩu thành công!");
						dispose();
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
		};
		worker.execute();
	}
}
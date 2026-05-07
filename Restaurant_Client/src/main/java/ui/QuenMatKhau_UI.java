package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

// ✅ Import Interface từ Shared thay vì DAO_Impl
import connect.ConfigManager;
import rmi_interfaces.ITaiKhoan_Service;

public class QuenMatKhau_UI extends JDialog {
	private final Color MAU_NEN_DARK = new Color(34, 40, 49);
	private final Color MAU_COMPONENT = new Color(57, 62, 70);
	private final Color MAU_CHU_TRANG = new Color(238, 238, 238);
	private final Color MAU_ACCENT = new Color(0, 173, 181);
	private final Color MAU_SUCCESS = new Color(76, 175, 80);
	private final Color MAU_ERROR = new Color(244, 67, 54);

	private JTextField txtTaiKhoan;
	private JTextField txtMaXacThuc;
	private JPasswordField txtMatKhauMoi;
	private JPasswordField txtNhacLaiMatKhau;
	private JButton btnGuiMa;
	private JButton btnXacNhan;
	private JButton btnHuy;

	// ✅ Đổi thành Interface
	private ITaiKhoan_Service taiKhoanDAO;
	private String maXacThucGui;
	private int lanThuTaiDo;

	// Phương thức main để test giao diện
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame parentFrame = new JFrame();
					parentFrame.setUndecorated(true);
					parentFrame.setLocationRelativeTo(null);
					parentFrame.setVisible(true);

					QuenMatKhau_UI dialog = new QuenMatKhau_UI(parentFrame);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					dialog.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosed(java.awt.event.WindowEvent windowEvent) {
							parentFrame.dispose();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor khởi tạo giao diện
	public QuenMatKhau_UI(JFrame parent) {
		super(parent, "Quên Mật Khẩu", true);

		// ✅ Khởi tạo qua RMI Lookup thay vì new Object
		try {
			String url = ConfigManager.getRmiUrl();
			taiKhoanDAO = (ITaiKhoan_Service) Naming.lookup(url +"TaiKhoan_Service");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
		}

		maXacThucGui = null;
		lanThuTaiDo = 0;

		setSize(450, 550);
		setLocationRelativeTo(parent);
		setResizable(false);

		JPanel contentPane = new JPanel(new BorderLayout(10, 20));
		contentPane.setBackground(MAU_NEN_DARK);
		contentPane.setBorder(new EmptyBorder(20, 25, 20, 25));
		setContentPane(contentPane);

		JLabel lblTieuDe = new JLabel("ĐẶT LẠI MẬT KHẨU");
		lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTieuDe.setForeground(MAU_ACCENT);
		lblTieuDe.setHorizontalAlignment(JLabel.CENTER);
		lblTieuDe.setBorder(new EmptyBorder(0, 0, 10, 0));
		contentPane.add(lblTieuDe, BorderLayout.NORTH);

		JPanel formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setOpaque(false);

		JPanel pnlTaiKhoan = new JPanel(new BorderLayout(10, 0));
		pnlTaiKhoan.setOpaque(false);
		txtTaiKhoan = taoTextField();

		btnGuiMa = taoNutChucNang("Gửi Mã", MAU_ACCENT, MAU_ACCENT.brighter());
		btnGuiMa.setFont(new Font("Segoe UI", Font.BOLD, 13));
		pnlTaiKhoan.add(txtTaiKhoan, BorderLayout.CENTER);
		pnlTaiKhoan.add(btnGuiMa, BorderLayout.EAST);

		formPanel.add(taoPanelInput("Tài khoản:", pnlTaiKhoan));
		formPanel.add(Box.createVerticalStrut(15));

		txtMaXacThuc = taoTextField();
		formPanel.add(taoPanelInput("Mã xác thực:", txtMaXacThuc));
		formPanel.add(Box.createVerticalStrut(15));

		txtMatKhauMoi = taoPasswordField();
		formPanel.add(taoPanelInput("Mật khẩu mới (tối thiểu 6 ký tự):", txtMatKhauMoi));
		formPanel.add(Box.createVerticalStrut(15));

		txtNhacLaiMatKhau = taoPasswordField();
		formPanel.add(taoPanelInput("Nhắc lại mật khẩu:", txtNhacLaiMatKhau));

		contentPane.add(formPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.setOpaque(false);

		btnXacNhan = taoNutChucNang("Xác Nhận", MAU_SUCCESS, MAU_SUCCESS.brighter());
		btnHuy = taoNutChucNang("Hủy", MAU_ERROR, MAU_ERROR.brighter());

		buttonPanel.add(btnXacNhan);
		buttonPanel.add(btnHuy);

		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		ganSuKien();

		datTrangThaiBanDau(true);
	}

	// Tạo panel chứa label và component nhập liệu
	private JPanel taoPanelInput(String tenLabel, Component inputComponent) {
		JPanel panel = new JPanel(new BorderLayout(0, 5));
		panel.setOpaque(false);

		JLabel label = new JLabel(tenLabel);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		label.setForeground(MAU_CHU_TRANG);

		panel.add(label, BorderLayout.NORTH);
		panel.add(inputComponent, BorderLayout.CENTER);
		return panel;
	}

	// Tạo TextField với định dạng chung
	private JTextField taoTextField() {
		JTextField textField = new JTextField();
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		textField.setBackground(MAU_COMPONENT);
		textField.setForeground(MAU_CHU_TRANG);
		textField.setCaretColor(MAU_CHU_TRANG);
		textField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(MAU_COMPONENT.darker(), 1),
				new EmptyBorder(8, 10, 8, 10)
		));
		textField.setPreferredSize(new Dimension(200, 40));
		return textField;
	}

	// Tạo PasswordField với định dạng chung
	private JPasswordField taoPasswordField() {
		JPasswordField passField = new JPasswordField();
		passField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		passField.setBackground(MAU_COMPONENT);
		passField.setForeground(MAU_CHU_TRANG);
		passField.setCaretColor(MAU_CHU_TRANG);
		passField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(MAU_COMPONENT.darker(), 1),
				new EmptyBorder(8, 10, 8, 10)
		));
		passField.setPreferredSize(new Dimension(200, 40));
		return passField;
	}

	// Tạo nút chức năng với màu sắc tùy chỉnh
	private JButton taoNutChucNang(String text, Color background, Color hover) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setBackground(background);
		button.setForeground(Color.WHITE);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setBorder(new EmptyBorder(10, 20, 10, 20));
		button.setFocusPainted(false);

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(hover);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(background);
			}
		});
		return button;
	}

	// Thiết lập trạng thái enable/disable của các component
	private void datTrangThaiBanDau(boolean trangThai) {
		txtTaiKhoan.setEnabled(trangThai);
		btnGuiMa.setEnabled(trangThai);

		txtMaXacThuc.setEnabled(!trangThai);
		txtMatKhauMoi.setEnabled(!trangThai);
		txtNhacLaiMatKhau.setEnabled(!trangThai);
		btnXacNhan.setEnabled(!trangThai);
	}

	// Gắn sự kiện cho các nút bấm
	private void ganSuKien() {
		btnGuiMa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guiMaXacThuc();
			}
		});

		btnXacNhan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xacNhanDatLaiMatKhau();
			}
		});

		btnHuy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		txtTaiKhoan.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnGuiMa.doClick();
			}
		});
	}

	// Gửi mã xác thực OTP qua email
	private void guiMaXacThuc() {
		if (taiKhoanDAO == null) {
			JOptionPane.showMessageDialog(this, "Lỗi: Chưa kết nối được với Máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String tenDangNhap = txtTaiKhoan.getText().trim();

		if (tenDangNhap.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản!", "Lỗi",
					JOptionPane.WARNING_MESSAGE);
			txtTaiKhoan.requestFocusInWindow();
			return;
		}

		// ✅ Bao bọc các thao tác RMI bằng try-catch
		try {
			if (!taiKhoanDAO.kiemTraTenDangNhapTonTai(tenDangNhap)) {
				JOptionPane.showMessageDialog(this, "Tài khoản không tồn tại!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				txtTaiKhoan.requestFocusInWindow();
				return;
			}

			String email = taiKhoanDAO.layEmailTheoTenDangNhap(tenDangNhap);

			if (email == null || email.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy email cho tài khoản này!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			maXacThucGui = taiKhoanDAO.taoDuMaXacThuc();

			if (taiKhoanDAO.guiEmailXacThuc(email, maXacThucGui, tenDangNhap)) {
				JOptionPane.showMessageDialog(this,
						"Mã xác thực đã được gửi đến email: " + email + "\n\nMã sẽ hết hiệu lực sau 10 phút.",
						"Thành công", JOptionPane.INFORMATION_MESSAGE);
				datTrangThaiBanDau(false);

			} else {
				JOptionPane.showMessageDialog(this, "Lỗi gửi email! Vui lòng thử lại sau.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (java.rmi.RemoteException re) {
			re.printStackTrace();
			JOptionPane.showMessageDialog(this, "Mất kết nối với máy chủ: " + re.getMessage(), "Lỗi Mạng", JOptionPane.ERROR_MESSAGE);
		}
	}

	// Xác nhận mã OTP và cập nhật mật khẩu mới
	private void xacNhanDatLaiMatKhau() {
		if (taiKhoanDAO == null) {
			JOptionPane.showMessageDialog(this, "Lỗi: Chưa kết nối được với Máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String tenDangNhap = txtTaiKhoan.getText().trim();
		String maXacThucNhap = txtMaXacThuc.getText().trim();
		String matKhauMoi = new String(txtMatKhauMoi.getPassword()).trim();
		String nhacLaiMatKhau = new String(txtNhacLaiMatKhau.getPassword()).trim();

		if (maXacThucNhap.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập mã xác thực!", "Lỗi",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (!maXacThucNhap.equals(maXacThucGui)) {
			lanThuTaiDo++;
			JOptionPane.showMessageDialog(this, "Mã xác thực không đúng! (Lần thử: " + lanThuTaiDo + "/3)",
					"Lỗi", JOptionPane.ERROR_MESSAGE);

			if (lanThuTaiDo >= 3) {
				JOptionPane.showMessageDialog(this,
						"Bạn đã nhập sai mã 3 lần! Vui lòng thử lại sau.", "Cảnh báo",
						JOptionPane.WARNING_MESSAGE);
				dispose();
			}
			return;
		}

		if (matKhauMoi.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu mới!", "Lỗi",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (nhacLaiMatKhau.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhắc lại mật khẩu!", "Lỗi",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (!matKhauMoi.equals(nhacLaiMatKhau)) {
			JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không trùng khớp!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (matKhauMoi.length() < 6) {
			JOptionPane.showMessageDialog(this, "Mật khẩu phải ít nhất 6 ký tự!", "Lỗi",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// ✅ Bao bọc RMI bằng try-catch
		try {
			if (taiKhoanDAO.capNhatMatKhauMoi(tenDangNhap, matKhauMoi)) {
				JOptionPane.showMessageDialog(this,
						"Đặt lại mật khẩu thành công!\nVui lòng đăng nhập lại bằng mật khẩu mới.",
						"Thành công", JOptionPane.INFORMATION_MESSAGE);
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "Lỗi cập nhật mật khẩu! Vui lòng thử lại.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (java.rmi.RemoteException re) {
			re.printStackTrace();
			JOptionPane.showMessageDialog(this, "Mất kết nối với máy chủ: " + re.getMessage(), "Lỗi Mạng", JOptionPane.ERROR_MESSAGE);
		}
	}
}
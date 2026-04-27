package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import rmi_interfaces.INhanVien_DAO;
import entity.NhanVien;

public class DangNhap_UI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private INhanVien_DAO nvDAO;
	private JButton btnDangNhap;
	private JTextField txtTaiKhoan;
	private JPasswordField txtMatKhau;

	// Phương thức main khởi chạy ứng dụng
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DangNhap_UI LoginFrame = new DangNhap_UI();
					LoginFrame.setVisible(true);
					LoginFrame.setLocationRelativeTo(null);
					LoginFrame.setResizable(false);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor khởi tạo giao diện và các thành phần
	public DangNhap_UI() {
		try {
			// Lưu ý: Chuỗi "rmi://localhost:1099/..." phải khớp 100% với tên bạn đã đăng ký (bind) bên phía Server.
			nvDAO = (INhanVien_DAO) java.rmi.Naming.lookup("rmi://localhost:1099/NhanVien_DAO");
		} catch (Exception e) {
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Hệ thống Quản lý Đặt Bàn Nhà hàng T3L");
		setBounds(100, 100, 813, 631);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel pNen = new JPanel();
		pNen.setBackground(new Color(30, 30, 50)); 
		pNen.setBounds(0, 0, 800, 600);
		contentPane.add(pNen);
		pNen.setLayout(null);

		// Panel bên trái chứa form đăng nhập
		JPanel jPnlLeft = new JPanel();
		jPnlLeft.setBackground(new Color(26, 0, 0));
		jPnlLeft.setBounds(0, 0, 400, 600);
		pNen.add(jPnlLeft);
		jPnlLeft.setLayout(null);

		JLabel lblDangNhap = new JLabel("ĐĂNG NHẬP");
		lblDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 40));
		lblDangNhap.setForeground(new Color(241, 121, 104));
		lblDangNhap.setBounds(61, 111, 245, 60);
		jPnlLeft.add(lblDangNhap);

		txtTaiKhoan = new JTextField("Tài khoản");
		txtTaiKhoan.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtTaiKhoan.setBounds(56, 213, 205, 40);
		txtTaiKhoan.setForeground(Color.GRAY);
		txtTaiKhoan.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtTaiKhoan.getText().equals("Tài khoản")) {
					txtTaiKhoan.setText("");
					txtTaiKhoan.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (txtTaiKhoan.getText().isEmpty()) {
					txtTaiKhoan.setText("Tài khoản");
					txtTaiKhoan.setForeground(Color.GRAY);
				}
			}
		});
		jPnlLeft.add(txtTaiKhoan);

		txtMatKhau = new JPasswordField("Mật khẩu");
		txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtMatKhau.setBounds(56, 273, 205, 40);
		txtMatKhau.setForeground(Color.GRAY);
		txtMatKhau.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnDangNhap.doClick();
			}
		});
		txtMatKhau.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (String.valueOf(txtMatKhau.getPassword()).equals("Mật khẩu")) {
					txtMatKhau.setText("");
					txtMatKhau.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (String.valueOf(txtMatKhau.getPassword()).isEmpty()) {
					txtMatKhau.setText("Mật khẩu");
					txtMatKhau.setForeground(Color.GRAY);
				}
			}
		});
		jPnlLeft.add(txtMatKhau);

		JLabel lblIconMatKhau = new JLabel(
				new ImageIcon(DangNhap_UI.class.getResource("/IMG/lockedPassword_white.png")));
		lblIconMatKhau.setBounds(271, 273, 40, 40);
		jPnlLeft.add(lblIconMatKhau);

		JLabel lblQuenMatKhau = new JLabel("Quên mật khẩu?");
		lblQuenMatKhau.setForeground(Color.WHITE);
		lblQuenMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblQuenMatKhau.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblQuenMatKhau.setBounds(161, 320, 100, 25);
		jPnlLeft.add(lblQuenMatKhau);

		lblQuenMatKhau.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				xuLyQuenMatKhau();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lblQuenMatKhau.setForeground(new Color(241, 121, 104));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblQuenMatKhau.setForeground(Color.WHITE);
			}
		});

		btnDangNhap = new JButton("Đăng nhập") {
			private static final int ARC = 35;
			private Color mauGoc;
			private Color mauKhiNhan;

			@Override
			public void setBackground(Color bg) {
				super.setBackground(bg);
				this.mauGoc = bg;
				this.mauKhiNhan = new Color(Math.max(0, bg.getRed() - 35), Math.max(0, bg.getGreen() - 35),
						Math.max(0, bg.getBlue() - 35));
			}

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (getModel().isPressed()) {
					g2d.setColor(mauKhiNhan);
				} else {
					g2d.setColor(getBackground());
				}

				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
				g2d.setColor(getForeground());
				super.paintComponent(g2d);
				g2d.dispose();
			}

			@Override
			public void updateUI() {
				super.updateUI();
				setOpaque(false);
				setContentAreaFilled(false); 
				setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
			}
		};
		btnDangNhap.setBounds(56, 361, 255, 45);
		btnDangNhap.setBackground(new Color(241, 121, 104));
		btnDangNhap.setForeground(Color.WHITE);
		btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 20));
		btnDangNhap.setFocusPainted(false);
		btnDangNhap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		jPnlLeft.add(btnDangNhap);

		btnDangNhap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thucHienDangNhap();
			}
		});

		// Panel bên phải chứa Logo và Slogan
		JPanel jPnlRight = new JPanel();
		jPnlRight.setBackground(new Color(241, 121, 104));
		jPnlRight.setBounds(400, 0, 400, 600);
		pNen.add(jPnlRight);
		jPnlRight.setLayout(null);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(DangNhap_UI.class.getResource("/IMG/t3LLogo_300px.png")));
		lblNewLabel.setBounds(50, 70, 308, 290);
		jPnlRight.add(lblNewLabel);

		JLabel lblSlogan1 = new JLabel("NHÀ HÀNG T3L");
		lblSlogan1.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblSlogan1.setForeground(new Color(255, 255, 255));
		lblSlogan1.setBounds(129, 370, 152, 39);
		jPnlRight.add(lblSlogan1);

		JLabel lblNewLabel_2 = new JLabel("ĂN NGON BẤT NGỜ !");
		lblNewLabel_2.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblNewLabel_2.setForeground(new Color(255, 255, 255));
		lblNewLabel_2.setBounds(102, 404, 211, 39);
		jPnlRight.add(lblNewLabel_2);

		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setBounds(271, 208, 47, 45);
		jPnlLeft.add(lblNewLabel_1);
		lblNewLabel_1.setIcon(new ImageIcon(DangNhap_UI.class.getResource("/IMG/user_white.png")));
	}

	// Mở giao diện quên mật khẩu
	private void xuLyQuenMatKhau() {
		 QuenMatKhau_UI quenMatKhauFrame = new QuenMatKhau_UI(this);
		 quenMatKhauFrame.setVisible(true);
		 quenMatKhauFrame.setLocationRelativeTo(this);
	}

	// Thực hiện kiểm tra thông tin và xử lý đăng nhập
	private void thucHienDangNhap() {
		String tenDangNhap = txtTaiKhoan.getText().trim();
		String matKhau = new String(txtMatKhau.getPassword()).trim();

		if (tenDangNhap.equals("Tài khoản") || tenDangNhap.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập Tài khoản!", "Lỗi", JOptionPane.WARNING_MESSAGE);
			txtTaiKhoan.requestFocusInWindow();
			return;
		}
		if (matKhau.equals("Mật khẩu") || matKhau.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập Mật khẩu!", "Lỗi", JOptionPane.WARNING_MESSAGE);
			txtMatKhau.requestFocusInWindow();
			return;
		}

		try {
			// Gọi hàm qua RMI
			NhanVien loggedInUser = nvDAO.xacThucDangNhap(tenDangNhap, matKhau);

			if (loggedInUser != null) {
				Auth.login(loggedInUser);
				try {
					TrangChu_UI home = new TrangChu_UI();
					home.setLocationRelativeTo(null);
					home.setVisible(true);
					this.dispose();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Lỗi khởi tạo Trang Chủ: " + ex.getMessage(), "Lỗi Hệ Thống",
							JOptionPane.ERROR_MESSAGE);
				}

			} else {
				JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc Mật khẩu không đúng!", "Đăng nhập thất bại",
						JOptionPane.ERROR_MESSAGE);

				txtMatKhau.setText("");
				txtTaiKhoan.requestFocusInWindow();
			}

		} catch (java.rmi.RemoteException e) {
			e.printStackTrace();
			javax.swing.JOptionPane.showMessageDialog(this,
					"Không thể kết nối đến Máy chủ. Vui lòng kiểm tra lại!",
					"Lỗi Kết Nối",
					javax.swing.JOptionPane.ERROR_MESSAGE);
		}

	}

}
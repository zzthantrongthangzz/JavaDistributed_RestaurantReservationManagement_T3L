package ui.monan;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;

import connect.ConfigManager;
import rmi_interfaces.IMonAn_Service;
import rmi_interfaces.ILoaiMon_Service;
import entity.MonAn;
import entity.LoaiMon;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.Naming;
import java.util.List;

public class ThemMonAn_UI extends JPanel {

	private JTextField txtMaMon;
	private JTextField txtTenMon;
	private JTextField txtGia;
	private JTextField txtDonVi;
	private JComboBox<String> cmbTinhTrang;
	private JComboBox<LoaiMon> cmbLoai;
	private JTextArea txtMoTa;
	private File selectedFile;
	private ImagePreviewPanel pnlImagePreview;
	private Image backgroundImage;
	private JButton btnThemNhieu;

	private IMonAn_Service monAnDAO;
	private ILoaiMon_Service loaiMonDAO;

	private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
	private final Color bgColor = new Color(48, 52, 56);
	private final Color componentColor = new Color(124, 124, 124);
	private final Color textColor = Color.WHITE;

	private final Color MAU_NUT_CAP_NHAT = new Color(255, 193, 7);
	private final Color MAU_NUT_XOA = new Color(244, 67, 54);
	private final Color MAU_NUT_THEM = new Color(76, 175, 80);
	private final Color MAU_NUT_LAM_MOI = new Color(30, 144, 255);
	private final Color MAU_NUT_THEM_NHIEU = new Color(156, 39, 176);

	public ThemMonAn_UI() {
		try {
			String url = ConfigManager.getRmiUrl();
			monAnDAO = (IMonAn_Service) Naming.lookup(url +"MonAn_Service");
			loaiMonDAO = (ILoaiMon_Service) Naming.lookup(url +"LoaiMon_Service");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
		}

		setBackground(bgColor);
		setLayout(null);

		try {
			backgroundImage = new ImageIcon(getClass().getResource("/img/vipbackground2.png")).getImage();
		} catch (Exception e) {
			backgroundImage = null;
		}

		JLabel lblTitle = new JLabel("THÊM MÓN");
		lblTitle.setForeground(textColor);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 40));
		lblTitle.setBounds(747, 34, 227, 62);
		add(lblTitle);

		pnlImagePreview = new ImagePreviewPanel();
		pnlImagePreview.setBounds(74, 303, 346, 343);
		add(pnlImagePreview);

		JButton btnChonAnh = createStyledButton("Chọn ảnh", "/IMG/folder_32px.png");
		btnChonAnh.setBounds(173, 686, 150, 40);
		add(btnChonAnh);
		btnChonAnh.addActionListener(e -> chonAnh());

		JLabel lblMaMon = createStyledLabel("Mã món:");
		lblMaMon.setBounds(508, 290, 160, 34);
		add(lblMaMon);
		txtMaMon = createStyledTextField();
		txtMaMon.setBounds(636, 287, 365, 45);
		txtMaMon.setEditable(false);
		txtMaMon.setBackground(new Color(80, 80, 80));
		loadMaMonTuDong();
		add(txtMaMon);

		JLabel lblTenMon = createStyledLabel("Tên món:");
		lblTenMon.setBounds(508, 383, 160, 34);
		add(lblTenMon);
		txtTenMon = createStyledTextField();
		txtTenMon.setBounds(636, 381, 365, 45);
		add(txtTenMon);

		JLabel lblDonVi = createStyledLabel("Đơn vị:");
		lblDonVi.setBounds(507, 480, 160, 34);
		add(lblDonVi);
		txtDonVi = createStyledTextField();
		txtDonVi.setBounds(636, 475, 365, 45);
		add(txtDonVi);

		JLabel lblGia = createStyledLabel("Giá:");
		lblGia.setBounds(507, 574, 160, 34);
		add(lblGia);
		txtGia = createStyledTextField();
		txtGia.setBounds(636, 569, 365, 45);
		add(txtGia);

		JLabel lblTinhTrang = createStyledLabel("Tình trạng:");
		lblTinhTrang.setBounds(1069, 290, 160, 34);
		add(lblTinhTrang);
		cmbTinhTrang = createStyledComboBox(new String[] { "Đang kinh doanh", "Ngừng kinh doanh" });
		cmbTinhTrang.setBounds(1228, 287, 365, 45);
		add(cmbTinhTrang);

		JLabel lblLoai = createStyledLabel("Loại:");
		lblLoai.setBounds(1069, 383, 160, 34);
		add(lblLoai);

		cmbLoai = new JComboBox<LoaiMon>();
		setupComboBoxUI(cmbLoai);
		cmbLoai.setBounds(1228, 381, 315, 45);
		add(cmbLoai);

		JButton btnThemLoai = createIconButton("/IMG/add_32px.png", componentColor, 24);
		btnThemLoai.setBounds(1228 + 315 + 5, 381, 45, 45);
		add(btnThemLoai);
		btnThemLoai.addActionListener(e -> hienThiDialogThemLoai());

		JLabel lblMoTa = createStyledLabel("Mô tả:");
		lblMoTa.setBounds(1069, 480, 160, 34);
		add(lblMoTa);

		txtMoTa = new JTextArea();
		txtMoTa.setBackground(componentColor);
		txtMoTa.setForeground(textColor);
		txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		txtMoTa.setBorder(new EmptyBorder(10, 10, 10, 10));
		txtMoTa.setLineWrap(true);
		txtMoTa.setWrapStyleWord(true);
		JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
		scrollMoTa.setBorder(null);
		scrollMoTa.setBounds(1228, 475, 365, 139);
		add(scrollMoTa);

		JButton btnThem = taoNutChucNang("Thêm", MAU_NUT_THEM);
		btnThem.setBounds(845, 685, 150, 40);
		btnThem.addActionListener(e -> xuLyThemMonAn());
		add(btnThem);

		JButton btnLamMoi = taoNutChucNang("Làm mới", MAU_NUT_LAM_MOI);
		btnLamMoi.setBounds(1076, 686, 150, 40);
		add(btnLamMoi);
		btnLamMoi.addActionListener(e -> lamMoi());

		btnThemNhieu = taoNutChucNang("Thêm nhiều", MAU_NUT_THEM_NHIEU);
		btnThemNhieu.setBounds(1307, 685, 150, 40);
		btnThemNhieu.addActionListener(e -> hienThiGiaoDienThemNhieu());
		add(btnThemNhieu);

		taiDuLieuLoaiMon();
	}

	private void loadMaMonTuDong() {
		if (monAnDAO == null) return;
		SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
			@Override protected String doInBackground() throws Exception {
				return monAnDAO.sinhMaMonTuDong();
			}
			@Override protected void done() {
				try { txtMaMon.setText(get()); } catch (Exception e) {}
			}
		};
		worker.execute();
	}

	private JButton taoNutChucNang(String text, Color mauNen) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.BOLD, 16));
		button.setForeground(Color.WHITE);
		button.setBackground(mauNen);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
		button.setBorder(new EmptyBorder(10, 20, 10, 20));

		button.addMouseListener(new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent e) { button.setBackground(mauNen.brighter()); }
			@Override public void mouseExited(MouseEvent e) { button.setBackground(mauNen); }
		});
		return button;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		} else {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	private class ImagePreviewPanel extends JPanel {
		private Image image;
		public ImagePreviewPanel() { setBackground(bgColor); setOpaque(false); }
		public void setImage(Image image) { this.image = image; repaint(); }
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int diameter = Math.min(getWidth(), getHeight());
			int x = (getWidth() - diameter) / 2;
			int y = (getHeight() - diameter) / 2;
			Ellipse2D.Double circle = new Ellipse2D.Double(x, y, diameter, diameter);
			if (image != null) {
				g2d.setClip(circle);
				g2d.drawImage(image, x, y, diameter, diameter, this);
			} else {
				g2d.setColor(componentColor);
				g2d.fill(circle);
			}
			g2d.dispose();
		}
	}

	private JLabel createStyledLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(textColor);
		label.setFont(new Font("Segoe UI", Font.BOLD, 25));
		return label;
	}

	private JTextField createStyledTextField() {
		JTextField textField = new JTextField();
		textField.setBackground(componentColor);
		textField.setForeground(textColor);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		textField.setBorder(new EmptyBorder(5, 10, 5, 10));
		textField.setCaretColor(Color.WHITE);
		return textField;
	}

	private <T> void setupComboBoxUI(JComboBox<T> comboBox) {
		comboBox.setBackground(componentColor);
		comboBox.setForeground(textColor);
		comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
			@Override protected JButton createArrowButton() {
				JButton button = super.createArrowButton();
				button.setBackground(componentColor);
				button.setBorder(BorderFactory.createEmptyBorder());
				return button;
			}
		});
		comboBox.setFocusable(false);
		Border border = BorderFactory.createLineBorder(componentColor, 2);
		Border padding = new EmptyBorder(5, 10, 5, 10);
		comboBox.setBorder(BorderFactory.createCompoundBorder(border, padding));
	}

	private JComboBox<String> createStyledComboBox(String[] items) {
		JComboBox<String> comboBox = new JComboBox<>(items);
		setupComboBoxUI(comboBox);
		return comboBox;
	}

	private JButton createStyledButton(String text, String iconPath) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.BOLD, 16));
		button.setBackground(componentColor);
		button.setForeground(textColor);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBorder(new EmptyBorder(10, 20, 10, 20));
		button.setFocusPainted(false);
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
			Image scaledIcon = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
			button.setIcon(new ImageIcon(scaledIcon));
		} catch (Exception e) {}
		return button;
	}

	private JButton createIconButton(String iconPath, Color backgroundColor, int iconSize) {
		JButton button = new JButton();
		button.setBackground(backgroundColor);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBorder(new EmptyBorder(5, 5, 5, 5));
		button.setFocusPainted(false);
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
			Image scaledIcon = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
			button.setIcon(new ImageIcon(scaledIcon));
		} catch (Exception e) { button.setText("+"); }
		button.addMouseListener(new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent e) { button.setBackground(backgroundColor.brighter()); }
			@Override public void mouseExited(MouseEvent e) { button.setBackground(backgroundColor); }
		});
		return button;
	}

	private void taiDuLieuLoaiMon() {
		if (loaiMonDAO == null) return;
		SwingWorker<List<LoaiMon>, Void> worker = new SwingWorker<List<LoaiMon>, Void>() {
			@Override protected List<LoaiMon> doInBackground() throws Exception {
				return loaiMonDAO.docDanhSachLoaiMon();
			}
			@Override protected void done() {
				try {
					List<LoaiMon> ds = get();
					cmbLoai.removeAllItems();
					for (LoaiMon lm : ds) cmbLoai.addItem(lm);
				} catch (Exception e) { e.printStackTrace(); }
			}
		};
		worker.execute();
	}

	private void hienThiDialogThemLoai() {
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Thêm loại món", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setSize(400, 350);
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBackground(bgColor);
		mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

		JPanel pnlHienCo = new JPanel(new BorderLayout(0, 5));
		pnlHienCo.setOpaque(false);
		JLabel lblHienCo = createStyledLabel("Loại hiện có:");
		lblHienCo.setFont(new Font("Segoe UI", Font.BOLD, 18));

		DefaultListModel<LoaiMon> listModel = new DefaultListModel<>();
		for (int i = 0; i < cmbLoai.getItemCount(); i++) listModel.addElement(cmbLoai.getItemAt(i));

		JList<LoaiMon> listHienCo = new JList<>(listModel);
		listHienCo.setBackground(componentColor);
		listHienCo.setForeground(textColor);
		listHienCo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		listHienCo.setSelectionBackground(MAU_NUT_CAP_NHAT.darker());
		listHienCo.setSelectionForeground(Color.WHITE);
		listHienCo.setBorder(new EmptyBorder(5, 10, 5, 10));

		JScrollPane scrollPane = new JScrollPane(listHienCo);
		scrollPane.setBorder(BorderFactory.createLineBorder(componentColor, 1));

		pnlHienCo.add(lblHienCo, BorderLayout.NORTH);
		pnlHienCo.add(scrollPane, BorderLayout.CENTER);
		pnlHienCo.setPreferredSize(new Dimension(0, 150));

		JPanel pnlThemMoi = new JPanel(new BorderLayout(0, 5));
		pnlThemMoi.setOpaque(false);
		JLabel lblThemMoi = createStyledLabel("Tên loại mới:");
		lblThemMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		JTextField txtTenLoaiMoi = createStyledTextField();

		pnlThemMoi.add(lblThemMoi, BorderLayout.NORTH);
		pnlThemMoi.add(txtTenLoaiMoi, BorderLayout.CENTER);

		JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		pnlButton.setOpaque(false);

		JButton btnThem = new JButton("Thêm");
		btnThem.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnThem.setBackground(MAU_NUT_THEM);
		btnThem.setForeground(textColor);
		btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnThem.setBorder(new EmptyBorder(8, 25, 8, 25));

		JButton btnHuy = new JButton("Hủy");
		btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnHuy.setBackground(MAU_NUT_XOA);
		btnHuy.setForeground(textColor);
		btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnHuy.setBorder(new EmptyBorder(8, 25, 8, 25));

		pnlButton.add(btnThem);
		pnlButton.add(Box.createRigidArea(new Dimension(10, 0)));
		pnlButton.add(btnHuy);

		mainPanel.add(pnlHienCo, BorderLayout.NORTH);
		mainPanel.add(pnlThemMoi, BorderLayout.CENTER);
		mainPanel.add(pnlButton, BorderLayout.SOUTH);

		btnHuy.addActionListener(e -> dialog.dispose());

		btnThem.addActionListener(e -> {
			String tenLoaiMoi = txtTenLoaiMoi.getText().trim();
			if (tenLoaiMoi.isEmpty()) {
				JOptionPane.showMessageDialog(dialog, "Tên loại mới không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
			btnThem.setEnabled(false);
			SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
				@Override protected Object[] doInBackground() throws Exception {
					LoaiMon loaiTonTai = loaiMonDAO.timLoaiTheoTen(tenLoaiMoi);
					if (loaiTonTai != null) return new Object[]{false, "exists", null};
					String maLoaiMoi = loaiMonDAO.sinhMaLoaiTuDong();
					LoaiMon loaiMoi = new LoaiMon(maLoaiMoi, tenLoaiMoi);
					boolean ok = loaiMonDAO.themLoaiMon(loaiMoi);
					return new Object[]{ok, "success", loaiMoi};
				}
				@Override protected void done() {
					btnThem.setEnabled(true);
					try {
						Object[] res = get();
						boolean ok = (boolean) res[0];
						String status = (String) res[1];
						LoaiMon loaiMoi = (LoaiMon) res[2];
						if (status.equals("exists")) {
							JOptionPane.showMessageDialog(dialog, "Loại món này đã tồn tại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
						} else if (ok) {
							cmbLoai.addItem(loaiMoi);
							cmbLoai.setSelectedItem(loaiMoi);
							listModel.addElement(loaiMoi);
							listHienCo.ensureIndexIsVisible(listModel.getSize() - 1);
							JOptionPane.showMessageDialog(dialog, "Đã thêm loại món mới!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
							txtTenLoaiMoi.setText("");
							txtTenLoaiMoi.requestFocus();
						} else {
							JOptionPane.showMessageDialog(dialog, "Thêm loại món mới thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception ex) {}
				}
			};
			worker.execute();
		});

		dialog.add(mainPanel);
		dialog.setVisible(true);
	}

	private void chonAnh() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Chọn ảnh món ăn");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "png", "gif", "jpeg"));
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			try {
				ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
				pnlImagePreview.setImage(imageIcon.getImage());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Không thể tải ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void lamMoi() {
		loadMaMonTuDong();
		txtTenMon.setText("");
		txtGia.setText("");
		txtDonVi.setText("");
		txtMoTa.setText("");
		if (cmbLoai.getItemCount() > 0) cmbLoai.setSelectedIndex(0);
		cmbTinhTrang.setSelectedIndex(0);
		pnlImagePreview.setImage(null);
		selectedFile = null;
		txtTenMon.requestFocus();
	}

	private void xuLyThemMonAn() {
		if (!kiemTraDuLieu()) return;

		String maMon = txtMaMon.getText().trim();
		String tenMon = txtTenMon.getText().trim();
		double gia = Double.parseDouble(txtGia.getText().trim());
		String donVi = txtDonVi.getText().trim();
		String tinhTrang = (String) cmbTinhTrang.getSelectedItem();
		String moTa = txtMoTa.getText().trim();
		LoaiMon loaiMon = (LoaiMon) cmbLoai.getSelectedItem();

		if (loaiMon == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn loại món!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String duongDanAnh = "/img/default_food.png";
		if (selectedFile != null) {
			String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
			String tenMonKhongDau = chuyenTenMonThanhTenFile(tenMon);
			String tenFileAnh = "mon_" + tenMonKhongDau + extension;
			duongDanAnh = "/img/" + tenFileAnh;
			try {
				java.net.URL resourceUrl = getClass().getResource("/img");
				if (resourceUrl == null) {
					File outputDir = new File("bin/img");
					if (!outputDir.exists()) outputDir.mkdirs();
					resourceUrl = outputDir.toURI().toURL();
				}
				File destFile = new File(new java.net.URI(resourceUrl.toString() + "/" + tenFileAnh));
				Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi khi lưu file ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		MonAn monAnMoi = new MonAn(maMon, tenMon, duongDanAnh, gia, tinhTrang, moTa, donVi, loaiMon);
		JButton btnThem = (JButton) SwingUtilities.getRootPane(this).getDefaultButton(); // Optional UI disable

		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override protected Boolean doInBackground() throws Exception {
				return monAnDAO.themMonAn(monAnMoi);
			}
			@Override protected void done() {
				try {
					if (get()) {
						JOptionPane.showMessageDialog(ThemMonAn_UI.this, "Thêm món ăn thành công!\nMã món: " + maMon, "Thành công", JOptionPane.INFORMATION_MESSAGE);
						lamMoi();
					} else {
						JOptionPane.showMessageDialog(ThemMonAn_UI.this, "Thêm món ăn thất bại!\nVui lòng kiểm tra lại thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception e) {}
			}
		};
		worker.execute();
	}

	private String chuyenTenMonThanhTenFile(String tenMon) {
		String result = tenMon.toLowerCase();
		result = result.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
		result = result.replaceAll("[èéẹẻẽêềếệểễ]", "e");
		result = result.replaceAll("[ìíịỉĩ]", "i");
		result = result.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
		result = result.replaceAll("[ùúụủũưừứựửữ]", "u");
		result = result.replaceAll("[ỳýỵỷỹ]", "y");
		result = result.replaceAll("đ", "d");
		result = result.replaceAll("[^a-z0-9]", "");
		return result;
	}

	private boolean kiemTraDuLieu() {
		String tenMon = txtTenMon.getText().trim();
		String giaStr = txtGia.getText().trim();
		String donVi = txtDonVi.getText().trim();

		if (tenMon.isEmpty()) { showValidationError("Vui lòng nhập tên món!", txtTenMon); return false; }
		if (!tenMon.matches("^[A-ZÀ-Ỹ][a-zA-Zà-ỹÀ-Ỹ\\s]*$")) {
			showValidationError("Tên món phải bắt đầu bằng chữ hoa và chỉ chứa chữ cái, khoảng trắng.\nVí dụ: Gà rán, Phở Bò Tái, Lẩu nấm", txtTenMon);
			return false;
		}

		if (giaStr.isEmpty()) { showValidationError("Vui lòng nhập giá món!", txtGia); return false; }
		try {
			double gia = Double.parseDouble(giaStr);
			if (gia <= 0) { showValidationError("Giá phải là một số dương (lớn hơn 0)!", txtGia); return false; }
		} catch (NumberFormatException e) {
			showValidationError("Giá phải là số hợp lệ! (Ví dụ: 50000 hoặc 50000.5)", txtGia); return false;
		}

		if (donVi.isEmpty()) { showValidationError("Vui lòng nhập đơn vị!", txtDonVi); return false; }
		if (!donVi.matches("^[A-ZÀ-Ỹ][a-zA-Zà-ỹÀ-Ỹ\\s]*$")) {
			showValidationError("Đơn vị phải bắt đầu bằng chữ hoa và chỉ chứa chữ cái, khoảng trắng.\nVí dụ: Dĩa, Ly, Phần, Tô", txtDonVi);
			return false;
		}
		return true;
	}

	private void showValidationError(String message, JComponent component) {
		JOptionPane.showMessageDialog(this, message, "Lỗi Nhập Liệu", JOptionPane.WARNING_MESSAGE);
		component.requestFocus();
	}

	private void hienThiGiaoDienThemNhieu() {
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Import Món Ăn Từ Excel", Dialog.ModalityType.APPLICATION_MODAL);
		ThemNhieuMonAn_UI panelThemNhieu = new ThemNhieuMonAn_UI();
		dialog.add(panelThemNhieu);
		dialog.setSize(1600, 750);
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(true);
		dialog.setVisible(true);
	}
}
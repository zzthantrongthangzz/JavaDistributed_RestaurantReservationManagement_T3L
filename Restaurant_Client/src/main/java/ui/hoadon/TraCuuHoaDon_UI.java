package ui.hoadon;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import connect.ConfigManager;
import rmi_interfaces.IHoaDon_Service;
import rmi_interfaces.IHoaDon_Ban_Service;
import rmi_interfaces.IKhachHang_Service;
import rmi_interfaces.IBanAn_Service;
import rmi_interfaces.IChiTietHoaDon_Service;
import rmi_interfaces.IMonAn_Service;
import rmi_interfaces.INhanVien_Service;

import entity.HoaDon;
import entity.BanAn;
import entity.ChiTietHoaDon;
import entity.KhachHang;
import entity.MonAn;
import entity.NhanVien;
import ui.HoaDonPDF;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TraCuuHoaDon_UI extends JPanel {

	private final Color MAU_NEN_TAB = new Color(48, 52, 56);
	private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
	private final Color MAU_CHU_CHUNG = Color.WHITE;
	private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
	private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
	private final Color MAU_PLACEHOLDER = new Color(150, 150, 160);
	private final Color MAU_LUOI_BANG = new Color(60, 62, 77);
	private final Color MAU_CHON_HANG = new Color(70, 72, 90);
	private final Color MAU_NEN_INPUT = new Color(60, 64, 68);

	private final Font FONT_NHAN = new Font("Segoe UI", Font.BOLD, 14);
	private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
	private final Font FONT_BANG = new Font("Segoe UI", Font.PLAIN, 14);
	private final Font FONT_HEADER_BANG = new Font("Segoe UI", Font.BOLD, 13);
	private final int KICH_THUOC_ICON = 24;

	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField txtTimKiemMaHD, txtTimKiemMaKH, txtTimKiemMaNV;
	private JDateChooser dateTuNgay, dateDenNgay;
	private JComboBox<String> cmbTrangThai;
	private JComboBox<String> cmbSapXep;

	private IHoaDon_Service hoaDonService;
	private IHoaDon_Ban_Service hoaDonBanService;
	private IKhachHang_Service khachHangService;
	private IBanAn_Service banAnService;
	private IChiTietHoaDon_Service chiTietHoaDonService;
	private IMonAn_Service monAnService;
	private INhanVien_Service nhanVienService;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

	public TraCuuHoaDon_UI() {
		try {
			String url = ConfigManager.getRmiUrl();
			hoaDonService = (IHoaDon_Service) Naming.lookup(url +"HoaDon_Service");
			hoaDonBanService = (IHoaDon_Ban_Service) Naming.lookup(url +"HoaDon_Ban_Service");
			khachHangService = (IKhachHang_Service) Naming.lookup(url +"KhachHang_Service");
			banAnService = (IBanAn_Service) Naming.lookup(url +"BanAn_Service");
			chiTietHoaDonService = (IChiTietHoaDon_Service) Naming.lookup(url +"ChiTietHoaDon_Service");
			monAnService = (IMonAn_Service) Naming.lookup(url +"MonAn_Service");
			nhanVienService = (INhanVien_Service) Naming.lookup(url +"NhanVien_Service");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
		}
		khoiTaoGiaoDien();
		taiDuLieuLenBang();
	}

	private void khoiTaoGiaoDien() {
		setLayout(new BorderLayout());
		setBackground(MAU_NEN_TAB);
		setBorder(new EmptyBorder(20, 25, 20, 25));

		add(taoPanelTieuDe(), BorderLayout.NORTH);

		JPanel wrapperNoiDung = new JPanel(new BorderLayout(0, 15));
		wrapperNoiDung.setOpaque(false);
		wrapperNoiDung.add(taoPanelDieuKhien(), BorderLayout.NORTH);
		wrapperNoiDung.add(taoPanelBang(), BorderLayout.CENTER);

		add(wrapperNoiDung, BorderLayout.CENTER);

		this.setFocusable(true);
		this.requestFocusInWindow();
	}

	private JPanel taoPanelTieuDe() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(10, 0, 20, 0));

		JLabel lblTieuDe = new JLabel("TRA CỨU HÓA ĐƠN");
		lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 36));
		lblTieuDe.setForeground(Color.WHITE);

		panel.add(lblTieuDe);
		return panel;
	}

	private JPanel taoPanelDieuKhien() {
		JPanel mainControlPanel = new JPanel();
		mainControlPanel.setOpaque(false);
		mainControlPanel.setLayout(new BoxLayout(mainControlPanel, BoxLayout.Y_AXIS));
		mainControlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel topRowPanel = new JPanel();
		topRowPanel.setOpaque(false);
		topRowPanel.setLayout(new BoxLayout(topRowPanel, BoxLayout.X_AXIS));
		topRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel wrapperMaHD = taoWrapperTimKiemCoNhan("Mã HĐ:", "Mã hóa đơn...");
		txtTimKiemMaHD = (JTextField) wrapperMaHD.getComponent(1);

		JPanel wrapperMaKH = taoWrapperTimKiemCoNhan("Mã KH:", "Mã khách hàng...");
		txtTimKiemMaKH = (JTextField) wrapperMaKH.getComponent(1);

		JPanel wrapperMaNV = taoWrapperTimKiemCoNhan("Mã NV:", "Mã nhân viên...");
		txtTimKiemMaNV = (JTextField) wrapperMaNV.getComponent(1);

		JPanel wrapperTuNgay = taoWrapperDateChooser("Từ ngày:");
		dateTuNgay = (JDateChooser) wrapperTuNgay.getComponent(1);
		dateTuNgay.addPropertyChangeListener("date", e -> thucHienTimKiemNangCao());

		JPanel wrapperDenNgay = taoWrapperDateChooser("Đến ngày:");
		dateDenNgay = (JDateChooser) wrapperDenNgay.getComponent(1);
		dateDenNgay.addPropertyChangeListener("date", e -> thucHienTimKiemNangCao());

		topRowPanel.add(wrapperMaHD);
		topRowPanel.add(Box.createHorizontalStrut(15));
		topRowPanel.add(wrapperMaKH);
		topRowPanel.add(Box.createHorizontalStrut(15));
		topRowPanel.add(wrapperMaNV);
		topRowPanel.add(Box.createHorizontalStrut(15));
		topRowPanel.add(wrapperTuNgay);
		topRowPanel.add(Box.createHorizontalStrut(15));
		topRowPanel.add(wrapperDenNgay);
		topRowPanel.add(Box.createHorizontalGlue());

		JPanel bottomRowPanel = new JPanel();
		bottomRowPanel.setOpaque(false);
		bottomRowPanel.setLayout(new BoxLayout(bottomRowPanel, BoxLayout.X_AXIS));
		bottomRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		cmbTrangThai = taoComboBox(new String[] { "Tất cả trạng thái", "Chưa thanh toán", "Đã thanh toán" },
				new Dimension(180, 40));
		cmbTrangThai.addActionListener(e -> thucHienTimKiemNangCao());

		cmbSapXep = taoComboBox(new String[] { "Sắp xếp mặc định", "Mới nhất", "Cũ nhất" }, new Dimension(200, 40));
		cmbSapXep.addActionListener(e -> thucHienSapXep());

		JButton btnLamMoi = taoNutChucNang("Làm mới", new Color(33, 150, 243));
		btnLamMoi.addActionListener(e -> lamMoiGiaoDien());

		JButton btnInLai = taoNutChucNang("In lại HĐ", new Color(76, 175, 80));
		btnInLai.addActionListener(e -> xuLyInLaiHoaDon());

		bottomRowPanel.add(cmbTrangThai);
		bottomRowPanel.add(Box.createHorizontalStrut(15));
		bottomRowPanel.add(cmbSapXep);
		bottomRowPanel.add(Box.createHorizontalGlue());
		bottomRowPanel.add(btnLamMoi);
		bottomRowPanel.add(Box.createHorizontalStrut(10));
		bottomRowPanel.add(btnInLai);
		bottomRowPanel.add(Box.createHorizontalStrut(10));

		mainControlPanel.add(topRowPanel);
		mainControlPanel.add(Box.createVerticalStrut(15));
		mainControlPanel.add(bottomRowPanel);

		return mainControlPanel;
	}

	private JPanel taoWrapperTimKiemCoNhan(String labelText, String placeholder) {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(MAU_NEN_TAB);
		wrapper.setPreferredSize(new Dimension(220, 40));
		wrapper.setMaximumSize(new Dimension(250, 40));

		JLabel label = new JLabel(labelText);
		label.setFont(FONT_NHAN);
		label.setForeground(Color.WHITE);
		label.setBackground(new Color(124, 124, 124));
		label.setOpaque(false);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		label.setPreferredSize(new Dimension(60, 40));

		JTextField txt = taoTextFieldTimKiem(placeholder);

		wrapper.add(label, BorderLayout.WEST);
		wrapper.add(txt, BorderLayout.CENTER);

		return wrapper;
	}

	private JPanel taoWrapperDateChooser(String labelText) {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(MAU_NEN_TAB);
		wrapper.setMaximumSize(new Dimension(220, 40));

		JLabel label = new JLabel(labelText);
		label.setFont(FONT_NHAN);
		label.setForeground(Color.WHITE);
		label.setBorder(new EmptyBorder(0, 5, 0, 5));

		JDateChooser dateChooser = taoDateChooser();

		wrapper.add(label, BorderLayout.WEST);
		wrapper.add(dateChooser, BorderLayout.CENTER);
		return wrapper;
	}

	private JDateChooser taoDateChooser() {
		JDateChooser dateChooser = new JDateChooser();
		dateChooser.setPreferredSize(new Dimension(140, 40));
		dateChooser.setDateFormatString("dd/MM/yyyy");
		dateChooser.setFont(FONT_TEXTFIELD);
		dateChooser.setBackground(MAU_THANH_TIM_KIEM);
		dateChooser.setForeground(MAU_CHU_CHUNG);
		dateChooser.getCalendarButton().setBackground(MAU_THANH_TIM_KIEM);
		dateChooser.getCalendarButton().setBorder(BorderFactory.createEmptyBorder());
		dateChooser.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
		dateChooser.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));

		JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) dateChooser.getDateEditor().getUiComponent();
		dateEditor.setBackground(MAU_THANH_TIM_KIEM);
		dateEditor.setForeground(Color.WHITE);
		dateEditor.setCaretColor(Color.WHITE);
		dateEditor.setSelectedTextColor(Color.WHITE);
		dateEditor.setSelectionColor(MAU_VIEN_THANH_TIM_KIEM);
		dateEditor.setFont(FONT_TEXTFIELD);
		dateEditor.setBorder(new EmptyBorder(0, 8, 0, 0));
		dateEditor.setOpaque(true);
		dateEditor.setEditable(false);

		dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
			if ("date".equals(evt.getPropertyName())) {
				SwingUtilities.invokeLater(() -> dateEditor.setForeground(Color.WHITE));
			}
		});
		dateEditor.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				dateEditor.setForeground(Color.WHITE);
			}

			@Override
			public void focusLost(FocusEvent e) {
				dateEditor.setForeground(Color.WHITE);
			}
		});

		return dateChooser;
	}

	private JTextField taoTextFieldTimKiem(String holder) {
		JTextField txt = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				try {
					ImageIcon icon = new ImageIcon(getClass().getResource("/IMG/search.png"));
					if (icon.getImage() != null) {
						Image img = icon.getImage().getScaledInstance(KICH_THUOC_ICON, KICH_THUOC_ICON,
								Image.SCALE_SMOOTH);
						Icon searchIcon = new ImageIcon(img);
						int y = (getHeight() - searchIcon.getIconHeight()) / 2;
						int x = getWidth() - searchIcon.getIconWidth() - 10;
						searchIcon.paintIcon(this, g, x, y);
					}
				} catch (Exception e) {}
			}
		};

		txt.setText(holder);
		txt.setForeground(MAU_PLACEHOLDER);
		txt.setBackground(MAU_THANH_TIM_KIEM);
		txt.setCaretColor(MAU_CHU_CHUNG);
		txt.setFont(FONT_TEXTFIELD);
		txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
				new EmptyBorder(8, 10, 8, 35)));

		txt.addActionListener(e -> thucHienTimKiemNangCao());

		txt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int iconX = txt.getWidth() - KICH_THUOC_ICON - 10;
				if (new Rectangle(iconX, 0, KICH_THUOC_ICON + 10, txt.getHeight()).contains(e.getPoint())) {
					thucHienTimKiemNangCao();
				}
			}
		});

		txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txt.getText().equals(holder)) {
					txt.setText("");
					txt.setForeground(MAU_CHU_CHUNG);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (txt.getText().isEmpty()) {
					txt.setText(holder);
					txt.setForeground(MAU_PLACEHOLDER);
				}
			}
		});
		return txt;
	}

	private JComboBox<String> taoComboBox(String[] items, Dimension size) {
		JComboBox<String> cmb = new JComboBox<>(items);
		cmb.setFont(FONT_NHAN);
		cmb.setBackground(MAU_THANH_TIM_KIEM);
		cmb.setForeground(MAU_CHU_CHUNG);
		cmb.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cmb.setFocusable(false);
		cmb.setPreferredSize(size);
		cmb.setMaximumSize(size);
		cmb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1),
				new EmptyBorder(0, 15, 0, 5)));

		cmb.setUI(new BasicComboBoxUI() {
			@Override
			protected JButton createArrowButton() {
				try {
					ImageIcon icon = new ImageIcon(getClass().getResource("/IMG/muitenxuong_32px.png"));
					Image img = icon.getImage().getScaledInstance(KICH_THUOC_ICON, KICH_THUOC_ICON, Image.SCALE_SMOOTH);
					JButton btn = new JButton(new ImageIcon(img));
					btn.setBackground(MAU_THANH_TIM_KIEM);
					btn.setOpaque(true);
					btn.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
					return btn;
				} catch (Exception e) {
					JButton btn = new JButton("▼");
					btn.setBackground(MAU_THANH_TIM_KIEM);
					return btn;
				}
			}
		});

		return cmb;
	}

	private JButton taoNutChucNang(String text, Color mauNen) {
		JButton btn = new JButton(text);
		btn.setFont(FONT_NHAN);
		btn.setForeground(Color.WHITE);
		btn.setBackground(mauNen);
		btn.setFocusPainted(false);
		btn.setBorder(new EmptyBorder(10, 20, 10, 20));
		btn.setPreferredSize(new Dimension(120, 40));
		btn.setMaximumSize(new Dimension(120, 40));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(mauNen.brighter());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(mauNen);
			}
		});
		return btn;
	}

	private JPanel taoPanelBang() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);

		String[] columnNames = { "Mã HĐ", "Ngày Lập", "Trạng Thái", "Tên Khách Hàng", "Mã NV", "Tiền Cọc", "Khách Trả",
				"Tiền Thối" };

		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}
		};

		table = new JTable(tableModel);
		setupTableStyle();

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(MAU_LUOI_BANG, 1));
		scrollPane.getViewport().setBackground(MAU_NEN_ITEM);
		JPanel corner = new JPanel();
		corner.setBackground(MAU_NEN_INPUT);
		scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
		tuyChinhScrollBar(scrollPane);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	private void tuyChinhScrollBar(JScrollPane s) {
		s.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		s.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
		s.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
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

	private void setupTableStyle() {

		table.setBackground(MAU_NEN_ITEM);
		table.setForeground(MAU_CHU_CHUNG);
		table.setGridColor(MAU_LUOI_BANG);
		table.setRowHeight(40);
		table.setFont(FONT_BANG);
		table.setSelectionBackground(MAU_CHON_HANG);
		table.setSelectionForeground(Color.WHITE);
		table.setFillsViewportHeight(true);

		JTableHeader header = table.getTableHeader();
		header.setPreferredSize(new Dimension(0, 45));
		header.setBackground(MAU_NEN_ITEM);

		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
														   boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				setBackground(MAU_NEN_ITEM);
				setForeground(MAU_CHU_CHUNG);
				setFont(FONT_HEADER_BANG);
				setHorizontalAlignment(JLabel.CENTER);

				setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MAU_LUOI_BANG));

				return this;
			}
		};

		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
		}

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
	}

	private void taiDuLieuLenBang() {
		if (hoaDonService == null) return;
		SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
			@Override
			protected Object[] doInBackground() throws Exception {
				List<HoaDon> list = hoaDonService.getAllHoaDon();
				Map<String, String> khMap = new HashMap<>();
				if (list != null) {
					for (HoaDon hd : list) {
						String maKH = hd.getMaKhachHang();
						if (maKH != null && !maKH.trim().isEmpty() && !maKH.equalsIgnoreCase("null") && !khMap.containsKey(maKH)) {
							List<KhachHang> khs = khachHangService.timKiemTheoMa(maKH);
							if (khs != null && !khs.isEmpty()) {
								khMap.put(maKH, khs.get(0).getHoTen());
							} else {
								khMap.put(maKH, "Khách lẻ");
							}
						}
					}
				}
				return new Object[]{list, khMap};
			}
			@Override
			@SuppressWarnings("unchecked")
			protected void done() {
				try {
					Object[] res = get();
					hienThiDanhSachUI((List<HoaDon>) res[0], (Map<String, String>) res[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		worker.execute();
	}

	private void hienThiDanhSachUI(List<HoaDon> danhSach, Map<String, String> khMap) {
		tableModel.setRowCount(0);
		if (danhSach == null) return;
		for (HoaDon hd : danhSach) {
			String tenKhachHang = "Khách lẻ";
			String maKH = hd.getMaKhachHang();
			// Kiểm tra lấy tên khách hàng từ Map đã được query
			if (maKH != null && !maKH.trim().isEmpty() && !maKH.equalsIgnoreCase("null")) {
				tenKhachHang = khMap.getOrDefault(maKH, "Khách lẻ");
			}

			// Giữ nguyên hiển thị mã Nhân Viên
			String maNV = hd.getMaNhanVien();
			if (maNV == null || maNV.trim().isEmpty() || maNV.equalsIgnoreCase("null")) {
				maNV = "";
			}

			double tienDatCoc = hd.getTienDatCoc() != null ? hd.getTienDatCoc().doubleValue() : 0;
			double khachTra = hd.getSoTienKhachTra() != null ? hd.getSoTienKhachTra().doubleValue() : 0;
			double tienThoi = hd.getSoTienThoi() != null ? hd.getSoTienThoi().doubleValue() : 0;

			tableModel.addRow(new Object[] {
					hd.getMaHoaDon(),
					hd.getNgayLapHoaDon() != null ? dateFormat.format(hd.getNgayLapHoaDon()) : "",
					hd.getTrangThai(),
					tenKhachHang,
					maNV,
					currencyFormat.format(tienDatCoc),
					currencyFormat.format(khachTra),
					currencyFormat.format(tienThoi)
			});
		}
	}

	private void thucHienTimKiemNangCao() {
		String rawMaHD = txtTimKiemMaHD.getText().trim();
		String rawMaKH = txtTimKiemMaKH.getText().trim();
		String rawMaNV = txtTimKiemMaNV.getText().trim();
		Date tuNgay = dateTuNgay.getDate();
		Date denNgay = dateDenNgay.getDate();

		final String maHD = rawMaHD.contains("...") ? "" : rawMaHD;
		final String maKH = rawMaKH.contains("...") ? "" : rawMaKH;
		final String maNV = rawMaNV.contains("...") ? "" : rawMaNV;

		int trangThaiIndex = cmbTrangThai.getSelectedIndex();
		final String trangThai = trangThaiIndex > 0 ? cmbTrangThai.getSelectedItem().toString() : "";

		SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
			@Override
			protected Object[] doInBackground() throws Exception {
				List<HoaDon> ketQua = hoaDonService.timKiemNangCao(maHD, maKH, maNV, tuNgay, denNgay);

				if (!trangThai.isEmpty() && ketQua != null) {
					List<HoaDon> filteredList = new ArrayList<>();
					for (HoaDon hd : ketQua) {
						if (hd.getTrangThai().equalsIgnoreCase(trangThai)) {
							filteredList.add(hd);
						}
					}
					ketQua = filteredList;
				}

				Map<String, String> khMap = new HashMap<>();
				if (ketQua != null) {
					for (HoaDon hd : ketQua) {
						String maKHTemp = hd.getMaKhachHang();
						if (maKHTemp != null && !maKHTemp.trim().isEmpty() && !maKHTemp.equalsIgnoreCase("null") && !khMap.containsKey(maKHTemp)) {
							List<KhachHang> khs = khachHangService.timKiemTheoMa(maKHTemp);
							if (khs != null && !khs.isEmpty()) {
								khMap.put(maKHTemp, khs.get(0).getHoTen());
							} else {
								khMap.put(maKHTemp, "Khách lẻ");
							}
						}
					}
				}
				return new Object[]{ketQua, khMap};
			}
			@Override
			@SuppressWarnings("unchecked")
			protected void done() {
				try {
					Object[] res = get();
					hienThiDanhSachUI((List<HoaDon>) res[0], (Map<String, String>) res[1]);
					TraCuuHoaDon_UI.this.requestFocusInWindow();
				} catch (Exception e) {}
			}
		};
		worker.execute();
	}

	private void thucHienSapXep() {
		int selectedIndex = cmbSapXep.getSelectedIndex();
		String orderByTemp;
		switch (selectedIndex) {
			case 1:
				orderByTemp = "ngayLapHoaDon DESC";
				break;
			case 2:
				orderByTemp = "ngayLapHoaDon ASC";
				break;
			default:
				taiDuLieuLenBang();
				return;
		}

		final String orderBy = orderByTemp;

		SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
			@Override
			protected Object[] doInBackground() throws Exception {
				List<HoaDon> ketQua = hoaDonService.sapXep(orderBy);
				Map<String, String> khMap = new HashMap<>();
				if (ketQua != null) {
					for (HoaDon hd : ketQua) {
						String maKHTemp = hd.getMaKhachHang();
						if (maKHTemp != null && !maKHTemp.trim().isEmpty() && !maKHTemp.equalsIgnoreCase("null") && !khMap.containsKey(maKHTemp)) {
							List<KhachHang> khs = khachHangService.timKiemTheoMa(maKHTemp);
							if (khs != null && !khs.isEmpty()) {
								khMap.put(maKHTemp, khs.get(0).getHoTen());
							} else {
								khMap.put(maKHTemp, "Khách lẻ");
							}
						}
					}
				}
				return new Object[]{ketQua, khMap};
			}
			@Override
			@SuppressWarnings("unchecked")
			protected void done() {
				try {
					Object[] res = get();
					hienThiDanhSachUI((List<HoaDon>) res[0], (Map<String, String>) res[1]);
				} catch (Exception e) {}
			}
		};
		worker.execute();
	}

	private void lamMoiGiaoDien() {
		taiDuLieuLenBang();
		txtTimKiemMaHD.setText("Mã hóa đơn...");
		txtTimKiemMaHD.setForeground(MAU_PLACEHOLDER);
		txtTimKiemMaKH.setText("Mã khách hàng...");
		txtTimKiemMaKH.setForeground(MAU_PLACEHOLDER);
		txtTimKiemMaNV.setText("Mã nhân viên...");
		txtTimKiemMaNV.setForeground(MAU_PLACEHOLDER);
		dateTuNgay.setDate(null);
		dateDenNgay.setDate(null);
		cmbTrangThai.setSelectedIndex(0);
		cmbSapXep.setSelectedIndex(0);
		this.requestFocusInWindow();
	}

	private void xuLyInLaiHoaDon() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để in lại!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		String maHoaDon = table.getValueAt(row, 0).toString();

		int confirm = JOptionPane.showConfirmDialog(this,
				"Bạn có muốn in lại hóa đơn " + maHoaDon + " không?",
				"Xác nhận in", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			SwingWorker<Void, Void> printWorker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					HoaDon hd = hoaDonService.timTheoMa(maHoaDon);

					if (hd == null) {
						throw new Exception("Không tìm thấy thông tin hóa đơn trong CSDL!");
					}
					if (!"Đã thanh toán".equalsIgnoreCase(hd.getTrangThai())) {
						throw new Exception("Chỉ có thể in lại những hóa đơn ĐÃ THANH TOÁN!");
					}

					KhachHang kh = null;
					if (hd.getMaKhachHang() != null) {
						List<KhachHang> listKH = khachHangService.timKiemTheoMa(hd.getMaKhachHang());
						if (!listKH.isEmpty()) {
							kh = listKH.get(0);
						}
					}

					NhanVien nv = null;
					if (hd.getMaNhanVien() != null) {
						List<NhanVien> nvs = nhanVienService.timKiemNhanVienTheoMa(hd.getMaNhanVien());
						if (nvs != null && !nvs.isEmpty()) {
							nv = nvs.get(0);
						}
					}

					List<BanAn> listBan = new ArrayList<>();
					List<String> listMaBan = hoaDonBanService.layDanhSachMaBanTheoHoaDon(maHoaDon);
					for (String maBan : listMaBan) {
						BanAn b = banAnService.timBanAnTheoMa(maBan);
						if (b != null)
							listBan.add(b);
					}

					DefaultTableModel modelChiTiet = new DefaultTableModel();
					modelChiTiet.setColumnIdentifiers(new Object[] { "STT", "Tên món", "Giá", "Đơn vị", "Số lượng", "Thành tiền" });

					List<ChiTietHoaDon> listCTHD = chiTietHoaDonService.getChiTietTheoMaHoaDon(maHoaDon);

					double tongCong = 0;
					int stt = 1;

					for (ChiTietHoaDon ct : listCTHD) {
						MonAn mon = monAnService.timMotMonTheoMa(ct.getMaMon());
						if (mon != null) {
							double donGia = ct.getDonGia().doubleValue();
							double thanhTien = donGia * ct.getSoLuong();
							tongCong += thanhTien;

							modelChiTiet.addRow(new Object[] {
									stt++,
									mon.getTenMon(),
									currencyFormat.format(donGia),
									mon.getDonVi(),
									ct.getSoLuong(),
									currencyFormat.format(thanhTien)
							});
						}
					}

					double thue = (hd.getThue() != null) ? hd.getThue().doubleValue() : 0;
					double tienCoc = (hd.getTienDatCoc() != null) ? hd.getTienDatCoc().doubleValue() : 0;
					double khachTra = (hd.getSoTienKhachTra() != null) ? hd.getSoTienKhachTra().doubleValue() : 0;
					double tienThoi = (hd.getSoTienThoi() != null) ? hd.getSoTienThoi().doubleValue() : 0;
					double tongThucThu = khachTra - tienThoi;
					double tongLyThuyet = tongCong + thue - tienCoc;
					double tienGiamGiaTri = tongLyThuyet - tongThucThu;
					if (tienGiamGiaTri < 0) tienGiamGiaTri = 0;
					BigDecimal tienGiamBD = BigDecimal.valueOf(tienGiamGiaTri);

					HoaDonPDF.xuatHoaDonPDF(
							hd,
							kh,
							nv,
							listBan,
							hd.getNgayLapHoaDon(),
							hd.getNgayLapHoaDon(),
							modelChiTiet,
							tongCong,
							thue,
							tongThucThu,
							tienCoc,
							tienGiamBD,
							null
					);
					return null;
				}

				@Override
				protected void done() {
					try {
						get();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(TraCuuHoaDon_UI.this, e.getMessage(), "Lỗi/Cảnh báo", JOptionPane.WARNING_MESSAGE);
					}
				}
			};
			printWorker.execute();
		}
	}
}
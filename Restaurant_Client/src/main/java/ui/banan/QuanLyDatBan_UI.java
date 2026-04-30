package ui.banan;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import java.util.Date;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.rmi.Naming;

import rmi_interfaces.IBanAn_Service;
import rmi_interfaces.IChiTietHoaDon_Service;
import rmi_interfaces.IChiTietPhieuDatBan_Service;
import rmi_interfaces.IHoaDon_Service;
import rmi_interfaces.IHoaDon_Ban_Service;
import rmi_interfaces.IKhachHang_Service;
import rmi_interfaces.IPhieuDatBan_Service;
import rmi_interfaces.ILichSuHuyDatBan_Service;
import rmi_interfaces.IPhieuDatBan_Ban_Service;

import entity.BanAn;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuDatBan;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatBan;
import ui.Auth;

public class QuanLyDatBan_UI extends JPanel {

	private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
	private final Color MAU_NEN_TAB = new Color(48, 52, 56);
	private final Color MAU_NEN_ITEM = new Color(31, 32, 34);
	private final Color MAU_CHU_CHUNG = Color.WHITE;
	private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
	private final Color MAU_CAM_GIA = new Color(255, 180, 0);
	private final Color MAU_VIEN_CHON = new Color(0, 191, 255);
	private final Color MAU_NUT_KICH_HOAT = new Color(241, 121, 104);
	private final Color MAU_NUT_KICH_HOAT_HOVER = new Color(245, 145, 130);
	private final Color MAU_NUT_VO_HIEU = new Color(60, 64, 68);
	private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
	private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);

	private final int KICH_THUOC_ITEM_RONG = 241;
	private final int KICH_THUOC_ITEM_CAO = 270;

	private JTextField searchField;
	private JTextField searchFieldKhachHang;
	private IBanAn_Service banAn_DAO;
	private IHoaDon_Service hoaDon_DAO;
	private IHoaDon_Ban_Service hoaDonBanDAO;
	private IKhachHang_Service khachHang_DAO;
	private IPhieuDatBan_Service phieuDatBanDAO;
	private IChiTietPhieuDatBan_Service chiTietPhieuDatBanDAO;
	private IChiTietHoaDon_Service chiTietHoaDonDAO;
	private ILichSuHuyDatBan_Service lichSuHuyDatBanDAO;
	private IPhieuDatBan_Ban_Service phieuDatBan_BanDAO;

	private List<BanAn> danhSachBanTongCuaNgay;
	private List<BanAn> danhSachBan;

	private JPanel panelLuoiBanAn;
	private JButton btnTrong, btnCho, btnPhucVu;

	private JComboBox<String> cmbLoaiBan;
	private JComboBox<String> cmbTang;
	private JComboBox<String> cmbKhu;
	private java.util.Map<String, JPanel> mapBanUI = new java.util.HashMap<>();

	private JDateChooser dateChooserLocNgay;

	private BanAn banDangChon = null;
	private List<BanAn> danhSachBanDangChon = new ArrayList<>();
	private boolean isMultiSelectMode = false;
	private JToggleButton btnCheDoChonNhieu;

	private int soBanTrong = 0;
	private int soBanCho = 0;
	private int soBanPhucVu = 0;

	private JButton btnDatBanNgay, btnDatBanCho, btnNhanBanCho, btnHuyBanCho, btnXemChiTiet, btnChuyenBan, btnDatMon,
			btnTinhTien;

	public QuanLyDatBan_UI() {
		try {
			banAn_DAO = (IBanAn_Service) Naming.lookup("rmi://localhost:1099/BanAn_Service");
			hoaDon_DAO = (IHoaDon_Service) Naming.lookup("rmi://localhost:1099/HoaDon_Service");
			khachHang_DAO = (IKhachHang_Service) Naming.lookup("rmi://localhost:1099/KhachHang_Service");
			phieuDatBanDAO = (IPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBan_Service");
			hoaDonBanDAO = (IHoaDon_Ban_Service) Naming.lookup("rmi://localhost:1099/HoaDon_Ban_Service");
			chiTietPhieuDatBanDAO = (IChiTietPhieuDatBan_Service) Naming.lookup("rmi://localhost:1099/ChiTietPhieuDatBan_Service");
			chiTietHoaDonDAO = (IChiTietHoaDon_Service) Naming.lookup("rmi://localhost:1099/ChiTietHoaDon_Service");
			lichSuHuyDatBanDAO = (ILichSuHuyDatBan_Service) Naming.lookup("rmi://localhost:1099/LichSuHuyDatBan_Service");
			phieuDatBan_BanDAO = (IPhieuDatBan_Ban_Service) Naming.lookup("rmi://localhost:1099/PhieuDatBan_Ban_Service");


			this.danhSachBan = banAn_DAO.docDanhSachBan();
		} catch (Exception e) {
			e.printStackTrace();
			this.danhSachBan = new ArrayList<>();
		}

		this.danhSachBanTongCuaNgay = new ArrayList<>(this.danhSachBan);

		setLayout(new BorderLayout());
		setBackground(MAU_NEN_TAB);

		JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
		mainPanel.setBackground(MAU_NEN_TAB);
		mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

		JPanel pNorth = taoPanelNorth();

		JPanel pSouth = taoPanelSouth();
		pSouth.setBackground(MAU_NEN_TAB);

		this.panelLuoiBanAn = new WrapFlowPanel();
		panelLuoiBanAn.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
		panelLuoiBanAn.setBackground(MAU_NEN_TAB);

		panelLuoiBanAn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				isMultiSelectMode = false;
				banDangChon = null;
				danhSachBanDangChon.clear();

				if (btnCheDoChonNhieu != null) {
					btnCheDoChonNhieu.setSelected(false);
					btnCheDoChonNhieu.setBackground(new Color(100, 100, 100));
				}
				panelLuoiBanAn.repaint();
				capNhatTrangThaiCacNutChucNang();
			}
		});

		JScrollPane scrollPane = new JScrollPane(panelLuoiBanAn);
		scrollPane.getVerticalScrollBar().setUnitIncrement(40);
		scrollPane.getVerticalScrollBar().setBlockIncrement(100);
		tuyChinhScrollBar(scrollPane);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(MAU_NEN_TAB);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		mainPanel.add(pNorth, BorderLayout.NORTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(pSouth, BorderLayout.SOUTH);

		add(mainPanel, BorderLayout.CENTER);

		khoiTaoKeyBindings();
		lamMoiGiaoDien();
		capNhatTrangThaiCacNutChucNang();

		Timer timerTuDongQuet = new Timer(120000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (QuanLyDatBan_UI.this.isShowing()) {
					tuDongQuetTrangThai();
				}
			}
		});
		timerTuDongQuet.start();
	}

	private JPanel taoItemsBan(BanAn ban) {
		JPanel panelItem = new JPanel(new BorderLayout());
		panelItem.setPreferredSize(new Dimension(KICH_THUOC_ITEM_RONG, KICH_THUOC_ITEM_CAO));
		panelItem.setBackground(MAU_NEN_TAB);
		panelItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

		JPanel backgroundPanel = new JPanel(new BorderLayout(0, 0)) {
			private final int ARC_SIZE = 15;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(MAU_NEN_ITEM);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_SIZE, ARC_SIZE);

				boolean isSelectedSingle = (banDangChon != null && banDangChon.getMaBan().equals(ban.getMaBan()));
				boolean isSelectedMulti = kiemTraBanDaChon(ban.getMaBan());

				if (isSelectedSingle || isSelectedMulti) {
					g2d.setColor(MAU_VIEN_CHON);
					g2d.setStroke(new BasicStroke(4));
					g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, ARC_SIZE, ARC_SIZE);

					if (isMultiSelectMode && isSelectedMulti) {
						g2d.setColor(MAU_VIEN_CHON);
						g2d.fillOval(getWidth() - 30, 10, 20, 20);
						g2d.setColor(Color.WHITE);
						g2d.setStroke(new BasicStroke(2));
						g2d.drawLine(getWidth() - 26, 20, getWidth() - 22, 24);
						g2d.drawLine(getWidth() - 22, 24, getWidth() - 14, 16);
					}
				}
				g2d.dispose();
			}
		};
		backgroundPanel.setOpaque(false);
		backgroundPanel.setBorder(new EmptyBorder(2, 10, 2, 10));

		JLabel lblHinhAnh = new JLabel();
		try {
			String imagePath = layDuongDanAnhBan(ban);
			ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
			Image img = icon.getImage().getScaledInstance(KICH_THUOC_ITEM_RONG - 15, 180, Image.SCALE_SMOOTH);
			lblHinhAnh.setIcon(new ImageIcon(img));
		} catch (Exception e) {
			lblHinhAnh.setText("Ảnh Lỗi");
			lblHinhAnh.setForeground(MAU_CHU_CHUNG);
		}
		lblHinhAnh.setHorizontalAlignment(SwingConstants.CENTER);
		backgroundPanel.add(lblHinhAnh, BorderLayout.NORTH);

		JPanel panelThongTin = new JPanel();
		panelThongTin.setLayout(new BoxLayout(panelThongTin, BoxLayout.Y_AXIS));
		panelThongTin.setOpaque(false);

		JLabel lblTenBan = new JLabel(ban.getTenBan());
		lblTenBan.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTenBan.setForeground(MAU_CHU_CHUNG);
		lblTenBan.setAlignmentX(Component.CENTER_ALIGNMENT);

		String hienThiChiTiet = String.format("%s - %s - %s", ban.getTenTang(), ban.getTenKhu(), ban.getLoaiBan());
		JLabel lblLoaiBan = new JLabel(hienThiChiTiet);
		lblLoaiBan.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblLoaiBan.setForeground(MAU_CAM_GIA);
		lblLoaiBan.setAlignmentX(Component.CENTER_ALIGNMENT);

		String hienThiKhachHang = " ";
		String trangThai = ban.getTrangThai();
		if (trangThai.equals("Bàn đang chờ") || trangThai.equals("Bàn đang phục vụ")) {
			String tenKhach = ban.getTenKhachHang();
			if (tenKhach != null && !tenKhach.isEmpty()) {
				if (tenKhach.length() > 20) {
					tenKhach = tenKhach.substring(0, 17) + "...";
				}
				hienThiKhachHang = tenKhach;
			} else {
				hienThiKhachHang = "KH: Khách lẻ";
			}
		}

		JLabel lblKhachHang = new JLabel(hienThiKhachHang);
		lblKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblKhachHang.setForeground(MAU_CHU_CHUNG);
		lblKhachHang.setAlignmentX(Component.CENTER_ALIGNMENT);

		panelThongTin.add(lblTenBan);
		panelThongTin.add(Box.createRigidArea(new Dimension(0, 4)));
		panelThongTin.add(lblLoaiBan);
		panelThongTin.add(Box.createRigidArea(new Dimension(0, 4)));
		panelThongTin.add(lblKhachHang);

		backgroundPanel.add(panelThongTin, BorderLayout.CENTER);
		panelItem.add(backgroundPanel, BorderLayout.CENTER);

		panelItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					List<BanAn> nhomBan = timNhomBanLienQuan(ban);
					if (nhomBan.size() > 1) {
						danhSachBanDangChon.clear();
						danhSachBanDangChon.addAll(nhomBan);
						banDangChon = null;
						isMultiSelectMode = false;
						if (btnCheDoChonNhieu != null) {
							btnCheDoChonNhieu.setSelected(false);
							btnCheDoChonNhieu.setText("Chọn nhiều");
							btnCheDoChonNhieu.setBackground(new Color(100, 100, 100));
						}
					}
				}
				else if (e.getClickCount() == 1) {
					if (isMultiSelectMode) {
						if (!ban.getTrangThai().equals("Bàn đang trống")) {
							return;
						}
						if (kiemTraBanDaChon(ban.getMaBan())) {
							danhSachBanDangChon.removeIf(b -> b.getMaBan().equals(ban.getMaBan()));
						} else {
							danhSachBanDangChon.add(ban);
						}
						banDangChon = null;
					} else {
						banDangChon = ban;
						danhSachBanDangChon.clear();
						if (btnCheDoChonNhieu != null) {
							btnCheDoChonNhieu.setSelected(false);
							btnCheDoChonNhieu.setBackground(new Color(100, 100, 100));
						}
					}
				}
				panelLuoiBanAn.repaint();
				capNhatTrangThaiCacNutChucNang();
			}
		});

		return panelItem;
	}

	private JPanel taoPanelSouth() {
		JPanel pContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 0));
		pContent.setBorder(new EmptyBorder(10, 0, 0, 0));
		pContent.setBackground(MAU_NEN_TAB);

		this.btnTrong = createStyledButton(String.format("Bàn đang trống (%d)", soBanTrong), new Color(157, 154, 155),
				MAU_NEN_ITEM.brighter());
		this.btnCho = createStyledButton(String.format("Bàn đang chờ (%d)", soBanCho), new Color(224, 211, 69),
				MAU_NEN_ITEM.brighter());
		this.btnPhucVu = createStyledButton(String.format("Bàn đang phục vụ (%d)", soBanPhucVu), new Color(235, 66, 66),
				MAU_NEN_ITEM.brighter());
		Dimension buttonSize = new Dimension(250, 50);
		btnTrong.setPreferredSize(buttonSize);
		btnCho.setPreferredSize(buttonSize);
		btnPhucVu.setPreferredSize(buttonSize);

		btnTrong.addActionListener(e -> locVaHienThiTheoNut("Bàn đang trống"));
		btnCho.addActionListener(e -> locVaHienThiTheoNut("Bàn đang chờ"));
		btnPhucVu.addActionListener(e -> locVaHienThiTheoNut("Bàn đang phục vụ"));
		pContent.add(btnTrong);
		pContent.add(btnCho);
		pContent.add(btnPhucVu);

		return pContent;
	}

	private void locVaHienThiTheoNut(String trangThai) {
		locDuLieuBan();

		List<BanAn> dsLocTheoNut = new ArrayList<>();
		if (this.danhSachBanTongCuaNgay != null) {
			for (BanAn b : this.danhSachBanTongCuaNgay) {
				if (b.getTrangThai().equalsIgnoreCase(trangThai)) {
					dsLocTheoNut.add(b);
				}
			}
		}

		capNhatLuoiBan(dsLocTheoNut);
	}

	private JPanel taoPanelNorth() {
		JPanel pContent = new JPanel(new BorderLayout());
		JPanel pCenter = new JPanel(new BorderLayout(0, 15));
		pCenter.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
		pCenter.setBackground(MAU_NEN_TAB);

		JPanel pButtonGrid = new JPanel(new GridLayout(2, 4, 15, 15));
		pButtonGrid.setBackground(MAU_NEN_TAB);

		btnDatBanNgay = createStyledButton("Đặt bàn ngay (F4)", MAU_NUT_KICH_HOAT, MAU_NUT_KICH_HOAT_HOVER);
		btnDatBanCho = createStyledButton("Đặt bàn chờ (F5)", MAU_NUT_KICH_HOAT, MAU_NUT_KICH_HOAT_HOVER);
		btnNhanBanCho = createStyledButton("Nhận bàn chờ (F6)", MAU_NUT_KICH_HOAT, MAU_NUT_KICH_HOAT_HOVER);
		btnHuyBanCho = createStyledButton("Hủy bàn chờ (F7)", MAU_NUT_KICH_HOAT, MAU_NUT_KICH_HOAT_HOVER);
		btnXemChiTiet = createStyledButton("Xem chi tiết (F8)", MAU_NUT_KICH_HOAT, MAU_NUT_KICH_HOAT_HOVER);
		btnChuyenBan = createStyledButton("Chuyển bàn (F9)", MAU_NUT_KICH_HOAT, MAU_NUT_KICH_HOAT_HOVER);
		btnDatMon = createStyledButton("Đặt món (F10)", MAU_NUT_KICH_HOAT, MAU_NUT_KICH_HOAT_HOVER);
		btnTinhTien = createStyledButton("Tính tiền (F11)", MAU_NUT_KICH_HOAT, MAU_NUT_KICH_HOAT_HOVER);

		btnDatBanNgay.addActionListener(e -> xuLyDatBanNgay());
		btnDatBanCho.addActionListener(e -> xuLyDatBanCho());
		btnNhanBanCho.addActionListener(e -> xuLyNhanBanCho());
		btnHuyBanCho.addActionListener(e -> xuLyHuyBanCho());
		btnXemChiTiet.addActionListener(e -> xuLyChiTiet());
		btnChuyenBan.addActionListener(e -> xuLyChuyenBan());
		btnDatMon.addActionListener(e -> xuLyDatMon());
		btnTinhTien.addActionListener(e -> xuLyTinhTien());

		pButtonGrid.add(btnDatBanNgay);
		pButtonGrid.add(btnDatBanCho);
		pButtonGrid.add(btnNhanBanCho);
		pButtonGrid.add(btnHuyBanCho);
		pButtonGrid.add(btnXemChiTiet);
		pButtonGrid.add(btnChuyenBan);
		pButtonGrid.add(btnDatMon);
		pButtonGrid.add(btnTinhTien);

		JPanel pFilterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		pFilterRow.setBackground(MAU_NEN_TAB);

		JPanel searchPanel = taoSearchPanel();
		searchPanel.setPreferredSize(new Dimension(320, 40));
		pFilterRow.add(searchPanel);

		JPanel searchPanelKhachHang = taoSearchPanelKhachHang();
		searchPanelKhachHang.setPreferredSize(new Dimension(290, 40));
		pFilterRow.add(searchPanelKhachHang);

		pFilterRow.add(taoComboBoxTang());
		pFilterRow.add(taoComboBoxKhu());
		pFilterRow.add(taoComboBoxLoaiBan());

		JPanel pNgay = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		pNgay.setBackground(MAU_NEN_TAB);
		JLabel lblNgay = new JLabel("Ngày xem: ");
		lblNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblNgay.setForeground(Color.WHITE);
		pNgay.add(lblNgay);

		dateChooserLocNgay = new JDateChooser();
		dateChooserLocNgay.setPreferredSize(new Dimension(140, 40));
		dateChooserLocNgay.setDateFormatString("dd/MM/yyyy");
		dateChooserLocNgay.setDate(new Date());
		dateChooserLocNgay.setFont(FONT_TEXTFIELD);
		dateChooserLocNgay.setBackground(MAU_THANH_TIM_KIEM);
		dateChooserLocNgay.setForeground(MAU_CHU_CHUNG);
		dateChooserLocNgay.getCalendarButton().setBackground(MAU_THANH_TIM_KIEM);
		dateChooserLocNgay.getCalendarButton().setBorder(BorderFactory.createEmptyBorder());
		dateChooserLocNgay.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
		dateChooserLocNgay.getCalendarButton().setCursor(new Cursor(Cursor.HAND_CURSOR));

		JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) dateChooserLocNgay.getDateEditor().getUiComponent();
		dateEditor.setBackground(MAU_THANH_TIM_KIEM);
		dateEditor.setForeground(Color.WHITE);
		dateEditor.setCaretColor(Color.WHITE);
		dateEditor.setSelectedTextColor(Color.WHITE);
		dateEditor.setSelectionColor(MAU_VIEN_THANH_TIM_KIEM);
		dateEditor.setFont(FONT_TEXTFIELD);
		dateEditor.setBorder(new EmptyBorder(0, 8, 0, 0));
		dateEditor.setOpaque(true);
		dateEditor.setEditable(false);

		dateChooserLocNgay.getDateEditor().addPropertyChangeListener(evt -> {
			if ("date".equals(evt.getPropertyName())) {
				SwingUtilities.invokeLater(() -> dateEditor.setForeground(Color.WHITE));
			}
		});
		dateEditor.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) { dateEditor.setForeground(Color.WHITE); }
			@Override
			public void focusLost(FocusEvent e) { dateEditor.setForeground(Color.WHITE); }
		});


		pNgay.add(dateChooserLocNgay);

		dateChooserLocNgay.addPropertyChangeListener("date", evt -> {
			locDuLieuBan();
		});
		pFilterRow.add(pNgay);

		pCenter.add(pButtonGrid, BorderLayout.CENTER);
		pCenter.add(pFilterRow, BorderLayout.SOUTH);

		pContent.add(pCenter, BorderLayout.CENTER);

		JPanel pEast = new JPanel(new GridLayout(2, 1, 0, 15));
		pEast.setBackground(MAU_NEN_TAB);
		pEast.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));

		JButton btnLamMoi = createStyledButton("Làm mới", new Color(30, 144, 255), MAU_NEN_ITEM.brighter());
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnLamMoi.addActionListener(e -> lamMoiGiaoDien());

		btnCheDoChonNhieu = new JToggleButton("Chọn nhiều") {
			@Override
			protected void paintComponent(Graphics g) {
				if (isSelected()) {
					g.setColor(new Color(34, 197, 94));
				} else {
					g.setColor(new Color(100, 100, 100));
				}
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		btnCheDoChonNhieu.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnCheDoChonNhieu.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnCheDoChonNhieu.setContentAreaFilled(false);
		btnCheDoChonNhieu.setFocusPainted(false);
		btnCheDoChonNhieu.setBorderPainted(false);
		btnCheDoChonNhieu.setBorder(null);
		btnCheDoChonNhieu.setBackground(new Color(100, 100, 100));
		btnCheDoChonNhieu.setForeground(Color.WHITE);

		btnCheDoChonNhieu.addActionListener(e -> {
			isMultiSelectMode = btnCheDoChonNhieu.isSelected();

			if (!isMultiSelectMode) {
				danhSachBanDangChon.clear();
			}

			banDangChon = null;
			panelLuoiBanAn.repaint();
			capNhatTrangThaiCacNutChucNang();
		});

		pEast.add(btnLamMoi);
		pEast.add(btnCheDoChonNhieu);

		pContent.add(pEast, BorderLayout.EAST);
		return pContent;
	}

	private JPanel taoSearchPanel() {
		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.setBackground(new Color(45, 47, 62));
		JPanel searchContainer = new JPanel(new BorderLayout());
		searchContainer.setBackground(new Color(48, 52, 56));
		searchContainer.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(70, 72, 87), 1), new EmptyBorder(8, 15, 8, 10)));

		ImageIcon searchIcon = new ImageIcon(getClass().getResource("/IMG/search.png"));
		Image searchImage = searchIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		JLabel searchLabel = new JLabel(new ImageIcon(searchImage));
		searchLabel.setBorder(new EmptyBorder(0, 0, 0, 13));

		final String placeholder = "Nhập mã bàn hoặc tên bàn";
		searchField = new JTextField(placeholder);
		searchField.setBackground(new Color(48, 52, 56));
		searchField.setForeground(new Color(150, 150, 160));
		searchField.setCaretColor(Color.WHITE);
		searchField.setBorder(null);
		searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		searchField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (searchField.getText().equals(placeholder)) {
					searchField.setText("");
					searchField.setForeground(MAU_CHU_CHUNG);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (searchField.getText().isEmpty()) {
					searchField.setForeground(new Color(150, 150, 160));
					searchField.setText(placeholder);
				}
			}
		});

		searchContainer.add(searchLabel, BorderLayout.WEST);
		searchContainer.add(searchField, BorderLayout.CENTER);

		JButton btnSearch = createStyledButton("Tìm", new Color(124, 124, 124), new Color(90, 92, 110));
		btnSearch.setPreferredSize(new Dimension(80, 50));

		ActionListener searchAction = e -> {
			String tuKhoa = searchField.getText().trim();
			final String placeholderBan = "Nhập mã bàn hoặc tên bàn";
			cmbTang.setSelectedIndex(0);
			cmbKhu.setSelectedIndex(0);
			cmbLoaiBan.setSelectedIndex(0);

			final String placeholderKH = "Nhập SĐT hoặc tên KH";
			if (searchFieldKhachHang != null && !searchFieldKhachHang.getText().equals(placeholderKH)) {
				searchFieldKhachHang.setForeground(new Color(150, 150, 160));
				searchFieldKhachHang.setText(placeholderKH);
			}

			try {
				if (tuKhoa.isEmpty() || tuKhoa.equals(placeholderBan)) {
					capNhatLuoiBan(banAn_DAO.docDanhSachBan());
				} else {
					capNhatLuoiBan(banAn_DAO.timKiemBan(tuKhoa));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			capNhatTrangThaiCacNutChucNang();
		};

		btnSearch.addActionListener(searchAction);
		searchField.addActionListener(searchAction);

		searchPanel.add(searchContainer, BorderLayout.CENTER);
		searchPanel.add(btnSearch, BorderLayout.EAST);

		return searchPanel;
	}

	private JPanel taoSearchPanelKhachHang() {
		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.setBackground(new Color(45, 47, 62));
		JPanel searchContainer = new JPanel(new BorderLayout());
		searchContainer.setBackground(new Color(48, 52, 56));
		searchContainer.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(70, 72, 87), 1), new EmptyBorder(8, 15, 8, 10)));

		ImageIcon searchIcon = new ImageIcon(getClass().getResource("/IMG/search.png"));
		Image searchImage = searchIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		JLabel searchLabel = new JLabel(new ImageIcon(searchImage));
		searchLabel.setBorder(new EmptyBorder(0, 0, 0, 13));

		final String placeholder = "Nhập SĐT hoặc tên KH";

		searchFieldKhachHang = new JTextField(placeholder);
		searchFieldKhachHang.setBackground(new Color(48, 52, 56));
		searchFieldKhachHang.setForeground(new Color(150, 150, 160));
		searchFieldKhachHang.setCaretColor(Color.WHITE);
		searchFieldKhachHang.setBorder(null);
		searchFieldKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		searchFieldKhachHang.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (searchFieldKhachHang.getText().equals(placeholder)) {
					searchFieldKhachHang.setText("");
					searchFieldKhachHang.setForeground(MAU_CHU_CHUNG);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (searchFieldKhachHang.getText().isEmpty()) {
					searchFieldKhachHang.setForeground(new Color(150, 150, 160));
					searchFieldKhachHang.setText(placeholder);
				}
			}
		});

		searchContainer.add(searchLabel, BorderLayout.WEST);
		searchContainer.add(searchFieldKhachHang, BorderLayout.CENTER);

		JButton btnSearch = createStyledButton("Tìm", new Color(124, 124, 124), new Color(90, 92, 110));
		btnSearch.setPreferredSize(new Dimension(80, 50));

		ActionListener searchAction = e -> {
			xuLyTimKiemKhachHang();
		};

		btnSearch.addActionListener(searchAction);
		searchFieldKhachHang.addActionListener(searchAction);

		searchPanel.add(searchContainer, BorderLayout.CENTER);
		searchPanel.add(btnSearch, BorderLayout.EAST);

		return searchPanel;
	}

	private BasicComboBoxUI createCustomComboBoxUI() {
		return new BasicComboBoxUI() {
			@Override
			protected JButton createArrowButton() {
				ImageIcon icon = new ImageIcon(getClass().getResource("/IMG/muitenxuong_32px.png"));
				Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
				JButton button = new JButton(new ImageIcon(img));
				button.setBackground(MAU_THANH_TIM_KIEM);
				button.setOpaque(true);
				button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
				return button;
			}
		};
	}

	private JPanel taoComboBoxTang() {
		JPanel pComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		pComboBox.setBackground(MAU_NEN_TAB);

		JLabel lblTang = new JLabel("Tầng ");
		lblTang.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblTang.setForeground(Color.white);
		pComboBox.add(lblTang);

		try {
			List<String> dsTang = banAn_DAO.docDanhSachTenTang();
			this.cmbTang = new JComboBox<>(dsTang.toArray(new String[0]));
		} catch (Exception e) {
			this.cmbTang = new JComboBox<>(new String[]{"Tất cả"});
		}

		cmbTang.setFont(new Font("Segoe UI", Font.BOLD, 14));
		cmbTang.setBackground(MAU_THANH_TIM_KIEM);
		cmbTang.setForeground(MAU_CHU_CHUNG);
		cmbTang.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cmbTang.setFocusable(false);
		cmbTang.setUI(createCustomComboBoxUI());
		cmbTang.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(70, 72, 87), 1),
				new EmptyBorder(0, 15, 0, 5)));
		cmbTang.setPreferredSize(new Dimension(120, 40));

		cmbTang.addActionListener(e -> {
			capNhatDanhSachKhu();
			locDuLieuBan();
			this.requestFocusInWindow();
		});

		pComboBox.add(cmbTang);
		return pComboBox;
	}

	private JPanel taoComboBoxKhu() {
		JPanel pComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		pComboBox.setBackground(MAU_NEN_TAB);

		JLabel lblKhu = new JLabel("Khu ");
		lblKhu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblKhu.setForeground(Color.white);
		pComboBox.add(lblKhu);

		this.cmbKhu = new JComboBox<>(new String[] { "Tất cả" });

		cmbKhu.setFont(new Font("Segoe UI", Font.BOLD, 14));
		cmbKhu.setBackground(MAU_THANH_TIM_KIEM);
		cmbKhu.setForeground(MAU_CHU_CHUNG);
		cmbKhu.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cmbKhu.setFocusable(false);
		cmbKhu.setUI(createCustomComboBoxUI());
		cmbKhu.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(70, 72, 87), 1),
				new EmptyBorder(0, 15, 0, 5)));
		cmbKhu.setPreferredSize(new Dimension(120, 40));

		cmbKhu.addActionListener(e -> {
			locDuLieuBan();
			this.requestFocusInWindow();
		});

		pComboBox.add(cmbKhu);
		return pComboBox;
	}

	private JPanel taoComboBoxLoaiBan() {
		JPanel pComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		pComboBox.setBackground(MAU_NEN_TAB);

		JLabel lblLoaiBan = new JLabel("Loại bàn ");
		lblLoaiBan.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblLoaiBan.setForeground(Color.white);
		pComboBox.add(lblLoaiBan);

		this.cmbLoaiBan = new JComboBox<>(new String[] { "Tất cả", "Bàn nhỏ", "Bàn vừa", "Bàn lớn", "Phòng VIP" });
		cmbLoaiBan.setFont(new Font("Segoe UI", Font.BOLD, 14));
		cmbLoaiBan.setBackground(MAU_THANH_TIM_KIEM);
		cmbLoaiBan.setForeground(MAU_CHU_CHUNG);
		cmbLoaiBan.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cmbLoaiBan.setFocusable(false);
		cmbLoaiBan.setUI(createCustomComboBoxUI());

		cmbLoaiBan.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(70, 72, 87), 1), new EmptyBorder(0, 15, 0, 5)));

		cmbLoaiBan.setPreferredSize(new Dimension(120, 40));

		cmbLoaiBan.addActionListener(e -> {
			locDuLieuBan();
			this.requestFocusInWindow();
		});

		pComboBox.add(cmbLoaiBan);
		return pComboBox;
	}

	private void capNhatDanhSachKhu() {
		String tangChon = "Tất cả";
		if (cmbTang != null) {
			tangChon = (String) cmbTang.getSelectedItem();
		}
		try {
			List<String> dsKhu = banAn_DAO.docDanhSachTenKhuTheoTang(tangChon);
			if (cmbKhu != null) {
				cmbKhu.setModel(new DefaultComboBoxModel<>(dsKhu.toArray(new String[0])));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void locDuLieuBan() {
		try {
			phieuDatBanDAO.huyPhieuDatQuaGio(30);
			String tang = (String) cmbTang.getSelectedItem();
			String khu = (String) cmbKhu.getSelectedItem();
			String loaiBan = (String) cmbLoaiBan.getSelectedItem();

			List<BanAn> dsBanGoc = banAn_DAO.locBanAn(tang, khu, loaiBan);

			java.util.Map<String, BanAn> mapBanDuyNhat = new java.util.LinkedHashMap<>();
			for (BanAn ban : dsBanGoc) {
				if (!mapBanDuyNhat.containsKey(ban.getMaBan())) {
					mapBanDuyNhat.put(ban.getMaBan(), ban);
				}
			}
			List<BanAn> dsBanDaXuLy = new ArrayList<>(mapBanDuyNhat.values());

			Date ngayChon = dateChooserLocNgay.getDate();
			if (ngayChon == null) {
				ngayChon = new Date();
			}

			Date homNay = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			boolean isHomNay = sdf.format(ngayChon).equals(sdf.format(homNay));
			boolean isTuongLai = ngayChon.after(homNay) && !isHomNay;
			boolean isQuasKhu = ngayChon.before(homNay) && !isHomNay;

			java.util.Map<String, String> mapDatTruoc = phieuDatBanDAO.layThongTinBanDatVaTenKhach(ngayChon);
			List<BanAn> dsHienThi = new ArrayList<>();

			for (BanAn ban : dsBanDaXuLy) {
				ban.setTenKhachHang("");
				String trangThaiGoc = ban.getTrangThai();

				if (isTuongLai || isQuasKhu) {
					ban.setTrangThai("Bàn đang trống");

					if (mapDatTruoc.containsKey(ban.getMaBan())) {
						ban.setTrangThai("Bàn đang chờ");
						ban.setTenKhachHang(mapDatTruoc.get(ban.getMaBan()));
					}
				} else {
					if (trangThaiGoc.equals("Bàn đang phục vụ")) {
						String tenKhach = hoaDon_DAO.getTenKhachHangTheoBan(ban.getMaBan());
						ban.setTenKhachHang(tenKhach != null ? tenKhach : "Khách lẻ");
						ban.setTrangThai("Bàn đang phục vụ");
					} else if (trangThaiGoc.equals("Bàn đang chờ") || mapDatTruoc.containsKey(ban.getMaBan())) {
						ban.setTrangThai("Bàn đang chờ");
						ban.setTenKhachHang(mapDatTruoc.getOrDefault(ban.getMaBan(), "Khách đặt trước"));
					} else {
						ban.setTrangThai("Bàn đang trống");
					}
				}
				dsHienThi.add(ban);
			}

			this.danhSachBanTongCuaNgay = dsHienThi;

			capNhatSoLuongBan();

			capNhatLuoiBan(this.danhSachBanTongCuaNgay);
			capNhatTrangThaiCacNutChucNang();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		button.setForeground(Color.WHITE);
		button.setBackground(bgColor);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBorder(new EmptyBorder(5, 20, 5, 20));

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (button.isEnabled()) {
					button.setBackground(hoverColor);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (button.isEnabled()) {
					button.setBackground(bgColor);
				}
			}
		});

		return button;
	}

	private class WrapFlowPanel extends JPanel implements Scrollable {
		public WrapFlowPanel() {
		}

		@Override
		public Dimension getPreferredSize() {
			int parentWidth = getParent().getWidth();
			if (parentWidth == 0) {
				return super.getPreferredSize();
			}
			int hgap = ((FlowLayout) getLayout()).getHgap();
			int vgap = ((FlowLayout) getLayout()).getVgap();
			int availableWidth = parentWidth - hgap;
			int itemsPerRow = Math.max(1, availableWidth / (KICH_THUOC_ITEM_RONG + hgap));
			int componentCount = getComponentCount();
			if (componentCount == 0) {
				return new Dimension(parentWidth, 0);
			}
			int rowCount = (int) Math.ceil((double) componentCount / itemsPerRow);
			int totalHeight = (rowCount * KICH_THUOC_ITEM_CAO) + ((rowCount - 1) * vgap) + vgap * 2;
			return new Dimension(parentWidth, totalHeight);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 16;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 16;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
	}

	private String layDuongDanAnhBan(BanAn ban) {
		String loaiBan = ban.getLoaiBan();
		String trangThai = ban.getTrangThai();

		if (loaiBan.equals("Phòng VIP")) {
			if (trangThai.equals("Bàn đang chờ")) {
				return "/IMG/banVang_vip.png";
			} else if (trangThai.equals("Bàn đang trống")) {
				return "/IMG/banXam_vip.png";
			} else {
				return "/IMG/banDo_vip.png";
			}
		} else {
			if (trangThai.equals("Bàn đang chờ")) {
				return "/IMG/banVang_thuong.png";
			} else if (trangThai.equals("Bàn đang trống")) {
				return "/IMG/banXam_thuong.png";
			} else {
				return "/IMG/banDo_thuong.png";
			}
		}
	}

	private void capNhatLuoiBan(List<BanAn> danhSachBanMoi) {
		this.danhSachBan = danhSachBanMoi;

		this.danhSachBan.sort((b1, b2) -> {
			boolean isVip1 = b1.getLoaiBan().equals("Phòng VIP");
			boolean isVip2 = b2.getLoaiBan().equals("Phòng VIP");
			if (isVip1 != isVip2) {
				return isVip1 ? -1 : 1;
			}
			return b1.getTenBan().compareTo(b2.getTenBan());
		});

		if (!isMultiSelectMode) {
			banDangChon = null;
		}

		panelLuoiBanAn.removeAll();
		mapBanUI.clear();

		for (BanAn ban : danhSachBan) {
			JPanel panelBan = taoItemsBan(ban);
			panelLuoiBanAn.add(panelBan);
			mapBanUI.put(ban.getMaBan(), panelBan);
		}

		capNhatSoLuongBan();
		panelLuoiBanAn.revalidate();
		panelLuoiBanAn.repaint();
	}

	private boolean kiemTraBanDaChon(String maBan) {
		for (BanAn b : danhSachBanDangChon) {
			if (b.getMaBan().equals(maBan)) {
				return true;
			}
		}
		return false;
	}

	private void capNhatGiaoDienMotBan(JPanel panelItem, BanAn banMoi) {
		try {
			JPanel backgroundPanel = (JPanel) panelItem.getComponent(0);
			JLabel lblHinhAnh = (JLabel) backgroundPanel.getComponent(0);

			String imagePath = layDuongDanAnhBan(banMoi);
			ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
			Image img = icon.getImage().getScaledInstance(241 - 15, 180, Image.SCALE_SMOOTH);
			lblHinhAnh.setIcon(new ImageIcon(img));

			JPanel panelThongTin = (JPanel) backgroundPanel.getComponent(1);
			JLabel lblKhachHang = (JLabel) panelThongTin.getComponent(4);

			String tenKhach = banMoi.getTenKhachHang();
			if (tenKhach == null || tenKhach.isEmpty()) tenKhach = " ";

			String hienThiKhach = " ";
			String trangThai = banMoi.getTrangThai();
			if (trangThai.equals("Bàn đang chờ") || trangThai.equals("Bàn đang phục vụ")) {
				if (tenKhach != null && !tenKhach.isEmpty()) {
					if (tenKhach.length() > 20) tenKhach = tenKhach.substring(0, 17) + "...";
					hienThiKhach = tenKhach;
				} else {
					hienThiKhach = "KH: Khách lẻ";
				}
			}
			lblKhachHang.setText(hienThiKhach);
			panelItem.repaint();

		} catch (Exception e) {
			System.err.println("Lỗi cập nhật UI bàn: " + e.getMessage());
		}
	}

	private void tuDongQuetTrangThai() {
		try {
			phieuDatBanDAO.huyPhieuDatQuaGio(30);

			List<BanAn> duLieuMoiDB = banAn_DAO.docDanhSachBan();

			Date ngayChon = dateChooserLocNgay.getDate();
			if (ngayChon == null) ngayChon = new Date();
			java.util.Map<String, String> mapDatTruoc = phieuDatBanDAO.layThongTinBanDatVaTenKhach(ngayChon);

			Date homNay = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			boolean isHomNay = sdf.format(ngayChon).equals(sdf.format(homNay));
			boolean isTuongLai = ngayChon.after(homNay) && !isHomNay;
			boolean isQuasKhu = ngayChon.before(homNay) && !isHomNay;

			List<BanAn> listMoiToanBo = new ArrayList<>();

			for (BanAn banMoi : duLieuMoiDB) {
				String trangThaiGoc = banMoi.getTrangThai();
				banMoi.setTenKhachHang("");

				if (isTuongLai || isQuasKhu) {
					banMoi.setTrangThai("Bàn đang trống");
					if (mapDatTruoc.containsKey(banMoi.getMaBan())) {
						banMoi.setTrangThai("Bàn đang chờ");
						banMoi.setTenKhachHang(mapDatTruoc.get(banMoi.getMaBan()));
					}
				} else {
					if (trangThaiGoc.equals("Bàn đang phục vụ")) {
						String tenKhach = hoaDon_DAO.getTenKhachHangTheoBan(banMoi.getMaBan());
						banMoi.setTenKhachHang(tenKhach != null && !tenKhach.isEmpty() ? tenKhach : "Khách lẻ");
					} else if (trangThaiGoc.equals("Bàn đang chờ") || (trangThaiGoc.equals("Bàn đang trống") && mapDatTruoc.containsKey(banMoi.getMaBan()))) {
						banMoi.setTrangThai("Bàn đang chờ");
						banMoi.setTenKhachHang(mapDatTruoc.getOrDefault(banMoi.getMaBan(), "Khách đặt trước"));
					}
				}

				listMoiToanBo.add(banMoi);

				if (mapBanUI.containsKey(banMoi.getMaBan())) {
					JPanel panelUI = mapBanUI.get(banMoi.getMaBan());
					capNhatGiaoDienMotBan(panelUI, banMoi);

					for(BanAn bCu : danhSachBan) {
						if(bCu.getMaBan().equals(banMoi.getMaBan())) {
							bCu.setTrangThai(banMoi.getTrangThai());
							bCu.setTenKhachHang(banMoi.getTenKhachHang());
							break;
						}
					}
				}
			}

			this.danhSachBanTongCuaNgay = listMoiToanBo;

			capNhatSoLuongBan();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void capNhatSoLuongBan() {
		if (this.danhSachBanTongCuaNgay == null) return;

		int trong = 0;
		int cho = 0;
		int phucVu = 0;

		for (BanAn ban : this.danhSachBanTongCuaNgay) {
			String trangThai = ban.getTrangThai();
			if ("Bàn đang trống".equalsIgnoreCase(trangThai)) {
				trong++;
			} else if ("Bàn đang chờ".equalsIgnoreCase(trangThai)) {
				cho++;
			} else if ("Bàn đang phục vụ".equalsIgnoreCase(trangThai)) {
				phucVu++;
			}
		}

		if (btnTrong != null)
			btnTrong.setText(String.format("Bàn đang trống (%d)", trong));
		if (btnCho != null)
			btnCho.setText(String.format("Bàn đang chờ (%d)", cho));
		if (btnPhucVu != null)
			btnPhucVu.setText(String.format("Bàn đang phục vụ (%d)", phucVu));

		this.soBanTrong = trong;
		this.soBanCho = cho;
		this.soBanPhucVu = phucVu;
	}

	private void lamMoiGiaoDien() {
		locDuLieuBan();

		final String placeholder = "Nhập mã bàn hoặc tên bàn";
		searchField.setForeground(new Color(150, 150, 160));
		searchField.setText(placeholder);

		final String placeholderKH = "Nhập SĐT hoặc tên KH";
		searchFieldKhachHang.setForeground(new Color(150, 150, 160));
		searchFieldKhachHang.setText(placeholderKH);

		if (cmbTang != null) {
			cmbTang.setSelectedIndex(0);
		}
		if (cmbKhu != null) {
			capNhatDanhSachKhu();
			cmbKhu.setSelectedIndex(0);
		}
		if (cmbLoaiBan != null) {
			cmbLoaiBan.setSelectedIndex(0);
		}

		if (btnCheDoChonNhieu != null) {
			btnCheDoChonNhieu.setSelected(false);
			isMultiSelectMode = false;
			btnCheDoChonNhieu.setBackground(new Color(100, 100, 100));
		}
		danhSachBanDangChon.clear();
		banDangChon = null;

		this.requestFocusInWindow();
		capNhatTrangThaiCacNutChucNang();
	}

	private void xuLyTimKiemKhachHang() {
		final String placeholder = "Nhập SĐT hoặc tên KH";
		String tuKhoa = searchFieldKhachHang.getText().trim();

		if (tuKhoa.isEmpty() || tuKhoa.equals(placeholder)) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập SĐT hoặc tên khách hàng cần tìm!");
			return;
		}

		try {
			List<BanAn> dsTimDuoc = banAn_DAO.timKiemBanTheoKhachHang(tuKhoa);

			if (dsTimDuoc.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng thỏa mãn!");
			} else {
				capNhatLuoiBan(dsTimDuoc);

				banDangChon = null;
				danhSachBanDangChon.clear();
				if (btnCheDoChonNhieu != null) {
					btnCheDoChonNhieu.setSelected(false);
					isMultiSelectMode = false;
				}
				capNhatTrangThaiCacNutChucNang();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void xuLyDatBanNgay() {
		Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);

		if (isMultiSelectMode && !danhSachBanDangChon.isEmpty()) {
			DatBanNgay_UI dialog = new DatBanNgay_UI(parentFrame, danhSachBanDangChon);
			dialog.setVisible(true);
		}
		else if (banDangChon != null) {
			List<BanAn> dsBanDon = new ArrayList<>();
			dsBanDon.add(banDangChon);

			DatBanNgay_UI dialog = new DatBanNgay_UI(parentFrame, dsBanDon);
			dialog.setVisible(true);
		}
		else {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để đặt!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		lamMoiGiaoDien();
	}

	private void xuLyDatBanCho() {
		Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);

		Date ngayHienTai = dateChooserLocNgay.getDate();
		if (ngayHienTai == null) {
			ngayHienTai = new Date();
		}

		if (isMultiSelectMode && !danhSachBanDangChon.isEmpty()) {
			DatBanCho_UI dialog = new DatBanCho_UI(parentFrame, danhSachBanDangChon, ngayHienTai);
			dialog.setVisible(true);
		}
		else if (banDangChon != null) {
			List<BanAn> dsBanDon = new ArrayList<>();
			dsBanDon.add(banDangChon);
			DatBanCho_UI dialog = new DatBanCho_UI(parentFrame, dsBanDon, ngayHienTai);
			dialog.setVisible(true);
		}
		else {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một bàn để đặt!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		lamMoiGiaoDien();
	}

	private void xuLyChuyenBan() {
		List<BanAn> dsBanCanXuLy = new ArrayList<>();

		if (banDangChon != null) {
			dsBanCanXuLy.add(banDangChon);
		}
		else if (!danhSachBanDangChon.isEmpty()) {
			dsBanCanXuLy.addAll(danhSachBanDangChon);
		}

		if (dsBanCanXuLy.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần chuyển!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		BanAn banGoc = null;

		if (dsBanCanXuLy.size() == 1) {
			banGoc = dsBanCanXuLy.get(0);
		} else {
			BanAn[] options = dsBanCanXuLy.toArray(new BanAn[0]);

			banGoc = (BanAn) JOptionPane.showInputDialog(
					this,
					"Bạn đang chọn một nhóm bàn.\nVui lòng chọn cụ thể bàn nào bạn muốn chuyển đi:",
					"Chọn bàn cần chuyển",
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]
			);

			if (banGoc == null) return;
		}

		String trangThai = banGoc.getTrangThai();
		if (trangThai.equals("Bàn đang trống")) {
			JOptionPane.showMessageDialog(this, "Không thể chuyển bàn đang trống!", "Lỗi thao tác", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
		Date ngayChon = dateChooserLocNgay.getDate();
		if (ngayChon == null) ngayChon = new Date();

		ChuyenBan_UI dialog = new ChuyenBan_UI(parentFrame, banGoc, ngayChon);
		dialog.setVisible(true);

		lamMoiGiaoDien();
	}

	private void xuLyHuyBanCho() {
		List<BanAn> dsBanCanHuy = new ArrayList<>();
		if (banDangChon != null) {
			dsBanCanHuy.add(banDangChon);
		} else if (!danhSachBanDangChon.isEmpty()) {
			dsBanCanHuy.addAll(danhSachBanDangChon);
		}

		if (dsBanCanHuy.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để hủy đặt!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		NhanVien nvHienTai = Auth.getCurrentNhanVien();
		if (nvHienTai == null) {
			JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy thông tin nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Date ngayChon = dateChooserLocNgay.getDate();
		if (ngayChon == null) ngayChon = new Date();
		Date homNay = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		boolean isHomNay = sdf.format(ngayChon).equals(sdf.format(homNay));

		try {
			java.util.Map<String, List<BanAn>> mapPhieuToBanHuy = new java.util.HashMap<>();

			for (BanAn ban : dsBanCanHuy) {
				String maPhieu = phieuDatBanDAO.timMaPhieuDatDangChoTheoBan(ban.getMaBan(), ngayChon);
				if (maPhieu != null) {
					mapPhieuToBanHuy.computeIfAbsent(maPhieu, k -> new ArrayList<>()).add(ban);
				}
			}

			if (mapPhieuToBanHuy.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt bàn phù hợp để hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
				return;
			}

			StringBuilder sbTenBan = new StringBuilder();
			for (BanAn b : dsBanCanHuy) sbTenBan.append(b.getTenBan()).append(", ");
			String strTenBan = sbTenBan.length() > 2 ? sbTenBan.substring(0, sbTenBan.length()-2) : sbTenBan.toString();

			String lyDoHuy = JOptionPane.showInputDialog(this,
					"Xác nhận hủy đặt bàn cho: " + strTenBan + "\nNhập lý do hủy:",
					"Xác nhận hủy",
					JOptionPane.QUESTION_MESSAGE);

			int soLuongThanhCong = 0;

			for (java.util.Map.Entry<String, List<BanAn>> entry : mapPhieuToBanHuy.entrySet()) {
				String maPhieu = entry.getKey();
				List<BanAn> dsBanHuyThuocPhieu = entry.getValue();

				int tongSoBanCuaPhieu = phieuDatBan_BanDAO.getDanhSachBanTheoPhieu(maPhieu).size();
				int soBanMuonHuy = dsBanHuyThuocPhieu.size();

				String tenKhach = "N/A";
				String sdtKhach = "N/A";
				PhieuDatBan phieuInfo = phieuDatBanDAO.getPhieuDatBanTheoMa(maPhieu);
				if (phieuInfo != null) {
					List<KhachHang> khs = khachHang_DAO.timKiemTheoMa(phieuInfo.getMaKhachHang());
					if (!khs.isEmpty()) {
						tenKhach = khs.get(0).getHoTen();
						sdtKhach = khs.get(0).getSoDienThoai();
					}
				}

				StringBuilder sbTenBanLog = new StringBuilder();
				for (BanAn b : dsBanHuyThuocPhieu) sbTenBanLog.append(b.getTenBan()).append(", ");
				String strTenBanLog = sbTenBanLog.length() > 2 ? sbTenBanLog.substring(0, sbTenBanLog.length()-2) : sbTenBanLog.toString();

				if (soBanMuonHuy >= tongSoBanCuaPhieu) {
					boolean kq = phieuDatBanDAO.capNhatTrangThai(maPhieu, "Đã hủy");
					if (kq) {
						if (isHomNay) {
							List<String> allBan = phieuDatBanDAO.layDanhSachMaBanTheoPhieuDat(maPhieu);
							for (String b : allBan) {
								banAn_DAO.capNhatTrangThaiBan(b, "Bàn đang trống");
							}
						}

						entity.LichSuHuyDatBan log = new entity.LichSuHuyDatBan(
								maPhieu, strTenBanLog + " (Toàn bộ)", tenKhach, sdtKhach,
								nvHienTai.getMaNhanVien(), nvHienTai.getHoTen(), lyDoHuy
						);
						lichSuHuyDatBanDAO.ghiLogHuyDatBan(log);
						soLuongThanhCong++;
					}
				}
				else {
					boolean coLoiLe = false;
					for (BanAn b : dsBanHuyThuocPhieu) {
						boolean xoaLienKet = phieuDatBan_BanDAO.xoaBanKhoiPhieu(maPhieu, b.getMaBan());
						if (xoaLienKet) {
							if (isHomNay) {
								banAn_DAO.capNhatTrangThaiBan(b.getMaBan(), "Bàn đang trống");
							}
						} else {
							coLoiLe = true;
						}
					}

					if (!coLoiLe) {
						entity.LichSuHuyDatBan log = new entity.LichSuHuyDatBan(
								maPhieu, strTenBanLog + " (Rút bớt)", tenKhach, sdtKhach,
								nvHienTai.getMaNhanVien(), nvHienTai.getHoTen(), lyDoHuy
						);
						lichSuHuyDatBanDAO.ghiLogHuyDatBan(log);
						soLuongThanhCong++;
					}
				}
			}

			if (soLuongThanhCong > 0) {
				JOptionPane.showMessageDialog(this, "Xử lý hủy bàn thành công!");
				lamMoiGiaoDien();
			} else {
				JOptionPane.showMessageDialog(this, "Có lỗi xảy ra trong quá trình hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void xuLyNhanBanCho() {
		BanAn banDaiDien = banDangChon;

		if (banDaiDien == null && !danhSachBanDangChon.isEmpty()) {
			banDaiDien = danhSachBanDangChon.get(0);
		}

		if (banDaiDien == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để nhận!");
			return;
		}

		Date ngayChon = dateChooserLocNgay.getDate();
		if (ngayChon == null) ngayChon = new Date();

		try {
			String maPhieu = phieuDatBanDAO.timMaPhieuDatDangChoTheoBan(banDaiDien.getMaBan(), ngayChon);

			if (maPhieu == null) {
				JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy phiếu đặt bàn đang chờ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			List<String> dsMaBanThuocPhieu = phieuDatBanDAO.layDanhSachMaBanTheoPhieuDat(maPhieu);

			PhieuDatBan phieu = phieuDatBanDAO.getPhieuDatBanTheoMa(maPhieu);
			if (phieu == null) {
				JOptionPane.showMessageDialog(this, "Lỗi: Không lấy được thông tin phiếu đặt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			StringBuilder sbTenBan = new StringBuilder();
			for (BanAn b : danhSachBan) {
				if (dsMaBanThuocPhieu.contains(b.getMaBan())) {
					sbTenBan.append(b.getTenBan()).append(", ");
				}
			}
			String strTenBan = sbTenBan.length() > 2 ? sbTenBan.substring(0, sbTenBan.length()-2) : "các bàn đã đặt";

			int xacNhan = JOptionPane.showConfirmDialog(this,
					"Xác nhận nhận bàn cho nhóm: " + strTenBan + "?\n(Hệ thống sẽ chuyển tất cả bàn trong nhóm này sang trạng thái Phục vụ)",
					"Xác nhận nhận bàn", JOptionPane.YES_NO_OPTION);

			if (xacNhan != JOptionPane.YES_OPTION) return;

			NhanVien nvHienTai = Auth.getCurrentNhanVien();
			if (nvHienTai == null) {
				JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			String maHD = hoaDon_DAO.sinhMaHoaDonTuDong();
			HoaDon hoaDonMoi = new HoaDon(
					maHD,
					"Chưa thanh toán",
					new Date(),
					BigDecimal.ZERO,
					nvHienTai.getMaNhanVien(),
					maPhieu,
					phieu.getMaKhachHang(),
					null,
					null,
					BigDecimal.valueOf(phieu.getTienDatCoc()),
					BigDecimal.ZERO,
					BigDecimal.ZERO
			);

			if (hoaDon_DAO.themHoaDon(hoaDonMoi)) {

				for (String maBan : dsMaBanThuocPhieu) {
					hoaDonBanDAO.themHoaDon_Ban(maHD, maBan);
					banAn_DAO.capNhatTrangThaiBan(maBan, "Bàn đang phục vụ");
				}

				List<ChiTietPhieuDatBan> listMonDaDat = chiTietPhieuDatBanDAO.getChiTietTheoPhieu(maPhieu);

				if (listMonDaDat != null && !listMonDaDat.isEmpty()) {
					for (ChiTietPhieuDatBan item : listMonDaDat) {
						ChiTietHoaDon ctHD = new ChiTietHoaDon(
								maHD,
								item.getMaMon(),
								item.getSoLuong(),
								item.getDonGia()
						);
						chiTietHoaDonDAO.themChiTietHoaDon(ctHD);
					}
				}

				phieuDatBanDAO.capNhatTrangThai(maPhieu, "Đã nhận");

				JOptionPane.showMessageDialog(this, "Nhận bàn thành công! Hóa đơn " + maHD + " đã được tạo.");
				lamMoiGiaoDien();

			} else {
				JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void xuLyDatMon() {
		List<BanAn> dsBanXuLy = new ArrayList<>();

		if (!danhSachBanDangChon.isEmpty()) {
			dsBanXuLy.addAll(danhSachBanDangChon);
		} else if (banDangChon != null) {
			dsBanXuLy.add(banDangChon);
		} else {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String trangThaiDau = dsBanXuLy.get(0).getTrangThai();
		for (BanAn b : dsBanXuLy) {
			if (!b.getTrangThai().equals(trangThaiDau)) {
				JOptionPane.showMessageDialog(this,
						"Các bàn đã chọn phải có cùng trạng thái (Cùng 'Đang chờ' hoặc cùng 'Đang phục vụ')!",
						"Lỗi logic", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);

		try {
			if (trangThaiDau.equalsIgnoreCase("Bàn đang phục vụ")) {
				HoaDon hoaDonChuan = hoaDon_DAO.timHoaDonChuaThanhToanTheoMaBan(dsBanXuLy.get(0).getMaBan());
				if (hoaDonChuan == null) {
					JOptionPane.showMessageDialog(this,
							"Lỗi: Không tìm thấy hóa đơn của bàn " + dsBanXuLy.get(0).getTenBan(), "Lỗi",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				for (int i = 1; i < dsBanXuLy.size(); i++) {
					HoaDon hdKhac = hoaDon_DAO.timHoaDonChuaThanhToanTheoMaBan(dsBanXuLy.get(i).getMaBan());
					if (hdKhac == null || !hdKhac.getMaHoaDon().equals(hoaDonChuan.getMaHoaDon())) {
						JOptionPane.showMessageDialog(this, "Không thể gọi món chung!\nBàn " + dsBanXuLy.get(i).getTenBan()
								+ " đang sử dụng hóa đơn khác.", "Lỗi gộp đơn", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				DatMonChoBan_UI dialog = new DatMonChoBan_UI(parentFrame, dsBanXuLy, hoaDonChuan);
				dialog.setVisible(true);
			}

			else if (trangThaiDau.equalsIgnoreCase("Bàn đang chờ")) {
				Date ngayChon = dateChooserLocNgay.getDate();
				if (ngayChon == null)
					ngayChon = new Date();

				String maPhieuChuan = phieuDatBanDAO.timMaPhieuDatDangChoTheoBan(dsBanXuLy.get(0).getMaBan(), ngayChon);
				if (maPhieuChuan == null) {
					JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt của bàn " + dsBanXuLy.get(0).getTenBan(),
							"Lỗi", JOptionPane.ERROR_MESSAGE);
					return;
				}

				for (int i = 1; i < dsBanXuLy.size(); i++) {
					String maPhieuKhac = phieuDatBanDAO.timMaPhieuDatDangChoTheoBan(dsBanXuLy.get(i).getMaBan(), ngayChon);
					if (!maPhieuChuan.equals(maPhieuKhac)) {
						JOptionPane.showMessageDialog(this, "Không thể đặt món chung!\nBàn " + dsBanXuLy.get(i).getTenBan()
								+ " thuộc phiếu đặt bàn khác.", "Lỗi logic", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				DatMonChoBan_UI dialog = new DatMonChoBan_UI(parentFrame, dsBanXuLy, maPhieuChuan);
				dialog.setVisible(true);
			}

			else {
				JOptionPane.showMessageDialog(this, "Chỉ có thể đặt món cho bàn Đang chờ hoặc Đang phục vụ!", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		lamMoiGiaoDien();
	}

	private void xuLyChiTiet() {
		BanAn banCanXem = (banDangChon != null) ? banDangChon
				: (danhSachBanDangChon.isEmpty() ? null : danhSachBanDangChon.get(0));
		if (banCanXem == null)
			return;

		if (isMultiSelectMode && danhSachBanDangChon.size() > 1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chỉ chọn 1 bàn để xem chi tiết!");
			return;
		}

		Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
		String trangThai = banCanXem.getTrangThai();

		try {
			if (trangThai.equalsIgnoreCase("Bàn đang phục vụ")) {
				HoaDon hoaDon = hoaDon_DAO.timHoaDonChuaThanhToanTheoMaBan(banCanXem.getMaBan());

				if (hoaDon == null) {
					JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn chưa thanh toán!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				ChiTietBan_UI dialog = new ChiTietBan_UI(parentFrame, banCanXem, hoaDon);
				dialog.setVisible(true);
			} else if (trangThai.equalsIgnoreCase("Bàn đang chờ")) {
				Date ngayChon = dateChooserLocNgay.getDate();
				if (ngayChon == null)
					ngayChon = new Date();
				String maPhieu = phieuDatBanDAO.timMaPhieuDatDangChoTheoBan(banCanXem.getMaBan(), ngayChon);

				if (maPhieu == null) {
					JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt chờ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
					return;
				}
				ChiTietBan_UI dialog = new ChiTietBan_UI(parentFrame, banCanXem, maPhieu);
				dialog.setVisible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		lamMoiGiaoDien();
	}

	private void xuLyTinhTien() {
		List<BanAn> dsBanDuocChon = new ArrayList<>();

		if (banDangChon != null) {
			dsBanDuocChon.add(banDangChon);
		} else if (!danhSachBanDangChon.isEmpty()) {
			dsBanDuocChon.addAll(danhSachBanDangChon);
		}

		if (dsBanDuocChon.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để tính tiền!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		for (BanAn b : dsBanDuocChon) {
			if (!b.getTrangThai().equals("Bàn đang phục vụ")) {
				JOptionPane.showMessageDialog(this,
						"Bàn " + b.getTenBan() + " không ở trạng thái phục vụ, không thể tính tiền!", "Lỗi trạng thái",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		try {
			String maHDChuan = null;

			HoaDon hdDau = hoaDon_DAO.timHoaDonChuaThanhToanTheoMaBan(dsBanDuocChon.get(0).getMaBan());
			if (hdDau == null) {
				JOptionPane.showMessageDialog(this,
						"Lỗi: Không tìm thấy hóa đơn cho bàn " + dsBanDuocChon.get(0).getTenBan());
				return;
			}
			maHDChuan = hdDau.getMaHoaDon();

			for (int i = 1; i < dsBanDuocChon.size(); i++) {
				HoaDon hdKhac = hoaDon_DAO.timHoaDonChuaThanhToanTheoMaBan(dsBanDuocChon.get(i).getMaBan());
				if (hdKhac == null || !hdKhac.getMaHoaDon().equals(maHDChuan)) {
					JOptionPane.showMessageDialog(this, "Không thể tính tiền gộp!\nBàn "
									+ dsBanDuocChon.get(i).getTenBan() + " thuộc hóa đơn khác.", "Lỗi khác hóa đơn",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			List<String> tatCaMaBanTrongNhom = hoaDonBanDAO.layDanhSachMaBanTheoHoaDon(maHDChuan);

			List<BanAn> dsBanThanhToanFull = new ArrayList<>();

			for (BanAn b : this.danhSachBan) {
				if (tatCaMaBanTrongNhom.contains(b.getMaBan())) {
					dsBanThanhToanFull.add(b);
				}
			}

			Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);

			if (dsBanThanhToanFull.size() > dsBanDuocChon.size()) {
				int confirm = JOptionPane.showConfirmDialog(this,
						"Bàn bạn chọn thuộc một nhóm " + dsBanThanhToanFull.size() + " bàn dùng chung hóa đơn.\n"
								+ "Hệ thống sẽ thực hiện thanh toán cho TOÀN BỘ nhóm bàn này.\nTiếp tục?",
						"Xác nhận thanh toán gộp", JOptionPane.YES_NO_OPTION);
				if (confirm != JOptionPane.YES_OPTION) return;
			}

			TinhTien_UI dialog = new TinhTien_UI(parentFrame, dsBanThanhToanFull);
			dialog.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
		lamMoiGiaoDien();
	}

	private void tuyChinhScrollBar(JScrollPane scrollPane) {
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		verticalScrollBar.setPreferredSize(new Dimension(8, 0));
		verticalScrollBar.setBackground(MAU_NEN_INPUT);
		verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(100, 105, 120);
				this.trackColor = MAU_NEN_INPUT;
				this.thumbDarkShadowColor = new Color(80, 85, 100);
				this.thumbHighlightColor = new Color(120, 125, 140);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}

			@Override
			protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
				if (thumbBounds.isEmpty() || !verticalScrollBar.isEnabled()) {
					return;
				}
				g.setColor(new Color(100, 105, 120));
				g.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 4, 4);
			}

			@Override
			protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
				g.setColor(MAU_NEN_INPUT);
				g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
			}
		});

		JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		horizontalScrollBar.setPreferredSize(new Dimension(0, 8));
		horizontalScrollBar.setBackground(MAU_NEN_INPUT);
		horizontalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(100, 105, 120);
				this.trackColor = MAU_NEN_INPUT;
				this.thumbDarkShadowColor = new Color(80, 85, 100);
				this.thumbHighlightColor = new Color(120, 125, 140);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}

			@Override
			protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
				if (thumbBounds.isEmpty() || !horizontalScrollBar.isEnabled()) {
					return;
				}
				g.setColor(new Color(100, 105, 120));
				g.fillRoundRect(thumbBounds.x, thumbBounds.y + 2, thumbBounds.width, thumbBounds.height - 4, 4, 4);
			}

			@Override
			protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
				g.setColor(MAU_NEN_INPUT);
				g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
			}
		});
	}

	private void setNutChucNang(JButton button, boolean enabled) {
		button.setEnabled(enabled);
		if (enabled) {
			button.setBackground(MAU_NUT_KICH_HOAT);
		} else {
			button.setBackground(MAU_NUT_VO_HIEU);
		}
	}

	private void capNhatTrangThaiCacNutChucNang() {
		Date ngayChon = dateChooserLocNgay.getDate();
		if (ngayChon == null)
			ngayChon = new Date();

		Date homNay = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		boolean isHomNay = sdf.format(ngayChon).equals(sdf.format(homNay));
		boolean isTuongLai = ngayChon.after(homNay) && !isHomNay;

		if (!danhSachBanDangChon.isEmpty()) {
			boolean tatCaTrong = true;
			boolean tatCaCho = true;
			boolean tatCaPhucVu = true;

			for (BanAn b : danhSachBanDangChon) {
				if (!b.getTrangThai().equals("Bàn đang trống"))
					tatCaTrong = false;
				if (!b.getTrangThai().equals("Bàn đang chờ"))
					tatCaCho = false;
				if (!b.getTrangThai().equals("Bàn đang phục vụ"))
					tatCaPhucVu = false;
			}

			tatCaCacNut();

			if (tatCaTrong) {
				if (!isTuongLai) {
					setNutChucNang(btnDatBanNgay, true);
				}
				setNutChucNang(btnDatBanCho, true);

			} else if (tatCaCho) {
				setNutChucNang(btnDatBanNgay, false);
				setNutChucNang(btnDatBanCho, false);

				if (isTuongLai) {
					setNutChucNang(btnNhanBanCho, false);
				} else {
					setNutChucNang(btnNhanBanCho, true);
				}

				setNutChucNang(btnHuyBanCho, true);
				setNutChucNang(btnXemChiTiet, true);
				setNutChucNang(btnChuyenBan, true);
				setNutChucNang(btnDatMon, false);
				setNutChucNang(btnTinhTien, false);

			} else if (tatCaPhucVu) {
				setNutChucNang(btnDatBanNgay, false);
				setNutChucNang(btnDatBanCho, false);
				setNutChucNang(btnNhanBanCho, false);
				setNutChucNang(btnHuyBanCho, false);
				setNutChucNang(btnXemChiTiet, true);
				setNutChucNang(btnChuyenBan, true);
				setNutChucNang(btnDatMon, true);
				setNutChucNang(btnTinhTien, true);
			}

			return;
		}

		if (banDangChon == null) {
			tatCaCacNut();
			return;
		}

		String trangThai = banDangChon.getTrangThai();

		switch (trangThai) {
			case "Bàn đang trống":
				if (isTuongLai) {
					setNutChucNang(btnDatBanNgay, false);
				} else {
					setNutChucNang(btnDatBanNgay, true);
				}
				setNutChucNang(btnDatBanCho, true);
				setNutChucNang(btnNhanBanCho, false);
				setNutChucNang(btnHuyBanCho, false);
				setNutChucNang(btnXemChiTiet, false);
				setNutChucNang(btnChuyenBan, false);
				setNutChucNang(btnDatMon, false);
				setNutChucNang(btnTinhTien, false);
				break;

			case "Bàn đang chờ":
				setNutChucNang(btnDatBanNgay, false);
				setNutChucNang(btnDatBanCho, false);
				if (isTuongLai) {
					setNutChucNang(btnNhanBanCho, false);
				} else {
					setNutChucNang(btnNhanBanCho, true);
				}
				setNutChucNang(btnHuyBanCho, true);
				setNutChucNang(btnXemChiTiet, true);
				setNutChucNang(btnChuyenBan, true);
				setNutChucNang(btnDatMon, false);
				setNutChucNang(btnTinhTien, false);
				break;

			case "Bàn đang phục vụ":
				setNutChucNang(btnDatBanNgay, false);
				setNutChucNang(btnDatBanCho, false);
				setNutChucNang(btnNhanBanCho, false);
				setNutChucNang(btnHuyBanCho, false);
				setNutChucNang(btnXemChiTiet, true);
				setNutChucNang(btnChuyenBan, true);
				setNutChucNang(btnDatMon, true);
				setNutChucNang(btnTinhTien, true);
				break;

			default:
				tatCaCacNut();
				break;
		}
	}

	private void tatCaCacNut() {
		setNutChucNang(btnDatBanNgay, false);
		setNutChucNang(btnDatBanCho, false);
		setNutChucNang(btnNhanBanCho, false);
		setNutChucNang(btnHuyBanCho, false);
		setNutChucNang(btnXemChiTiet, false);
		setNutChucNang(btnChuyenBan, false);
		setNutChucNang(btnDatMon, false);
		setNutChucNang(btnTinhTien, false);
	}

	private void khoiTaoKeyBindings() {
		InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = this.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke("F4"), "datBanNgayAction");
		actionMap.put("datBanNgayAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnDatBanNgay.isEnabled())
					xuLyDatBanNgay();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("F5"), "datBanChoAction");
		actionMap.put("datBanChoAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnDatBanCho.isEnabled())
					xuLyDatBanCho();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("F6"), "nhanBanChoAction");
		actionMap.put("nhanBanChoAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnNhanBanCho.isEnabled())
					xuLyNhanBanCho();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("F7"), "huyBanChoAction");
		actionMap.put("huyBanChoAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnHuyBanCho.isEnabled())
					xuLyHuyBanCho();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("F8"), "xemChiTietAction");
		actionMap.put("xemChiTietAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnXemChiTiet.isEnabled())
					xuLyChiTiet();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("F9"), "chuyenBanAction");
		actionMap.put("chuyenBanAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnChuyenBan.isEnabled())
					xuLyChuyenBan();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("F10"), "datMonAction");
		actionMap.put("datMonAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnDatMon.isEnabled())
					xuLyDatMon();
			}
		});

		inputMap.put(KeyStroke.getKeyStroke("F11"), "tinhTienAction");
		actionMap.put("tinhTienAction", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnTinhTien.isEnabled())
					xuLyTinhTien();
			}
		});
	}

	private List<BanAn> timNhomBanLienQuan(BanAn banGoc) {
		List<BanAn> ketQua = new ArrayList<>();
		List<String> dsMaBanLienQuan = new ArrayList<>();
		String trangThai = banGoc.getTrangThai();

		try {
			if (trangThai.equals("Bàn đang phục vụ")) {
				HoaDon hd = hoaDon_DAO.timHoaDonChuaThanhToanTheoMaBan(banGoc.getMaBan());
				if (hd != null) {
					dsMaBanLienQuan = hoaDonBanDAO.layDanhSachMaBanTheoHoaDon(hd.getMaHoaDon());
				}
			} else if (trangThai.equals("Bàn đang chờ")) {
				Date ngayXem = dateChooserLocNgay.getDate();
				if (ngayXem == null)
					ngayXem = new Date();
				String maPhieu = phieuDatBanDAO.timMaPhieuDatDangChoTheoBan(banGoc.getMaBan(), ngayXem);
				if (maPhieu != null) {
					dsMaBanLienQuan = phieuDatBanDAO.layDanhSachMaBanTheoPhieuDat(maPhieu);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dsMaBanLienQuan.isEmpty()) {
			ketQua.add(banGoc);
			return ketQua;
		}

		for (BanAn b : danhSachBan) {
			if (dsMaBanLienQuan.contains(b.getMaBan())) {
				ketQua.add(b);
			}
		}
		return ketQua;
	}
}
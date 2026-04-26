package ui.banan;

import dao_impl.*;
import entity.*;
import ui.HoaDonPDF;
import ui.Auth;
import connect.DBConnect;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TinhTien_UI extends JDialog implements ActionListener {
	private JLabel lblMaHoaDon, lblNhanVien, lblKhachHang, lblTenBan, lblGioNhanBan, lblGioThanhToan;
	private JTable tableMonAn;
	private DefaultTableModel tableModel;
	private JLabel lblDiemTichLuy, lblGiaTriGiam1, lblGiaTriGiam2;
	private JTextField txtMaGiamGia;
	private JButton btnKiemTra;
	private JLabel lblTongCong, lblThue, lblTongThanhToan, lblTienThua;
	private JTextField txtTienNhan;
	private JButton btnDong, btnThanhToan;
	private JRadioButton rdoDoiDiem, rdoKhongDoiDiem;
	private ButtonGroup groupDoiDiem;
	private JTabbedPane tabbedPane;
	private JLabel lblTienDaCoc;

	List<BanAn> dsBanThanhToan;
	private HoaDon hoaDon;
	private KhachHang khachHang;
	private NhanVien nhanVien;
	private KhuyenMai khuyenMaiApDung;

	private final DecimalFormat currencyFormatter = new DecimalFormat("#,##0");
	private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
	private final Color COLOR_DARK = new Color(48, 52, 56);
	private final Color COLOR_TEXT_RED = new Color(255, 87, 87);
	private final Color MAU_NEN_INPUT = new Color(45, 49, 56); 

	private double tongCong = 0;
	private double thue = 0;
	private double giamDiemTichLuy = 0;
	private double giamKhuyenMai = 0;
	private double tongThanhToan = 0;
	private boolean suDungDiemTichLuy = false;
	private double tienDaCoc = 0;
	
	private final String NGAN_HANG_ID = "MB";       
	private final String SO_TAI_KHOAN = "0935037651"; 
	private final String TEN_CHU_TK = "NGUYEN VAN TAN"; 
	private Timer timerKiemTraThanhToan;
	private final String SEPAY_API_TOKEN = "BKHTO6BVQKAUSJOL4RLJRLTSXZD2JBTQ2KZFPACH7IPSYN0VOHFYXCOB81KD8S5G"; 
	
	// Khởi tạo giao diện tính tiền
	public TinhTien_UI(Frame parent, List<BanAn> dsBan) {
	    super(parent, "Tính Tiền", true);
	    this.dsBanThanhToan = dsBan; 
	    this.nhanVien = Auth.getCurrentNhanVien();

	    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    setSize(1400, 800);
	    setLocationRelativeTo(parent);
	    setResizable(false);

	    initComponents();
	    loadData();
	}

	// Khởi tạo các thành phần giao diện
	private void initComponents() {
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setBackground(COLOR_DARK);

		JLabel lblHeader = new JLabel("TÍNH TIỀN");
		lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 32));
		lblHeader.setForeground(Color.WHITE);
		lblHeader.setBounds(600, 15, 300, 40);
		mainPanel.add(lblHeader);

		int leftX = 50, rightX = 600, rightXX = 1050;
		int y = 70;
		int fieldHeight = 30;
		int rowGap = 40;

		JLabel lbl1 = new JLabel("Mã hoá đơn:");
		lbl1.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lbl1.setForeground(Color.WHITE);
		lbl1.setBounds(leftX, y, 120, fieldHeight);
		mainPanel.add(lbl1);

		lblMaHoaDon = new JLabel("HD000001");
		lblMaHoaDon.setForeground(COLOR_TEXT_RED);
		lblMaHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblMaHoaDon.setBounds(leftX + 130, y, 150, fieldHeight);
		mainPanel.add(lblMaHoaDon);

		y += rowGap;
		JLabel lbl2 = new JLabel("Nhân viên:");
		lbl2.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lbl2.setForeground(Color.WHITE);
		lbl2.setBounds(leftX, y, 120, fieldHeight);
		mainPanel.add(lbl2);

		lblNhanVien = new JLabel("N/A");
		lblNhanVien.setForeground(COLOR_TEXT_RED);
		lblNhanVien.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblNhanVien.setBounds(leftX + 130, y, 150, fieldHeight);
		mainPanel.add(lblNhanVien);

		y += rowGap;
		JLabel lbl3 = new JLabel("Khách hàng:");
		lbl3.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lbl3.setForeground(Color.WHITE);
		lbl3.setBounds(leftX, y, 120, fieldHeight);
		mainPanel.add(lbl3);

		lblKhachHang = new JLabel("N/A");
		lblKhachHang.setForeground(COLOR_TEXT_RED);
		lblKhachHang.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblKhachHang.setBounds(leftX + 130, y, 150, fieldHeight);
		mainPanel.add(lblKhachHang);

		y = 70;

		JLabel lbl4 = new JLabel("Tên bàn:");
		lbl4.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lbl4.setForeground(Color.WHITE);
		lbl4.setBounds(rightX, y, 120, fieldHeight);
		mainPanel.add(lbl4);

		lblTenBan = new JLabel("Bàn 001");
		lblTenBan.setForeground(COLOR_TEXT_RED);
		lblTenBan.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTenBan.setBounds(rightX + 150, y, 550, fieldHeight);;
		mainPanel.add(lblTenBan);

		y += rowGap;
		JLabel lbl5 = new JLabel("Giờ nhận bàn:");
		lbl5.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lbl5.setForeground(Color.WHITE);
		lbl5.setBounds(rightX, y, 140, fieldHeight);
		mainPanel.add(lbl5);

		lblGioNhanBan = new JLabel("16:45 - 21/10/2025");
		lblGioNhanBan.setForeground(COLOR_TEXT_RED);
		lblGioNhanBan.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblGioNhanBan.setBounds(rightX + 150, y, 200, fieldHeight);
		mainPanel.add(lblGioNhanBan);

		y += rowGap;
		JLabel lbl6 = new JLabel("Giờ thanh toán:");
		lbl6.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lbl6.setForeground(Color.WHITE);
		lbl6.setBounds(rightX, y, 140, fieldHeight);
		mainPanel.add(lbl6);

		lblGioThanhToan = new JLabel("19:40 - 21/10/2025");
		lblGioThanhToan.setForeground(COLOR_TEXT_RED);
		lblGioThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblGioThanhToan.setBounds(rightX + 150, y, 200, fieldHeight);
		mainPanel.add(lblGioThanhToan);

		JPanel panelTable = new JPanel(new BorderLayout());
		panelTable.setBackground(COLOR_DARK);
		panelTable.setBounds(50, 200, 1300, 280);

		TitledBorder titleBorder = BorderFactory.createTitledBorder(null, "Danh sách món ăn", TitledBorder.LEADING,
				TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.WHITE);
		titleBorder.setBorder(BorderFactory.createEmptyBorder());
		panelTable.setBorder(titleBorder);

		tableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tableModel.setColumnIdentifiers(new Object[] { "STT", "Tên món", "Giá", "Đơn vị", "Số lượng", "Thành tiền" });

		tableMonAn = new JTable(tableModel);
		tableMonAn.setGridColor(new Color(80, 80, 80));
		tableMonAn.setIntercellSpacing(new Dimension(0, 0));
		tableMonAn.setBackground(COLOR_DARK);
		tableMonAn.setForeground(Color.WHITE);
		tableMonAn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		tableMonAn.getTableHeader().setBackground(new Color(40, 44, 48));
		tableMonAn.getTableHeader().setForeground(Color.WHITE);
		tableMonAn.getTableHeader().setBorder(null);
		tableMonAn.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
		tableMonAn.setBorder(null);
		tableMonAn.setRowHeight(30);

		JScrollPane scrollPane = new JScrollPane(tableMonAn);
		scrollPane.getViewport().setBackground(COLOR_DARK);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
		JPanel corner = new JPanel();
        corner.setBackground(MAU_NEN_INPUT);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
		tuyChinhScrollBar(scrollPane);

		panelTable.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(panelTable);

		y = 510;

		JLabel lbl7 = new JLabel("Điểm tích luỹ:");
		lbl7.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl7.setForeground(Color.WHITE);
		lbl7.setBounds(leftX, y, 120, fieldHeight);
		mainPanel.add(lbl7);

		lblDiemTichLuy = new JLabel("xxxx");
		lblDiemTichLuy.setForeground(COLOR_TEXT_RED);
		lblDiemTichLuy.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblDiemTichLuy.setBounds(leftX + 130, y, 100, fieldHeight);
		mainPanel.add(lblDiemTichLuy);

		groupDoiDiem = new ButtonGroup();

		rdoDoiDiem = new JRadioButton("Đổi điểm");
		rdoDoiDiem.setBounds(leftX + 240, y, 100, fieldHeight);
		rdoDoiDiem.setBackground(COLOR_DARK);
		rdoDoiDiem.setForeground(Color.WHITE);
		rdoDoiDiem.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		rdoDoiDiem.setSelected(false);
		rdoDoiDiem.addActionListener(this);
		groupDoiDiem.add(rdoDoiDiem);
		mainPanel.add(rdoDoiDiem);

		rdoKhongDoiDiem = new JRadioButton("Không đổi điểm");
		rdoKhongDoiDiem.setBounds(leftX + 350, y, 150, fieldHeight);
		rdoKhongDoiDiem.setBackground(COLOR_DARK);
		rdoKhongDoiDiem.setForeground(Color.WHITE);
		rdoKhongDoiDiem.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		rdoKhongDoiDiem.setSelected(true);
		rdoKhongDoiDiem.addActionListener(this);
		groupDoiDiem.add(rdoKhongDoiDiem);
		mainPanel.add(rdoKhongDoiDiem);

		y += rowGap;
		JLabel lbl8 = new JLabel("Giá trị giảm:");
		lbl8.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl8.setForeground(Color.WHITE);
		lbl8.setBounds(leftX, y, 120, fieldHeight);
		mainPanel.add(lbl8);

		lblGiaTriGiam1 = new JLabel("xxxx");
		lblGiaTriGiam1.setForeground(COLOR_TEXT_RED);
		lblGiaTriGiam1.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblGiaTriGiam1.setBounds(leftX + 130, y, 100, fieldHeight);
		mainPanel.add(lblGiaTriGiam1);

		y += rowGap;
		JLabel lbl9 = new JLabel("Mã giảm giá:");
		lbl9.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl9.setForeground(Color.WHITE);
		lbl9.setBounds(leftX, y, 120, fieldHeight);
		mainPanel.add(lbl9);

		txtMaGiamGia = new JTextField();
		txtMaGiamGia.setBounds(leftX + 130, y, 150, fieldHeight);
		txtMaGiamGia.setBackground(new Color(124, 124, 124));
		txtMaGiamGia.setForeground(Color.WHITE);
		txtMaGiamGia.setCaretColor(Color.WHITE);
		txtMaGiamGia.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		mainPanel.add(txtMaGiamGia);

		btnKiemTra = new JButton("Kiểm tra");
		btnKiemTra.setBounds(leftX + 290, y, 100, fieldHeight);
		btnKiemTra.setBackground(new Color(124, 124, 124));
		btnKiemTra.setForeground(Color.WHITE);
		btnKiemTra.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnKiemTra.addActionListener(this);
		mainPanel.add(btnKiemTra);

		y += rowGap;
		JLabel lbl10 = new JLabel("Giá trị giảm:");
		lbl10.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl10.setForeground(Color.WHITE);
		lbl10.setBounds(leftX, y, 120, fieldHeight);
		mainPanel.add(lbl10);

		lblGiaTriGiam2 = new JLabel("");
		lblGiaTriGiam2.setForeground(COLOR_TEXT_RED);
		lblGiaTriGiam2.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblGiaTriGiam2.setBounds(leftX + 130, y, 100, fieldHeight);
		mainPanel.add(lblGiaTriGiam2);

		y = 510;

		JLabel lbl11 = new JLabel("Tổng cộng:");
		lbl11.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl11.setForeground(Color.WHITE);
		lbl11.setBounds(rightX, y, 140, fieldHeight);
		mainPanel.add(lbl11);

		lblTongCong = new JLabel("0 VND");
		lblTongCong.setForeground(COLOR_TEXT_RED);
		lblTongCong.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTongCong.setBounds(rightX + 150, y, 150, fieldHeight);
		mainPanel.add(lblTongCong);

		y += rowGap;

		JLabel lbl12 = new JLabel("Thuế (VAT):");
		lbl12.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl12.setForeground(Color.WHITE);
		lbl12.setBounds(rightX, y, 140, fieldHeight);
		mainPanel.add(lbl12);

		lblThue = new JLabel("0 VND");
		lblThue.setForeground(COLOR_TEXT_RED);
		lblThue.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblThue.setBounds(rightX + 150, y, 150, fieldHeight);
		mainPanel.add(lblThue);

		JLabel iconInfo;
		try {
			java.net.URL imgUrl = TinhTien_UI.class.getResource("/img/info.png");
			if (imgUrl == null)
				throw new java.io.FileNotFoundException("Không tìm thấy file icon: /img/info.png");
			ImageIcon originalIcon = new ImageIcon(imgUrl);
			Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
			iconInfo = new JLabel(new ImageIcon(scaledImage));
		} catch (Exception e) {
			iconInfo = new JLabel("i");
			iconInfo.setForeground(Color.WHITE);
			iconInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
		}
		iconInfo.setBounds(rightX + 290, y, 24, fieldHeight);
		iconInfo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		iconInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(TinhTien_UI.this, "Thuế được tính 10% cho dịch vụ ăn uống",
						"Thông tin thuế", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mainPanel.add(iconInfo);

		y += rowGap;

		JLabel lbl13a = new JLabel("Tiền đã cọc:");
		lbl13a.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl13a.setForeground(Color.WHITE);
		lbl13a.setBounds(rightX, y, 140, fieldHeight);
		mainPanel.add(lbl13a);

		lblTienDaCoc = new JLabel("0 VND");
		lblTienDaCoc.setForeground(COLOR_TEXT_RED);
		lblTienDaCoc.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTienDaCoc.setBounds(rightX + 150, y, 150, fieldHeight);
		mainPanel.add(lblTienDaCoc);

		y += rowGap;

		JLabel lbl13 = new JLabel("Tổng thanh toán:");
		lbl13.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl13.setForeground(Color.WHITE);
		lbl13.setBounds(rightX, y, 140, fieldHeight);
		mainPanel.add(lbl13);

		lblTongThanhToan = new JLabel("");
		lblTongThanhToan.setForeground(COLOR_TEXT_RED);
		lblTongThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTongThanhToan.setBounds(rightX + 150, y, 150, fieldHeight);
		mainPanel.add(lblTongThanhToan);

		y += rowGap;

		tabbedPane = new JTabbedPane();
		tabbedPane.setBounds(rightXX, 490, 500, 180);
		tabbedPane.setBackground(COLOR_DARK);
		tabbedPane.setForeground(Color.WHITE);
		tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
		
		mainPanel.add(tabbedPane);

		JPanel panelTienMat = new JPanel();
		panelTienMat.setLayout(null);
		panelTienMat.setBackground(COLOR_DARK);

		JLabel lbl14 = new JLabel("Tiền nhận:");
		lbl14.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl14.setForeground(Color.WHITE);
		lbl14.setBounds(0, 10, 120, fieldHeight);
		panelTienMat.add(lbl14);

		txtTienNhan = new JTextField();
		txtTienNhan.setBounds(130, 10, 150, fieldHeight);
		txtTienNhan.setBackground(new Color(124, 124, 124));
		txtTienNhan.setForeground(Color.WHITE);
		txtTienNhan.setCaretColor(Color.WHITE);
		txtTienNhan.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtTienNhan.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				capNhatTienThua();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				capNhatTienThua();
			}

			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				capNhatTienThua();
			}
		});
		panelTienMat.add(txtTienNhan);

		JLabel lbl15 = new JLabel("Tiền thừa:");
		lbl15.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lbl15.setForeground(Color.WHITE);
		lbl15.setBounds(0, 10 + rowGap, 120, fieldHeight);
		panelTienMat.add(lbl15);

		lblTienThua = new JLabel("");
		lblTienThua.setForeground(COLOR_TEXT_RED);
		lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTienThua.setBounds(130, 10 + rowGap, 150, fieldHeight);
		panelTienMat.add(lblTienThua);

		tabbedPane.addTab("Tiền mặt", panelTienMat);

		JPanel panelMaQR = new JPanel();
		panelMaQR.setLayout(null);
		panelMaQR.setBackground(COLOR_DARK);

		JButton btnInVaCheck = new JButton("In Hoá Đơn"); 
		btnInVaCheck.setBackground(new Color(33, 150, 243));
		btnInVaCheck.setForeground(Color.WHITE);
		btnInVaCheck.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnInVaCheck.setBounds(10, 10, 200, 35); 
		btnInVaCheck.setBorder(null);
		btnInVaCheck.setFocusPainted(false);

		btnInVaCheck.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        xuLyInVaCheckQR(); 
		    }
		});

		panelMaQR.add(btnInVaCheck);
		tabbedPane.addTab("Mã QR", panelMaQR);

		btnDong = new JButton("Đóng");
		btnDong.setBounds(1200, 700, 120, 35);
		btnDong.setBackground(new Color(244, 67, 54));
		btnDong.setForeground(Color.WHITE);
		btnDong.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnDong.addActionListener(this);
		mainPanel.add(btnDong);

		btnThanhToan = new JButton("Thanh toán");
		btnThanhToan.setBounds(1050, 700, 120, 35);
		btnThanhToan.setBackground(new Color(76, 175, 80));
		btnThanhToan.setForeground(Color.WHITE);
		btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnThanhToan.addActionListener(this);
		mainPanel.add(btnThanhToan);

		JLabel lblFooter = new JLabel("Sau khi thanh toán hoá đơn này, khách hàng sẽ được + 1% điểm tích luỹ");
		lblFooter.setForeground(new Color(150, 150, 150));
		lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblFooter.setBounds(50, 765, 1000, 20);
		mainPanel.add(lblFooter);

		add(mainPanel);
		
		tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
		    @Override
		    public void stateChanged(javax.swing.event.ChangeEvent e) {
		        int index = tabbedPane.getSelectedIndex();
		        if (index == 1) { 
		            btnThanhToan.setEnabled(false);
		            btnThanhToan.setBackground(Color.GRAY);
		        } else {
		            btnThanhToan.setEnabled(true);
		            btnThanhToan.setBackground(new Color(76, 175, 80)); 
		        }
		    }
		});
	}
	
	// Xử lý in hóa đơn và kiểm tra mã QR
	private void xuLyInVaCheckQR() {
	    long soTien = (long) tongThanhToan;
	    if (soTien <= 0) {
	        JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
	        return;
	    }
	    String qrURL = taoLinkVietQR(soTien);

	    double tongTienGiam = giamDiemTichLuy + giamKhuyenMai;
	    HoaDonPDF.xuatHoaDonPDF(
	        hoaDon, khachHang, nhanVien, dsBanThanhToan, 
	        hoaDon != null ? hoaDon.getNgayLapHoaDon() : new Date(), 
	        new Date(), tableModel, tongCong, thue, tongThanhToan, tienDaCoc, 
	        BigDecimal.valueOf(tongTienGiam), 
	        qrURL
	    );
	    hienThiDialogQR(qrURL); 
	}

	// Tải dữ liệu hóa đơn và hiển thị lên giao diện
	private void loadData() {
		loadMonAnTable();
		if (hoaDon != null) {
			lblMaHoaDon.setText(hoaDon.getMaHoaDon());
		} else {
			HoaDon_DAO hdDao = new HoaDon_DAO();
			String maHD = hdDao.sinhMaHoaDonTuDong();
			lblMaHoaDon.setText(maHD);
		}

		if (nhanVien != null) {
			lblNhanVien.setText(nhanVien.getHoTen());
		}

		if (dsBanThanhToan != null && !dsBanThanhToan.isEmpty()) {
		    StringBuilder sb = new StringBuilder();
		    for (int i = 0; i < dsBanThanhToan.size(); i++) {
		        sb.append(dsBanThanhToan.get(i).getTenBan());
		        if (i < dsBanThanhToan.size() - 1) sb.append(", ");
		    }
		    String chuoiTenBan = sb.toString();
		    lblTenBan.setText(chuoiTenBan);
		    lblTenBan.setToolTipText(chuoiTenBan);
		}

		if (hoaDon != null && hoaDon.getMaKhachHang() != null) {
			KhachHang_DAO khDao = new KhachHang_DAO();
			List<KhachHang> khList = khDao.timKiemTheoMa(hoaDon.getMaKhachHang());
			if (khList != null && !khList.isEmpty()) {
				khachHang = khList.get(0);
				lblKhachHang.setText(khachHang.getHoTen());
				lblDiemTichLuy.setText(String.valueOf(khachHang.getTichDiem()));
			}
		} else {
			lblKhachHang.setText("Khách lẻ");
			lblDiemTichLuy.setText("0");
		}

		if (hoaDon != null && hoaDon.getNgayLapHoaDon() != null) {
			lblGioNhanBan.setText(dateTimeFormatter.format(hoaDon.getNgayLapHoaDon()));
		} else {
			lblGioNhanBan.setText(dateTimeFormatter.format(new Date()));
		}
		lblGioThanhToan.setText(dateTimeFormatter.format(new Date()));

		calculateTotals();
	}

	// Tải danh sách món ăn vào bảng
	private void loadMonAnTable() {
		tableModel.setRowCount(0);

	    if (dsBanThanhToan == null || dsBanThanhToan.isEmpty())
	        return;

	    HoaDon_DAO hdDao = new HoaDon_DAO();
	    hoaDon = hdDao.timHoaDonChuaThanhToanTheoMaBan(dsBanThanhToan.get(0).getMaBan());

	    if (hoaDon == null) return;

		if (hoaDon.getTienDatCoc() != null) {
			this.tienDaCoc = hoaDon.getTienDatCoc().doubleValue();
		} else {
			this.tienDaCoc = 0;
		}

		ChiTietHoaDon_DAO cthdDao = new ChiTietHoaDon_DAO();
	    List<ChiTietHoaDon> chiTietList = cthdDao.getChiTietTheoMaHoaDon(hoaDon.getMaHoaDon());

		MonAn_DAO monDao = new MonAn_DAO();
		int stt = 1;
		for (ChiTietHoaDon ct : chiTietList) {
			MonAn mon = monDao.timMotMonTheoMa(ct.getMaMon());
			if (mon != null) {
				double thanhTien = ct.getDonGia().doubleValue() * ct.getSoLuong();
				tableModel.addRow(
						new Object[] { stt++, mon.getTenMon(), currencyFormatter.format(ct.getDonGia().doubleValue()),
								mon.getDonVi(), ct.getSoLuong(), currencyFormatter.format(thanhTien) });
			}
		}
	}

	// Tính toán tổng tiền, thuế và giảm giá
	private void calculateTotals() {
		tongCong = 0;
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String thanhTienStr = tableModel.getValueAt(i, 5).toString().replaceAll("[^0-9]", "");
			tongCong += Double.parseDouble(thanhTienStr.isEmpty() ? "0" : thanhTienStr);
		}

		thue = tongCong * 0.1;
		giamDiemTichLuy = (suDungDiemTichLuy && khachHang != null) ? khachHang.getTichDiem() : 0;

		tongThanhToan = tongCong + thue - giamDiemTichLuy - giamKhuyenMai - tienDaCoc;

		lblTongCong.setText(currencyFormatter.format(tongCong) + " VND");
		lblThue.setText(currencyFormatter.format(thue) + " VND");
		lblGiaTriGiam1.setText(currencyFormatter.format(giamDiemTichLuy) + " VND");
		lblTienDaCoc.setText(currencyFormatter.format(tienDaCoc) + " VND");
		lblTongThanhToan.setText(currencyFormatter.format(tongThanhToan) + " VND");
	
	}

	// Cập nhật tiền thừa khi nhập tiền khách đưa
	private void capNhatTienThua() {
		String tienNhanStr = txtTienNhan.getText().trim().replaceAll("[^0-9]", "");

		if (tienNhanStr.isEmpty()) {
			lblTienThua.setText("");
			return;
		}

		try {
			double tienNhan = Double.parseDouble(tienNhanStr);

			if (tienNhan >= tongThanhToan) {
				double tienThua = tienNhan - tongThanhToan;
				lblTienThua.setText(currencyFormatter.format(tienThua) + " VND");
			} else {
				lblTienThua.setText("Chưa đủ");
			}
		} catch (NumberFormatException e) {
			lblTienThua.setText("");
		}
	}

	// Xử lý sự kiện click nút
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnDong) {
			dispose();
		} else if (e.getSource() == btnThanhToan) {
			processPayment();
		} else if (e.getSource() == btnKiemTra) {
		    if (txtMaGiamGia.getText().trim().isEmpty()) {
		        hienThiDialogChonKhuyenMai();
		    } else {
		        hienThiDialogChonKhuyenMai(); 
		    }
		} else if (e.getSource() == rdoDoiDiem || e.getSource() == rdoKhongDoiDiem) {
			suDungDiemTichLuy = rdoDoiDiem.isSelected();
			calculateTotals();
			capNhatTienThua();
		}
	}

	// Xử lý quy trình thanh toán và cập nhật dữ liệu
	private void processPayment() {
		double tienNhan = 0;
		double tienThua = 0;

		int selectedTab = tabbedPane.getSelectedIndex();

		if (selectedTab == 0) {
			String tienNhanStr = txtTienNhan.getText().trim().replaceAll("[^0-9]", "");
			if (tienNhanStr.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền nhận", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			tienNhan = Double.parseDouble(tienNhanStr);
			if (tienNhan < tongThanhToan) {
				JOptionPane.showMessageDialog(this, "Tiền nhận phải >= Tổng thanh toán", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			tienThua = tienNhan - tongThanhToan;
			lblTienThua.setText(currencyFormatter.format(tienThua) + " VND");
		} else { 
			tienNhan = tongThanhToan;
			tienThua = 0;
		}

		if (hoaDon != null) {
	        hoaDon.setTrangThai("Đã thanh toán");
	        hoaDon.setThue(BigDecimal.valueOf(thue));
	        hoaDon.setSoTienKhachTra(BigDecimal.valueOf(tienNhan));
	        HoaDon_DAO hdDao = new HoaDon_DAO();
	        
	        if (khuyenMaiApDung != null) {
	            hoaDon.setMaKhuyenMai(khuyenMaiApDung.getMaKhuyenMai());
	        }
	        if (hdDao.capNhatHoaDon(hoaDon)) {
	            if (khachHang != null) {
	                KhachHang_DAO khDao = new KhachHang_DAO();
	                int diemHienCo = khachHang.getTichDiem();
	                int diemSauKhiTru = diemHienCo;
	                if (suDungDiemTichLuy) {
	                    int diemDaDung = (int) giamDiemTichLuy;
	                    diemSauKhiTru = diemHienCo - diemDaDung;
	                    if (diemSauKhiTru < 0) diemSauKhiTru = 0;
	                }
	                int diemThuong = (int) (tongCong * 0.01);
	                int diemCuoiCung = diemSauKhiTru + diemThuong;
	                khachHang.setTichDiem(diemCuoiCung);
	                khDao.capNhatKhachHang(khachHang);
	            }
	            if (dsBanThanhToan != null) {
	                BanAn_DAO banDao = new BanAn_DAO();
	                for (BanAn b : dsBanThanhToan) {
	                    banDao.capNhatTrangThaiBan(b.getMaBan(), "Bàn đang trống");
	                }
	            }

	            if (selectedTab == 0) {
	                int result = JOptionPane.showConfirmDialog(this, "Thanh toán thành công! Bạn có muốn xem hóa đơn PDF?",
	                        "Thành công", JOptionPane.YES_NO_OPTION);

	                if (result == JOptionPane.YES_OPTION) {
	                    double tongTienGiam = giamDiemTichLuy + giamKhuyenMai;
	                    BigDecimal tienGiamBD = BigDecimal.valueOf(tongTienGiam);

	                    HoaDonPDF.xuatHoaDonPDF(
	                        hoaDon, khachHang, nhanVien, dsBanThanhToan, hoaDon.getNgayLapHoaDon(), new Date(), 
	                        tableModel, tongCong, thue, tongThanhToan, tienDaCoc, tienGiamBD, null 
	                    );
	                }
	            } else {
	                JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
	            }
	            dispose();
	        }
	    }
	}
	
	// Tạo đường dẫn mã QR VietQR
	private String taoLinkVietQR(long soTien) {
	    try {
	        String noiDungCK = lblMaHoaDon.getText().trim();
	        String noiDungEncoded = java.net.URLEncoder.encode(noiDungCK, "UTF-8").replace("+", "%20");
	        String tenChuTkEncoded = java.net.URLEncoder.encode(TEN_CHU_TK, "UTF-8").replace("+", "%20");
	        return String.format("https://img.vietqr.io/image/%s-%s-compact.png?amount=%d&addInfo=%s&accountName=%s",
	                NGAN_HANG_ID, SO_TAI_KHOAN, soTien, noiDungEncoded, tenChuTkEncoded);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "";
	    }
	}
	
	// Hiển thị hộp thoại quét mã QR
	private void hienThiDialogQR(String urlQRDaTao) {
	    long soTien = (long) tongThanhToan;
	    if (soTien <= 0) {
	        JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
	        return;
	    }
	    
	    String noiDungCK = lblMaHoaDon.getText().trim();
	    
	    String finalUrl = (urlQRDaTao != null && !urlQRDaTao.isEmpty()) ? urlQRDaTao : taoLinkVietQR(soTien);

	    JDialog dialogQR = new JDialog(this, "Quét Mã Thanh Toán", true);
	    dialogQR.setSize(450, 650);
	    dialogQR.setLocationRelativeTo(this);
	    dialogQR.setLayout(new BorderLayout());
	    dialogQR.getContentPane().setBackground(Color.WHITE);

	    JLabel lblTitle = new JLabel("MỞ APP NGÂN HÀNG QUÉT MÃ", SwingConstants.CENTER);
	    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
	    lblTitle.setForeground(new Color(0, 102, 204)); 
	    lblTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
	    dialogQR.add(lblTitle, BorderLayout.NORTH);

	    JLabel lblAnhQR = new JLabel("Đang tải mã QR...", SwingConstants.CENTER);
	    lblAnhQR.setFont(new Font("Segoe UI", Font.ITALIC, 14));
	    dialogQR.add(lblAnhQR, BorderLayout.CENTER);

	    JPanel pnlFooter = new JPanel();
	    pnlFooter.setLayout(new BoxLayout(pnlFooter, BoxLayout.Y_AXIS));
	    pnlFooter.setBackground(Color.WHITE);
	    pnlFooter.setBorder(new EmptyBorder(10, 10, 20, 10));

	    JLabel lblTrangThai = new JLabel("Đang chờ thanh toán...", SwingConstants.CENTER);
	    lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 15));
	    lblTrangThai.setForeground(Color.RED);
	    lblTrangThai.setAlignmentX(Component.CENTER_ALIGNMENT);
	    
	    JLabel lblInstruction = new JLabel("(Hệ thống tự động duyệt sau 3-5 giây)", SwingConstants.CENTER);
	    lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	    lblInstruction.setForeground(Color.GRAY);
	    lblInstruction.setAlignmentX(Component.CENTER_ALIGNMENT);

	    Dimension btnSize = new Dimension(180, 40); 

	    JButton btnCheckNow = new JButton("Kiểm tra ngay");
	    btnCheckNow.setBackground(new Color(33, 150, 243)); 
	    btnCheckNow.setForeground(Color.WHITE);
	    btnCheckNow.setFont(new Font("Segoe UI", Font.BOLD, 14));
	    btnCheckNow.setPreferredSize(btnSize);
	    btnCheckNow.setMaximumSize(btnSize);
	    btnCheckNow.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnCheckNow.setBorder(null);
	    btnCheckNow.setFocusPainted(false);
	    btnCheckNow.addActionListener(e -> {
	        lblTrangThai.setText("Đang kiểm tra...");
	        lblTrangThai.setForeground(Color.BLUE);
	        checkGiaoDichSepay(noiDungCK, soTien, dialogQR, lblTrangThai);
	    });

	    JButton btnHuy = new JButton("Hủy Bỏ");
	    btnHuy.setBackground(new Color(244, 67, 54)); 
	    btnHuy.setForeground(Color.WHITE);
	    btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
	    btnHuy.setPreferredSize(btnSize);
	    btnHuy.setMaximumSize(btnSize);
	    btnHuy.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnHuy.setBorder(null);
	    btnHuy.setFocusPainted(false);
	    btnHuy.addActionListener(e -> {
	        if (timerKiemTraThanhToan != null) timerKiemTraThanhToan.stop();
	        dialogQR.dispose();
	    });

	    JButton btnDemoSuccess = new JButton("Đã nhận");
	    btnDemoSuccess.setBackground(Color.GRAY); 
	    btnDemoSuccess.setForeground(Color.WHITE);
	    btnDemoSuccess.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	    btnDemoSuccess.setPreferredSize(new Dimension(120, 30));
	    btnDemoSuccess.setMaximumSize(new Dimension(120, 30));
	    btnDemoSuccess.setAlignmentX(Component.CENTER_ALIGNMENT);
	    btnDemoSuccess.setBorder(null);
	    btnDemoSuccess.setFocusPainted(false);
	    btnDemoSuccess.addActionListener(e -> {
	        if (timerKiemTraThanhToan != null) timerKiemTraThanhToan.stop();
	        dialogQR.dispose();
	        processPayment(); 
	    });

	    pnlFooter.add(lblTrangThai);
	    pnlFooter.add(Box.createVerticalStrut(5));
	    pnlFooter.add(lblInstruction);
	    pnlFooter.add(Box.createVerticalStrut(20)); 
	    pnlFooter.add(btnCheckNow);                 
	    pnlFooter.add(Box.createVerticalStrut(10)); 
	    pnlFooter.add(btnHuy);                      
	    pnlFooter.add(Box.createVerticalStrut(20)); 
	    pnlFooter.add(btnDemoSuccess);             

	    dialogQR.add(pnlFooter, BorderLayout.SOUTH);

	    new SwingWorker<ImageIcon, Void>() {
	        @Override
	        protected ImageIcon doInBackground() throws Exception {
	            java.net.URL url = new java.net.URL(finalUrl);
	            java.net.URLConnection connection = url.openConnection();
	            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
	            Image image = javax.imageio.ImageIO.read(connection.getInputStream());
	            return new ImageIcon(image.getScaledInstance(350, 350, Image.SCALE_SMOOTH));
	        }

	        @Override
	        protected void done() {
	            try {
	                lblAnhQR.setText("");
	                lblAnhQR.setIcon(get());
	            } catch (Exception e) {
	                lblAnhQR.setText("Lỗi tải QR! Vui lòng thử lại.");
	                e.printStackTrace();
	            }
	        }
	    }.execute();

	    timerKiemTraThanhToan = new Timer(2000, new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            checkGiaoDichSepay(noiDungCK, soTien, dialogQR, lblTrangThai);
	        }
	    });
	    timerKiemTraThanhToan.start();

	    dialogQR.addWindowListener(new java.awt.event.WindowAdapter() {
	        @Override
	        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	            timerKiemTraThanhToan.stop();
	        }
	    });

	    dialogQR.setVisible(true);
	}
	
	// Kiểm tra trạng thái giao dịch qua API SePay
	private void checkGiaoDichSepay(String noiDungCanTim, long soTienCanTim, JDialog dialog, JLabel lblStatus) {
	    String apiURL = "https://my.sepay.vn/userapi/transactions/list";

	    new SwingWorker<Boolean, Void>() {
	        @Override
	        protected Boolean doInBackground() throws Exception {
	            try {
	                java.net.URL url = new java.net.URL(apiURL);
	                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
	                conn.setRequestMethod("GET");
	                conn.setRequestProperty("Authorization", "Bearer " + SEPAY_API_TOKEN);
	                conn.setRequestProperty("Content-Type", "application/json");

	                int responseCode = conn.getResponseCode();
	                if (responseCode != 200) {
	                    System.out.println("Lỗi kết nối Sepay: " + responseCode);
	                    return false;
	                }

	                java.io.BufferedReader in = new java.io.BufferedReader(
	                        new java.io.InputStreamReader(conn.getInputStream()));
	                String inputLine;
	                StringBuilder content = new StringBuilder();
	                while ((inputLine = in.readLine()) != null) {
	                    content.append(inputLine);
	                }
	                in.close();

	                String responseBody = content.toString();
	             
	                boolean khopNoiDung = responseBody.contains(noiDungCanTim);
	                boolean khopSoTien = responseBody.contains("\"amount_in\":\"" + soTienCanTim + ".00\"") || 
	                                     responseBody.contains("\"amount_in\":" + soTienCanTim) ||
	                                     responseBody.contains(String.valueOf(soTienCanTim));

	                return khopNoiDung && khopSoTien;

	            } catch (Exception e) {
	                e.printStackTrace();
	                return false;
	            }
	        }

	        @Override
	        protected void done() {
	            try {
	                boolean thanhCong = get();
	                if (thanhCong) {
	                    timerKiemTraThanhToan.stop();

	                    lblStatus.setText("Giao dịch thành công!");
	                    lblStatus.setForeground(new Color(76, 175, 80));
	                   
	                    Timer delayClose = new Timer(1000, new ActionListener() {
	                        @Override
	                        public void actionPerformed(ActionEvent e) {
	                            dialog.dispose(); 
	                            
	                            processPayment(); 
	                        }
	                    });
	                    delayClose.setRepeats(false);
	                    delayClose.start();
	                } 
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }.execute();
	}

	// Tùy chỉnh giao diện thanh cuộn
	private void tuyChinhScrollBar(JScrollPane s) {
		s.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		s.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
		s.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
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
	
	// Hiển thị hộp thoại chọn khuyến mãi
	private void hienThiDialogChonKhuyenMai() {
	    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Chọn Khuyến Mãi", ModalityType.APPLICATION_MODAL);
	    dialog.setSize(950, 600);
	    dialog.setLocationRelativeTo(this);
	    dialog.setLayout(new BorderLayout());
	    dialog.getContentPane().setBackground(COLOR_DARK);

	    JLabel lblTitle = new JLabel("DANH SÁCH KHUYẾN MÃI KHẢ DỤNG", SwingConstants.CENTER);
	    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
	    lblTitle.setForeground(Color.WHITE);
	    lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
	    dialog.add(lblTitle, BorderLayout.NORTH);

	    String[] columnNames = {"Mã KM", "Tên khuyến mãi", "Loại", "Giá trị", "Ngày kết thúc"};
	    DefaultTableModel modelDialog = new DefaultTableModel(columnNames, 0) {
	        @Override
	        public boolean isCellEditable(int row, int column) { return false; }
	    };

	    SimpleDateFormat dateFormatOnly = new SimpleDateFormat("dd/MM/yyyy");
	    final List<KhuyenMai> listKM = new ArrayList<>(); 
	    
	    try {
	        KhuyenMai_DAO kmDao = new KhuyenMai_DAO(DBConnect.getConnection());
	    
	        listKM.addAll(kmDao.getAllList()); 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    if (listKM.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Không có khuyến mãi nào khả dụng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
	        return;
	    }

	    for (KhuyenMai km : listKM) {
	        String giaTriHienThi = km.getLoaiKhuyenMai().equals("Giảm %") ? 
	                               String.format("%.0f%%", km.getGiaTriGiam()) : 
	                               currencyFormatter.format(km.getGiaTriGiam());
	        
	        modelDialog.addRow(new Object[]{
	            km.getMaKhuyenMai(),
	            km.getTenKhuyenMai(),
	            km.getLoaiKhuyenMai(),
	            giaTriHienThi,
	            dateFormatOnly.format(km.getNgayKetThuc())
	        });
	    }

	    JTable tableDialog = new JTable(modelDialog);
	    tableDialog.setRowHeight(30);
	    tableDialog.setBackground(new Color(45, 49, 56));
	    tableDialog.setForeground(Color.WHITE);
	    tableDialog.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	    tableDialog.getTableHeader().setBackground(new Color(40, 44, 48));
	    tableDialog.getTableHeader().setForeground(Color.WHITE);
	    tableDialog.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
	    tableDialog.setSelectionForeground(Color.WHITE);
	    
	    JScrollPane scroll = new JScrollPane(tableDialog);
	    scroll.getViewport().setBackground(COLOR_DARK);
	    tuyChinhScrollBar(scroll);
	    dialog.add(scroll, BorderLayout.CENTER);

	    JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
	    pnlBottom.setBackground(COLOR_DARK);
	    Dimension btnSize = new Dimension(120, 40);
	    JButton btnHuy = new JButton("Hủy");
	    btnHuy.setBackground(new Color(244, 67, 54));
	    btnHuy.setForeground(Color.WHITE);
	    btnHuy.setPreferredSize(btnSize);
	    btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
	    btnHuy.addActionListener(e -> dialog.dispose());

	    JButton btnApDung = new JButton("Áp dụng");
	    btnApDung.setBackground(new Color(76, 175, 80));
	    btnApDung.setForeground(Color.WHITE);
	    btnApDung.setPreferredSize(btnSize);
	    btnApDung.setFont(new Font("Segoe UI", Font.BOLD, 14));
	    
	    btnApDung.addActionListener(e -> {
	        int selectedRow = tableDialog.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(dialog, "Vui lòng chọn một khuyến mãi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        KhuyenMai kmSelected = listKM.get(selectedRow);
	      
	        xuLyApDungKhuyenMai(kmSelected);
	        
	        dialog.dispose();
	    });

	    pnlBottom.add(btnHuy);
	    pnlBottom.add(btnApDung);
	    dialog.add(pnlBottom, BorderLayout.SOUTH);

	    dialog.setVisible(true);
	}
	
	// Áp dụng khuyến mãi được chọn vào hóa đơn
	private void xuLyApDungKhuyenMai(KhuyenMai km) {
	    this.khuyenMaiApDung = km;
	    txtMaGiamGia.setText(km.getMaKhuyenMai());

	    double giaTri = km.getGiaTriGiam();

	    if ("Giảm %".equals(km.getLoaiKhuyenMai())) {
	        giamKhuyenMai = tongCong * (giaTri / 100);
	    } else {
	        giamKhuyenMai = giaTri;
	    }

	    lblGiaTriGiam2.setText(currencyFormatter.format(giamKhuyenMai) + " VND");
	    
	    calculateTotals();
	    capNhatTienThua();
	    
	    JOptionPane.showMessageDialog(this, 
	        "Đã áp dụng mã: " + km.getTenKhuyenMai(), 
	        "Thành công", 
	        JOptionPane.INFORMATION_MESSAGE);
	}
}
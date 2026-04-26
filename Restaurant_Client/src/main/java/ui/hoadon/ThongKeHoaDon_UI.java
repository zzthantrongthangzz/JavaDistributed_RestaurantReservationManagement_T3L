package ui.hoadon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import dao_impl.HoaDon_DAO;
import entity.HoaDon;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ThongKeHoaDon_UI extends JPanel {

    private final Color MAU_XANH_LUC = new Color(76, 175, 80);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_NEN_ITEM = new Color(31, 32, 44);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_XANH_LAM = new Color(30, 144, 255);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_TIEU_DE_STATS = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_GIA_TRI_STATS = new Font("Segoe UI", Font.BOLD, 24);
    private final Color MAU_DOANH_THU = new Color(76, 175, 80);
    private final Color MAU_HOA_DON = new Color(33, 150, 243);
    private final Color MAU_TRUNG_BINH = new Color(255, 152, 0);
    private HoaDon_DAO hoaDonDAO;
    private JPanel panelChinh;
    private JDateChooser dcTuNgay;
    private JDateChooser dcDenNgay;
    private ChartPanel chartPanel;
    private JPanel panelBieuDoContainer;
    private JLabel lblGiaTriDoanhThu, lblGiaTriSoHoaDon, lblGiaTriTrungBinh;
    private JTable tblThongKe;
    private DefaultTableModel modelThongKe;
    private JTabbedPane tabbedPane;
    private JButton btnHomNay, btnTuanNay, btnThangNay, btnNamNay, btnXuatExcel;
    private List<HoaDon> danhSachHoaDonDayDu;
    private final DecimalFormat currencyFormat = new DecimalFormat("###,###,### VNĐ");
    private final DecimalFormat numberFormat = new DecimalFormat("###,###");

    // Khởi tạo giao diện và các thành phần
    public ThongKeHoaDon_UI() {
        hoaDonDAO = new HoaDon_DAO();
        
        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB);
        add(taoPanelTieuDe(), BorderLayout.NORTH);
        this.panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(10, 25, 20, 25));

        JPanel panelDieuKhien = new JPanel();
        panelDieuKhien.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0)); 
        panelDieuKhien.setBackground(MAU_NEN_TAB);
        panelDieuKhien.setPreferredSize(new Dimension(0, 50));
        
        btnHomNay = taoNutThoiGian("Hôm nay");
        btnTuanNay = taoNutThoiGian("Tuần này");
        btnThangNay = taoNutThoiGian("Tháng này");
        btnNamNay = taoNutThoiGian("Năm nay"); 
        
        panelDieuKhien.add(btnHomNay);
        panelDieuKhien.add(btnTuanNay);
        panelDieuKhien.add(btnThangNay);
        panelDieuKhien.add(btnNamNay); 
            
        panelDieuKhien.add(Box.createRigidArea(new Dimension(20, 0)));
        
        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTuNgay.setForeground(MAU_CHU_CHUNG);
        
        dcTuNgay = taoDateChooser();
        dcTuNgay.addPropertyChangeListener("date", (PropertyChangeEvent evt) -> {
            thucHienThongKe();
        });
        
        JLabel lblDenNgay = new JLabel("Đến ngày:");
        lblDenNgay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDenNgay.setForeground(MAU_CHU_CHUNG);
        
        dcDenNgay = taoDateChooser();
        dcDenNgay.addPropertyChangeListener("date", (PropertyChangeEvent evt) -> {
            thucHienThongKe();
        });

        panelDieuKhien.add(lblTuNgay);
        panelDieuKhien.add(dcTuNgay);
        panelDieuKhien.add(Box.createRigidArea(new Dimension(10, 0)));
        panelDieuKhien.add(lblDenNgay);
        panelDieuKhien.add(dcDenNgay);
        panelDieuKhien.add(Box.createRigidArea(new Dimension(10, 0)));
        
        btnXuatExcel = new JButton("Xuất File");
        btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXuatExcel.setForeground(MAU_CHU_CHUNG);
        btnXuatExcel.setBackground(MAU_XANH_LUC);
        btnXuatExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXuatExcel.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
        btnXuatExcel.setPreferredSize(new Dimension(110, 40));
        btnXuatExcel.setFocusPainted(false);
        btnXuatExcel.addActionListener(e -> xuatFileExcel());
        panelDieuKhien.add(btnXuatExcel);
        
        panelChinh.add(panelDieuKhien, BorderLayout.NORTH);
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(MAU_NEN_TAB);
        tabbedPane.setForeground(MAU_CHU_CHUNG);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }
        });
        JPanel panelTab1 = new JPanel(new BorderLayout(0, 10));
        panelTab1.setBackground(MAU_NEN_TAB);
        JPanel panelKPI = new JPanel(new GridLayout(1, 3, 20, 0));
        panelKPI.setOpaque(false);
        panelKPI.setBorder(new EmptyBorder(0, 0, 10, 0));

        lblGiaTriDoanhThu = new JLabel("0 VNĐ");
        lblGiaTriSoHoaDon = new JLabel("0");
        lblGiaTriTrungBinh = new JLabel("0 VNĐ");

        panelKPI.add(taoBoxThongKe("TỔNG DOANH THU", lblGiaTriDoanhThu, MAU_DOANH_THU));
        panelKPI.add(taoBoxThongKe("TỔNG SỐ HÓA ĐƠN", lblGiaTriSoHoaDon, MAU_HOA_DON));
        panelKPI.add(taoBoxThongKe("TRUNG BÌNH / HÓA ĐƠN", lblGiaTriTrungBinh, MAU_TRUNG_BINH));
        
        panelTab1.add(panelKPI, BorderLayout.NORTH);

        panelBieuDoContainer = new JPanel(new BorderLayout());
        panelBieuDoContainer.setBackground(MAU_NEN_TAB);
        chartPanel = new ChartPanel(null); 
        chartPanel.setBackground(MAU_NEN_TAB);
        chartPanel.setOpaque(false);
        panelBieuDoContainer.add(chartPanel, BorderLayout.CENTER);
        
        panelTab1.add(panelBieuDoContainer, BorderLayout.CENTER);
        tabbedPane.addTab("Tổng quan & Biểu đồ", panelTab1);
        
        JPanel panelBang = new JPanel(new BorderLayout());
        panelBang.setOpaque(false);
        String[] headers = {"STT", "Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên", "Tổng tiền"};
        modelThongKe = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblThongKe = new JTable(modelThongKe);
        tuyChinhBang(tblThongKe);
        JScrollPane scrollPane = new JScrollPane(tblThongKe);
        tuyChinhScrollBar(scrollPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelBang.add(scrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Danh sách hóa đơn", panelBang);
        
        panelChinh.add(tabbedPane, BorderLayout.CENTER);
        add(panelChinh, BorderLayout.CENTER);
        datThoiGianThangNay();
        setButtonActive(btnThangNay);
    }

    // Tạo panel tiêu đề chính
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 80)); 
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setBackground(MAU_NEN_TAB);
        
        JLabel lblTieuDe = new JLabel("THỐNG KÊ HOÁ ĐƠN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTieuDe.setForeground(Color.WHITE);
        
        lblTieuDe.setBorder(new EmptyBorder(10, 30, 10, 30));        
        contentPanel.add(lblTieuDe);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Tạo ô hiển thị chỉ số thống kê (KPI)
    private JPanel taoBoxThongKe(String tieuDe, JLabel lblGiaTri, Color mauVien) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(MAU_NEN_ITEM);
        box.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(mauVien, 2),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTieuDe = new JLabel(tieuDe, SwingConstants.CENTER);
        lblTieuDe.setFont(FONT_TIEU_DE_STATS);
        lblTieuDe.setForeground(MAU_CHU_CHUNG);
        box.add(lblTieuDe, BorderLayout.NORTH);

        lblGiaTri.setFont(FONT_GIA_TRI_STATS);
        lblGiaTri.setForeground(mauVien);
        lblGiaTri.setHorizontalAlignment(SwingConstants.CENTER);
        box.add(lblGiaTri, BorderLayout.CENTER);

        return box;
    }

    // Tạo các nút chọn nhanh thời gian
    private JButton taoNutThoiGian(String text) {
        JButton btn = new JButton(text); 
        setupButton(btn, null);
        
        if (text.equals("Hôm nay")) btn.addActionListener(e -> { datThoiGianHomNay(); setButtonActive(btn); });
        else if (text.equals("Tuần này")) btn.addActionListener(e -> { datThoiGianTuanNay(); setButtonActive(btn); });
        else if (text.equals("Tháng này")) btn.addActionListener(e -> { datThoiGianThangNay(); setButtonActive(btn); });
        else if (text.equals("Năm nay")) btn.addActionListener(e -> { datThoiGianNamNay(); setButtonActive(btn); });
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                if (btn.getBackground() != MAU_XANH_LUC) {
                    btn.setBackground(MAU_VIEN_THANH_TIM_KIEM); 
                }
            }
            public void mouseExited(MouseEvent e) { 
                if (btn.getBackground() != MAU_XANH_LUC) {
                    btn.setBackground(MAU_THANH_TIM_KIEM); 
                }
            }
        });
        return btn;
    }

    // Thiết lập style chung cho Button
    private void setupButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(MAU_CHU_CHUNG);
        btn.setBackground(bg != null ? bg : MAU_THANH_TIM_KIEM);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setFocusPainted(false);
    }
    
    // Tạo và cấu hình JDateChooser
    private JDateChooser taoDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(140, 40));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(FONT_TEXTFIELD);
        dateChooser.setBackground(MAU_THANH_TIM_KIEM);
        dateChooser.setForeground(MAU_CHU_CHUNG);
        
        JButton calendarButton = dateChooser.getCalendarButton();
        calendarButton.setBackground(MAU_THANH_TIM_KIEM);
        calendarButton.setBorder(BorderFactory.createEmptyBorder());
        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateChooser.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
        JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) dateChooser.getDateEditor().getUiComponent();
        dateEditor.setBackground(MAU_THANH_TIM_KIEM);
        dateEditor.setForeground(Color.WHITE);
        dateEditor.setDisabledTextColor(Color.WHITE);
        dateEditor.setCaretColor(Color.WHITE);
        dateEditor.setSelectedTextColor(Color.WHITE);
        dateEditor.setSelectionColor(MAU_VIEN_THANH_TIM_KIEM);
        dateEditor.setFont(FONT_TEXTFIELD);
        dateEditor.setBorder(new EmptyBorder(0, 8, 0, 0));
        dateEditor.setOpaque(true);
        dateEditor.setEditable(false); 
        dateChooser.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName()) || "foreground".equals(evt.getPropertyName())) {
                if (!Color.WHITE.equals(dateEditor.getForeground())) {
                   dateEditor.setForeground(Color.WHITE);
                }
                if (!Color.WHITE.equals(dateEditor.getDisabledTextColor())) {
                    dateEditor.setDisabledTextColor(Color.WHITE);
                }
            }
        });

        return dateChooser;
    }

    // Tùy chỉnh giao diện JTable
    private void tuyChinhBang(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(60, 63, 65));
        table.setBackground(MAU_NEN_TAB);
        table.setForeground(MAU_CHU_CHUNG);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(MAU_XANH_LAM.darker());
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_INPUT);
        header.setForeground(MAU_CHU_CHUNG);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 40));
        
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(50);
        cm.getColumn(1).setPreferredWidth(100);
        cm.getColumn(2).setPreferredWidth(150);
    }
    
    // Tùy chỉnh thanh cuộn
    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
        scrollPane.getHorizontalScrollBar().setBackground(MAU_NEN_INPUT);
    }

    // Thực hiện logic thống kê và cập nhật UI
    private void thucHienThongKe() {
    	Date tuNgay = dcTuNgay.getDate();
        Date denNgay = dcDenNgay.getDate();        
        if (tuNgay == null || denNgay == null) {
            return; 
        }
        if (tuNgay.after(denNgay)) {
            if (this.isShowing()) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc.");
            }
            return;
        }
        BigDecimal tongDoanhThu = hoaDonDAO.getTongDoanhThu(tuNgay, denNgay);
        int tongSoHoaDon = hoaDonDAO.getTongSoHoaDon(tuNgay, denNgay);
        BigDecimal trungBinh = BigDecimal.ZERO;
        if (tongSoHoaDon > 0) {
            trungBinh = tongDoanhThu.divide(new BigDecimal(tongSoHoaDon), 2, RoundingMode.HALF_UP);
        }

        lblGiaTriDoanhThu.setText(currencyFormat.format(tongDoanhThu));
        lblGiaTriSoHoaDon.setText(numberFormat.format(tongSoHoaDon));
        lblGiaTriTrungBinh.setText(currencyFormat.format(trungBinh));

        Map<Date, BigDecimal> dataChart = hoaDonDAO.getDoanhThuTheoNgay(tuNgay, denNgay);
        capNhatBieuDo(dataChart, tuNgay, denNgay);

        danhSachHoaDonDayDu = hoaDonDAO.getDanhSachHoaDon(tuNgay, denNgay); 
        capNhatBang(danhSachHoaDonDayDu);
    }

    // Cập nhật dữ liệu cho biểu đồ
    private void capNhatBieuDo(Map<Date, BigDecimal> data, Date tuNgay, Date denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        if (data != null && !data.isEmpty()) {
            TreeMap<Date, BigDecimal> sortedData = new TreeMap<>(data);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
            
            for (Map.Entry<Date, BigDecimal> entry : sortedData.entrySet()) {
                dataset.addValue(entry.getValue(), "Doanh thu", sdf.format(entry.getKey()));
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Biểu đồ biến động doanh thu",
                "Thời gian", "Doanh thu (VNĐ)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        lineChart.setBackgroundPaint(MAU_NEN_TAB);
        lineChart.getTitle().setPaint(MAU_CHU_CHUNG);
        
        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(MAU_NEN_INPUT);
        plot.setDomainGridlinePaint(MAU_THANH_TIM_KIEM);
        plot.setRangeGridlinePaint(MAU_THANH_TIM_KIEM);
        plot.setOutlineVisible(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelPaint(MAU_CHU_CHUNG);
        domainAxis.setTickLabelPaint(MAU_CHU_CHUNG);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelPaint(MAU_CHU_CHUNG);
        rangeAxis.setTickLabelPaint(MAU_CHU_CHUNG);
        NumberAxis yAxis = (NumberAxis) rangeAxis;
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setNumberFormatOverride(numberFormat);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, MAU_DOANH_THU);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);

        chartPanel.setChart(lineChart);
        panelBieuDoContainer.revalidate();
        panelBieuDoContainer.repaint();
    }

    // Cập nhật dữ liệu cho bảng
    private void capNhatBang(List<HoaDon> list) {
        modelThongKe.setRowCount(0);
        if (list == null) return;
        
        int stt = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (HoaDon hd : list) {
            String nhanVien = (hd.getMaNhanVien() != null) ? hd.getMaNhanVien() : "N/A"; 
            String khachHang = (hd.getMaKhachHang() != null) ? hd.getMaKhachHang() : "Khách lẻ";
            
            BigDecimal tongTien = hoaDonDAO.tinhTongTienCuaHoaDon(hd.getMaHoaDon()); 

            modelThongKe.addRow(new Object[]{
                stt++,
                hd.getMaHoaDon(),
                sdf.format(hd.getNgayLapHoaDon()),
                khachHang,
                nhanVien,
                currencyFormat.format(tongTien)
            });
        }
    }

    // Xuất file Excel từ dữ liệu thống kê
    private void xuatFileExcel() {
        if (danhSachHoaDonDayDu == null || danhSachHoaDonDayDu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất file Excel.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));        
        fileChooser.setSelectedFile(new File("ThongKeHoaDon.xlsx"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(fileToSave)) {

                Sheet sheet = workbook.createSheet("ThongKeHoaDon");

                CellStyle titleStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                
                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);

                Row rowTitle = sheet.createRow(0);
                Cell cellTitle = rowTitle.createCell(0);
                cellTitle.setCellValue("BÁO CÁO THỐNG KÊ HÓA ĐƠN");
                cellTitle.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

                Row rowTime = sheet.createRow(1);
                String thoiGianStr;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String tuStr = (dcTuNgay.getDate() != null) ? sdf.format(dcTuNgay.getDate()) : "?";
                String denStr = (dcDenNgay.getDate() != null) ? sdf.format(dcDenNgay.getDate()) : "?";
                thoiGianStr = "Từ ngày: " + tuStr + " - Đến ngày: " + denStr;
                rowTime.createCell(0).setCellValue(thoiGianStr);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));

                String[] headers = {"STT", "Mã HĐ", "Ngày lập", "Khách hàng", "Nhân viên", "Tổng tiền"};
                Row headerRow = sheet.createRow(3);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowNum = 4;
                int stt = 1;
                SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                
                for (HoaDon hd : danhSachHoaDonDayDu) {
                    Row row = sheet.createRow(rowNum++);
                    
                    createCell(row, 0, stt++, dataStyle);
                    createCell(row, 1, hd.getMaHoaDon(), dataStyle);
                    createCell(row, 2, sdfTime.format(hd.getNgayLapHoaDon()), dataStyle);
                    createCell(row, 3, (hd.getMaKhachHang() != null ? hd.getMaKhachHang() : "Khách lẻ"), dataStyle);
                    createCell(row, 4, (hd.getMaNhanVien() != null ? hd.getMaNhanVien() : "N/A"), dataStyle);
                    
                    BigDecimal tong = hoaDonDAO.tinhTongTienCuaHoaDon(hd.getMaHoaDon());                    Cell cellTien = row.createCell(5);
                    cellTien.setCellValue(tong.doubleValue());
                    
                    CellStyle currencyStyle = workbook.createCellStyle();
                    currencyStyle.cloneStyleFrom(dataStyle);
                    currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
                    cellTien.setCellStyle(currencyStyle);
                }

                for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
                
                workbook.write(fos);
                JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + fileToSave.getAbsolutePath());

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Tạo ô dữ liệu Excel
    private void createCell(Row row, int col, Object value, CellStyle style) {
        Cell cell = row.createCell(col);
        if (value instanceof Integer) cell.setCellValue((Integer) value);
        else cell.setCellValue(value.toString());
        cell.setCellStyle(style);
    }

    // Thiết lập trạng thái active cho nút thời gian
    private void setButtonActive(JButton activeBtn) {
        JButton[] buttons = {btnHomNay, btnTuanNay, btnThangNay, btnNamNay};
        for (JButton btn : buttons) {
            if (btn == activeBtn) {
                btn.setBackground(MAU_XANH_LUC); 
            } else {
                btn.setBackground(MAU_THANH_TIM_KIEM);
            }
        }
    }

    // Lấy thời điểm bắt đầu ngày (00:00:00)
    private Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // Lấy thời điểm kết thúc ngày (23:59:59)
    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    // Sự kiện nút "Hôm nay"
    private void datThoiGianHomNay() {
        Date now = new Date();
        dcTuNgay.setDate(getStartOfDay(now));
        dcDenNgay.setDate(getEndOfDay(now));
        thucHienThongKe();
    }

    // Sự kiện nút "Tuần này"
    private void datThoiGianTuanNay() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        dcTuNgay.setDate(getStartOfDay(c.getTime()));
        c.add(Calendar.DATE, 6);
        dcDenNgay.setDate(getEndOfDay(c.getTime()));
        thucHienThongKe();
    }

    // Sự kiện nút "Tháng này"
    private void datThoiGianThangNay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        dcTuNgay.setDate(getStartOfDay(c.getTime()));
        dcDenNgay.setDate(getEndOfDay(new Date()));
        thucHienThongKe();
    }

    // Sự kiện nút "Năm nay"
    private void datThoiGianNamNay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 1);     
        dcTuNgay.setDate(getStartOfDay(c.getTime()));
        dcDenNgay.setDate(getEndOfDay(new Date()));      
        thucHienThongKe();
    }
}
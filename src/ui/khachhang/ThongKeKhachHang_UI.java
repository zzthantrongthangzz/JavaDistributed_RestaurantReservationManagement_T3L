package ui.khachhang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import connect.DBConnect;
import dao.KhachHang_DAO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ThongKeKhachHang_UI extends JPanel {

    private final Color MAU_XANH_LUC = new Color(76, 175, 80);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_XANH_LAM = new Color(30, 144, 255);
    private final Color MAU_CAM = new Color(255, 152, 0);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_KPI_VALUE = new Font("Segoe UI", Font.BOLD, 24);
    private KhachHang_DAO khachHangDAO;
    private JPanel panelChinh;
    private JDateChooser dcTuNgay, dcDenNgay;    
    private JTable tblTopKhach;
    private DefaultTableModel modelTopKhach;    
    private ChartPanel chartPanel;
    private JPanel panelBieuDoContainer;    
    private JButton btnHomNay, btnTuanNay, btnThangNay, btnNamNay, btnXuatExcel;
    private JLabel lblTongKhach, lblTongChiTieu, lblTongDiem;
    private JTabbedPane tabbedPane;    
    private final DecimalFormat currencyFormat = new DecimalFormat("###,### VNĐ");
    private final DecimalFormat numberFormat = new DecimalFormat("###,###");

    private final int LIMIT_LOAD_DATA = 10000; 
    private final int LIMIT_CHART_DATA = 10;

    // Constructor khởi tạo giao diện và các thành phần
    public ThongKeKhachHang_UI() {
        khachHangDAO = new KhachHang_DAO();
        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB);
        add(taoPanelTieuDe(), BorderLayout.NORTH);

        panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel panelDieuKhien = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
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
        panelDieuKhien.add(taoLabel("Từ ngày:"));
        dcTuNgay = taoDateChooser();
        dcTuNgay.addPropertyChangeListener("date", evt -> { 
            thucHienThongKe(); 
        });
        panelDieuKhien.add(dcTuNgay);
        
        panelDieuKhien.add(Box.createRigidArea(new Dimension(10, 0)));
        panelDieuKhien.add(taoLabel("Đến ngày:"));
        dcDenNgay = taoDateChooser();
        dcDenNgay.addPropertyChangeListener("date", evt -> { 
            thucHienThongKe(); 
        });
        panelDieuKhien.add(dcDenNgay);
        
        panelDieuKhien.add(Box.createRigidArea(new Dimension(10, 0)));
        btnXuatExcel = new JButton("Xuất File");
        setupButton(btnXuatExcel, MAU_XANH_LUC);
        btnXuatExcel.addActionListener(e -> xuatFileExcel());
        panelDieuKhien.add(btnXuatExcel);

        panelChinh.add(panelDieuKhien, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }
        });
        tabbedPane.setBackground(MAU_NEN_TAB);
        tabbedPane.setForeground(MAU_CHU_CHUNG);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel wrapperTab1 = new JPanel(new BorderLayout(0, 10));
        wrapperTab1.setBackground(MAU_NEN_TAB);
        
        JPanel panelKPI = new JPanel(new GridLayout(1, 3, 20, 0));
        panelKPI.setOpaque(false);
        panelKPI.setBorder(new EmptyBorder(0, 20, 0, 20));
        lblTongKhach = new JLabel("0", SwingConstants.CENTER);
        lblTongChiTieu = new JLabel("0 VNĐ", SwingConstants.CENTER);
        lblTongDiem = new JLabel("0", SwingConstants.CENTER);
        panelKPI.add(taoBoxKPI("TỔNG KHÁCH HÀNG", lblTongKhach, MAU_XANH_LAM));
        panelKPI.add(taoBoxKPI("TỔNG DOANH THU", lblTongChiTieu, MAU_XANH_LUC));
        panelKPI.add(taoBoxKPI("TỔNG ĐIỂM TÍCH LŨY", lblTongDiem, MAU_CAM));
        wrapperTab1.add(panelKPI, BorderLayout.NORTH);
        
        panelBieuDoContainer = new JPanel(new BorderLayout());
        panelBieuDoContainer.setBackground(MAU_NEN_TAB);
        chartPanel = new ChartPanel(null);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(MAU_NEN_TAB);
        panelBieuDoContainer.add(chartPanel, BorderLayout.CENTER);
        wrapperTab1.add(panelBieuDoContainer, BorderLayout.CENTER);
        
        tabbedPane.addTab("Biểu đồ Top Khách Hàng", wrapperTab1);

        JPanel panelBang = new JPanel(new BorderLayout());
        panelBang.setOpaque(false);
        
        String[] headers = {"Hạng", "Tên Khách Hàng", "Tổng Chi Tiêu", "Điểm Tích Lũy"};
        modelTopKhach = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblTopKhach = new JTable(modelTopKhach);
        tuyChinhBang(tblTopKhach);
        
        JScrollPane scrollPane = new JScrollPane(tblTopKhach);
        tuyChinhScrollBar(scrollPane);
        scrollPane.getViewport().setBackground(MAU_NEN_TAB);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelBang.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Bảng Xếp Hạng Chi Tiết", panelBang);

        panelChinh.add(tabbedPane, BorderLayout.CENTER);
        add(panelChinh, BorderLayout.CENTER);

        datThoiGianThangNay();
        setButtonActive(btnThangNay);
    }
    
    // Tạo panel chứa tiêu đề chính
    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 80)); 
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setBackground(MAU_NEN_TAB);
        
        JLabel lblTieuDe = new JLabel("THỐNG KÊ KHÁCH HÀNG");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTieuDe.setForeground(Color.WHITE);
       
        lblTieuDe.setBorder(new EmptyBorder(10, 30, 10, 30));        
        contentPanel.add(lblTieuDe);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    // Tạo các ô hiển thị chỉ số KPI
    private JPanel taoBoxKPI(String title, JLabel value, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(MAU_NEN_INPUT);
        p.setBorder(BorderFactory.createCompoundBorder(new LineBorder(color, 2), new EmptyBorder(10,10,10,10)));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14)); t.setForeground(MAU_CHU_CHUNG);
        value.setFont(FONT_KPI_VALUE); value.setForeground(color);
        p.add(t, BorderLayout.NORTH); p.add(value, BorderLayout.CENTER);
        return p;
    }
    
    // Cài đặt giao diện chung cho các nút bấm
    private void setupButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(MAU_CHU_CHUNG);
        btn.setBackground(bg != null ? bg : MAU_THANH_TIM_KIEM);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setFocusPainted(false);
    }
    
    // Tạo nút chọn khoảng thời gian
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
    
    // Đặt trạng thái active cho nút thời gian được chọn
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
    
    // Tạo nhãn label tiêu chuẩn
    private JLabel taoLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(MAU_CHU_CHUNG); return l;
    }

    // Tạo component chọn ngày tháng custom
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
    
    // Tùy chỉnh hiển thị bảng
    private void tuyChinhBang(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(60, 63, 65));
        table.setBackground(MAU_NEN_TAB);
        table.setForeground(MAU_CHU_CHUNG);
        table.setSelectionBackground(MAU_XANH_LAM.darker());
        table.setSelectionForeground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_INPUT);
        header.setForeground(MAU_CHU_CHUNG);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 40));
        
        table.setFillsViewportHeight(true);

        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(50);
        cm.getColumn(1).setPreferredWidth(200);
        cm.getColumn(2).setPreferredWidth(150);
        cm.getColumn(3).setPreferredWidth(100);
    }
    
    // Tùy chỉnh thanh cuộn
    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
        scrollPane.getHorizontalScrollBar().setBackground(MAU_NEN_INPUT);
    }
    
    // Thực hiện thống kê dữ liệu và cập nhật giao diện
    private void thucHienThongKe() {
    	Date tu = dcTuNgay.getDate();
        Date den = dcDenNgay.getDate();        
        if (tu == null || den == null) {
            return; 
        }
        if (tu.after(den)) {
            if (this.isShowing()) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc.");
            }
            return;
        }

        lblTongKhach.setText(numberFormat.format(khachHangDAO.getTongSoKhachHang(tu, den)));
        lblTongChiTieu.setText(currencyFormat.format(khachHangDAO.getTongChiTieuTatCaKhachHang(tu, den)));
        lblTongDiem.setText(numberFormat.format(khachHangDAO.getTongDiemTichLuy(tu, den)));

        List<Object[]> listFull = khachHangDAO.getTopKhachHangDayDu(LIMIT_LOAD_DATA, tu, den);
        
        capNhatBang(listFull);

        List<Object[]> listTopChart = new ArrayList<>();
        for (int i = 0; i < Math.min(LIMIT_CHART_DATA, listFull.size()); i++) {
            listTopChart.add(listFull.get(i));
        }
        capNhatBieuDoCot(listTopChart);
    }

    // Cập nhật biểu đồ cột
    private void capNhatBieuDoCot(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (listData != null) {
            for (Object[] obj : listData) {
                String tenKhach = (String) obj[0];
                BigDecimal chiTieu = (BigDecimal) obj[1];
                dataset.addValue(chiTieu, "Chi tiêu", tenKhach);
            }
        }


        JFreeChart barChart = ChartFactory.createBarChart(
            "Top Khách Hàng Chi Tiêu Cao Nhất",
            "Khách Hàng", "Mức Chi Tiêu (VNĐ)", 
            dataset, PlotOrientation.VERTICAL, false, true, false
        );

        barChart.setBackgroundPaint(MAU_NEN_TAB);
        barChart.getTitle().setPaint(MAU_CHU_CHUNG);
        
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(MAU_NEN_INPUT);
        plot.setDomainGridlinePaint(MAU_THANH_TIM_KIEM);
        plot.setRangeGridlinePaint(MAU_THANH_TIM_KIEM);
        plot.setOutlineVisible(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelPaint(MAU_CHU_CHUNG);
        domainAxis.setTickLabelPaint(MAU_CHU_CHUNG);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelPaint(MAU_CHU_CHUNG);
        rangeAxis.setTickLabelPaint(MAU_CHU_CHUNG);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, MAU_XANH_LUC);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter()); 
        renderer.setShadowVisible(false);

        chartPanel.setChart(barChart);
        panelBieuDoContainer.revalidate();
        panelBieuDoContainer.repaint();
    }

    // Cập nhật dữ liệu vào bảng
    private void capNhatBang(List<Object[]> listData) {
        modelTopKhach.setRowCount(0);
        int rank = 1;
        for (Object[] obj : listData) {
            String ten = (String) obj[0];
            BigDecimal tien = (BigDecimal) obj[1];
            int diem = (Integer) obj[2];
            modelTopKhach.addRow(new Object[]{
                rank++, ten, currencyFormat.format(tien), numberFormat.format(diem)
            });
        }
    }
    
    // Xuất báo cáo ra file Excel
    private void xuatFileExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu báo cáo khách hàng");
        fileChooser.setSelectedFile(new File("ThongKeKhachHang.xlsx"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
             File f = fileChooser.getSelectedFile();
             if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");
             
             try (Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(f)) {
                Sheet sheet = wb.createSheet("KhachHang");
                
                CellStyle titleStyle = wb.createCellStyle();
                org.apache.poi.ss.usermodel.Font titleFont = wb.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);
                
                CellStyle timeStyle = wb.createCellStyle();
                org.apache.poi.ss.usermodel.Font timeFont = wb.createFont();
                timeFont.setItalic(true);
                timeFont.setFontHeightInPoints((short) 12);
                timeStyle.setFont(timeFont);
                timeStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle headerStyle = wb.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle dataStyle = wb.createCellStyle();
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);

                Row rowTitle = sheet.createRow(0);
                Cell cellTitle = rowTitle.createCell(0);
                cellTitle.setCellValue("BÁO CÁO THỐNG KÊ KHÁCH HÀNG");
                cellTitle.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3)); 

                Row rowTime = sheet.createRow(1);
                Cell cellTime = rowTime.createCell(0);
                
                String timeString;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String s = (dcTuNgay.getDate() != null) ? sdf.format(dcTuNgay.getDate()) : "?";
                String e = (dcDenNgay.getDate() != null) ? sdf.format(dcDenNgay.getDate()) : "?";
                timeString = "Từ ngày: " + s + "  -  Đến ngày: " + e;
                cellTime.setCellValue(timeString);
                cellTime.setCellStyle(timeStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3)); 

                int startRow = 3;
                Row rowHeader = sheet.createRow(startRow);
                for(int i=0; i<modelTopKhach.getColumnCount(); i++) {
                    Cell cell = rowHeader.createCell(i);
                    cell.setCellValue(modelTopKhach.getColumnName(i));
                    cell.setCellStyle(headerStyle);
                }
                
                for(int i=0; i<modelTopKhach.getRowCount(); i++) {
                    Row r = sheet.createRow(startRow + 1 + i);
                    for(int j=0; j<modelTopKhach.getColumnCount(); j++) {
                        Cell cell = r.createCell(j);
                        Object val = modelTopKhach.getValueAt(i, j);
                        cell.setCellValue(val != null ? val.toString() : "");
                        cell.setCellStyle(dataStyle);
                    }
                }
                
                for(int i=0; i<modelTopKhach.getColumnCount(); i++) sheet.autoSizeColumn(i);

                wb.write(fos);
                JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + f.getAbsolutePath());
             } catch (Exception e) { 
                 e.printStackTrace(); 
                 JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + e.getMessage());
             }
        }
    }

    // Lấy thời điểm bắt đầu của ngày
    private Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // Lấy thời điểm kết thúc của ngày
    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    // Đặt khoảng thời gian lọc là hôm nay
    private void datThoiGianHomNay() {
        Date now = new Date();
        dcTuNgay.setDate(getStartOfDay(now));
        dcDenNgay.setDate(getEndOfDay(now));
        thucHienThongKe();
    }

    // Đặt khoảng thời gian lọc là tuần này
    private void datThoiGianTuanNay() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        dcTuNgay.setDate(getStartOfDay(c.getTime()));
        c.add(Calendar.DATE, 6);
        dcDenNgay.setDate(getEndOfDay(c.getTime()));
        thucHienThongKe();
    }

    // Đặt khoảng thời gian lọc là tháng này
    private void datThoiGianThangNay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        dcTuNgay.setDate(getStartOfDay(c.getTime()));
        dcDenNgay.setDate(getEndOfDay(new Date()));
        thucHienThongKe();
    }

    // Đặt khoảng thời gian lọc là năm nay
    private void datThoiGianNamNay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 1);     
        dcTuNgay.setDate(getStartOfDay(c.getTime()));
        dcDenNgay.setDate(getEndOfDay(new Date()));      
        thucHienThongKe();
    }
}
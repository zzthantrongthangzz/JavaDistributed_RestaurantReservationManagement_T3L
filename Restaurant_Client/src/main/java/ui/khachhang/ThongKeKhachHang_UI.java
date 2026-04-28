package ui.khachhang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import rmi_interfaces.IKhachHang_DAO;

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
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ThongKeKhachHang_UI extends JPanel {

    private final Color MAU_XANH_LUC = new Color(76, 175, 80);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_XANH_LAM = new Color(30, 144, 255);
    private final Color MAU_CAM = new Color(255, 152, 0);
    private final Color MAU_TIM = new Color(156, 39, 176);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font FONT_KPI_VALUE = new Font("Segoe UI", Font.BOLD, 24);

    private IKhachHang_DAO khachHangDAO;
    private JPanel panelChinh;
    private JDateChooser dcTuNgay, dcDenNgay;
    private JTable tblThongKe;
    private DefaultTableModel modelThongKe;
    private ChartPanel chartPanelTron, chartPanelCot;
    private JPanel panelBieuDoContainer;
    private JLabel lblTongSoKhach, lblTongChiTieu, lblTongDiemTichLuy;
    private JButton btnHomNay, btnTuanNay, btnThangNay, btnNamNay, btnXuatExcel;
    private JTabbedPane tabbedPane;
    private final DecimalFormat currencyFormat = new DecimalFormat("###,### VNĐ");
    private final DecimalFormat numberFormat = new DecimalFormat("###,###");
    private List<Object[]> danhSachThongKeDayDu;

    public ThongKeKhachHang_UI() {
        try {
            khachHangDAO = (IKhachHang_DAO) Naming.lookup("rmi://localhost:1099/KhachHang_DAO");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB);
        add(taoPanelTieuDe(), BorderLayout.NORTH);

        panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(10, 25, 20, 25));

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
        dcTuNgay.addPropertyChangeListener("date", (PropertyChangeEvent evt) -> {
            thucHienThongKe();
        });
        panelDieuKhien.add(dcTuNgay);

        panelDieuKhien.add(Box.createRigidArea(new Dimension(10, 0)));
        panelDieuKhien.add(taoLabel("Đến ngày:"));
        dcDenNgay = taoDateChooser();
        dcDenNgay.addPropertyChangeListener("date", (PropertyChangeEvent evt) -> {
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
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {}
        });
        tabbedPane.setBackground(MAU_NEN_TAB);
        tabbedPane.setForeground(MAU_CHU_CHUNG);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel wrapperTab1 = new JPanel(new BorderLayout(0, 10));
        wrapperTab1.setBackground(MAU_NEN_TAB);

        JPanel panelKPI = new JPanel(new GridLayout(1, 3, 20, 0));
        panelKPI.setOpaque(false);
        panelKPI.setBorder(new EmptyBorder(0, 10, 0, 10));

        lblTongSoKhach = new JLabel("0", SwingConstants.CENTER);
        lblTongChiTieu = new JLabel("0 VNĐ", SwingConstants.CENTER);
        lblTongDiemTichLuy = new JLabel("0", SwingConstants.CENTER);

        panelKPI.add(taoBoxKPI("KHÁCH HÀNG MỚI", lblTongSoKhach, MAU_XANH_LAM));
        panelKPI.add(taoBoxKPI("TỔNG CHI TIÊU", lblTongChiTieu, MAU_CAM));
        panelKPI.add(taoBoxKPI("ĐIỂM TÍCH LŨY", lblTongDiemTichLuy, MAU_TIM));

        wrapperTab1.add(panelKPI, BorderLayout.NORTH);

        panelBieuDoContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        panelBieuDoContainer.setBackground(MAU_NEN_TAB);
        panelBieuDoContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        chartPanelTron = new ChartPanel(null);
        chartPanelTron.setOpaque(false);
        chartPanelTron.setBackground(MAU_NEN_TAB);
        panelBieuDoContainer.add(chartPanelTron);

        chartPanelCot = new ChartPanel(null);
        chartPanelCot.setOpaque(false);
        chartPanelCot.setBackground(MAU_NEN_TAB);
        panelBieuDoContainer.add(chartPanelCot);

        wrapperTab1.add(panelBieuDoContainer, BorderLayout.CENTER);
        tabbedPane.addTab("Tổng quan & Biểu đồ", wrapperTab1);

        JPanel panelBang = new JPanel(new BorderLayout());
        panelBang.setOpaque(false);

        String[] headers = {"STT", "Mã KH", "Họ Tên", "Số Điện Thoại", "Tổng chi tiêu", "Lượt mua", "Điểm tích lũy"};
        modelThongKe = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblThongKe = new JTable(modelThongKe);
        tuyChinhBang(tblThongKe);

        JScrollPane scrollPane = new JScrollPane(tblThongKe);
        tuyChinhScrollBar(scrollPane);
        scrollPane.getViewport().setBackground(MAU_NEN_TAB);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelBang.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Danh sách chi tiết", panelBang);

        panelChinh.add(tabbedPane, BorderLayout.CENTER);
        add(panelChinh, BorderLayout.CENTER);

        datThoiGianThangNay();
        setButtonActive(btnThangNay);
    }

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

    private JPanel taoBoxKPI(String title, JLabel valueLabel, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_INPUT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(MAU_CHU_CHUNG);

        valueLabel.setFont(FONT_KPI_VALUE);
        valueLabel.setForeground(color);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void setupButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(MAU_CHU_CHUNG);
        btn.setBackground(bg != null ? bg : MAU_THANH_TIM_KIEM);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setFocusPainted(false);
    }

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

    private JLabel taoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(MAU_CHU_CHUNG);
        return l;
    }

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

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(3).setPreferredWidth(150);
        columnModel.getColumn(4).setPreferredWidth(150);
        columnModel.getColumn(5).setPreferredWidth(100);
        columnModel.getColumn(6).setPreferredWidth(100);
    }

    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
        scrollPane.getHorizontalScrollBar().setBackground(MAU_NEN_INPUT);
    }

    private void thucHienThongKe() {
        if (khachHangDAO == null) return;
        Date tuNgay = dcTuNgay.getDate();
        Date denNgay = dcDenNgay.getDate();
        if (tuNgay == null || denNgay == null) return;
        if (tuNgay.after(denNgay)) {
            if (this.isShowing()) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                int tongKhach = khachHangDAO.getTongSoKhachHang(tuNgay, denNgay);
                BigDecimal tongChi = khachHangDAO.getTongChiTieuTatCaKhachHang(tuNgay, denNgay);
                int tongDiem = khachHangDAO.getTongDiemTichLuy(tuNgay, denNgay);
                Map<String, Integer> gioiTinh = khachHangDAO.getSoLuongKhachHangTheoGioiTinh();
                List<Object[]> dsDayDu = khachHangDAO.getTopKhachHangDayDu(100, tuNgay, denNgay);
                if (dsDayDu == null) dsDayDu = new ArrayList<>();
                return new Object[]{tongKhach, tongChi, tongDiem, gioiTinh, dsDayDu};
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] result = get();
                    int tongKhach = (Integer) result[0];
                    BigDecimal tongChi = (BigDecimal) result[1];
                    int tongDiem = (Integer) result[2];
                    Map<String, Integer> gioiTinh = (Map<String, Integer>) result[3];
                    danhSachThongKeDayDu = (List<Object[]>) result[4];

                    lblTongSoKhach.setText(numberFormat.format(tongKhach));
                    lblTongChiTieu.setText(currencyFormat.format(tongChi));
                    lblTongDiemTichLuy.setText(numberFormat.format(tongDiem));

                    capNhatBieuDoTron(gioiTinh);
                    capNhatBang(danhSachThongKeDayDu);

                    List<Object[]> dsTop10 = danhSachThongKeDayDu.stream().limit(10).collect(Collectors.toList());
                    capNhatBieuDoCot(dsTop10);
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void capNhatBieuDoTron(Map<String, Integer> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (data != null && !data.isEmpty()) {
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Tỷ Lệ Khách Hàng Theo Giới Tính",
                dataset, true, true, false
        );
        pieChart.setBackgroundPaint(MAU_NEN_TAB);
        pieChart.getTitle().setPaint(MAU_CHU_CHUNG);
        pieChart.getLegend().setBackgroundPaint(MAU_NEN_TAB);
        pieChart.getLegend().setItemPaint(MAU_CHU_CHUNG);

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(MAU_NEN_INPUT);
        plot.setOutlineVisible(false);
        plot.setSectionPaint("Nam", MAU_XANH_LAM);
        plot.setSectionPaint("Nữ", MAU_TIM);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        plot.setLabelBackgroundPaint(new Color(220, 220, 220));

        chartPanelTron.setChart(pieChart);
    }

    private void capNhatBieuDoCot(List<Object[]> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (data != null && !data.isEmpty()) {
            for (Object[] row : data) {
                String tenKH = (String) row[1];
                BigDecimal chiTieu = (BigDecimal) row[3];
                dataset.addValue(chiTieu, "Chi tiêu", tenKH);
            }
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Top 10 Khách Hàng Chi Tiêu Cao Nhất",
                "Khách hàng", "Tổng chi tiêu (VNĐ)",
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
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelPaint(MAU_CHU_CHUNG);
        rangeAxis.setTickLabelPaint(MAU_CHU_CHUNG);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, MAU_CAM);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);

        chartPanelCot.setChart(barChart);
    }

    private void capNhatBang(List<Object[]> list) {
        modelThongKe.setRowCount(0);
        int stt = 1;
        for (Object[] row : list) {
            String maKH = (String) row[0];
            String tenKH = (String) row[1];
            String sdt = (String) row[2];
            BigDecimal tongChiTieu = (BigDecimal) row[3];
            int luotMua = (Integer) row[4];
            int diemTichLuy = (Integer) row[5];

            modelThongKe.addRow(new Object[]{
                    stt++, maKH, tenKH, sdt,
                    currencyFormat.format(tongChiTieu),
                    luotMua, numberFormat.format(diemTichLuy)
            });
        }
    }

    private void xuatFileExcel() {
        if (danhSachThongKeDayDu == null || danhSachThongKeDayDu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất file Excel.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

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
                org.apache.poi.ss.usermodel.Font font = wb.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);

                CellStyle dataStyle = wb.createCellStyle();
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);

                Row rowTitle = sheet.createRow(0);
                Cell cellTitle = rowTitle.createCell(0);
                cellTitle.setCellValue("BÁO CÁO THỐNG KÊ KHÁCH HÀNG");
                cellTitle.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

                Row rowTime = sheet.createRow(1);
                Cell cellTime = rowTime.createCell(0);
                String timeString;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String s = (dcTuNgay.getDate() != null) ? sdf.format(dcTuNgay.getDate()) : "?";
                String e = (dcDenNgay.getDate() != null) ? sdf.format(dcDenNgay.getDate()) : "?";
                timeString = "Từ ngày: " + s + "  -  Đến ngày: " + e;

                cellTime.setCellValue(timeString);
                cellTime.setCellStyle(timeStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

                int startRow = 3;
                Row rowHeader = sheet.createRow(startRow);
                for(int i=0; i<modelThongKe.getColumnCount(); i++) {
                    Cell cell = rowHeader.createCell(i);
                    cell.setCellValue(modelThongKe.getColumnName(i));
                    cell.setCellStyle(headerStyle);
                }

                for(int i=0; i<modelThongKe.getRowCount(); i++) {
                    Row r = sheet.createRow(startRow + 1 + i);
                    for(int j=0; j<modelThongKe.getColumnCount(); j++) {
                        Cell cell = r.createCell(j);
                        Object val = modelThongKe.getValueAt(i, j);
                        cell.setCellValue(val != null ? val.toString() : "");
                        cell.setCellStyle(dataStyle);
                    }
                }

                for(int i=0; i<modelThongKe.getColumnCount(); i++) sheet.autoSizeColumn(i);

                wb.write(fos);
                JOptionPane.showMessageDialog(this, "Xuất file thành công!\nĐã lưu tại: " + f.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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

    private Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private void datThoiGianHomNay() {
        Date now = new Date();
        dcTuNgay.setDate(getStartOfDay(now));
        dcDenNgay.setDate(getEndOfDay(now));
        thucHienThongKe();
    }

    private void datThoiGianTuanNay() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        dcTuNgay.setDate(getStartOfDay(c.getTime()));
        c.add(Calendar.DATE, 6);
        dcDenNgay.setDate(getEndOfDay(c.getTime()));
        thucHienThongKe();
    }

    private void datThoiGianThangNay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        dcTuNgay.setDate(getStartOfDay(c.getTime()));
        dcDenNgay.setDate(getEndOfDay(new Date()));
        thucHienThongKe();
    }

    private void datThoiGianNamNay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 1);
        dcTuNgay.setDate(getStartOfDay(c.getTime()));
        dcDenNgay.setDate(getEndOfDay(new Date()));
        thucHienThongKe();
    }
}
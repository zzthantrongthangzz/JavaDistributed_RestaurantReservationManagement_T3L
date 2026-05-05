package ui.khuyenmai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import connect.ConfigManager;
import rmi_interfaces.IKhuyenMai_Service;
import dto.ThongKeKhuyenMaiDTO;

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
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.rmi.Naming;

public class ThongKeKhuyenMai_UI extends JPanel {

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

    private IKhuyenMai_Service khuyenMaiService;
    private JPanel panelChinh;
    private JDateChooser dcTuNgay, dcDenNgay;
    private JTable tblThongKe;
    private DefaultTableModel modelThongKe;
    private ChartPanel chartPanel;
    private JPanel panelBieuDoContainer;
    private JLabel lblTongTienGiam, lblTongLuotDung;
    private JButton btnHomNay, btnTuanNay, btnThangNay, btnNamNay, btnXuatExcel;
    private JTabbedPane tabbedPane;
    private final DecimalFormat currencyFormat = new DecimalFormat("###,### VNĐ");
    private final DecimalFormat numberFormat = new DecimalFormat("###,###");

    public ThongKeKhuyenMai_UI() {
        try {
            String url = ConfigManager.getRmiUrl();
            khuyenMaiService = (IKhuyenMai_Service) Naming.lookup(url +"KhuyenMai_Service");
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

        JPanel panelKPI = new JPanel(new GridLayout(1, 2, 20, 0));
        panelKPI.setOpaque(false);
        panelKPI.setBorder(new EmptyBorder(0, 50, 0, 50));

        lblTongTienGiam = new JLabel("0 VNĐ", SwingConstants.CENTER);
        lblTongLuotDung = new JLabel("0", SwingConstants.CENTER);

        panelKPI.add(taoBoxKPI("TỔNG TIỀN GIẢM", lblTongTienGiam, MAU_CAM));
        panelKPI.add(taoBoxKPI("TỔNG LƯỢT SỬ DỤNG", lblTongLuotDung, MAU_XANH_LAM));

        wrapperTab1.add(panelKPI, BorderLayout.NORTH);

        panelBieuDoContainer = new JPanel(new BorderLayout());
        panelBieuDoContainer.setBackground(MAU_NEN_TAB);
        chartPanel = new ChartPanel(null);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(MAU_NEN_TAB);
        panelBieuDoContainer.add(chartPanel, BorderLayout.CENTER);

        wrapperTab1.add(panelBieuDoContainer, BorderLayout.CENTER);
        tabbedPane.addTab("Tổng quan & Biểu đồ", wrapperTab1);

        JPanel panelBang = new JPanel(new BorderLayout());
        panelBang.setOpaque(false);

        String[] headers = {"STT", "Mã KM", "Tên chương trình", "Số lượt sử dụng", "Tổng giá trị", "Ngày BĐ", "Ngày KT"};
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
        scrollPane.setViewportBorder(null);
        panelBang.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Danh sách khuyến mãi", panelBang);

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

        JLabel lblTieuDe = new JLabel("THỐNG KÊ KHUYẾN MÃI");
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
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(MAU_CHU_CHUNG); return l;
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
    }

    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
        scrollPane.getHorizontalScrollBar().setBackground(MAU_NEN_INPUT);
    }

    private void thucHienThongKe() {
        if (khuyenMaiService == null) return;
        final Date tuNgay = dcTuNgay.getDate();
        final Date denNgay = dcDenNgay.getDate();
        if (tuNgay == null || denNgay == null) return;
        if (tuNgay.after(denNgay)) {
            if (this.isShowing()) JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc.");
            return;
        }

        SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                BigDecimal tongTien = khuyenMaiService.getTongTienGiam(tuNgay, denNgay);
                int tongLuot = khuyenMaiService.getTongLuotSuDung(tuNgay, denNgay);
                Map<Date, BigDecimal> dataChart = khuyenMaiService.getTienGiamTheoNgay(tuNgay, denNgay);

                // Đã sử dụng List<ThongKeKhuyenMaiDTO> thay vì List<Object[]>
                List<ThongKeKhuyenMaiDTO> listKM = khuyenMaiService.getThongKeChiTietKhuyenMai(tuNgay, denNgay);

                return new Object[]{tongTien, tongLuot, dataChart, listKM};
            }
            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] result = get();
                    BigDecimal tongTien = (BigDecimal) result[0];
                    int tongLuot = (Integer) result[1];
                    Map<Date, BigDecimal> dataChart = (Map<Date, BigDecimal>) result[2];
                    List<ThongKeKhuyenMaiDTO> listKM = (List<ThongKeKhuyenMaiDTO>) result[3];

                    lblTongTienGiam.setText(currencyFormat.format(tongTien));
                    lblTongLuotDung.setText(numberFormat.format(tongLuot));
                    capNhatBieuDo(dataChart);
                    capNhatBang(listKM);
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private void capNhatBieuDo(Map<Date, BigDecimal> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (data != null && !data.isEmpty()) {
            List<Date> sortedDates = new ArrayList<>(data.keySet());
            Collections.sort(sortedDates);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
            for (Date d : sortedDates) {
                dataset.addValue(data.get(d), "Tiền giảm", sdf.format(d));
            }
        }
        JFreeChart barChart = ChartFactory.createBarChart(
                "Biến Động Tiền Giảm Giá Theo Ngày",
                "Ngày", "Số tiền (VNĐ)",
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
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, MAU_XANH_LUC);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setShadowVisible(false);

        chartPanel.setChart(barChart);
        panelBieuDoContainer.revalidate();
        panelBieuDoContainer.repaint();
    }

    // Đã thay đổi tham số truyền vào thành List<ThongKeKhuyenMaiDTO>
    private void capNhatBang(List<ThongKeKhuyenMaiDTO> list) {
        modelThongKe.setRowCount(0);
        int stt = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (ThongKeKhuyenMaiDTO dto : list) {
            modelThongKe.addRow(new Object[]{
                    stt++,
                    dto.getMaKhuyenMai(),
                    dto.getTenKhuyenMai(),
                    numberFormat.format(dto.getSoLuotSuDung()),
                    currencyFormat.format(dto.getTongTienGiam()),
                    sdf.format(dto.getNgayBatDau()),
                    sdf.format(dto.getNgayKetThuc())
            });
        }
    }

    private void xuatFileExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu báo cáo khuyến mãi");
        fileChooser.setSelectedFile(new File("ThongKeKhuyenMai.xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");

            try (Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(f)) {
                Sheet sheet = wb.createSheet("KhuyenMai");

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
                cellTitle.setCellValue("BÁO CÁO THỐNG KÊ KHUYẾN MÃI");
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
                JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + f.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage());
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
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999);
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
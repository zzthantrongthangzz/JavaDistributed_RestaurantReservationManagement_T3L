package ui.khachhang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import rmi_interfaces.IKhachHang_Service;
import dto.ThongKeKhachHangDTO;

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
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.rmi.Naming;

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

    private IKhachHang_Service khachHangDAO;
    private JPanel panelChinh;
    private JDateChooser dcTuNgay, dcDenNgay;
    private JTable tblKhachHangTop;
    private DefaultTableModel modelKhachHangTop;
    private ChartPanel chartPanelDoanhThu, chartPanelGioiTinh;
    private JPanel panelBieuDoContainer, panelBieuDoGioiTinh;

    private JLabel lblTongChiTieu, lblTongKhachHang, lblDiemTichLuy;
    private JButton btnHomNay, btnTuanNay, btnThangNay, btnNamNay, btnXuatExcel;
    private JTabbedPane tabbedPane;

    private final DecimalFormat currencyFormat = new DecimalFormat("###,### VNĐ");
    private final DecimalFormat numberFormat = new DecimalFormat("###,###");

    public ThongKeKhachHang_UI() {
        try {
            khachHangDAO = (IKhachHang_Service) Naming.lookup("rmi://localhost:1099/KhachHangService");
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

        btnHomNay = taoNutThoiGian("Hôm nay", e -> { datThoiGianHomNay(); setButtonActive(btnHomNay); });
        btnTuanNay = taoNutThoiGian("Tuần này", e -> { datThoiGianTuanNay(); setButtonActive(btnTuanNay); });
        btnThangNay = taoNutThoiGian("Tháng này", e -> { datThoiGianThangNay(); setButtonActive(btnThangNay); });
        btnNamNay = taoNutThoiGian("Năm nay", e -> { datThoiGianNamNay(); setButtonActive(btnNamNay); });

        panelDieuKhien.add(btnHomNay);
        panelDieuKhien.add(btnTuanNay);
        panelDieuKhien.add(btnThangNay);
        panelDieuKhien.add(btnNamNay);

        panelDieuKhien.add(Box.createRigidArea(new Dimension(20, 0)));
        panelDieuKhien.add(taoLabel("Từ ngày:"));
        dcTuNgay = taoDateChooser();
        dcTuNgay.addPropertyChangeListener("date", evt -> thucHienThongKe());
        panelDieuKhien.add(dcTuNgay);

        panelDieuKhien.add(Box.createRigidArea(new Dimension(10, 0)));
        panelDieuKhien.add(taoLabel("Đến ngày:"));
        dcDenNgay = taoDateChooser();
        dcDenNgay.addPropertyChangeListener("date", evt -> thucHienThongKe());
        panelDieuKhien.add(dcDenNgay);

        btnXuatExcel = new JButton("Xuất Excel");
        btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXuatExcel.setForeground(Color.WHITE);
        btnXuatExcel.setBackground(MAU_XANH_LUC);
        btnXuatExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXuatExcel.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THANH_TIM_KIEM, 1));
        btnXuatExcel.setPreferredSize(new Dimension(100, 40));
        btnXuatExcel.setFocusPainted(false);
        btnXuatExcel.addActionListener(e -> xuatExcelTopKhachHang());
        panelDieuKhien.add(Box.createRigidArea(new Dimension(10, 0)));
        panelDieuKhien.add(btnXuatExcel);

        panelChinh.add(panelDieuKhien, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(MAU_NEN_TAB);
        tabbedPane.setForeground(MAU_CHU_CHUNG);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel wrapperTab1 = new JPanel(new BorderLayout(0, 10));
        wrapperTab1.setBackground(MAU_NEN_TAB);

        JPanel panelKPI = new JPanel(new GridLayout(1, 3, 20, 0));
        panelKPI.setOpaque(false);
        lblTongChiTieu = new JLabel("0 VNĐ", SwingConstants.CENTER);
        lblTongKhachHang = new JLabel("0", SwingConstants.CENTER);
        lblDiemTichLuy = new JLabel("0", SwingConstants.CENTER);
        panelKPI.add(taoBoxKPI("TỔNG KHÁCH MỚI/CŨ", lblTongKhachHang, MAU_XANH_LAM));
        panelKPI.add(taoBoxKPI("TỔNG CHI TIÊU KHÁCH", lblTongChiTieu, MAU_CAM));
        panelKPI.add(taoBoxKPI("TỔNG ĐIỂM TÍCH LŨY", lblDiemTichLuy, MAU_TIM));

        wrapperTab1.add(panelKPI, BorderLayout.NORTH);

        JPanel wrapBieuDo = new JPanel(new GridLayout(1, 2, 20, 0));
        wrapBieuDo.setOpaque(false);
        panelBieuDoContainer = new JPanel(new BorderLayout());
        panelBieuDoContainer.setBackground(MAU_NEN_TAB);
        chartPanelDoanhThu = new ChartPanel(null);
        chartPanelDoanhThu.setOpaque(false);
        panelBieuDoContainer.add(chartPanelDoanhThu, BorderLayout.CENTER);
        wrapBieuDo.add(panelBieuDoContainer);

        panelBieuDoGioiTinh = new JPanel(new BorderLayout());
        panelBieuDoGioiTinh.setBackground(MAU_NEN_TAB);
        chartPanelGioiTinh = new ChartPanel(null);
        chartPanelGioiTinh.setOpaque(false);
        panelBieuDoGioiTinh.add(chartPanelGioiTinh, BorderLayout.CENTER);
        wrapBieuDo.add(panelBieuDoGioiTinh);

        wrapperTab1.add(wrapBieuDo, BorderLayout.CENTER);
        tabbedPane.addTab("Tổng quan", wrapperTab1);

        JPanel panelBang = new JPanel(new BorderLayout());
        panelBang.setOpaque(false);
        String[] headers = {"STT", "Tên Khách Hàng", "Tổng Chi Tiêu", "Điểm Hiện Tại"};
        modelKhachHangTop = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblKhachHangTop = new JTable(modelKhachHangTop);
        tuyChinhBang(tblKhachHangTop);
        JScrollPane scrollPane = new JScrollPane(tblKhachHangTop);
        scrollPane.getViewport().setBackground(MAU_NEN_TAB);
        panelBang.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Top Khách Hàng", panelBang);

        panelChinh.add(tabbedPane, BorderLayout.CENTER);
        add(panelChinh, BorderLayout.CENTER);

        datThoiGianThangNay();
        setButtonActive(btnThangNay);
    }

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_TAB);
        panel.setPreferredSize(new Dimension(0, 80));
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

    private JButton taoNutThoiGian(String text, java.awt.event.ActionListener l) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(MAU_THANH_TIM_KIEM);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 40));
        btn.addActionListener(l);
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
        dateChooser.setBackground(MAU_THANH_TIM_KIEM);
        dateChooser.setForeground(Color.WHITE);
        JTextFieldDateEditor editor = (JTextFieldDateEditor) dateChooser.getDateEditor().getUiComponent();
        editor.setBackground(MAU_THANH_TIM_KIEM);
        editor.setForeground(Color.WHITE);
        return dateChooser;
    }

    private JPanel taoBoxKPI(String title, JLabel valueLabel, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_INPUT);
        panel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(color, 2), new EmptyBorder(10, 10, 10, 10)));
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(MAU_CHU_CHUNG);
        valueLabel.setFont(FONT_KPI_VALUE);
        valueLabel.setForeground(color);
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
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
    }

    private void setButtonActive(JButton activeBtn) {
        JButton[] buttons = {btnHomNay, btnTuanNay, btnThangNay, btnNamNay};
        for (JButton btn : buttons) {
            btn.setBackground(btn == activeBtn ? MAU_XANH_LUC : MAU_THANH_TIM_KIEM);
        }
    }

    private Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance(); cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance(); cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private void datThoiGianHomNay() {
        Date now = new Date(); dcTuNgay.setDate(getStartOfDay(now)); dcDenNgay.setDate(getEndOfDay(now)); thucHienThongKe();
    }
    private void datThoiGianTuanNay() {
        Calendar c = Calendar.getInstance(); c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        dcTuNgay.setDate(getStartOfDay(c.getTime())); c.add(Calendar.DATE, 6); dcDenNgay.setDate(getEndOfDay(c.getTime())); thucHienThongKe();
    }
    private void datThoiGianThangNay() {
        Calendar c = Calendar.getInstance(); c.set(Calendar.DAY_OF_MONTH, 1);
        dcTuNgay.setDate(getStartOfDay(c.getTime())); dcDenNgay.setDate(getEndOfDay(new Date())); thucHienThongKe();
    }
    private void datThoiGianNamNay() {
        Calendar c = Calendar.getInstance(); c.set(Calendar.DAY_OF_YEAR, 1);
        dcTuNgay.setDate(getStartOfDay(c.getTime())); dcDenNgay.setDate(getEndOfDay(new Date())); thucHienThongKe();
    }

    private void thucHienThongKe() {
        if (khachHangDAO == null) return;
        final Date tuNgay = dcTuNgay.getDate();
        final Date denNgay = dcDenNgay.getDate();
        if (tuNgay == null || denNgay == null) return;

        SwingWorker<Object[], Void> worker = new SwingWorker<>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                return new Object[]{
                        khachHangDAO.getTongSoKhachHang(tuNgay, denNgay),
                        khachHangDAO.getTongChiTieuTatCaKhachHang(tuNgay, denNgay),
                        khachHangDAO.getTongDiemTichLuy(tuNgay, denNgay),
                        khachHangDAO.getTopKhachHangDayDu(10, tuNgay, denNgay),
                        khachHangDAO.getSoLuongKhachHangTheoGioiTinh()
                };
            }
            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] res = get();
                    lblTongKhachHang.setText(numberFormat.format(res[0]));
                    lblTongChiTieu.setText(currencyFormat.format(res[1]));
                    lblDiemTichLuy.setText(numberFormat.format(res[2]));

                    List<ThongKeKhachHangDTO> topKH = (List<ThongKeKhachHangDTO>) res[3];
                    capNhatBangTopKhachHang(topKH);
                    capNhatBieuDoTopKhachHang(topKH);
                    capNhatBieuDoGioiTinh((Map<String, Integer>) res[4]);
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    private void capNhatBangTopKhachHang(List<ThongKeKhachHangDTO> topKH) {
        modelKhachHangTop.setRowCount(0);
        int stt = 1;
        for (ThongKeKhachHangDTO dto : topKH) {
            modelKhachHangTop.addRow(new Object[]{
                    stt++, dto.getHoTen(), currencyFormat.format(dto.getTongChiTieu()), dto.getTichDiem()
            });
        }
    }

    private void capNhatBieuDoTopKhachHang(List<ThongKeKhachHangDTO> topKH) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ThongKeKhachHangDTO dto : topKH) {
            dataset.addValue(dto.getTongChiTieu(), "Chi Tiêu", dto.getHoTen());
        }
        JFreeChart barChart = ChartFactory.createBarChart("TOP KHÁCH HÀNG THEO CHI TIÊU", "Tên KH", "VNĐ", dataset, PlotOrientation.VERTICAL, false, true, false);
        barChart.setBackgroundPaint(MAU_NEN_TAB);
        barChart.getTitle().setPaint(Color.WHITE);
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(MAU_NEN_INPUT);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelPaint(Color.WHITE);
        domainAxis.setLabelPaint(Color.WHITE);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(Color.WHITE);
        rangeAxis.setLabelPaint(Color.WHITE);
        ((BarRenderer) plot.getRenderer()).setSeriesPaint(0, MAU_CAM);

        chartPanelDoanhThu.setChart(barChart);
        chartPanelDoanhThu.repaint();
    }

    private void capNhatBieuDoGioiTinh(Map<String, Integer> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (data != null) {
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }
        JFreeChart pieChart = ChartFactory.createPieChart("TỶ LỆ GIỚI TÍNH KHÁCH HÀNG", dataset, true, true, false);
        pieChart.setBackgroundPaint(MAU_NEN_TAB);
        pieChart.getTitle().setPaint(Color.WHITE);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(MAU_NEN_INPUT);
        plot.setSectionPaint("Nam", MAU_XANH_LAM);
        plot.setSectionPaint("Nữ", MAU_TIM);
        plot.setLabelPaint(Color.WHITE);
        chartPanelGioiTinh.setChart(pieChart);
        chartPanelGioiTinh.repaint();
    }

    private void xuatExcelTopKhachHang() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu báo cáo Top Khách Hàng");
        fileChooser.setSelectedFile(new File("TopKhachHang.xlsx"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");

            try (Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(f)) {
                Sheet sheet = wb.createSheet("TopKhachHang");
                Row rowTitle = sheet.createRow(0);
                Cell cellTitle = rowTitle.createCell(0);
                cellTitle.setCellValue("BÁO CÁO TOP KHÁCH HÀNG THEO CHI TIÊU");

                int startRow = 3;
                Row rowHeader = sheet.createRow(startRow);
                for (int i = 0; i < modelKhachHangTop.getColumnCount(); i++) {
                    rowHeader.createCell(i).setCellValue(modelKhachHangTop.getColumnName(i));
                }
                for (int i = 0; i < modelKhachHangTop.getRowCount(); i++) {
                    Row r = sheet.createRow(startRow + 1 + i);
                    for (int j = 0; j < modelKhachHangTop.getColumnCount(); j++) {
                        Object val = modelKhachHangTop.getValueAt(i, j);
                        r.createCell(j).setCellValue(val != null ? val.toString() : "");
                    }
                }
                wb.write(fos);
                JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xuất file!");
            }
        }
    }
}
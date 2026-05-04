package ui.hoadon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import rmi_interfaces.IHoaDon_Service;
import rmi_interfaces.IKhachHang_Service;
import entity.HoaDon;
import entity.KhachHang;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ThongKeHoaDon_UI extends JPanel {

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

    private IHoaDon_Service hoaDonService;
    private IKhachHang_Service khachHangService;
    private JPanel panelChinh;
    private JDateChooser dcTuNgay, dcDenNgay;
    private JTable tblThongKe;
    private DefaultTableModel modelThongKe;
    private ChartPanel chartPanel;
    private JPanel panelBieuDoContainer;
    private JLabel lblTongDoanhThu, lblTongSoHoaDon;
    private JButton btnHomNay, btnTuanNay, btnThangNay, btnNamNay, btnXuatExcel;
    private JTabbedPane tabbedPane;
    private final DecimalFormat currencyFormat = new DecimalFormat("###,### VNĐ");
    private final DecimalFormat numberFormat = new DecimalFormat("###,###");
    private List<HoaDon> danhSachThongKe;

    public ThongKeHoaDon_UI() {
        try {
            hoaDonService = (IHoaDon_Service) Naming.lookup("rmi://localhost:1099/HoaDon_Service");
            khachHangService = (IKhachHang_Service) Naming.lookup("rmi://localhost:1099/KhachHang_Service");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        dcTuNgay.addPropertyChangeListener("date", evt -> thucHienThongKe());
        panelDieuKhien.add(dcTuNgay);

        panelDieuKhien.add(Box.createRigidArea(new Dimension(10, 0)));
        panelDieuKhien.add(taoLabel("Đến ngày:"));
        dcDenNgay = taoDateChooser();
        dcDenNgay.addPropertyChangeListener("date", evt -> thucHienThongKe());
        panelDieuKhien.add(dcDenNgay);

        panelDieuKhien.add(Box.createRigidArea(new Dimension(10, 0)));
        btnXuatExcel = new JButton("Xuất File");
        setupButton(btnXuatExcel, MAU_XANH_LUC);
        btnXuatExcel.addActionListener(e -> xuatFileExcel());
        panelDieuKhien.add(btnXuatExcel);

        panelChinh.add(panelDieuKhien, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {}
        });
        tabbedPane.setBackground(MAU_NEN_TAB);
        tabbedPane.setForeground(MAU_CHU_CHUNG);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel wrapperTab1 = new JPanel(new BorderLayout(0, 10));
        wrapperTab1.setBackground(MAU_NEN_TAB);

        JPanel panelKPI = new JPanel(new GridLayout(1, 2, 20, 0));
        panelKPI.setOpaque(false);
        panelKPI.setBorder(new EmptyBorder(0, 50, 0, 50));

        lblTongDoanhThu = new JLabel("0 VNĐ", SwingConstants.CENTER);
        lblTongSoHoaDon = new JLabel("0", SwingConstants.CENTER);

        panelKPI.add(taoBoxKPI("TỔNG DOANH THU", lblTongDoanhThu, MAU_CAM));
        panelKPI.add(taoBoxKPI("TỔNG SỐ HÓA ĐƠN", lblTongSoHoaDon, MAU_XANH_LAM));

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

        String[] headers = {"STT", "Mã Hóa Đơn", "Ngày Lập", "Nhân Viên", "Khách Hàng", "Tổng Tiền"};
        modelThongKe = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblThongKe = new JTable(modelThongKe);
        tblThongKe.setRowHeight(30);
        tblThongKe.setBackground(MAU_NEN_TAB);
        tblThongKe.setForeground(MAU_CHU_CHUNG);

        JScrollPane scrollPane = new JScrollPane(tblThongKe);
        scrollPane.getViewport().setBackground(MAU_NEN_TAB);
        panelBang.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Danh sách chi tiết", panelBang);

        panelChinh.add(tabbedPane, BorderLayout.CENTER);
        add(panelChinh, BorderLayout.CENTER);

        datThoiGianThangNay();
        setButtonActive(btnThangNay);
    }

    private JPanel taoPanelTieuDe() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setBackground(MAU_NEN_TAB);
        JLabel lblTieuDe = new JLabel("THỐNG KÊ DOANH THU HÓA ĐƠN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTieuDe.setForeground(Color.WHITE);
        lblTieuDe.setBorder(new EmptyBorder(10, 30, 20, 30));
        panel.add(lblTieuDe);
        return panel;
    }

    private JPanel taoBoxKPI(String title, JLabel valueLabel, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_INPUT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color, 2), new EmptyBorder(10, 10, 10, 10)));
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
        btn.setPreferredSize(new Dimension(100, 40));
    }

    private JButton taoNutThoiGian(String text) {
        JButton btn = new JButton(text);
        setupButton(btn, null);
        if (text.equals("Hôm nay")) btn.addActionListener(e -> { datThoiGianHomNay(); setButtonActive(btn); });
        else if (text.equals("Tuần này")) btn.addActionListener(e -> { datThoiGianTuanNay(); setButtonActive(btn); });
        else if (text.equals("Tháng này")) btn.addActionListener(e -> { datThoiGianThangNay(); setButtonActive(btn); });
        else if (text.equals("Năm nay")) btn.addActionListener(e -> { datThoiGianNamNay(); setButtonActive(btn); });
        return btn;
    }

    private JLabel taoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(MAU_CHU_CHUNG);
        return l;
    }

    private JDateChooser taoDateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setPreferredSize(new Dimension(140, 40));
        dc.setDateFormatString("dd/MM/yyyy");
        dc.setBackground(MAU_THANH_TIM_KIEM);
        JTextFieldDateEditor editor = (JTextFieldDateEditor) dc.getDateEditor().getUiComponent();
        editor.setBackground(MAU_THANH_TIM_KIEM);
        editor.setForeground(Color.WHITE);
        return dc;
    }

    private void thucHienThongKe() {
        if (hoaDonService == null) return;
        Date tuNgay = dcTuNgay.getDate();
        Date denNgay = dcDenNgay.getDate();
        if (tuNgay == null || denNgay == null || tuNgay.after(denNgay)) return;

        SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                BigDecimal tongTien = hoaDonService.getTongDoanhThu(tuNgay, denNgay);
                int tongSL = hoaDonService.getTongSoHoaDon(tuNgay, denNgay);
                Map<Date, BigDecimal> chartData = hoaDonService.getDoanhThuTheoNgay(tuNgay, denNgay);
                List<HoaDon> listDetail = hoaDonService.getDanhSachHoaDon(tuNgay, denNgay);

                Map<String, String> tenKHMap = new HashMap<>();
                if (listDetail != null) {
                    for (HoaDon hd : listDetail) {
                        if (hd.getMaKhachHang() != null && !hd.getMaKhachHang().isEmpty() && !tenKHMap.containsKey(hd.getMaKhachHang())) {
                            List<KhachHang> khs = khachHangService.timKiemTheoMa(hd.getMaKhachHang());
                            if (khs != null && !khs.isEmpty()) {
                                tenKHMap.put(hd.getMaKhachHang(), khs.get(0).getHoTen());
                            } else {
                                tenKHMap.put(hd.getMaKhachHang(), "Khách lẻ");
                            }
                        }
                    }
                }

                return new Object[]{tongTien, tongSL, chartData, listDetail, tenKHMap};
            }
            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] result = get();
                    lblTongDoanhThu.setText(currencyFormat.format((BigDecimal) result[0]));
                    lblTongSoHoaDon.setText(numberFormat.format((Integer) result[1]));
                    capNhatBieuDo((Map<Date, BigDecimal>) result[2]);
                    capNhatBang((List<HoaDon>) result[3], (Map<String, String>) result[4]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                dataset.addValue(data.get(d), "Doanh thu", sdf.format(d));
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Biến Động Doanh Thu Hóa Đơn Theo Ngày", "Ngày", "Doanh thu (VNĐ)",
                dataset, PlotOrientation.VERTICAL, false, true, false
        );
        lineChart.setBackgroundPaint(MAU_NEN_TAB);
        lineChart.getTitle().setPaint(MAU_CHU_CHUNG);
        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(MAU_NEN_INPUT);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, MAU_CAM);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        plot.setRenderer(renderer);

        chartPanel.setChart(lineChart);
        panelBieuDoContainer.revalidate();
        panelBieuDoContainer.repaint();
    }

    private void capNhatBang(List<HoaDon> list, Map<String, String> tenKHMap) {
        modelThongKe.setRowCount(0);
        danhSachThongKe = list;
        int stt = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (HoaDon hd : list) {
            String maNV = hd.getMaNhanVien() != null ? hd.getMaNhanVien() : "";

            String tenKH = "Khách lẻ";
            if (hd.getMaKhachHang() != null && tenKHMap.containsKey(hd.getMaKhachHang())) {
                tenKH = tenKHMap.get(hd.getMaKhachHang());
            }

            double tongTien = (hd.getSoTienKhachTra() != null && hd.getSoTienThoi() != null) ?
                    hd.getSoTienKhachTra().doubleValue() - hd.getSoTienThoi().doubleValue() : 0;

            modelThongKe.addRow(new Object[]{
                    stt++, hd.getMaHoaDon(),
                    hd.getNgayLapHoaDon() != null ? sdf.format(hd.getNgayLapHoaDon()) : "",
                    maNV, tenKH, currencyFormat.format(tongTien)
            });
        }
    }

    private void xuatFileExcel() {
        if (danhSachThongKe == null || danhSachThongKe.isEmpty()) return;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("ThongKeHoaDon.xlsx"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");
            try (Workbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(f)) {
                Sheet sheet = wb.createSheet("DoanhThu");

                CellStyle headerStyle = wb.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = wb.createFont();
                font.setBold(true);
                headerStyle.setFont(font);

                Row rowHeader = sheet.createRow(0);
                for(int i=0; i<modelThongKe.getColumnCount(); i++) {
                    Cell cell = rowHeader.createCell(i);
                    cell.setCellValue(modelThongKe.getColumnName(i));
                    cell.setCellStyle(headerStyle);
                }

                for(int i=0; i<modelThongKe.getRowCount(); i++) {
                    Row r = sheet.createRow(i + 1);
                    for(int j=0; j<modelThongKe.getColumnCount(); j++) {
                        Cell cell = r.createCell(j);
                        Object val = modelThongKe.getValueAt(i, j);
                        cell.setCellValue(val != null ? val.toString() : "");
                    }
                }
                for(int i=0; i<modelThongKe.getColumnCount(); i++) sheet.autoSizeColumn(i);

                wb.write(fos);
                JOptionPane.showMessageDialog(this, "Xuất thành công: " + f.getAbsolutePath());
            } catch (Exception e) {}
        }
    }

    private void setButtonActive(JButton activeBtn) {
        for (JButton btn : new JButton[]{btnHomNay, btnTuanNay, btnThangNay, btnNamNay}) {
            btn.setBackground(btn == activeBtn ? MAU_XANH_LUC : MAU_THANH_TIM_KIEM);
        }
    }

    private Date getStartOfDay(Date date) { Calendar c = Calendar.getInstance(); c.setTime(date); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); return c.getTime(); }
    private Date getEndOfDay(Date date) { Calendar c = Calendar.getInstance(); c.setTime(date); c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59); return c.getTime(); }
    private void datThoiGianHomNay() { Date now = new Date(); dcTuNgay.setDate(getStartOfDay(now)); dcDenNgay.setDate(getEndOfDay(now)); thucHienThongKe(); }
    private void datThoiGianTuanNay() { Calendar c = Calendar.getInstance(); c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); dcTuNgay.setDate(getStartOfDay(c.getTime())); c.add(Calendar.DATE, 6); dcDenNgay.setDate(getEndOfDay(c.getTime())); thucHienThongKe(); }
    private void datThoiGianThangNay() { Calendar c = Calendar.getInstance(); c.set(Calendar.DAY_OF_MONTH, 1); dcTuNgay.setDate(getStartOfDay(c.getTime())); dcDenNgay.setDate(getEndOfDay(new Date())); thucHienThongKe(); }
    private void datThoiGianNamNay() { Calendar c = Calendar.getInstance(); c.set(Calendar.DAY_OF_YEAR, 1); dcTuNgay.setDate(getStartOfDay(c.getTime())); dcDenNgay.setDate(getEndOfDay(new Date())); thucHienThongKe(); }
}
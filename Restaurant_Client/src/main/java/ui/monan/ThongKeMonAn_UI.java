package ui.monan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import rmi_interfaces.IMonAn_Service;
import dto.ThongKeMonAnDTO;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

public class ThongKeMonAn_UI extends JPanel {

    private final Color MAU_XANH_LUC = new Color(76, 175, 80);
    private final Color MAU_VIEN_THANH_TIM_KIEM = new Color(70, 72, 87);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_NEN_TAB = new Color(48, 52, 56);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_THANH_TIM_KIEM = new Color(60, 64, 68);
    private final Color MAU_XANH_LAM = new Color(30, 144, 255);
    private final Font FONT_TEXTFIELD = new Font("Segoe UI", Font.PLAIN, 15);

    private IMonAn_Service monAnService;
    private JPanel panelChinh;
    private JDateChooser dcTuNgay;
    private JDateChooser dcDenNgay;
    private JTable tblThongKe;
    private DefaultTableModel modelThongKe;
    private JButton btnHomNay;
    private JButton btnTuanNay;
    private JButton btnThangNay;
    private JButton btnNamNay;
    private ChartPanel chartPanel;
    private JPanel panelBieuDoContainer;
    private JTabbedPane tabbedPane;
    private JButton btnXuatExcel;

    // Đã thay đổi từ List<Object[]> sang List<ThongKeMonAnDTO>
    private List<ThongKeMonAnDTO> danhSachThongKeDayDu;

    public ThongKeMonAn_UI() {
        try {
            monAnService = (IMonAn_Service) Naming.lookup("rmi://localhost:1099/MonAn_Service");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        setLayout(new BorderLayout());
        setBackground(MAU_NEN_TAB);
        add(taoPanelTieuDe(), BorderLayout.NORTH);
        this.panelChinh = new JPanel(new BorderLayout(0, 15));
        panelChinh.setBackground(MAU_NEN_TAB);
        panelChinh.setBorder(new EmptyBorder(20, 25, 20, 25));

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

        panelDieuKhien.add(Box.createRigidArea(new Dimension(5, 0)));

        JLabel lblTuNgay = new JLabel("Từ ngày:");
        lblTuNgay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTuNgay.setForeground(MAU_CHU_CHUNG);

        dcTuNgay = taoDateChooser();
        dcTuNgay.addPropertyChangeListener("date", (PropertyChangeEvent evt) -> {
            if (evt.getNewValue() != null) thucHienThongKe();
        });

        JLabel lblDenNgay = new JLabel("Đến ngày:");
        lblDenNgay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDenNgay.setForeground(MAU_CHU_CHUNG);

        dcDenNgay = taoDateChooser();
        dcDenNgay.addPropertyChangeListener("date", (PropertyChangeEvent evt) -> {
            if (evt.getNewValue() != null) thucHienThongKe();
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
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {}
        });
        tabbedPane.setBackground(MAU_NEN_TAB);
        tabbedPane.setForeground(MAU_CHU_CHUNG);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panelBieuDoContainer = new JPanel(new BorderLayout());
        panelBieuDoContainer.setBackground(MAU_NEN_TAB);
        chartPanel = new ChartPanel(null);
        chartPanel.setBackground(MAU_NEN_TAB);
        panelBieuDoContainer.add(chartPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Biểu đồ", panelBieuDoContainer);

        JPanel panelBang = new JPanel(new BorderLayout());
        panelBang.setOpaque(false);
        String[] headers = {"STT", "Mã món", "Tên món", "Giá bán", "Tổng số lượng bán"};
        modelThongKe = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblThongKe = new JTable(modelThongKe);
        tuyChinhBang(tblThongKe);
        JScrollPane scrollPane = new JScrollPane(tblThongKe);
        tuyChinhScrollBar(scrollPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelBang.add(scrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Bảng chi tiết", panelBang);

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

        JLabel lblTieuDe = new JLabel("THỐNG KÊ MÓN ĂN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTieuDe.setForeground(Color.WHITE);

        lblTieuDe.setBorder(new EmptyBorder(10, 30, 10, 30));
        contentPanel.add(lblTieuDe);
        panel.add(contentPanel, BorderLayout.CENTER);

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
            public void mouseEntered(MouseEvent e) { if (btn.getBackground() != MAU_XANH_LUC) btn.setBackground(MAU_VIEN_THANH_TIM_KIEM); }
            public void mouseExited(MouseEvent e) { if (btn.getBackground() != MAU_XANH_LUC) btn.setBackground(MAU_THANH_TIM_KIEM); }
        });
        return btn;
    }

    private void setButtonActive(JButton activeBtn) {
        for (JButton btn : new JButton[]{btnHomNay, btnTuanNay, btnThangNay, btnNamNay}) {
            btn.setBackground(btn == activeBtn ? MAU_XANH_LUC : MAU_THANH_TIM_KIEM);
        }
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
                if (!Color.WHITE.equals(dateEditor.getForeground())) dateEditor.setForeground(Color.WHITE);
                if (!Color.WHITE.equals(dateEditor.getDisabledTextColor())) dateEditor.setDisabledTextColor(Color.WHITE);
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
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(MAU_XANH_LAM.darker());
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_INPUT);
        header.setForeground(MAU_CHU_CHUNG);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(3).setPreferredWidth(150);
        columnModel.getColumn(4).setPreferredWidth(150);
    }

    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setBackground(MAU_NEN_INPUT);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = new Color(100, 105, 120); this.trackColor = MAU_NEN_INPUT; }
            @Override protected JButton createDecreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected JButton createIncreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
        });
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
        scrollPane.getHorizontalScrollBar().setBackground(MAU_NEN_INPUT);
        scrollPane.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = new Color(100, 105, 120); this.trackColor = MAU_NEN_INPUT; }
            @Override protected JButton createDecreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected JButton createIncreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
        });
    }

    private void xuatFileExcel() {
        if (danhSachThongKeDayDu == null || danhSachThongKeDayDu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất file Excel.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File("ThongKeMonAn.xlsx"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
                fileToSave = new File(filePath);
            }

            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(fileToSave)) {

                Sheet sheet = workbook.createSheet("ThongKeMonAn");
                CellStyle titleStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
                titleFont.setBold(true); titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont); titleStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle timeStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font timeFont = workbook.createFont();
                timeFont.setItalic(true); timeFont.setFontHeightInPoints((short) 12);
                timeStyle.setFont(timeFont); timeStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle headerCellStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true); headerFont.setFontHeightInPoints((short) 13); headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerCellStyle.setFont(headerFont); headerCellStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
                headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); headerCellStyle.setBorderTop(BorderStyle.THIN);
                headerCellStyle.setBorderBottom(BorderStyle.THIN); headerCellStyle.setBorderLeft(BorderStyle.THIN); headerCellStyle.setBorderRight(BorderStyle.THIN);

                DataFormat format = workbook.createDataFormat();
                CellStyle currencyCellStyle = workbook.createCellStyle();
                currencyCellStyle.setDataFormat(format.getFormat("#,##0" + " VNĐ"));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date tu = dcTuNgay.getDate(); Date den = dcDenNgay.getDate();
                String tuStr = (tu != null) ? sdf.format(tu) : "N/A";
                String denStr = (den != null) ? sdf.format(den) : "N/A";
                String thoiGianStr = "Từ ngày: " + tuStr + " - Đến ngày: " + denStr;

                Row rowTitle = sheet.createRow(0);
                Cell cellTitle = rowTitle.createCell(0);
                cellTitle.setCellValue("THỐNG KÊ DOANH SỐ MÓN ĂN");
                cellTitle.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

                Row rowTime = sheet.createRow(1);
                Cell cellTime = rowTime.createCell(0);
                cellTime.setCellValue(thoiGianStr);
                cellTime.setCellStyle(timeStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

                String[] headers = {"STT", "Mã món", "Tên món", "Giá bán", "Tổng số lượng bán"};
                int headerRowNum = 3;
                Row headerRow = sheet.createRow(headerRowNum);

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerCellStyle);
                }

                int stt = 1; int rowNum = headerRowNum + 1;
                // Đổi đối tượng sang ThongKeMonAnDTO
                for (ThongKeMonAnDTO tk : danhSachThongKeDayDu) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(stt++);
                    row.createCell(1).setCellValue(tk.getMaMon());
                    row.createCell(2).setCellValue(tk.getTenMon());
                    Cell cellGia = row.createCell(3);
                    cellGia.setCellValue(tk.getGia());
                    cellGia.setCellStyle(currencyCellStyle);
                    row.createCell(4).setCellValue(tk.getTongSoLuong());
                }
                for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

                workbook.write(fos);
                JOptionPane.showMessageDialog(this, "Xuất file Excel thành công!\nĐã lưu tại: " + fileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xuất file Excel:\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void thucHienThongKe() {
        if (monAnService == null) return;
        Date tuNgay = dcTuNgay.getDate();
        Date denNgay = dcDenNgay.getDate();
        if (tuNgay == null || denNgay == null) return;
        if (tuNgay.after(denNgay)) {
            if (this.isShowing()) JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            modelThongKe.setRowCount(0);
            capNhatBieuDo(new ArrayList<>());
            if (danhSachThongKeDayDu != null) danhSachThongKeDayDu.clear();
            return;
        }

        // Đổi từ List<Object[]> sang List<ThongKeMonAnDTO>
        SwingWorker<List<ThongKeMonAnDTO>, Void> worker = new SwingWorker<List<ThongKeMonAnDTO>, Void>() {
            @Override protected List<ThongKeMonAnDTO> doInBackground() throws Exception {
                return monAnService.getThongKeMonAn(tuNgay, denNgay);
            }
            @Override protected void done() {
                try {
                    danhSachThongKeDayDu = get();
                    if (danhSachThongKeDayDu == null) danhSachThongKeDayDu = new ArrayList<>();
                    capNhatBang(danhSachThongKeDayDu);
                    List<ThongKeMonAnDTO> danhSachTop10 = danhSachThongKeDayDu.stream().limit(10).collect(Collectors.toList());
                    capNhatBieuDo(danhSachTop10);
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    private void capNhatBang(List<ThongKeMonAnDTO> danhSach) {
        modelThongKe.setRowCount(0);
        Locale vn = new Locale("vi", "VN");
        NumberFormat nf = NumberFormat.getCurrencyInstance(vn);
        int stt = 1;
        for (ThongKeMonAnDTO tk : danhSach) {
            modelThongKe.addRow(new Object[]{stt++, tk.getMaMon(), tk.getTenMon(), nf.format(tk.getGia()), tk.getTongSoLuong()});
        }
    }

    private void capNhatBieuDo(List<ThongKeMonAnDTO> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            chartPanel.setChart(null);
            panelBieuDoContainer.revalidate();
            panelBieuDoContainer.repaint();
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String series = "Số lượng";
        for (ThongKeMonAnDTO tk : danhSach) {
            dataset.addValue(tk.getTongSoLuong(), series, tk.getTenMon());
        }

        JFreeChart chart = ChartFactory.createBarChart("Biểu đồ Top 10 Món ăn Bán chạy", "Món ăn", "Số lượng", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(MAU_NEN_TAB);
        chart.getTitle().setPaint(MAU_CHU_CHUNG);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(MAU_NEN_INPUT);
        plot.setDomainGridlinePaint(MAU_THANH_TIM_KIEM);
        plot.setRangeGridlinePaint(MAU_THANH_TIM_KIEM);
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, MAU_XANH_LUC);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelPaint(MAU_CHU_CHUNG);
        domainAxis.setTickLabelPaint(MAU_CHU_CHUNG);
        domainAxis.setAxisLineVisible(false);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelPaint(MAU_CHU_CHUNG);
        rangeAxis.setTickLabelPaint(MAU_CHU_CHUNG);
        rangeAxis.setAxisLineVisible(false);
        NumberAxis yAxis = (NumberAxis) rangeAxis;
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        chartPanel.setChart(chart);
        panelBieuDoContainer.revalidate();
        panelBieuDoContainer.repaint();
    }

    private Date getStartOfDay(Date date) { Calendar cal = Calendar.getInstance(); cal.setTime(date); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0); return cal.getTime(); }
    private Date getEndOfDay(Date date) { Calendar cal = Calendar.getInstance(); cal.setTime(date); cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999); return cal.getTime(); }
    private void datThoiGianHomNay() { Date now = new Date(); dcTuNgay.setDate(getStartOfDay(now)); dcDenNgay.setDate(getEndOfDay(now)); thucHienThongKe(); }
    private void datThoiGianTuanNay() { Calendar c = Calendar.getInstance(); c.setFirstDayOfWeek(Calendar.MONDAY); c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); dcTuNgay.setDate(getStartOfDay(c.getTime())); c.add(Calendar.DATE, 6); dcDenNgay.setDate(getEndOfDay(c.getTime())); thucHienThongKe(); }
    private void datThoiGianThangNay() { Calendar c = Calendar.getInstance(); c.set(Calendar.DAY_OF_MONTH, 1); dcTuNgay.setDate(getStartOfDay(c.getTime())); dcDenNgay.setDate(getEndOfDay(new Date())); thucHienThongKe(); }
    private void datThoiGianNamNay() { Calendar c = Calendar.getInstance(); c.set(Calendar.DAY_OF_YEAR, 1); dcTuNgay.setDate(getStartOfDay(c.getTime())); dcDenNgay.setDate(getEndOfDay(new Date())); thucHienThongKe(); }
}
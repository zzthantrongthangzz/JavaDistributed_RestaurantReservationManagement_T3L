package ui.monan;

import org.apache.poi.ss.usermodel.Cell;
import rmi_interfaces.ILoaiMon_Service;
import rmi_interfaces.IMonAn_Service;
import entity.LoaiMon;
import entity.MonAn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ThemNhieuMonAn_UI extends JPanel {

    private final Color MAU_NEN_TAB = new Color(45, 49, 56);
    private final Color MAU_NEN_INPUT = new Color(45, 49, 56);
    private final Color MAU_CHU_CHUNG = Color.WHITE;
    private final Color MAU_XANH_LUC = new Color(76, 175, 80);
    private final Color MAU_XANH_DUONG = new Color(30, 144, 255);
    private final Color MAU_NEN_BANG = new Color(60, 64, 68);
    private final Color MAU_KE_BANG = new Color(85, 89, 93);
    private final Color MAU_CAM = new Color(255, 152, 0);
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);

    private JTable tblPreview;
    private DefaultTableModel modelPreview;
    private List<MonAn> listMonAnImport;

    private File thuMucChuaAnhNguon = null;
    private JLabel lblThuMucAnh;

    private IMonAn_Service monAnDAO;
    private ILoaiMon_Service loaiMonDAO;

    public ThemNhieuMonAn_UI() {
        try {
            monAnDAO = (IMonAn_Service) Naming.lookup("rmi://localhost:1099/MonAn_DAO");
            loaiMonDAO = (ILoaiMon_Service) Naming.lookup("rmi://localhost:1099/LoaiMon_DAO");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Máy chủ!", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }

        listMonAnImport = new ArrayList<>();

        setLayout(new BorderLayout(0, 15));
        setBackground(MAU_NEN_TAB);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setBackground(MAU_NEN_TAB);

        JLabel lblTitle = new JLabel("THÊM NHIỀU MÓN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(MAU_CHU_CHUNG);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        pnlTop.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlActionWrapper = new JPanel(new BorderLayout(0, 10));
        pnlActionWrapper.setBackground(MAU_NEN_TAB);

        JPanel pnlButtonRow = new JPanel(new BorderLayout());
        pnlButtonRow.setBackground(MAU_NEN_TAB);

        JPanel pnlLeftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlLeftControls.setBackground(MAU_NEN_TAB);
        pnlLeftControls.setBorder(new EmptyBorder(0, -15, 0, 0));

        JButton btnTaiMau = taoNut("Tải File Mẫu", MAU_CAM);
        btnTaiMau.addActionListener(e -> taiFileMau());

        JButton btnChonFolderAnh = taoNut("1. Chọn Thư Mục Ảnh", MAU_XANH_DUONG);
        btnChonFolderAnh.addActionListener(e -> chonThuMucAnh());

        JButton btnChonFile = taoNut("2. Chọn File Excel", MAU_XANH_DUONG);
        btnChonFile.addActionListener(e -> chonFileExcel());

        pnlLeftControls.add(btnTaiMau);
        pnlLeftControls.add(btnChonFolderAnh);
        pnlLeftControls.add(btnChonFile);

        JPanel pnlRightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRightControls.setBackground(MAU_NEN_TAB);

        JButton btnLamMoi = taoNut("Làm mới", MAU_XANH_DUONG);
        btnLamMoi.setPreferredSize(new Dimension(110, 45));
        btnLamMoi.addActionListener(e -> lamMoi());

        JButton btnLuu = taoNut("LƯU VÀO HỆ THỐNG", MAU_XANH_LUC);
        btnLuu.setPreferredSize(new Dimension(200, 45));
        btnLuu.addActionListener(e -> luuVaoDatabase());

        pnlRightControls.add(btnLamMoi);
        pnlRightControls.add(btnLuu);

        pnlButtonRow.add(pnlLeftControls, BorderLayout.WEST);
        pnlButtonRow.add(pnlRightControls, BorderLayout.EAST);

        JPanel pnlLabelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlLabelRow.setBackground(MAU_NEN_TAB);

        lblThuMucAnh = new JLabel("Chưa chọn thư mục ảnh (Sẽ dùng ảnh mặc định)");
        lblThuMucAnh.setForeground(Color.GRAY);
        lblThuMucAnh.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblThuMucAnh.setBorder(new EmptyBorder(0, 0, 0, 0));

        pnlLabelRow.add(lblThuMucAnh);

        pnlActionWrapper.add(pnlButtonRow, BorderLayout.NORTH);
        pnlActionWrapper.add(pnlLabelRow, BorderLayout.CENTER);

        pnlTop.add(pnlActionWrapper, BorderLayout.CENTER);

        add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"STT", "Tên Món", "Loại", "Giá", "Đơn Vị", "File Ảnh (Excel)", "Trạng Thái Ảnh", "Kiểm Tra"};
        modelPreview = new DefaultTableModel(cols, 0);
        tblPreview = new JTable(modelPreview);
        tuyChinhBang(tblPreview);
        JScrollPane scrollPane = new JScrollPane(tblPreview);
        tuyChinhScrollBar(scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(MAU_KE_BANG, 1));
        scrollPane.getViewport().setBackground(MAU_NEN_BANG);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void lamMoi() {
        listMonAnImport.clear();
        modelPreview.setRowCount(0);
        thuMucChuaAnhNguon = null;
        lblThuMucAnh.setText("Chưa chọn thư mục ảnh (Sẽ dùng ảnh mặc định)");
        lblThuMucAnh.setForeground(Color.GRAY);
        lblThuMucAnh.setToolTipText(null);
    }

    private void chonThuMucAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Chọn thư mục chứa các file ảnh món ăn");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            thuMucChuaAnhNguon = fileChooser.getSelectedFile();
            lblThuMucAnh.setText("Nguồn ảnh: " + thuMucChuaAnhNguon.getAbsolutePath());
            lblThuMucAnh.setForeground(Color.CYAN);
            lblThuMucAnh.setToolTipText(thuMucChuaAnhNguon.getAbsolutePath());
        }
    }

    private void taiFileMau() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Mau_Them_Mon_An_Co_Anh.xlsx"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("DanhSachMon");
                Row header = sheet.createRow(0);
                String[] headers = {"Tên Món", "Tên Loại", "Giá Bán", "Đơn Vị", "Mô Tả", "Tên File Ảnh"};

                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(style);
                    sheet.setColumnWidth(i, 5000);
                }

                Row row = sheet.createRow(1);
                row.createCell(0).setCellValue("Gà Rán");
                row.createCell(1).setCellValue("Món chính");
                row.createCell(2).setCellValue(35000);
                row.createCell(3).setCellValue("Phần");
                row.createCell(4).setCellValue("Gà rán giòn tan");
                row.createCell(5).setCellValue("ga_ran.jpg");

                FileOutputStream out = new FileOutputStream(fileChooser.getSelectedFile());
                workbook.write(out);
                out.close();
                JOptionPane.showMessageDialog(this, "Tải file mẫu thành công!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi tạo file: " + e.getMessage());
            }
        }
    }

    private void chonFileExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx", "xls"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            docFileExcel(file);
        }
    }

    private void docFileExcel(File file) {
        if (monAnDAO == null || loaiMonDAO == null) {
            JOptionPane.showMessageDialog(this, "Chưa kết nối đến máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        listMonAnImport.clear();
        modelPreview.setRowCount(0);

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<LoaiMon> dsLoaiDB = loaiMonDAO.docDanhSachLoaiMon();
            String currentMaxID = monAnDAO.sinhMaMonTuDong();
            int currentNum = Integer.parseInt(currentMaxID.substring(2));

            int stt = 1;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                try {
                    String tenMon = getCellValue(row.getCell(0));
                    String tenLoai = getCellValue(row.getCell(1));
                    String giaStr = getCellValue(row.getCell(2));
                    String donVi = getCellValue(row.getCell(3));
                    String moTa = getCellValue(row.getCell(4));
                    String tenFileAnh = getCellValue(row.getCell(5));

                    if (tenMon.isEmpty() || tenLoai.isEmpty() || giaStr.isEmpty()) continue;

                    double gia = Double.parseDouble(giaStr);

                    LoaiMon loaiChon = null;
                    for (LoaiMon lm : dsLoaiDB) {
                        if (lm.getTenLoai().equalsIgnoreCase(tenLoai.trim())) {
                            loaiChon = lm;
                            break;
                        }
                    }

                    String trangThaiKiemTra = "Hợp lệ";
                    String trangThaiAnh = "Mặc định";
                    String duongDanAnhCuoiCung = "/img/default_food.png";

                    if (loaiChon == null) {
                        trangThaiKiemTra = "Lỗi: Loại không tồn tại";
                    } else {
                        if (!tenFileAnh.isEmpty() && thuMucChuaAnhNguon != null) {
                            File fileAnhNguon = new File(thuMucChuaAnhNguon, tenFileAnh);
                            if (fileAnhNguon.exists()) {
                                trangThaiAnh = "Tìm thấy";
                                duongDanAnhCuoiCung = fileAnhNguon.getAbsolutePath();
                            } else {
                                trangThaiAnh = "Không thấy file";
                            }
                        } else if (!tenFileAnh.isEmpty() && thuMucChuaAnhNguon == null) {
                            trangThaiAnh = "Chưa chọn thư mục nguồn";
                        }

                        String maMonMoi = String.format("MM%06d", currentNum + (stt - 1));

                        MonAn monMoi = new MonAn(maMonMoi, tenMon, duongDanAnhCuoiCung, gia, "Đang kinh doanh", moTa, donVi, loaiChon);
                        listMonAnImport.add(monMoi);
                    }

                    modelPreview.addRow(new Object[]{
                            stt, tenMon, tenLoai, String.format("%,.0f", gia), donVi,
                            tenFileAnh, trangThaiAnh, trangThaiKiemTra
                    });
                    stt++;

                } catch (Exception ex) {
                    System.err.println("Lỗi dòng " + row.getRowNum());
                }
            }

            if (!listMonAnImport.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Đọc file thành công! Vui lòng kiểm tra kỹ trạng thái ảnh trước khi Lưu.");
            } else {
                JOptionPane.showMessageDialog(this, "File trống hoặc lỗi định dạng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (RemoteException re) {
            re.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối máy chủ: " + re.getMessage(), "Lỗi Mạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + e.getMessage());
        }
    }

    private boolean xuLyCopyAnhVaChuanHoaDuongDan() {
        try {
            File outputDir = new File("bin/img");
            if (!outputDir.exists()) outputDir.mkdirs();

            for (MonAn mon : listMonAnImport) {
                String pathHienTai = mon.getDuongDanAnh();

                if (pathHienTai != null && !pathHienTai.startsWith("/img/")) {
                    File sourceFile = new File(pathHienTai);
                    if (sourceFile.exists()) {
                        String extension = "";
                        int i = sourceFile.getName().lastIndexOf('.');
                        if (i > 0) extension = sourceFile.getName().substring(i);

                        String tenFileMoi = "mon_" + chuyenTenMonThanhTenFile(mon.getTenMon()) + extension;
                        File destFile = new File(outputDir, tenFileMoi);

                        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        mon.setDuongDanAnh("/img/" + tenFileMoi);
                    } else {
                        mon.setDuongDanAnh("/img/default_food.png");
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi copy ảnh: " + e.getMessage());
            return false;
        }
    }

    private void luuVaoDatabase() {
        if (listMonAnImport.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Danh sách trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hệ thống sẽ copy hình ảnh (nếu có) và lưu dữ liệu.\nTiếp tục?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (!xuLyCopyAnhVaChuanHoaDuongDan()) {
                return;
            }

            try {
                boolean ketQua = monAnDAO.themDanhSachMonAn(listMonAnImport);

                if (ketQua) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công " + listMonAnImport.size() + " món ăn!");
                    lamMoi();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi lưu Database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RemoteException re) {
                re.printStackTrace();
                JOptionPane.showMessageDialog(this, "Mất kết nối máy chủ khi lưu!", "Lỗi Mạng", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String chuyenTenMonThanhTenFile(String tenMon) {
        String result = tenMon.toLowerCase();
        result = result.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        result = result.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        result = result.replaceAll("[ìíịỉĩ]", "i");
        result = result.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        result = result.replaceAll("[ùúụủũưừứựửữ]", "u");
        result = result.replaceAll("[ỳýỵỷỹ]", "y");
        result = result.replaceAll("đ", "d");
        result = result.replaceAll("[^a-z0-9]", "");
        return result + "_" + System.currentTimeMillis();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long)cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private JButton taoNut(String text, Color mauNen) {
        JButton btn = new JButton(text);
        btn.setBackground(mauNen);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void tuyChinhBang(JTable table) {
        table.setFont(FONT_TEXT);
        table.setRowHeight(30);

        table.setGridColor(MAU_KE_BANG);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.setBackground(MAU_NEN_BANG);
        table.setForeground(MAU_CHU_CHUNG);
        table.setSelectionBackground(MAU_XANH_DUONG);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(MAU_NEN_INPUT);
        header.setForeground(MAU_CHU_CHUNG);
        header.setFont(FONT_HEADER);
        header.setPreferredSize(new Dimension(0, 40));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setFont(FONT_HEADER);
                label.setForeground(MAU_CHU_CHUNG);
                label.setBackground(MAU_NEN_INPUT);
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);

                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, MAU_KE_BANG),
                        new EmptyBorder(10, 5, 10, 5)
                ));

                return label;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
    }

    private void tuyChinhScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100);
                this.trackColor = MAU_NEN_INPUT;
            }
        });
    }
}
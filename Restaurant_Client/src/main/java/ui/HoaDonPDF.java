package ui;

import entity.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class HoaDonPDF {
    private static com.lowagie.text.Font fontTitle;
    private static com.lowagie.text.Font fontHeader;
    private static com.lowagie.text.Font fontNormal;
    private static com.lowagie.text.Font fontBold;
    private static String FONT_PATH = "/fonts/DejaVuSans.ttf";

    private static final DecimalFormat currencyFormatter = new DecimalFormat("#,##0");
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("HH:mm - dd/MM/yyyy");

    // Khối khởi tạo font chữ cho file PDF
    static {
        try {
            java.net.URL fontUrl = HoaDonPDF.class.getResource(FONT_PATH);
            if (fontUrl != null) {
                String fontPath = fontUrl.getPath();
                fontPath = java.net.URLDecoder.decode(fontPath, "UTF-8");

                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                fontTitle = new com.lowagie.text.Font(bf, 18, Font.BOLD);
                fontHeader = new com.lowagie.text.Font(bf, 12, Font.BOLD);
                fontNormal = new com.lowagie.text.Font(bf, 11, Font.ITALIC);
                fontBold = new com.lowagie.text.Font(bf, 11, Font.BOLD);
            } else {
                throw new IOException("Không tìm thấy file font: " + FONT_PATH);
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
            fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
            fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.ITALIC);
            fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD);
        }
    }

    // Xuất hóa đơn ra file PDF bao gồm thông tin chung, danh sách món và mã QR
    public static void xuatHoaDonPDF(HoaDon hoaDon, KhachHang khachHang, NhanVien nhanVien,
                                     List<BanAn> dsBan, Date gioVaoBan, Date gioThanhToan, DefaultTableModel tableModel,
                                     double tongCong, double thue, double tongThanhToan, double tienDaCoc, BigDecimal tienGiam,
                                     String linkQR) {
        try {
            String fileName = "HoaDon_" + hoaDon.getMaHoaDon() + "_" + System.currentTimeMillis() + ".pdf";
            File file = new File(fileName);

            // Tính toán chiều cao trang in
            float chieuCaoCoDinh = 600;
            if (linkQR != null && !linkQR.isEmpty()) {
                chieuCaoCoDinh += 180;
            }

            float chieuCaoMoiDong = 30;
            int soLuongMon = tableModel.getRowCount();
            float chieuCaoBangMonAn = soLuongMon * chieuCaoMoiDong;

            float tongChieuCao = chieuCaoCoDinh + chieuCaoBangMonAn;

            if (tongChieuCao < PageSize.A4.getHeight()) {
                tongChieuCao = PageSize.A4.getHeight();
            }

            Rectangle pageSize = new Rectangle(PageSize.A4.getWidth(), tongChieuCao);

            Document document = new Document(pageSize, 30, 30, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Tạo phần Header (Logo và thông tin nhà hàng)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3});

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            try {
                Image logo = Image.getInstance(HoaDonPDF.class.getResource("/IMG/t3LLogo_300px.png"));
                if (logo != null) {
                    logo.scaleToFit(80, 80);
                    logoCell.addElement(logo);
                } else {
                    logoCell.addElement(new Phrase("LOGO", fontBold));
                }
            } catch (Exception e) {
                logoCell.addElement(new Phrase("LOGO", fontBold));
            }
            headerTable.addCell(logoCell);

            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Paragraph title = new Paragraph("NHÀ HÀNG T3L", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            titleCell.addElement(title);

            Paragraph address = new Paragraph("12 Nguyễn Văn Bảo, phường Hạnh Thông, Gò Vấp, TP.HCM", fontNormal);
            address.setAlignment(Element.ALIGN_CENTER);
            titleCell.addElement(address);

            Paragraph slogan = new Paragraph("Nhà hàng T3L - Ăn ngon bất ngờ !", fontNormal);
            slogan.setAlignment(Element.ALIGN_CENTER);
            titleCell.addElement(slogan);

            headerTable.addCell(titleCell);
            document.add(headerTable);
            document.add(Chunk.NEWLINE);

            Paragraph hoaDonTitle = new Paragraph("HOÁ ĐƠN THANH TOÁN", fontTitle);
            hoaDonTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(hoaDonTitle);
            document.add(Chunk.NEWLINE);

            // Tạo phần thông tin chi tiết hóa đơn (Khách hàng, Nhân viên, Bàn...)
            PdfPTable infoContainer = new PdfPTable(2);
            infoContainer.setWidthPercentage(100);
            infoContainer.setWidths(new float[]{1, 1});
            infoContainer.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // Thông tin bên trái
            PdfPTable infoTableLeft = new PdfPTable(2);
            infoTableLeft.setWidthPercentage(100);
            infoTableLeft.setWidths(new float[]{1, 2});
            infoTableLeft.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            addInfoRow(infoTableLeft, "Mã hoá đơn:", hoaDon.getMaHoaDon());
            addInfoRow(infoTableLeft, "Khách hàng:", khachHang != null ? khachHang.getHoTen() : "Khách lẻ");
            addInfoRow(infoTableLeft, "Nhân viên lập:", nhanVien != null ? nhanVien.getHoTen() : "N/A");
            infoContainer.addCell(infoTableLeft);

            // Thông tin bên phải
            PdfPTable infoTableRight = new PdfPTable(2);
            infoTableRight.setWidthPercentage(100);
            infoTableRight.setWidths(new float[]{1.2f, 2});
            infoTableRight.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            String tenBanHienThi = "N/A";
            if (dsBan != null && !dsBan.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < dsBan.size(); i++) {
                    sb.append(dsBan.get(i).getTenBan());
                    if (i < dsBan.size() - 1) {
                        sb.append(", ");
                    }
                }
                tenBanHienThi = sb.toString();
            }
            addInfoRow(infoTableRight, "Tên bàn:", tenBanHienThi);
            addInfoRow(infoTableRight, "Giờ vào bàn:", gioVaoBan != null ? dateTimeFormatter.format(gioVaoBan) : "N/A");
            addInfoRow(infoTableRight, "Giờ thanh toán:", gioThanhToan != null ? dateTimeFormatter.format(gioThanhToan) : "N/A");
            infoContainer.addCell(infoTableRight);

            document.add(infoContainer);
            document.add(Chunk.NEWLINE);

            // Tạo bảng danh sách món ăn
            PdfPTable itemTable = new PdfPTable(6);
            itemTable.setWidthPercentage(100);
            itemTable.setWidths(new float[]{0.5f, 2, 1, 1, 1, 1.5f});

            addTableHeader(itemTable, "STT");
            addTableHeader(itemTable, "Tên món");
            addTableHeader(itemTable, "Giá");
            addTableHeader(itemTable, "Đơn vị");
            addTableHeader(itemTable, "Số lượng");
            addTableHeader(itemTable, "Thành tiền");

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                addTableCell(itemTable, tableModel.getValueAt(i, 0).toString(), Element.ALIGN_CENTER);
                addTableCell(itemTable, tableModel.getValueAt(i, 1).toString(), Element.ALIGN_LEFT);
                addTableCell(itemTable, tableModel.getValueAt(i, 2).toString(), Element.ALIGN_RIGHT);
                addTableCell(itemTable, tableModel.getValueAt(i, 3).toString(), Element.ALIGN_CENTER);
                addTableCell(itemTable, tableModel.getValueAt(i, 4).toString(), Element.ALIGN_CENTER);
                addTableCell(itemTable, tableModel.getValueAt(i, 5).toString(), Element.ALIGN_RIGHT);
            }

            document.add(itemTable);
            document.add(Chunk.NEWLINE);

            // Tạo bảng tổng tiền thanh toán
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{3, 1.5f});
            totalTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            addTotalRow(totalTable, "Tổng cộng:", currencyFormatter.format(tongCong) + " VND");
            addTotalRow(totalTable, "Thuế (VAT 10%):", currencyFormatter.format(thue) + " VND");

            if (tienDaCoc > 0) {
                addTotalRow(totalTable, "Tiền cọc trước (Đã thanh toán):",
                        currencyFormatter.format(tienDaCoc) + " VND");
            }

            if (tienGiam != null && tienGiam.compareTo(BigDecimal.ZERO) > 0) {
                addTotalRow(totalTable, "Tiền được giảm:", "-" + currencyFormatter.format(tienGiam) + " VND");
            }

            addTotalRow(totalTable, "Tổng thanh toán:", currencyFormatter.format(tongThanhToan) + " VND");
            addTotalRow(totalTable, "Tiền khách đưa:", currencyFormatter.format(tongThanhToan) + " VND");

            document.add(totalTable);
            document.add(Chunk.NEWLINE);

            // Lời cảm ơn và ghi chú
            Paragraph thanks = new Paragraph("Cảm ơn quý khách. Hẹn gặp lại !", fontBold);
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            Paragraph note1 = new Paragraph("Hoá đơn đã bao gồm thuế VAT.", fontNormal);
            note1.setAlignment(Element.ALIGN_CENTER);
            document.add(note1);

            Paragraph note2 = new Paragraph("*Thuế VAT cho dịch vụ ăn uống là 10%", fontNormal);
            note2.setAlignment(Element.ALIGN_CENTER);
            document.add(note2);

            // In mã QR thanh toán (nếu có)
            if (linkQR != null && !linkQR.isEmpty()) {
                document.add(Chunk.NEWLINE);

                PdfPTable qrTable = new PdfPTable(1);
                qrTable.setWidthPercentage(100);
                qrTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                qrTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

                try {
                    Image qrImage = Image.getInstance(new java.net.URL(linkQR));
                    qrImage.scaleToFit(240, 240);
                    qrImage.setAlignment(Element.ALIGN_CENTER);

                    PdfPCell qrCell = new PdfPCell(qrImage);
                    qrCell.setBorder(Rectangle.NO_BORDER);
                    qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    qrTable.addCell(qrCell);

                    PdfPCell textCell = new PdfPCell(new Phrase("Quét mã để thanh toán", fontNormal));
                    textCell.setBorder(Rectangle.NO_BORDER);
                    textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    textCell.setPaddingTop(5);
                    qrTable.addCell(textCell);

                    document.add(qrTable);
                } catch (Exception e) {
                    System.err.println("Không thể tải ảnh QR vào PDF: " + e.getMessage());
                    Paragraph errorQR = new Paragraph("(Không tải được mã QR do lỗi mạng)", fontNormal);
                    errorQR.setAlignment(Element.ALIGN_CENTER);
                    document.add(errorQR);
                }
            }

            document.close();
            writer.close();

            // Tự động mở file sau khi xuất xong
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(null, "Đã xuất file PDF: " + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Đã xảy ra lỗi khi xuất file PDF:\n" + e.getMessage(),
                    "Lỗi PDF",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thêm một dòng thông tin dạng label-value vào bảng
    private static void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, fontBold));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "", fontNormal));
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    // Thêm tiêu đề cho cột của bảng
    private static void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header, fontBold));
        cell.setBackgroundColor(new java.awt.Color(200, 200, 200));
        cell.setBorder(Rectangle.BOX);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    // Thêm nội dung vào ô của bảng với canh lề tùy chỉnh
    private static void addTableCell(PdfPTable table, String value, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value, fontNormal));
        cell.setBorder(Rectangle.BOX);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    // Thêm dòng tổng tiền vào bảng cuối hóa đơn
    private static void addTotalRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, fontBold));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, fontBold));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
}
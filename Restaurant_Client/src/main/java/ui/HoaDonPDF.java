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

    static {
        try {
            BaseFont bf = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            fontTitle = new com.lowagie.text.Font(bf, 18, com.lowagie.text.Font.BOLD);
            fontHeader = new com.lowagie.text.Font(bf, 12, com.lowagie.text.Font.BOLD);
            fontNormal = new com.lowagie.text.Font(bf, 10, com.lowagie.text.Font.NORMAL);
            fontBold = new com.lowagie.text.Font(bf, 10, com.lowagie.text.Font.BOLD);
        } catch (Exception e) {
            fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        }
    }

    public static void xuatHoaDon(HoaDon hd, List<ChiTietHoaDon> dsCTHD, String tenNV, String tenKH) {
        String fileName = "HoaDon_" + hd.getMaHoaDon() + ".pdf";
        Document document = new Document(PageSize.A5);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Paragraph title = new Paragraph("NHÀ HÀNG T3L", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subTitle = new Paragraph("HÓA ĐƠN THANH TOÁN", fontHeader);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitle);
            document.add(new Paragraph(" "));

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            addInfoRow(infoTable, "Mã hóa đơn:", hd.getMaHoaDon());
            addInfoRow(infoTable, "Ngày lập:", dateTimeFormatter.format(hd.getNgayLapHoaDon()));
            addInfoRow(infoTable, "Nhân viên:", tenNV);
            addInfoRow(infoTable, "Khách hàng:", tenKH);
            document.add(infoTable);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{40, 15, 20, 25});
            addTableHeader(table, "Tên món");
            addTableHeader(table, "SL");
            addTableHeader(table, "Đơn giá");
            addTableHeader(table, "Thành tiền");

            BigDecimal tongTien = BigDecimal.ZERO;
            for (ChiTietHoaDon ct : dsCTHD) {
                addTableCell(table, ct.getMaMon(), Element.ALIGN_LEFT);
                addTableCell(table, String.valueOf(ct.getSoLuong()), Element.ALIGN_CENTER);
                addTableCell(table, currencyFormatter.format(ct.getDonGia()), Element.ALIGN_RIGHT);
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));
                addTableCell(table, currencyFormatter.format(thanhTien), Element.ALIGN_RIGHT);
                tongTien = tongTien.add(thanhTien);
            }
            document.add(table);

            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            addTotalRow(totalTable, "Tổng cộng:", currencyFormatter.format(tongTien) + " VND");
            addTotalRow(totalTable, "Thuế (VAT):", hd.getThue().toString() + "%");
            addTotalRow(totalTable, "Tiền khách trả:", currencyFormatter.format(hd.getSoTienKhachTra()) + " VND");
            addTotalRow(totalTable, "Tiền thối:", currencyFormatter.format(hd.getSoTienThoi()) + " VND");
            document.add(totalTable);

            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Cảm ơn Quý khách! Hẹn gặp lại!", fontNormal);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, fontNormal));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);
        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "", fontNormal));
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    private static void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header, fontBold));
        cell.setBackgroundColor(new java.awt.Color(200, 200, 200));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String value, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value, fontNormal));
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

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
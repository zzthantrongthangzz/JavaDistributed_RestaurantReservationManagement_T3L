package ui;

import connect.ConfigManager;
import entity.*;
import rmi_interfaces.IMonAn_Service;

import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class PhieuDatBanPDF {
    private static com.lowagie.text.Font fontTitle;
    private static com.lowagie.text.Font fontHeader;
    private static com.lowagie.text.Font fontNormal;
    private static com.lowagie.text.Font fontBold;
    private static String FONT_PATH = "/fonts/DejaVuSans.ttf";
    private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
    private static final DecimalFormat currencyFormatter = new DecimalFormat("#,##0");

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

    public static void xuatPhieuDatBan(PhieuDatBan pdb, List<ChiTietPhieuDatBan> dsCT, String tenNV, String tenKH, List<String> dsTenBan) {
        String fileName = "PhieuDat_" + pdb.getMaPhieuDatBan() + ".pdf";
        Document document = new Document(PageSize.A5);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Paragraph title = new Paragraph("NHÀ HÀNG T3L", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subTitle = new Paragraph("PHIẾU ĐẶT BÀN TRƯỚC", fontHeader);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitle);
            document.add(new Paragraph(" "));

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            addInfoRow(infoTable, "Mã phiếu:", pdb.getMaPhieuDatBan());
            addInfoRow(infoTable, "Thời gian đặt:", dateTimeFormatter.format(pdb.getThoiGianDat()));
            addInfoRow(infoTable, "Khách hàng:", tenKH);
            addInfoRow(infoTable, "Nhân viên lập:", tenNV);
            addInfoRow(infoTable, "Danh sách bàn:", String.join(", ", dsTenBan));
            document.add(infoTable);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{40, 10, 25, 25});
            addTableHeader(table, "Món ăn");
            addTableHeader(table, "SL");
            addTableHeader(table, "Đơn giá");
            addTableHeader(table, "Thành tiền");

            // Kết nối RMI để dịch mã món thành tên món
            IMonAn_Service monAnService = null;
            try {
                String url = ConfigManager.getRmiUrl();
                monAnService = (IMonAn_Service) Naming.lookup(url +"MonAn_Service");
            } catch (Exception e) {
                System.err.println("Không thể kết nối Server để lấy tên món: " + e.getMessage());
            }

            BigDecimal tongTien = BigDecimal.ZERO;
            for (ChiTietPhieuDatBan ct : dsCT) {
                String tenMonHienThi = ct.getMaMon(); // Mặc định là mã món nếu không tìm thấy
                if (monAnService != null) {
                    try {
                        MonAn mon = monAnService.timMotMonTheoMa(ct.getMaMon());
                        if (mon != null && mon.getTenMon() != null) {
                            tenMonHienThi = mon.getTenMon();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                addTableCell(table, tenMonHienThi, Element.ALIGN_LEFT);
                addTableCell(table, String.valueOf(ct.getSoLuong()), Element.ALIGN_CENTER);
                addTableCell(table, currencyFormatter.format(ct.getDonGia()), Element.ALIGN_RIGHT);
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));
                addTableCell(table, currencyFormatter.format(thanhTien), Element.ALIGN_RIGHT);
                tongTien = tongTien.add(thanhTien);
            }
            document.add(table);
            document.add(new Paragraph(" "));

            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            addTotalRow(footerTable, "Tổng tiền món:", currencyFormatter.format(tongTien) + " VND");
            addTotalRow(footerTable, "Tiền đặt cọc:", currencyFormatter.format(pdb.getTienDatCoc()) + " VND");
            document.add(footerTable);

            document.add(new Paragraph(" "));
            Paragraph ghiChu = new Paragraph("Ghi chú: " + (pdb.getGhiChu() != null ? pdb.getGhiChu() : ""), fontNormal);
            document.add(ghiChu);

            document.close();
            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể tạo file PDF! Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fontNormal));
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);
        PdfPCell c2 = new PdfPCell(new Phrase(value, fontNormal));
        c2.setBorder(Rectangle.NO_BORDER);
        table.addCell(c2);
    }

    private static void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header, fontBold));
        cell.setBackgroundColor(new java.awt.Color(230, 230, 230));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String value, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(value, fontNormal));
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    private static void addTotalRow(PdfPTable table, String label, String value) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fontBold));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c1);
        PdfPCell c2 = new PdfPCell(new Phrase(value, fontBold));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c2);
    }
}
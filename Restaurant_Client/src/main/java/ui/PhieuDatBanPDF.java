package ui;

import entity.*;
// ✅ Import Interface từ Shared
import rmi_interfaces.IMonAn_DAO;
import rmi_interfaces.IChiTietPhieuDatBan_DAO;

import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    // Khối khởi tạo font chữ cho PDF
    static {
        try {
            java.net.URL fontUrl = PhieuDatBanPDF.class.getResource(FONT_PATH);
            if (fontUrl != null) {
                String fontPath = java.net.URLDecoder.decode(fontUrl.getPath(), "UTF-8");
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

    // Xuất phiếu đặt bàn ra file PDF
    public static void xuatPhieuDatBanPDF(PhieuDatBan phieuDat, KhachHang khachHang, NhanVien nhanVien, List<BanAn> dsBanDat, double tienDatCoc) {

        // ✅ Khai báo kiểu Interface
        IChiTietPhieuDatBan_DAO chiTietDAO = null;
        IMonAn_DAO monAnDAO = null;

        try {
            // ✅ Lookup từ RMI Registry (Đảm bảo tên trên Server trùng khớp)
            chiTietDAO = (IChiTietPhieuDatBan_DAO) Naming.lookup("rmi://localhost:1099/ChiTietPhieuDatBan_DAO");
            monAnDAO = (IMonAn_DAO) Naming.lookup("rmi://localhost:1099/MonAn_DAO");

            List<ChiTietPhieuDatBan> dsChiTiet = chiTietDAO.getChiTietTheoPhieu(phieuDat.getMaPhieuDatBan());

            String fileName = "PhieuDatBan_" + phieuDat.getMaPhieuDatBan() + "_" + System.currentTimeMillis() + ".pdf";
            File file = new File(fileName);

            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3});

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            try {
                java.net.URL logoUrl = PhieuDatBanPDF.class.getResource("/IMG/t3LLogo_300px.png");
                if (logoUrl != null) {
                    Image logo = Image.getInstance(logoUrl);
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

            Paragraph phieuTitle = new Paragraph("PHIẾU ĐẶT BÀN CHỜ", fontTitle);
            phieuTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(phieuTitle);
            document.add(Chunk.NEWLINE);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(80);
            infoTable.setWidths(new float[]{1, 2});
            infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            String tenBanStr = "Chưa xếp bàn";
            if (dsBanDat != null && !dsBanDat.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (BanAn ban : dsBanDat) {
                    sb.append(ban.getTenBan()).append(", ");
                }
                if (sb.length() > 2) {
                    tenBanStr = sb.substring(0, sb.length() - 2);
                }
            }

            addInfoRow(infoTable, "Mã phiếu đặt:", phieuDat.getMaPhieuDatBan());
            addInfoRow(infoTable, "Danh sách bàn:", tenBanStr);
            addInfoRow(infoTable, "Nhân viên lập:", nhanVien != null ? nhanVien.getHoTen() : "N/A");
            addInfoRow(infoTable, "Khách hàng:", khachHang != null ? khachHang.getHoTen() : "N/A");
            addInfoRow(infoTable, "Số điện thoại:", khachHang != null ? khachHang.getSoDienThoai() : "N/A");

            addInfoRow(infoTable, "Thời gian đặt:", dateTimeFormatter.format(phieuDat.getThoiGianDat() != null ? phieuDat.getThoiGianDat() : new Date()));

            addInfoRow(infoTable, "Trạng thái:", phieuDat.getTrangThai());
            addInfoRow(infoTable, "Ghi chú:", phieuDat.getGhiChu() != null ? phieuDat.getGhiChu() : "Không");

            document.add(infoTable);
            document.add(Chunk.NEWLINE);

            if (dsChiTiet != null && !dsChiTiet.isEmpty()) {
                document.add(new Paragraph("Danh sách món đã đặt trước:", fontBold));
                document.add(Chunk.NEWLINE);

                PdfPTable itemTable = new PdfPTable(4);
                itemTable.setWidthPercentage(80);
                itemTable.setWidths(new float[]{2.5f, 1, 1.5f, 2.5f});
                itemTable.setHorizontalAlignment(Element.ALIGN_CENTER);

                addTableHeader(itemTable, "Tên món");
                addTableHeader(itemTable, "Số lượng");
                addTableHeader(itemTable, "Đơn giá");
                addTableHeader(itemTable, "Thành tiền");

                double tongTienMon = 0;
                for (ChiTietPhieuDatBan ct : dsChiTiet) {
                    MonAn mon = monAnDAO.timMotMonTheoMa(ct.getMaMon());
                    String tenMon = (mon != null) ? mon.getTenMon() : ct.getMaMon();

                    double donGia = ct.getDonGia().doubleValue();
                    double thanhTien = donGia * ct.getSoLuong();
                    tongTienMon += thanhTien;

                    addTableCell(itemTable, tenMon, Element.ALIGN_LEFT);
                    addTableCell(itemTable, String.valueOf(ct.getSoLuong()), Element.ALIGN_CENTER);
                    addTableCell(itemTable, currencyFormatter.format(donGia) + " đ", Element.ALIGN_RIGHT);
                    addTableCell(itemTable, currencyFormatter.format(thanhTien) + " đ", Element.ALIGN_RIGHT);
                }

                document.add(itemTable);

                Paragraph totalMon = new Paragraph("Tổng tiền món đặt trước: " + currencyFormatter.format(tongTienMon) + " đ", fontBold);
                totalMon.setAlignment(Element.ALIGN_RIGHT);
                totalMon.setIndentationRight(50);
                document.add(totalMon);

                document.add(Chunk.NEWLINE);
            }

            document.add(Chunk.NEWLINE);

            Paragraph depositParagraph = new Paragraph(
                    "SỐ TIỀN ĐẶT CỌC: " + currencyFormatter.format(tienDatCoc) + " VNĐ",
                    fontTitle
            );
            depositParagraph.setAlignment(Element.ALIGN_CENTER);
            depositParagraph.setSpacingAfter(10f);
            document.add(depositParagraph);

            Paragraph note = new Paragraph("Lưu ý: Số tiền đặt cọc này sẽ được trừ vào tổng tiền thanh toán hóa đơn khi nhận bàn.", fontNormal);
            note.setAlignment(Element.ALIGN_CENTER);
            document.add(note);
            document.add(Chunk.NEWLINE);

            Paragraph thanks = new Paragraph("Cảm ơn quý khách!", fontBold);
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            document.close();
            writer.close();

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (java.rmi.RemoteException re) {
            re.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi kết nối đến máy chủ:\n" + re.getMessage(),
                    "Lỗi Mạng",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Đã xảy ra lỗi khi xuất Phiếu Đặt Bàn PDF:\n" + e.getMessage(),
                    "Lỗi PDF",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thêm tiêu đề cột cho bảng
    private static void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header, fontBold));
        cell.setBackgroundColor(new java.awt.Color(200, 200, 200));
        cell.setBorder(Rectangle.BOX);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    // Thêm nội dung vào ô của bảng
    private static void addTableCell(PdfPTable table, String value, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value, fontNormal));
        cell.setBorder(Rectangle.BOX);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    // Thêm một dòng thông tin label-value vào bảng
    private static void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, fontBold));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        labelCell.setPaddingBottom(8f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "", fontNormal));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setPaddingBottom(8f);
        table.addCell(valueCell);
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import services.ReportData;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * PdfReportGenerator - creates a polished PDF using iText 5.
 * - Adds logo (from InputStream)
 * - Title, date range or params
 * - Table with headers and rows
 */
public class PdfReportGenerator {

    /**
     * Generate a PDF file.
     *
     * @param report ReportData containing title, headers and rows
     * @param outFile File to write PDF to
     * @param logoStream (optional) InputStream for logo (e.g. getResourceAsStream("/images/logo.png"))
     * @param parameters optional additional lines displayed under title (e.g. date range)
     * @throws Exception on IO or PDF error
     */
    public static void generatePdf(ReportData report, File outFile, InputStream logoStream, List<String> parameters) throws Exception {
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 54, 36); // landscape for wide tables
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            PdfWriter.getInstance(doc, fos);
            doc.open();

            // Header: logo + title
            PdfPTable header = new PdfPTable(2);
            header.setWidths(new int[]{1, 4});
            header.setWidthPercentage(100);

            // Logo cell
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            if (logoStream != null) {
                byte[] bytes = toByteArray(logoStream);
                Image logo = Image.getInstance(bytes);
                logo.scaleToFit(120, 60);
                logoCell.addElement(logo);
            } else {
                // placeholder text if no logo
                Paragraph p = new Paragraph("Company", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY));
                logoCell.addElement(p);
            }
            header.addCell(logoCell);

            // Title cell
            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            Paragraph title = new Paragraph(report.getTitle(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setSpacingAfter(6);
            titleCell.addElement(title);

            // params (date range etc.)
            if (parameters != null) {
                for (String line : parameters) {
                    Paragraph p = new Paragraph(line, FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY));
                    titleCell.addElement(p);
                }
            }

            // metadata line
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
            Paragraph meta = new Paragraph("Generated: " + now, FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY));
            meta.setSpacingBefore(6);
            titleCell.addElement(meta);

            header.addCell(titleCell);
            doc.add(header);

            doc.add(Chunk.NEWLINE);

            // Build table with dynamic number of columns
            List<String> headers = report.getHeaders();
            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(100);
            table.setSpacingBefore(6f);
            table.setSpacingAfter(6f);

            // Header style
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
            BaseColor headBack = new BaseColor(40, 116, 166); // bluish

            for (String h : headers) {
                PdfPCell hcell = new PdfPCell(new Phrase(h, headFont));
                hcell.setBackgroundColor(headBack);
                hcell.setPadding(6);
                table.addCell(hcell);
            }

            // Rows
            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            for (List<Object> r : report.getRows()) {
                for (Object cellObj : r) {
                    String text = cellObj == null ? "" : cellObj.toString();
                    PdfPCell cell = new PdfPCell(new Phrase(text, rowFont));
                    cell.setPadding(6);
                    table.addCell(cell);
                }
            }

            doc.add(table);

            // Footer: small note
            Paragraph footer = new Paragraph("This is a system-generated report.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY));
            footer.setSpacingBefore(12);
            doc.add(footer);

            doc.close();
        }
    }

    private static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = is.read(data, 0, data.length)) != -1) buffer.write(data, 0, nRead);
        buffer.flush();
        return buffer.toByteArray();
    }
}

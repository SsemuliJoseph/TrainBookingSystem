/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import db.DBConnection;

import javax.swing.*;
import java.io.File;
import java.sql.*;

/**
 * ReportGenerator - PDF bookings report (iText) or shows message if library missing.
 */
public class ReportGenerator {

    public void generateBookingsReportPdf(JFrame parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("bookings_report.pdf"));
        int rc = chooser.showSaveDialog(parent);
        if (rc != JFileChooser.APPROVE_OPTION) return;
        File out = chooser.getSelectedFile();

        try {
            Class.forName("com.itextpdf.text.Document");
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new java.io.FileOutputStream(out));
            doc.open();
            com.itextpdf.text.Font h = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
            doc.add(new com.itextpdf.text.Paragraph("Bookings Report", h));
            doc.add(new com.itextpdf.text.Paragraph("Generated: " + new java.util.Date()));
            doc.add(new com.itextpdf.text.Paragraph(" "));
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("BookingID"); table.addCell("PNR"); table.addCell("Passenger"); table.addCell("Route"); table.addCell("RunDate"); table.addCell("Amount");

            String sql = "SELECT b.booking_id, b.pnr, p.full_name, s1.station_name origin, s2.station_name destination, tr.run_date, b.total_amount " +
                    "FROM bookings b JOIN passengers p ON b.passenger_id = p.passenger_id " +
                    "JOIN train_runs tr ON b.run_id = tr.run_id " +
                    "JOIN routes r ON tr.route_id = r.route_id " +
                    "JOIN stations s1 ON r.origin_station_id=s1.station_id " +
                    "JOIN stations s2 ON r.destination_station_id=s2.station_id ORDER BY tr.run_date DESC LIMIT 1000";
            try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    table.addCell(String.valueOf(rs.getInt(1)));
                    table.addCell(rs.getString(2));
                    table.addCell(rs.getString(3));
                    table.addCell(rs.getString("origin") + " → " + rs.getString("destination"));
                    table.addCell(String.valueOf(rs.getDate("run_date")));
                    table.addCell(rs.getBigDecimal("total_amount").toPlainString());
                }
            }

            doc.add(table);
            doc.close();
            JOptionPane.showMessageDialog(parent, "Report written to: " + out.getAbsolutePath());
        } catch (ClassNotFoundException cnf) {
            JOptionPane.showMessageDialog(parent, "iText not found. Add it to generate PDF reports.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Report generation failed: " + ex.getMessage());
        }
    }
}

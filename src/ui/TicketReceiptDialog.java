/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * TicketReceiptDialog - shows ticket info and allows saving.
 */
public class TicketReceiptDialog extends JDialog {

    public TicketReceiptDialog(Frame parent, String passengerName, String pnr,
                               String route, String seat, String seatClass,
                               String fare, String transactionRef, String status) {
        super(parent, "Ticket", true);
        setSize(420,360);
        setLayout(new BorderLayout(8,8));
        JPanel content = new JPanel(new GridLayout(9,2,6,6));
        content.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        content.add(new JLabel("Passenger:")); content.add(new JLabel(passengerName));
        content.add(new JLabel("PNR:")); content.add(new JLabel(pnr));
        content.add(new JLabel("Route:")); content.add(new JLabel(route));
        content.add(new JLabel("Seat:")); content.add(new JLabel(seat));
        content.add(new JLabel("Class:")); content.add(new JLabel(seatClass));
        content.add(new JLabel("Fare:")); content.add(new JLabel(fare));
        content.add(new JLabel("Transaction Ref:")); content.add(new JLabel(transactionRef));
        content.add(new JLabel("Status:")); content.add(new JLabel(status));
        add(content, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton btnSave = new JButton("Save Ticket");
        JButton btnClose = new JButton("Close");
        buttons.add(btnSave); buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);

        btnClose.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("ticket_" + pnr + ".pdf"));
            int rc = chooser.showSaveDialog(this);
            if (rc != JFileChooser.APPROVE_OPTION) return;
            File file = chooser.getSelectedFile();
            try {
                // try iText
                savePdf(file.getAbsolutePath(), passengerName, pnr, route, seat, seatClass, fare, transactionRef, status);
                JOptionPane.showMessageDialog(this, "Ticket saved: " + file.getAbsolutePath());
            } catch (Throwable t) {
                // fallback to text
                try (FileWriter fw = new FileWriter(file.getAbsolutePath().replaceAll("\\.pdf$", ".txt"))) {
                    fw.write("TICKET\n");
                    fw.write("PNR: " + pnr + "\n");
                    fw.write("Passenger: " + passengerName + "\n");
                    fw.write("Route: " + route + "\n");
                    fw.write("Seat: " + seat + "\n");
                    fw.write("Class: " + seatClass + "\n");
                    fw.write("Fare: " + fare + "\n");
                    fw.write("TransactionRef: " + transactionRef + "\n");
                    fw.write("Status: " + status + "\n");
                    fw.flush();
                    JOptionPane.showMessageDialog(this, "iText not available; saved as text: " + file.getAbsolutePath().replaceAll("\\.pdf$", ".txt"));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Save failed: " + ioe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setLocationRelativeTo(parent);
    }

    private void savePdf(String path, String passengerName, String pnr,
                         String route, String seat, String seatClass, String fare, String txRef, String status) throws Exception {
        // iText 5.x usage
        Class.forName("com.itextpdf.text.Document"); // will throw if not on classpath
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new java.io.FileOutputStream(path));
        doc.open();
        com.itextpdf.text.Font f = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
        doc.add(new com.itextpdf.text.Paragraph("Train Ticket", f));
        doc.add(new com.itextpdf.text.Paragraph("PNR: " + pnr));
        doc.add(new com.itextpdf.text.Paragraph("Passenger: " + passengerName));
        doc.add(new com.itextpdf.text.Paragraph("Route: " + route));
        doc.add(new com.itextpdf.text.Paragraph("Seat: " + seat));
        doc.add(new com.itextpdf.text.Paragraph("Class: " + seatClass));
        doc.add(new com.itextpdf.text.Paragraph("Fare: " + fare));
        doc.add(new com.itextpdf.text.Paragraph("Transaction Ref: " + txRef));
        doc.add(new com.itextpdf.text.Paragraph("Status: " + status));
        doc.close();
    }
}

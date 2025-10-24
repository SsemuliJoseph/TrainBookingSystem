package ui;

import reports.PdfReportGenerator;
import services.ReportData;
import services.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * AdminReportsFrame - Swing UI to generate admin reports and export them to PDF.
 * Uses ReportService (JDBC) and PdfReportGenerator (iText 5).
 *
 * IMPORTANT: this class expects services.ReportService and reports.PdfReportGenerator
 * to be present and working in your project.
 */
public class AdminReportsFrame extends JFrame {
    private final JComboBox<String> cmbReportType = new JComboBox<>();
    private final JSpinner spinnerFrom;
    private final JSpinner spinnerTo;
    private final JButton btnGenerate = new JButton("Generate");
    private final JButton btnExport = new JButton("Export PDF");
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable tblPreview = new JTable(tableModel);

    private ReportData lastReport;
    private final ReportService reportService = new ReportService();

    public AdminReportsFrame() {
        setTitle("Admin Reports");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // date spinners
        spinnerFrom = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spinnerTo   = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spinnerFrom.setEditor(new JSpinner.DateEditor(spinnerFrom, "yyyy-MM-dd"));
        spinnerTo.setEditor(new JSpinner.DateEditor(spinnerTo, "yyyy-MM-dd"));

        // reports list (5 admin reports)
        cmbReportType.addItem("Train Schedules");
        cmbReportType.addItem("Bookings Summary (by route)");
        cmbReportType.addItem("Top Routes (by bookings)");
        cmbReportType.addItem("Cancellations");
        cmbReportType.addItem("Train Utilization");

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        top.add(new JLabel("Report:"), gbc);
        gbc.gridx = 1; top.add(cmbReportType, gbc);

        gbc.gridx = 2; top.add(new JLabel("From:"), gbc);
        gbc.gridx = 3; top.add(spinnerFrom, gbc);

        gbc.gridx = 4; top.add(new JLabel("To:"), gbc);
        gbc.gridx = 5; top.add(spinnerTo, gbc);

        gbc.gridx = 6; top.add(btnGenerate, gbc);
        gbc.gridx = 7; top.add(btnExport, gbc);

        add(top, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tblPreview);
        add(scroll, BorderLayout.CENTER);

        // actions
        btnGenerate.addActionListener(e -> onGenerate());
        btnExport.addActionListener(e -> onExport());

        // initial UI tweaks
        tblPreview.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblPreview.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tblPreview.setRowHeight(24);
    }

    // Generate report to preview table
    private void onGenerate() {
        try {
            String choice = (String) cmbReportType.getSelectedItem();
            Date from = (Date) spinnerFrom.getValue();   // <--- correct usage
            Date to   = (Date) spinnerTo.getValue();     // <--- correct usage

            if ("Train Schedules".equals(choice)) {
                lastReport = reportService.getTrainSchedules(from, to);
            } else if ("Bookings Summary (by route)".equals(choice)) {
                lastReport = reportService.getBookingsSummary(from, to);
            } else if ("Top Routes (by bookings)".equals(choice)) {
                lastReport = reportService.getTopRoutes(from, to);
            } else if ("Cancellations".equals(choice)) {
                lastReport = reportService.getCancellations(from, to);
            } else if ("Train Utilization".equals(choice)) {
                lastReport = reportService.getTrainUtilization(from, to);
            } else {
                JOptionPane.showMessageDialog(this, "Report not implemented.");
                return;
            }

            // populate table
            List<String> headers = lastReport.getHeaders();
            tableModel.setColumnIdentifiers(headers.toArray());
            tableModel.setRowCount(0);
            for (List<Object> row : lastReport.getRows()) {
                Object[] rowArr = row.toArray(new Object[0]);
                tableModel.addRow(rowArr);
            }

            // autosize columns (basic)
            for (int i = 0; i < tblPreview.getColumnCount(); i++) {
                tblPreview.getColumnModel().getColumn(i).setPreferredWidth(180);
            }

            JOptionPane.showMessageDialog(this, "Report generated. Rows: " + lastReport.getRows().size());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Export lastReport -> PDF using PdfReportGenerator
    private void onExport() {
        if (lastReport == null) {
            JOptionPane.showMessageDialog(this, "Generate a report first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(lastReport.getTitle().replaceAll("\\s+","_") + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf"));
        int res = chooser.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File out = chooser.getSelectedFile();
        try (InputStream logoIs = getClass().getResourceAsStream("/resources/images/logo.png")) {
            Date from = (Date) spinnerFrom.getValue();
            Date to   = (Date) spinnerTo.getValue();
            String fromS = new SimpleDateFormat("yyyy-MM-dd").format(from);
            String toS   = new SimpleDateFormat("yyyy-MM-dd").format(to);

            List<String> params = java.util.Arrays.asList("Date Range: " + fromS + " to " + toS, "Generated by: Admin");
            PdfReportGenerator.generatePdf(lastReport, out, logoIs, params);

            JOptionPane.showMessageDialog(this, "PDF saved: " + out.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

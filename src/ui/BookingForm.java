/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ui;

import db.DBConnection;
import models.User;
import services.BookingService;
import services.FareService;
import services.ScheduleService;
import services.SeatService;
import services.SeatService.SeatInfo;
import services.ScheduleService.RunInfo;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.desktop.OpenURIEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * BookingForm - Passenger booking UI that:
 *  - search runs (origin/destination/date)
 *  - display runs in a table
 *  - pick seat class, show available seats for selected run+class
 *  - show correct fare (FareService)
 *  - book & pay (BookingService)
 *  - generate downloadable ticket (PDF via iText if available, else plain text)
 *
 * Replace your existing ui.BookingForm with this class.
 */
public class BookingForm extends JFrame {
    private final User user;

    // Services
    private final ScheduleService scheduleService = new ScheduleService();
    private final FareService fareService = new FareService();
    private final SeatService seatService = new SeatService();
    private final BookingService bookingService = new BookingService();

    // UI controls
    private final JTextField txtOrigin = new JTextField(12);
    private final JTextField txtDestination = new JTextField(12);
    private final JTextField txtDate = new JTextField("YYYY-MM-DD", 10);
    private final JButton btnSearch = new JButton("Search Runs");

    private final DefaultTableModel runsModel = new DefaultTableModel(new String[]{"Run ID","Train","Origin","Destination","Date","Departure"}, 0);
    private final JTable runsTable = new JTable(runsModel);

    private final JComboBox<ClassItem> cmbClass = new JComboBox<>();
    private final JComboBox<SeatItem> cmbSeats = new JComboBox<>();
    private final JLabel lblFare = new JLabel("UGX 0");
    private final JButton btnBook = new JButton("Book & Download Ticket");

    public BookingForm(User user) {
        this.user = user;
        setTitle("Book Ticket - " + user.getUsername());
        setSize(920, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadSeatClasses();
    }

    private void initUI() {
        ImagePanel bg = new ImagePanel("/resources/images/dashboard_bg.jpg");
        bg.setLayout(new BorderLayout(10,10));
        setContentPane(bg);

        // Top search panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.setOpaque(false);
        top.add(new JLabel("Origin:")); top.add(txtOrigin);
        top.add(new JLabel("Destination:")); top.add(txtDestination);
        top.add(new JLabel("Date:")); top.add(txtDate);
        top.add(btnSearch);
        bg.add(top, BorderLayout.NORTH);

        // Runs table center
        runsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(runsTable);
        sp.setPreferredSize(new Dimension(880, 300));
        bg.add(sp, BorderLayout.CENTER);

        // Bottom booking controls
        JPanel bottom = new JPanel(new GridBagLayout());
        bottom.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; bottom.add(new JLabel("Seat Class:"), gbc);
        gbc.gridx=1; bottom.add(cmbClass, gbc);

        gbc.gridx=0; gbc.gridy=1; bottom.add(new JLabel("Available Seats:"), gbc);
        gbc.gridx=1; bottom.add(cmbSeats, gbc);

        gbc.gridx=0; gbc.gridy=2; bottom.add(new JLabel("Fare:"), gbc);
        gbc.gridx=1; bottom.add(lblFare, gbc);

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; bottom.add(btnBook, gbc);

        bg.add(bottom, BorderLayout.SOUTH);

        // Listeners
        btnSearch.addActionListener(e -> doSearch());
        runsTable.getSelectionModel().addListSelectionListener(this::onRunSelected);
        cmbClass.addActionListener(e -> updateSeatsAndFare());
        btnBook.addActionListener(e -> doBook());

        // small UI polish: double-click run to open selection
        runsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { updateSeatsAndFare(); }
            }
        });
    }

    // Load seat classes into cmbClass
    private void loadSeatClasses() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT class_id, class_name FROM seat_classes ORDER BY class_name");
             ResultSet rs = ps.executeQuery()) {
            cmbClass.removeAllItems();
            while (rs.next()) {
                cmbClass.addItem(new ClassItem(rs.getInt("class_id"), rs.getString("class_name")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load seat classes: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Search runs by origin/destination/date (date optional)
    private void doSearch() {
        String origin = txtOrigin.getText().trim();
        String dest = txtDestination.getText().trim();
        String dateStr = txtDate.getText().trim();
        java.sql.Date date = null;
        if (!dateStr.isBlank() && !dateStr.equalsIgnoreCase("YYYY-MM-DD")) {
            try { date = java.sql.Date.valueOf(dateStr); }
            catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        runsModel.setRowCount(0);
        try {
            List<RunInfo> runs = scheduleService.searchRuns(origin.isEmpty() ? "%" : origin, dest.isEmpty() ? "%" : dest, date);
            for (RunInfo r : runs) {
                runsModel.addRow(new Object[]{r.runId, r.trainName, r.origin, r.destination, r.runDate, r.departure});
            }
            if (runs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No runs found for the given criteria.", "No results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Called when run selection changes
    private void onRunSelected(ListSelectionEvent ev) {
        if (ev.getValueIsAdjusting()) return;
        updateSeatsAndFare();
    }

    // Load seats for selected run+class and update fare
    private void updateSeatsAndFare() {
        int row = runsTable.getSelectedRow();
        if (row == -1) {
            cmbSeats.removeAllItems();
            lblFare.setText("UGX 0");
            return;
        }
        int runId = (int) runsModel.getValueAt(row, 0);
        ClassItem ci = (ClassItem) cmbClass.getSelectedItem();
        if (ci == null) { lblFare.setText("UGX 0"); return; }
        int classId = ci.id;

        // Load seats
        cmbSeats.removeAllItems();
        try {
            List<SeatInfo> seats = seatService.getAvailableSeatsForRunAndClass(runId, classId);
            if (seats.isEmpty()) {
                cmbSeats.addItem(new SeatItem(-1, "No seats available"));
            } else {
                for (SeatInfo s : seats) cmbSeats.addItem(new SeatItem(s.seatId, s.toString()));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            cmbSeats.addItem(new SeatItem(-1, "Error loading seats"));
        }

        // Load fare
        try {
            BigDecimal fare = fareService.getActiveFareForRunAndClass(runId, classId);
            lblFare.setText("UGX " + (fare == null ? "0" : fare.toPlainString()));
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblFare.setText("UGX 0");
        }
    }

    // Booking action -> calls BookingService, then offers ticket download
    private void doBook() {
        int row = runsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a train run first.", "No run selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int runId = (int) runsModel.getValueAt(row, 0);
        ClassItem ci = (ClassItem) cmbClass.getSelectedItem();
        if (ci == null) {
            JOptionPane.showMessageDialog(this, "Please select a seat class.", "No class", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int classId = ci.id;

        SeatItem si = (SeatItem) cmbSeats.getSelectedItem();
        int seatId = (si == null) ? -1 : si.id;

        String paymentMethod = "Mobile Money";
        String txRef = "MM" + System.currentTimeMillis();

        try {
            Optional<String> pnrOpt;
            if (seatId > 0) {
                pnrOpt = bookingService.bookAndPayWithSeat(user.getUserId(), runId, seatId, classId, paymentMethod, txRef);
                if (pnrOpt.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Selected seat is no longer available. Please refresh seats.", "Seat unavailable", JOptionPane.WARNING_MESSAGE);
                    updateSeatsAndFare();
                    return;
                }
            } else {
                pnrOpt = bookingService.bookAndPay(user.getUserId(), runId, classId, paymentMethod, txRef);
                if (pnrOpt.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No seats available in the selected class.", "Sold out", JOptionPane.WARNING_MESSAGE);
                    updateSeatsAndFare();
                    return;
                }
            }

            String pnr = pnrOpt.get();
            JOptionPane.showMessageDialog(this, "Booking confirmed. PNR: " + pnr, "Booked", JOptionPane.INFORMATION_MESSAGE);

            // After successful booking, fetch booking & ticket info and prompt to save PDF
            postBookingActions(pnr);

            // Refresh seat list after booking
            updateSeatsAndFare();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // After booking: query booking/ticket details by PNR and prompt to save ticket (PDF/TXT)
    private void postBookingActions(String pnr) {
        String sql = "SELECT b.booking_id, b.run_id, t.ticket_id, t.seat_id, t.passenger_name, t.fare_paid " +
                     "FROM bookings b JOIN tickets t ON b.booking_id = t.booking_id " +
                     "WHERE b.pnr = ? LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int bookingId = rs.getInt("booking_id");
                    int runId = rs.getInt("run_id");
                    int ticketId = rs.getInt("ticket_id");
                    int seatId = rs.getInt("seat_id");
                    String passengerName = rs.getString("passenger_name");
                    BigDecimal fare = rs.getBigDecimal("fare_paid");

                    // Build readable labels
                    String runLabel = buildRunLabel(runId);
                    String seatLabel = seatService.getSeatLabel(seatId);

                    // Ask user where to save
                    JFileChooser chooser = new JFileChooser();
                    chooser.setSelectedFile(new File("ticket_" + pnr + ".pdf"));
                    int rc = chooser.showSaveDialog(this);
                    if (rc != JFileChooser.APPROVE_OPTION) return;
                    File out = chooser.getSelectedFile();

                    // Try to create PDF (iText), fallback to text file
                    boolean saved = saveTicketPdfOrText(out.getAbsolutePath(), pnr, passengerName, runLabel, seatLabel, fare);
                    if (saved) {
                        JOptionPane.showMessageDialog(this, "Ticket saved to: " + out.getAbsolutePath());
                        // try to open automatically
                        try {
                            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(out);
                        } catch (Exception ignored) { /* ignore if cannot open */ }
                    } else {
                        JOptionPane.showMessageDialog(this, "Ticket could not be saved.", "Save failed", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Booking record not found for PNR: " + pnr, "Not found", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve booking: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper to build a readable run label
    private String buildRunLabel(int runId) {
        try {
            RunInfo r = scheduleService.getRunById(runId);
            if (r != null) return r.trainName + " | " + r.origin + " → " + r.destination + " | " + r.runDate + " " + r.departure;
        } catch (SQLException ex) { /* ignore */ }
        return "Run " + runId;
    }

    /**
     * Save ticket as PDF using iText if available; otherwise save as text file.
     * Returns true on success.
     */
    private boolean saveTicketPdfOrText(String path, String pnr, String passenger, String runLabel, String seatLabel, BigDecimal fare) {
        // If user selected .txt explicitly, just write text
        if (path.toLowerCase().endsWith(".txt")) {
            return saveTicketAsText(path, pnr, passenger, runLabel, seatLabel, fare);
        }

        // Prefer PDF if iText available
        try {
            // check for iText Document class
            Class.forName("com.itextpdf.text.Document");
            // generate PDF
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            com.itextpdf.text.Font h = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            doc.add(new com.itextpdf.text.Paragraph("Train Ticket", h));
            doc.add(new com.itextpdf.text.Paragraph(" "));
            doc.add(new com.itextpdf.text.Paragraph("PNR: " + pnr));
            doc.add(new com.itextpdf.text.Paragraph("Passenger: " + passenger));
            doc.add(new com.itextpdf.text.Paragraph("Run: " + runLabel));
            doc.add(new com.itextpdf.text.Paragraph("Seat: " + seatLabel));
            doc.add(new com.itextpdf.text.Paragraph("Fare: UGX " + (fare == null ? "0" : fare.toPlainString())));
            doc.add(new com.itextpdf.text.Paragraph("Status: Confirmed"));
            doc.close();
            return true;
        } catch (ClassNotFoundException cnf) {
            // iText not available -> fallback to text file (same filename but .txt)
            String txtPath = path.endsWith(".pdf") ? path.replaceAll("(?i)\\.pdf$", ".txt") : path + ".txt";
            return saveTicketAsText(txtPath, pnr, passenger, runLabel, seatLabel, fare);
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    private boolean saveTicketAsText(String path, String pnr, String passenger, String runLabel, String seatLabel, BigDecimal fare) {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("TRAIN TICKET\n");
            fw.write("PNR: " + pnr + "\n");
            fw.write("Passenger: " + passenger + "\n");
            fw.write("Run: " + runLabel + "\n");
            fw.write("Seat: " + seatLabel + "\n");
            fw.write("Fare: UGX " + (fare == null ? "0" : fare.toPlainString()) + "\n");
            fw.write("Status: Confirmed\n");
            fw.flush();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Small helper classes for combo boxes
    private static class ClassItem {
        final int id; final String name;
        ClassItem(int id, String name){ this.id = id; this.name = name; }
        public String toString(){ return name; }
    }
    private static class SeatItem {
        final int id; final String label;
        SeatItem(int id, String label){ this.id = id; this.label = label; }
        public String toString(){ return label; }
    }
}

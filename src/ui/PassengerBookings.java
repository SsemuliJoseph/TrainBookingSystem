/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ssemu
 */
package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PassengerBookings extends JFrame {
    private final int passengerId;
    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Booking ID", "PNR", "Train", "Run Date", "Seat", "Status"}, 0);

    public PassengerBookings(int passengerId) {
        this.passengerId = passengerId;
        setTitle("My Bookings");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadBookings();
    }

    private void initUI() {
        table.setModel(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Request Cancellation");
        btnCancel.addActionListener(e -> requestCancellation());
        bottom.add(btnCancel);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadBookings() {
        model.setRowCount(0);
        String sql = """
            SELECT b.booking_id, b.pnr, t.train_name, tr.run_date, s.seat_number, b.status
            FROM bookings b
            JOIN tickets tk ON b.booking_id = tk.booking_id
            JOIN seats s ON tk.seat_id = s.seat_id
            JOIN coaches c ON s.coach_id = c.coach_id
            JOIN train_runs tr ON b.run_id = tr.run_id
            JOIN trains t ON tr.train_id = t.train_id
            WHERE b.passenger_id = ?
            ORDER BY tr.run_date DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("booking_id"),
                            rs.getString("pnr"),
                            rs.getString("train_name"),
                            rs.getDate("run_date"),
                            rs.getString("seat_number"),
                            rs.getString("status")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void requestCancellation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking to cancel.");
            return;
        }
        int bookingId = (int) model.getValueAt(row, 0);

        String reason = JOptionPane.showInputDialog(this, "Reason for cancellation (optional):");
        if (reason == null) return; // user pressed cancel

        String insertCancel = "INSERT INTO cancellations (booking_id, requested_by, reason, status) VALUES (?, ?, ?, 'Pending')";
        String updateBooking = "UPDATE bookings SET status='Cancelled' WHERE booking_id=?";
        String updateTicket = "UPDATE tickets SET ticket_status='Cancelled' WHERE booking_id=?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(insertCancel);
                 PreparedStatement ps2 = conn.prepareStatement(updateBooking);
                 PreparedStatement ps3 = conn.prepareStatement(updateTicket)) {

                ps1.setInt(1, bookingId);
                ps1.setInt(2, passengerId);
                ps1.setString(3, reason == null ? "" : reason);
                ps1.executeUpdate();

                ps2.setInt(1, bookingId);
                ps2.executeUpdate();

                ps3.setInt(1, bookingId);
                ps3.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Cancellation requested.");
                loadBookings();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Cancellation failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PassengerBookings(1).setVisible(true));
    }
}


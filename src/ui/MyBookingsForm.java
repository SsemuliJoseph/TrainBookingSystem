/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import db.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyBookingsForm extends JFrame {
    private final User user;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"BookingID","PNR","Run","Seat","Fare","Status"}, 0);

    public MyBookingsForm(User user) {
        this.user = user;
        setTitle("My Bookings - " + user.getUsername());
        setSize(800, 400);
        setLocationRelativeTo(null);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        loadBookings();
    }

    private void loadBookings() {
        model.setRowCount(0);
        String sql = "SELECT b.booking_id, b.pnr, b.run_id, t.seat_id, t.fare_paid, b.status FROM bookings b JOIN tickets t ON b.booking_id=t.booking_id WHERE b.passenger_id=(SELECT passenger_id FROM passengers WHERE user_id=?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, user.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String runLabel = runLabel(rs.getInt("run_id"));
                    String seatLabel = seatLabel(rs.getInt("seat_id"));
                    model.addRow(new Object[]{rs.getInt("booking_id"), rs.getString("pnr"), runLabel, seatLabel, rs.getBigDecimal("fare_paid"), rs.getString("status")});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String runLabel(int runId) {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT r.origin_station_id, r.destination_station_id, tr.run_date FROM train_runs tr JOIN routes r ON tr.route_id=r.route_id WHERE tr.run_id=?")) {
            ps.setInt(1, runId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return "Run " + runId + " (" + rs.getDate("run_date") + ")";
            }
        } catch (Exception ignored) {}
        return "Run " + runId;
    }

    private String seatLabel(int seatId) {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT s.seat_number, c.coach_label FROM seats s JOIN coaches c ON s.coach_id=c.coach_id WHERE s.seat_id=?")) {
            ps.setInt(1, seatId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString("coach_label") + ":" + rs.getString("seat_number"); }
        } catch (Exception ignored) {}
        return "Seat " + seatId;
    }
}

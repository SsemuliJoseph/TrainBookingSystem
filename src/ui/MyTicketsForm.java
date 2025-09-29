/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

/**
 *
 * @author ssemu
 */


import models.User;
import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Shows passenger's booked tickets
 */
public class MyTicketsForm extends JFrame {
    private User passenger;
    private JTable table;

    public MyTicketsForm(User passenger) {
        this.passenger = passenger;

        setTitle("My Tickets - " + passenger.getUsername());
        setSize(700, 400);
        setLocationRelativeTo(null);

        String[] columns = {"Ticket ID", "Train", "Route", "Seat", "Fare", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        loadTickets(model);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadTickets(DefaultTableModel model) {
        String sql = "SELECT t.ticket_id, tr.train_id, r.origin, r.destination, s.seat_number, t.fare_paid, t.ticket_status " +
                     "FROM tickets t " +
                     "JOIN bookings b ON t.booking_id = b.booking_id " +
                     "JOIN train_runs tr ON b.run_id = tr.run_id " +
                     "JOIN routes r ON tr.route_id = r.route_id " +
                     "JOIN seats s ON t.seat_id = s.seat_id " +
                     "WHERE b.passenger_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, passenger.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ticket_id"),
                        rs.getInt("train_id"),
                        rs.getString("origin") + " → " + rs.getString("destination"),
                        rs.getString("seat_number"),
                        rs.getDouble("fare_paid"),
                        rs.getString("ticket_status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading tickets: " + ex.getMessage());
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class CancelBookingForm extends JFrame {
    private int userId;
    private JTextField bookingIdField;
    private JButton cancelButton;

    public CancelBookingForm(int userId) {
        this.userId = userId;

        setTitle("Cancel Booking");
        setSize(400, 200);
        setLayout(new GridLayout(2, 2, 10, 10));

        bookingIdField = new JTextField();
        cancelButton = new JButton("Cancel Booking");

        add(new JLabel("Booking ID:"));
        add(bookingIdField);
        add(new JLabel(""));
        add(cancelButton);

        cancelButton.addActionListener(this::handleCancel);

        setLocationRelativeTo(null);
    }

    private void handleCancel(ActionEvent e) {
        int bookingId = Integer.parseInt(bookingIdField.getText().trim());

        String sql = "UPDATE bookings SET status='Cancelled' WHERE booking_id=? " +
                     "AND passenger_id=(SELECT passenger_id FROM passengers WHERE user_id=?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid booking ID or not your booking.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}


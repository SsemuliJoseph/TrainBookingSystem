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

public class RefundForm extends JFrame {
    private int userId;
    private JTextField bookingIdField;
    private JTextField reasonField;
    private JButton requestRefundButton;

    public RefundForm(int userId) {
        this.userId = userId;

        setTitle("Request Refund");
        setSize(400, 250);
        setLayout(new GridLayout(3, 2, 10, 10));

        bookingIdField = new JTextField();
        reasonField = new JTextField();
        requestRefundButton = new JButton("Submit Refund Request");

        add(new JLabel("Booking ID:"));
        add(bookingIdField);
        add(new JLabel("Reason:"));
        add(reasonField);
        add(new JLabel(""));
        add(requestRefundButton);

        requestRefundButton.addActionListener(this::handleRefund);

        setLocationRelativeTo(null);
    }

    private void handleRefund(ActionEvent e) {
        int bookingId = Integer.parseInt(bookingIdField.getText().trim());
        String reason = reasonField.getText().trim();

        String sql = "INSERT INTO cancellations (booking_id, requested_by, reason, status) " +
                     "VALUES (?, ?, ?, 'Pending')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, userId);
            ps.setString(3, reason);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Refund request submitted.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

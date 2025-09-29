/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import db.DBConnection;

import java.math.BigDecimal;
import java.sql.*;

/**
 * CancellationService - request & process cancellations and refunds.
 */
public class CancellationService {

    /**
     * Passenger requests cancellation - inserts into cancellations with status Pending.
     */
    public boolean requestCancel(int bookingId, int requestedByUserId, String reason) throws SQLException {
        String sql = "INSERT INTO cancellations (booking_id, requested_by, request_date, reason, status) VALUES (?, ?, NOW(), ?, 'Pending')";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, requestedByUserId);
            ps.setString(3, reason);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Admin processes cancellation: marks booking and tickets cancelled, creates refund record and updates payments.
     * Returns true on success.
     */
    public boolean processCancellation(int bookingId, int processedByUserId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // get payment id and amount for booking
            int paymentId = -1;
            BigDecimal amount = BigDecimal.ZERO;
            try (PreparedStatement ps = conn.prepareStatement("SELECT payment_id, amount FROM payments WHERE booking_id = ? LIMIT 1")) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        paymentId = rs.getInt("payment_id");
                        amount = rs.getBigDecimal("amount");
                    }
                }
            }

            // update booking
            try (PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status = 'Cancelled' WHERE booking_id = ?")) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            // update tickets
            try (PreparedStatement ps = conn.prepareStatement("UPDATE tickets SET ticket_status = 'Cancelled' WHERE booking_id = ?")) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            // create refund record (if payment exists)
            if (paymentId != -1) {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO refunds (payment_id, amount, refund_date, reason) VALUES (?, ?, NOW(), ?)")) {
                    ps.setInt(1, paymentId);
                    ps.setBigDecimal(2, amount);
                    ps.setString(3, "Admin processed refund");
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("UPDATE payments SET status = 'Refunded' WHERE payment_id = ?")) {
                    ps.setInt(1, paymentId);
                    ps.executeUpdate();
                }
            }

            // update cancellations entry
            try (PreparedStatement ps = conn.prepareStatement("UPDATE cancellations SET processed_by = ?, processed_time = NOW(), status = 'Processed' WHERE booking_id = ?")) {
                ps.setInt(1, processedByUserId);
                ps.setInt(2, bookingId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            throw ex;
        } finally {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ssemu
 */


package services;

import db.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

/**
 * BookingService - transactional booking + payment + ticket creation.
 *
 * - bookAndPay(...) auto-allocates seat in class
 * - bookAndPayWithSeat(...) attempts to reserve a specific seat
 */
public class BookingService {

    /**
     * Auto allocate seat in class for run and book.
     */
    public Optional<String> bookAndPay(int userId, int runId, int classId, String paymentMethod, String txRef) throws SQLException {
        return bookInternal(userId, runId, classId, -1, paymentMethod, txRef);
    }

    /**
     * Book a specific seat if available.
     */
    public Optional<String> bookAndPayWithSeat(int userId, int runId, int seatId, int classId, String paymentMethod, String txRef) throws SQLException {
        return bookInternal(userId, runId, classId, seatId, paymentMethod, txRef);
    }

    private Optional<String> bookInternal(int userId, int runId, int classId, int requestedSeatId, String paymentMethod, String txRef) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1) find passenger_id for this user_id
            int passengerId;
            try (PreparedStatement ps = conn.prepareStatement("SELECT passenger_id FROM passengers WHERE user_id = ?")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) passengerId = rs.getInt(1);
                    else throw new SQLException("Passenger profile not found for user_id=" + userId);
                }
            }

            // 2) determine seat to assign (with FOR UPDATE)
            int seatId = -1;
            if (requestedSeatId > 0) {
                String sqlCheck = """
                    SELECT s.seat_id
                    FROM seats s
                    JOIN coaches c ON s.coach_id = c.coach_id
                    JOIN train_runs tr ON c.train_id = tr.train_id
                    WHERE tr.run_id = ? AND s.class_id = ? AND s.seat_id = ?
                      AND s.seat_id NOT IN (
                        SELECT t.seat_id FROM tickets t JOIN bookings b ON t.booking_id = b.booking_id WHERE b.run_id = ?
                      )
                    FOR UPDATE
                    """;
                try (PreparedStatement ps = conn.prepareStatement(sqlCheck)) {
                    ps.setInt(1, runId);
                    ps.setInt(2, classId);
                    ps.setInt(3, requestedSeatId);
                    ps.setInt(4, runId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) seatId = rs.getInt(1);
                        else {
                            conn.rollback();
                            return Optional.empty(); // requested seat not available
                        }
                    }
                }
            } else {
                String sqlAuto = """
                    SELECT s.seat_id
                    FROM seats s
                    JOIN coaches c ON s.coach_id = c.coach_id
                    JOIN train_runs tr ON c.train_id = tr.train_id
                    WHERE tr.run_id = ? AND s.class_id = ?
                      AND s.seat_id NOT IN (
                        SELECT t.seat_id FROM tickets t JOIN bookings b ON t.booking_id = b.booking_id WHERE b.run_id = ?
                      )
                    LIMIT 1 FOR UPDATE
                    """;
                try (PreparedStatement ps = conn.prepareStatement(sqlAuto)) {
                    ps.setInt(1, runId);
                    ps.setInt(2, classId);
                    ps.setInt(3, runId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) seatId = rs.getInt(1);
                    }
                }
                if (seatId == -1) {
                    conn.rollback();
                    return Optional.empty(); // no seats
                }
            }

            // 3) fare lookup
            BigDecimal fare = BigDecimal.ZERO;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT f.amount FROM fares f JOIN train_runs tr ON f.route_id = tr.route_id WHERE tr.run_id = ? AND f.class_id = ? " +
                            "AND (f.effective_from IS NULL OR f.effective_from <= CURDATE()) " +
                            "AND (f.effective_to IS NULL OR f.effective_to >= CURDATE()) ORDER BY f.effective_from DESC LIMIT 1")) {
                ps.setInt(1, runId);
                ps.setInt(2, classId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) fare = rs.getBigDecimal(1);
                }
            }

            // 4) create PNR
            String pnr = "PNR" + System.currentTimeMillis();
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO pnr_status (pnr, status) VALUES (?, 'Confirmed')")) {
                ps.setString(1, pnr);
                ps.executeUpdate();
            }

            // 5) insert booking
            int bookingId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bookings (passenger_id, run_id, pnr, booking_date, total_amount, status) VALUES (?, ?, ?, NOW(), ?, 'Confirmed')",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, passengerId);
                ps.setInt(2, runId);
                ps.setString(3, pnr);
                ps.setBigDecimal(4, fare);
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) bookingId = gk.getInt(1);
                    else throw new SQLException("Failed to create booking");
                }
            }

            // 6) insert ticket
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO tickets (booking_id, seat_id, passenger_name, fare_paid, ticket_status, issued_at) VALUES (?, ?, (SELECT full_name FROM passengers WHERE passenger_id = ?), ?, 'Active', NOW())")) {
                ps.setInt(1, bookingId);
                ps.setInt(2, seatId);
                ps.setInt(3, passengerId);
                ps.setBigDecimal(4, fare);
                ps.executeUpdate();
            }

            // 7) insert payment
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO payments (booking_id, amount, method, transaction_ref, payment_date, status) VALUES (?, ?, ?, ?, NOW(), 'Paid')")) {
                ps.setInt(1, bookingId);
                ps.setBigDecimal(2, fare);
                ps.setString(3, paymentMethod);
                ps.setString(4, txRef);
                ps.executeUpdate();
            }

            conn.commit();
            return Optional.of(pnr);

        } catch (SQLException ex) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            throw ex;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignore) {}
        }
    }
}

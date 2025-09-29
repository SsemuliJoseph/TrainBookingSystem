/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SeatService - helpers to find seats available for a run and class.
 */
public class SeatService {

    public static class SeatInfo {
        public int seatId;
        public String coachLabel;
        public String seatNumber;

        public SeatInfo(int seatId, String coachLabel, String seatNumber) {
            this.seatId = seatId;
            this.coachLabel = coachLabel;
            this.seatNumber = seatNumber;
        }

        @Override
        public String toString() {
            return coachLabel + ":" + seatNumber;
        }
    }

    /**
     * Returns list of available seats (seat_id, coach_label, seat_number) for the given run and class.
     */
    public List<SeatInfo> getAvailableSeatsForRunAndClass(int runId, int classId) throws SQLException {
        List<SeatInfo> out = new ArrayList<>();
        String sql = """
            SELECT s.seat_id, c.coach_label, s.seat_number
            FROM seats s
            JOIN coaches c ON s.coach_id = c.coach_id
            JOIN train_runs tr ON c.train_id = tr.train_id
            WHERE tr.run_id = ? AND s.class_id = ?
              AND s.seat_id NOT IN (
                SELECT t.seat_id FROM tickets t
                JOIN bookings b ON t.booking_id = b.booking_id
                WHERE b.run_id = ?
              )
            ORDER BY c.coach_label, s.seat_number
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, runId);
            ps.setInt(2, classId);
            ps.setInt(3, runId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new SeatInfo(rs.getInt("seat_id"), rs.getString("coach_label"), rs.getString("seat_number")));
                }
            }
        }
        return out;
    }

    /**
     * Get readable label for a seat id.
     */
    public String getSeatLabel(int seatId) throws SQLException {
        String sql = "SELECT c.coach_label, s.seat_number FROM seats s JOIN coaches c ON s.coach_id = c.coach_id WHERE s.seat_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, seatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("coach_label") + ":" + rs.getString("seat_number");
            }
        }
        return "Seat " + seatId;
    }
}


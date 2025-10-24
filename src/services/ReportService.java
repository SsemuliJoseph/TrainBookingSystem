/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import db.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * ReportService - fetches report data from DB for Admin and Passenger reports.
 * Add more reports here as needed.
 */
public class ReportService {

    // --------------- ADMIN REPORTS ---------------

    /**
     * Daily Train Schedules between two dates (inclusive).
     */
    public ReportData getTrainSchedules(java.util.Date from, java.util.Date to) throws SQLException {
        String title = "Train Schedules";
        List<String> headers = Arrays.asList("Run ID", "Train #", "Train Name", "Origin", "Destination", "Date", "Departure", "Arrival", "Status");
        List<List<Object>> rows = new ArrayList<>();

        String sql = """
            SELECT tr.run_id, t.train_number, t.train_name,
                   s1.station_name AS origin, s2.station_name AS destination,
                   tr.run_date, tr.departure_time, tr.arrival_time, tr.status
            FROM train_runs tr
            JOIN trains t ON tr.train_id = t.train_id
            JOIN routes ro ON tr.route_id = ro.route_id
            JOIN stations s1 ON ro.origin_station_id = s1.station_id
            JOIN stations s2 ON ro.destination_station_id = s2.station_id
            WHERE tr.run_date BETWEEN ? AND ?
            ORDER BY tr.run_date, tr.departure_time
            """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(Arrays.asList(
                        rs.getInt("run_id"),
                        rs.getString("train_number"),
                        rs.getString("train_name"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getDate("run_date"),
                        rs.getTime("departure_time"),
                        rs.getTime("arrival_time"),
                        rs.getString("status")
                    ));
                }
            }
        }
        return new ReportData(title, headers, rows);
    }

    /**
     * Bookings Summary by route: counts and revenue.
     */
    public ReportData getBookingsSummary(java.util.Date from, java.util.Date to) throws SQLException {
        String title = "Bookings Summary (by Route)";
        List<String> headers = Arrays.asList("Route ID", "Origin", "Destination", "Bookings", "Revenue (UGX)");
        List<List<Object>> rows = new ArrayList<>();

        String sql = """
            SELECT ro.route_id, s1.station_name AS origin, s2.station_name AS destination,
                   COUNT(b.booking_id) AS bookings, COALESCE(SUM(b.total_amount),0) AS revenue
            FROM bookings b
            JOIN train_runs tr ON b.run_id = tr.run_id
            JOIN routes ro ON tr.route_id = ro.route_id
            JOIN stations s1 ON ro.origin_station_id = s1.station_id
            JOIN stations s2 ON ro.destination_station_id = s2.station_id
            WHERE b.booking_date BETWEEN ? AND ?
            GROUP BY ro.route_id
            ORDER BY bookings DESC
            """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(Arrays.asList(
                        rs.getInt("route_id"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getInt("bookings"),
                        rs.getBigDecimal("revenue")
                    ));
                }
            }
        }
        return new ReportData(title, headers, rows);
    }

    /**
     * Top Routes by bookings (limit top 10).
     */
    public ReportData getTopRoutes(java.util.Date from, java.util.Date to) throws SQLException {
        String title = "Top Routes (by Bookings)";
        List<String> headers = Arrays.asList("Rank", "Route ID", "Origin", "Destination", "Bookings");
        List<List<Object>> rows = new ArrayList<>();

        String sql = """
            SELECT ro.route_id, s1.station_name AS origin, s2.station_name AS destination, COUNT(b.booking_id) AS bookings
            FROM bookings b
            JOIN train_runs tr ON b.run_id = tr.run_id
            JOIN routes ro ON tr.route_id = ro.route_id
            JOIN stations s1 ON ro.origin_station_id = s1.station_id
            JOIN stations s2 ON ro.destination_station_id = s2.station_id
            WHERE b.booking_date BETWEEN ? AND ?
            GROUP BY ro.route_id
            ORDER BY bookings DESC
            LIMIT 10
            """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    rows.add(Arrays.asList(
                        rank++,
                        rs.getInt("route_id"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getInt("bookings")
                    ));
                }
            }
        }
        return new ReportData(title, headers, rows);
    }

    /**
     * Cancellations detail in date range.
     */
    public ReportData getCancellations(java.util.Date from, java.util.Date to) throws SQLException {
        String title = "Cancellations Report";
        List<String> headers = Arrays.asList("Cancel ID", "Booking ID", "PNR", "Requested By (user_id)", "Request Date", "Reason", "Refund Amount", "Status");
        List<List<Object>> rows = new ArrayList<>();

        String sql = """
            SELECT c.cancel_id, c.booking_id, b.pnr, c.requested_by, c.request_date, c.reason, c.refund_amount, c.status
            FROM cancellations c
            JOIN bookings b ON c.booking_id = b.booking_id
            WHERE c.request_date BETWEEN ? AND ?
            ORDER BY c.request_date DESC
            """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(Arrays.asList(
                        rs.getInt("cancel_id"),
                        rs.getInt("booking_id"),
                        rs.getString("pnr"),
                        rs.getObject("requested_by"),
                        rs.getTimestamp("request_date"),
                        rs.getString("reason"),
                        rs.getBigDecimal("refund_amount"),
                        rs.getString("status")
                    ));
                }
            }
        }
        return new ReportData(title, headers, rows);
    }

    /**
     * Train utilization: percent occupancy per train in date range.
     */
    public ReportData getTrainUtilization(java.util.Date from, java.util.Date to) throws SQLException {
        String title = "Train Utilization (Occupancy %)";
        List<String> headers = Arrays.asList("Train ID", "Train Number", "Train Name", "Total Seats", "Booked Seats", "Occupancy (%)");
        List<List<Object>> rows = new ArrayList<>();

        String sql = """
            SELECT t.train_id, t.train_number, t.train_name,
                   COALESCE(seats.total_seats,0) AS total_seats,
                   COALESCE(booked.booked_seats,0) AS booked_seats,
                   (COALESCE(booked.booked_seats,0) / NULLIF(COALESCE(seats.total_seats,0),0)) * 100 AS occupancy_pct
            FROM trains t
            LEFT JOIN (
              SELECT c.train_id, COUNT(s.seat_id) AS total_seats
              FROM seats s JOIN coaches c ON s.coach_id = c.coach_id
              GROUP BY c.train_id
            ) seats ON seats.train_id = t.train_id
            LEFT JOIN (
              SELECT tr.train_id, COUNT(tic.ticket_id) AS booked_seats
              FROM tickets tic
              JOIN bookings b ON tic.booking_id = b.booking_id
              JOIN train_runs tr ON b.run_id = tr.run_id
              WHERE b.booking_date BETWEEN ? AND ?
              GROUP BY tr.train_id
            ) booked ON booked.train_id = t.train_id
            ORDER BY occupancy_pct DESC
            """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int total = rs.getInt("total_seats");
                    int booked = rs.getInt("booked_seats");
                    double occ = 0.0;
                    if (total > 0) occ = (100.0 * booked) / total;
                    rows.add(Arrays.asList(
                        rs.getInt("train_id"),
                        rs.getString("train_number"),
                        rs.getString("train_name"),
                        total,
                        booked,
                        String.format(Locale.US, "%.2f", occ)
                    ));
                }
            }
        }
        return new ReportData(title, headers, rows);
    }

    // ---------- PASSENGER reports can be added similarly ----------
    /**
     * Passenger booking history (example) - provide passengerUserId = users.user_id
     */
    public ReportData getPassengerBookingHistory(int userId) throws SQLException {
        String title = "Passenger Booking History";
        List<String> headers = Arrays.asList("Booking ID", "PNR", "Run Date", "Train", "Origin", "Destination", "Amount", "Status");
        List<List<Object>> rows = new ArrayList<>();

        String sql = """
            SELECT b.booking_id, b.pnr, tr.run_date, t.train_name,
                   s1.station_name AS origin, s2.station_name AS destination,
                   b.total_amount, b.status
            FROM bookings b
            JOIN passengers p ON b.passenger_id = p.passenger_id
            JOIN train_runs tr ON b.run_id = tr.run_id
            JOIN trains t ON tr.train_id = t.train_id
            JOIN routes ro ON tr.route_id = ro.route_id
            JOIN stations s1 ON ro.origin_station_id = s1.station_id
            JOIN stations s2 ON ro.destination_station_id = s2.station_id
            WHERE p.user_id = ?
            ORDER BY b.booking_date DESC
            """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(Arrays.asList(
                        rs.getInt("booking_id"),
                        rs.getString("pnr"),
                        rs.getDate("run_date"),
                        rs.getString("train_name"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getBigDecimal("total_amount"),
                        rs.getString("status")
                    ));
                }
            }
        }
        return new ReportData(title, headers, rows);
    }
}

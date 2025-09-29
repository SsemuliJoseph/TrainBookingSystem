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
 * ScheduleService - methods to fetch train runs (train_runs).
 * Uses schema: train_runs(run_id, train_id, route_id, run_date, departure_time, arrival_time)
 * and stations.station_name, routes.origin_station_id/destination_station_id.
 */
public class ScheduleService {

    public static class RunInfo {
        public int runId;
        public int trainId;
        public int routeId;
        public Date runDate;
        public Time departure;
        public Time arrival;
        public String trainName;
        public String origin;
        public String destination;

        @Override
        public String toString() {
            return runId + " - " + trainName + " (" + origin + " → " + destination + ") " + runDate + " " + departure;
        }
    }

    /**
     * Get all scheduled runs (limited by 1000).
     */
    public List<RunInfo> getAllRuns() throws SQLException {
        List<RunInfo> out = new ArrayList<>();
        String sql = """
            SELECT tr.run_id, tr.train_id, tr.route_id, tr.run_date, tr.departure_time, tr.arrival_time,
                   t.train_name,
                   s1.station_name AS origin, s2.station_name AS destination
            FROM train_runs tr
            JOIN trains t ON tr.train_id = t.train_id
            JOIN routes ro ON tr.route_id = ro.route_id
            JOIN stations s1 ON ro.origin_station_id = s1.station_id
            JOIN stations s2 ON ro.destination_station_id = s2.station_id
            ORDER BY tr.run_date, tr.departure_time
            LIMIT 1000
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RunInfo r = new RunInfo();
                r.runId = rs.getInt("run_id");
                r.trainId = rs.getInt("train_id");
                r.routeId = rs.getInt("route_id");
                r.runDate = rs.getDate("run_date");
                r.departure = rs.getTime("departure_time");
                r.arrival = rs.getTime("arrival_time");
                r.trainName = rs.getString("train_name");
                r.origin = rs.getString("origin");
                r.destination = rs.getString("destination");
                out.add(r);
            }
        }
        return out;
    }

    /**
     * Search runs by origin station name and destination station name and optional runDate.
     * Pass null for runDate to ignore date filter.
     */
    public List<RunInfo> searchRuns(String originLike, String destinationLike, Date runDate) throws SQLException {
        List<RunInfo> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder("""
            SELECT tr.run_id, tr.train_id, tr.route_id, tr.run_date, tr.departure_time, tr.arrival_time,
                   t.train_name,
                   s1.station_name AS origin, s2.station_name AS destination
            FROM train_runs tr
            JOIN trains t ON tr.train_id = t.train_id
            JOIN routes ro ON tr.route_id = ro.route_id
            JOIN stations s1 ON ro.origin_station_id = s1.station_id
            JOIN stations s2 ON ro.destination_station_id = s2.station_id
            WHERE s1.station_name LIKE ? AND s2.station_name LIKE ?
            """);
        if (runDate != null) {
            sb.append(" AND tr.run_date = ? ");
        }
        sb.append(" ORDER BY tr.run_date, tr.departure_time LIMIT 1000");

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {
            ps.setString(1, "%" + originLike + "%");
            ps.setString(2, "%" + destinationLike + "%");
            if (runDate != null) ps.setDate(3, runDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RunInfo r = new RunInfo();
                    r.runId = rs.getInt("run_id");
                    r.trainId = rs.getInt("train_id");
                    r.routeId = rs.getInt("route_id");
                    r.runDate = rs.getDate("run_date");
                    r.departure = rs.getTime("departure_time");
                    r.arrival = rs.getTime("arrival_time");
                    r.trainName = rs.getString("train_name");
                    r.origin = rs.getString("origin");
                    r.destination = rs.getString("destination");
                    out.add(r);
                }
            }
        }
        return out;
    }

    /**
     * Get RunInfo by run_id
     */
    public RunInfo getRunById(int runId) throws SQLException {
        String sql = """
            SELECT tr.run_id, tr.train_id, tr.route_id, tr.run_date, tr.departure_time, tr.arrival_time,
                   t.train_name,
                   s1.station_name AS origin, s2.station_name AS destination
            FROM train_runs tr
            JOIN trains t ON tr.train_id = t.train_id
            JOIN routes ro ON tr.route_id = ro.route_id
            JOIN stations s1 ON ro.origin_station_id = s1.station_id
            JOIN stations s2 ON ro.destination_station_id = s2.station_id
            WHERE tr.run_id = ?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, runId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RunInfo r = new RunInfo();
                    r.runId = rs.getInt("run_id");
                    r.trainId = rs.getInt("train_id");
                    r.routeId = rs.getInt("route_id");
                    r.runDate = rs.getDate("run_date");
                    r.departure = rs.getTime("departure_time");
                    r.arrival = rs.getTime("arrival_time");
                    r.trainName = rs.getString("train_name");
                    r.origin = rs.getString("origin");
                    r.destination = rs.getString("destination");
                    return r;
                }
            }
        }
        return null;
    }
}

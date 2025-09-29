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
 * RouteService - helpers for routes.
 */
public class RouteService {

    public static class RouteInfo {
        public int routeId;
        public String origin;
        public String destination;
        public double distanceKm;
        public String label() { return origin + " → " + destination; }
    }

    /**
     * Returns list of all routes with station names.
     */
    public List<RouteInfo> getAllRoutes() throws SQLException {
        List<RouteInfo> out = new ArrayList<>();
        String sql = "SELECT r.route_id, s1.station_name AS origin, s2.station_name AS destination, r.distance_km " +
                     "FROM routes r JOIN stations s1 ON r.origin_station_id = s1.station_id " +
                     "JOIN stations s2 ON r.destination_station_id = s2.station_id ORDER BY r.route_id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RouteInfo ri = new RouteInfo();
                ri.routeId = rs.getInt("route_id");
                ri.origin = rs.getString("origin");
                ri.destination = rs.getString("destination");
                ri.distanceKm = rs.getDouble("distance_km");
                out.add(ri);
            }
        }
        return out;
    }

    /**
     * Create route
     */
    public int createRoute(int originStationId, int destinationStationId, double distanceKm) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO routes (origin_station_id, destination_station_id, distance_km) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, originStationId);
            ps.setInt(2, destinationStationId);
            ps.setDouble(3, distanceKm);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) return gk.getInt(1); }
        }
        return -1;
    }
}

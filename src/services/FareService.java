/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import db.DBConnection;

import java.math.BigDecimal;
import java.sql.*;

/**
 * FareService - find active fare per run (via route) and seat class.
 * Schema: fares(fare_id, route_id, class_id, amount, effective_from, effective_to)
 */
public class FareService {

    /**
     * Get the active fare for a given run_id and class_id.
     * Returns BigDecimal.ZERO if none found.
     */
    public BigDecimal getActiveFareForRunAndClass(int runId, int classId) throws SQLException {
        String sql = """
            SELECT f.amount
            FROM fares f
            JOIN train_runs tr ON f.route_id = tr.route_id
            WHERE tr.run_id = ? AND f.class_id = ?
              AND (f.effective_from IS NULL OR f.effective_from <= CURDATE())
              AND (f.effective_to IS NULL OR f.effective_to >= CURDATE())
            ORDER BY f.effective_from DESC
            LIMIT 1
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, runId);
            ps.setInt(2, classId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("amount");
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get fare for route+class (if needed).
     */
    public BigDecimal getFareForRouteAndClass(int routeId, int classId) throws SQLException {
        String sql = """
            SELECT f.amount
            FROM fares f
            WHERE f.route_id = ? AND f.class_id = ?
              AND (f.effective_from IS NULL OR f.effective_from <= CURDATE())
              AND (f.effective_to IS NULL OR f.effective_to >= CURDATE())
            ORDER BY f.effective_from DESC
            LIMIT 1
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            ps.setInt(2, classId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("amount");
            }
        }
        return BigDecimal.ZERO;
    }
}

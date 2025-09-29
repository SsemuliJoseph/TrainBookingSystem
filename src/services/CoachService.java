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
 * CoachService - manage coaches and seats generation.
 */
public class CoachService {

    public static class CoachInfo {
        public int coachId;
        public int trainId;
        public String coachLabel;
        public String coachType;
        public int capacity;
    }

    public List<CoachInfo> getCoachesForTrain(int trainId) throws SQLException {
        List<CoachInfo> out = new ArrayList<>();
        String sql = "SELECT coach_id, train_id, coach_label, coach_type, capacity FROM coaches WHERE train_id = ? ORDER BY coach_id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CoachInfo ci = new CoachInfo();
                    ci.coachId = rs.getInt("coach_id");
                    ci.trainId = rs.getInt("train_id");
                    ci.coachLabel = rs.getString("coach_label");
                    ci.coachType = rs.getString("coach_type");
                    ci.capacity = rs.getInt("capacity");
                    out.add(ci);
                }
            }
        }
        return out;
    }

    public int createCoach(int trainId, String label, String type, int capacity) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO coaches (train_id, coach_label, coach_type, capacity) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, trainId);
            ps.setString(2, label);
            ps.setString(3, type);
            ps.setInt(4, capacity);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) return gk.getInt(1); }
        }
        return -1;
    }

    /**
     * Helper: generate seats for a coach. Seat numbers will be like A1..A{capacity} where coachLabel provided.
     * classId is the seat class (e.g., sleeper, economy).
     */
    public void generateSeatsForCoach(int coachId, String coachLabel, int classId, int capacity) throws SQLException {
        String insert = "INSERT INTO seats (coach_id, seat_number, class_id, is_available) VALUES (?, ?, ?, 1)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(insert)) {
            c.setAutoCommit(false);
            try {
                for (int i = 1; i <= capacity; i++) {
                    String seatNum = coachLabel + i;
                    ps.setInt(1, coachId);
                    ps.setString(2, seatNum);
                    ps.setInt(3, classId);
                    ps.addBatch();
                }
                ps.executeBatch();
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }
}


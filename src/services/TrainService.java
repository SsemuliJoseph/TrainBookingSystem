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
 * TrainService - helpers to list/create trains.
 */
public class TrainService {

    public static class TrainInfo {
        public int trainId;
        public String trainNumber;
        public String trainName;
    }

    public List<TrainInfo> getActiveTrains() throws SQLException {
        List<TrainInfo> out = new ArrayList<>();
        String sql = "SELECT train_id, train_number, train_name FROM trains WHERE active = 1 ORDER BY train_id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TrainInfo ti = new TrainInfo();
                ti.trainId = rs.getInt("train_id");
                ti.trainNumber = rs.getString("train_number");
                ti.trainName = rs.getString("train_name");
                out.add(ti);
            }
        }
        return out;
    }

    public int createTrain(String trainNumber, String trainName) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO trains (train_number, train_name, active) VALUES (?, ?, 1)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, trainNumber);
            ps.setString(2, trainName);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) return gk.getInt(1); }
        }
        return -1;
    }
}

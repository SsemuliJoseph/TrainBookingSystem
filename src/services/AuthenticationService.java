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
import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

/**
 * AuthenticationService - login & register passenger.
 * Uses users.password_hash column (VARCHAR) for bcrypt hashes.
 */
public class AuthenticationService {

    /**
     * Authenticate user by username/password.
     * Returns User on success, null on failure.
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.password_hash, r.role_name " +
                     "FROM users u JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.username = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    if (hash != null && BCrypt.checkpw(password, hash)) {
                        return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("role_name"));
                    }
                }
            }
        }
        return null;
    }

    /**
     * Register a new passenger: creates users + passengers rows inside a transaction.
     * Returns the created User.
     */
    public User registerPassenger(String username, String plainPassword, String fullName, String gender, Date dob) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // get role_id for Passenger
                int roleId = -1;
                try (PreparedStatement ps = c.prepareStatement("SELECT role_id FROM roles WHERE role_name = 'Passenger'")) {
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) roleId = rs.getInt(1); }
                }
                if (roleId == -1) throw new SQLException("Passenger role not present in roles table");

                String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
                int userId;
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO users (username, password_hash, role_id, created_at) VALUES (?, ?, ?, NOW())", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, username);
                    ps.setString(2, hashed);
                    ps.setInt(3, roleId);
                    ps.executeUpdate();
                    try (ResultSet gk = ps.getGeneratedKeys()) {
                        if (gk.next()) userId = gk.getInt(1);
                        else throw new SQLException("Failed to create user");
                    }
                }

                try (PreparedStatement ps = c.prepareStatement("INSERT INTO passengers (user_id, full_name, gender, dob) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, userId);
                    ps.setString(2, fullName);
                    ps.setString(3, gender);
                    ps.setDate(4, dob);
                    ps.executeUpdate();
                }

                c.commit();
                return new User(userId, username, "Passenger");
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import db.DBConnection;
import java.sql.*;

public class PaymentService {
    public static boolean makePayment(int bookingId, double amount, String method, String transactionRef) {
        String sql = "INSERT INTO payments (booking_id, amount, method, transaction_ref, status) VALUES (?, ?, ?, ?, 'Paid')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setDouble(2, amount);
            ps.setString(3, method);
            ps.setString(4, transactionRef);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

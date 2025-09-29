/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import db.DBConnection;
import java.sql.*;

public class CancelService {
    public static boolean cancelBooking(int bookingId) {
        String updateBooking = "UPDATE bookings SET status='Cancelled' WHERE booking_id=?";
        String updateTicket = "UPDATE tickets SET ticket_status='Cancelled' WHERE booking_id=?";
        String refund = "INSERT INTO refunds (booking_id, amount, refund_status, refund_date) " +
                        "SELECT b.booking_id, p.amount, 'Processed', NOW() " +
                        "FROM bookings b JOIN payments p ON b.booking_id=p.booking_id " +
                        "WHERE b.booking_id=?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(updateBooking);
                 PreparedStatement ps2 = conn.prepareStatement(updateTicket);
                 PreparedStatement ps3 = conn.prepareStatement(refund)) {

                ps1.setInt(1, bookingId);
                ps2.setInt(1, bookingId);
                ps3.setInt(1, bookingId);

                ps1.executeUpdate();
                ps2.executeUpdate();
                ps3.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

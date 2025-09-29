/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ssemu
 */
package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PnrStatusForm extends JFrame {
    private final JTextField txtPnr = new JTextField();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"PNR", "Booking ID", "Train", "Run Date", "Seat", "Ticket Status"}, 0);
    private final JTable table = new JTable(model);

    public PnrStatusForm() {
        setTitle("PNR Status");
        setSize(900, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(new JLabel("Enter PNR:"), BorderLayout.WEST);
        top.add(txtPnr, BorderLayout.CENTER);
        JButton btn = new JButton("Search");
        top.add(btn, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        btn.addActionListener(e -> searchPnr());
    }

    private void searchPnr() {
        String pnr = txtPnr.getText().trim();
        if (pnr.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter PNR"); return; }
        model.setRowCount(0);

        String sql = """
            SELECT b.pnr, b.booking_id, t.train_name, tr.run_date, s.seat_number, tk.ticket_status
            FROM bookings b
            JOIN tickets tk ON b.booking_id = tk.booking_id
            JOIN train_runs tr ON b.run_id = tr.run_id
            JOIN trains t ON tr.train_id = t.train_id
            JOIN seats s ON tk.seat_id = s.seat_id
            WHERE b.pnr = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    model.addRow(new Object[]{
                            rs.getString("pnr"),
                            rs.getInt("booking_id"),
                            rs.getString("train_name"),
                            rs.getDate("run_date"),
                            rs.getString("seat_number"),
                            rs.getString("ticket_status")
                    });
                }
                if (!found) JOptionPane.showMessageDialog(this, "PNR not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PnrStatusForm().setVisible(true));
    }
}


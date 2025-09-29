/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchTrainsForm extends JFrame {
    private JComboBox<String> originBox, destinationBox;
    private JTextField dateField;
    private JButton searchButton;
    private JTable resultsTable;

    public SearchTrainsForm() {
        setTitle("🔍 Search Trains");
        setSize(800, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for inputs
        JPanel inputPanel = new JPanel(new FlowLayout());

        originBox = new JComboBox<>();
        destinationBox = new JComboBox<>();
        dateField = new JTextField(10);
        searchButton = new JButton("Search");

        inputPanel.add(new JLabel("From:"));
        inputPanel.add(originBox);
        inputPanel.add(new JLabel("To:"));
        inputPanel.add(destinationBox);
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        inputPanel.add(dateField);
        inputPanel.add(searchButton);

        add(inputPanel, BorderLayout.NORTH);

        // Results table
        resultsTable = new JTable();
        resultsTable.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Run ID", "Train Name", "Departure", "Arrival", "Duration (min)", "Status"}
        ));
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // Load stations
        loadStations();

        // Search button action
        searchButton.addActionListener(e -> searchTrains());
    }

    private void loadStations() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT station_name FROM stations ORDER BY station_name")) {

            while (rs.next()) {
                String station = rs.getString("station_name");
                originBox.addItem(station);
                destinationBox.addItem(station);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading stations: " + e.getMessage());
        }
    }

    private void searchTrains() {
        String origin = (String) originBox.getSelectedItem();
        String destination = (String) destinationBox.getSelectedItem();
        String travelDate = dateField.getText();

        if (origin.equals(destination)) {
            JOptionPane.showMessageDialog(this, "Origin and destination cannot be the same!");
            return;
        }

        String sql = """
            SELECT tr.run_id, t.train_name, tr.departure_time, tr.arrival_time, tr.duration_minutes, tr.status
            FROM train_runs tr
            JOIN trains t ON tr.train_id = t.train_id
            JOIN routes r ON tr.route_id = r.route_id
            JOIN stations s1 ON r.origin_station_id = s1.station_id
            JOIN stations s2 ON r.destination_station_id = s2.station_id
            WHERE s1.station_name = ? AND s2.station_name = ? AND tr.run_date = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, origin);
            ps.setString(2, destination);
            ps.setString(3, travelDate);

            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
            model.setRowCount(0); // clear old results

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("run_id"),
                    rs.getString("train_name"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    rs.getInt("duration_minutes"),
                    rs.getString("status")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No trains found for this route/date.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching trains: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchTrainsForm().setVisible(true));
    }
}


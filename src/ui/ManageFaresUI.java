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

/**
 * ManageFaresUI - list/add/delete fares
 * fares(fare_id, route_id, class_id, amount, effective_from, effective_to)
 */
public class ManageFaresUI extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"fare_id","route","class","amount","from","to"}, 0);
    private final JTable table = new JTable(model);

    public ManageFaresUI() {
        setTitle("Manage Fares");
        setSize(900,420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Add Fare"), btnDelete = new JButton("Delete Selected");
        bottom.add(btnAdd); bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addFareDialog());
        btnDelete.addActionListener(e -> deleteSelected());

        loadFares();
    }

    private void loadFares() {
        model.setRowCount(0);
        String sql = """
            SELECT f.fare_id, s1.station_name AS origin, s2.station_name AS destination, sc.class_name, f.amount, f.effective_from, f.effective_to
            FROM fares f
            JOIN routes r ON f.route_id = r.route_id
            JOIN stations s1 ON r.origin_station_id = s1.station_id
            JOIN stations s2 ON r.destination_station_id = s2.station_id
            JOIN seat_classes sc ON f.class_id = sc.class_id
            ORDER BY f.fare_id
            """;
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String route = rs.getString("origin")+" → "+rs.getString("destination");
                model.addRow(new Object[]{rs.getInt("fare_id"), route, rs.getString("class_name"), rs.getBigDecimal("amount"), rs.getDate("effective_from"), rs.getDate("effective_to")});
            }
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }

    private void addFareDialog() {
        try (Connection c = DBConnection.getConnection()) {
            // collect routes
            DefaultComboBoxModel<String> routeModel = new DefaultComboBoxModel<>();
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT r.route_id, s1.station_name AS origin, s2.station_name AS destination FROM routes r JOIN stations s1 ON r.origin_station_id=s1.station_id JOIN stations s2 ON r.destination_station_id=s2.station_id")) {
                while (rs.next()) routeModel.addElement(rs.getInt("route_id")+" - "+rs.getString("origin")+" → "+rs.getString("destination"));
            }

            // collect classes
            DefaultComboBoxModel<String> classModel = new DefaultComboBoxModel<>();
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT class_id, class_name FROM seat_classes ORDER BY class_name")) {
                while (rs.next()) classModel.addElement(rs.getInt("class_id")+" - "+rs.getString("class_name"));
            }

            JComboBox<String> cbRoute = new JComboBox<>(routeModel);
            JComboBox<String> cbClass = new JComboBox<>(classModel);
            JTextField tfAmount = new JTextField("amount (eg 50000.00)");
            JTextField tfFrom = new JTextField("YYYY-MM-DD");
            JTextField tfTo = new JTextField("YYYY-MM-DD (or blank)");

            JPanel p = new JPanel(new GridLayout(5,2,6,6));
            p.add(new JLabel("Route:")); p.add(cbRoute);
            p.add(new JLabel("Class:")); p.add(cbClass);
            p.add(new JLabel("Amount:")); p.add(tfAmount);
            p.add(new JLabel("Effective From:")); p.add(tfFrom);
            p.add(new JLabel("Effective To:")); p.add(tfTo);

            int res = JOptionPane.showConfirmDialog(this, p, "Add Fare", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;

            int routeId = Integer.parseInt(((String)cbRoute.getSelectedItem()).split(" - ")[0]);
            int classId = Integer.parseInt(((String)cbClass.getSelectedItem()).split(" - ")[0]);
            java.math.BigDecimal amount = new java.math.BigDecimal(tfAmount.getText().trim());
            Date effFrom = Date.valueOf(tfFrom.getText().trim());
            Date effTo = null;
            if (!tfTo.getText().trim().isEmpty()) effTo = Date.valueOf(tfTo.getText().trim());

            String insert = "INSERT INTO fares (route_id, class_id, amount, effective_from, effective_to) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(insert)) {
                ps.setInt(1, routeId);
                ps.setInt(2, classId);
                ps.setBigDecimal(3, amount);
                ps.setDate(4, effFrom);
                if (effTo == null) ps.setNull(5, Types.DATE); else ps.setDate(5, effTo);
                ps.executeUpdate();
                loadFares();
            }
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Error adding fare: "+ex.getMessage()); }
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this,"Select a fare to delete"); return; }
        int id = (int) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this,"Delete fare " + id + " ?") != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM fares WHERE fare_id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); ps.executeUpdate(); loadFares(); }
        catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Delete error: "+ex.getMessage()); }
    }
}

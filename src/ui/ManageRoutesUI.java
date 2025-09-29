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
 * ManageRoutesUI - list/add/delete routes (route_id, origin_station_id, destination_station_id, distance_km)
 */
public class ManageRoutesUI extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"route_id","origin","destination","distance_km"}, 0);
    private final JTable table = new JTable(model);

    public ManageRoutesUI() {
        setTitle("Manage Routes");
        setSize(700,380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Add Route"), btnDelete = new JButton("Delete Selected");
        bottom.add(btnAdd); bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addRouteDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        loadRoutes();
    }

    private void loadRoutes() {
        model.setRowCount(0);
        String sql = "SELECT r.route_id, s1.station_name AS origin, s2.station_name AS destination, r.distance_km FROM routes r JOIN stations s1 ON r.origin_station_id=s1.station_id JOIN stations s2 ON r.destination_station_id=s2.station_id ORDER BY r.route_id";
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt("route_id"), rs.getString("origin"), rs.getString("destination"), rs.getBigDecimal("distance_km")});
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
    }

    private void addRouteDialog() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT station_id, station_name FROM stations ORDER BY station_name")) {

            DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
            while (rs.next()) cbModel.addElement(rs.getInt("station_id") + " - " + rs.getString("station_name"));

            JComboBox<String> cbOrigin = new JComboBox<>(cbModel);
            JComboBox<String> cbDest = new JComboBox<>(cbModel);
            JTextField tfDist = new JTextField("Distance in km (e.g. 200)");

            JPanel panel = new JPanel(new GridLayout(3,2,6,6));
            panel.add(new JLabel("Origin:")); panel.add(cbOrigin);
            panel.add(new JLabel("Destination:")); panel.add(cbDest);
            panel.add(new JLabel("Distance (km):")); panel.add(tfDist);

            int res = JOptionPane.showConfirmDialog(this, panel, "Add Route", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;

            int originId = Integer.parseInt(((String)cbOrigin.getSelectedItem()).split(" - ")[0]);
            int destId = Integer.parseInt(((String)cbDest.getSelectedItem()).split(" - ")[0]);
            double dist = Double.parseDouble(tfDist.getText().trim());

            String insert = "INSERT INTO routes (origin_station_id, destination_station_id, distance_km) VALUES (?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(insert)) {
                ps.setInt(1, originId); ps.setInt(2, destId); ps.setDouble(3, dist);
                ps.executeUpdate();
                loadRoutes();
            }
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error adding route: "+ex.getMessage()); }
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this,"Select a route"); return; }
        int id = (int) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this,"Delete route " + id + " ?") != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM routes WHERE route_id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); ps.executeUpdate(); loadRoutes(); }
        catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Delete error: "+ex.getMessage()); }
    }
}

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
 * ManageStationsUI - CRUD for stations (station_id, station_name, country)
 */
public class ManageStationsUI extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"station_id","station_name","country"}, 0);
    private final JTable table = new JTable(model);

    public ManageStationsUI() {
        setTitle("Manage Stations");
        setSize(600,350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Add Station");
        JButton btnDelete = new JButton("Delete Selected");
        bottom.add(btnAdd); bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addStation());
        btnDelete.addActionListener(e -> deleteSelected());

        loadStations();
    }

    private void loadStations() {
        model.setRowCount(0);
        String sql = "SELECT station_id, station_name, country FROM stations ORDER BY station_name";
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt("station_id"), rs.getString("station_name"), rs.getString("country")});
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
    }

    private void addStation() {
        String name = JOptionPane.showInputDialog(this, "Station name (e.g. Kampala):");
        if (name==null || name.trim().isEmpty()) return;
        String country = JOptionPane.showInputDialog(this, "Country (e.g. Uganda):");
        if (country==null || country.trim().isEmpty()) country = "Uganda";
        String sql = "INSERT INTO stations (station_name, country) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name.trim()); ps.setString(2, country.trim()); ps.executeUpdate(); loadStations();
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Add error: "+ex.getMessage()); }
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this,"Select a station"); return; }
        int id = (int) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this,"Delete station " + id + " ?") != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM stations WHERE station_id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); ps.executeUpdate(); loadStations(); }
        catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Delete error: "+ex.getMessage()); }
    }
}

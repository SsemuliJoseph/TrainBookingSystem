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

public class ManageTrainRunsUI extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"run_id","train_id","route_id","run_date","departure_time","arrival_time","status"},0);
    private final JTable table = new JTable(model);

    public ManageTrainRunsUI() {
        setTitle("Manage Train Runs");
        setSize(900,420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Add Run"), btnDelete = new JButton("Delete Selected");
        bottom.add(btnAdd); bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);
        btnAdd.addActionListener(e -> addRunDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        loadRuns();
    }

    private void loadRuns() {
        model.setRowCount(0);
        String sql = "SELECT run_id, train_id, route_id, run_date, departure_time, arrival_time, status FROM train_runs ORDER BY run_date, departure_time";
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt("run_id"), rs.getInt("train_id"), rs.getInt("route_id"), rs.getDate("run_date"), rs.getTime("departure_time"), rs.getTime("arrival_time"), rs.getString("status")});
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Load runs error: "+ex.getMessage()); }
    }

    private void addRunDialog() {
        try (Connection c = DBConnection.getConnection()) {
            // select train
            DefaultComboBoxModel<String> trainModel = new DefaultComboBoxModel<>();
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT train_id, train_number, train_name FROM trains")) {
                while (rs.next()) trainModel.addElement(rs.getInt("train_id")+" - "+rs.getString("train_number")+" | "+rs.getString("train_name"));
            }
            DefaultComboBoxModel<String> routeModel = new DefaultComboBoxModel<>();
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT r.route_id, s1.station_name AS origin, s2.station_name AS destination FROM routes r JOIN stations s1 ON r.origin_station_id=s1.station_id JOIN stations s2 ON r.destination_station_id=s2.station_id")) {
                while (rs.next()) routeModel.addElement(rs.getInt("route_id")+" - "+rs.getString("origin")+" → "+rs.getString("destination"));
            }

            JComboBox<String> cbTrain = new JComboBox<>(trainModel);
            JComboBox<String> cbRoute = new JComboBox<>(routeModel);
            JTextField tfDate = new JTextField("YYYY-MM-DD");
            JTextField tfDep = new JTextField("HH:MM:SS");
            JTextField tfArr = new JTextField("HH:MM:SS");
            JTextField tfDur = new JTextField("duration_mins");

            JPanel p = new JPanel(new GridLayout(6,2,6,6));
            p.add(new JLabel("Train:")); p.add(cbTrain);
            p.add(new JLabel("Route:")); p.add(cbRoute);
            p.add(new JLabel("Run Date:")); p.add(tfDate);
            p.add(new JLabel("Departure (HH:MM:SS):")); p.add(tfDep);
            p.add(new JLabel("Arrival (HH:MM:SS):")); p.add(tfArr);
            p.add(new JLabel("Duration (mins):")); p.add(tfDur);

            int res = JOptionPane.showConfirmDialog(this, p, "Add Run", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;
            int trainId = Integer.parseInt(((String)cbTrain.getSelectedItem()).split(" - ")[0]);
            int routeId = Integer.parseInt(((String)cbRoute.getSelectedItem()).split(" - ")[0]);
            Date runDate = Date.valueOf(tfDate.getText().trim());
            Time dep = Time.valueOf(tfDep.getText().trim());
            Time arr = Time.valueOf(tfArr.getText().trim());
            int dur = Integer.parseInt(tfDur.getText().trim());

            try (PreparedStatement ps = c.prepareStatement("INSERT INTO train_runs (train_id, route_id, run_date, departure_time, arrival_time, duration_minutes, status) VALUES (?, ?, ?, ?, ?, ?, 'Scheduled')")) {
                ps.setInt(1, trainId); ps.setInt(2, routeId); ps.setDate(3, runDate); ps.setTime(4, dep); ps.setTime(5, arr); ps.setInt(6, dur);
                ps.executeUpdate();
                loadRuns();
                JOptionPane.showMessageDialog(this,"Run added");
            }
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Add run error: "+ex.getMessage()); }
    }

    private void deleteSelected() {
        int r = table.getSelectedRow(); if (r==-1) { JOptionPane.showMessageDialog(this,"Select run"); return; }
        int id = (int) model.getValueAt(r,0);
        if (JOptionPane.showConfirmDialog(this,"Delete run "+id+"?")!=JOptionPane.YES_OPTION) return;
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM train_runs WHERE run_id=?")) {
            ps.setInt(1,id); ps.executeUpdate(); loadRuns();
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Delete run error: "+ex.getMessage()); }
    }
}

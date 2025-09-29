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

public class ManageTrainsUI extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"train_id","train_number","train_name","active"}, 0);
    private final JTable table = new JTable(model);

    public ManageTrainsUI() {
        setTitle("Manage Trains");
        setSize(700,400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Add Train"), btnDelete = new JButton("Delete Selected");
        bottom.add(btnAdd); bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);
        btnAdd.addActionListener(e -> addTrain());
        btnDelete.addActionListener(e -> deleteSelected());
        loadTrains();
    }

    private void loadTrains() {
        model.setRowCount(0);
        String sql = "SELECT train_id, train_number, train_name, active FROM trains ORDER BY train_id";
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt("train_id"), rs.getString("train_number"), rs.getString("train_name"), rs.getInt("active")});
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Load trains error: "+ex.getMessage()); }
    }

    private void addTrain() {
        String number = JOptionPane.showInputDialog(this, "Train number:");
        if (number==null || number.trim().isEmpty()) return;
        String name = JOptionPane.showInputDialog(this, "Train name:");
        if (name==null || name.trim().isEmpty()) return;
        String sql = "INSERT INTO trains (train_number, train_name, active) VALUES (?, ?, 1)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, number.trim()); ps.setString(2, name.trim()); ps.executeUpdate(); loadTrains();
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Add train error: "+ex.getMessage()); }
    }

    private void deleteSelected() {
        int r = table.getSelectedRow(); if (r==-1) { JOptionPane.showMessageDialog(this,"Select a train"); return; }
        int id = (int) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this,"Delete train "+id+"?")!=JOptionPane.YES_OPTION) return;
        String sql="DELETE FROM trains WHERE train_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1,id); ps.executeUpdate(); loadTrains(); }
        catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Delete error: "+ex.getMessage()); }
    }
}

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
 * ManageCoachesUI - create/delete/list coaches
 * coaches(coach_id, train_id, coach_label, coach_type, capacity)
 */
public class ManageCoachesUI extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"coach_id","train_id","coach_label","coach_type","capacity"}, 0);
    private final JTable table = new JTable(model);

    public ManageCoachesUI() {
        setTitle("Manage Coaches");
        setSize(700,380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Add Coach"), btnDelete = new JButton("Delete Selected");
        bottom.add(btnAdd); bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addCoachDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        loadCoaches();
    }

    private void loadCoaches() {
        model.setRowCount(0);
        String sql = "SELECT coach_id, train_id, coach_label, coach_type, capacity FROM coaches ORDER BY coach_id";
        try (Connection c = DBConnection.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt("coach_id"), rs.getInt("train_id"), rs.getString("coach_label"), rs.getString("coach_type"), rs.getInt("capacity")});
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }

    private void addCoachDialog() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT train_id, train_number, train_name FROM trains ORDER BY train_id")) {

            DefaultComboBoxModel<String> trainModel = new DefaultComboBoxModel<>();
            while (rs.next()) trainModel.addElement(rs.getInt("train_id")+" - "+rs.getString("train_number")+" | "+rs.getString("train_name"));

            JComboBox<String> cbTrain = new JComboBox<>(trainModel);
            JTextField tfLabel = new JTextField("Coach label (e.g. A, B)");
            JTextField tfType = new JTextField("coach type (Sleeper/AC)");
            JTextField tfCap = new JTextField("capacity (e.g. 40)");

            JPanel p = new JPanel(new GridLayout(4,2,6,6));
            p.add(new JLabel("Train:")); p.add(cbTrain);
            p.add(new JLabel("Coach Label:")); p.add(tfLabel);
            p.add(new JLabel("Coach Type:")); p.add(tfType);
            p.add(new JLabel("Capacity:")); p.add(tfCap);

            int res = JOptionPane.showConfirmDialog(this, p, "Add Coach", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;

            int trainId = Integer.parseInt(((String)cbTrain.getSelectedItem()).split(" - ")[0]);
            String label = tfLabel.getText().trim();
            String type = tfType.getText().trim();
            int cap = Integer.parseInt(tfCap.getText().trim());

            String insert = "INSERT INTO coaches (train_id, coach_label, coach_type, capacity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(insert)) {
                ps.setInt(1, trainId); ps.setString(2, label); ps.setString(3, type); ps.setInt(4, cap);
                ps.executeUpdate();
                loadCoaches();
            }
        } catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this,"Error adding coach: "+ex.getMessage()); }
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this,"Select a coach"); return; }
        int id = (int) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this,"Delete coach " + id + " ?") != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM coaches WHERE coach_id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { ps.setInt(1, id); ps.executeUpdate(); loadCoaches(); }
        catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Delete error: "+ex.getMessage()); }
    }
}

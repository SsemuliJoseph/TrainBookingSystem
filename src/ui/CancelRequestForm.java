/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ssemu
 */
package ui;

import models.User;
import services.CancellationService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CancelRequestForm extends JFrame {
    private final User user;
    private final JTextField txtBooking = new JTextField();
    private final JTextField txtReason = new JTextField();

    public CancelRequestForm(User user) {
        this.user = user;
        setTitle("Request Cancellation");
        setSize(380, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel p = new JPanel(new GridLayout(3,2,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Booking ID:")); p.add(txtBooking);
        p.add(new JLabel("Reason:")); p.add(txtReason);
        JButton btn = new JButton("Submit"); p.add(new JLabel()); p.add(btn);
        add(p);

        btn.addActionListener(e -> doRequest());
    }

    private void doRequest() {
        try {
            int bookingId = Integer.parseInt(txtBooking.getText().trim());
            String reason = txtReason.getText().trim();
            CancellationService cs = new CancellationService();
            boolean ok = cs.requestCancel(bookingId, user.getUserId(), reason);
            JOptionPane.showMessageDialog(this, ok ? "Cancellation requested" : "Request failed");
            if (ok) dispose();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid booking id", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

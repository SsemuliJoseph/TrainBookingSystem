/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javax.swing.*;
import services.CancellationService;

import java.awt.*;

public class RefundRequestForm extends JFrame {
    private final JTextField txtBooking = new JTextField();

    public RefundRequestForm() {
        setTitle("Process Refund (Admin)");
        setSize(360, 150);
        setLocationRelativeTo(null);
        JPanel p = new JPanel(new GridLayout(2,2,8,8));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel("Booking ID:")); p.add(txtBooking);
        JButton btn = new JButton("Process"); p.add(new JLabel()); p.add(btn);
        add(p);
        btn.addActionListener(e -> {
            try {
                int bookingId = Integer.parseInt(txtBooking.getText().trim());
                boolean ok = CancellationService.processCancellation(bookingId, 0); // processedBy = admin (0)
                JOptionPane.showMessageDialog(this, ok ? "Refund processed" : "Processing failed");
                if (ok) dispose();
            } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
        });
    }
}
 



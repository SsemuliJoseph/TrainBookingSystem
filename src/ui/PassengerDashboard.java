/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import models.User;

import javax.swing.*;
import java.awt.*;

/**
 * PassengerDashboard - fully transparent panel, background image visible.
 */
public class PassengerDashboard extends JFrame {
    private final User user;

    public PassengerDashboard(User user) {
        this.user = user;
        setTitle("Passenger Dashboard - " + user.getUsername());
        setSize(900, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Background image
        ImagePanel bg = new ImagePanel("/resources/images/dashboard_bg.jpg");
        bg.setLayout(new GridBagLayout());
        setContentPane(bg);

        // Buttons panel (fully transparent)
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false); // NO white overlay

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Buttons
        JButton btnBook = subtleButton("Book Ticket");
        JButton btnBookings = subtleButton("My Bookings");
        JButton btnCancel = subtleButton("Request Cancellation");
        JButton btnPNR = subtleButton("Check PNR");

        gbc.gridx = 0; gbc.gridy = 0; buttonPanel.add(btnBook, gbc);
        gbc.gridx = 1; gbc.gridy = 0; buttonPanel.add(btnBookings, gbc);
        gbc.gridx = 0; gbc.gridy = 1; buttonPanel.add(btnCancel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; buttonPanel.add(btnPNR, gbc);

        bg.add(buttonPanel);

        // Actions
        btnBook.addActionListener(e -> new BookingForm(user).setVisible(true));
        btnBookings.addActionListener(e -> new MyBookingsForm(user).setVisible(true));
        btnCancel.addActionListener(e -> new CancelRequestForm(user).setVisible(true));
        btnPNR.addActionListener(e -> new PnrStatusForm().setVisible(true));

        // Responsive resizing
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeButtons(btnBook, btnBookings, btnCancel, btnPNR);
            }
        });
    }

    private JButton subtleButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(100, 150, 220, 180)); // semi-transparent
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setUI(new RoundedButtonUI());
        b.setFont(new Font("Segoe UI", Font.BOLD, 20));

        // Hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(120, 170, 230, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(100, 150, 220, 180));
            }
        });

        return b;
    }

    private void resizeButtons(JButton... buttons) {
        int width = getWidth();
        int height = getHeight();

        int btnWidth = Math.max(200, width / 4);
        int btnHeight = Math.max(100, height / 5);
        int fontSize = Math.max(16, btnHeight / 6);

        for (JButton b : buttons) {
            b.setPreferredSize(new Dimension(btnWidth, btnHeight));
            b.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            b.revalidate();
        }
    }
}

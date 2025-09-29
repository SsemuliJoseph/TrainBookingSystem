/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AdminDashboard - modern, responsive dashboard for admin.
 */
public class AdminDashboard extends JFrame {
    private final User user;

    public AdminDashboard(User user) {
        this.user = user;
        setTitle("Admin Dashboard - " + user.getUsername());
        setSize(950, 600);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Background image
        ImagePanel bg = new ImagePanel("/resources/images/admin_bg.jpg");
        bg.setLayout(new GridBagLayout());
        setContentPane(bg);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu mFile = new JMenu("File");
        JMenuItem miExit = new JMenuItem("Exit");
        miExit.addActionListener(e -> System.exit(0));
        mFile.add(miExit);

        JMenu mReports = new JMenu("Reports");
        JMenuItem miBookings = new JMenuItem("Bookings Report (PDF)");
        miBookings.addActionListener(e -> new ReportGenerator().generateBookingsReportPdf(this));
        mReports.add(miBookings);

        menuBar.add(mFile);
        menuBar.add(mReports);
        setJMenuBar(menuBar);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Buttons
        JButton bStations = subtleButton("Manage Stations");
        JButton bRoutes = subtleButton("Manage Routes");
        JButton bTrains = subtleButton("Manage Trains");
        JButton bRuns = subtleButton("Manage Train Runs");
        JButton bCoaches = subtleButton("Manage Coaches");
        JButton bFares = subtleButton("Manage Fares");

        gbc.gridx = 0; gbc.gridy = 0; buttonPanel.add(bStations, gbc);
        gbc.gridx = 1; gbc.gridy = 0; buttonPanel.add(bRoutes, gbc);
        gbc.gridx = 0; gbc.gridy = 1; buttonPanel.add(bTrains, gbc);
        gbc.gridx = 1; gbc.gridy = 1; buttonPanel.add(bRuns, gbc);
        gbc.gridx = 0; gbc.gridy = 2; buttonPanel.add(bCoaches, gbc);
        gbc.gridx = 1; gbc.gridy = 2; buttonPanel.add(bFares, gbc);

        bg.add(buttonPanel);

        // Button actions
        bStations.addActionListener(e -> new ManageStationsUI().setVisible(true));
        bRoutes.addActionListener(e -> new ManageRoutesUI().setVisible(true));
        bTrains.addActionListener(e -> new ManageTrainsUI().setVisible(true));
        bRuns.addActionListener(e -> new ManageTrainRunsUI().setVisible(true));
        bCoaches.addActionListener(e -> new ManageCoachesUI().setVisible(true));
        bFares.addActionListener(e -> new ManageFaresUI().setVisible(true));

        // Responsive resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeButtons(bStations, bRoutes, bTrains, bRuns, bCoaches, bFares);
            }
        });
    }

    private JButton subtleButton(String text) {
        JButton b = new JButton(text);
        // Semi-transparent, subtle color
        b.setBackground(new Color(120, 150, 200, 180));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setUI(new RoundedButtonUI());
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(140, 170, 220, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(120, 150, 200, 180));
            }
        });

        return b;
    }

    private void resizeButtons(JButton... buttons) {
        int width = getWidth();
        int height = getHeight();

        int btnWidth = Math.max(200, width / 3);
        int btnHeight = Math.max(100, height / 5);
        int fontSize = Math.max(16, btnHeight / 6);

        for (JButton b : buttons) {
            b.setPreferredSize(new Dimension(btnWidth, btnHeight));
            b.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            b.revalidate();
        }
    }
}

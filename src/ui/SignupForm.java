/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ui;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;
import services.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Properties;

public class SignupForm extends JFrame {

    private final JTextField txtFull = new JTextField();
    private final JComboBox<String> cmbGender = new JComboBox<>(new String[]{"M","F"});
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JDatePickerImpl datePicker;

    // Fonts
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
    private final Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 18);

    public SignupForm() {
        setTitle("Passenger Registration");
        setSize(600, 600);
        setMinimumSize(new Dimension(550, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Background image
        ImagePanel bg = new ImagePanel("/resources/images/sign_up.jpg");
        bg.setLayout(new GridBagLayout());

        // Form panel with rounded corners
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 90)); // slightly darker (less transparent)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Date picker
        SqlDateModel model = new SqlDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setPreferredSize(new Dimension(200, 35));
        datePicker.getJFormattedTextField().setPreferredSize(new Dimension(200, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        // Add fields
        addField(formPanel, gbc, "Full Name:", txtFull);
        addField(formPanel, gbc, "Gender:", cmbGender);
        addField(formPanel, gbc, "DOB:", datePicker);
        addField(formPanel, gbc, "Username:", txtUsername);
        addField(formPanel, gbc, "Password:", txtPassword);

        // Buttons row - Register (left), Back (right)
        gbc.gridwidth = 1;
        gbc.gridy++;

        JButton btnRegister = new JButton("Register");
        styleButton(btnRegister, new Color(0, 123, 255, 200));
        gbc.gridx = 0;
        formPanel.add(btnRegister, gbc);

        JButton btnBack = new JButton("Back to Login");
        styleButton(btnBack, new Color(220, 53, 69, 200));
        gbc.gridx = 1;
        formPanel.add(btnBack, gbc);

        bg.add(formPanel);
        setContentPane(bg);

        // Actions
        btnRegister.addActionListener(e -> doRegister());
        btnBack.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        setFonts(formPanel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(Color.BLACK);

        gbc.gridx = 0;
        panel.add(label, gbc);

        field.setFont(fieldFont);
        field.setPreferredSize(new Dimension(200, 35)); // ✅ keep fields large
        field.setForeground(Color.BLACK);
        field.setBackground(new Color(255, 255, 255, 150));
        field.setOpaque(true);

        gbc.gridx = 1;
        panel.add(field, gbc);
        gbc.gridy++;
    }

    private void setFonts(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel) c.setFont(labelFont);
            else if (c instanceof JTextField || c instanceof JPasswordField) c.setFont(fieldFont);
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(buttonFont);
        btn.setPreferredSize(new Dimension(200, 45)); // ✅ same size for both buttons
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setUI(new RoundedButtonUI());
    }

    private void doRegister() {
        String full = txtFull.getText().trim();
        String gender = (String) cmbGender.getSelectedItem();
        Object selected = datePicker.getModel().getValue();
        java.sql.Date dob = null;

        if (selected instanceof java.sql.Date) {
            dob = (java.sql.Date) selected;
        } else if (selected instanceof java.util.Date) {
            dob = new java.sql.Date(((java.util.Date) selected).getTime());
        } else if (selected instanceof Calendar) {
            dob = new java.sql.Date(((Calendar) selected).getTimeInMillis());
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (full.isEmpty() || dob == null || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.length() < 4) {
            JOptionPane.showMessageDialog(this, "Username must be at least 4 characters", "Invalid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters", "Invalid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            AuthenticationService auth = new AuthenticationService();
            auth.registerPassenger(username, password, full, gender, dob);
            JOptionPane.showMessageDialog(this, "Registration successful. Please login.");
            dispose();
            new LoginForm().setVisible(true); // ✅ Redirect after success
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

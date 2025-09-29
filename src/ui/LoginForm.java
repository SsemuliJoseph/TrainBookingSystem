/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * LoginForm.java
 * Handles login for both Passenger and Admin roles
 */
/*
 * LoginForm.java
 * Handles login for both Passenger and Admin roles
 */
package ui;

import models.User;
import services.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JRadioButton rbPassenger, rbAdmin;

    public LoginForm() {
        setTitle("🚆 SGR Train - Login");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set Nimbus Look & Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Background image
        JLabel background = new JLabel(loadBackground("/resources/images/login_bg.jpg"));
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        // Transparent Login Panel
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 100)); // slightly transparent white
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        panel.setPreferredSize(new Dimension(400, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("SGR Train Ticket System Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Username
        gbc.gridy++; gbc.gridwidth = 1;
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.BLACK);
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(lblUsername, gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setOpaque(true);
        txtUsername.setBackground(new Color(255, 255, 255, 150));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0,100), 1, true),
                BorderFactory.createEmptyBorder(8,10,8,10)
        ));
        panel.add(txtUsername, gbc);

        // Password
        gbc.gridy++; gbc.gridx = 0;
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.BLACK);
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(lblPassword, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setOpaque(true);
        txtPassword.setBackground(new Color(255, 255, 255, 150));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0,100), 1, true),
                BorderFactory.createEmptyBorder(8,10,8,10)
        ));
        panel.add(txtPassword, gbc);

        // Role Selection
        rbPassenger = new JRadioButton("Passenger", true);
        rbAdmin = new JRadioButton("Admin");
        rbPassenger.setForeground(Color.BLACK);
        rbAdmin.setForeground(Color.BLACK);
        rbPassenger.setOpaque(false);
        rbAdmin.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        group.add(rbPassenger);
        group.add(rbAdmin);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(rbPassenger, gbc);
        gbc.gridx = 1;
        panel.add(rbAdmin, gbc);

        // Buttons
        JButton btnLogin = createStyledButton("Login");
        JButton btnSignup = createStyledButton("Sign Up");

        gbc.gridy++; gbc.gridx = 0;
        panel.add(btnLogin, gbc);
        gbc.gridx = 1;
        panel.add(btnSignup, gbc);

        background.add(panel);

        // Actions
        btnLogin.addActionListener(e -> {
            login();
            this.dispose(); // Dispose after login pressed
        });
        btnSignup.addActionListener(e -> {
            new SignupForm().setVisible(true);
            this.dispose(); // Dispose after opening signup
        });
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        AuthenticationService authService = new AuthenticationService();
        try {
            User user = authService.login(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Welcome " + username + "!");
                if (rbPassenger.isSelected()) new PassengerDashboard(user).setVisible(true);
                else if (rbAdmin.isSelected()) new AdminDashboard(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon loadBackground(String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image scaled = icon.getImage().getScaledInstance(800, 500, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("⚠️ Background not found: " + path);
            return new ImageIcon();
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(30, 144, 255, 200)); // semi-transparent blue
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setUI(new RoundedButtonUI());
        return btn;
    }
}

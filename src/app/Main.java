/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import ui.LoginForm;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main entry
 */
public class Main {
    public static void main(String[] args) {
        // Set cross-platform look-and-feel or system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}

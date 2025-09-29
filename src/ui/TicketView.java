/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import models.User;

import javax.swing.*;

public class TicketView extends JFrame {
    private final User user;

    public TicketView(User user) {
        this.user = user;
        setTitle("Your Tickets");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(new JLabel("Ticket viewing coming soon for: " + user.getUsername()));
    }
}

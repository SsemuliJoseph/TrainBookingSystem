/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class RoundedButtonUI extends BasicButtonUI {
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JButton button = (JButton) c;
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        JButton b = (JButton) c;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(2, 2, b.getWidth() - 4, b.getHeight() - 4, 25, 25);

        // Button background
        g2.setColor(b.getBackground());
        g2.fillRoundRect(0, 0, b.getWidth() - 4, b.getHeight() - 4, 25, 25);

        // Button text
        FontMetrics fm = g2.getFontMetrics();
        Rectangle r = new Rectangle(0, 0, b.getWidth(), b.getHeight());
        String text = b.getText();
        int x = (r.width - fm.stringWidth(text)) / 2;
        int y = (r.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(b.getForeground());
        g2.drawString(text, x, y);

        g2.dispose();
    }
}

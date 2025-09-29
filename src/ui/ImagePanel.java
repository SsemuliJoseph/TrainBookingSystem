/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/**
 * ImagePanel draws a scaled background image (cover) and allows components on top.
 * Resource path examples: "/resources/images/login_bg.jpg" or a filesystem path.
 */
public class ImagePanel extends JPanel {
    private BufferedImage image;
    private final boolean cover;

    public ImagePanel(String resourcePath) {
        this(resourcePath, true);
    }

    public ImagePanel(String resourcePath, boolean cover) {
        this.cover = cover;
        loadImage(resourcePath);
        setLayout(new BorderLayout());
    }

    private void loadImage(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                image = ImageIO.read(is);
            } else {
                File f = new File(path);
                if (f.exists()) image = ImageIO.read(f);
            }
        } catch (Exception ignored) { image = null; }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) return;
        int w = getWidth(), h = getHeight();
        if (cover) {
            double scale = Math.max((double)w / image.getWidth(), (double)h / image.getHeight());
            int iw = (int)(image.getWidth()*scale), ih = (int)(image.getHeight()*scale);
            int x = (w-iw)/2, y = (h-ih)/2;
            g.drawImage(image, x, y, iw, ih, this);
        } else {
            g.drawImage(image, 0, 0, w, h, this);
        }
    }
}

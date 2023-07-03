package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageViewer extends  JPanel
{
    private final BufferedImage image;
    public ImageViewer(BufferedImage img)
    {
        this.image = img;
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 25, 25, image.getWidth(), image.getHeight(), null);
    }

    public static JFrame viewImage(BufferedImage img)
    {
        JFrame frame = new JFrame("Image Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new ImageViewer(img));
        frame.setSize(img.getWidth() + 50, img.getHeight() + 50);
        frame.setVisible(true);
        return frame;
    }
}

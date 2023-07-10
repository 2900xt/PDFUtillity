package org.taha;

import org.taha.Util.ImageTools;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageViewer extends JPanel
{
    private BufferedImage image;
    private static final int IMG_WIDTH = 600, IMG_HEIGHT = 400;
    public ImageViewer(BufferedImage img)
    {
        this.image = ImageTools.resizeImage(img, IMG_WIDTH, IMG_HEIGHT);
    }

    protected void paintComponent(Graphics g)
    {
        g.drawImage(image, (getWidth() - image.getWidth()) / 2, (getHeight() - image.getHeight()) / 2,  image.getWidth(), image.getHeight(), null);
    }

    public void changeImage(BufferedImage image)
    {
        this.image = ImageTools.resizeImage(image, IMG_WIDTH, IMG_HEIGHT);
        repaint();
    }

    public static ArrayList<Integer> getBarcodeSelection(ArrayList<BufferedImage> barcodes) {
        // Assuming all barcodes are same size :)
        AtomicBoolean isDone = new AtomicBoolean(false);
        AtomicInteger currentIndex = new AtomicInteger(0);
        ArrayList<Integer> barcodeSelections = new ArrayList<>();
        for(int i = 0; i < barcodes.size() + 2; i++)
        {
            barcodeSelections.add(0);
        }
        JFrame frame = new JFrame("Image Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(IMG_WIDTH * 2, IMG_HEIGHT * 2);

        JTextField startingRowField = new JTextField("0");
        JLabel startingRowLabel = new JLabel("Starting Row (zero indexed)");
        JTextField startingColField = new JTextField("0");
        JLabel startingColLabel = new JLabel("Starting Column (zero indexed)");
        JTextField instancesField = new JTextField("0");
        JLabel instancesLabel = new JLabel("Instances for barcode");
        JLabel barcodesLabel = new JLabel("Available Barcodes");

        JButton rightBarcodeButton = new JButton(">");
        JButton leftBarcodeButton = new JButton("<");
        JButton doneButton = new JButton("Done");

        ImageViewer panel = new ImageViewer(barcodes.get(0));

        panel.setLayout(null);
        panel.setSize(frame.getSize());
        startingRowLabel.setBounds(panel.getWidth() / 2 - 200, 130, 300, 20);
        panel.add(startingRowLabel);
        startingRowField.setBounds(panel.getWidth() / 2 - 200 + 35, 155, 75, 20);
        panel.add(startingRowField);
        startingColLabel.setBounds(panel.getWidth() / 2 , 130, 300, 20);
        panel.add(startingColLabel);
        startingColField.setBounds( panel.getWidth() / 2 + 35 , 155, 75, 20);
        panel.add(startingColField);

        instancesField.setBounds(panel.getWidth() / 2 - 75, panel.getHeight() - 150, 150, 20);
        panel.add(instancesField);
        instancesLabel.setBounds(panel.getWidth() / 2 - 100, panel.getHeight() - 175, 200, 20);
        instancesLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(instancesLabel);
        barcodesLabel.setHorizontalAlignment(JLabel.CENTER);
        barcodesLabel.setFont(new Font("Arial", Font.BOLD, 50));
        barcodesLabel.setBounds((frame.getWidth() - 500) / 2 , 50, 500, 50);
        panel.add(barcodesLabel);

        leftBarcodeButton.setBounds((panel.getWidth() - IMG_WIDTH) / 2 - 100, (frame.getHeight() - IMG_HEIGHT) / 2 - 10, 50, IMG_HEIGHT);
        panel.add(leftBarcodeButton);
        rightBarcodeButton.setBounds((panel.getWidth() + IMG_WIDTH) / 2 + 50, (frame.getHeight() - IMG_HEIGHT) / 2 - 10, 50, IMG_HEIGHT);
        panel.add(rightBarcodeButton);
        doneButton.setBounds(panel.getWidth() / 2 - 100, panel.getHeight() - 100, 200, 50);
        panel.add(doneButton);
        frame.setContentPane(panel);
        frame.setVisible(true);

        doneButton.addActionListener((event) -> {
            barcodeSelections.set(0, Integer.parseInt(startingRowField.getText()));
            barcodeSelections.set(1, Integer.parseInt(startingColField.getText()));
            barcodeSelections.set(currentIndex.get() + 2, Integer.parseInt(instancesField.getText()));
            isDone.set(true);
        });

        rightBarcodeButton.addActionListener((event) -> {
            if(currentIndex.get() + 1 < barcodes.size())
            {
                barcodeSelections.set(currentIndex.get() + 2, Integer.parseInt(instancesField.getText()));
                currentIndex.getAndIncrement();
                panel.changeImage(barcodes.get(currentIndex.get()));
                instancesField.setText(String.valueOf(barcodeSelections.get(currentIndex.get() + 2)));
            }

            rightBarcodeButton.setEnabled(currentIndex.get() != barcodes.size() - 1);

            leftBarcodeButton.setEnabled(currentIndex.get() != 0);
        });

        leftBarcodeButton.addActionListener((event) -> {
            if(currentIndex.get() > 0)
            {
                barcodeSelections.set(currentIndex.get() + 2, Integer.parseInt(instancesField.getText()));
                currentIndex.getAndDecrement();
                panel.changeImage(barcodes.get(currentIndex.get()));
                instancesField.setText(String.valueOf(barcodeSelections.get(currentIndex.get() + 2)));
            }

            leftBarcodeButton.setEnabled(currentIndex.get() != 0);

            rightBarcodeButton.setEnabled(currentIndex.get() != barcodes.size() - 1);
        });

        rightBarcodeButton.doClick();
        leftBarcodeButton.doClick();


        while(!isDone.get());
        frame.dispose();
        frame.setVisible(false);
        return barcodeSelections;

    }
}
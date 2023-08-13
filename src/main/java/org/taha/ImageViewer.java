package org.taha;

import org.taha.Util.BarcodeData;
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
    private static final int IMG_WIDTH = 400, IMG_HEIGHT = 200;
    private ImageViewer(BufferedImage img)
    {
        this.image = ImageTools.resizeImage(img, IMG_WIDTH, IMG_HEIGHT);
    }
    protected void paintComponent(Graphics g)
    {
        g.drawImage(image, (getWidth() - image.getWidth()) / 2, (getHeight() - image.getHeight() / 2) / 2,  image.getWidth(), image.getHeight() / 2, null);
    }

    public void changeImage(BufferedImage image)
    {
        this.image = ImageTools.resizeImage(image, IMG_WIDTH, IMG_HEIGHT);
        repaint();
    }

    public static BarcodeData getBarcodeSelection() {
        AtomicBoolean isDone = new AtomicBoolean(false);
        AtomicBoolean barcodeAddRequest = new AtomicBoolean(false);
        AtomicInteger currentIndex = new AtomicInteger(0);
        ArrayList<Integer> barcodeSelections = new ArrayList<>();
        ArrayList<BufferedImage> barcodes = new ArrayList<>();

        barcodes.add(BarcodeSelector.getBarcodeFromPDF());

        for(int i = 0; i < barcodes.size() + 2; i++)
        {
            barcodeSelections.add(0);
        }
        JFrame frame = new JFrame("Amazon Barcode List Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int)(IMG_WIDTH * 1.5), IMG_HEIGHT * 2);
        frame.setResizable(false);

        JTextField startingRowField = new JTextField("");
        JLabel startingRowLabel = new JLabel("Starting Row");
        JTextField startingColField = new JTextField("");
        JLabel startingColLabel = new JLabel("Starting Column");
        JTextField instancesField = new JTextField("");
        JLabel instancesLabel = new JLabel("Barcode Instances");
        JLabel barcodesLabel = new JLabel("Amazon Barcodes Editor");

        JButton rightBarcodeButton = new JButton(">");
        JButton leftBarcodeButton = new JButton("<");
        JButton doneButton = new JButton("Done");
        JButton addBarcodeButton = new JButton("Add Barcode");

        ImageViewer panel = new ImageViewer(barcodes.get(0));

        panel.setLayout(null);
        panel.setSize(frame.getSize());
        startingRowLabel.setBounds(panel.getWidth() / 2 - 175, 80, 300, 20);
        panel.add(startingRowLabel);
        startingRowField.setBounds(panel.getWidth() / 2 - 175, 100, 75, 20);
        panel.add(startingRowField);
        startingColLabel.setBounds(panel.getWidth() / 2  + 25, 80, 300, 20);
        panel.add(startingColLabel);
        startingColField.setBounds( panel.getWidth() / 2 + 35 , 100, 75, 20);
        panel.add(startingColField);

        instancesField.setBounds(panel.getWidth() / 2 - 100, panel.getHeight() - 135, 150, 20);
        panel.add(instancesField);
        instancesLabel.setBounds(panel.getWidth() / 2 - 125, panel.getHeight() - 155, 200, 20);
        instancesLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(instancesLabel);
        barcodesLabel.setHorizontalAlignment(JLabel.CENTER);
        barcodesLabel.setFont(new Font("Arial", Font.BOLD, 25));
        barcodesLabel.setBounds((frame.getWidth() - 550) / 2 , 20, 500, 50);
        panel.add(barcodesLabel);

        leftBarcodeButton.setBounds((panel.getWidth() - IMG_WIDTH) / 2 - 100, (frame.getHeight() - IMG_HEIGHT) / 2 - 20, 50, IMG_HEIGHT);
        panel.add(leftBarcodeButton);
        rightBarcodeButton.setBounds((panel.getWidth() + IMG_WIDTH) / 2 + 30, (frame.getHeight() - IMG_HEIGHT) / 2 - 20, 50, IMG_HEIGHT);
        panel.add(rightBarcodeButton);
        doneButton.setBounds(panel.getWidth() / 2 - 205, panel.getHeight() - 100, 150, 30);
        panel.add(doneButton);
        addBarcodeButton.setBounds(panel.getWidth() / 2 - 5, panel.getHeight() - 100, 150, 30);
        panel.add(addBarcodeButton);
        frame.setContentPane(panel);
        frame.setVisible(true);

        doneButton.addActionListener((event) -> {
            if(startingRowField.getText().isEmpty())
            {
                barcodeSelections.set(0, 1);
            } else {
                barcodeSelections.set(0, Integer.parseInt(startingRowField.getText()));
            }

            if(startingColField.getText().isEmpty())
            {
                barcodeSelections.set(1, 1);
            } else {
                barcodeSelections.set(1, Integer.parseInt(startingColField.getText()));
            }

            if(instancesField.getText().isEmpty())
            {
                barcodeSelections.set(currentIndex.get() + 2, 1);
            } else {
                barcodeSelections.set(currentIndex.get() + 2, Integer.parseInt(instancesField.getText()));
            }
            isDone.set(true);
        });

        rightBarcodeButton.addActionListener((event) -> {
            if(currentIndex.get() + 1 < barcodes.size())
            {
                if(instancesField.getText().isEmpty())
                {
                    barcodeSelections.set(currentIndex.get() + 2, 1);
                } else {
                    barcodeSelections.set(currentIndex.get() + 2, Integer.parseInt(instancesField.getText()));
                }
                currentIndex.getAndIncrement();
                panel.changeImage(barcodes.get(currentIndex.get()));

                if(barcodeSelections.get(currentIndex.get()+2) == 1)
                {
                    instancesField.setText("");
                } else {
                    instancesField.setText(String.valueOf(barcodeSelections.get(currentIndex.get() + 2)));
                }
                frame.repaint();
            }

            rightBarcodeButton.setEnabled(currentIndex.get() != barcodes.size() - 1);

            leftBarcodeButton.setEnabled(currentIndex.get() != 0);
        });

        leftBarcodeButton.addActionListener((event) -> {
            if(currentIndex.get() > 0)
            {
                if(instancesField.getText().isEmpty())
                {
                    barcodeSelections.set(currentIndex.get() + 2, 1);
                } else {
                    barcodeSelections.set(currentIndex.get() + 2, Integer.parseInt(instancesField.getText()));
                }
                currentIndex.getAndDecrement();
                panel.changeImage(barcodes.get(currentIndex.get()));

                if(barcodeSelections.get(currentIndex.get()+2) == 1)
                {
                    instancesField.setText("");
                } else {
                    instancesField.setText(String.valueOf(barcodeSelections.get(currentIndex.get() + 2)));
                }
                frame.repaint();
            }

            leftBarcodeButton.setEnabled(currentIndex.get() != 0);

            rightBarcodeButton.setEnabled(currentIndex.get() != barcodes.size() - 1);
        });

        addBarcodeButton.addActionListener((event) ->  {
            barcodeAddRequest.set(true);
        });

        rightBarcodeButton.doClick();
        leftBarcodeButton.doClick();

        frame.repaint();

        while(!isDone.get())
        {
            if(barcodeAddRequest.get())
            {
                barcodeAddRequest.set(false);
                barcodes.add(BarcodeSelector.getBarcodeFromPDF());
                barcodeSelections.add(0);
                leftBarcodeButton.setEnabled(currentIndex.get() != 0);
                rightBarcodeButton.setEnabled(currentIndex.get() != barcodes.size() - 1);
            }
        }
        frame.setVisible(false);

        ArrayList<BufferedImage> output = new ArrayList<>();
        for(int i = 2; i < barcodeSelections.size(); i++)
        {
            for(int j = 0; j < barcodeSelections.get(i); j++)
            {
                output.add(barcodes.get(i - 2));
            }
        }

        return new BarcodeData(output, barcodeSelections.get(0) - 1, barcodeSelections.get(1) - 1);
    }
}
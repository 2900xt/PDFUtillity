package org.taha;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.taha.Util.ImageTools;
import org.taha.Util.PDFTools;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BarcodeSelector extends JPanel
{
    private BufferedImage pdfImage;
    private static final int WIDTH = 600, HEIGHT = 550;
    private static final int IMG_X = (int) (WIDTH / 3), IMG_Y = HEIGHT / 20;
    private static final int IMG_W = (int) (WIDTH - (WIDTH / 2.5)), IMG_H = (int) (HEIGHT -  (HEIGHT / 5));

    private BarcodeSelector()
    {
        this.pdfImage = null;
    }

    protected void paintComponent(Graphics g)
    {
        if(this.pdfImage == null)
        {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.fillRect(IMG_X, IMG_Y, IMG_W, IMG_H);
            g.setColor(Color.BLACK);
            g.drawString("No File Selected!", IMG_X + (IMG_W / 2) - 120, IMG_Y + (IMG_H / 2));
        } else
        {
            g.drawImage(pdfImage, IMG_X, IMG_Y, IMG_W, IMG_H, null);
        }
    }

    public void setPDFImage(PDDocument doc, int pg) throws IOException {
        this.pdfImage = ImageTools.resizeImage(PDFTools.getImageFromPDF(doc, pg), IMG_W, IMG_H);
        repaint(IMG_X, IMG_Y, IMG_W, IMG_H);
    }

    public static BufferedImage getBarcodeFromPDF()
    {
        AtomicBoolean isDone = new AtomicBoolean(false);
        AtomicInteger pageNumber = new AtomicInteger(0);
        AtomicReference<BufferedImage> chosenBarcode = new AtomicReference<>(null);
        AtomicReference<PDDocument> chosenDocument = new AtomicReference<>(null);

        JButton prevPageButton = new JButton("<");
        JButton nextPageButton = new JButton(">");
        JButton doneButton = new JButton("Done");

        JLabel selectedFileLabel = new JLabel("No File Selected");
        selectedFileLabel.setHorizontalAlignment(JLabel.CENTER);
        selectedFileLabel.setBounds(5, 130, 200, 20);

        JTextField rowField = new JTextField("");
        rowField.setHorizontalAlignment(JTextField.CENTER);
        rowField.setBounds(40, 475, 50, 20);

        JLabel rowLabel = new JLabel("ROW");
        rowLabel.setHorizontalAlignment(JLabel.CENTER);
        rowLabel.setBounds(40, 450, 50, 20);

        JTextField colField = new JTextField("");
        colField.setHorizontalAlignment(JTextField.CENTER);
        colField.setBounds(120, 475, 50, 20);

        JLabel colLabel = new JLabel("COL");
        colLabel.setHorizontalAlignment(JLabel.CENTER);
        colLabel.setBounds(120, 450, 50, 20);

        JFrame frame = new JFrame("Amazon Barcode Selector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);

        BarcodeSelector panel = new BarcodeSelector();

        JButton chooseFileButton = new JButton("Choose File");
        chooseFileButton.setBounds(30, 200, 150, 100);
        chooseFileButton.addActionListener((event) -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select PDF File");
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

            // Get the user's home directory
            String userHome = System.getProperty("user.home");

            // Set the current directory of the file chooser to the Downloads directory
            File downloadsDirectory = new File(userHome + File.separator + "Downloads");
            fileChooser.setCurrentDirectory(downloadsDirectory);

            Action details = fileChooser.getActionMap().get("viewTypeDetails");
            details.actionPerformed(null);

            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                throw new RuntimeException("No File Selected!");
            } else {
                File f = fileChooser.getSelectedFile();
                selectedFileLabel.setText(f.getName());

                try {
                    PDDocument doc = PDDocument.load(f);
                    panel.setPDFImage(doc, pageNumber.get());
                    chosenDocument.set(doc);
                    nextPageButton.setEnabled(pageNumber.get() < doc.getNumberOfPages() - 1);
                    prevPageButton.setEnabled(pageNumber.get() > 0);
                    doneButton.setEnabled(true);
                    frame.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                    Main.ErrorExit("Error Reading PDF File: " + f.getAbsolutePath());
                }
            }
        });

        doneButton.setBounds(30, 325, 150, 100);
        doneButton.setEnabled(false);
        doneButton.addActionListener((event) -> {
            if(chosenDocument.get() == null) return;

            int row, col;
            if(rowField.getText().isEmpty())
            {
                row = 1;
            } else
            {
                row = Integer.parseInt(rowField.getText()) - 1;
            }

            if(colField.getText().isEmpty())
            {
                col = 1;
            } else
            {
                col = Integer.parseInt(colField.getText()) - 1;
            }

            try {
                BufferedImage barcode = ImageTools.getBarcodeSubImage(row, col, pageNumber.get(), chosenDocument.get());
                chosenBarcode.set(barcode);
            } catch (Exception e) {
                e.printStackTrace();
                Main.ErrorExit("Unable to read barcode sample from PDF");
            }
            isDone.set(true);
        });

        JLabel pageLabel = new JLabel("Page 1");
        pageLabel.setHorizontalAlignment(JLabel.CENTER);
        pageLabel.setBounds(325, HEIGHT - 75, 100, 20);


        nextPageButton.setBounds(425, HEIGHT - 75, 75, 20);
        nextPageButton.addActionListener((event) -> {
            PDDocument doc = chosenDocument.get();
            pageNumber.getAndIncrement();

            try {
                panel.setPDFImage(doc, pageNumber.get());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            chosenDocument.set(doc);
            pageLabel.setText("Page " + pageNumber.get());
            frame.repaint();
            nextPageButton.setEnabled(pageNumber.get() < doc.getNumberOfPages() - 1);
            prevPageButton.setEnabled(pageNumber.get() > 0);
        });

        prevPageButton.setBounds(250, HEIGHT - 75, 75, 20);
        prevPageButton.addActionListener((event) -> {
            PDDocument doc = chosenDocument.get();
            pageNumber.getAndDecrement();

            try {
                panel.setPDFImage(doc, pageNumber.get());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            chosenDocument.set(doc);
            pageLabel.setText("Page " + pageNumber.get());
            frame.repaint();

            nextPageButton.setEnabled(pageNumber.get() < doc.getNumberOfPages() - 1);
            prevPageButton.setEnabled(pageNumber.get() > 0);
        });

        nextPageButton.setEnabled(false);
        prevPageButton.setEnabled(false);

        panel.setLayout(null);
        panel.setSize(frame.getSize());
        panel.add(colLabel);
        panel.add(rowLabel);
        panel.add(colField);
        panel.add(rowField);
        panel.add(selectedFileLabel);
        panel.add(chooseFileButton);
        panel.add(doneButton);
        panel.add(pageLabel);
        panel.add(nextPageButton);
        panel.add(prevPageButton);
        frame.setContentPane(panel);
        frame.setVisible(true);

        while(!isDone.get());

        try {
            chosenDocument.get().close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        panel.setVisible(false);
        panel.setEnabled(false);
        frame.remove(panel);
        frame.invalidate();
        frame.setVisible(false);
        frame.setEnabled(false);
        frame.dispose();

        return chosenBarcode.get();
    }
}

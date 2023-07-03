package org.example;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main
{
    /*
     * Goal: Condense two or more PDF documents into one
     * 1. Ask the user to choose some documents
     */

    public static ArrayList<BufferedImage> barcodes;
    public static PDDocument output;


    public static PDPage createBlankPage(PDDocument doc)
    {
        PDPage newPage = new PDPage(new PDRectangle(612, 792));
        doc.addPage(newPage);
        return newPage;
    }

    public static BufferedImage getImageFromPDF(PDDocument document) throws IOException
    {
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage pageImage = renderer.renderImageWithDPI(0, 600f);
        PDPage pg = document.getPage(0);

        float height = pageImage.getHeight();
        float width = pageImage.getWidth();
        float unit_x = width / 3, unit_y = height / 10;
        int x = 0, y = (int)(unit_y / 2);
        BufferedImage subImage = pageImage.getSubimage(x, y, (int)unit_x, (int)unit_y);
        ImageViewer.viewImage(subImage);
        return subImage;
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException
    {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    //Ask the user how many of each barcode they want
    public static int getNumber(BufferedImage barcode)
    {
        JFrame f = ImageViewer.viewImage(barcode);
        int output = Integer.parseInt(JOptionPane.showInputDialog("Enter the amount of instances for this barcode:"));
        f.setVisible(false);
        return output;
    }

    public static void main(String[] args) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setDialogTitle("Select PDF Files");
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

            fileChooser.showOpenDialog(null);
            File[] selected = fileChooser.getSelectedFiles();
            barcodes = new ArrayList<>();

            for (File f : selected)
            {
                PDDocument doc = PDDocument.load(f);
                barcodes.add(getImageFromPDF(doc));
            }
            output = new PDDocument();

            ArrayList<BufferedImage> images = new ArrayList<>();
            int totalBarcodes = 0;
            for(BufferedImage img : barcodes)
            {
                int num = getNumber(img);
                for(int i = 0; i < num; i++)
                {
                    images.add(img);
                }
                totalBarcodes += num;
            }

            int startingRow = Integer.parseInt(JOptionPane.showInputDialog("Enter the starting row (zero indexed)")) % 10;
            int startingCol = Integer.parseInt(JOptionPane.showInputDialog("Enter the starting column (zero indexed)")) % 3;

            totalBarcodes += startingRow;
            totalBarcodes += startingCol * 10;

            int pageCount = totalBarcodes / 30;
            if(totalBarcodes % 30 != 0)
            {
                pageCount++;
            }

            int imgInd = 0;
            for(int i = 0; i < pageCount; i++)
            {
                PDPage pg = createBlankPage(output);
                PDPageContentStream cStream = new PDPageContentStream(output, pg);
                float height = pg.getMediaBox().getHeight();
                float width = pg.getMediaBox().getWidth();

                float unit_x = width / 3, unit_y = height / 11;
                for(float y = height - (unit_y / 2 + (startingRow + 1) * unit_y); y >= unit_y / 2; y -= unit_y)
                {
                    for(float x = startingCol * unit_x; x < width; x += unit_x)
                    {
                        try {
                            BufferedImage img = images.get(imgInd++);
                            PDImageXObject pdImage = LosslessFactory.createFromImage(output, img);
                            cStream.drawImage(pdImage, x, y, unit_x, unit_y);
                        } catch (IndexOutOfBoundsException ignored) {}
                    }
                }
                startingRow = 0;
                startingCol = 0;
                cStream.close();
            }
            String filename = JOptionPane.showInputDialog("Enter output file name");
            output.save("./" + filename);
            output.close();
            System.exit(0);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
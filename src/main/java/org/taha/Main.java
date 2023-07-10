package org.taha;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.taha.Util.PDFTools;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Main
{
    public static ArrayList<BufferedImage> barcodes;
    public static PDDocument output;
    public static void main(String[] args)
    {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setDialogTitle("Select PDF Files");
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

            fileChooser.showOpenDialog(null);
            File[] selected = fileChooser.getSelectedFiles();

            if(selected == null || selected.length == 0)
            {
                throw new RuntimeException("No Files Selected");
            }
            barcodes = new ArrayList<>();

            for (File f : selected)
            {
                PDDocument doc = PDDocument.load(f);
                barcodes.add(PDFTools.getImageFromPDF(doc));
            }
            output = new PDDocument();

            ArrayList<BufferedImage> images = new ArrayList<>();
            ArrayList<Integer> data = ImageViewer.getBarcodeSelection(barcodes);
            int totalBarcodes = 0;

            int startingRow = data.get(0), startingCol = data.get(1);

            for(int i = 2; i < data.size(); i++)
            {
                int count = data.get(i), index = i-2;
                totalBarcodes += count;
                for(int j = 0; j < count; j++)
                {
                    images.add(barcodes.get(index));
                }
            }

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
                PDPage pg = PDFTools.createBlankPage(output);
                PDPageContentStream cStream = new PDPageContentStream(output, pg);
                float height = pg.getMediaBox().getHeight();
                float width = pg.getMediaBox().getWidth();

                float unit_x = width / 3, unit_y = height / 11;
                for(float x = startingCol * unit_x; x < width; x += unit_x)
                {
                    for(float y = height - (unit_y / 2 + (startingRow + 1) * unit_y); y >= unit_y / 2; y -= unit_y)
                    {
                        try {
                            BufferedImage img = images.get(imgInd++);
                            PDImageXObject pdImage = LosslessFactory.createFromImage(output, img);
                            cStream.drawImage(pdImage, x, y, unit_x / 1.125f, unit_y * 1.025f);
                        } catch (IndexOutOfBoundsException ignored) {}
                    }
                }
                startingRow = 0;
                startingCol = 0;
                cStream.close();
            }
            File outputFileDir = new File("./OutputFiles/");
            if(!outputFileDir.exists()) outputFileDir.mkdirs();
            String time = LocalDateTime.now().toString().replace(":", "");
            output.save("./OutputFiles/" + time.substring(0, time.indexOf('.')) + ".pdf");
            output.close();
            System.exit(0);
        } catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Critical Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }
}
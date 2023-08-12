package org.taha;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.taha.Util.BarcodeData;
import org.taha.Util.PDFTools;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main
{
    public static PDDocument output;
    public static void main(String[] args)
    {
        try {
            output = new PDDocument();
            BarcodeData data = ImageViewer.getBarcodeSelection();
            ArrayList<BufferedImage> images = data.images;
            int startingRow = data.startingRow, startingCol = data.startingCol;
            int totalBarcodes = images.size() + startingRow + startingCol * 10;
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

                for(int col = startingCol; col < 3; col++)
                {
                    for(int row = startingRow; row < 10; row++)
                    {
                        try {
                            BufferedImage img = images.get(imgInd++);
                            PDImageXObject pdImage = LosslessFactory.createFromImage(output, img);
                            PDRectangle coordinates = PDFTools.getBarcodeCoordinates(row, col, pg);
                            cStream.drawImage(pdImage, coordinates.getWidth(), coordinates.getHeight(), unit_x, unit_y * 1.05f);
                        } catch (IndexOutOfBoundsException ignored) {}
                    }
                    startingRow = 0;
                }

                startingCol = 0;
                cStream.close();
            }
            File outputFileDir = new File("./OutputFiles/");
            if(!outputFileDir.exists() && !outputFileDir.mkdirs())
            {
                throw new RuntimeException("Unable to create output file directory");
            }
            String time = LocalDateTime.now().toString().replace(":", "");
            output.save("./OutputFiles/" + time.substring(0, time.indexOf('.')) + ".pdf");
            output.close();
            System.exit(0);
        } catch (Exception e)
        {
            e.printStackTrace();
            ErrorExit(e.getMessage());
        }
    }

    public static void ErrorExit(String message)
    {
        JOptionPane.showMessageDialog(null, message, "Critical Error", JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }
}
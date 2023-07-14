package org.taha.Util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageTools
{
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight)
    {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public static BufferedImage getBarcodeSubImage(int row, int col, int pageNumber, PDDocument doc) throws IOException
    {
        PDPage pg = doc.getPage(pageNumber);
        PDRectangle coordinates = PDFTools.getBarcodeCoordinates(row, col, pg);
        float height = pg.getMediaBox().getHeight();
        float width = pg.getMediaBox().getWidth();
        float unit_x = width / 3, unit_y = height / 11;

        BufferedImage pageImage = PDFTools.getImageFromPDF(doc, pageNumber);
        float x = (coordinates.getWidth() / width) * pageImage.getWidth();
        float y = (coordinates.getHeight() / height) * pageImage.getHeight();
        float imgw = (unit_x / width) * pageImage.getWidth();
        float imgh = (unit_y * 1.05f / height) * pageImage.getHeight();

        return pageImage.getSubimage((int) x, (int) y, (int) imgw, (int)imgh);
    }
}

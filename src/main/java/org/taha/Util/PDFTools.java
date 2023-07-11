package org.taha.Util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class PDFTools
{
    public static final float QUALITY = 600f;
    public static final float STD_WIDTH = 612f, STD_HEIGHT = 792f;
    public static PDPage createBlankPage(PDDocument doc)
    {
        PDPage newPage = new PDPage(new PDRectangle(STD_WIDTH, STD_HEIGHT));
        doc.addPage(newPage);
        return newPage;
    }

    public static BufferedImage getImageFromPDF(PDDocument document) throws IOException
    {
        PDFRenderer renderer = new PDFRenderer(document);
        BufferedImage pageImage = renderer.renderImageWithDPI(0, QUALITY);

        float height = pageImage.getHeight();
        float width = pageImage.getWidth();
        float unit_x = width / 3, unit_y = height / 10;
        int x = 0, y = (int)(unit_y / 2);
        return pageImage.getSubimage(x, y, (int)unit_x, (int)unit_y);
    }

    public static PDRectangle getBarcodeCoordinates(int row, int col, PDPage pg)
    {
        float height = pg.getMediaBox().getHeight();
        float width = pg.getMediaBox().getWidth();
        float unit_x = width / 3, unit_y = height / 11;
        float y = (height - (unit_y / 2 + 1.125f * unit_y)) - ((unit_y * 0.998f) * row);
        float x = unit_x * col;
        return new PDRectangle(x, y);
    }
}

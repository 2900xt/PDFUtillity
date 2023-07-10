package org.taha.Util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class PDFTools
{
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

        float height = pageImage.getHeight();
        float width = pageImage.getWidth();
        float unit_x = width / 3, unit_y = height / 10;
        int x = 0, y = (int)(unit_y / 2);
        BufferedImage subImage = pageImage.getSubimage(x, y, (int)unit_x, (int)unit_y);
        return subImage;
    }
}

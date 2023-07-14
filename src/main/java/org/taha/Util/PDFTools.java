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

    //Standard A4 PDF width and height:
    public static final float STD_WIDTH = 612f, STD_HEIGHT = 792f;
    public static final float UNIT_X = (STD_WIDTH / 3) * 0.98f, UNIT_Y = STD_HEIGHT / 11;
    public static final float X_START = 0, Y_START = (STD_HEIGHT - (UNIT_Y * 1.52f));
    public static final float[] barcodeXLookup = {
            X_START,
            X_START + UNIT_X,
            X_START + UNIT_X * 2,
    };

    public static final float[] barcodeYLookup = {
            Y_START,
            Y_START - UNIT_Y,
            Y_START - UNIT_Y * 2,
            Y_START - UNIT_Y * 3,
            Y_START - UNIT_Y * 4,
            Y_START - UNIT_Y * 5,
            Y_START - UNIT_Y * 6,
            Y_START - UNIT_Y * 7,
            Y_START - UNIT_Y * 8,
            Y_START - UNIT_Y * 9,
    };
    public static PDPage createBlankPage(PDDocument doc)
    {
        PDPage newPage = new PDPage(new PDRectangle(STD_WIDTH, STD_HEIGHT));
        doc.addPage(newPage);
        return newPage;
    }

    public static BufferedImage getImageFromPDF(PDDocument document, int page) throws IOException
    {
        PDFRenderer renderer = new PDFRenderer(document);
        return renderer.renderImageWithDPI(page, QUALITY);
    }

    //Returns the position of a barcode from a PDF in a 10x3 standard orientation
    public static PDRectangle getBarcodeCoordinates(int row, int col, PDPage pg)
    {
        return new PDRectangle(barcodeXLookup[col], barcodeYLookup[row]);
    }
}

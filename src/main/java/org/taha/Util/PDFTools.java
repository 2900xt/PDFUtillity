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

    //Amazon Barcode Specifications
    public static final float AMZ_UNIT_X = (STD_WIDTH / 3) * 0.98f, AMZ_UNIT_Y = STD_HEIGHT / 11;
    public static final float AMZ_X_START = 0, AMZ_Y_START = (STD_HEIGHT - (AMZ_UNIT_Y * 1.52f));
    public static BarcodeLookupData amazonData  = new BarcodeLookupData(
            new float[] {
                    AMZ_Y_START,
                    AMZ_Y_START - AMZ_UNIT_Y,
                    AMZ_Y_START - AMZ_UNIT_Y * 2,
                    AMZ_Y_START - AMZ_UNIT_Y * 3,
                    AMZ_Y_START - AMZ_UNIT_Y * 4,
                    AMZ_Y_START - AMZ_UNIT_Y * 5,
                    AMZ_Y_START - AMZ_UNIT_Y * 6,
                    AMZ_Y_START - AMZ_UNIT_Y * 7,
                    AMZ_Y_START - AMZ_UNIT_Y * 8,
                    AMZ_Y_START - AMZ_UNIT_Y * 9,
            },
            new float[] {
                    AMZ_X_START,
                    AMZ_X_START + AMZ_UNIT_X,
                    AMZ_X_START + AMZ_UNIT_X * 2,
            },
            1,
            1.05f
    );

    //Walmart Barcode Specifications
    public static final float WM_UNIT_X = (STD_WIDTH / 3) * 0.98f, WM_UNIT_Y = STD_HEIGHT / 11f;
    public static final float WM_X_START = 5, WM_Y_START = (STD_HEIGHT - (WM_UNIT_Y * 1.68f));
    public static BarcodeLookupData walmartData  = new BarcodeLookupData(
            new float[] {
                    WM_Y_START,
                    WM_Y_START - WM_UNIT_Y,
                    WM_Y_START - WM_UNIT_Y * 2,
                    WM_Y_START - WM_UNIT_Y * 3,
                    WM_Y_START - WM_UNIT_Y * 4,
                    WM_Y_START - WM_UNIT_Y * 5,
                    WM_Y_START - WM_UNIT_Y * 6,
                    WM_Y_START - WM_UNIT_Y * 7,
                    WM_Y_START - WM_UNIT_Y * 8,
                    WM_Y_START - WM_UNIT_Y * 9,
            },
            new float[] {
                    WM_X_START,
                    WM_X_START + WM_UNIT_X,
                    WM_X_START + WM_UNIT_X * 2,
            },
            0.9f,
            0.95f
    );


    public static BarcodeLookupData currentLookupData = walmartData;
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

    public static PDRectangle getBarcodeCoordinates(int row, int col, PDPage pg)
    {
        return new PDRectangle(amazonData.barcodeXLookup[col], amazonData.barcodeYLookup[row]);
    }

    public static PDRectangle getImageBarcodeCoordinates(int row, int col, PDPage pg)
    {
        return new PDRectangle(currentLookupData.barcodeXLookup[col], currentLookupData.barcodeYLookup[9 - row]);
    }

    public static class BarcodeLookupData
    {
        public float[] barcodeYLookup, barcodeXLookup;
        public float WMultiplier, HMultiplier;
        public BarcodeLookupData(float[] barcodeYLookup, float[] barcodeXLookup, float WMultiplier, float HMultiplier)
        {
            this.barcodeXLookup = barcodeXLookup;
            this.barcodeYLookup = barcodeYLookup;
            this.WMultiplier = WMultiplier;
            this.HMultiplier = HMultiplier;
        }
    }
}

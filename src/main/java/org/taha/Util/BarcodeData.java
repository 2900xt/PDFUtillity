package org.taha.Util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BarcodeData
{
    public ArrayList<BufferedImage> images;
    public int startingCol;
    public int startingRow;
    public BarcodeData(ArrayList<BufferedImage> images, int startingRow, int startingCol)
    {
        this.images = images;
        this.startingCol = startingCol;
        this.startingRow = startingRow;
    }
}

package com.team2.higallery.utils;

import android.graphics.Bitmap;

public class BitmapUtils {
    public static Bitmap resize(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if ((width != maxWidth) || (height != maxHeight)) {
            if (width > maxWidth) {
                height = (int)((double)height / ((double)width / (double)maxWidth));
                width = maxWidth;
            }
            if (height > maxHeight) {
                width = (int)((double)width / ((double)height / (double)maxHeight));
                height = maxHeight;
            }
            return Bitmap.createScaledBitmap(bitmap, width, height, false);
        }
        return bitmap;
    }

    public static long getHashCode(Bitmap bitmap) {
        long result = 343;
        for(int x = 0; x < bitmap.getWidth(); x++){
            for (int y = 0; y < bitmap.getHeight(); y++){
                result = result * (bitmap.getPixel(x,y) + 31);
            }
        }
        return result;
    }

    public static boolean compare(Bitmap bitmap1, Bitmap bitmap2) {
        int w1 = bitmap1.getWidth();
        int h1 = bitmap1.getHeight();

        int w2 = bitmap2.getWidth();
        int h2 = bitmap2.getHeight();

        if (w1 != w2 || h1 != h2) {
            return false;
        }

        for (int x = 0; x < w1; x++) {
            for (int y = 0; y < h1; y++) {
                if (bitmap1.getPixel(x, y) != bitmap2.getPixel(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

}

package com.team2.higallery.utils;

import android.graphics.Bitmap;

import java.util.Arrays;

public class BitmapUtils {
    public static int getHashCode(Bitmap bitmap) {
//        long hash = 31;
//        for(int x = 0; x < bitmap.getWidth(); x++){
//            for (int y = 0; y < bitmap.getHeight(); y++){
//                hash *= (bitmap.getPixel(x,y) + 31);
//            }
//        }
//        return hash;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] buffer = new int[width * height];
        bitmap.getPixels(buffer, 0, width, 0, 0, width, height);
        return Arrays.hashCode(buffer);
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

package com.team2.higallery.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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

    public static int getHashCode(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int result = w;
        result = (result << 7) ^ h;
        for (int i = 0; i < 20; i++) {
            int x = (i * 29) % w;
            int y = (i * 71) % h;
            int pixel = bitmap.getPixel(x, y);
            result = (result << 7) ^ pixel;
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

    public static boolean save(Bitmap bitmap, String filePath, Context context) {
        try {
            FileOutputStream out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String save(Bitmap bitmap, Context context) {
        String fileName = DataUtils.generateRandomString(20) + ".png";
        String filePath = FileUtils.HIGALERRY_FOLDER_PATH + "/" + fileName;
        if (save(bitmap, filePath, context)) {
            return fileName;
        };
        return null;
    }
}

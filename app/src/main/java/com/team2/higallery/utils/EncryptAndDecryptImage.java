package com.team2.higallery.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptAndDecryptImage {
    public static byte[] encryptImage(Bitmap bitmap) throws IOException {
        //Convert Bitmap to Byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();

        //Mã hóa một cách tùy biến ở đây
        int lengthBytes = bytes.length;
        int step = bytes.length/1024;
        for (int i = 0; i < lengthBytes; i += step) {
            bytes[i] += (byte)1;
        }

        return bytes;
    }

    public static Bitmap decryptImage(byte[] bytes)   throws FileNotFoundException, IOException {
        //Giải mã theo cách đã mã hóa ở hàm mã hóa
        int length = bytes.length;
        int step = bytes.length/1024;
        for (int i = 0; i < length; i += step) {
            bytes[i] -= (byte)1;
        }

        //Convert Byte array to Bitmap
        Bitmap result = BitmapFactory.decodeByteArray(bytes,  0, bytes.length);
        return result;
    }

    public static Bitmap getBitmapFromImagePath(String imagePath){
        File imageFile = new File(imagePath);

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        return bitmap;
    }

    public static void saveFile(Context context, byte[] bytes, String fileName) {
        try {
            File dirFile = new File(context.getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(dirFile);

            fos.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
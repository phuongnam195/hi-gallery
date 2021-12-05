package com.team2.higallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class EncryptUtils {
    public static byte[] encryptImage(Bitmap bitmap) {
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

    public static Bitmap decryptImage(byte[] bytes) {
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
}
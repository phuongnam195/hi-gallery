package com.team2.higallery.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

public class FileUtils {
    public static String HIGALERRY_FOLDER_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/HiGallery";

    public static String getParentFolder(String path) {
        int lastSlash = path.length() - 2;
        while (path.charAt(lastSlash) != '/' && path.charAt(lastSlash) != '\\') {
            lastSlash--;
            if (lastSlash == 0) {
                return null;
            }
        }
        return path.substring(0, lastSlash);
    }

    public static String getName(String path) {
        int lastSlash = path.length() - 2;
        while (path.charAt(lastSlash) != '/' && path.charAt(lastSlash) != '\\') {
            lastSlash--;
            if (lastSlash == 0) {
                return null;
            }
        }
        return path.substring(lastSlash + 1);
    }

    public static String getExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        return path.substring(lastDot + 1);
    }

    public static Bitmap.CompressFormat getCompressFormat(String path) {
        String extension = getExtension(path).toUpperCase();
        if (extension.equals("JPG") || extension.equals("JPEG")) {
            return Bitmap.CompressFormat.JPEG;
        } else if (extension.equals("WEBP")) {
            return Bitmap.CompressFormat.WEBP;
        }
        return Bitmap.CompressFormat.PNG;
    }

    public static File moveImageFile(String imagePath, File newFolder, Context context) {
        File oldFile = new File(imagePath);

        if (!newFolder.exists()) {
            if (!newFolder.mkdirs()) {
                return null;
            }
        }

        String fullname = oldFile.getName();
        String name = fullname.substring(0, fullname.lastIndexOf('.'));
        String extension = fullname.substring(name.length());
        File newFile = new File(newFolder + "/" + fullname);

        int countExist = 0;
        while (newFile.exists()) {
            countExist++;
            String newFullname = name + " (" + countExist + ")" + extension;
            newFile = new File(newFolder + "/" + newFullname);
        }

        if (!oldFile.renameTo(newFile)) {
            return null;
        }

        return newFile;
    }

    // Credit: https://stackoverflow.com/questions/39530663/delete-image-file-from-device-programmatically
    public static boolean removeImageMedia(Context context, File imageFile) {
        String[] projection = {MediaStore.Images.Media._ID};

        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{imageFile.getAbsolutePath()};

        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (cursor == null) {
            return false;
        }
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        } else {
            return false;
        }
        cursor.close();
        return true;
    }
}

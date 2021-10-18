package com.team2.higallery.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtils {
    //Credit: https://stackoverflow.com/questions/8204680/java-regex-email
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public static String hash(String pin) {
        // Băm mã PIN
        return pin;
    }

    public static ArrayList<String> getAllImagePathsFromExternalStorage(Context context) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        ArrayList<String> allImagePaths = new ArrayList<>();

        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(column_index_data);
            allImagePaths.add(absolutePathOfImage);
        }

        return allImagePaths;
    }
}
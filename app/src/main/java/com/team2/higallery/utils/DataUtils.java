package com.team2.higallery.utils;

import android.content.Context;
import android.database.Cursor;
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

    public static ArrayList<String> allImages = new ArrayList<>();

    // Load toàn bộ đường dẫn các ảnh trong bộ nhớ ngoài
    // Trả về true, nếu có sự thay đổi (thêm, bớt ảnh, đổi tên hoặc thứ tự thay đổi,...)
    public static boolean updateAllImagesFromExternalStorage(Context context) {
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,null, orderBy + " DESC");
        if (cursor == null) {
            return false;
        }
        ArrayList<String> oldAllImages = new ArrayList<String>(allImages);
        if (!allImages.isEmpty()) {
            allImages.clear();
        }
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            allImages.add(absolutePathOfImage);
        }
        cursor.close();

        // Kiểm tra có sự thay đổi hay không?
        if (allImages.size() != oldAllImages.size()) {
            return true;
        }
        for (int i = 0; i < allImages.size(); i++) {
            if (!allImages.get(i).equals(oldAllImages.get(i))) {
                return true;
            }
        }
        return false;
//        Uri uri;
//        Cursor cursor;
//        int column_index_data;
//        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
//        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
//        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
//
//        if (cursor != null) {
//            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//            ArrayList<String> allImagePaths = new ArrayList<>();
//
//            while (cursor.moveToNext()) {
//                String absolutePathOfImage = cursor.getString(column_index_data);
//                allImagePaths.add(absolutePathOfImage);
//            }
//
//            cursor.close();
//            return allImagePaths;
//        }
    }
}
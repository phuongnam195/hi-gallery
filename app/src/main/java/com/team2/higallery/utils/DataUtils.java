package com.team2.higallery.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.github.chrisbanes.photoview.PhotoView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
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

    public static String getNamePhoto(String pathPhoto) {
        int pos = pathPhoto.lastIndexOf('/');
        String namePhoto = " " + pathPhoto.substring(pos+1);
        return namePhoto;
    }

    public static String getSizePhoto(double size) {
        String result = "";
        int count = 0;
        while (size >= 1024) {
            size = size / 1024;
            count++;
        }
        size =Math.round(size * 10)*1.0 / 10;
        switch (count) {
            case 0:
                result = " " + size + " B";
                break;
            case 1:
                result = " " + size + " KB";
                break;
            case 2:
                result = " " + size + " MB";
                break;
            case 3:
                result = " " + size + " GB";
                break;
        }
        return result;
    }

    public static String convertDateTimeToString(String dateTime, Context context) {
        SimpleDateFormat dateFormat;
        String result = "";

        // chỉnh thời gian theo ngôn ngữ
        if (LocaleHelper.getLocale(context).getLanguage() == "vi") {
            dateFormat = new SimpleDateFormat("MMM dd, yyyy '-' h:mm a", new Locale("vi", "VN"));
        }
        else {
            dateFormat = new SimpleDateFormat("MMM dd, yyyy '-' h:mm a", Locale.ENGLISH);
        }
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dt.setTimeZone(TimeZone.getTimeZone("GMT")); // chuyển về giờ GMT

        try {
            Date date = dt.parse(dateTime);
            result = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return " " + result;
    }

    public static String getResolutionPhoto(String path, Context context) {
        // tạo ảnh bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path,bmOptions);
        Uri myUri = Uri.parse(path);
        PhotoView photoView = new PhotoView(context);
        photoView.setImageURI(myUri);
        photoView.setImageBitmap(bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double MP = width * height * 1.0 / 1000000;
        MP = Math.round(MP * 10)*1.0 / 10;
        String result = " " + width + "x" + height + " (" + MP + " MP)";
        return result;
    }

}
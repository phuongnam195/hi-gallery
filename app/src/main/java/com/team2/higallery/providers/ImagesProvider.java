package com.team2.higallery.providers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.github.chrisbanes.photoview.PhotoView;
import com.team2.higallery.models.Album;
import com.team2.higallery.utils.BitmapUtils;
import com.team2.higallery.utils.FileUtils;
import com.team2.higallery.utils.LocaleHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class ImagesProvider {
    public static ArrayList<String> allImages = new ArrayList<>();
    public static ArrayList<Album> allAlbums = new ArrayList<>();

    // Load toàn bộ đường dẫn các ảnh trong bộ nhớ ngoài
    // Trả về true, nếu có sự thay đổi (thêm, bớt ảnh, đổi tên hoặc thứ tự thay đổi,...)
    public static boolean updateAllImagesFromExternalStorage(Context context) {
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, orderBy + " DESC");
        if (cursor == null) {
            return false;
        }
        ArrayList<String> oldAllImages = new ArrayList<>(allImages);
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
            divideAllImagesToAlbums();
            return true;
        }
        for (int i = 0; i < allImages.size(); i++) {
            if (!allImages.get(i).equals(oldAllImages.get(i))) {
                divideAllImagesToAlbums();
                return true;
            }
        }
        return false;
    }

    // Chia tất cả hình ảnh ra các album
    public static void divideAllImagesToAlbums() {
        if (allImages.isEmpty()) {
            return;
        }

        if (!allAlbums.isEmpty()) {
            allAlbums.clear();
        }

        for (String imagePath : allImages) {
            String imageParentFolder = FileUtils.getParentFolder(imagePath);

            int i = 0;
            while (i < allAlbums.size()) {
                if (allAlbums.get(i).getPath().equals(imageParentFolder)) {
                    allAlbums.get(i).addImage(imagePath);
                    break;
                }
                i++;
            }
            if (i == allAlbums.size()) {
                Album album = new Album(imageParentFolder);
                album.addImage(imagePath);
                allAlbums.add(album);
            }
        }
    }

    // Loại bỏ ảnh trùng lặp, trả về số lượng ảnh bị loại bỏ
    public static int deleteDuplicateImages(Context context) {
        int count = 0;
        HashMap<Long, ArrayList<Bitmap>> hashMap = new HashMap<>();
        TrashManager trashManager = TrashManager.getInstance(context);

        // Duyệt tất cả các path ảnh
        for (String imagePath : allImages) {
            if (FileUtils.getExtension(imagePath).equalsIgnoreCase("gif")) {
                continue;
            }

            Bitmap currBitmap = BitmapFactory.decodeFile(imagePath);

            // Lấy mã hash của bitmap
            long hashCode = BitmapUtils.getHashCode(currBitmap);

            // Nếu trong map đã có hashCode này
            if (hashMap.containsKey(hashCode)) {
                ArrayList<Bitmap> insertedBitmaps = hashMap.get(hashCode);
                boolean notFound = true;

                // Thì duyệt các bitmap có cùng hashCode
                for (Bitmap ibm : insertedBitmaps) {
                    // Nếu đã tồn tại bitmap giống 100%
                    if (BitmapUtils.compare(ibm, currBitmap)) {
                        count++;
                        notFound = false;
                        // Xóa ảnh cũ hơn (cũng chính là ảnh hiện tại của vòng lặp) và không cần kiểm tra thêm
                        trashManager.delete(imagePath);
                        break;
                    }
                }
                // Nếu không có bitmap (cùng hashCode) nào giống currBitmap, thì thêm vào danh sách
                if (notFound) {
                    insertedBitmaps.add(currBitmap);
                    hashMap.put(hashCode, insertedBitmaps);
                }
            }
            // Nếu trong map chưa có hashCode này
            else {
                ArrayList<Bitmap> newValue = new ArrayList<>();
                newValue.add(currBitmap);
                hashMap.put(hashCode, newValue);
            }
        }

        return count;
    }



    public static String getNamePhoto(String pathPhoto) {
        int pos = pathPhoto.lastIndexOf('/');
        String namePhoto = " " + pathPhoto.substring(pos + 1);
        return namePhoto;
    }

    public static String getSizePhoto(double size) {
        String result = "";
        int count = 0;
        while (size >= 1024) {
            size = size / 1024;
            count++;
        }
        size = Math.round(size * 10) * 1.0 / 10;
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
        } else {
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
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        Uri myUri = Uri.parse(path);
        PhotoView photoView = new PhotoView(context);
        photoView.setImageURI(myUri);
        photoView.setImageBitmap(bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double MP = width * height * 1.0 / 1000000;
        MP = Math.round(MP * 10) * 1.0 / 10;
        String result = " " + width + "x" + height + " (" + MP + " MP)";
        return result;
    }
}

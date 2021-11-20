package com.team2.higallery.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.team2.higallery.utils.DatabaseHelper;
import com.team2.higallery.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.sql.Date;

public class TrashManager {
    // Singleton pattern
    private static TrashManager instance;
    public static TrashManager getInstance(Context context) {
        if (instance == null) {
            instance = new TrashManager(context);
        }
        return instance;
    }
    private TrashManager(Context context) {
        trashFolder = new File(Environment.getExternalStorageDirectory(), "HiGallery/.nomedia/");
        if (!trashFolder.exists()) {
            boolean success = trashFolder.mkdirs();
            if (!success) {
                trashFolder = null;
                // TODO: Xử lý hoặc thông báo lỗi
                return;
            }
        }
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        deletedImages = dbHelper.getAllDeletedImages();
    }

    File trashFolder;
    ArrayList<DeletedImage> deletedImages;
    DatabaseHelper dbHelper;
    Context context;

    public int count() {
        return deletedImages.size();
    }

    public ArrayList<String> getAllPaths() {
        ArrayList<String> paths = new ArrayList<>();
        for (DeletedImage image : deletedImages) {
            paths.add(image.getTrashPath());
        }
        return paths;
    }

    public boolean delete(String imagePath) {
        if (trashFolder == null) {
            return false;
        }
        File imageFile = new File(imagePath);

        String fullname = imageFile.getName();
        String name = fullname.substring(0, fullname.lastIndexOf('.'));
        String extension = fullname.substring(name.length());
        File trashFile = new File(trashFolder + "/" + fullname);
        int countExist = 0;

        while (trashFile.exists()) {
            countExist++;
            String newFullname = name + " (" + String.valueOf(countExist) + ")" + extension;
            trashFile = new File(trashFolder + "/" + newFullname);
        }

        if (!imageFile.renameTo(trashFile)) {
            return false;
        }

        if (!FileUtils.removeImageFile(context, imageFile)) {
            return false;
        }

        Date datetime = new Date(System.currentTimeMillis());
        DeletedImage deletedImage = new DeletedImage(trashFile.getPath(), imagePath, datetime);
        long id = dbHelper.insertDeletedImage(deletedImage);
        deletedImage.setId(id);
        deletedImages.add(deletedImage);
        return true;
    }

    public boolean deletePermanently(int index) {
        if (trashFolder == null) {
            return false;
        }
        DeletedImage deletedImage = deletedImages.get(index);

        String trashPath = deletedImage.getTrashPath();
        File file = new File(trashPath);
        boolean success = file.delete();

        if (!success) {
            return false;
        }

        deletedImages.remove(index);
        long id = deletedImage.getId();
        dbHelper.removeDeletedImage(id);
        return true;
    }

    public boolean restore(int index) {
        if (trashFolder == null) {
            return false;
        }
        DeletedImage deletedImage = deletedImages.get(index);
        String trashPath = deletedImage.getTrashPath();
        String oldPath = deletedImage.getOldPath();
        File trashFile = new File(trashPath);
        File oldFile = new File(oldPath);
        boolean success = trashFile.renameTo(oldFile);

        if (!success) {
            return false;
        }

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(oldFile)));

        deletedImages.remove(index);
        long id = deletedImage.getId();
        dbHelper.removeDeletedImage(id);
        return true;
    }

    public boolean deletePermanentlyAll() {
        if (trashFolder == null || deletedImages.isEmpty()) {
            return true;
        }

        for (DeletedImage deletedImage : deletedImages) {
            String oldPath = deletedImage.getOldPath();
            FavoriteImages.list.remove(oldPath);
            String trashPath = deletedImage.getTrashPath();
            File file = new File(trashPath);

            if (!file.delete()) {
                return false;
            }
        }

        dbHelper.removeAllDeletedImages();
        deletedImages.clear();
        return true;
    }

    public boolean restoreAll() {
        if (trashFolder == null || deletedImages.isEmpty()) {
            return true;
        }

        for (DeletedImage deletedImage : deletedImages) {
            String trashPath = deletedImage.getTrashPath();
            String oldPath = deletedImage.getOldPath();
            File trashFile = new File(trashPath);
            File oldFile = new File(oldPath);

            if (!trashFile.renameTo(oldFile)) {
                return false;
            }

            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(oldFile)));
        }

        dbHelper.removeAllDeletedImages();
        deletedImages.clear();
        return true;
    }
}

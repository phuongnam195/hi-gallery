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
        deletedImageList = dbHelper.getAllDeletedImages();
    }

    File trashFolder;
    ArrayList<DeletedImage> deletedImageList;
    DatabaseHelper dbHelper;
    Context context;

    public int count() {
        return deletedImageList.size();
    }

    public boolean delete(Context context, String imagePath) {
        if (trashFolder == null) {
            return false;
        }
        File imageFile = new File(imagePath);

        String trashPath = trashFolder.getPath() + "/" + imageFile.getName();

        if (!imageFile.renameTo(new File(trashPath))) {
            return false;
        }

        if (!FileUtils.removeImageFile(context, imageFile)) {
            return false;
        }

        Date datetime = new Date(System.currentTimeMillis());
        DeletedImage deletedImage = new DeletedImage(trashPath, imagePath, datetime);
        long id = dbHelper.insertDeletedImage(deletedImage);
        deletedImage.setId(id);
        deletedImageList.add(deletedImage);
        return true;
    }

    public boolean deletePermanently(long id) {
        if (trashFolder == null) {
            return false;
        }
        DeletedImage deletedImage = dbHelper.getDeletedImage(id);
        String trashPath = deletedImage.getTrashPath();
        File file = new File(trashPath);
        boolean success = file.delete();

        if (!success) {
            return false;
        }

        dbHelper.removeDeletedImage(id);
        deletedImageList.removeIf(di -> di.getId() == id);
        return true;
    }

    public boolean restore(long id) {
        if (trashFolder == null) {
            return false;
        }
        DeletedImage deletedImage = dbHelper.getDeletedImage(id);
        String trashPath = deletedImage.getTrashPath();
        String oldPath = deletedImage.getOldPath();
        File trashFile = new File(trashPath);
        File oldFile = new File(oldPath);
        boolean success = trashFile.renameTo(oldFile);

        if (!success) {
            return false;
        }

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(oldFile)));

        dbHelper.removeDeletedImage(id);
        deletedImageList.removeIf(di -> di.getId() == id);
        return true;
    }

    public boolean deletePermanentlyAll() {
        if (trashFolder == null || deletedImageList.isEmpty()) {
            return true;
        }

        for (DeletedImage deletedImage : deletedImageList) {
            String trashPath = deletedImage.getTrashPath();
            File file = new File(trashPath);

            if (!file.delete()) {
                return false;
            }
        }

        dbHelper.removeAllDeletedImages();
        deletedImageList.clear();
        return true;
    }

    public boolean restoreAll() {
        if (trashFolder == null || deletedImageList.isEmpty()) {
            return true;
        }

        for (DeletedImage deletedImage : deletedImageList) {
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
        deletedImageList.clear();
        return true;
    }
}

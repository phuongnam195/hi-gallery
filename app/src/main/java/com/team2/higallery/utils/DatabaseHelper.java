package com.team2.higallery.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.team2.higallery.models.DeletedImage;

import java.sql.Date;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ImageData";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_DELETED_IMAGES = "deleted_images";

    private static final String KEY_ID = "id";
    private static final String KEY_TRASH_PATH = "trash_path";
    private static final String KEY_OLD_PATH = "old_path";
    private static final String KEY_DATETIME = "datetime";

    private static final String CREATE_TABLE_DELETED_IMAGES = "CREATE TABLE " + TABLE_DELETED_IMAGES + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TRASH_PATH + " TEXT, " + KEY_OLD_PATH + " TEXT, " + KEY_DATETIME + " TEXT)";

    private static final String GET_LAST_ID = "SELECT last_insert_rowid()";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DELETED_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETED_IMAGES);
        onCreate(db);
    }

    public long insertDeletedImage(DeletedImage deletedImage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRASH_PATH, deletedImage.getTrashPath());
        values.put(KEY_OLD_PATH, deletedImage.getOldPath());
        values.put(KEY_DATETIME, deletedImage.getDatetime().toString());
        long id = db.insert(TABLE_DELETED_IMAGES, null, values);
        db.close();

        return id;
    }

    public DeletedImage getDeletedImage(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DELETED_IMAGES, null, KEY_ID + " = ?",
                new String[] { String.valueOf(id) },null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            DeletedImage deletedImage = new DeletedImage(cursor.getInt(0), cursor.getString(1), cursor.getString(2), Date.valueOf(cursor.getString(3)));
            cursor.close();
            return deletedImage;
        }
        return null;
    }

    public void removeDeletedImage(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DELETED_IMAGES, KEY_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    public void removeAllDeletedImages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DELETED_IMAGES, null, null);
        db.close();
    }

    public ArrayList<DeletedImage> getAllDeletedImages() {
        ArrayList<DeletedImage> deletedImageList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DELETED_IMAGES, null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                long id = cursor.getInt(0);
                String trashPath = cursor.getString(1);
                String oldPath = cursor.getString(2);
                Date datetime = Date.valueOf(cursor.getString(3));
                DeletedImage deletedImage = new DeletedImage(id, trashPath, oldPath, datetime);
                deletedImageList.add(deletedImage);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return deletedImageList;
    }
}

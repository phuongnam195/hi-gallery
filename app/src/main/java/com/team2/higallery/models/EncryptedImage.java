package com.team2.higallery.models;

import java.util.HashMap;
import java.util.Map;

public class EncryptedImage {
    private long id;
    private String oldPath;              // Đường dẫn cũ
    private String fileName;             // Tên file (đuôi .hgv)
    private boolean isSynced = true;     // true nếu đã được upload lên cloud

    public EncryptedImage() { }

    public EncryptedImage(long id, String fileName, String oldPath, boolean isSynced) {
        this.id = id;
        this.fileName = fileName;
        this.oldPath = oldPath;
        this.isSynced = isSynced;
    }

    public EncryptedImage(String fileName, String oldPath) {
        this.fileName = fileName;
        this.oldPath = oldPath;
    }

    public static EncryptedImage fromMap(Map<String, Object> map) {
        return new EncryptedImage(String.valueOf(map.get("fileName")), String.valueOf(map.get("oldPath")));
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("oldPath", oldPath);
        map.put("fileName", fileName);
        return map;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public boolean isSynced() {
        return isSynced;
    }
}

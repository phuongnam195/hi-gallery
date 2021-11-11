package com.team2.higallery.models;

import java.sql.Date;

public class DeletedImage {
    private long id;
    private String trashPath;
    private String oldPath;
    private Date datetime;

    public DeletedImage() { }

    public DeletedImage(String trashPath, String oldPath, Date datetime) {
        this.trashPath = trashPath;
        this.oldPath = oldPath;
        this.datetime = datetime;
    }

    public DeletedImage(long id, String trashPath, String oldPath, Date datetime) {
        this.id = id;
        this.trashPath = trashPath;
        this.oldPath = oldPath;
        this.datetime = datetime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrashPath() {
        return trashPath;
    }

    public void setTrashPath(String trashPath) {
        this.trashPath = trashPath;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Date getDatetime() {
        return datetime;
    }
}

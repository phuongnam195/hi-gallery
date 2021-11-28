package com.team2.higallery.models;

import com.team2.higallery.utils.FileUtils;

import java.util.ArrayList;

public class Album {
    String path;
    ArrayList<String> images = new ArrayList<>();

    public Album() {}

    public Album(String path) { this.path = path; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public ArrayList<String> getImages() { return images; }
    public void setImages(ArrayList<String> images) { this.images = images; }
    public void addImage(String imagePath) { images.add(imagePath);}

    public int getQuantity() { return images.size(); }
    public String getName() { return FileUtils.getName(path); }
    public String getAvatar() { return images.get(0); }
    public String getTitle() { return "(" + String.valueOf(getQuantity()) + ") " + getName(); }
}

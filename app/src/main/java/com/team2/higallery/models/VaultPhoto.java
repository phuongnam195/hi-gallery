package com.team2.higallery.models;

public class VaultPhoto {
    private String nameImage = "";
    private String pathImage = "";

    public VaultPhoto(String nameImage, String pathImage){
        this.nameImage = nameImage;
        this.pathImage = pathImage;
    }

    public String getNameImage(){
        return this.nameImage;
    }

    public String getPathImage() {
        return this.pathImage;
    }

    public void setNameImage(String nameImage) {
        this.nameImage = nameImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }
}

package com.riuir.calibur.data.params.newImage;

import com.riuir.calibur.data.params.QiniuImageParams;

import java.util.ArrayList;

public class CreateNewImageForAlbum {
    private int album_id;
    private ArrayList<QiniuImageParams.QiniuImageParamsData> images;

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public ArrayList<QiniuImageParams.QiniuImageParamsData> getImages() {
        return images;
    }

    public void setImages(ArrayList<QiniuImageParams.QiniuImageParamsData> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "CreateNewImageForAlbum{" +
                "album_id=" + album_id +
                ", images=" + images +
                '}';
    }
}

package com.riuir.calibur.data.album;

public class CreateNewAlbumInfo {
    private int code;
    private ChooseImageAlbum.ChooseImageAlbumData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ChooseImageAlbum.ChooseImageAlbumData getData() {
        return data;
    }

    public void setData(ChooseImageAlbum.ChooseImageAlbumData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CreateNewAlbumInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}

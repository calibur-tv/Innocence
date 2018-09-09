package com.riuir.calibur.data.anime;

public class AnimeFollowInfo {
    private int code;
    private boolean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AnimeFollowInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}

package com.riuir.calibur.data.params;

import com.riuir.calibur.data.AnimeShowInfo;

import java.util.List;

public class DramaTags {
    private int code;
    private List<AnimeShowInfo.AnimeShowInfoTags> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<AnimeShowInfo.AnimeShowInfoTags> getData() {
        return data;
    }

    public void setData(List<AnimeShowInfo.AnimeShowInfoTags> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DramaTags{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

}

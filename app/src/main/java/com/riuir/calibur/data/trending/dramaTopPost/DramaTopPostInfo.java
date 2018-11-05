package com.riuir.calibur.data.trending.dramaTopPost;

import com.riuir.calibur.data.MainTrendingInfo;

import java.util.List;

public class DramaTopPostInfo {
    private int code;
    private List<MainTrendingInfo.MainTrendingInfoList> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<MainTrendingInfo.MainTrendingInfoList> getData() {
        return data;
    }

    public void setData(List<MainTrendingInfo.MainTrendingInfoList> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DramaTopPostInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}

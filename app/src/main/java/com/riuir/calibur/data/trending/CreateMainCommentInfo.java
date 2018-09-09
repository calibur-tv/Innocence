package com.riuir.calibur.data.trending;

import java.util.List;

public class CreateMainCommentInfo {
    private int code;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList getData() {
        return data;
    }

    public void setData(TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CreateMainCommentInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

//    public class CreateMainCommentInfoData{
//        private int id;
//        private String content;
//        private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainImages> images;
//        private int modal_id;
//        private String created_at;
//        private int floor_count;
//        private int to_user_id;
//        private int from_user_id;
//        private String from_user_name;
//        private String from_user_zone;
//        private String from_user_avatar;
//        private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainComment comments;
//        private boolean liked;
//        private int like_count;
//
//
//    }
}

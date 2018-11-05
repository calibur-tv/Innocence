package com.riuir.calibur.data.trending;

import java.util.List;

public class CreateMainCommentInfo {
    private int code;
    private CreateMainCommentInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CreateMainCommentInfoData getData() {
        return data;
    }

    public void setData(CreateMainCommentInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CreateMainCommentInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class CreateMainCommentInfoData{
        private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList data;
        private int exp;
        private String message;

        public TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList getData() {
            return data;
        }

        public void setData(TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList data) {
            this.data = data;
        }

        public int getExp() {
            return exp;
        }

        public void setExp(int exp) {
            this.exp = exp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "CreateMainCommentInfoData{" +
                    "data=" + data +
                    ", exp=" + exp +
                    ", message='" + message + '\'' +
                    '}';
        }
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

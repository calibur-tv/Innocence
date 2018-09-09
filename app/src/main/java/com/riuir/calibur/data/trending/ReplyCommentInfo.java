package com.riuir.calibur.data.trending;

public class ReplyCommentInfo {
    private int code;
    private TrendingChildCommentInfo.TrendingChildCommentInfoList data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TrendingChildCommentInfo.TrendingChildCommentInfoList getData() {
        return data;
    }

    public void setData(TrendingChildCommentInfo.TrendingChildCommentInfoList data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReplyCommentInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

//    public class ReplyCommentInfoData{
//        private int id;
//        private String content;
//        private String created_at;
//        private int parent_id;
//        private int from_user_id;
//        private String from_user_name;
//        private String from_user_zone;
//        private String from_user_avatar;
//        private int to_user_id;
//        private String to_user_name;
//        private String to_user_zone;
//        private String to_user_avatar;
//
//
//    }
}

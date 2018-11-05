package com.riuir.calibur.data.trending;

public class ReplyCommentInfo {
    private int code;
    private ReplyCommentInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ReplyCommentInfoData getData() {
        return data;
    }

    public void setData(ReplyCommentInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReplyCommentInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class ReplyCommentInfoData{
        private TrendingChildCommentInfo.TrendingChildCommentInfoList data;
        private int exp;
        private String message;

        public TrendingChildCommentInfo.TrendingChildCommentInfoList getData() {
            return data;
        }

        public void setData(TrendingChildCommentInfo.TrendingChildCommentInfoList data) {
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
            return "ReplyCommentInfoData{" +
                    "data=" + data +
                    ", exp=" + exp +
                    ", message='" + message + '\'' +
                    '}';
        }
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

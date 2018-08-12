package com.riuir.calibur.data.trending;

import java.util.List;

public class TrendingChildCommentInfo {
    private int code;
    private TrendingChildCommentInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TrendingChildCommentInfoData getData() {
        return data;
    }

    public void setData(TrendingChildCommentInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TrendingChildCommentInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class TrendingChildCommentInfoData{
        private List<TrendingChildCommentInfoList> list;
        private int total;
        private boolean noMore;

        public List<TrendingChildCommentInfoList> getList() {
            return list;
        }

        public void setList(List<TrendingChildCommentInfoList> list) {
            this.list = list;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public boolean isNoMore() {
            return noMore;
        }

        public void setNoMore(boolean noMore) {
            this.noMore = noMore;
        }

        @Override
        public String toString() {
            return "TrendingChildCommentInfoData{" +
                    "list=" + list +
                    ", total=" + total +
                    ", noMore=" + noMore +
                    '}';
        }
    }
    public class TrendingChildCommentInfoList{
        private int id;
        private String content;
        private String created_at;
        private int parent_id;
        private int from_user_id;
        private String from_user_name;
        private String from_user_zone;
        private String from_user_avatar;
        private int to_user_id;
        private String to_user_name;
        private String to_user_zone;
        private String to_user_avatar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getParent_id() {
            return parent_id;
        }

        public void setParent_id(int parent_id) {
            this.parent_id = parent_id;
        }

        public int getFrom_user_id() {
            return from_user_id;
        }

        public void setFrom_user_id(int from_user_id) {
            this.from_user_id = from_user_id;
        }

        public String getFrom_user_name() {
            return from_user_name;
        }

        public void setFrom_user_name(String from_user_name) {
            this.from_user_name = from_user_name;
        }

        public String getFrom_user_zone() {
            return from_user_zone;
        }

        public void setFrom_user_zone(String from_user_zone) {
            this.from_user_zone = from_user_zone;
        }

        public String getFrom_user_avatar() {
            return from_user_avatar;
        }

        public void setFrom_user_avatar(String from_user_avatar) {
            this.from_user_avatar = from_user_avatar;
        }

        public int getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(int to_user_id) {
            this.to_user_id = to_user_id;
        }

        public String getTo_user_name() {
            return to_user_name;
        }

        public void setTo_user_name(String to_user_name) {
            this.to_user_name = to_user_name;
        }

        public String getTo_user_zone() {
            return to_user_zone;
        }

        public void setTo_user_zone(String to_user_zone) {
            this.to_user_zone = to_user_zone;
        }

        public String getTo_user_avatar() {
            return to_user_avatar;
        }

        public void setTo_user_avatar(String to_user_avatar) {
            this.to_user_avatar = to_user_avatar;
        }

        @Override
        public String toString() {
            return "TrendingChildCommentInfoList{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", parent_id=" + parent_id +
                    ", from_user_id=" + from_user_id +
                    ", from_user_name='" + from_user_name + '\'' +
                    ", from_user_zone='" + from_user_zone + '\'' +
                    ", from_user_avatar='" + from_user_avatar + '\'' +
                    ", to_user_id=" + to_user_id +
                    ", to_user_name='" + to_user_name + '\'' +
                    ", to_user_zone='" + to_user_zone + '\'' +
                    ", to_user_avatar='" + to_user_avatar + '\'' +
                    '}';
        }
    }
}

package com.riuir.calibur.data.card;

import java.io.Serializable;
import java.util.List;

public class CardShowInfoCommentMain {
    private int code;
    private CardShowInfoCommentMainData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CardShowInfoCommentMainData getData() {
        return data;
    }

    public void setData(CardShowInfoCommentMainData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CardShowInfoCommentMain{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class CardShowInfoCommentMainData{
        private int total;
        private boolean noMore;
        private List<CardShowInfoCommentMainList> list;

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

        public List<CardShowInfoCommentMainList> getList() {
            return list;
        }

        public void setList(List<CardShowInfoCommentMainList> list) {
            this.list = list;
        }

        @Override
        public String toString() {
            return "CardShowInfoCommentMainData{" +
                    "total=" + total +
                    ", noMore=" + noMore +
                    ", list=" + list +
                    '}';
        }
    }

    public class CardShowInfoCommentMainList implements Serializable {
        private int id;
        private String content;
        private List<CardShowInfoCommentMainImages> images;
        private int modal_id;
        private String created_at;
        private int floor_count;
        private int to_user_id;
        private int from_user_id;
        private String from_user_name;
        private String from_user_zone;
        private String from_user_avatar;
        private CardShowInfoCommentMainComment comments;
        private boolean liked;
        private int like_count;

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

        public List<CardShowInfoCommentMainImages> getImages() {
            return images;
        }

        public void setImages(List<CardShowInfoCommentMainImages> images) {
            this.images = images;
        }

        public int getModal_id() {
            return modal_id;
        }

        public void setModal_id(int modal_id) {
            this.modal_id = modal_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getFloor_count() {
            return floor_count;
        }

        public void setFloor_count(int floor_count) {
            this.floor_count = floor_count;
        }

        public int getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(int to_user_id) {
            this.to_user_id = to_user_id;
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

        public CardShowInfoCommentMainComment getComments() {
            return comments;
        }

        public void setComments(CardShowInfoCommentMainComment comments) {
            this.comments = comments;
        }

        public boolean isLiked() {
            return liked;
        }

        public void setLiked(boolean liked) {
            this.liked = liked;
        }

        public int getLike_count() {
            return like_count;
        }

        public void setLike_count(int like_count) {
            this.like_count = like_count;
        }

        @Override
        public String toString() {
            return "CardShowInfoCommentMainList{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    ", images=" + images +
                    ", modal_id=" + modal_id +
                    ", created_at='" + created_at + '\'' +
                    ", floor_count=" + floor_count +
                    ", to_user_id=" + to_user_id +
                    ", from_user_id=" + from_user_id +
                    ", from_user_name='" + from_user_name + '\'' +
                    ", from_user_zone='" + from_user_zone + '\'' +
                    ", from_user_avatar='" + from_user_avatar + '\'' +
                    ", comments=" + comments +
                    ", liked=" + liked +
                    ", like_count=" + like_count +
                    '}';
        }
    }

    public class CardShowInfoCommentMainImages implements Serializable{
        private String url;
        private String width;
        private String height;
        private String size;
        private String type;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "CardShowInfoCommentMainImages{" +
                    "url='" + url + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", size='" + size + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
    public class CardShowInfoCommentMainComment implements Serializable{
        private int total;
        private boolean noMore;
        private List<CardShowInfoCommentMainCommentList> list;

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

        public List<CardShowInfoCommentMainCommentList> getList() {
            return list;
        }

        public void setList(List<CardShowInfoCommentMainCommentList> list) {
            this.list = list;
        }

        @Override
        public String toString() {
            return "CardShowInfoCommentMainComment{" +
                    "total=" + total +
                    ", noMore=" + noMore +
                    ", list=" + list +
                    '}';
        }
    }
    public class CardShowInfoCommentMainCommentList implements Serializable{
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
            return "CardShowInfoCommentMainCommentList{" +
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

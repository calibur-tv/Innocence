package com.riuir.calibur.data;

import java.util.List;


//TODO 暂时作废

public class MainCardInfo {
    private int code;
    private MainCardInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public MainCardInfoData getData() {
        return data;
    }

    public void setData(MainCardInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MainCardInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class MainCardInfoData{
        private List<MainCardInfoList> list;
        private int total;
        private boolean noMore;

        public List<MainCardInfoList> getList() {
            return list;
        }

        public void setList(List<MainCardInfoList> list) {
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
            return "MainCardInfoData{" +
                    "list=" + list +
                    ", total=" + total +
                    ", noMore=" + noMore +
                    '}';
        }
    }

    public class MainCardInfoList{

        //公用字段
        private int id;
        private MainCardInfoUser user;
        private MainCardInfoBangumi bangumi;
        private String created_at;
        private String updated_at;

        //帖子字段
        private String title;
        private String desc;
        private List<MainCardImages> images;
        private int view_count;
        private int like_count;
        private int comment_count;
        private int mark_count;
        private boolean liked;
        private boolean marked;
        private boolean commented;
        private boolean is_nice;


        //图片需要字段
        private int user_id;
        private int bangumi_id;
        private String name;
        private int image_count;
        private MainImageInfoSource source;
        private boolean is_album;
        private boolean is_creator;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public MainCardInfoUser getUser() {
            return user;
        }

        public void setUser(MainCardInfoUser user) {
            this.user = user;
        }

        public MainCardInfoBangumi getBangumi() {
            return bangumi;
        }

        public void setBangumi(MainCardInfoBangumi bangumi) {
            this.bangumi = bangumi;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public List<MainCardImages> getImages() {
            return images;
        }

        public void setImages(List<MainCardImages> images) {
            this.images = images;
        }

        public int getView_count() {
            return view_count;
        }

        public void setView_count(int view_count) {
            this.view_count = view_count;
        }

        public int getLike_count() {
            return like_count;
        }

        public void setLike_count(int like_count) {
            this.like_count = like_count;
        }

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        public int getMark_count() {
            return mark_count;
        }

        public void setMark_count(int mark_count) {
            this.mark_count = mark_count;
        }

        public boolean isLiked() {
            return liked;
        }

        public void setLiked(boolean liked) {
            this.liked = liked;
        }

        public boolean isMarked() {
            return marked;
        }

        public void setMarked(boolean marked) {
            this.marked = marked;
        }

        public boolean isCommented() {
            return commented;
        }

        public void setCommented(boolean commented) {
            this.commented = commented;
        }

        public boolean isIs_nice() {
            return is_nice;
        }

        public void setIs_nice(boolean is_nice) {
            this.is_nice = is_nice;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getBangumi_id() {
            return bangumi_id;
        }

        public void setBangumi_id(int bangumi_id) {
            this.bangumi_id = bangumi_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getImage_count() {
            return image_count;
        }

        public void setImage_count(int image_count) {
            this.image_count = image_count;
        }

        public MainImageInfoSource getSource() {
            return source;
        }

        public void setSource(MainImageInfoSource source) {
            this.source = source;
        }

        public boolean isIs_album() {
            return is_album;
        }

        public void setIs_album(boolean is_album) {
            this.is_album = is_album;
        }

        public boolean isIs_creator() {
            return is_creator;
        }

        public void setIs_creator(boolean is_creator) {
            this.is_creator = is_creator;
        }


        @Override
        public String toString() {
            return "MainCardInfoList{" +
                    "id=" + id +
                    ", user=" + user +
                    ", bangumi=" + bangumi +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    ", title='" + title + '\'' +
                    ", desc='" + desc + '\'' +
                    ", images=" + images +
                    ", view_count=" + view_count +
                    ", like_count=" + like_count +
                    ", comment_count=" + comment_count +
                    ", mark_count=" + mark_count +
                    ", liked=" + liked +
                    ", marked=" + marked +
                    ", commented=" + commented +
                    ", is_nice=" + is_nice +
                    ", user_id=" + user_id +
                    ", bangumi_id=" + bangumi_id +
                    ", name='" + name + '\'' +
                    ", image_count=" + image_count +
                    ", source=" + source +
                    ", is_album=" + is_album +
                    ", is_creator=" + is_creator +
                    '}';
        }
    }

    public class  MainCardImages{
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
            return "MainCardImages{" +
                    "url='" + url + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", size='" + size + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public class  MainCardInfoUser{
        private int id;
        private String zone;
        private String nickname;
        private String avatar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        @Override
        public String toString() {
            return "MainCardInfoUser{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }

    public class MainCardInfoBangumi{
        private int id;
        private String name;
        private String avatar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        @Override
        public String toString() {
            return "MainCardInfoBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }

    public class MainImageInfoSource{
        private String url;
        private int width;
        private int height;
        private int size;
        private String type;
    }

}




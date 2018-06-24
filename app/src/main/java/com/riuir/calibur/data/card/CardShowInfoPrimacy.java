package com.riuir.calibur.data.card;

import java.io.Serializable;
import java.util.List;

public class CardShowInfoPrimacy  {
    private int code;
    private CardShowInfoPrimacyData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CardShowInfoPrimacyData getData() {
        return data;
    }

    public void setData(CardShowInfoPrimacyData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CardShowInfoPrimacy{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class CardShowInfoPrimacyData{
        private CardShowInfoPrimacyBangumi bangumi;
        private CardShowInfoPrimacyPost post;
        private CardShowInfoPrimacyUser user;

        public CardShowInfoPrimacyBangumi getBangumi() {
            return bangumi;
        }

        public void setBangumi(CardShowInfoPrimacyBangumi bangumi) {
            this.bangumi = bangumi;
        }

        public CardShowInfoPrimacyPost getPost() {
            return post;
        }

        public void setPost(CardShowInfoPrimacyPost post) {
            this.post = post;
        }

        public CardShowInfoPrimacyUser getUser() {
            return user;
        }

        public void setUser(CardShowInfoPrimacyUser user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "CardShowInfoPrimacyData{" +
                    "bangumi=" + bangumi +
                    ", post=" + post +
                    ", user=" + user +
                    '}';
        }
    }

    public class CardShowInfoPrimacyBangumi{
        private int id;
        private String name;
        private String avatar;
        private String summary;
        private boolean followed;

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

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public boolean isFollowed() {
            return followed;
        }

        public void setFollowed(boolean followed) {
            this.followed = followed;
        }

        @Override
        public String toString() {
            return "CardShowInfoPrimacyBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", summary='" + summary + '\'' +
                    ", followed=" + followed +
                    '}';
        }
    }
    public class CardShowInfoPrimacyPost{
        private int id;
        private int comment_count;
        private int like_count;
        private int view_count;
        private int mark_count;
        private String title;
        private String desc;
        private boolean liked;
        private boolean marked;
        private boolean commented;
        private String content;
        private List<CardShowInfoPrimacyImages> images;
        private String created_at;
        private String updated_at;
        private List<CardShowInfoPrimacyPreviewImages> preview_images;
        private List<CardShowInfoPrimacyLikeUser> like_users;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        public int getLike_count() {
            return like_count;
        }

        public void setLike_count(int like_count) {
            this.like_count = like_count;
        }

        public int getView_count() {
            return view_count;
        }

        public void setView_count(int view_count) {
            this.view_count = view_count;
        }

        public int getMark_count() {
            return mark_count;
        }

        public void setMark_count(int mark_count) {
            this.mark_count = mark_count;
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<CardShowInfoPrimacyImages> getImages() {
            return images;
        }

        public void setImages(List<CardShowInfoPrimacyImages> images) {
            this.images = images;
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

        public List<CardShowInfoPrimacyPreviewImages> getPreview_images() {
            return preview_images;
        }

        public void setPreview_images(List<CardShowInfoPrimacyPreviewImages> preview_images) {
            this.preview_images = preview_images;
        }

        public List<CardShowInfoPrimacyLikeUser> getLike_users() {
            return like_users;
        }

        public void setLike_users(List<CardShowInfoPrimacyLikeUser> like_users) {
            this.like_users = like_users;
        }

        @Override
        public String toString() {
            return "CardShowInfoPrimacyPost{" +
                    "id=" + id +
                    ", comment_count=" + comment_count +
                    ", like_count=" + like_count +
                    ", view_count=" + view_count +
                    ", mark_count=" + mark_count +
                    ", title='" + title + '\'' +
                    ", desc='" + desc + '\'' +
                    ", liked=" + liked +
                    ", marked=" + marked +
                    ", commented=" + commented +
                    ", content='" + content + '\'' +
                    ", images=" + images +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    ", preview_images=" + preview_images +
                    ", like_users=" + like_users +
                    '}';
        }
    }
    public class CardShowInfoPrimacyUser{
        private int id;
        private String zone;
        private String avatar;
        private String nickname;

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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        @Override
        public String toString() {
            return "CardShowInfoPrimacyUser{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }
    }

    public class CardShowInfoPrimacyImages{
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
            return "CardShowInfoPrimacyImages{" +
                    "url='" + url + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", size='" + size + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public class CardShowInfoPrimacyPreviewImages {
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
            return "CardShowInfoPrimacyPreviewImages{" +
                    "url='" + url + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", size='" + size + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public class CardShowInfoPrimacyLikeUser{
        private int id;
        private String zone;
        private String avatar;
        private String nickname;
        private Long created_at;

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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Long created_at) {
            this.created_at = created_at;
        }

        @Override
        public String toString() {
            return "CardShowInfoPrimacyLikeUser{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", created_at=" + created_at +
                    '}';
        }
    }
}

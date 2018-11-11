package com.riuir.calibur.data.anime;

import android.print.PrinterId;

import java.io.Serializable;
import java.util.List;

public class SearchAnimeInfo {
    private int code;
    private SearchAnimeInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SearchAnimeInfoData getData() {
        return data;
    }

    public void setData(SearchAnimeInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SearchAnimeInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class SearchAnimeInfoData implements Serializable {
        private List<SearchAnimeInfoList> list;
        private int total;
        private boolean noMore;

        public List<SearchAnimeInfoList> getList() {
            return list;
        }

        public void setList(List<SearchAnimeInfoList> list) {
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
            return "SearchAnimeInfoData{" +
                    "list=" + list +
                    ", total=" + total +
                    ", noMore=" + noMore +
                    '}';
        }
    }

    public class SearchAnimeInfoList implements Serializable {
        private int id;
        private String title;
        private String desc;
        private String top_at;
        private boolean is_nice;
        private boolean is_creator;
        private int like_count;
        private int reward_count;
        private int comment_count;
        private int mark_count;
        private SearchAnimeInfoBangumi bangumi;
        private SearchAnimeInfoUser user;
        private List<SearchAnimeInfoTags> tags;
        private int total;
        private int star_count;
        private int fans_count;
        private SearchAnimeInfoUser lover;
        private String name;
        private String avatar;
        private String intro;
        private String poster;
        private String type;
        private String summary;
        private String zone;
        private String nickname;
        private String signature;
        private String created_at;
        private String updated_at;
        private List<SearchAnimeInfoImage> images;

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

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
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

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public List<SearchAnimeInfoImage> getImages() {
            return images;
        }

        public void setImages(List<SearchAnimeInfoImage> images) {
            this.images = images;
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

        public String getTop_at() {
            return top_at;
        }

        public void setTop_at(String top_at) {
            this.top_at = top_at;
        }

        public boolean isIs_nice() {
            return is_nice;
        }

        public void setIs_nice(boolean is_nice) {
            this.is_nice = is_nice;
        }

        public boolean isIs_creator() {
            return is_creator;
        }

        public void setIs_creator(boolean is_creator) {
            this.is_creator = is_creator;
        }

        public int getLike_count() {
            return like_count;
        }

        public void setLike_count(int like_count) {
            this.like_count = like_count;
        }

        public int getReward_count() {
            return reward_count;
        }

        public void setReward_count(int reward_count) {
            this.reward_count = reward_count;
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

        public SearchAnimeInfoBangumi getBangumi() {
            return bangumi;
        }

        public void setBangumi(SearchAnimeInfoBangumi bangumi) {
            this.bangumi = bangumi;
        }

        public SearchAnimeInfoUser getUser() {
            return user;
        }

        public void setUser(SearchAnimeInfoUser user) {
            this.user = user;
        }

        public List<SearchAnimeInfoTags> getTags() {
            return tags;
        }

        public void setTags(List<SearchAnimeInfoTags> tags) {
            this.tags = tags;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getStar_count() {
            return star_count;
        }

        public void setStar_count(int star_count) {
            this.star_count = star_count;
        }

        public int getFans_count() {
            return fans_count;
        }

        public void setFans_count(int fans_count) {
            this.fans_count = fans_count;
        }

        public SearchAnimeInfoUser getLover() {
            return lover;
        }

        public void setLover(SearchAnimeInfoUser lover) {
            this.lover = lover;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        @Override
        public String toString() {
            return "SearchAnimeInfoList{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", desc='" + desc + '\'' +
                    ", top_at='" + top_at + '\'' +
                    ", is_nice=" + is_nice +
                    ", is_creator=" + is_creator +
                    ", like_count=" + like_count +
                    ", reward_count=" + reward_count +
                    ", comment_count=" + comment_count +
                    ", mark_count=" + mark_count +
                    ", bangumi=" + bangumi +
                    ", user=" + user +
                    ", tags=" + tags +
                    ", total=" + total +
                    ", star_count=" + star_count +
                    ", fans_count=" + fans_count +
                    ", lover=" + lover +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", intro='" + intro + '\'' +
                    ", poster='" + poster + '\'' +
                    ", type='" + type + '\'' +
                    ", summary='" + summary + '\'' +
                    ", zone='" + zone + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", signature='" + signature + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    ", images=" + images +
                    '}';
        }
    }

    public class SearchAnimeInfoImage implements Serializable {
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
            return "SearchAnimeInfoImage{" +
                    "url='" + url + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", size='" + size + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public class SearchAnimeInfoBangumi implements Serializable {
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
            return "SearchAnimeInfoBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }

    public class SearchAnimeInfoUser implements Serializable {
        private int id;
        private String nickname;
        private String avatar;
        private String zone;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        @Override
        public String toString() {
            return "SearchAnimeInfoUser{" +
                    "id=" + id +
                    ", nickname='" + nickname + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", zone='" + zone + '\'' +
                    '}';
        }
    }

    public class SearchAnimeInfoTags implements Serializable {
        private int id;
        private String name;

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

        @Override
        public String toString() {
            return "SearchAnimeInfoTags{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}

package com.riuir.calibur.data.anime;

import android.print.PrinterId;

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

    public class SearchAnimeInfoData{
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

    public class SearchAnimeInfoList{
        private int id;
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

        @Override
        public String toString() {
            return "SearchAnimeInfoList{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", intro='" + intro + '\'' +
                    ", poster='" + poster + '\'' +
                    ", type=" + type +
                    ", summary='" + summary + '\'' +
                    ", zone='" + zone + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", signature='" + signature + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", images=" + images +
                    '}';
        }
    }

    public class SearchAnimeInfoImage{
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
}

package com.riuir.calibur.data.anime;

import java.util.Arrays;

public class AnimeVideosActivityInfo {
    private int code;
    private AnimeVideosActivityInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AnimeVideosActivityInfoData getData() {
        return data;
    }

    public void setData(AnimeVideosActivityInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AnimeVideosActivityInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class AnimeVideosActivityInfoData{
        private AnimeVideosActivityInfoInfo info;
        private AnimeVideosActivityInfoBangumi bangumi;
        private AnimeVideosActivityInfoSeason season;
        private AnimeShowVideosInfo.AnimeShowVideosInfoData list;

        public AnimeVideosActivityInfoInfo getInfo() {
            return info;
        }

        public void setInfo(AnimeVideosActivityInfoInfo info) {
            this.info = info;
        }

        public AnimeVideosActivityInfoBangumi getBangumi() {
            return bangumi;
        }

        public void setBangumi(AnimeVideosActivityInfoBangumi bangumi) {
            this.bangumi = bangumi;
        }

        public AnimeVideosActivityInfoSeason getSeason() {
            return season;
        }

        public void setSeason(AnimeVideosActivityInfoSeason season) {
            this.season = season;
        }

        public AnimeShowVideosInfo.AnimeShowVideosInfoData getList() {
            return list;
        }

        public void setList(AnimeShowVideosInfo.AnimeShowVideosInfoData list) {
            this.list = list;
        }

        @Override
        public String toString() {
            return "AnimeVideosActivityInfoData{" +
                    "info=" + info +
                    ", bangumi=" + bangumi +
                    ", season=" + season +
                    ", list=" + list +
                    '}';
        }
    }

    public class AnimeVideosActivityInfoInfo{
        private int id;
        private int user_id;
        private int bangumi_id;
        private String src;
        private String name;
        private int part;
        private String poster;
        private boolean other_site;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPart() {
            return part;
        }

        public void setPart(int part) {
            this.part = part;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public boolean isOther_site() {
            return other_site;
        }

        public void setOther_site(boolean other_site) {
            this.other_site = other_site;
        }

        @Override
        public String toString() {
            return "AnimeVideosActivityInfoInfo{" +
                    "id=" + id +
                    ", user_id=" + user_id +
                    ", bangumi_id=" + bangumi_id +
                    ", src='" + src + '\'' +
                    ", name='" + name + '\'' +
                    ", part=" + part +
                    ", poster='" + poster + '\'' +
                    ", other_site=" + other_site +
                    '}';
        }
    }

    public class AnimeVideosActivityInfoBangumi{
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
            return "AnimeVideosActivityInfoBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", summary='" + summary + '\'' +
                    ", followed=" + followed +
                    '}';
        }
    }
    public class AnimeVideosActivityInfoSeason{
        private String[] name;
        private int[] part;
        private String[] time;
        private int re;

        public String[] getName() {
            return name;
        }

        public void setName(String[] name) {
            this.name = name;
        }

        public int[] getPart() {
            return part;
        }

        public void setPart(int[] part) {
            this.part = part;
        }

        public String[] getTime() {
            return time;
        }

        public void setTime(String[] time) {
            this.time = time;
        }

        public int getRe() {
            return re;
        }

        public void setRe(int re) {
            this.re = re;
        }

        @Override
        public String toString() {
            return "AnimeVideosActivityInfoSeason{" +
                    "name=" + Arrays.toString(name) +
                    ", part=" + Arrays.toString(part) +
                    ", time=" + Arrays.toString(time) +
                    ", re=" + re +
                    '}';
        }
    }
}

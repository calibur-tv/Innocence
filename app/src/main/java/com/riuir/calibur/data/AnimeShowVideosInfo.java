package com.riuir.calibur.data;

import java.util.List;

public class AnimeShowVideosInfo {
    private int code;
    private AnimeShowVideosInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AnimeShowVideosInfoData getData() {
        return data;
    }

    public void setData(AnimeShowVideosInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AnimeShowVideosInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class AnimeShowVideosInfoData{
        private List<AnimeShowVideosInfoVideos> videos;
        private boolean has_season;
        private int total;

        public List<AnimeShowVideosInfoVideos> getVideos() {
            return videos;
        }

        public void setVideos(List<AnimeShowVideosInfoVideos> videos) {
            this.videos = videos;
        }

        public boolean isHas_season() {
            return has_season;
        }

        public void setHas_season(boolean has_season) {
            this.has_season = has_season;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "AnimeShowVideosInfoData{" +
                    "videos=" + videos +
                    ", has_season=" + has_season +
                    ", total=" + total +
                    '}';
        }
    }

    public class AnimeShowVideosInfoVideos{
        private String name;
        private String time;
        private int base;
        private List<AnimeShowVideosInfoDataEpisodes> data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getBase() {
            return base;
        }

        public void setBase(int base) {
            this.base = base;
        }

        public List<AnimeShowVideosInfoDataEpisodes> getData() {
            return data;
        }

        public void setData(List<AnimeShowVideosInfoDataEpisodes> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "AnimeShowVideosInfoData{" +
                    "name='" + name + '\'' +
                    ", time='" + time + '\'' +
                    ", base=" + base +
                    ", data=" + data +
                    '}';
        }
    }

    public class AnimeShowVideosInfoDataEpisodes{
        private int id;
        private String name;
        private String poster;
        private int part;

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

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public int getPart() {
            return part;
        }

        public void setPart(int part) {
            this.part = part;
        }

        @Override
        public String toString() {
            return "AnimeShowVideosInfoDataEpisodes{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", poster='" + poster + '\'' +
                    ", part=" + part +
                    '}';
        }
    }
}

package com.riuir.calibur.data.loop;

import java.util.List;

public class BannerLoopInfo {
    private int code;
    private List<BannerLoopInfoData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<BannerLoopInfoData> getData() {
        return data;
    }

    public void setData(List<BannerLoopInfoData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BannerLoopInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class BannerLoopInfoData{
        private int id;
        private String title;
        private String desc;
        private String link;
        private String poster;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        @Override
        public String toString() {
            return "BannerLoopInfoData{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", desc='" + desc + '\'' +
                    ", link='" + link + '\'' +
                    ", poster='" + poster + '\'' +
                    '}';
        }
    }
}

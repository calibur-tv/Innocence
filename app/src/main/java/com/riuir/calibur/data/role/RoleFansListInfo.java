package com.riuir.calibur.data.role;

import java.util.List;

public class RoleFansListInfo {
    private int code;
    private RoleFansListInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public RoleFansListInfoData getData() {
        return data;
    }

    public void setData(RoleFansListInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RoleFansListInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class  RoleFansListInfoData{
        private List<RoleFansListInfoList> list;
        private int total;
        private boolean noMore;

        public List<RoleFansListInfoList> getList() {
            return list;
        }

        public void setList(List<RoleFansListInfoList> list) {
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
            return "RoleFansListInfoData{" +
                    "list=" + list +
                    ", total=" + total +
                    ", noMore=" + noMore +
                    '}';
        }
    }

    public class  RoleFansListInfoList{
        private int id;
        private String zone;
        private String avatar;
        private String nickname;
        private String score;

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

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        @Override
        public String toString() {
            return "RoleFansListInfoData{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", score='" + score + '\'' +
                    '}';
        }
    }
}

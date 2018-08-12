package com.riuir.calibur.data.role;

import java.util.List;

public class RoleFansListInfo {
    private int code;
    private List<RoleFansListInfoData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<RoleFansListInfoData> getData() {
        return data;
    }

    public void setData(List<RoleFansListInfoData> data) {
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

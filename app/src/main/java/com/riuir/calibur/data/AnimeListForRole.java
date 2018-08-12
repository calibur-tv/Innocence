package com.riuir.calibur.data;

import java.util.List;

public class AnimeListForRole {
    private int code;
    private List<AnimeListForRoleData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<AnimeListForRoleData> getData() {
        return data;
    }

    public void setData(List<AnimeListForRoleData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AnimeListForRole{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class AnimeListForRoleData{
        private int id;
        private String avatar;
        private String name;
        private String intro;
        private int star_count;
        private int fans_count;
        private int bangumi_id;
        private String bangumi_avatar;
        private String bangumi_name;
        private int lover_id;
        private String lover_avatar;
        private String lover_nickname;
        private String lover_zone;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
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

        public int getBangumi_id() {
            return bangumi_id;
        }

        public void setBangumi_id(int bangumi_id) {
            this.bangumi_id = bangumi_id;
        }

        public String getBangumi_avatar() {
            return bangumi_avatar;
        }

        public void setBangumi_avatar(String bangumi_avatar) {
            this.bangumi_avatar = bangumi_avatar;
        }

        public String getBangumi_name() {
            return bangumi_name;
        }

        public void setBangumi_name(String bangumi_name) {
            this.bangumi_name = bangumi_name;
        }

        public int getLover_id() {
            return lover_id;
        }

        public void setLover_id(int lover_id) {
            this.lover_id = lover_id;
        }

        public String getLover_avatar() {
            return lover_avatar;
        }

        public void setLover_avatar(String lover_avatar) {
            this.lover_avatar = lover_avatar;
        }

        public String getLover_nickname() {
            return lover_nickname;
        }

        public void setLover_nickname(String lover_nickname) {
            this.lover_nickname = lover_nickname;
        }

        public String getLover_zone() {
            return lover_zone;
        }

        public void setLover_zone(String lover_zone) {
            this.lover_zone = lover_zone;
        }

        @Override
        public String toString() {
            return "AnimeListForRoleData{" +
                    "id=" + id +
                    ", avatar='" + avatar + '\'' +
                    ", name='" + name + '\'' +
                    ", intro='" + intro + '\'' +
                    ", star_count=" + star_count +
                    ", fans_count=" + fans_count +
                    ", bangumi_id=" + bangumi_id +
                    ", bangumi_avatar='" + bangumi_avatar + '\'' +
                    ", bangumi_name='" + bangumi_name + '\'' +
                    ", lover_id=" + lover_id +
                    ", lover_avatar='" + lover_avatar + '\'' +
                    ", lover_nickname='" + lover_nickname + '\'' +
                    ", lover_zone='" + lover_zone + '\'' +
                    '}';
        }
    }
}

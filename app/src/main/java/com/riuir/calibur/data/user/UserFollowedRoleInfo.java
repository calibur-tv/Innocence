package com.riuir.calibur.data.user;

import java.util.List;

public class UserFollowedRoleInfo {

    private int code;
    private UserFollowedRoleInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserFollowedRoleInfoData getData() {
        return data;
    }

    public void setData(UserFollowedRoleInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserFollowedRoleInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class UserFollowedRoleInfoData{
        private List<UserFollowedRoleInfoList> list;
        private boolean noMore;
        private int total;

        public List<UserFollowedRoleInfoList> getList() {
            return list;
        }

        public void setList(List<UserFollowedRoleInfoList> list) {
            this.list = list;
        }

        public boolean isNoMore() {
            return noMore;
        }

        public void setNoMore(boolean noMore) {
            this.noMore = noMore;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "UserFollowedRoleInfoData{" +
                    "list=" + list +
                    ", noMore=" + noMore +
                    ", total=" + total +
                    '}';
        }
    }

    public class UserFollowedRoleInfoList{
        private int id;
        private String avatar;
        private String name;
        private String intro;
        private String star_count;
        private String fans_count;
        private UserFollowedRoleInfoBangumi bangumi;
        private UserFollowedRoleInfoLover lover;
        private int has_star;

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

        public String getStar_count() {
            return star_count;
        }

        public void setStar_count(String star_count) {
            this.star_count = star_count;
        }

        public String getFans_count() {
            return fans_count;
        }

        public void setFans_count(String fans_count) {
            this.fans_count = fans_count;
        }

        public UserFollowedRoleInfoBangumi getBangumi() {
            return bangumi;
        }

        public void setBangumi(UserFollowedRoleInfoBangumi bangumi) {
            this.bangumi = bangumi;
        }

        public UserFollowedRoleInfoLover getLover() {
            return lover;
        }

        public void setLover(UserFollowedRoleInfoLover lover) {
            this.lover = lover;
        }

        public int getHas_star() {
            return has_star;
        }

        public void setHas_star(int has_star) {
            this.has_star = has_star;
        }

        @Override
        public String toString() {
            return "UserFollowedRoleInfoList{" +
                    "id=" + id +
                    ", avatar='" + avatar + '\'' +
                    ", name='" + name + '\'' +
                    ", intro='" + intro + '\'' +
                    ", star_count='" + star_count + '\'' +
                    ", fans_count='" + fans_count + '\'' +
                    ", bangumi=" + bangumi +
                    ", lover=" + lover +
                    ", has_star=" + has_star +
                    '}';
        }
    }
    public class UserFollowedRoleInfoBangumi{
        private int id;
        private String avatar;
        private String name;

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

        @Override
        public String toString() {
            return "UserFollowedRoleInfoBangumi{" +
                    "id=" + id +
                    ", avatar='" + avatar + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
    public class UserFollowedRoleInfoLover{
        private int id;
        private String avatar;
        private String nickname;
        private String zone;

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

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        @Override
        public String toString() {
            return "UserFollowedRoleInfoLover{" +
                    "id=" + id +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", zone='" + zone + '\'' +
                    '}';
        }
    }
}

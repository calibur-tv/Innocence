package com.riuir.calibur.data.user;

import java.util.List;

public class UserFollowedBangumiInfo {
    private int code;
    private List<UserFollowedBangumiInfoData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<UserFollowedBangumiInfoData> getData() {
        return data;
    }

    public void setData(List<UserFollowedBangumiInfoData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserFollowedBangumiInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class UserFollowedBangumiInfoData{
        private int id;
        private String name;
        private String avatar;
        private String created_at;

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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        @Override
        public String toString() {
            return "UserFollowedBangumiInfoData{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", created_at='" + created_at + '\'' +
                    '}';
        }
    }
}

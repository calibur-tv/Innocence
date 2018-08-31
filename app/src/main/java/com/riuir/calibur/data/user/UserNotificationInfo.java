package com.riuir.calibur.data.user;

import java.util.List;

public class UserNotificationInfo {
    private int code;
    private UserNotificationInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserNotificationInfoData getData() {
        return data;
    }

    public void setData(UserNotificationInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserNotificationInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class UserNotificationInfoData{
        private List<UserNotificationInfoList> list;
        private int total;
        private boolean noMore;

        public List<UserNotificationInfoList> getList() {
            return list;
        }

        public void setList(List<UserNotificationInfoList> list) {
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
            return "UserNotificationInfoData{" +
                    "list=" + list +
                    ", total=" + total +
                    ", noMore=" + noMore +
                    '}';
        }
    }

    public class UserNotificationInfoList{
        private int id;
        private boolean checked;
        private int type;
        private UserNotificationInfoUser user;
        private String message;
        private UserNotificationInfoModel model;
        private String created_at;
        private String link;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public UserNotificationInfoUser getUser() {
            return user;
        }

        public void setUser(UserNotificationInfoUser user) {
            this.user = user;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public UserNotificationInfoModel getModel() {
            return model;
        }

        public void setModel(UserNotificationInfoModel model) {
            this.model = model;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @Override
        public String toString() {
            return "UserNotificationInfoList{" +
                    "id=" + id +
                    ", checked=" + checked +
                    ", type=" + type +
                    ", user=" + user +
                    ", message='" + message + '\'' +
                    ", model=" + model +
                    ", created_at='" + created_at + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }

    public class UserNotificationInfoUser{
        private int id;
        private String zone;
        private String avatar;
        private String nickname;

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

        @Override
        public String toString() {
            return "UserNotificationInfoUser{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }
    }
    public class UserNotificationInfoModel{
        private int id;
        private String title;

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

        @Override
        public String toString() {
            return "UserNotificationInfoModel{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
}

package com.riuir.calibur.data.anime;

import java.util.List;

public class CartoonListInfo {
    private int code;
    private CartoonListInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CartoonListInfoData getData() {
        return data;
    }

    public void setData(CartoonListInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CartoonListInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class CartoonListInfoData{
        private List<CartoonListInfoList> list;
        private int total;
        private boolean noMore;

        public List<CartoonListInfoList> getList() {
            return list;
        }

        public void setList(List<CartoonListInfoList> list) {
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
            return "CartoonListInfoData{" +
                    "list=" + list +
                    ", total=" + total +
                    ", noMore=" + noMore +
                    '}';
        }
    }
    public class CartoonListInfoList{
        private int id;
        private int user_id;
        private int bangumi_id;
        private String name;
        private int part;
        private int image_count;
        private CartoonListInfoUser user;
        private CartoonListInfoSource source;
        private boolean is_creator;
        private String created_at;
        private String updated_at;

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

        public int getImage_count() {
            return image_count;
        }

        public void setImage_count(int image_count) {
            this.image_count = image_count;
        }

        public CartoonListInfoUser getUser() {
            return user;
        }

        public void setUser(CartoonListInfoUser user) {
            this.user = user;
        }

        public CartoonListInfoSource getSource() {
            return source;
        }

        public void setSource(CartoonListInfoSource source) {
            this.source = source;
        }

        public boolean isIs_creator() {
            return is_creator;
        }

        public void setIs_creator(boolean is_creator) {
            this.is_creator = is_creator;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        @Override
        public String toString() {
            return "CartoonListInfoList{" +
                    "id=" + id +
                    ", user_id=" + user_id +
                    ", bangumi_id=" + bangumi_id +
                    ", name='" + name + '\'' +
                    ", part=" + part +
                    ", image_count=" + image_count +
                    ", user=" + user +
                    ", source=" + source +
                    ", is_creator=" + is_creator +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    '}';
        }
    }
    public class CartoonListInfoUser{
        private int id;
        private String nickname;
        private String zone;
        private String avatar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        @Override
        public String toString() {
            return "CartoonListInfoUser{" +
                    "id=" + id +
                    ", nickname='" + nickname + '\'' +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }
    public class CartoonListInfoSource{
        private String url;
        private int width;
        private int height;
        private int size;
        private String type;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
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
            return "CartoonListInfoSource{" +
                    "url='" + url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}

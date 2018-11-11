package com.riuir.calibur.data.trending;

import java.io.Serializable;
import java.util.List;

public class ImageShowInfoPrimacy {
    private int code;
    private ImageShowInfoPrimacyData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ImageShowInfoPrimacyData getData() {
        return data;
    }

    public void setData(ImageShowInfoPrimacyData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ImageShowInfoPrimacy{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class ImageShowInfoPrimacyData implements Serializable {
        private int id;
        private int user_id;
        private int bangumi_id;
        private String name;
        private float part;
        private List<ImageShowInfoPrimacyParts> parts;
        private List<ImageShowInfoPrimacyImages> images;
        private int image_count;
        private ImageShowInfoPrimacyUser user;
        private ImageShowInfoPrimacyBangumi bangumi;
        private ImageShowInfoPrimacySource source;
        private boolean is_album;
        private boolean is_cartoon;
        private boolean is_creator;
        private boolean liked;
        private boolean marked;
        private boolean rewarded;
        private ImageShowInfoPrimacyLikeUser like_users;
        private ImageShowInfoPrimacyLikeUser reward_users;
        private ImageShowInfoPrimacyLikeUser mark_users;
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

        public float getPart() {
            return part;
        }

        public void setPart(int part) {
            this.part = part;
        }

        public List<ImageShowInfoPrimacyParts> getParts() {
            return parts;
        }

        public void setParts(List<ImageShowInfoPrimacyParts> parts) {
            this.parts = parts;
        }

        public List<ImageShowInfoPrimacyImages> getImages() {
            return images;
        }

        public void setImages(List<ImageShowInfoPrimacyImages> images) {
            this.images = images;
        }

        public int getImage_count() {
            return image_count;
        }

        public void setImage_count(int image_count) {
            this.image_count = image_count;
        }

        public ImageShowInfoPrimacyUser getUser() {
            return user;
        }

        public void setUser(ImageShowInfoPrimacyUser user) {
            this.user = user;
        }

        public ImageShowInfoPrimacyBangumi getBangumi() {
            return bangumi;
        }

        public void setBangumi(ImageShowInfoPrimacyBangumi bangumi) {
            this.bangumi = bangumi;
        }

        public ImageShowInfoPrimacySource getSource() {
            return source;
        }

        public void setSource(ImageShowInfoPrimacySource source) {
            this.source = source;
        }

        public boolean isIs_album() {
            return is_album;
        }

        public void setIs_album(boolean is_album) {
            this.is_album = is_album;
        }

        public boolean isIs_cartoon() {
            return is_cartoon;
        }

        public void setIs_cartoon(boolean is_cartoon) {
            this.is_cartoon = is_cartoon;
        }

        public boolean isIs_creator() {
            return is_creator;
        }

        public void setIs_creator(boolean is_creator) {
            this.is_creator = is_creator;
        }

        public boolean isLiked() {
            return liked;
        }

        public void setLiked(boolean liked) {
            this.liked = liked;
        }

        public boolean isMarked() {
            return marked;
        }

        public void setMarked(boolean marked) {
            this.marked = marked;
        }

        public boolean isRewarded() {
            return rewarded;
        }

        public void setRewarded(boolean rewarded) {
            this.rewarded = rewarded;
        }

        public ImageShowInfoPrimacyLikeUser getLike_users() {
            return like_users;
        }

        public void setLike_users(ImageShowInfoPrimacyLikeUser like_users) {
            this.like_users = like_users;
        }

        public ImageShowInfoPrimacyLikeUser getReward_users() {
            return reward_users;
        }

        public void setReward_users(ImageShowInfoPrimacyLikeUser reward_users) {
            this.reward_users = reward_users;
        }

        public ImageShowInfoPrimacyLikeUser getMark_users() {
            return mark_users;
        }

        public void setMark_users(ImageShowInfoPrimacyLikeUser mark_users) {
            this.mark_users = mark_users;
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
            return "ImageShowInfoPrimacyData{" +
                    "id=" + id +
                    ", user_id=" + user_id +
                    ", bangumi_id=" + bangumi_id +
                    ", name='" + name + '\'' +
                    ", part=" + part +
                    ", parts=" + parts +
                    ", images=" + images +
                    ", image_count=" + image_count +
                    ", user=" + user +
                    ", bangumi=" + bangumi +
                    ", source=" + source +
                    ", is_album=" + is_album +
                    ", is_cartoon=" + is_cartoon +
                    ", is_creator=" + is_creator +
                    ", liked=" + liked +
                    ", marked=" + marked +
                    ", rewarded=" + rewarded +
                    ", like_users=" + like_users +
                    ", reward_users=" + reward_users +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    '}';
        }
    }
    public class  ImageShowInfoPrimacyParts implements Serializable{
        private int id;
        private String name;
        private float part;

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

        public float getPart() {
            return part;
        }

        public void setPart(int part) {
            this.part = part;
        }

        @Override
        public String toString() {
            return "ImageShowInfoPrimacyParts{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", part=" + part +
                    '}';
        }
    }

    public class ImageShowInfoPrimacyImages implements Serializable{
        private int id;
        private String url;
        private int width;
        private int height;
        private int size;
        private String type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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
            return "ImageShowInfoPrimacyImages{" +
                    "id=" + id +
                    ", url='" + url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
    public class ImageShowInfoPrimacyUser implements Serializable{
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
            return "ImageShowInfoPrimacyUser{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }
    }

    public class ImageShowInfoPrimacyBangumi implements Serializable{
        private int id;
        private String name;
        private String avatar;
        private String summary;
        private boolean followed;
        private boolean is_master;

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

        public boolean isIs_master() {
            return is_master;
        }

        public void setIs_master(boolean is_master) {
            this.is_master = is_master;
        }

        @Override
        public String toString() {
            return "ImageShowInfoPrimacyBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", summary='" + summary + '\'' +
                    ", followed=" + followed +
                    ", is_master=" + is_master +
                    '}';
        }
    }
    public class ImageShowInfoPrimacySource implements Serializable{
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
            return "ImageShowInfoPrimacySource{" +
                    "url='" + url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
    public class ImageShowInfoPrimacyLikeUser implements Serializable{
        private int total;
        private boolean noMore;
        private List<ImageShowInfoPrimacyLikeUserList> list;

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

        public List<ImageShowInfoPrimacyLikeUserList> getList() {
            return list;
        }

        public void setList(List<ImageShowInfoPrimacyLikeUserList> list) {
            this.list = list;
        }

        @Override
        public String toString() {
            return "ImageShowInfoPrimacyLikeUser{" +
                    "total=" + total +
                    ", noMore=" + noMore +
                    ", list=" + list +
                    '}';
        }
    }

    public class ImageShowInfoPrimacyLikeUserList implements Serializable{
        private int id;
        private String zone;
        private String avatar;
        private String nickname;
        private String created_at;

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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        @Override
        public String toString() {
            return "ImageShowInfoPrimacyLikeUser{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", created_at='" + created_at + '\'' +
                    '}';
        }
    }
}

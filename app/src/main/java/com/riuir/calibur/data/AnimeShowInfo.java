package com.riuir.calibur.data;

import java.util.List;

public class AnimeShowInfo {
    private int code;
    private AnimeShowInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AnimeShowInfoData getData() {
        return data;
    }

    public void setData(AnimeShowInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AnimeShowInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class AnimeShowInfoData{
        private int id;
        private String name;
        private String avatar;
        private String banner;
        private String summary;
        private double score;
        private int count_score;
        private int count_like;
        private String alias;
        private boolean followed;
        private boolean scored;
        private boolean is_master;
        private AnimeInfoManagersUser manager_users;
        private List<AnimeShowInfoTags> tags;
        private AnimeShowInfoUsers follow_users;
        private boolean has_video;
        private boolean has_cartoon;

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

        public String getBanner() {
            return banner;
        }

        public void setBanner(String banner) {
            this.banner = banner;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getCount_score() {
            return count_score;
        }

        public void setCount_score(int count_score) {
            this.count_score = count_score;
        }

        public int getCount_like() {
            return count_like;
        }

        public void setCount_like(int count_like) {
            this.count_like = count_like;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public boolean isFollowed() {
            return followed;
        }

        public void setFollowed(boolean followed) {
            this.followed = followed;
        }

        public boolean isScored() {
            return scored;
        }

        public void setScored(boolean scored) {
            this.scored = scored;
        }

        public boolean isIs_master() {
            return is_master;
        }

        public void setIs_master(boolean is_master) {
            this.is_master = is_master;
        }

        public AnimeInfoManagersUser getManager_users() {
            return manager_users;
        }

        public void setManager_users(AnimeInfoManagersUser manager_users) {
            this.manager_users = manager_users;
        }

        public AnimeShowInfoUsers getFollow_users() {
            return follow_users;
        }

        public void setFollow_users(AnimeShowInfoUsers follow_users) {
            this.follow_users = follow_users;
        }

        public List<AnimeShowInfoTags> getTags() {
            return tags;
        }

        public void setTags(List<AnimeShowInfoTags> tags) {
            this.tags = tags;
        }



        public boolean isHas_video() {
            return has_video;
        }

        public void setHas_video(boolean has_video) {
            this.has_video = has_video;
        }

        public boolean isHas_cartoon() {
            return has_cartoon;
        }

        public void setHas_cartoon(boolean has_cartoon) {
            this.has_cartoon = has_cartoon;
        }

        @Override
        public String toString() {
            return "AnimeShowInfoData{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", banner='" + banner + '\'' +
                    ", summary='" + summary + '\'' +
                    ", score=" + score +
                    ", count_score=" + count_score +
                    ", count_like=" + count_like +
                    ", alias='" + alias + '\'' +
                    ", followed=" + followed +
                    ", scored=" + scored +
                    ", is_master=" + is_master +
                    ", manager_users=" + manager_users +
                    ", tags=" + tags +
                    ", follow_users=" + follow_users +
                    ", has_video=" + has_video +
                    ", has_cartoon=" + has_cartoon +
                    '}';
        }
    }

    public class AnimeInfoManagers{
        private int is_leader;
        private String created_at;
        private AnimeInfoManagersUser user;
    }

    public class AnimeInfoManagersUser{
        private int total;
        private boolean noMore;
        private List<AnimeInfoManagersUserList> list;
    }

    public class AnimeInfoManagersUserList{
        private int is_leader;
        private String created_at;
        private AnimeInfoManagersUserListUser user;
    }

    public class AnimeInfoManagersUserListUser{
        private int id;
        private String zone;
        private String avatar;
        private String nickname;
    }

    public class AnimeShowInfoTags{
        private int id;
        private String name;

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

        @Override
        public String toString() {
            return "AnimeShowInfoTags{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public class AnimeShowInfoUsers{
        private int total;
        private boolean noMore;
        private List<AnimeShowInfoUserList> list;

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

        public List<AnimeShowInfoUserList> getList() {
            return list;
        }

        public void setList(List<AnimeShowInfoUserList> list) {
            this.list = list;
        }

        @Override
        public String toString() {
            return "AnimeShowInfoUsers{" +
                    "total=" + total +
                    ", noMore=" + noMore +
                    ", list=" + list +
                    '}';
        }
    }

    public class AnimeShowInfoUserList{
        private int id;
        private String zone;
        private String avatar;
        private String nickname;
        private long created_at;

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

        public long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(long created_at) {
            this.created_at = created_at;
        }

        @Override
        public String toString() {
            return "AnimeShowInfoFollowers{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", created_at=" + created_at +
                    '}';
        }
    }
}

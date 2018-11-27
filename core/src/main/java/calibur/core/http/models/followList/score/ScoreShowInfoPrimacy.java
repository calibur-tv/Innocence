package calibur.core.http.models.followList.score;

import java.util.List;

public class ScoreShowInfoPrimacy {
    private int id;
    private ScoreShowInfoPrimacyUser user;
    private String title;
    private ScoreShowInfoPrimacyBangumi bangumi;
    private String total;
    private String lol;
    private String cry;
    private String fight;
    private String moe;
    private String sound;
    private String vision;
    private String role;
    private String story;
    private String express;
    private String style;

    private String intro;
    private List<ScoreShowInfoPrimacyContent> content;
    private boolean is_creator;
    private int commented;
    private int comment_count;
    private boolean liked;
    private ScoreShowInfoPrimacyLikeUser like_users;
    private boolean rewarded;
    private ScoreShowInfoPrimacyLikeUser reward_users;
    private boolean marked;
    private ScoreShowInfoPrimacyLikeUser mark_users;
    private String created_at;
    private String updated_at;
    private String published_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ScoreShowInfoPrimacyUser getUser() {
        return user;
    }

    public void setUser(ScoreShowInfoPrimacyUser user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ScoreShowInfoPrimacyBangumi getBangumi() {
        return bangumi;
    }

    public void setBangumi(ScoreShowInfoPrimacyBangumi bangumi) {
        this.bangumi = bangumi;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getLol() {
        return lol;
    }

    public void setLol(String lol) {
        this.lol = lol;
    }

    public String getCry() {
        return cry;
    }

    public void setCry(String cry) {
        this.cry = cry;
    }

    public String getFight() {
        return fight;
    }

    public void setFight(String fight) {
        this.fight = fight;
    }

    public String getMoe() {
        return moe;
    }

    public void setMoe(String moe) {
        this.moe = moe;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<ScoreShowInfoPrimacyContent> getContent() {
        return content;
    }

    public void setContent(List<ScoreShowInfoPrimacyContent> content) {
        this.content = content;
    }

    public boolean isIs_creator() {
        return is_creator;
    }

    public void setIs_creator(boolean is_creator) {
        this.is_creator = is_creator;
    }

    public int getCommented() {
        return commented;
    }

    public void setCommented(int commented) {
        this.commented = commented;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setLike_users(ScoreShowInfoPrimacyLikeUser like_users) {
        this.like_users = like_users;
    }

    public ScoreShowInfoPrimacyLikeUser getLike_users() {
        return like_users;
    }

    public void setMark_users(ScoreShowInfoPrimacyLikeUser mark_users) {
        this.mark_users = mark_users;
    }

    public ScoreShowInfoPrimacyLikeUser getMark_users() {
        return mark_users;
    }

    public void setReward_users(ScoreShowInfoPrimacyLikeUser reward_users) {
        this.reward_users = reward_users;
    }

    public ScoreShowInfoPrimacyLikeUser getReward_users() {
        return reward_users;
    }


    public boolean isRewarded() {
        return rewarded;
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }


    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
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

    public String getPublished_at() {
        return published_at;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }

    @Override
    public String toString() {
        return "ScoreShowInfoPrimacyData{" +
                "id=" + id +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", bangumi=" + bangumi +
                ", total='" + total + '\'' +
                ", lol='" + lol + '\'' +
                ", cry='" + cry + '\'' +
                ", fight='" + fight + '\'' +
                ", moe='" + moe + '\'' +
                ", sound='" + sound + '\'' +
                ", vision='" + vision + '\'' +
                ", role='" + role + '\'' +
                ", story='" + story + '\'' +
                ", express='" + express + '\'' +
                ", style='" + style + '\'' +
                ", intro='" + intro + '\'' +
                ", content=" + content +
                ", is_creator=" + is_creator +
                ", commented=" + commented +
                ", comment_count=" + comment_count +
                ", liked=" + liked +
                ", like_users=" + like_users +
                ", rewarded=" + rewarded +
                ", reward_users=" + reward_users +
                ", marked=" + marked +
                ", mark_users=" + mark_users +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", published_at='" + published_at + '\'' +
                '}';
    }

    public class ScoreShowInfoPrimacyUser {
        private int id;
        private String zone;
        private String nickname;
        private String avatar;

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

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        @Override
        public String toString() {
            return "ScoreShowInfoPrimacyUser{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }

    public class ScoreShowInfoPrimacyBangumi {
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
            return "ScoreShowInfoPrimacyBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", summary='" + summary + '\'' +
                    ", followed=" + followed +
                    ", is_master=" + is_master +
                    '}';
        }
    }

    public class  ScoreShowInfoPrimacyContent {
        private String type;
        private String url;
        private int width;
        private int height;
        private String mime;
        private String text;
        private int id;
        private int size;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return "ScoreShowInfoPrimacyContent{" +
                    "type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", mime='" + mime + '\'' +
                    ", text='" + text + '\'' +
                    ", id=" + id +
                    ", size=" + size +
                    '}';
        }
    }
    public class ScoreShowInfoPrimacyLikeUser{
        private List<ScoreShowInfoPrimacyLikeUserList> list;
        private int total;
        private boolean noMore;


        public List<ScoreShowInfoPrimacyLikeUserList> getList() {
            return list;
        }

        public void setList(List<ScoreShowInfoPrimacyLikeUserList> list) {
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
            return "ScoreShowInfoPrimacyLikeUser{" +
                    "list=" + list +
                    ", total=" + total +
                    ", noMore=" + noMore +
                    '}';
        }
    }
    public class ScoreShowInfoPrimacyLikeUserList {
        private int id;
        private String zone;
        private String avatar;
        private String nickname;
        private Long created_at;

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

        public Long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Long created_at) {
            this.created_at = created_at;
        }

        @Override
        public String toString() {
            return "ScoreShowInfoPrimacyLikeUser{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", created_at=" + created_at +
                    '}';
        }
    }
}

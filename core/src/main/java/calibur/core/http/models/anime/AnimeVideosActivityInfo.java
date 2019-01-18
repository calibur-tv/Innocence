package calibur.core.http.models.anime;

import java.util.Arrays;

import calibur.core.http.models.followList.post.CardShowInfoPrimacy;
import calibur.core.http.models.share.ShareDataModel;

public class AnimeVideosActivityInfo {
    private AnimeVideosActivityInfoInfo info;
    private AnimeVideosActivityInfoBangumi bangumi;
    private AnimeVideosActivityInfoSeason season;
    private AnimeShowVideosInfo list;
    private boolean ip_blocked;
    private boolean must_reward;
    private int need_min_level;
    private ShareDataModel share_data;

    public AnimeVideosActivityInfoInfo getInfo() {
        return info;
    }

    public void setInfo(AnimeVideosActivityInfoInfo info) {
        this.info = info;
    }

    public AnimeVideosActivityInfoBangumi getBangumi() {
        return bangumi;
    }

    public void setBangumi(AnimeVideosActivityInfoBangumi bangumi) {
        this.bangumi = bangumi;
    }

    public AnimeVideosActivityInfoSeason getSeason() {
        return season;
    }

    public void setSeason(AnimeVideosActivityInfoSeason season) {
        this.season = season;
    }

    public AnimeShowVideosInfo getList() {
        return list;
    }

    public void setList(AnimeShowVideosInfo list) {
        this.list = list;
    }

    public boolean isIp_blocked() {
        return ip_blocked;
    }

    public void setIp_blocked(boolean ip_blocked) {
        this.ip_blocked = ip_blocked;
    }

    public boolean isMust_reward() {
        return must_reward;
    }

    public void setMust_reward(boolean must_reward) {
        this.must_reward = must_reward;
    }

    public int getNeed_min_level() {
        return need_min_level;
    }

    public void setNeed_min_level(int need_min_level) {
        this.need_min_level = need_min_level;
    }

    public ShareDataModel getShare_data() {
        return share_data;
    }

    public void setShare_data(ShareDataModel share_data) {
        this.share_data = share_data;
    }

    @Override
    public String toString() {
        return "AnimeVideosActivityInfoData{" +
                "info=" + info +
                ", bangumi=" + bangumi +
                ", season=" + season +
                ", list=" + list +
                ", ip_blocked=" + ip_blocked +
                ", must_reward=" + must_reward +
                ", need_min_level=" + need_min_level +
                ", share_data=" + share_data +
                '}';
    }

    public class AnimeVideosActivityInfoInfo{
        private int id;
        private int user_id;
        private int bangumi_id;
        private String src;
        private String name;
        private int part;
        private String poster;
        private boolean other_site;
        private boolean is_creator;
        private boolean liked;
        private boolean marked;
        private boolean rewarded;

        private CardShowInfoPrimacy.CardShowInfoPrimacyLikeUser like_users;
        private CardShowInfoPrimacy.CardShowInfoPrimacyLikeUser reward_users;
        private CardShowInfoPrimacy.CardShowInfoPrimacyLikeUser mark_users;

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

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
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

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public boolean isOther_site() {
            return other_site;
        }

        public void setOther_site(boolean other_site) {
            this.other_site = other_site;
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

        public CardShowInfoPrimacy.CardShowInfoPrimacyLikeUser getLike_users() {
            return like_users;
        }

        public void setLike_users(CardShowInfoPrimacy.CardShowInfoPrimacyLikeUser like_users) {
            this.like_users = like_users;
        }

        public CardShowInfoPrimacy.CardShowInfoPrimacyLikeUser getReward_users() {
            return reward_users;
        }

        public void setReward_users(CardShowInfoPrimacy.CardShowInfoPrimacyLikeUser reward_users) {
            this.reward_users = reward_users;
        }

        public CardShowInfoPrimacy.CardShowInfoPrimacyLikeUser getMark_users() {
            return mark_users;
        }

        public void setMark_users(CardShowInfoPrimacy.CardShowInfoPrimacyLikeUser mark_users) {
            this.mark_users = mark_users;
        }

        @Override
        public String toString() {
            return "AnimeVideosActivityInfoInfo{" +
                    "id=" + id +
                    ", user_id=" + user_id +
                    ", bangumi_id=" + bangumi_id +
                    ", src='" + src + '\'' +
                    ", name='" + name + '\'' +
                    ", part=" + part +
                    ", poster='" + poster + '\'' +
                    ", other_site=" + other_site +
                    ", is_creator=" + is_creator +
                    ", liked=" + liked +
                    ", marked=" + marked +
                    ", rewarded=" + rewarded +
                    ", like_users=" + like_users +
                    ", reward_users=" + reward_users +
                    ", mark_users=" + mark_users +
                    '}';
        }
    }

    public class AnimeVideosActivityInfoBangumi{
        private int id;
        private String name;
        private String avatar;
        private String summary;
        private boolean others_site_video;
        private boolean followed;

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

        @Override
        public String toString() {
            return "AnimeVideosActivityInfoBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", summary='" + summary + '\'' +
                    ", followed=" + followed +
                    '}';
        }
    }
    public class AnimeVideosActivityInfoSeason{
        private String[] name;
        private int[] part;
        private String[] time;
//        private int[] re;

        public String[] getName() {
            return name;
        }

        public void setName(String[] name) {
            this.name = name;
        }

        public int[] getPart() {
            return part;
        }

        public void setPart(int[] part) {
            this.part = part;
        }

        public String[] getTime() {
            return time;
        }

        public void setTime(String[] time) {
            this.time = time;
        }



        @Override
        public String toString() {
            return "AnimeVideosActivityInfoSeason{" +
                    "name=" + Arrays.toString(name) +
                    ", part=" + Arrays.toString(part) +
                    ", time=" + Arrays.toString(time) +
//                    ", re=" + re +
                    '}';
        }
    }
}

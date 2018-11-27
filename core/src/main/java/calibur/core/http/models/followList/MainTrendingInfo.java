package calibur.core.http.models.followList;

import java.util.List;

import calibur.core.http.models.anime.AnimeShowInfo;

public class MainTrendingInfo {
    private List<MainTrendingInfoList> list;
    private int total;
    private boolean noMore;

    public List<MainTrendingInfoList> getList() {
        return list;
    }

    public void setList(List<MainTrendingInfoList> list) {
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
        return "MainTrendingInfoData{" +
                "list=" + list +
                ", total=" + total +
                ", noMore=" + noMore +
                '}';
    }

    public class MainTrendingInfoList{

        //公用字段
        private int id;
        private String name;
        private String intro;
        private MainTrendingInfoUser user;
        private MainTrendingInfoBangumi bangumi;
        private String created_at;
        private String updated_at;
        private String title;
        private boolean liked;
        private boolean commented;
        private int like_count;
        private int comment_count;
        private boolean is_creator;

        //帖子字段
        private String desc;
        private List<MainTrendingImages> images;
        private int reward_count;
        private int mark_count;
        private boolean marked;
        private boolean is_nice;
        private String top_at;
        private List<AnimeShowInfo.AnimeShowInfoTags> tags;

        //图片需要字段
        private int user_id;
        private int bangumi_id;
        private int image_count;
        private MainImageInfoSource source;
        private boolean is_album;

        //漫评字段
        private int total;

        //偶像字段
        private String avatar;
        private int star_count;
        private int fans_count;
        private MainTrendingInfoUser lover;

        private int has_star;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public MainTrendingInfoUser getUser() {
            return user;
        }

        public void setUser(MainTrendingInfoUser user) {
            this.user = user;
        }

        public MainTrendingInfoBangumi getBangumi() {
            return bangumi;
        }

        public void setBangumi(MainTrendingInfoBangumi bangumi) {
            this.bangumi = bangumi;
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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public List<MainTrendingImages> getImages() {
            return images;
        }

        public void setImages(List<MainTrendingImages> images) {
            this.images = images;
        }

        public int getReward_count() {
            return reward_count;
        }

        public void setReward_count(int reward_count) {
            this.reward_count = reward_count;
        }

        public int getLike_count() {
            return like_count;
        }

        public void setLike_count(int like_count) {
            this.like_count = like_count;
        }

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        public int getMark_count() {
            return mark_count;
        }

        public void setMark_count(int mark_count) {
            this.mark_count = mark_count;
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

        public boolean isCommented() {
            return commented;
        }

        public void setCommented(boolean commented) {
            this.commented = commented;
        }

        public boolean isIs_nice() {
            return is_nice;
        }

        public void setIs_nice(boolean is_nice) {
            this.is_nice = is_nice;
        }

        public String getTop_at() {
            return top_at;
        }

        public void setTop_at(String top_at) {
            this.top_at = top_at;
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

        public int getImage_count() {
            return image_count;
        }

        public void setImage_count(int image_count) {
            this.image_count = image_count;
        }

        public MainImageInfoSource getSource() {
            return source;
        }

        public void setSource(MainImageInfoSource source) {
            this.source = source;
        }

        public boolean isIs_album() {
            return is_album;
        }

        public void setIs_album(boolean is_album) {
            this.is_album = is_album;
        }

        public boolean isIs_creator() {
            return is_creator;
        }

        public void setIs_creator(boolean is_creator) {
            this.is_creator = is_creator;
        }


        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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

        public MainTrendingInfoUser getLover() {
            return lover;
        }

        public void setLover(MainTrendingInfoUser lover) {
            this.lover = lover;
        }

        public int getHas_star() {
            return has_star;
        }

        public void setHas_star(int has_star) {
            this.has_star = has_star;
        }

        public List<AnimeShowInfo.AnimeShowInfoTags> getTags() {
            return tags;
        }

        public void setTags(List<AnimeShowInfo.AnimeShowInfoTags> tags) {
            this.tags = tags;
        }

        @Override
        public String toString() {
            return "MainTrendingInfoList{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", intro='" + intro + '\'' +
                    ", user=" + user +
                    ", bangumi=" + bangumi +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    ", title='" + title + '\'' +
                    ", liked=" + liked +
                    ", commented=" + commented +
                    ", like_count=" + like_count +
                    ", comment_count=" + comment_count +
                    ", is_creator=" + is_creator +
                    ", desc='" + desc + '\'' +
                    ", images=" + images +
                    ", reward_count=" + reward_count +
                    ", mark_count=" + mark_count +
                    ", marked=" + marked +
                    ", is_nice=" + is_nice +
                    ", top_at='" + top_at + '\'' +
                    ", tags=" + tags +
                    ", user_id=" + user_id +
                    ", bangumi_id=" + bangumi_id +
                    ", image_count=" + image_count +
                    ", source=" + source +
                    ", is_album=" + is_album +
                    ", total=" + total +
                    ", avatar='" + avatar + '\'' +
                    ", star_count=" + star_count +
                    ", fans_count=" + fans_count +
                    ", lover=" + lover +
                    ", has_star=" + has_star +
                    '}';
        }
    }

    public class  MainTrendingImages{
        private String url;
        private String width;
        private String height;
        private String size;
        private String type;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
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
            return "MainTrendingImages{" +
                    "url='" + url + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", size='" + size + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public class  MainTrendingInfoUser{
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
            return "MainTrendingInfoUser{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }

    public class MainTrendingInfoBangumi{
        private int id;
        private String name;
        private String avatar;

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

        @Override
        public String toString() {
            return "MainTrendingInfoBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }

    public class MainImageInfoSource{
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
            return "MainImageInfoSource{" +
                    "url='" + url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}

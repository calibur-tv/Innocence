package calibur.core.http.models.anime;

public class RoleShowInfo {
    private RoleShowInfoDataBangumi bangumi;
    private RoleShowInfoDataData data;

    public RoleShowInfoDataBangumi getBangumi() {
        return bangumi;
    }

    public void setBangumi(RoleShowInfoDataBangumi bangumi) {
        this.bangumi = bangumi;
    }

    public RoleShowInfoDataData getData() {
        return data;
    }

    public void setData(RoleShowInfoDataData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RoleShowInfoData{" +
                "bangumi=" + bangumi +
                ", data=" + data +
                '}';
    }

    public class RoleShowInfoDataBangumi{
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
            return "RoleShowInfoDataBangumi{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", summary='" + summary + '\'' +
                    ", followed=" + followed +
                    ", is_master=" + is_master +
                    '}';
        }
    }
    public class RoleShowInfoDataData{
        private int id;
        private String alias;
        private String avatar;
        private int fans_count;
        private int hasStar;
        private String intro;
        private RoleShowInfoDataLover lover;
        private String name;
        private int star_count;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getFans_count() {
            return fans_count;
        }

        public void setFans_count(int fans_count) {
            this.fans_count = fans_count;
        }

        public int getHasStar() {
            return hasStar;
        }

        public void setHasStar(int hasStar) {
            this.hasStar = hasStar;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getStar_count() {
            return star_count;
        }

        public void setStar_count(int star_count) {
            this.star_count = star_count;
        }

        public RoleShowInfoDataLover getLover() {
            return lover;
        }

        public void setLover(RoleShowInfoDataLover lover) {
            this.lover = lover;
        }

        @Override
        public String toString() {
            return "RoleShowInfoDataData{" +
                    "id=" + id +
                    ", alias='" + alias + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", fans_count=" + fans_count +
                    ", hasStar=" + hasStar +
                    ", intro='" + intro + '\'' +
                    ", lover=" + lover +
                    ", name='" + name + '\'' +
                    ", star_count=" + star_count +
                    '}';
        }
    }

    public class  RoleShowInfoDataLover{
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
            return "RoleShowInfoDataLover{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }
    }
}

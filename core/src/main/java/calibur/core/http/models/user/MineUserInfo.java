package calibur.core.http.models.user;

import java.io.Serializable;

public class MineUserInfo implements Serializable {
    private int id;
    private String zone;
    private String avatar;
    private String banner;
    private String nickname;
    private String birthday;
    private int sex;
    private boolean birthSecret;
    private boolean sexSecret;
    private String signature;
    private MineUserInfoUpToken upToken;
    private boolean daySign;
    private int coin;
    private int coin_from_sign;
    private boolean faker;
    private boolean is_admin;
    private int notification;
    private int power;
    private MineUserInfoExp exp;

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

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public boolean isBirthSecret() {
        return birthSecret;
    }

    public void setBirthSecret(boolean birthSecret) {
        this.birthSecret = birthSecret;
    }

    public boolean isSexSecret() {
        return sexSecret;
    }

    public void setSexSecret(boolean sexSecret) {
        this.sexSecret = sexSecret;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public MineUserInfoUpToken getUpToken() {
        return upToken;
    }

    public void setUpToken(MineUserInfoUpToken upToken) {
        this.upToken = upToken;
    }

    public boolean isDaySign() {
        return daySign;
    }

    public void setDaySign(boolean daySign) {
        this.daySign = daySign;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public boolean isFaker() {
        return faker;
    }

    public void setFaker(boolean faker) {
        this.faker = faker;
    }

    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getCoin_from_sign() {
        return coin_from_sign;
    }

    public void setCoin_from_sign(int coin_from_sign) {
        this.coin_from_sign = coin_from_sign;
    }

    public MineUserInfoExp getExp() {
        return exp;
    }

    public void setExp(MineUserInfoExp exp) {
        this.exp = exp;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "MinEUserInfoData{" +
                "id=" + id +
                ", zone='" + zone + '\'' +
                ", avatar='" + avatar + '\'' +
                ", banner='" + banner + '\'' +
                ", nickname='" + nickname + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sex=" + sex +
                ", birthSecret=" + birthSecret +
                ", sexSecret=" + sexSecret +
                ", signature='" + signature + '\'' +
                ", upToken=" + upToken +
                ", daySign=" + daySign +
                ", coin=" + coin +
                ", coin_from_sign=" + coin_from_sign +
                ", faker=" + faker +
                ", is_admin=" + is_admin +
                ", notification=" + notification +
                ", power=" + power +
                ", exp=" + exp +
                '}';
    }

    public class MineUserInfoUpToken implements Serializable{
        private String upToken;
        private long expiredAt;

        public String getUpToken() {
            return upToken;
        }

        public void setUpToken(String upToken) {
            this.upToken = upToken;
        }

        public long getExpiredAt() {
            return expiredAt;
        }

        public void setExpiredAt(long expiredAt) {
            this.expiredAt = expiredAt;
        }

        @Override
        public String toString() {
            return "MinEUserInfoUpToken{" +
                    "upToken='" + upToken + '\'' +
                    ", expiredAt=" + expiredAt +
                    '}';
        }
    }

    public class MineUserInfoExp implements Serializable{
        private int level;
        private int next_level_exp;
        private int have_exp;

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getNext_level_exp() {
            return next_level_exp;
        }

        public void setNext_level_exp(int next_level_exp) {
            this.next_level_exp = next_level_exp;
        }

        public int getHave_exp() {
            return have_exp;
        }

        public void setHave_exp(int have_exp) {
            this.have_exp = have_exp;
        }

        @Override
        public String toString() {
            return "MineUserInfoExp{" +
                    "level=" + level +
                    ", next_level_exp=" + next_level_exp +
                    ", have_exp=" + have_exp +
                    '}';
        }
    }
}

package com.riuir.calibur.data;

import java.io.Serializable;

public class MineUserInfo {
    private int code;
    private MinEUserInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public MinEUserInfoData getData() {
        return data;
    }

    public void setData(MinEUserInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MineUserInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class  MinEUserInfoData implements Serializable{
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
        private MinEUserInfoUpToken upToken;
        private boolean daySign;
        private int coin;
        private boolean faker;
        private boolean is_admin;
        private int notification;

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

        public MinEUserInfoUpToken getUpToken() {
            return upToken;
        }

        public void setUpToken(MinEUserInfoUpToken upToken) {
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
                    ", faker=" + faker +
                    ", is_admin=" + is_admin +
                    ", notification=" + notification +
                    '}';
        }
    }

    public class MinEUserInfoUpToken implements Serializable{
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
}

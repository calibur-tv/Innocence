package com.riuir.calibur.data.user;

public class UserMainInfo {
    private int code;
    private UserMainInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserMainInfoData getData() {
        return data;
    }

    public void setData(UserMainInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserMainInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class UserMainInfoData{
        private int id;
        private String zone;
        private String avatar;
        private String banner;
        private String nickname;
        private String signature;
        private int level;
        private int power;
        private String sex;
        private boolean sexSecret;
        private boolean faker;

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

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public boolean isFaker() {
            return faker;
        }

        public void setFaker(boolean faker) {
            this.faker = faker;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public boolean isSexSecret() {
            return sexSecret;
        }

        public void setSexSecret(boolean sexSecret) {
            this.sexSecret = sexSecret;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        @Override
        public String toString() {
            return "UserMainInfoData{" +
                    "id=" + id +
                    ", zone='" + zone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", banner='" + banner + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", signature='" + signature + '\'' +
                    ", level=" + level +
                    ", power=" + power +
                    ", sex='" + sex + '\'' +
                    ", sexSecret=" + sexSecret +
                    ", faker=" + faker +
                    '}';
        }
    }


}

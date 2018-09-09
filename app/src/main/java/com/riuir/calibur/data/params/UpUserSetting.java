package com.riuir.calibur.data.params;

public class UpUserSetting {
    private int sex;
    private String nickname;
    private String signature;
    private long birthday;
    private boolean birth_secret;
    private boolean sex_secret;

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public boolean isBirth_secret() {
        return birth_secret;
    }

    public void setBirth_secret(boolean birth_secret) {
        this.birth_secret = birth_secret;
    }

    public boolean isSex_secret() {
        return sex_secret;
    }

    public void setSex_secret(boolean sex_secret) {
        this.sex_secret = sex_secret;
    }
}

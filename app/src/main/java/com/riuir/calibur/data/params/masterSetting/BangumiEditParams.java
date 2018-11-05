package com.riuir.calibur.data.params.masterSetting;

import java.util.List;

public class BangumiEditParams {
    private String avatar;
    private String banner;
    private String summary;
    private List<Integer> tags;

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

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "BangumiEditParams{" +
                "avatar='" + avatar + '\'' +
                ", banner='" + banner + '\'' +
                ", summary='" + summary + '\'' +
                ", tags=" + tags +
                '}';
    }
}

package com.riuir.calibur.data.params.newPost;

import com.riuir.calibur.data.params.QiniuImageParams;
import com.riuir.calibur.data.params.VerificationCodeBody;

import java.util.ArrayList;
import java.util.List;

public class CreatePostParams {
    private int bangumiId;
    private String title;
    private String content;
    private ArrayList<QiniuImageParams.QiniuImageParamsData> images;
    private boolean is_creator;
    private VerificationCodeBody.VerificationCodeBodyGeeTest geetest;
    private List<Integer> tags;

    public int getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(int bangumiId) {
        this.bangumiId = bangumiId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<QiniuImageParams.QiniuImageParamsData> getImages() {
        return images;
    }

    public void setImages(ArrayList<QiniuImageParams.QiniuImageParamsData> images) {
        this.images = images;
    }

    public boolean isIs_creator() {
        return is_creator;
    }

    public void setIs_creator(boolean is_creator) {
        this.is_creator = is_creator;
    }

    public VerificationCodeBody.VerificationCodeBodyGeeTest getGeetest() {
        return geetest;
    }

    public void setGeetest(VerificationCodeBody.VerificationCodeBodyGeeTest geetest) {
        this.geetest = geetest;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "CreatePostParams{" +
                "bangumiId=" + bangumiId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", images=" + images +
                ", is_creator=" + is_creator +
                ", geetest=" + geetest +
                ", tags=" + tags +
                '}';
    }
}

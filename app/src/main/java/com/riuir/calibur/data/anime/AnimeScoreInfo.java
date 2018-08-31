package com.riuir.calibur.data.anime;

import java.util.List;

public class AnimeScoreInfo {
    private int code;
    private AnimeScoreInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AnimeScoreInfoData getData() {
        return data;
    }

    public void setData(AnimeScoreInfoData data) {
        this.data = data;
    }

    public class AnimeScoreInfoData{
        private String total;
        private int count;
        private AnimeScoreInfoRadar radar;
        private List<AnimeScoreInfoLadder> ladder;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public AnimeScoreInfoRadar getRadar() {
            return radar;
        }

        public void setRadar(AnimeScoreInfoRadar radar) {
            this.radar = radar;
        }

        public List<AnimeScoreInfoLadder> getLadder() {
            return ladder;
        }

        public void setLadder(List<AnimeScoreInfoLadder> ladder) {
            this.ladder = ladder;
        }
    }
    public class AnimeScoreInfoRadar{
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
    }
    public class AnimeScoreInfoLadder{
        private int key;
        private int val;

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }
    }
}

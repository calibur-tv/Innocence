package com.riuir.calibur.data.anime;

import java.util.ArrayList;
import java.util.List;

public class BangumiAllList {
    private int code;
    private ArrayList<BangumiAllListData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<BangumiAllListData> getData() {
        return data;
    }

    public void setData(ArrayList<BangumiAllListData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BangumiAllList{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class BangumiAllListData{
        private int id;
        private String name;
        private String avatar;
        private String alias;

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

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        @Override
        public String toString() {
            return "BangumiAllListData{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", alias='" + alias + '\'' +
                    '}';
        }
    }
}

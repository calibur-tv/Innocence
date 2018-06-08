package com.riuir.calibur.data;

import java.util.List;

public class AnimeListForTagsSearch {
    private int code;
    private AnimeListForTagsSearchList data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public AnimeListForTagsSearchList getData() {
        return data;
    }

    public void setData(AnimeListForTagsSearchList data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AnimeListForTagsSearch{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class  AnimeListForTagsSearchList{
        private int total;
        private List<AnimeListForTagsSearchData> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<AnimeListForTagsSearchData> getList() {
            return list;
        }

        public void setList(List<AnimeListForTagsSearchData> list) {
            this.list = list;
        }

        @Override
        public String toString() {
            return "AnimeListForTagsSearchList{" +
                    "total=" + total +
                    ", list=" + list +
                    '}';
        }
    }
    public class AnimeListForTagsSearchData{
        private int id;
        private String name;
        private String avatar;
        private String summary;

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

        @Override
        public String toString() {
            return "AnimeListForTagsSearchData{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", summary='" + summary + '\'' +
                    '}';
        }
    }
}

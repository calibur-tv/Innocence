package com.riuir.calibur.data.params;

import java.util.List;

public class DramaTags {
    private int code;
    private List<DramaTagsData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DramaTagsData> getData() {
        return data;
    }

    public void setData(List<DramaTagsData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DramaTags{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class DramaTagsData{
        private int id;
        private String name;

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

        @Override
        public String toString() {
            return "DramaTagsData{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}

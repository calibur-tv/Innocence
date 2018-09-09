package com.riuir.calibur.data.params;

import java.util.ArrayList;

public class CreateMainComment {
    private String content;
    private ArrayList<QiniuImageParams.QiniuImageParamsData> images;
    private String type;
    private int id;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CreateMainComment{" +
                "content='" + content + '\'' +
                ", images=" + images +
                ", type='" + type + '\'' +
                ", id=" + id +
                '}';
    }

    public class CreateMainCommentImage{
        private String url;
        private String type;
        private int width;
        private int height;
        private int size;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return "CreateMainCommentImg{" +
                    "url='" + url + '\'' +
                    ", type='" + type + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    '}';
        }
    }

}

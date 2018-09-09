package com.riuir.calibur.data.params;

public class QiniuImageParams {

    private int code;
    private QiniuImageParamsData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public QiniuImageParamsData getData() {
        return data;
    }

    public void setData(QiniuImageParamsData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QiniuImageParams{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class QiniuImageParamsData{
        private int height;
        private String type;
        private String url;
        private int size;
        private int width;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public String toString() {
            return "QiniuImageParamsData{" +
                    "height=" + height +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    ", size='" + size + '\'' +
                    ", width=" + width +
                    '}';
        }

    }
}

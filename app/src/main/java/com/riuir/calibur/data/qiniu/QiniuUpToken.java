package com.riuir.calibur.data.qiniu;

public class QiniuUpToken {
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public QiniuUpTokenData getData() {
        return data;
    }

    public void setData(QiniuUpTokenData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QiniuUpToken{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    private QiniuUpTokenData data;
    public class QiniuUpTokenData{
        private String upToken;
        private String expiredAt;

        public String getUpToken() {
            return upToken;
        }

        public void setUpToken(String upToken) {
            this.upToken = upToken;
        }

        public String getExpiredAt() {
            return expiredAt;
        }

        public void setExpiredAt(String expiredAt) {
            this.expiredAt = expiredAt;
        }

        @Override
        public String toString() {
            return "QiniuUpTokenData{" +
                    "upToken='" + upToken + '\'' +
                    ", expiredAt='" + expiredAt + '\'' +
                    '}';
        }
    }
}

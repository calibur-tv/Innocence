package calibur.core.http.models.qiniu;

public class QiniuUpToken {
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

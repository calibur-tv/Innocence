package calibur.core.http.models.geetest;

public class GeeTestInfo {
    private int success;
    private String gt;
    private String challenge;
    private String payload;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getGt() {
        return gt;
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }


    @Override
    public String toString() {
        return "GeeTest{" +
                "success=" + success +
                ", gt='" + gt + '\'' +
                ", challenge='" + challenge + '\'' +
                ", payload=" + payload +
                '}';
    }
}

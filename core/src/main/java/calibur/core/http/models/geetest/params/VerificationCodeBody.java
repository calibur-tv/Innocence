package calibur.core.http.models.geetest.params;

public class VerificationCodeBody {
    private String type;
    private String phone_number;
    private VerificationCodeBodyGeeTest geetest;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public VerificationCodeBodyGeeTest getGeetest() {
        return geetest;
    }

    public void setGeetest(VerificationCodeBodyGeeTest geetest) {
        this.geetest = geetest;
    }

    @Override
    public String toString() {
        return "VerificationCodeBody{" +
                "type='" + type + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", geetest=" + geetest +
                '}';
    }

    public static class VerificationCodeBodyGeeTest{
        private String geetest_challenge;
        private String geetest_validate;
        private String geetest_seccode;
        private String payload;
        private int success;

        public String getGeetest_challenge() {
            return geetest_challenge;
        }

        public void setGeetest_challenge(String geetest_challenge) {
            this.geetest_challenge = geetest_challenge;
        }

        public String getGeetest_validate() {
            return geetest_validate;
        }

        public void setGeetest_validate(String geetest_validate) {
            this.geetest_validate = geetest_validate;
        }

        public String getGeetest_seccode() {
            return geetest_seccode;
        }

        public void setGeetest_seccode(String geetest_seccode) {
            this.geetest_seccode = geetest_seccode;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        @Override
        public String toString() {
            return "VerificationCodeBodyGeeTest{" +
                    "geetest_challenge='" + geetest_challenge + '\'' +
                    ", geetest_validate='" + geetest_validate + '\'' +
                    ", geetest_seccode='" + geetest_seccode + '\'' +
                    ", payload='" + payload + '\'' +
                    ", success=" + success +
                    '}';
        }
    }
}

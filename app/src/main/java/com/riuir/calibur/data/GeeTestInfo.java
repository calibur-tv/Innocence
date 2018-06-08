package com.riuir.calibur.data;

import org.json.JSONObject;

public class GeeTestInfo {
    private int code;
    private GeeTest data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public GeeTest getData() {
        return data;
    }

    public void setData(GeeTest data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GeeTestInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class GeeTest extends JSONObject{
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
}

package com.riuir.calibur.data.user;

public class UserDaySign {
    private int code;
    private UserDaySignData data;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserDaySignData getData() {
        return data;
    }

    public void setData(UserDaySignData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserDaySign{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class UserDaySignData{
        private int exp;
        private String message;

        public int getExp() {
            return exp;
        }

        public void setExp(int exp) {
            this.exp = exp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "UserDaySignData{" +
                    "exp=" + exp +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

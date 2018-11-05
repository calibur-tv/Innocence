package com.riuir.calibur.data.create;

public class CreateCard {
    private int code;
    private CreateCardData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CreateCardData getData() {
        return data;
    }

    public void setData(CreateCardData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CreateCard{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class CreateCardData{
        private int data;
        private int exp;
        private String message;

        public int getData() {
            return data;
        }

        public void setData(int data) {
            this.data = data;
        }

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
            return "CreateCardData{" +
                    "data=" + data +
                    ", exp=" + exp +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

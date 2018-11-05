package com.riuir.calibur.data.create;

public class DeleteInfo {
    private int code;
    private DeleteInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DeleteInfoData getData() {
        return data;
    }

    public void setData(DeleteInfoData data) {
        this.data = data;
    }

    public class DeleteInfoData{
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
    }
}

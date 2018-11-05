package com.riuir.calibur.data.album;

public class CreateNewAlbumInfo {
    private int code;
    private CreateNewAlbumInfoData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CreateNewAlbumInfoData getData() {
        return data;
    }

    public void setData(CreateNewAlbumInfoData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CreateNewAlbumInfo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
    public class CreateNewAlbumInfoData{
        private ChooseImageAlbum.ChooseImageAlbumData data;
        private int exp;
        private String message;

        public ChooseImageAlbum.ChooseImageAlbumData getData() {
            return data;
        }

        public void setData(ChooseImageAlbum.ChooseImageAlbumData data) {
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
            return "CreateNewAlbumInfoData{" +
                    "data=" + data +
                    ", exp=" + exp +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}

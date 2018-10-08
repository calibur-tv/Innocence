package com.riuir.calibur.data.check;

public class AppVersionCheck {
    private int code;
    private AppVersionCheckData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AppVersionCheckData getData() {
        return data;
    }

    public void setData(AppVersionCheckData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AppVersionCheck{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class AppVersionCheckData{
        private String latest_version;
        private boolean force_update;
        private String download_url;

        public String getLatest_version() {
            return latest_version;
        }

        public void setLatest_version(String latest_version) {
            this.latest_version = latest_version;
        }

        public boolean isForce_update() {
            return force_update;
        }

        public void setForce_update(boolean force_update) {
            this.force_update = force_update;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }

        @Override
        public String toString() {
            return "AppVersionCheckData{" +
                    "latest_version='" + latest_version + '\'' +
                    ", force_update=" + force_update +
                    ", download_url='" + download_url + '\'' +
                    '}';
        }
    }
}

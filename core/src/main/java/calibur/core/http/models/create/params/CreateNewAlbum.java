package calibur.core.http.models.create.params;

import calibur.core.http.models.geetest.params.VerificationCodeBody;

public class CreateNewAlbum {
    private int bangumi_id;
    private String name;
    private boolean is_cartoon;
    private boolean is_creator;
    private String url;
    private int width;
    private int height;
    private String size;
    private String type;
    private int part;
    private VerificationCodeBody.VerificationCodeBodyGeeTest geetest;


    public int getBangumi_id() {
        return bangumi_id;
    }

    public void setBangumi_id(int bangumi_id) {
        this.bangumi_id = bangumi_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIs_cartoon() {
        return is_cartoon;
    }

    public void setIs_cartoon(boolean is_cartoon) {
        this.is_cartoon = is_cartoon;
    }

    public boolean isIs_creator() {
        return is_creator;
    }

    public void setIs_creator(boolean is_creator) {
        this.is_creator = is_creator;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public VerificationCodeBody.VerificationCodeBodyGeeTest getGeetest() {
        return geetest;
    }

    public void setGeetest(VerificationCodeBody.VerificationCodeBodyGeeTest geetest) {
        this.geetest = geetest;
    }

    @Override
    public String toString() {
        return "CreateNewAlbum{" +
                "bangumi_id=" + bangumi_id +
                ", name='" + name + '\'' +
                ", is_cartoon=" + is_cartoon +
                ", is_creator=" + is_creator +
                ", url='" + url + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", size='" + size + '\'' +
                ", type='" + type + '\'' +
                ", part=" + part +
                ", geetest=" + geetest +
                '}';
    }
}

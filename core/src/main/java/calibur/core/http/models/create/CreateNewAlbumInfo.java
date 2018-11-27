package calibur.core.http.models.create;

public class CreateNewAlbumInfo {
    private ChooseImageAlbum data;
    private int exp;
    private String message;

    public ChooseImageAlbum getData() {
        return data;
    }

    public void setData(ChooseImageAlbum data) {
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

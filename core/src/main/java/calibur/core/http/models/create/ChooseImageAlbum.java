package calibur.core.http.models.create;

public class ChooseImageAlbum {
    private int id;
    private String name;
    private String is_cartoon;
    private String is_creator;
    private int image_count;
    private String poster;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIs_cartoon() {
        return is_cartoon;
    }

    public void setIs_cartoon(String is_cartoon) {
        this.is_cartoon = is_cartoon;
    }

    public String getIs_creator() {
        return is_creator;
    }

    public void setIs_creator(String is_creator) {
        this.is_creator = is_creator;
    }

    public int getImage_count() {
        return image_count;
    }

    public void setImage_count(int image_count) {
        this.image_count = image_count;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public String toString() {
        return "ChooseImageAlbumData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", is_cartoon='" + is_cartoon + '\'' +
                ", is_creator='" + is_creator + '\'' +
                ", image_count=" + image_count +
                ", poster='" + poster + '\'' +
                '}';
    }
}

package calibur.core.http.models.anime;

public class AnimeNewListForWeek {
    private int id;
    private String name;
    private String avatar;
    private boolean update;
    private String released_video_id;
    private String released_part;
    private String end;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public String getReleased_video_id() {
        return released_video_id;
    }

    public void setReleased_video_id(String released_video_id) {
        this.released_video_id = released_video_id;
    }

    public String getReleased_part() {
        return released_part;
    }

    public void setReleased_part(String released_part) {
        this.released_part = released_part;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "AnimeNewListForWeekData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", update=" + update +
                ", released_video_id='" + released_video_id + '\'' +
                ", released_part='" + released_part + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}

package calibur.core.http.models.comment;

import java.util.List;

public class TrendingShowInfoCommentItem {
    private int id;
    private String content;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainImages> images;
    private int modal_id;
    private String created_at;
    private int floor_count;
    private int to_user_id;
    private int from_user_id;
    private String from_user_name;
    private String from_user_zone;
    private String from_user_avatar;
    private TrendingChildCommentInfo comments;
    private boolean liked;
    private int like_count;
    private boolean is_owner;
    private boolean is_master;
    private boolean is_leader;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainImages> getImages() {
        return images;
    }

    public void setImages(List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainImages> images) {
        this.images = images;
    }

    public int getModal_id() {
        return modal_id;
    }

    public void setModal_id(int modal_id) {
        this.modal_id = modal_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getFloor_count() {
        return floor_count;
    }

    public void setFloor_count(int floor_count) {
        this.floor_count = floor_count;
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(int to_user_id) {
        this.to_user_id = to_user_id;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(int from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getFrom_user_name() {
        return from_user_name;
    }

    public void setFrom_user_name(String from_user_name) {
        this.from_user_name = from_user_name;
    }

    public String getFrom_user_zone() {
        return from_user_zone;
    }

    public void setFrom_user_zone(String from_user_zone) {
        this.from_user_zone = from_user_zone;
    }

    public String getFrom_user_avatar() {
        return from_user_avatar;
    }

    public void setFrom_user_avatar(String from_user_avatar) {
        this.from_user_avatar = from_user_avatar;
    }

    public TrendingChildCommentInfo getComments() {
        return comments;
    }

    public void setComments(TrendingChildCommentInfo comments) {
        this.comments = comments;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public boolean isIs_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public boolean isIs_master() {
        return is_master;
    }

    public void setIs_master(boolean is_master) {
        this.is_master = is_master;
    }

    public boolean isIs_leader() {
        return is_leader;
    }

    public void setIs_leader(boolean is_leader) {
        this.is_leader = is_leader;
    }

    @Override
    public String toString() {
        return "TrendingShowInfoCommentItemData{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", images=" + images +
                ", modal_id=" + modal_id +
                ", created_at='" + created_at + '\'' +
                ", floor_count=" + floor_count +
                ", to_user_id=" + to_user_id +
                ", from_user_id=" + from_user_id +
                ", from_user_name='" + from_user_name + '\'' +
                ", from_user_zone='" + from_user_zone + '\'' +
                ", from_user_avatar='" + from_user_avatar + '\'' +
                ", comments=" + comments +
                ", liked=" + liked +
                ", like_count=" + like_count +
                ", is_owner=" + is_owner +
                ", is_master=" + is_master +
                ", is_leader=" + is_leader +
                '}';
    }
}

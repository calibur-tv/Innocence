package calibur.core.http.models.followList.params;

public class FolllowListParams {
    private String type;
    private String sort;
    private int bangumiId;
    private String userZone;
    private int page;
    private int take;
    private int minId;
    private String seenIds;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(int bangumiId) {
        this.bangumiId = bangumiId;
    }

    public String getUserZone() {
        return userZone;
    }

    public void setUserZone(String userZone) {
        this.userZone = userZone;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTake() {
        return take;
    }

    public void setTake(int take) {
        this.take = take;
    }

    public int getMinId() {
        return minId;
    }

    public void setMinId(int minId) {
        this.minId = minId;
    }

    public String getSeenIds() {
        return seenIds;
    }

    public void setSeenIds(String seenIds) {
        this.seenIds = seenIds;
    }

    @Override
    public String toString() {
        return "FolllowListParams{" +
                "type='" + type + '\'' +
                ", sort='" + sort + '\'' +
                ", bangumiId=" + bangumiId +
                ", userZone='" + userZone + '\'' +
                ", page=" + page +
                ", take=" + take +
                ", minId=" + minId +
                ", seenIds='" + seenIds + '\'' +
                '}';
    }
}

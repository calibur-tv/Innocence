package calibur.core.http.models.comment;

public class CreateMainCommentInfo {
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList data;
    private int exp;
    private String message;

    public TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList getData() {
        return data;
    }

    public void setData(TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList data) {
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
        return "CreateMainCommentInfoData{" +
                "data=" + data +
                ", exp=" + exp +
                ", message='" + message + '\'' +
                '}';
    }
}

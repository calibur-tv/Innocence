package calibur.core.http.models.comment;

public class ReplyCommentInfo {
    private TrendingChildCommentInfo.TrendingChildCommentInfoList data;
    private int exp;
    private String message;

    public TrendingChildCommentInfo.TrendingChildCommentInfoList getData() {
        return data;
    }

    public void setData(TrendingChildCommentInfo.TrendingChildCommentInfoList data) {
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
        return "ReplyCommentInfoData{" +
                "data=" + data +
                ", exp=" + exp +
                ", message='" + message + '\'' +
                '}';
    }
}

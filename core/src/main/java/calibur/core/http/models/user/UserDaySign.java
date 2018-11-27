package calibur.core.http.models.user;

public class UserDaySign {
    private int exp;
    private String message;

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
        return "UserDaySignData{" +
                "exp=" + exp +
                ", message='" + message + '\'' +
                '}';
    }
}

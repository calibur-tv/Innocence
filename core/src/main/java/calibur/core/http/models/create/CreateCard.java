package calibur.core.http.models.create;

public class CreateCard {
    private int data;
    private int exp;
    private String message;

    public int getData() {
        return data;
    }

    public void setData(int data) {
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
        return "CreateCardData{" +
                "data=" + data +
                ", exp=" + exp +
                ", message='" + message + '\'' +
                '}';
    }
}

package calibur.core.http.models.jsbridge.models;

public class H5ShowConfirmResultModel {
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "H5ShowConfirmResultModel{" +
                "result=" + result +
                '}';
    }
}

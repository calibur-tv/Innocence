package calibur.core.http.models.jsbridge.models;

public class H5ToggleModel {
    private String model;
    private String type;
    private int id;
    private H5ToggleModelResult result;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public H5ToggleModelResult getResult() {
        return result;
    }

    public void setResult(H5ToggleModelResult result) {
        this.result = result;
    }

    public void setAll(String model,String type,int id,boolean rewarded){
        H5ToggleModelResult result  = new H5ToggleModelResult();
        result.setRewarded(rewarded);
        setModel(model);
        setType(type);
        setId(id);
    }

    @Override
    public String toString() {
        return "H5ToggleModel{" +
                "model='" + model + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", result=" + result +
                '}';
    }

    public class H5ToggleModelResult{
        private boolean rewarded;

        public boolean isRewarded() {
            return rewarded;
        }

        public void setRewarded(boolean rewarded) {
            this.rewarded = rewarded;
        }

        @Override
        public String toString() {
            return "H5ToggleModelResult{" +
                    "rewarded=" + rewarded +
                    '}';
        }
    }
}

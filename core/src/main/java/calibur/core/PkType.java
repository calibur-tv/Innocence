package calibur.core;
public class PkType {
    public PkType(){
    }

    /**
     * 构建包类型
     * P: product D: debug B: beta
     */
    private String mtype = "D";
    public String getType() {
        return mtype;
    }
}

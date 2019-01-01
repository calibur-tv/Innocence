package calibur.core.http.models.jsbridge;

import calibur.core.http.models.jsbridge.models.H5ImageModel;
import calibur.core.http.models.jsbridge.models.H5ShowConfirmModel;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.foundation.utils.JSONUtil;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import java.io.Serializable;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2019/01/01 3:17 PM
 * version: 1.0
 * description: 根据func的不同来解析出对应的params对象
 */
@JsonAdapter(H5RespModelDeserializer.class)
public class H5RespModel implements Serializable{

  private String func;
  private Object params;
  private String callbackId;
  public H5RespModel(JsonObject object) {
    if (object != null) {
      try {
        func = object.get("func").getAsString();
        callbackId = object.get("callbackId").getAsString();
        params = JSONUtil.fromJson(object.get("params"), getClazz());
      } catch (Throwable r) {
        r.printStackTrace();
      }
    }
  }

  public String getFunc() {
    return func;
  }

  public String getCallbackId() {
    return callbackId;
  }

  public Object getParams() {
    return params;
  }

  private Class<?> getClazz() {
    if (IH5JsCallApp.previewImages.equals(func)) {
      return H5ImageModel.class;
    } else if (IH5JsCallApp.showConfirm.equals(func)) {
      return H5ShowConfirmModel.class;
    }
    return null;
  }

  public H5ImageModel getImagesInfo() {
    return (H5ImageModel) params;
  }

}

package calibur.core.http.models.jsbridge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/07 5:12 PM
 * version: 1.0
 * description:
 */
public class H5RespModelDeserializer implements JsonDeserializer<H5RespModel> {
  @Override public H5RespModel deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    return new H5RespModel(json.getAsJsonObject());
  }
}

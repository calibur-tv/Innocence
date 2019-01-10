package calibur.foundation.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2017/06/14/10:09 AM
 * desc   :
 * version: 1.0
 */
public final class JSONUtil {

  private JSONUtil() {
  }

  /**
   * 将奇数个转义字符变为偶数个
   */
  public static String replaceChar(String s){
    StringBuilder sb = new StringBuilder();
    char c;
    for (int i = 0; i < s.length(); i++) {
      c = s.charAt(i);
      switch (c) {
        case '\\':
          sb.append("\\\\");
          break;
        default:
          sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * 对象转换成json字符串
   */
  public static String toJson(Object obj) {
    try {
      Gson gson = new Gson();
      return gson.toJson(obj);
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return null;
  }

  public static String toJson(Object obj, Type typeOfSrc) {
    try {
      Gson gson = new Gson();
      return gson.toJson(obj, typeOfSrc);
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * json字符串转成对象
   */
  public static <T> T fromJson(String str, Type type) {
    try {
      Gson gson = new Gson();
      return gson.fromJson(str, type);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * json字符串转成对象
   */
  public static <T> T fromJson(String str, Class<T> clzz) {
    try {
      Gson gson = new Gson();
      return gson.fromJson(str, clzz);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }

  public static <T> T fromJson(JsonElement jsonElement, Class<T> clzz){
    try{
      return new Gson().fromJson(jsonElement, clzz);
    } catch (Throwable e){
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Object只能是基本类型和String
   */
  //public static <T> T fromMap(String str) {
  //  Type type = new TypeToken<LinkedTreeMap<String, Object>>() {}.getType();
  //  try {
  //    Gson gson = new GsonBuilder().registerTypeAdapterFactory(MapTypeAdapter.FACTORY).create();
  //    return gson.fromJson(str, type);
  //  } catch (JsonSyntaxException e) {
  //    e.printStackTrace();
  //  }
  //  return null;
  //}

  public static Gson getDefaultGson() {
    JsonSerializer<Date> ser = new JsonSerializer<Date>() {
      @Override
      public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? null : new JsonPrimitive(src.getTime());
      }
    };
    JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
      @Override
      public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException {
        return json == null ? null : new Date(json.getAsLong());
      }
    };

    return new GsonBuilder().registerTypeAdapter(Date.class, ser)
        .registerTypeAdapter(Date.class, deser)
        .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
        .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
        .create();
  }

  public static GsonBuilder getDefaultGsonBuilder() {
    JsonSerializer<Date> ser = new JsonSerializer<Date>() {
      @Override
      public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
          context) {
        return src == null ? null : new JsonPrimitive(src.getTime());
      }
    };
    JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
      @Override
      public Date deserialize(JsonElement json, Type typeOfT,
          JsonDeserializationContext context) throws JsonParseException {
        return json == null ? null : new Date(json.getAsLong());
      }
    };
    return new GsonBuilder()
        .registerTypeAdapter(Date.class, ser)
        .registerTypeAdapter(Date.class, deser)
        .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
        .registerTypeAdapter(boolean.class, booleanAsIntAdapter);
  }

  private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
      if (value == null) {
        out.nullValue();
      } else {
        out.value(value);
      }
    }

    @Override
    public Boolean read(JsonReader in) throws IOException {
      JsonToken peek = in.peek();
      switch (peek) {
        case BOOLEAN:
          return in.nextBoolean();
        case NULL:
          in.nextNull();
          return null;
        case NUMBER:
          return in.nextInt() != 0;
        case STRING:
          return in.nextString().equalsIgnoreCase("1");
        default:
          throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
      }
    }
  };
}

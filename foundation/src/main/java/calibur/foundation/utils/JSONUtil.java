package calibur.foundation.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;

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
   * 对象转换成json字符串
   */
  public static String toJson(Object obj) {
    try {
      Gson gson = new Gson();
      return gson.toJson(obj);
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static String toJson(Object obj, Type typeOfSrc) {
    try {
      Gson gson = new Gson();
      return gson.toJson(obj, typeOfSrc);
    } catch (JsonSyntaxException e) {
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
    } catch (JsonSyntaxException e) {
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
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static <T> T fromJson(JsonElement jsonElement, Class<T> clzz){
    try{
      return new Gson().fromJson(jsonElement, clzz);
    } catch (JsonSyntaxException e){
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

}

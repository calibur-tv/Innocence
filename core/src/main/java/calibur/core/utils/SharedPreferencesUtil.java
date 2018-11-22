package calibur.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import calibur.foundation.FoundationContextHolder;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/21 3:43 PM
 * version: 1.0
 * description:
 */
public final class SharedPreferencesUtil {
  public static final String FILE_NAME = "share_data";

  public static void putString(String key, String value) {
    SharedPreferences sp = FoundationContextHolder.getContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    sp.edit().putString(key, value).apply();
  }

  public static String getString(String key) {
    SharedPreferences sp = FoundationContextHolder.getContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    return sp.getString(key, "");
  }

}

package calibur.foundation;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 1:35 PM
 * version: 1.0
 * description:
 */
public class FoundationContextHolder {
  @SuppressLint("StaticFieldLeak")
  private static Context context;

  public static void setContext(Context context) {
    FoundationContextHolder.context = context;
  }

  public static  Context getContext() {
    return context;
  }
}

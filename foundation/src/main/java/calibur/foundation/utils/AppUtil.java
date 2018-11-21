package calibur.foundation.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import calibur.foundation.FoundationContextHolder;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 5:35 PM
 * version: 1.0
 * description:
 */
public class AppUtil {

  private static String versionName;
  public static String getAppVersionName() {
    if (TextUtils.isEmpty(versionName)) {
      try {
        PackageInfo packageInfo = FoundationContextHolder.getContext().getApplicationContext()
            .getPackageManager()
            .getPackageInfo(FoundationContextHolder.getContext().getPackageName(), 0);
        versionName = packageInfo.versionName;
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
    }
    return versionName;
  }
}

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
  public static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
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

  public static boolean isPkgInstalled(String pkgName) {
    if (TextUtils.isEmpty(pkgName)) return false;
    try {
      PackageManager pm = FoundationContextHolder.getContext().getPackageManager();
      return pm.getApplicationInfo(pkgName, 0).enabled;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

}

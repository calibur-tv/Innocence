package com.riuir.calibur.assistUtils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.text.TextUtils;
import com.riuir.calibur.app.App;

public class VersionUtils {
    /** * 版本号比较 *
     * * @param version1  new 接口获取的版本
     * * @param version2  old 本地版本
     * * @return
     * 0代表相等，1代表version1大于version2，-1代表version1小于version2*/
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) { return 0; }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        LogUtils.d("VersionCheck", "version1Array=="+version1Array.length);
        LogUtils.d("VersionCheck", "version2Array=="+version2Array.length);
        int index = 0;
        // 获取最小长度值
         int minLen = Math.min(version1Array.length, version2Array.length);
         int diff = 0;
         // 循环判断每位的大小
        LogUtils.d("VersionCheck", "verTag2=2222="+version1Array[index]);
         while (index < minLen
                 && (diff = Integer.parseInt(version1Array[index])
                 - Integer.parseInt(version2Array[index])) == 0) {
             index++;
         }
         if (diff == 0) {
             // 如果位数不一致，比较多余位数
              for (int i = index; i < version1Array.length; i++) {
                  if (Integer.parseInt(version1Array[i]) > 0) {
                      return 1;
                  }
              }
              for (int i = index; i < version2Array.length; i++) {
                  if (Integer.parseInt(version2Array[i]) > 0) {
                      return -1;
                  }
              }
              return 0;
         } else {
             return diff > 0 ? 1 : -1;
         }
    }

    /** 获取本地软件版本号
	 */
    public static int getLocalVersion() {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = App.instance().getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(App.instance().getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            LogUtils.d("VersionCheck", "本地版本号（int） = " + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName() {
      String localVersion = "";
      if (TextUtils.isEmpty(localVersion)) {
        try {
          PackageInfo packageInfo = App.instance().getApplicationContext()
              .getPackageManager()
              .getPackageInfo(App.instance().getPackageName(), 0);
          localVersion = packageInfo.versionName;
          LogUtils.d("VersionCheck", "本地版本号 = " + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
          e.printStackTrace();
        }
      }
      return localVersion;
    }

}

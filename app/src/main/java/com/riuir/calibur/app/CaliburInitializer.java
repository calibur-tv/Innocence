package com.riuir.calibur.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import calibur.core.http.CaliburHttpContext;
import calibur.core.http.OkHttpClientManager;
import calibur.core.http.RetrofitManager;
import calibur.foundation.bus.BusinessBusManager;
import com.riuir.calibur.BuildConfig;
import java.util.List;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 2:04 PM
 * version: 1.0
 * description:
 */
public class CaliburInitializer {

  public static final String MAIN_PROCESS_NAME = BuildConfig.APPLICATION_ID;
  private App mApp;

  public CaliburInitializer(App app) {
    this.mApp = app;
  }
  public void doLaunching() {
    String pro = getProcessName(mApp);
    BusinessBusManager.init();
    initBase();
    if (MAIN_PROCESS_NAME.equals(pro)) {
      initForMainProcess();
    } else {
      generalInit();
    }
  }

  private void initBase() {
  }

  private void initForMainProcess() {
    OkHttpClientManager.init(new CaliburHttpContext());
    RetrofitManager.getInstance().init();
  }

  private void generalInit() {
  }

  private static String getProcessName(Application application) {
    ActivityManager activityManager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
    if (runningAppProcessInfoList != null) {
      for (ActivityManager.RunningAppProcessInfo appProcessInfo : runningAppProcessInfoList) {
        if (android.os.Process.myPid() == appProcessInfo.pid) {
          return appProcessInfo.processName;
        }
      }
    }
    return null;
  }
}

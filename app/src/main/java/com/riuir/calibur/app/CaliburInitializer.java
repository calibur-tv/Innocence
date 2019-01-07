package com.riuir.calibur.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import calibur.core.http.CaliburHttpContext;
import calibur.core.http.OkHttpClientManager;
import calibur.core.http.RetrofitManager;
import calibur.foundation.bus.BusinessBus;
import calibur.foundation.bus.BusinessBusManager;
import calibur.foundation.config.PackageTypeConfig;

import com.alibaba.android.arouter.launcher.ARouter;
import com.newrelic.agent.android.NewRelic;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.riuir.calibur.BuildConfig;
import com.riuir.calibur.utils.album.MyAlbumLoader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static com.riuir.calibur.assistUtils.LogUtils.isDebug;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 2:04 PM
 * version: 1.0
 * description:
 */
public class CaliburInitializer {

  private static final String MAIN_PROCESS_NAME = BuildConfig.APPLICATION_ID;
  private App mApp;

  public CaliburInitializer(App app) {
    this.mApp = app;
  }
  public void doLaunching() {
    String pro = getProcessName(mApp);
    initBase();
    if (MAIN_PROCESS_NAME.equals(pro)) {
      mainProcessInit();
    } else {
      generalInit();
    }
  }

  private void initBase() {
    isDebug = false;
    BusinessBusManager.init();
    initBugly();
    initAlbum();
    initARoute();
  }


  private void mainProcessInit() {
    //if (PackageTypeConfig.isDebugEnv()) {
    //  BusinessBus.post(mApp, "debugModule/init");
    //} else {
      OkHttpClientManager.init(new CaliburHttpContext());
    //}
    RetrofitManager.getInstance().init();
    NewRelic.withApplicationToken("AAa57f0da1dfaa0c520a452934e733f80792e57c3e").start(mApp);
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

  /**
   * 初始化日志工具
   */

  private void initLogger() {
    PrettyFormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
        .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
        .tag("r_logger")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
        .build();
    Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
      @Override
      public boolean isLoggable(int priority, String tag) {
        return BuildConfig.DEBUG;
      }
    });
  }

  /**
   * 初始化腾讯bugly管理工具
   */
  private void initBugly() {
    Context context = App.instance();
    // 获取当前包名
    String packageName = context.getPackageName();
    // 获取当前进程名
    String processName = getProcessName(android.os.Process.myPid());
    // 设置是否为上报进程
    CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
    strategy.setUploadProcess(processName == null || processName.equals(packageName));
    // 初始化Bugly
    //        CrashReport.initCrashReport(context, "d07841a6e4", isDebug, strategy);
    Bugly.init(context, "d07841a6e4", isDebug, strategy);
  }

  /**
   * 初始化X5浏览器内核
   */

  private void initX5Web() {
    QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
      @Override
      public void onViewInitFinished(boolean arg0) {
        // TODO Auto-generated method stub            Log.d("app", " onViewInitFinished is " + arg0);
        //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核
      }

      @Override
      public void onCoreInitFinished() {
        // TODO Auto-generated method stub
        //x5内核初始化完成回调接口，可通过参数判断是否加载起来了x5内核
      }
    };
    //x5内核初始化接口
    QbSdk.initX5Environment(App.instance(),  cb);

  }

  /**
   * 初始化album
   */
  private void initAlbum() {
    Album.initialize(AlbumConfig.newBuilder(App.instance())
        .setAlbumLoader(new MyAlbumLoader())
        .build());
  }


  private void initARoute() {
    if (isDebug){
      ARouter.openDebug();
      ARouter.openLog();
    }
    ARouter.init(App.instance());
  }

  /**
   * 获取进程号对应的进程名
   *
   * @param pid 进程号
   * @return 进程名
   */
  private static String getProcessName(int pid) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
      String processName = reader.readLine();
      if (!TextUtils.isEmpty(processName)) {
        processName = processName.trim();
      }
      return processName;
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    }
    return null;
  }
}

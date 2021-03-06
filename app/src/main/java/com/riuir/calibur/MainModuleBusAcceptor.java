package com.riuir.calibur;

import android.content.Context;
import calibur.foundation.bus.BusinessBusObject;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.utils.Constants;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/18 9:34 AM
 * version: 1.0
 * description: 主模块事件总线
 */
public class MainModuleBusAcceptor extends BusinessBusObject {

  public MainModuleBusAcceptor(String host) {
    super(host);
  }

  @Override public Object doBusinessJob(final Context context, String bizName, Object... param) {

    if ("mainModule/showToast".equalsIgnoreCase(bizName)) {
      String msg = (String) param[0];
      ToastUtils.toastShort(msg);
    } else if ("mainModule/postException2Bugly".equalsIgnoreCase(bizName)) {
      if (Constants.userInfoData != null) {
        CrashReport.setUserId(Constants.userInfoData.getNickname());
      }
      Throwable throwable = (Throwable) param[0];
      CrashReport.postCatchedException(throwable);
    }

    return null;
  }

  @Override public void doAsyncBusinessJob(Context context, String bizName, AsyncCallResultListener resultListener, Object... param) {
  }

  @Override public Object doURLJob(Context context, String url) {
    return null;
  }

  @Override public void doAsyncURLJob(Context context, String url, AsyncCallResultListener resultListener) {
  }
}

package calibur.debug;

import android.content.Context;
import calibur.foundation.bus.BusinessBusObject;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/15 9:34 AM
 * version: 1.0
 * description:Debug模块
 * 仅在开发环境下该模块参与编译.(需在local.properties文件中开启:buildModel=debug)
 */
public class DebugModuleBusAcceptor extends BusinessBusObject {

  public DebugModuleBusAcceptor(String host) {
    super(host);
  }

  @Override public Object doBusinessJob(final Context context, String bizName, Object... param) {

    if ("debugModule/init".equalsIgnoreCase(bizName)) {
      String msg = (String) param[0];

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

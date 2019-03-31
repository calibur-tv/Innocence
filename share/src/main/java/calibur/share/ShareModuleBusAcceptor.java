package calibur.share;

import android.content.Context;
import calibur.foundation.bus.BusinessBusObject;
import com.tencent.tauth.Tencent;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/18 9:34 AM
 * version: 1.0
 * description: 登录块事件总线
 */
public class ShareModuleBusAcceptor extends BusinessBusObject {
  private static final String QQ_APP_ID = "1107909078";
  private static final String WX_APP_ID = "wx938caba780eb9fd1";
  public ShareModuleBusAcceptor(String host) {
    super(host);
  }

  @Override public Object doBusinessJob(final Context context, String bizName, Object... param) {
    if (bizName.equalsIgnoreCase("shareModule/init")) {
      Tencent.createInstance(QQ_APP_ID, context);
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

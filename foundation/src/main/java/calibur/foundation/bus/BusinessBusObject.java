package calibur.foundation.bus;

import android.content.Context;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/18 9:35 AM
 * version: 1.0
 * description:
 */
public abstract class BusinessBusObject {
  private String prefixAndHost;
  public interface AsyncCallResultListener {
    void asyncCallResult(String errorCode, Object... obj);
  }

  public BusinessBusObject(String host) {
    prefixAndHost = host;
  }

  public String getHost() {
    return  prefixAndHost;
  }

  public abstract Object doBusinessJob(Context context,String bizName, Object... param);

  public abstract void doAsyncBusinessJob(Context context,String bizName, AsyncCallResultListener resultListener, Object... param);

  public abstract Object doURLJob(Context context,String url);

  public abstract void doAsyncURLJob(Context context,String url, AsyncCallResultListener resultListener);
}

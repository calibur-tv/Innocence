package calibur.foundation.bus;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import java.util.HashMap;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/18 9:34 AM
 * version: 1.0
 * description:
 */
public class BusinessBus {
  private static final HashMap<String, BusinessBusObject> hostMap = new HashMap<String, BusinessBusObject>();

  public synchronized static boolean register(BusinessBusObject busObject) {
    if (busObject == null) {
      return false;
    }

    String host = busObject.getHost().toLowerCase();
    if (hostMap.containsKey(host)) {
      //LogUtil.d("BusinessBus", host + " :已注册，不可重复注册");
    }
    hostMap.put(host, busObject);
    return true;
  }

  public static Object post(Context context, String bizName, Object... param) {
    BusinessBusObject obj = findBusObject(parseBizNameHost(bizName));
    if (obj != null) {
      try {
        return obj.doBusinessJob(context, bizName, param);
      } catch (Throwable t) {
        BusinessBus.post(null, "mainModule/postException2Bugly", t);
      }
    }

    return null;
  }

  public static void postByCallback(Context context, String bizName, BusinessBusObject.AsyncCallResultListener callResultListener, Object... param) {
    BusinessBusObject obj = findBusObject(parseBizNameHost(bizName));
    if (obj != null) {
      try {
        obj.doAsyncBusinessJob(context, bizName, callResultListener, param);
      } catch (Throwable t) {
        BusinessBus.post(null, "mainModule/postException2Bugly", t);
      }
    }
  }

  public static Object callURL(Context context, String url) {
    BusinessBusObject obj = findBusObject(parseUrlHost(url));
    if (obj != null) {
      return obj.doURLJob(context, url);
    }

    return null;
  }

  private static String parseBizNameHost(String bizName) {
    if (TextUtils.isEmpty(bizName)) {
      return null;
    }
    int slashIndex = bizName.indexOf('/');
    if (slashIndex != -1) {
      return bizName.substring(0, slashIndex);
    }
    return null;
  }

  private static String parseUrlHost(String url) {
    Uri uri = Uri.parse(url);
    return uri.getHost();
  }

  private static BusinessBusObject findBusObject(String host) {
    if (TextUtils.isEmpty(host)) {
      return null;
    }

    BusinessBusObject obj = hostMap.get(host.toLowerCase());
    if (obj == null) {
      obj = BusinessBusManager.registerBusObjectWithHost(host);
    }
    return obj;
  }
}

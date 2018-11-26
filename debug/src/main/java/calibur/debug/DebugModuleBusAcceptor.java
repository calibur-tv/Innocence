package calibur.debug;

import android.app.Application;
import android.content.Context;
import calibur.core.http.OkHttpClientManager;
import calibur.foundation.bus.BusinessBusObject;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/15 9:34 AM
 * version: 1.0
 * description:Debug模块
 * 仅在开发环境下该模块参与编译.(需在local.properties文件中开启:buildModel=debug)
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class DebugModuleBusAcceptor extends BusinessBusObject {

  public DebugModuleBusAcceptor(String host) {
    super(host);
  }

  @Override public Object doBusinessJob(final Context context, String bizName, Object... param) {

    if ("debugModule/init".equalsIgnoreCase(bizName)) {
      OkHttpClientManager.init(new DebugHttpConfig ());
      //init LeakCanary
      LeakCanary.install((Application) context);
      //init Facebook stetho
      Stetho.initializeWithDefaults(context);
      //init Facebook Flipper
      //SoLoader.init(context, false);
      //final FlipperClient client = AndroidFlipperClient.getInstance(context);
      //client.addPlugin(new InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()));
      //client.addPlugin(new NetworkFlipperPlugin());
      //client.addPlugin(new SharedPreferencesFlipperPlugin(context, "my_shared_preference_file"));
      //client.start();
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

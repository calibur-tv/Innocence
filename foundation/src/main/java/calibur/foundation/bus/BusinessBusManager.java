package calibur.foundation.bus;

import android.text.TextUtils;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/18 9:35 AM
 * version: 1.0
 * description:
 */
public class BusinessBusManager {
  private static final String TAG = "BusinessBusManager";
  private static HashMap<String, String> moduleConfigMap = new HashMap<>();
  private static AtomicBoolean hasInit = new AtomicBoolean(false);

  public static void init() {
    hasInit.set(true);
    moduleConfigMap.put("mainModule", "com.riuir.calibur.MainModuleBusAcceptor");
    moduleConfigMap.put("debugModule", "calibur.debug.DebugModuleBusAcceptor");
  }

  @SuppressWarnings("unchecked")
  public static BusinessBusObject registerBusObjectWithHost(String hostName) {
    if (TextUtils.isEmpty(hostName)) {
      return null;
    }

    if (!hasInit.get()) {
      init();
      BusinessBus.post(null, "mainModule/postException2Bugly", new Throwable("reInit BusinessBusManager..."));
    }
    String className = moduleConfigMap.get(hostName);
    BusinessBusObject retObj = null;

    if (!TextUtils.isEmpty(className)) {
      try {
        Class<BusinessBusObject> clazz = (Class<BusinessBusObject>) Class.forName(className);
        Constructor<BusinessBusObject> constructor = clazz.getConstructor(String.class);
        BusinessBusObject tmpRetObj = constructor.newInstance(hostName);
        if (BusinessBus.register(tmpRetObj)) {
          retObj = tmpRetObj;
        }
      } catch (Throwable e) {
        e.printStackTrace();
        BusinessBus.post(null, "mainModule/postException2Bugly", e);
      }
    }

    return retObj;
  }
}

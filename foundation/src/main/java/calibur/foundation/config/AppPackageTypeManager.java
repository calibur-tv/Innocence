package calibur.foundation.config;

import android.text.TextUtils;
import calibur.foundation.bus.BusinessBus;
import java.lang.reflect.Method;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/25 10:47 PM
 * version: 1.0
 * description:
 */
public class AppPackageTypeManager {

    private static AppPackageTypeManager sInstance = null;
    private static String packType = "";

    private AppPackageTypeManager() {
    }

    public static AppPackageTypeManager getInstance() {
        if (sInstance == null) {
            synchronized (AppPackageTypeManager.class) {
                if (sInstance == null) {
                    sInstance = new AppPackageTypeManager();
                }
            }
        }
        return sInstance;
    }

    public String getPackageType() {
        if (TextUtils.isEmpty(packType)) {
            Class<?> packageType;
            try {
                packageType = Class.forName("calibur.core.PkType");
                Method method = packageType.getDeclaredMethod("getType");
                packType = (String) method.invoke(packageType.newInstance());
            } catch (Throwable e) {
                e.printStackTrace();
                packType = "";
                BusinessBus.post(null, "mainModule/postException2Bugly", e);
            }
        }

        return packType;

    }
}

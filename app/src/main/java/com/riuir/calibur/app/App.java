package com.riuir.calibur.app;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.support.multidex.MultiDex;
import calibur.core.manager.UserSystem;
import calibur.foundation.FoundationContextHolder;
import com.bumptech.glide.Glide;
import com.riuir.calibur.utils.Constants;

public class App extends Application  {
    private static App instance;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        FoundationContextHolder.setContext(this);
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            CaliburInitializer initializer = new CaliburInitializer(this);
            initializer.doLaunching();
            Constants.AUTH_TOKEN = UserSystem.getInstance().getUserToken();
            Constants.ISLOGIN = UserSystem.getInstance().isLogin();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static App instance() {
        return instance;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                Glide.get(instance).clearMemory();
                break;
        }
    }
}

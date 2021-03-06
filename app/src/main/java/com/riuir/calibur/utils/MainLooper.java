package com.riuir.calibur.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by jkyeo on 2017/10/24.
 */

public class MainLooper extends Handler {

    private static MainLooper instance = new MainLooper(Looper.getMainLooper());

    protected MainLooper(Looper looper) {
        super(looper);
    }

    public static MainLooper getInstance() {
        return instance;
    }

    public static void runOnUiThread(Runnable runnable) {
        if(Looper.getMainLooper().equals(Looper.myLooper())) {
            runnable.run();
        } else {
            instance.post(runnable);
        }

    }

}

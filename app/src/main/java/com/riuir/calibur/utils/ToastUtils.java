package com.riuir.calibur.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.riuir.calibur.app.App;

/**
 * @author 杨建宽
 * @date 2017/11/14
 * @mail yangjiankuan@lanjingren.com
 * @desc
 */

public class ToastUtils {

    public static void toastShort(final String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        MainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.instance(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void toastLong(final String message) {
        MainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.instance(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

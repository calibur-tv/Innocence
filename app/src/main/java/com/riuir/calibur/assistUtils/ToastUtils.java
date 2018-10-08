package com.riuir.calibur.assistUtils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.riuir.calibur.app.App;
import com.riuir.calibur.utils.MainLooper;


/**
 * Toast统一管理类
 *
 */
public class ToastUtils
{

    private ToastUtils()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message)
    {
        try {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message)
    {
        try {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

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

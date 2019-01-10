package com.riuir.calibur.assistUtils;

import android.util.Log;
import calibur.foundation.config.PackageTypeConfig;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * log日志辅助类
 */
public class LogUtils
{

    private LogUtils()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = PackageTypeConfig.isDebugEnv();
    private static final String TAG = "way";

    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, msg);

    }

    public static void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg)
    {
        if (isDebug)
            Log.v(TAG, msg);
    }


    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
        //网络异常上报
        BuglyLog.v(tag,msg);

    }
}

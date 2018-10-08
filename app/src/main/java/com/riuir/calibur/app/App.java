package com.riuir.calibur.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.riuir.calibur.BuildConfig;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.utils.Constants;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static com.riuir.calibur.assistUtils.LogUtils.isDebug;

/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/12/22
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 * 版权：
 * ************************************
 */
public class App extends Application  {
    private static App instance;


    @Override
    public void onCreate() {
        super.onCreate();
        //正式打包时取消注释
//        isDebug = false;
        instance = this;
        initLogger();
        initBugly();

        Constants.AUTH_TOKEN = (String) SharedPreferencesUtils.get(App.instance(),"Authorization",new String());

        LogUtils.d("userToken","token = "+Constants.AUTH_TOKEN );

        if (Constants.AUTH_TOKEN!=null&&Constants.AUTH_TOKEN.length()!=0){
            Constants.ISLOGIN = true;
        }else {
            Constants.ISLOGIN = false;
        }
        Logger.d("app");
    }




    public static App instance() {
        return instance;
    }

    /**
     * 初始化日志工具
     */

    private void initLogger() {
        PrettyFormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .tag("r_logger")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    /**
     * 初始化腾讯bugly管理工具
     */
    private void initBugly() {
//        CrashReport.initCrashReport(getApplicationContext(), "d07841a6e4", false);

        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, "d07841a6e4", isDebug, strategy);

    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        android.support.multidex.MultiDex.install(this);
    }
}

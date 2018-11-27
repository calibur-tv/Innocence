package com.riuir.calibur.app;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import calibur.core.manager.UserSystem;
import calibur.foundation.FoundationContextHolder;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.riuir.calibur.BuildConfig;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.album.MyAlbumLoader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static com.riuir.calibur.assistUtils.LogUtils.isDebug;

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
        //正式打包时取消注释
        isDebug = false;
        CaliburInitializer initializer = new CaliburInitializer(this);
        initializer.doLaunching();
        initLogger();
        initBugly();
        initX5Web();
        initAlbum();
        Constants.AUTH_TOKEN = (String) SharedPreferencesUtils.get(App.instance(),"Authorization",new String());
        Constants.ISLOGIN = UserSystem.getInstance().isLogin();
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
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
//        CrashReport.initCrashReport(context, "d07841a6e4", isDebug, strategy);
        Bugly.init(getApplicationContext(), "d07841a6e4", isDebug, strategy);
    }

    /**
     * 初始化X5浏览器内核
     */

    private void initX5Web() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub            Log.d("app", " onViewInitFinished is " + arg0);
                //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
                //x5内核初始化完成回调接口，可通过参数判断是否加载起来了x5内核
            }
        };
        //x5内核初始化接口
         QbSdk.initX5Environment(getApplicationContext(),  cb);

    }

    /**
     * 初始化album
     */
    private void initAlbum() {
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MyAlbumLoader())
                .build());
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
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                //TODO:
                break;
        }
    }
}

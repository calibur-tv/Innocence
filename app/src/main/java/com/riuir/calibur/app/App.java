package com.riuir.calibur.app;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.riuir.calibur.BuildConfig;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.utils.Constants;

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
        instance = this;
        initLogger();
        Constants.AUTH_TOKEN = (String) SharedPreferencesUtils.get(App.instance(),"Authorization",new String());


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




}

package com.riuir.calibur.app

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.riuir.calibur.BuildConfig
import com.riuir.calibur.net.ApiClient

/**
 *************************************
 * 作者：韩宝坤
 * 日期：2017/11/18
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 *************************************
 */
class App : Application() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        ApiClient.instance.init()
        initLogger()
    }

    companion object {
        lateinit var instance: App
    }

    /**
     * 初始化日志工具
     */
    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .tag("mp_logger")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }
}
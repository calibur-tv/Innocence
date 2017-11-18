package com.riuir.calibur.app

import android.app.Application
import com.riuir.calibur.net.ApiClient

/**
 *************************************
 * 作者：韩宝坤
 * 日期：2017/11/18
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 *************************************
 */
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        ApiClient.instance.init()
    }
}
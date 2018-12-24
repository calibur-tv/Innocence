package com.riuir.calibur.ui.jsbridge

import android.os.Handler
import calibur.core.http.models.jsbridge.JsBridgeMessage
import calibur.core.jsbridge.AbsJsBridge
import calibur.core.jsbridge.JsBridgeContract
import calibur.core.jsbridge.JsCallNativeFunsRegister
import calibur.core.jsbridge.interfaces.IBaseJsCallApp
import calibur.core.jsbridge.interfaces.IH5JsCallApp
import calibur.foundation.utils.JSONUtil

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/03/27/5:45 PM
 * version: 1.0
 * description:
 */
class CommonJsCallRegistry(handler: Handler, absJsBridge: AbsJsBridge) : JsCallNativeFunsRegister(handler, absJsBridge) {

  override fun jsCallNative(funs: JsBridgeContract?, jsonString: String): String {
    val bridgeMessage = JSONUtil.fromJson(jsonString, JsBridgeMessage::class.java)
    val func = bridgeMessage.func
    val jsFun = funs as IH5JsCallApp
    when (func) {
      IBaseJsCallApp.getUserInfo -> {
        jsFun.getUserInfo()
      }
      IBaseJsCallApp.getDeviceInfo -> {
        jsFun.getDeviceInfo()
      }
    }
    return ""
  }
}
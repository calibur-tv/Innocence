package calibur.core.jsbridge

import android.os.Handler
import android.text.TextUtils
import calibur.core.http.models.jsbridge.JsBridgeMessage
import calibur.core.jsbridge.interfaces.IBaseJsCallApp
import calibur.foundation.bus.BusinessBus
import calibur.foundation.config.PackageTypeConfig
import calibur.foundation.utils.JSONUtil
import java.util.HashMap

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/12/27/3:29 PM
 * version: 1.0
 * description: js 调用 native 的方法集中处理，子类需要继承实现方法: jsCallNative()来处理自己的业务逻辑
 */
abstract class JsCallNativeFunsRegister(protected var handler: Handler, absJsBridge: AbsJsBridge) {
  private var emptyCallbackId = "-1"
  private var callbacksMap: MutableMap<String, AbsJsBridge.IJsCallNativeCallback> = HashMap()
  protected var javaScriptNativeBridge: AbsJsBridge = absJsBridge

  abstract fun jsCallNative(funs: JsBridgeContract?, jsonString: String?): String?

  private fun putCallBack(callbackId: String?, callback: AbsJsBridge.IJsCallNativeCallback) {
    callbackId?.let {
      callbacksMap[it] = callback
    }
  }

  fun getCallback(callbackId: String?): AbsJsBridge.IJsCallNativeCallback? {
    val callback = callbacksMap[callbackId]
    if (callback != null) {
      callbacksMap.remove(callbackId)
      return callback
    }
    return null
  }

  fun handleJsCall(jsCallContract: JsBridgeContract?, data: String?): String? {
    if(jsCallContract == null) return ""
    val baseH5Obj = JSONUtil.fromJson(data, JsBridgeMessage::class.java) ?: return ""

    //如果这个 callbackId != ‘-1’，那么在 Native 执行完被调用的方法后，使用这个 callbackId 将结果回调给 Javascript，否则不用回调
    if (!TextUtils.isEmpty(baseH5Obj.callbackId) && baseH5Obj.callbackId != emptyCallbackId) {
      this.putCallBack(baseH5Obj.callbackId, object : AbsJsBridge.IJsCallNativeCallback {
        override fun onResponse(args: Any?, callbackId: String?) {
          javaScriptNativeBridge.handleJsCallback(args, callbackId)
          if (PackageTypeConfig.isDebugEnv()) {
            javaScriptNativeBridge.callJavascript(IBaseJsCallApp.logMsg, args, null)
          }
        }
      })
    }
    try {
      return jsCallNative(jsCallContract, data)
    } catch (r: Throwable) {
      BusinessBus.post(null, "mainModule/postException2Bugly", r)
    }
    return ""
  }
}
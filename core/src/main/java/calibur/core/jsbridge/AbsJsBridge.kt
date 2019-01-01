package calibur.core.jsbridge

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.webkit.JavascriptInterface
import android.webkit.WebView
import calibur.core.http.models.jsbridge.JsBridgeMessage
import calibur.foundation.bus.BusinessBus
import calibur.foundation.utils.JSONUtil

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/12/27/3:29 PM
 * version: 1.0
 * description:抽象的 JsBridge
 */
@Suppress("LeakingThis")
abstract class AbsJsBridge(context: Context, handler: Handler,
    private var jsCallContract: JsBridgeContract?,
    private var webView: WebView) {

  var mContext: Context? = null
  private var nativeCallJsFunsRegister: NativeCallJsFunsRegister
  private var jsCallNativeFunsRegister: JsCallNativeFunsRegister

  abstract fun getJsCallNativeFunsRegister(handler: Handler): JsCallNativeFunsRegister

  init {
    this.mContext = context
    this.nativeCallJsFunsRegister = NativeCallJsFunsRegister()
    this.jsCallNativeFunsRegister = getJsCallNativeFunsRegister(handler)
  }

  /**
   * [Javascript 方法被调用后数据回调给 App 的回调方法入口]
   * {
   * "callbackId": "<之前请求 App 方法时参数里的指定的 callback_id>",
   * "args": {
   * "x": 1,
   * "y": 2
   * }
   * }
   */
  @JavascriptInterface
  fun handleCallbackFromJS(data: String) {
    if (!TextUtils.isEmpty(data)) {
      val msg = JSONUtil.fromJson(data, JsBridgeMessage::class.java)
      val callbackId = msg.callbackId
      val id = Integer.parseInt(callbackId)
      if (id != -1) {
        val callback = nativeCallJsFunsRegister.findCallbackById(id)
        callback?.onResponse(webView, data)
      }
    }
  }

  /**
   * [Javascript 调用 App 方法的入口]
   * {
   * "func": "Namespace.Object.Function",
   * "args": {
   * "x": 1,
   * "y": 2,
   * "z": 3
   * },
   * "callbackId": “-1”
   * }
   */
  @JavascriptInterface
  fun handleMessageFromJS(data: String): String? {
    if (!TextUtils.isEmpty(data)) {
      try {
        return jsCallNativeFunsRegister.handleJsCall(jsCallContract, data)
      } catch (t: Throwable) {
        BusinessBus.post(null, "mainModule/postException2Bugly", t)
      }
    }
    return ""
  }

  /**
   *
   * [App 主动调用 Javascript 方法的入口方法
   * 方法名: M.invoker.appCallJs(args)]
   * {
   * "func": "Namespace.Object.Function",
   * "args": {
   * "x": 1,
   * "y": 2,
   * "z": 3
   * },
   * "callbackId": “-1”
   * }
   */
  fun callJavascript(jsFName: String, args: Any?, callbackName: INativeCallJsCallback?) {
    if(mContext == null || (mContext as Activity).isFinishing) return
    webView.post(object : Runnable {
      override fun run() {
        if (!TextUtils.isEmpty(webView.url)) {
          try {
            val message = JsBridgeMessage().apply {
              this.callbackId = nativeCallJsFunsRegister.generateCallbackIdByFunName(callbackName)
              this.func = jsFName
              this.params = args
            }
            val param = JSONUtil.toJson(message, JsBridgeMessage::class.java)
            webView.loadUrl("javascript:M.invoker.appCallJs('$param')")
          } catch (r: Throwable) {
            r.printStackTrace()
            BusinessBus.post(null, "mainModule/postException2Bugly", r)
          }
        }
      }
    })
  }

  fun callJavascript(jsFName: String, callback: INativeCallJsCallback?) {
    callJavascript(jsFName, "", callback)
  }

  /**
   * [App 处理js的回调方法
   * 方法名: M.invoker.JsCallAppCallback(args)]
   * {
   * "callbackId": "<之前请求 App 方法时参数里的指定的 callback_id>",
   * "args": {
   * "x": 1,
   * "y": 2
   * }
   * }
   */
  fun handleJsCallback(args: Any?, callbackId: String) {
    if (!TextUtils.isEmpty(webView.url) ) {
      try {
        val msg = JsBridgeMessage().apply {
          this.callbackId = callbackId
          //取消 JSONUtil.tojson 否则JSCallAPP不通
          this.params = args
        }
        val param = JSONUtil.toJson(msg)
        webView.loadUrl("javascript:M.invoker.JsCallAppCallback('$param')")
      } catch (r: Throwable) {
        r.printStackTrace()
        BusinessBus.post(null, "mainModule/postException2Bugly", r)
      }
    }
  }

  /**
   * 通过callbackId异步执行js的回调
   */
  fun executeJsCallbackByCallbackId(callbackId: String) {
    executeJsCallbackByCallbackId("", callbackId)
  }

  /**
   * 通过callbackId异步执行js的回调
   */
  fun executeJsCallbackByCallbackId(args: Any?, callbackId: String) {
    val callback = jsCallNativeFunsRegister.getCallback(callbackId)
    callback?.onResponse(args, callbackId)
  }

  interface IJsCallNativeCallback {
    fun onResponse(args: Any?, callbackId: String)
  }

  interface INativeCallJsCallback {
    fun onResponse(webView: WebView?, jsonString: String?)
  }
}
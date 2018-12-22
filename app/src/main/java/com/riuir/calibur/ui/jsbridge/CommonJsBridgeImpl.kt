package com.riuir.calibur.ui.jsbridge

import android.content.Context
import android.os.Handler
import android.webkit.WebView
import calibur.core.jsbridge.AbsJsBridge
import calibur.core.jsbridge.JsBridgeContract
import calibur.core.jsbridge.JsCallNativeFunsRegister

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/03/27/5:43 PM
 * version: 1.0
 * description:
 */
class CommonJsBridgeImpl(context: Context, handler: Handler, jsCallContract: JsBridgeContract, webView: WebView) : AbsJsBridge(context, handler, jsCallContract, webView) {
  override fun getJsCallNativeFunsRegister(handler: Handler): JsCallNativeFunsRegister {
    return CommonJsCallRegistry(handler, this)
  }
}
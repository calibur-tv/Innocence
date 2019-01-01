package com.riuir.calibur.ui.jsbridge

import android.os.Handler
import calibur.core.http.models.jsbridge.H5BeanObject
import calibur.core.http.models.jsbridge.H5ParamsData
import calibur.core.jsbridge.AbsJsBridge
import calibur.core.jsbridge.JsBridgeContract
import calibur.core.jsbridge.JsCallNativeFunsRegister
import calibur.core.jsbridge.interfaces.IBaseJsCallApp
import calibur.core.jsbridge.interfaces.IH5JsCallApp
import calibur.foundation.utils.JSONUtil
import com.riuir.calibur.assistUtils.activityUtils.PreviewImageUtils
import java.util.ArrayList

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/03/27/5:45 PM
 * version: 1.0
 * description:
 */
class CommonJsCallRegistry(handler: Handler, absJsBridge: AbsJsBridge) : JsCallNativeFunsRegister(handler, absJsBridge) {

  override fun jsCallNative(funs: JsBridgeContract?, jsonString: String): String? {
    val bridgeMessage = JSONUtil.fromJson(jsonString, H5BeanObject::class.java)
    val func = bridgeMessage.func
    val jsFun = funs as IH5JsCallApp
    var h5Data: H5ParamsData? = bridgeMessage.params
    when (func) {
      IBaseJsCallApp.getUserInfo -> {
        handler.post {
          javaScriptNativeBridge.executeJsCallbackByCallbackId(jsFun.getUserInfo(), bridgeMessage.callbackId)
        }
      }
      IBaseJsCallApp.getDeviceInfo -> {
        handler.post {
          javaScriptNativeBridge.executeJsCallbackByCallbackId(jsFun.getDeviceInfo(), bridgeMessage.callbackId)
        }
      }
      IH5JsCallApp.setUserInfo -> {
        handler.post {
          javaScriptNativeBridge.executeJsCallbackByCallbackId(jsFun.setUserInfo(bridgeMessage.params), bridgeMessage.callbackId)
        }
      }
      IH5JsCallApp.toNativePage -> {
        handler.post {
          callNativeBusiness("")
        }
      }
      IH5JsCallApp.previewImages -> { //跳转到大图浏览
        handler.post {
          h5Data?.let {
            val images: MutableList<String>? = it.let {
              val imageList: MutableList<String> = mutableListOf()
              it.images?.forEach {
                imageList.add(it.url)
              }
              imageList
            }
            images?.let {
              val clickUrl = images.get(h5Data.index)
              PreviewImageUtils.startPreviewImage(javaScriptNativeBridge.mContext, images as ArrayList<String>?, clickUrl, null)
            }
          }
        }
      }
      IH5JsCallApp.createMainComment -> {
        handler.post {
          javaScriptNativeBridge.executeJsCallbackByCallbackId(jsFun.createMainComment(bridgeMessage.params), bridgeMessage.callbackId)
        }
      }
      IH5JsCallApp.createSubComment -> {
        handler.post {
          javaScriptNativeBridge.executeJsCallbackByCallbackId(jsFun.createSubComment(bridgeMessage.params), bridgeMessage.callbackId)
        }
      }
      IH5JsCallApp.toggleClick -> {
        handler.post {
          javaScriptNativeBridge.executeJsCallbackByCallbackId(jsFun.toggleClick(bridgeMessage.params), bridgeMessage.callbackId)
        }
      }
      IH5JsCallApp.showConfirm -> {
        handler.post {
          javaScriptNativeBridge.executeJsCallbackByCallbackId(jsFun.showConfirm(bridgeMessage.params), bridgeMessage.callbackId)
        }
      }
    }
    return ""
  }

  /**
   * H5 调用ative的业务
   */
  private fun callNativeBusiness(bizName: String?) {
    when (bizName) {

    }
  }


}
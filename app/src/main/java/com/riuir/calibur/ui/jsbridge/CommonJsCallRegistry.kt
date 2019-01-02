package com.riuir.calibur.ui.jsbridge

import android.os.Handler
import calibur.core.http.models.jsbridge.H5RespModel
import calibur.core.http.models.jsbridge.models.H5CallAppBusinessModel
import calibur.core.http.models.jsbridge.models.IAppBusiness
import calibur.core.http.models.user.MineUserInfo
import calibur.core.jsbridge.AbsJsBridge
import calibur.core.jsbridge.JsBridgeContract
import calibur.core.jsbridge.JsCallNativeFunsRegister
import calibur.core.jsbridge.interfaces.IBaseJsCallApp
import calibur.core.jsbridge.interfaces.IH5JsCallApp
import calibur.foundation.utils.JSONUtil
import com.riuir.calibur.assistUtils.LogUtils
import com.riuir.calibur.assistUtils.activityUtils.PreviewImageUtils
import com.riuir.calibur.ui.route.RouteUtils
import com.riuir.calibur.utils.Constants
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
    val bridgeMessage = JSONUtil.fromJson(jsonString, H5RespModel::class.java)
    val func = bridgeMessage.func
    val jsFun = funs as IH5JsCallApp
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
          jsSetUserInfo(bridgeMessage.params)
        }
      }
      IH5JsCallApp.toNativePage -> {
        handler.post {
          jsCallNativeBusiness(bridgeMessage.params)
        }
      }
      IH5JsCallApp.previewImages -> { //跳转到大图浏览
        handler.post {
          bridgeMessage?.let {
            val images: MutableList<String>? = it.imagesInfo.let {
              val imageList: MutableList<String> = mutableListOf()
              it.images?.forEach {
                imageList.add(it.url)
              }
              imageList
            }
            val clickUrl = images?.get(bridgeMessage.imagesInfo.index)
            images?.let {
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
  private fun jsCallNativeBusiness(biz: Any?) {
    val bizName: H5CallAppBusinessModel = biz as H5CallAppBusinessModel
    RouteUtils.toPage(bizName.uri)
//    when (bizName.uri) {
//      IAppBusiness.bizName1 ->{
//        ///:TODO 跳转到用户
//      }
//      IAppBusiness.bizName2 ->{
//        ///:TODO 跳转到帖子详情
//      }
//      IAppBusiness.bizName3 ->{
//        ///:TODO 跳转到漫评详情
//      }
//      IAppBusiness.bizName4 ->{
//        ///:TODO 跳转到相册详情
//      }
//      IAppBusiness.bizName5 ->{
//        ///:TODO 跳转到番剧详情
//      }
//      IAppBusiness.bizName6 ->{
//        ///:TODO 跳转到系统公告
//      }
//    }
  }

  private fun jsSetUserInfo(biz: Any?){
    val bizData: MineUserInfo = biz as MineUserInfo
    Constants.userInfoData = bizData
  }


}
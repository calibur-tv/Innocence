package com.riuir.calibur.ui.jsbridge

import android.content.Intent
import android.os.Handler
import calibur.core.http.models.jsbridge.H5RespModel
import calibur.core.http.models.jsbridge.models.H5CallAppBusinessModel
import calibur.core.http.models.jsbridge.models.H5ShowConfirmModel
import calibur.core.http.models.jsbridge.models.H5ShowConfirmResultModel
import calibur.core.http.models.user.MineUserInfo
import calibur.core.jsbridge.AbsJsBridge
import calibur.core.jsbridge.JsBridgeContract
import calibur.core.jsbridge.JsCallNativeFunsRegister
import calibur.core.jsbridge.interfaces.IBaseJsCallApp
import calibur.core.jsbridge.interfaces.IH5EditJsCallApp
import calibur.core.jsbridge.interfaces.IH5JsCallApp
import calibur.foundation.utils.JSONUtil
import com.riuir.calibur.assistUtils.LogUtils
import com.riuir.calibur.assistUtils.PhoneSystemUtils
import com.riuir.calibur.assistUtils.activityUtils.PreviewImageUtils
import com.riuir.calibur.ui.home.MineFragment
import com.riuir.calibur.ui.route.RouteUtils
import com.riuir.calibur.utils.Constants
import com.riuir.calibur.utils.DialogHelper
import java.util.ArrayList

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/03/27/5:45 PM
 * version: 1.0
 * description:
 */
class CommonJsCallRegistry(handler: Handler, absJsBridge: AbsJsBridge) : JsCallNativeFunsRegister(handler, absJsBridge) {

    override fun jsCallNative(funs: JsBridgeContract?, jsonString: String?): String? {
        LogUtils.d("createNewScore","jsonString = $jsonString")
        val bridgeMessage = JSONUtil.fromJson(jsonString, H5RespModel::class.java)
        val func = bridgeMessage.func
        if (funs is IH5JsCallApp){
            val jsFun = funs as IH5JsCallApp
            when (func) {
                IBaseJsCallApp.getUserInfo -> {
                    handler.post {
                        LogUtils.d("createNewScore","userInfo = ${Constants.userInfoData}")
                        javaScriptNativeBridge.executeJsCallbackByCallbackId(Constants.userInfoData, bridgeMessage.callbackId)
                    }
                }
                IBaseJsCallApp.getDeviceInfo -> {
                    handler.post {
                        LogUtils.d("createNewScore","DeviceInfo = ${PhoneSystemUtils.getDeviceInfo()}")
                        javaScriptNativeBridge.executeJsCallbackByCallbackId(PhoneSystemUtils.getDeviceInfo(), bridgeMessage.callbackId)
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
                        jsFun.createMainComment(bridgeMessage.params)
                    }
                }
                IH5JsCallApp.createSubComment -> {
                    handler.post {
                        jsFun.createSubComment(bridgeMessage.params)
                    }
                }
                IH5JsCallApp.toggleClick -> {
                    handler.post {
                        jsFun.toggleClick(bridgeMessage.params)
                    }
                }
                IH5JsCallApp.showConfirm -> {
                    handler.post {
                        jsShowConfrim(bridgeMessage.params, bridgeMessage.callbackId)
                    }
                }
                IH5JsCallApp.readNotification -> {
                    handler.post {
                        jsFun.readNotification(bridgeMessage.params)
                    }
                }

            }
        }
        if (funs is IH5EditJsCallApp){
            val jsEditFun = funs as IH5EditJsCallApp
            when(func) {
                IBaseJsCallApp.getUserInfo -> {
                    handler.post {
                        LogUtils.d("createNewScore","userInfo = ${Constants.userInfoData}")
                        javaScriptNativeBridge.executeJsCallbackByCallbackId(Constants.userInfoData, bridgeMessage.callbackId)
                    }
                }
                IBaseJsCallApp.getDeviceInfo -> {
                    handler.post {
                        LogUtils.d("createNewScore","DeviceInfo = ${PhoneSystemUtils.getDeviceInfo()}")
                        javaScriptNativeBridge.executeJsCallbackByCallbackId(PhoneSystemUtils.getDeviceInfo(), bridgeMessage.callbackId)
                    }
                }
                IH5EditJsCallApp.createEditorSection ->{
                    handler.post {
                        jsEditFun.createEditorSection()
                    }
                }
                IH5EditJsCallApp.editEditorSection ->{
                    handler.post {
                        jsEditFun.editEditorSection()
                    }
                }
                IH5EditJsCallApp.editEditorImageSection ->{
                    handler.post {
                        jsEditFun.editEditorImageSection()
                    }
                }
                IH5EditJsCallApp.editEditorTextSection ->{
                    handler.post {
                        jsEditFun.editEditorTextSection()
                    }
                }
                IH5EditJsCallApp.sendEditorContent ->{
                    handler.post {
                        jsEditFun.sendEditorContent()
                    }
                }
            }
        }

        return ""
    }

    /**
     * H5 调用ative的业务
     * 页面跳转
     */
    private fun jsCallNativeBusiness(biz: Any?) {
        val bizName: H5CallAppBusinessModel = biz as H5CallAppBusinessModel
        RouteUtils.toPage(bizName.uri)
    }

    private fun jsSetUserInfo(biz: Any?) {
        LogUtils.d("callbackErrorLog","jsonString = $biz")
        val bizData: MineUserInfo = biz as MineUserInfo
        Constants.userInfoData = bizData
        val intent = Intent(MineFragment.COINCHANGE)
        javaScriptNativeBridge.mContext?.sendBroadcast(intent)
    }

    private fun jsShowConfrim(params: Any?,callBackId:String) {

        if (params is H5ShowConfirmModel) {
            DialogHelper.getConfirmDialog(javaScriptNativeBridge.mContext,
                    (params as H5ShowConfirmModel).title,
                    (params as H5ShowConfirmModel).message,
                    (params as H5ShowConfirmModel).submitButtonText,
                    (params as H5ShowConfirmModel).cancelButtonText,
                    false, { _, _ ->
                val result = H5ShowConfirmResultModel()
                result.isResult = true
                javaScriptNativeBridge.executeJsCallbackByCallbackId(result, callBackId)
            }, { _, _ ->
                val result = H5ShowConfirmResultModel()
                result.isResult = false
                javaScriptNativeBridge.executeJsCallbackByCallbackId(result, callBackId)
            }).show()
        }
    }
}
package calibur.core.jsbridge.interfaces

import calibur.core.jsbridge.JsBridgeContract

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/12/22 11:56 AM
 * version: 1.0
 * description:
 */
interface IBaseJsCallApp: JsBridgeContract {
  companion object {
    const val getDeviceInfo = "getDeviceInfo"
    const val getUserInfo = "getUserInfo"
    const val logMsg = "log"
  }

  fun getDeviceInfo(): Any?
  fun getUserInfo(): Any?
}
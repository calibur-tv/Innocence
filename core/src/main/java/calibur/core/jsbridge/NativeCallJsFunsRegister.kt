package calibur.core.jsbridge

import android.util.SparseArray
import calibur.core.jsbridge.AbsJsBridge.INativeCallJsCallback

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/12/27/3:29 PM
 * version: 1.0
 * description:该类是存放native调js的方法map集合
 */
class NativeCallJsFunsRegister {
  private var callbacks: SparseArray<INativeCallJsCallback> = SparseArray()
  private var key = 0

  fun generateCallbackIdByFunName(funName: AbsJsBridge.INativeCallJsCallback?): String {
    return if (funName == null) {
      "-1"
    } else {
      key++
      callbacks.append(key, funName)
      key.toString() + ""
    }
  }

  fun findCallbackById(callbackId: Int): AbsJsBridge.INativeCallJsCallback? {
    val callback = callbacks.get(callbackId)
    if (callback != null) {
      callbacks.remove(callbackId)
      return callback
    }
    return null
  }
}
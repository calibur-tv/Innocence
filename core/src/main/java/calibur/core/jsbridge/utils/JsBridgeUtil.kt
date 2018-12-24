package calibur.core.jsbridge.utils

import org.json.JSONObject
import java.util.HashMap

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/12/22 11:39 AM
 * version: 1.0
 * description:
 */
object JsBridgeUtil {
  const val BRIDGE_NAME = "__AndroidBridge"

  fun httpHeaders(): Map<String, String> {
    val additionalHttpHeaders = HashMap<String, String>()
    //Add params here
    return additionalHttpHeaders
  }


  fun httpHeadersToJsonString(): String {
    val map = httpHeaders()
    val jsonObject = JSONObject(map)
    return jsonObject.toString()
  }
}
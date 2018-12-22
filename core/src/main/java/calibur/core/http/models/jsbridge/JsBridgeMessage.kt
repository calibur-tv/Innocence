package calibur.core.http.models.jsbridge

import java.io.Serializable

class JsBridgeMessage : Serializable{
  var func: String? = null
  var callbackId: String = ""
  var params: Any? = null
}

package calibur.core.http.models.jsbridge

import calibur.core.http.models.base.IBaseResponse
import java.io.Serializable

class JsBridgeMessage : IBaseResponse, Serializable{
  var func: String? = null
  var callbackId: String? = ""
  var params: Any? = null
}

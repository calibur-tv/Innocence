package calibur.core.http.models.login

import java.io.Serializable

class WeChatLoginModel:Serializable {
    var access_token:String? = null
    var expires_in:Int = 0
    var refresh_token:String? = null
    var openid:String? = null
    var scope:String? = null
    var unionid:String? = null
}
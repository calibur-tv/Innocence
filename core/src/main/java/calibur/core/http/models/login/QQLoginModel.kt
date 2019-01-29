package calibur.core.http.models.login

import java.io.Serializable

class QQLoginModel: Serializable {
    var ret:Int = 0
    var pay_token:String?=null
    var pf:String?=null
    var query_authority_cost:Int = 0
    var authority_cost:Int = 0
    var expires_time:Long = 0
    var openid:String?=null
    var expires_in:Long = 0
    var pfkey:String?=null
    var msg:String?=null
    var access_token:String?=null
    var login_cost:Int = 0
}
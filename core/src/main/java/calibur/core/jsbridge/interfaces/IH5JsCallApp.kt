package calibur.core.jsbridge.interfaces

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/12/22 11:56 AM
 * version: 1.0
 * description:
 */
interface IH5JsCallApp: IBaseJsCallApp {
  companion object {
    const val setUserInfo = "setUserInfo"
    const val toNativePage = "toNativePage"
    const val previewImages = "previewImages"
    const val createMainComment = "createMainComment"
    const val createSubComment = "createSubComment"
    const val toggleClick = "toggleClick"
    const val showConfirm = "showConfirm"
  }

  fun createMainComment(params:Any?)
  fun createSubComment(params:Any?)
  fun toggleClick(params:Any?)
}
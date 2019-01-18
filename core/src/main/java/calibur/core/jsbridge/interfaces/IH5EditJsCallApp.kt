package calibur.core.jsbridge.interfaces

interface IH5EditJsCallApp :IBaseJsCallApp{
    companion object {
        const val createEditorSection = "createEditorSection"
        const val editEditorSection = "editEditorSection"
        const val editEditorImageSection = "editEditorImageSection"
        const val editEditorTextSection = "editEditorTextSection"
        const val getEditorContent = "getEditorContent"
        const val sendEditorContent = "sendEditorContent "
    }
    fun createEditorSection()
    fun editEditorSection()
    fun editEditorImageSection()
    fun editEditorTextSection()
    fun sendEditorContent()
}
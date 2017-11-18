package com.riuir.calibur.ui.widget

import android.app.Dialog
import android.content.Context
import com.riuir.calibur.R

/**
 *************************************
 * 作者：韩宝坤
 * 日期：2017/11/18
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 *************************************
 */
object LoadingDialog {
    private var dialog: Dialog? = null

    fun show(context: Context) {
        cancel()
        dialog = Dialog(context, R.style.LoadingDialog)
        dialog?.setContentView(R.layout.dialog_loading)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()
    }

    fun cancel() {
        dialog?.dismiss()
    }
}
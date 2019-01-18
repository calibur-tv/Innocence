package com.riuir.calibur.ui.share;

import android.util.Log;

import com.riuir.calibur.assistUtils.LogUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class QQBaseUiListener implements IUiListener {
    @Override
    public void onComplete(Object o) {
        LogUtils.d("qqShare","complete obj = "+o.toString());
    }

    @Override
    public void onError(UiError uiError) {
        LogUtils.d("qqShare","error code = "+uiError.errorCode+", msg = "+uiError.errorMessage);
    }

    @Override
    public void onCancel() {
        LogUtils.d("qqShare","onCancel");
    }
}

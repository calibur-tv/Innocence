package com.riuir.calibur.ui.home.image;

import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.utils.Constants;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.JSONUtil;

public class ImageDetailActivity extends BaseActivity implements IH5JsCallApp {

    private WebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;
    private int imageID;

    @BindView(R.id.image_detail_back_btn)
    ImageView backBtn;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_image_detail;
    }

    @Override
    protected void onInit() {
        imageID = getIntent().getIntExtra("imageID", 0);
        initWebView();
        initCommentView();
        TemplateRenderEngine.getInstance().checkImageDetailPageTemplateForUpdate();
    }

    private void initWebView() {
        mWebView = findViewById(R.id.image_detail_webview);
        mJavaScriptNativeBridge = new CommonJsBridgeImpl(this, new Handler(), this, mWebView);
        mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
    }

    private void initCommentView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setLoadingView(findViewById(R.id.refresh_layout));
    }

    @Override
    protected void onLoadData() {
        showLoading();
        apiService.getImageDetailData(imageID)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Object>() {
                    @Override public void onSuccess(Object data) {
                        JSONObject jsonObj = new JSONObject((Map) data);
                        LogUtils.d("imagedetail","data = "+jsonObj.toString());
                        WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.IMAGEDETAIL, jsonObj.toString());
                    }

                    @Override public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }

                    @Override public void onComplete() {
                        hideLoading();
                    }
                });
    }

    @Nullable
    @Override
    public String getDeviceInfo() {
        return null;
    }

    @Nullable
    @Override
    public String getUserInfo() {
        return JSONUtil.toJson(Constants.userInfoData);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

package com.riuir.calibur.ui.home.user;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;
@Route(path = RouteUtils.userBulletinPath)
public class UserBulletinActivity extends BaseActivity implements IH5JsCallApp {

    @BindView(R.id.user_bulletin_activity_web_view)
    AthenaWebView mWebView;
    @BindView(R.id.user_bulletin_back_btn)
    ImageView backBtn;
    @BindView(R.id.user_bulletin_activity_loading_view)
    ImageView webPageLoadingView;

    public AbsJsBridge mJavaScriptNativeBridge;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_bulletin;
    }

    @Override
    protected void onInit() {
        TemplateRenderEngine.getInstance().checkNoticeTemplateForUpdate();
        initView();
        initWebView();
    }

    private void initView() {
        GlideUtils.loadImageViewStaticGif(this,R.mipmap.web_page_loading,webPageLoadingView);
        setLoadingView(webPageLoadingView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mJavaScriptNativeBridge = new CommonJsBridgeImpl(this, new Handler(), this, mWebView);
        mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
        mWebView.setListener(this, new AthenaWebView.Listener() {
            @Override
            public void onPageStarted(String url, Bitmap favicon) {
                showLoading();
            }

            @Override
            public void onPageFinished(String url) {
                hideLoading();
            }

            @Override
            public void onPageError(int errorCode, String description, String failingUrl) {
            }

            @Override
            public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
            }

            @Override
            public void onExternalPageRequest(String url) {
            }
        });
    }

    @Override
    protected void onLoadData() {
        WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.NOTICE,"");
    }

    @Override
    public void createMainComment(@Nullable Object params) {

    }

    @Override
    public void createSubComment(@Nullable Object params) {
    }

    @Override
    public void toggleClick(@Nullable Object params) {

    }

    @Override
    public void readNotification(@Nullable Object params) {
    }
}

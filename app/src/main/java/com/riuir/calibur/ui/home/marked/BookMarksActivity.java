package com.riuir.calibur.ui.home.marked;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import butterknife.BindView;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.utils.Constants;

import org.jetbrains.annotations.Nullable;

@Route(path = RouteUtils.userBookmarkPath)
public class BookMarksActivity extends BaseActivity implements IH5JsCallApp {

    private WebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;

    @BindView(R.id.book_marks_back_btn)
    ImageView backBtn;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_book_marks;
    }

    @Override
    protected void onInit() {
        initWebView();
        initView();
        TemplateRenderEngine.getInstance().checkBookmarksTemplateForUpdate();
    }

    private void initView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setLoadingView(refreshLayout);
        refreshLayout.setEnabled(false);
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = findViewById(R.id.book_marks_webview);
        mJavaScriptNativeBridge = new CommonJsBridgeImpl(this, new Handler(), this, mWebView);
        mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
    }

    @Override
    protected void onLoadData() {
        WebTemplatesUtils.loadTemplates(mWebView, TemplateRenderEngine.BOOKMARKS, "");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public Object getDeviceInfo() {
        return PhoneSystemUtils.getDeviceInfo();
    }

    @Override
    public Object getUserInfo() {
        return Constants.userInfoData;
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
}

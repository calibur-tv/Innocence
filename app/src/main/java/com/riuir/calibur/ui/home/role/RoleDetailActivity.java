package com.riuir.calibur.ui.home.role;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import butterknife.BindView;
import calibur.core.http.models.anime.RoleShowInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.JSONUtil;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Route(path = RouteUtils.roleDetailPath)
public class RoleDetailActivity extends BaseActivity implements IH5JsCallApp {

    private AthenaWebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;
    private int roleId;

    @BindView(R.id.role_detail_back_btn)
    ImageView backBtn;
    @BindView(R.id.role_detail_activity_loading_view)
    ImageView webPageLoadingView;

    RoleShowInfo roleShowInfo;
    @BindView(R.id.role_detail_header_more)
    AppHeaderPopupWindows headerMore;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_role_detail;
    }

    @Override
    protected void onInit() {
        roleId = getIntent().getIntExtra("roleId", 0);
        TemplateRenderEngine.getInstance().checkRoleDetailTemplateForUpdate();
        initWebView();
        initView();
    }

    private void initView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GlideUtils.loadImageViewStaticGif(this,R.mipmap.web_page_loading,webPageLoadingView);
        setLoadingView(webPageLoadingView);
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = findViewById(R.id.role_detail_webview);
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
        showLoading();
        apiService.getRoleDetail(roleId)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        JSONObject jsonObj = new JSONObject((Map) data);
                        WebTemplatesUtils.loadTemplates(mWebView, TemplateRenderEngine.ROLE, jsonObj.toString());
                        roleShowInfo = JSONUtil.fromJson(jsonObj.toString(), RoleShowInfo.class);
                        setHeaderMore();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }

                    @Override
                    public void onComplete() {
                        hideLoading();
                    }
                });
    }

    private void setHeaderMore() {
        headerMore.setReportModelTag(AppHeaderPopupWindows.ROLE,roleShowInfo.getData().getId());
        headerMore.setShareLayout(roleShowInfo.getData().getName(),AppHeaderPopupWindows.ROLE,roleShowInfo.getData().getId(),"");
    }

    @Nullable
    @Override
    public Object getDeviceInfo() {
        return PhoneSystemUtils.getDeviceInfo();
    }

    @Nullable
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

    @Override
    public void readNotification(@Nullable Object params) {
    }
}

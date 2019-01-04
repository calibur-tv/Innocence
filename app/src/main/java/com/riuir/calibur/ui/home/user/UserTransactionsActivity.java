package com.riuir.calibur.ui.home.user;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;

import calibur.core.http.models.jsbridge.models.H5ShowConfirmResultModel;

import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.DialogHelper;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import calibur.core.http.models.jsbridge.models.H5ShowConfirmModel;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;

@Route(path = RouteUtils.userTrasactionRecordPath)
public class UserTransactionsActivity extends BaseActivity implements IH5JsCallApp {

    @BindView(R.id.user_transactions_activity_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.user_transactions_activity_web_view)
    AthenaWebView mWebView;
    @BindView(R.id.user_transactions_back_btn)
    ImageView backBtn;

    public AbsJsBridge mJavaScriptNativeBridge;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_transactions;
    }

    @Override
    protected void onInit() {
        refreshLayout.setEnabled(false);
        setLoadingView(refreshLayout);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initWebView();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mJavaScriptNativeBridge = new CommonJsBridgeImpl(this, new Handler(), this, mWebView);
        mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
    }

    @Override
    protected void onLoadData() {
        showLoading();
        apiService.getUserTransactions()
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Object>(){
                    @Override
                    public void onSuccess(Object data) {
                        JSONObject jsonObj = new JSONObject((Map) data);
                        WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.TRANSACTIONS,jsonObj.toString());
                    }

                    @Override
                    public void onComplete() {
                        hideLoading();
                    }
                });

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
}

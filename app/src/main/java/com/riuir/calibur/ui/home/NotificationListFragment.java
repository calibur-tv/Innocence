package com.riuir.calibur.ui.home;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;

import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.utils.Constants;

import butterknife.BindView;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationListFragment extends BaseFragment implements IH5JsCallApp {

    @BindView(R.id.notification_list_fragment_web_view_swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.notification_list_fragment_web_view)
    AthenaWebView mWebView;
    MainActivity mainActivity;

    public AbsJsBridge mJavaScriptNativeBridge;

    boolean isLoad = false;

    public static NotificationListFragment newInstance() {
        NotificationListFragment notificationListFragment = new NotificationListFragment();
        Bundle b = new Bundle();
        notificationListFragment.setArguments(b);
        return notificationListFragment;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_notification_list;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        refreshLayout.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setWeb();
    }

    @SuppressLint("JavascriptInterface")
    private void setWeb() {
        mainActivity.setOnRefreshMessageList(new MainActivity.OnRefreshMessageList() {
            @Override
            public void OnReFresh(int count) {
                if (!isLoad){
                    WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.NOTIFICATIONS, "");
                    isLoad = true;
                }
            }
        });
        mJavaScriptNativeBridge = new CommonJsBridgeImpl(getContext(), new Handler(), this, mWebView);
        mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
    }

    @Override
    public void createMainComment(@org.jetbrains.annotations.Nullable Object params) {

    }

    @Override
    public void createSubComment(@org.jetbrains.annotations.Nullable Object params) {

    }

    @Override
    public void toggleClick(@org.jetbrains.annotations.Nullable Object params) {

    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Object getDeviceInfo() {
        return PhoneSystemUtils.getDeviceInfo();
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Object getUserInfo() {
        return Constants.userInfoData;
    }
}

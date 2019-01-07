package com.riuir.calibur.ui.home;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;

import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.ui.widget.SearchLayout;
import com.riuir.calibur.utils.ActivityUtils;
import com.riuir.calibur.utils.Constants;

import butterknife.BindView;
import calibur.core.http.models.jsbridge.models.H5ReadNotificationModel;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.manager.UserSystem;
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
    @BindView(R.id.notification_list_search_layout)
    SearchLayout searchLayout;

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
        if (Constants.userInfoData == null)
            Constants.userInfoData = SharedPreferencesUtils.getUserInfoData(getContext());
        LogUtils.d("notificationWeb","userInfo = "+Constants.userInfoData.toString());
        mainActivity = (MainActivity) getActivity();
        refreshLayout.setEnabled(false);
        TemplateRenderEngine.getInstance().checkNotificationTemplateForUpdate();
        setWeb();
    }

    @Override
    public void onResume() {
        super.onResume();
        int stautsBarHeight = ActivityUtils.getStatusBarHeight(getContext());
        rootView.setPadding(0,stautsBarHeight,0,0);
    }

    @SuppressLint("JavascriptInterface")
    private void setWeb() {
        //点击底部“消息按钮之后加载”
        mainActivity.setOnRefreshMessageList(new MainActivity.OnRefreshMessageList() {
            @Override
            public void OnReFresh(int count) {
                if (!isLoad){
                    WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.NOTIFICATIONS, "");
                    isLoad = true;
                }
            }
        });
        //直接加载
//        WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.NOTIFICATIONS, "");
        mWebView.setListener(getActivity(), new AthenaWebView.Listener() {
            @Override
            public void onPageStarted(String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(String url) {
            }

            @Override
            public void onPageError(int errorCode, String description, String failingUrl) {
                LogUtils.d("notificationWeb","errorCode = "+errorCode+",description = "+description+",failingUrl ="+failingUrl);
            }

            @Override
            public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
            }

            @Override
            public void onExternalPageRequest(String url) {
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
        LogUtils.d("notificationWeb","userInfo = "+Constants.userInfoData.toString());
        LogUtils.d("notificationWeb","token = "+UserSystem.getInstance().getUserToken());
        return Constants.userInfoData;
    }

    @Override
    public void readNotification(@org.jetbrains.annotations.Nullable Object params) {
        if (params instanceof H5ReadNotificationModel){
            mainActivity.setNoReadMsgCount(((H5ReadNotificationModel) params).getCount());
        }
    }
}

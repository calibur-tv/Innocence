package com.riuir.calibur.ui.home.user;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.web.WebTemplatesUtils;

import butterknife.BindView;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;

public class UserHomeActivity extends BaseActivity {

    @BindView(R.id.user_home_activity_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.user_home_activity_web_view)
    AthenaWebView mWebView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_home;
    }

    @Override
    protected void onInit() {
        setLoadingView(refreshLayout);
        WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.HOME,"");
    }


}

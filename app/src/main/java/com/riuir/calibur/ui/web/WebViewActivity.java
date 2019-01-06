package com.riuir.calibur.ui.web;

import android.content.Intent;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.utils.Constants;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Route(path = "/browser/base")
public class WebViewActivity extends BaseActivity {

    @BindView(R.id.web_view_activity_web_view)
    AthenaWebView webView;
    @BindView(R.id.web_view_activity_back_btn)
    ImageView backBtn;
    @BindView(R.id.web_view_activity_web_view_swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.web_view_activity_web_view_title_Layout)
    RelativeLayout titleLayout;

    MyWebViewClient client;
    WebSettings webSettings;

    public static final String TYPE_RULE = "rule";
    public static final String TYPE_INVITE = "invite";
    public static final String TYPE_BROWSER_BASE = "browserBase";

    private String type = "";
    private String baseUrl = "";
    private int index = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if (type.equals(TYPE_RULE)){
            index = intent.getIntExtra("index",0);
        }
        baseUrl = intent.getStringExtra("baseUrl");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refreshLayout.setEnabled(false);
        setWebView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    private void setWebView() {

        setClient();
        if (type.equals(TYPE_INVITE)){
            setInviteLoad();
        }else if (type.equals(TYPE_BROWSER_BASE)){
            setBrowserBase();
        }else {
            setRuleLoad();
        }
    }

    private void setClient() {
        client = new MyWebViewClient();
        client.setOnPageStartedListener(new MyWebViewClient.OnPageStartedListener() {
            @Override
            public void onPageStart() {
                if (refreshLayout!=null){
                    refreshLayout.setRefreshing(true);
                }
            }
        });
        client.setOnPageFinishedListener(new MyWebViewClient.OnPageFinishedListener() {
            @Override
            public void onPageFinish() {
                if (refreshLayout!=null){
                    refreshLayout.setRefreshing(false);
                }
            }
        });
        webView.setWebViewClient(client);
    }


    private void setInviteLoad() {
        Map<String,String> header = new HashMap<>();
        header.put("Authorization",UserSystem.getInstance().getUserToken());

        webView.loadUrl("https://m.calibur.tv/app/invite",header);
    }

    private void setRuleLoad() {
        if (index!=0){
            webView.loadUrl("https://m.calibur.tv/app/handbook?index="+index);
        }else {
            webView.loadUrl("https://m.calibur.tv/app/handbook");
        }
    }


    private void setBrowserBase() {
        if (!TextUtils.isEmpty(baseUrl)){
            webView.loadUrl(baseUrl);
        }
    }

    @Override
    protected void handler(Message msg) {

    }
}

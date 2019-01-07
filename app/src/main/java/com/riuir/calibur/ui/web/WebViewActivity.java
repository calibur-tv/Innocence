package com.riuir.calibur.ui.web;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import calibur.core.manager.UserSystem;
import calibur.core.widget.webview.AthenaWebView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.utils.GlideUtils;

import java.util.HashMap;
import java.util.Map;

@Route(path = "/browser/base")
public class WebViewActivity extends BaseActivity {

    @BindView(R.id.web_view_activity_web_view)
    AthenaWebView webView;
    @BindView(R.id.web_view_activity_back_btn)
    ImageView backBtn;
    @BindView(R.id.web_view_activity_web_view_title_Layout)
    RelativeLayout titleLayout;

    @BindView(R.id.web_activity_loading_view)
    ImageView webPageLoadingView;

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
        setWebView();
    }


    private void setWebView() {
//        setClient();
        GlideUtils.loadImageViewStaticGif(this,R.mipmap.web_page_loading,webPageLoadingView);
        setLoadingView(webPageLoadingView);
        webView.setListener(this, new AthenaWebView.Listener() {
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
                LogUtils.d("notificationWeb","errorCode = "+errorCode+",description = "+description+",failingUrl ="+failingUrl);
            }

            @Override
            public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

            }

            @Override
            public void onExternalPageRequest(String url) {

            }
        });

        if (type.equals(TYPE_INVITE)){
            setInviteLoad();
        }else if (type.equals(TYPE_BROWSER_BASE)){
            setBrowserBase();
        }else {
            setRuleLoad();
        }
    }

    private void setInviteLoad() {
        Map<String,String> header = new HashMap<>();
        header.put("Authorization",UserSystem.getInstance().getUserToken());
        LogUtils.d("notificationWeb","token = "+UserSystem.getInstance().getUserToken());
        LogUtils.d("notificationWeb","header = "+header.get("Authorization"));
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
        LogUtils.d("notificationWeb","baseUrl = "+baseUrl);
        if (!TextUtils.isEmpty(baseUrl)){
            webView.loadUrl(baseUrl);
        }
    }

    @Override
    protected void handler(Message msg) {

    }
}

package com.riuir.calibur.ui.web;

import android.content.Intent;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import calibur.core.manager.UserSystem;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.utils.Constants;
import com.tencent.smtt.sdk.WebSettings;
import java.util.HashMap;
import java.util.Map;

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
    public static final String TYPE_WITHDRAWALS = "withdrawals";

    private String type = "";
    private String uri = "";
    private int index = 0;

    private static final String APP_CACAHE_DIRNAME = "/webcache";

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
        if (Constants.AUTH_TOKEN==null||Constants.AUTH_TOKEN.length()==0){
            Constants.AUTH_TOKEN = (String) SharedPreferencesUtils.get(App.instance(),"Authorization",new String());
        }

        setClient();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        String cacheDirPath = App.instance().getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
        //设置  Application Caches 缓存目录
        webSettings.setAppCachePath(cacheDirPath);
        webSettings.setAppCacheMaxSize(20*1024*1024);
        // 设置 WebView 的缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 支持启用缓存模式
        webSettings.setAppCacheEnabled(true);

        if (type.equals(TYPE_INVITE)){
            setInviteLoad();
        }else if (type.equals(TYPE_WITHDRAWALS)){
            setWithdrawals();
        }else {
            setRuleLoad();
        }
    }

    private void setClient() {
//        chromeClient = new WebChromeClient();
//        webView.setWebChromeClient(chromeClient);
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
        LogUtils.d("WebViewIndex","index = "+index);
        if (index!=0){
            webView.loadUrl("https://m.calibur.tv/app/handbook?index="+index);
        }else {
            webView.loadUrl("https://m.calibur.tv/app/handbook");
        }
    }

    private void setWithdrawals() {
        WebTemplatesUtils.loadTemplates(webView,TemplateRenderEngine.BOOKMARKS,"");
    }

    @Override
    protected void handler(Message msg) {

    }
}

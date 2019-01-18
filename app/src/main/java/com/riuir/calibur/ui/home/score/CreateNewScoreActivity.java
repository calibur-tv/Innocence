package com.riuir.calibur.ui.home.score;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.tencent.smtt.sdk.WebView;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5EditJsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;

@Route(path = RouteUtils.scoreCreatePath)
public class CreateNewScoreActivity extends BaseActivity implements IH5EditJsCallApp {
    @BindView(R.id.score_create_new_activity_loading_view)
    ImageView webPageLoadingView;
    @BindView(R.id.score_create_new_activity_title_back)
    ImageView backBtn;
    private AthenaWebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;
    private int scoreID;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_create_new_score;
    }

    @Override
    protected void onInit() {
        scoreID = getIntent().getIntExtra("scoreID", 891);
        TemplateRenderEngine.getInstance().checkEditorTemplateForUpdate();
        initView();
        initWebView();
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

    @Override
    protected void onLoadData() {
        showLoading();
        if (scoreID == 0){
            WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.EDITOR, "");
        }else {
            apiService.getScoreEditData(scoreID)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<Object>() {
                        @Override public void onSuccess(Object data) {
                            JSONObject jsonObj = new JSONObject((Map) data);
                            LogUtils.d("scoreCreate","data = "+jsonObj.toString());
                            WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.EDITOR, jsonObj.toString());
                        }

                        @Override public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                        }

                        @Override public void onComplete() {
                            hideLoading();
                        }
                    });
        }
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = findViewById(R.id.score_create_new_activity_web_view);
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
    protected void handler(Message msg) {
    }

    @Override
    public void createEditorSection() {
        LogUtils.d("createNewScore","1111111111111");

    }

    @Override
    public void editEditorSection() {
        LogUtils.d("createNewScore","22222222222222");
    }

    @Override
    public void editEditorImageSection() {
        LogUtils.d("createNewScore","33333333333333");
    }

    @Override
    public void editEditorTextSection() {
        LogUtils.d("createNewScore","44444444444444");
    }

    @Override
    public void sendEditorContent() {
        LogUtils.d("createNewScore","555555555555555");
    }
}

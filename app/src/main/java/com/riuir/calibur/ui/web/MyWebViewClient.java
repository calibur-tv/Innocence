package com.riuir.calibur.ui.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;


import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import calibur.core.manager.UserSystem;
import calibur.core.templates.TemplateRenderEngine;


public class MyWebViewClient extends WebViewClient {

    private OnPageStartedListener onPageStartedListener;
    private OnPageFinishedListener onPageFinishedListener;

    String url;

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        sslErrorHandler.proceed();
        super.onReceivedSslError(webView, sslErrorHandler, sslError);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        this.url = url;
        if (onPageStartedListener!=null){
            onPageStartedListener.onPageStart();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (onPageFinishedListener!=null){
            onPageFinishedListener.onPageFinish();
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String s) {
//        if (url.equals("https://static.calibur.tv/hybrid/templates/self/bookmarks/v1/v1-f7f83c9e52fdbb535d64154849cb05dc-8f010bbe472265b12c3a1e00ac8f57b3.mustache")){
//            String bookMarks = TemplateRenderEngine.getInstance().getTemplateRender(TemplateRenderEngine.BOOKMARKS).getTemplateRenderData("");
//            LogUtils.d("shouldOverrideUrlLoading","token = "+UserSystem.getUserToken());
//            LogUtils.d("shouldOverrideUrlLoading","bookMarks = "+bookMarks);
//            return true;
//        }
        return super.shouldOverrideUrlLoading(webView, s);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
//        if (url.equals("https://static.calibur.tv/hybrid/templates/self/bookmarks/v1/v1-f7f83c9e52fdbb535d64154849cb05dc-8f010bbe472265b12c3a1e00ac8f57b3.mustache")){
            String bookMarks = TemplateRenderEngine.getInstance().getTemplateRender(TemplateRenderEngine.BOOKMARKS).getTemplateRenderData("");
            LogUtils.d("shouldOverrideUrlLoading1","token = "+UserSystem.getUserToken());
            LogUtils.d("shouldOverrideUrlLoading1","bookMarks = "+bookMarks);
            return super.shouldInterceptRequest(webView, bookMarks);
//        }
//        return super.shouldInterceptRequest(webView, webResourceRequest);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
//        if (url.equals("https://static.calibur.tv/hybrid/templates/self/bookmarks/v1/v1-f7f83c9e52fdbb535d64154849cb05dc-8f010bbe472265b12c3a1e00ac8f57b3.mustache")){
            String bookMarks = TemplateRenderEngine.getInstance().getTemplateRender(TemplateRenderEngine.BOOKMARKS).getTemplateRenderData("");
            LogUtils.d("shouldOverrideUrlLoading2","token = "+UserSystem.getUserToken());
            LogUtils.d("shouldOverrideUrlLoading2","bookMarks = "+bookMarks);
            return super.shouldInterceptRequest(webView, bookMarks);
//        }
//        return super.shouldInterceptRequest(webView, s);
    }

    public void setOnPageStartedListener(OnPageStartedListener onPageStartedListener) {
        this.onPageStartedListener = onPageStartedListener;
    }

    public void setOnPageFinishedListener(OnPageFinishedListener onPageFinishedListener) {
        this.onPageFinishedListener = onPageFinishedListener;
    }

    public interface OnPageStartedListener{
        void onPageStart();
    }

    public interface OnPageFinishedListener{
        void onPageFinish();
    }
}

package com.riuir.calibur.ui.web;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


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
        return super.shouldOverrideUrlLoading(webView, s);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
        return super.shouldInterceptRequest(webView, webResourceRequest);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
        return super.shouldInterceptRequest(webView, s);
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

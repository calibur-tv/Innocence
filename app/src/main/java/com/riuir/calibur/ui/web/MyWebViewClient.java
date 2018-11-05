package com.riuir.calibur.ui.web;

import android.graphics.Bitmap;


import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


public class MyWebViewClient extends WebViewClient {

    private OnPageStartedListener onPageStartedListener;
    private OnPageFinishedListener onPageFinishedListener;

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        sslErrorHandler.proceed();
        super.onReceivedSslError(webView, sslErrorHandler, sslError);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
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

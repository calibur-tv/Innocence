package com.riuir.calibur.ui.home.card;

import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import butterknife.BindView;
import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.JSONUtil;
import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.utils.Constants;
import java.util.Map;
import org.json.JSONObject;

public class PostDetailActivity extends BaseActivity implements IH5JsCallApp {

  private WebView mWebView;
  public AbsJsBridge mJavaScriptNativeBridge;
  private int postId;

  @BindView(R.id.card_show_info_back_btn)
  ImageView backBtn;


  ReplyAndCommentView commentView;

  @Override
  protected int getContentViewId() {
    return R.layout.activity_post_detail;
  }

  @Override
  protected void onInit() {
    postId = getIntent().getIntExtra("cardID", 0);
    initWebView();
    initCommentView();
    TemplateRenderEngine.getInstance().checkPostDetailPageTemplateForUpdate();
  }

  private void initWebView() {
    mWebView = findViewById(R.id.post_detail_webview);
    mJavaScriptNativeBridge = new CommonJsBridgeImpl(this, new Handler(), this, mWebView);
    mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
  }

  private void initCommentView() {
      backBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
          }
      });
    setLoadingView(findViewById(R.id.refresh_layout));
  }

  @Override protected void onLoadData() {
    showLoading();
    apiService.getPostDetailData(postId)
        .compose(Rx2Schedulers.applyObservableAsync())
        .subscribe(new ObserverWrapper<Object>() {
          @Override public void onSuccess(Object data) {
            JSONObject jsonObj = new JSONObject((Map) data);
            WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.POST, jsonObj.toString());
          }

          @Override public void onFailure(int code, String errorMsg) {
            super.onFailure(code, errorMsg);
          }

          @Override public void onComplete() {
            hideLoading();
          }
        });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override public void onPointerCaptureChanged(boolean hasCapture) {

  }

  @Override public String getDeviceInfo() {
    return null;
  }

  @Override public String getUserInfo() {
    return JSONUtil.toJson(Constants.userInfoData);
  }
}

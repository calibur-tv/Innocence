package com.riuir.calibur.ui.home.comment;



import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.utils.Constants;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import calibur.core.http.models.comment.CreateMainCommentInfo;
import calibur.core.http.models.comment.ReplyCommentInfo;
import calibur.core.http.models.comment.TrendingShowInfoCommentMain;
import calibur.core.http.models.followList.post.CardShowInfoPrimacy;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.JSONUtil;

public class CommentDetailActivity extends BaseActivity implements IH5JsCallApp {

    private WebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;
    private int postId;

    @BindView(R.id.comment_detail_back_btn)
    ImageView backBtn;

    @BindView(R.id.comment_view)
    ReplyAndCommentView commentView;

    int commentId;
    String type;
//    TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList mainComment;

    private static CommentDetailActivity instance;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_comment_detail;
    }

    @Override
    protected void onInit() {
        instance = this;
        Intent intent = getIntent();
        commentId = intent.getIntExtra("id",0);
        type = intent.getStringExtra("type");

        initWebView();
        initView();
        TemplateRenderEngine.getInstance().checkCommentItemTemplateForUpdate();
    }

    private void initWebView() {
        mWebView = findViewById(R.id.comment_detail_webview);
        mJavaScriptNativeBridge = new CommonJsBridgeImpl(this, new Handler(), this, mWebView);
        mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
    }

    private void initView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setLoadingView(findViewById(R.id.refresh_layout));
    }

    @Override
    protected void onLoadData() {
        showLoading();
        apiService.getCommentItem(type,commentId,0)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        JSONObject jsonObj = new JSONObject((Map) data);
                        WebTemplatesUtils.loadTemplates(mWebView, TemplateRenderEngine.COMMENT, jsonObj.toString());
                        initCommentView();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }

                    @Override
                    public void onComplete() {
                        hideLoading();
                    }
                });
    }

    private void initCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_SUB_COMMENT);
        commentView.setSubType(ReplyAndCommentView.TYPE_SUB_MESSAGE);
        commentView.setApiPost(apiPost);
        commentView.setFromUserName("");
        commentView.setMainCommentid(commentId);
        commentView.setType(type);
        commentView.setTargetUserId(0);
//        commentView.setTargetUserMainId(commentData.getFrom_user_id());
        commentView.setNetAndListener();
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public String getDeviceInfo() {
        return null;
    }

    @Nullable
    @Override
    public String getUserInfo() {
        return JSONUtil.toJson(Constants.userInfoData);
    }

    @Override
    public void setUserInfo(@Nullable Object params) {

    }

    @Override
    public void toNativePage(@Nullable Object params) {

    }

    @Override
    public void previewImages(@Nullable Object params) {

    }

    @Override
    public void createMainComment(@Nullable Object params) {

    }

    @Override
    public void createSubComment(@Nullable Object params) {

    }

    @Override
    public void toggleClick(@Nullable Object params) {

    }

    @Nullable
    @Override
    public Object showConfirm(@Nullable Object params) {
        return null;
    }

    public static CommentDetailActivity getInstance() {
        return instance;
    }

    /**
     * 评论成功时执行该函数（由ReplyAndCommentView调用）
     * 将数据通过AppCallJS传递给模板
     */
    public void setCommentSuccessResult(ReplyCommentInfo info) {

    }

}

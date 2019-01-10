package com.riuir.calibur.ui.home.comment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import butterknife.BindView;
import calibur.core.http.models.comment.ReplyCommentInfo;
import calibur.core.http.models.comment.TrendingShowInfoCommentItem;
import calibur.core.http.models.jsbridge.models.H5CreateSubComment;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.JSONUtil;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Route(path = RouteUtils.commentDetailPath)
public class CommentDetailActivity extends BaseActivity implements IH5JsCallApp {

    private AthenaWebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;

    @BindView(R.id.comment_detail_back_btn)
    ImageView backBtn;
    @BindView(R.id.comment_view)
    ReplyAndCommentView commentView;
    @BindView(R.id.comment_detail_header_more)
    AppHeaderPopupWindows headerMore;

    @BindView(R.id.comment_detail_activity_loading_view)
    ImageView webPageLoadingView;
    private
    int commentId;
    String type;
    int reply_id;

    TrendingShowInfoCommentItem commentItem;

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
        reply_id = intent.getIntExtra("reply_id",0);

        initView();
        initWebView();
        TemplateRenderEngine.getInstance().checkCommentItemTemplateForUpdate();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = findViewById(R.id.comment_detail_webview);
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
        apiService.getCommentItem(type,commentId,reply_id)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        JSONObject jsonObj = new JSONObject((Map) data);
                        WebTemplatesUtils.loadTemplates(mWebView, TemplateRenderEngine.COMMENT, jsonObj.toString());
                        commentItem = JSONUtil.fromJson(jsonObj.toString(), TrendingShowInfoCommentItem.class);
                        initCommentView();
                        setHeaderMore();
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
        commentView.setSubType(ReplyAndCommentView.TYPE_SUB_COMMENT);
        commentView.setFromUserName("");
        commentView.setMainCommentid(commentId);
        commentView.setType(type);
        commentView.setTargetUserId(0);
        commentView.setTargetUserMainId(commentItem.getFrom_user_id());
        commentView.setNetAndListener();
    }

    private void setHeaderMore() {
        if (type.equals("post")){
            headerMore.setReportModelTag(AppHeaderPopupWindows.POST_COMMENT,commentItem.getId());
            headerMore.setDeleteLayout(AppHeaderPopupWindows.POST_COMMENT,commentItem.getId(),commentItem.getFrom_user_id()
                    ,0,apiPost);
        }else if (type.equals("image")){
            headerMore.setReportModelTag(AppHeaderPopupWindows.IMAGE_COMMENT,commentItem.getId());
            headerMore.setDeleteLayout(AppHeaderPopupWindows.IMAGE_COMMENT,commentItem.getId(),commentItem.getFrom_user_id()
                    ,0,apiPost);
        }else if (type.equals("score")){
            headerMore.setReportModelTag(AppHeaderPopupWindows.SCORE_COMMENT,commentItem.getId());
            headerMore.setDeleteLayout(AppHeaderPopupWindows.SCORE_COMMENT,commentItem.getId(),commentItem.getFrom_user_id()
                    ,0,apiPost);
        }else if (type.equals("video")){
            headerMore.setReportModelTag(AppHeaderPopupWindows.VIDEO_COMMENT,commentItem.getId());
            headerMore.setDeleteLayout(AppHeaderPopupWindows.VIDEO_COMMENT,commentItem.getId(),commentItem.getFrom_user_id()
                    ,0,apiPost);
        }
        headerMore.setOnDeleteFinish(new AppHeaderPopupWindows.OnDeleteFinish() {
            @Override
            public void deleteFinish() {
                finish();
            }
        });
        headerMore.setShowBtnWhite();
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (commentView.getIsClickedSubItem()){
            commentView.setClickToSubComment(0,"");
        }else {
            super.onBackPressed();
        }
    }

    @Nullable
    @Override
    public String getDeviceInfo() {
        return PhoneSystemUtils.getDeviceInfo();
    }

    @Nullable
    @Override
    public String getUserInfo() {
        return JSONUtil.toJson(Constants.userInfoData);
    }

    @Override
    public void createMainComment(@Nullable Object params) {

    }

    @Override
    public void createSubComment(@Nullable Object params) {
        if (params instanceof H5CreateSubComment){
            commentView.setJSToSubComment(((H5CreateSubComment) params).getModel_type(),
                    ((H5CreateSubComment) params).getParent_comment_id(),
                    ((H5CreateSubComment) params).getTarget_user_id(),
                    ((H5CreateSubComment) params).getTarget_user_name());
        }
    }

    @Override
    public void toggleClick(@Nullable Object params) {
    }

    @Override
    public void readNotification(@Nullable Object params) {
    }


    public static CommentDetailActivity getInstance() {
        return instance;
    }

    /**
     * 评论成功时执行该函数（由ReplyAndCommentView调用）
     * 将数据通过AppCallJS传递给模板
     */
    public void setCommentSuccessResult(ReplyCommentInfo info) {
        LogUtils.d("createSubComm","info = "+info.toString());
        mJavaScriptNativeBridge.callJavascript(IH5JsCallApp.createSubComment, info, null);
    }
}

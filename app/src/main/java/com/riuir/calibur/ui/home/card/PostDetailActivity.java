package com.riuir.calibur.ui.home.card;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import butterknife.BindView;
import calibur.core.http.models.comment.CreateMainCommentInfo;
import calibur.core.http.models.followList.post.CardShowInfoPrimacy;
import calibur.core.http.models.jsbridge.models.H5CreateMainComment;
import calibur.core.http.models.jsbridge.models.H5ToggleModel;
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
import com.riuir.calibur.ui.home.report.ReportActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.share.SharePopupActivity;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Route(path = RouteUtils.posterDetailPath)
public class PostDetailActivity extends BaseActivity implements IH5JsCallApp {

    private AthenaWebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;
    private int postId;

    @BindView(R.id.post_detail_back_btn)
    ImageView backBtn;

    @BindView(R.id.comment_view)
    ReplyAndCommentView commentView;
    @BindView(R.id.post_detail_header_more)
    ImageView headerCardMore;
    @BindView(R.id.post_detail_activity_loading_view)
    ImageView webPageLoadingView;

    private CardShowInfoPrimacy primacyData;


    private static PostDetailActivity instance;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void onInit() {
        instance = this;
        postId = getIntent().getIntExtra("cardID", 0);
        TemplateRenderEngine.getInstance().checkPostDetailPageTemplateForUpdate();
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

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = findViewById(R.id.post_detail_webview);
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
    protected void onLoadData() {
        showLoading();
        apiService.getPostDetailData(postId)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        JSONObject jsonObj = new JSONObject((Map) data);
                        WebTemplatesUtils.loadTemplates(mWebView, TemplateRenderEngine.POST, jsonObj.toString());
                        primacyData = JSONUtil.fromJson(jsonObj.toString(), CardShowInfoPrimacy.class);
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

    private void setHeaderMore() {
//        headerCardMore.setReportModelTag(AppHeaderPopupWindows.POST,primacyData.getPost().getId());
////        headerCardMore.setShareLayout(primacyData.getPost().getTitle(),AppHeaderPopupWindows.POST,primacyData.getPost().getId(),"");
//        headerCardMore.setShareLayout(PostDetailActivity.this,primacyData.getShare_data(),AppHeaderPopupWindows.POST);
//        headerCardMore.setDeleteLayout(AppHeaderPopupWindows.POST,primacyData.getPost().getId(),
//                primacyData.getUser().getId(),primacyData.getUser().getId(),apiPost);
//        headerCardMore.setOnDeleteFinish(new AppHeaderPopupWindows.OnDeleteFinish() {
//            @Override
//            public void deleteFinish() {
//                finish();
//            }
//        });
        headerCardMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailActivity.this,SharePopupActivity.class);
                intent.putExtra("share_data",primacyData.getShare_data());
                intent.putExtra("targetTag",SharePopupActivity.POST);
                intent.putExtra("targetId",primacyData.getPost().getId());
                intent.putExtra("targetUserId",primacyData.getUser().getId());
                intent.putExtra("postData",primacyData);
                startActivityForResult(intent,SharePopupActivity.SHARE_POPUP_REQUEST_CODE);
            }
        });
    }

    private void initCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_MAIN_COMMENT);
        commentView.setFromUserName("");
        commentView.setId(postId);
        commentView.setTitleId(primacyData.getUser().getId());
        commentView.setType(ReplyAndCommentView.TYPE_POST);
        commentView.setTargetUserId(0);
        commentView.setIs_creator(primacyData.getPost().isIs_creator());
        commentView.setLiked(primacyData.getPost().isLiked());
        commentView.setRewarded(primacyData.getPost().isRewarded());
        commentView.setMarked(primacyData.getPost().isMarked());
        commentView.setLikeCount(primacyData.getPost().getLike_users().getTotal());
        commentView.setMarkCount(primacyData.getPost().getMark_users().getTotal());
        commentView.setRewardCount(primacyData.getPost().getReward_users().getTotal());
        commentView.setOnLFCNetFinish(new ReplyAndCommentView.OnLFCNetFinish() {
            @Override
            public void onRewardFinish() {
                H5ToggleModel model = new H5ToggleModel();
                model.setAll("post","reward",primacyData.getPost().getId(),true);
                mJavaScriptNativeBridge.callJavascript("app-invoker-toggleClick",model,null);
            }

            @Override
            public void onLikeFinish(boolean isLike) {
                H5ToggleModel model = new H5ToggleModel();
                model.setAll("post","like",primacyData.getPost().getId(),true);
                mJavaScriptNativeBridge.callJavascript("app-invoker-toggleClick",model,null);
            }

            @Override
            public void onMarkFinish(boolean isMark) {
                H5ToggleModel model = new H5ToggleModel();
                model.setAll("post","mark",primacyData.getPost().getId(),true);
                mJavaScriptNativeBridge.callJavascript("app-invoker-toggleClick",model,null);
            }
        });
        commentView.setNetAndListener();
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @android.support.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SharePopupActivity.SHARE_POPUP_REQUEST_CODE){
            if (resultCode == SharePopupActivity.SHARE_POPUP_DELETE_RESULT_CODE) finish();
            if (resultCode == SharePopupActivity.SHARE_POPUP_POST_SETTING_RESULT_CODE){
                if (data!=null&&data.getSerializableExtra("postData")!=null){
                    primacyData = (CardShowInfoPrimacy) data.getSerializableExtra("postData");
                }
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void createMainComment(@Nullable Object params) {
        if (params instanceof H5CreateMainComment){
            commentView.toJumpBtn(((H5CreateMainComment) params).getModel_type(),
                    ((H5CreateMainComment) params).getModel_id());
        }

    }

    @Override
    public void createSubComment(@Nullable Object params) {
        LogUtils.d("postSetUserInfo","data = "+String.valueOf(params));
    }

    @Override
    public void toggleClick(@Nullable Object params) {
        LogUtils.d("toggleClick","data = "+String.valueOf(params));
        if (params instanceof H5ToggleModel){
            H5ToggleModel toggleModel = (H5ToggleModel) params;
            switch (toggleModel.getType()){
                case "reward":
                    commentView.setRewardedChange(toggleModel.getResult().isRewarded());
                    break;
                case "like":
                    break;
                case "mark":
                    break;
                case "follow":
                    break;
            }
        }
    }

    @Override
    public void readNotification(@Nullable Object params) {
    }

    public static PostDetailActivity getInstance() {
        return instance;
    }

    /**
     * 评论成功时执行该函数（由ReplyAndCommentView调用）
     * 将数据通过AppCallJS传递给模板
     */
    public void setCommentSuccessResult(CreateMainCommentInfo info) {
        mJavaScriptNativeBridge.callJavascript(IH5JsCallApp.createMainComment, info, null);
    }
}

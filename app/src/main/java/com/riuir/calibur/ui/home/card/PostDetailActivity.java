package com.riuir.calibur.ui.home.card;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import butterknife.BindView;
import calibur.core.http.models.comment.CreateMainCommentInfo;
import calibur.core.http.models.followList.post.CardShowInfoPrimacy;
import calibur.core.http.models.jsbridge.models.H5ShowConfirmModel;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
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
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.DialogHelper;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Route(path = RouteUtils.posterDetailPath)
public class PostDetailActivity extends BaseActivity implements IH5JsCallApp {

    private WebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;
    private int postId;

    @BindView(R.id.card_show_info_back_btn)
    ImageView backBtn;

    @BindView(R.id.comment_view)
    ReplyAndCommentView commentView;

    CardShowInfoPrimacy primacyData;

    private static PostDetailActivity instance;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void onInit() {
        instance = this;
        postId = getIntent().getIntExtra("cardID", 0);
        initWebView();
        initView();
        TemplateRenderEngine.getInstance().checkPostDetailPageTemplateForUpdate();
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

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = findViewById(R.id.post_detail_webview);
        mJavaScriptNativeBridge = new CommonJsBridgeImpl(this, new Handler(), this, mWebView);
        mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
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
        commentView.setOnLFCNetFinish(new ReplyAndCommentView.OnLFCNetFinish() {
            @Override
            public void onRewardFinish() {
//              trendingLFCView.setRewarded(true);
            }

            @Override
            public void onLikeFinish(boolean isLike) {
//              trendingLFCView.setLiked(isLike);
            }

            @Override
            public void onMarkFinish(boolean isMark) {
//              trendingLFCView.setCollected(isMark);
            }
        });
        commentView.setNetAndListener();
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

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override public Object getDeviceInfo() {
        return PhoneSystemUtils.getDeviceInfo();
    }

    @Override public Object getUserInfo() {
        return Constants.userInfoData;
    }

    @Override
    public void createMainComment(@Nullable Object params) {
        LogUtils.d("postSetUserInfo","data = "+String.valueOf(params));
    }

    @Override
    public void createSubComment(@Nullable Object params) {
        LogUtils.d("postSetUserInfo","data = "+String.valueOf(params));
    }

    @Override
    public void toggleClick(@Nullable Object params) {
        LogUtils.d("postSetUserInfo","data = "+String.valueOf(params));
    }

    @Nullable
    @Override
    public Object showConfirm(@Nullable Object params) {
        if (params instanceof H5ShowConfirmModel) {
            DialogHelper.getConfirmDialog(this,
                ((H5ShowConfirmModel) params).getTitle(),
                ((H5ShowConfirmModel) params).getMessage(),
                ((H5ShowConfirmModel) params).getCancelButtonText(),
                ((H5ShowConfirmModel) params).getSubmitButtonText(),
                false).show();
        }
        return null;
    }

    public static PostDetailActivity getInstance() {
        return instance;
    }

    /**
     * 评论成功时执行该函数（由ReplyAndCommentView调用）
     * 将数据通过AppCallJS传递给模板
     */
    public void setCommentSuccessResult(CreateMainCommentInfo info) {

    }
}

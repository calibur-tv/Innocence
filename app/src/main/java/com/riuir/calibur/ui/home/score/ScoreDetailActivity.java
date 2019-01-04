package com.riuir.calibur.ui.home.score;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import butterknife.BindView;
import calibur.core.http.models.comment.CreateMainCommentInfo;
import calibur.core.http.models.followList.score.ScoreShowInfoPrimacy;
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
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.utils.Constants;

import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Route(path = RouteUtils.scoreDetailPath)
public class ScoreDetailActivity extends BaseActivity implements IH5JsCallApp {

    private WebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;
    private int scoreID;

    @BindView(R.id.score_detail_back_btn)
    ImageView backBtn;

    @BindView(R.id.comment_view)
    ReplyAndCommentView commentView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    ScoreShowInfoPrimacy primacyData;

    private static ScoreDetailActivity instance;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_score_detail;
    }

    @Override
    protected void onInit() {
        instance = this;
        scoreID = getIntent().getIntExtra("scoreID", 0);
        initWebView();
        initView();

        TemplateRenderEngine.getInstance().checkReviewTemplateForUpdate();
    }

    private void initView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setLoadingView(refreshLayout);
        refreshLayout.setEnabled(false);
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = findViewById(R.id.score_detail_webview);
        mJavaScriptNativeBridge = new CommonJsBridgeImpl(this, new Handler(), this, mWebView);
        mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
    }

    private void initCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_MAIN_COMMENT);
        commentView.setFromUserName("");
        commentView.setId(scoreID);
        commentView.setTitleId(primacyData.getUser().getId());
        commentView.setType(ReplyAndCommentView.TYPE_SCORE);
        commentView.setTargetUserId(0);
        commentView.setIs_creator(primacyData.isIs_creator());
        commentView.setLiked(primacyData.isLiked());
        commentView.setRewarded(primacyData.isRewarded());
        commentView.setMarked(primacyData.isMarked());
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
        apiService.getScoreDetailData(scoreID)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Object>() {
                    @Override public void onSuccess(Object data) {
                        JSONObject jsonObj = new JSONObject((Map) data);
                        LogUtils.d("imagedetail","data = "+jsonObj.toString());
                        WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.REVIEW, jsonObj.toString());
                        primacyData = JSONUtil.fromJson(jsonObj.toString(),ScoreShowInfoPrimacy.class);
                        initCommentView();
                    }

                    @Override public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }

                    @Override public void onComplete() {
                        hideLoading();
                    }
                });
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
    public void createMainComment(@Nullable Object params) {

    }

    @Override
    public void createSubComment(@Nullable Object params) {

    }

    @Override
    public void toggleClick(@Nullable Object params) {

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static ScoreDetailActivity getInstance() {
        return instance;
    }

    /**
     * 评论成功时执行该函数（由ReplyAndCommentView调用）
     * 将数据通过AppCallJS传递给模板
     */
    public void setCommentSuccessResult(CreateMainCommentInfo info){
        mJavaScriptNativeBridge.callJavascript(IH5JsCallApp.createMainComment, info, null);
    }

}

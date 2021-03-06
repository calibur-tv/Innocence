package com.riuir.calibur.ui.home.image;

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
import calibur.core.http.models.followList.image.ImageShowInfoPrimacy;
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
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.dramaInfo.DramaInfoActivity;
import com.riuir.calibur.ui.home.card.PostDetailActivity;
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

@Route(path = RouteUtils.imageDetailPath)
public class ImageDetailActivity extends BaseActivity implements IH5JsCallApp {

    private AthenaWebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;
    private int imageID;

    @BindView(R.id.image_detail_back_btn)
    ImageView backBtn;
    @BindView(R.id.image_detail_header_more)
    ImageView headerMore;

    @BindView(R.id.comment_view)
    ReplyAndCommentView commentView;

    @BindView(R.id.image_detail_activity_loading_view)
    ImageView webPageLoadingView;

    ImageShowInfoPrimacy primacyData;

    private static ImageDetailActivity instance;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_image_detail;
    }

    @Override
    protected void onInit() {
        instance = this;
        imageID = getIntent().getIntExtra("imageID", 0);
        TemplateRenderEngine.getInstance().checkImageDetailPageTemplateForUpdate();
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
        mWebView = findViewById(R.id.image_detail_webview);
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
        apiService.getImageDetailData(imageID)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Object>() {
                    @Override public void onSuccess(Object data) {
                        JSONObject jsonObj = new JSONObject((Map) data);
                        WebTemplatesUtils.loadTemplates(mWebView,TemplateRenderEngine.IMAGEDETAIL, jsonObj.toString());
                        primacyData = JSONUtil.fromJson(jsonObj.toString(),ImageShowInfoPrimacy.class);
                        initCommentView();
                        setHeaderMore();
                    }

                    @Override public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }

                    @Override public void onComplete() {
                        hideLoading();
                    }
                });
    }

    private void initCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_MAIN_COMMENT);
        commentView.setFromUserName("");
        commentView.setId(imageID);
        commentView.setTitleId(primacyData.getUser().getId());
        commentView.setType(ReplyAndCommentView.TYPE_IMAGE);
        commentView.setTargetUserId(0);
        commentView.setIs_creator(primacyData.isIs_creator());
        commentView.setLiked(primacyData.isLiked());
        commentView.setRewarded(primacyData.isRewarded());
        commentView.setMarked(primacyData.isMarked());
        commentView.setLikeCount(primacyData.getLike_users().getTotal());
        commentView.setMarkCount(primacyData.getMark_users().getTotal());
        commentView.setRewardCount(primacyData.getReward_users().getTotal());

        commentView.setOnLFCNetFinish(new ReplyAndCommentView.OnLFCNetFinish() {
            @Override
            public void onRewardFinish() {
                H5ToggleModel model = new H5ToggleModel();
                model.setAll("image","reward",primacyData.getId(),true);
                mJavaScriptNativeBridge.callJavascript("app-invoker-toggleClick",model,null);
            }

            @Override
            public void onLikeFinish(boolean isLike) {
                H5ToggleModel model = new H5ToggleModel();
                model.setAll("image","like",primacyData.getId(),true);
                mJavaScriptNativeBridge.callJavascript("app-invoker-toggleClick",model,null);
            }

            @Override
            public void onMarkFinish(boolean isMark) {
                H5ToggleModel model = new H5ToggleModel();
                model.setAll("image","mark",primacyData.getId(),true);
                mJavaScriptNativeBridge.callJavascript("app-invoker-toggleClick",model,null);
            }
        });
        commentView.setNetAndListener();
    }

    private void setHeaderMore() {
        headerMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageDetailActivity.this,SharePopupActivity.class);
                intent.putExtra("share_data",primacyData.getShare_data());
                intent.putExtra("targetTag",SharePopupActivity.IMAGE);
                intent.putExtra("targetId",primacyData.getId());
                intent.putExtra("targetUserId",primacyData.getUser().getId());
                startActivityForResult(intent,SharePopupActivity.SHARE_POPUP_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
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

    }

    @Override
    public void toggleClick(@Nullable Object params) {
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public static ImageDetailActivity getInstance() {
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

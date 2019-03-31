package com.riuir.calibur.ui.share;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import calibur.share.util.QQBaseUiListener;
import calibur.share.util.QQShareUtils;
import calibur.share.util.WXShareUtils;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.dramaConfig.DramaMasterPostSettingActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.report.ReportActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.DialogHelper;

import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.delete.DeleteInfo;
import calibur.core.http.models.followList.post.CardShowInfoPrimacy;
import calibur.core.http.models.share.ShareDataModel;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.tencent.tauth.Tencent;
import io.reactivex.Observable;
import retrofit2.Response;

public class SharePopupActivity extends BaseActivity {

    @BindView(R.id.share_popup_share_wechat_btn)
    RelativeLayout wechatBtn;
    @BindView(R.id.share_popup_share_timeline_btn)
    RelativeLayout timelineBtn;
    @BindView(R.id.share_popup_share_qq_btn)
    RelativeLayout qqBtn;
    @BindView(R.id.share_popup_share_zone_btn)
    RelativeLayout zoneBtn;
    @BindView(R.id.share_popup_share_link_btn)
    RelativeLayout linkBtn;

    @BindView(R.id.share_popup_nice_btn)
    RelativeLayout niceBtn;
    @BindView(R.id.share_popup_top_btn)
    RelativeLayout topBtn;
    @BindView(R.id.share_popup_nice_icon)
    ImageView niceIcon;
    @BindView(R.id.share_popup_top_icon)
    ImageView topIcon;
    @BindView(R.id.share_popup_nice_text)
    TextView niceText;
    @BindView(R.id.share_popup_top_text)
    TextView topText;

    @BindView(R.id.share_popup_report_btn)
    RelativeLayout reportBtn;
    @BindView(R.id.share_popup_delete_btn)
    RelativeLayout deleteBtn;

    @BindView(R.id.share_popup_delete_btn_text)
    TextView deleteBtnText;

    @BindView(R.id.share_popup_activity_empty)
    RelativeLayout emptyLayout;
    @BindView(R.id.share_popup_cancel_btn)
    TextView cancelBtn;

    public static final int SHARE_POPUP_REQUEST_CODE = 666;
    public static final int SHARE_POPUP_DELETE_RESULT_CODE = 667;
    public static final int SHARE_POPUP_POST_SETTING_RESULT_CODE = 668;

    public static final String USER = "user";
    public static final String BANGUMI = "bangumi";
    public static final String VIDEO = "video";
    public static final String ROLE = "role";
    public static final String POST = "post";
    public static final String IMAGE = "image";
    public static final String SCORE = "score";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String POST_COMMENT = "post_comment";
    public static final String IMAGE_COMMENT = "image_comment";
    public static final String SCORE_COMMENT = "score_comment";
    public static final String VIDEO_COMMENT = "video_comment";
    public static final String QUESTION_COMMENT = "question_comment";
    public static final String ANSWER_COMMENT = "answer_comment";
    public static final String POST_REPLY = "post_reply";
    public static final String IMAGE_REPLY = "image_reply";
    public static final String SCORE_REPLY = "score_reply";
    public static final String VIDEO_REPLY = "video_reply";
    public static final String QUESTION_REPLY = "question_reply";
    public static final String ANSWER_REPLY = "answer_reply";
    public static final String ROLE_REPLY = "role_reply";

    ShareDataModel shareData;

    private String targetTag;
    private int targetId;
    private int targetUserId;
    private CardShowInfoPrimacy postData;
    private boolean postIsNice;
    private boolean postIsTop;

    WXShareUtils wxShareUtils;

    AlertDialog deleteDialog;

    private Observable<Response<ResponseBean<DeleteInfo>>> deleteApi;
    private Observable<Response<ResponseBean<String>>> settingApi;

    private static final String POST_NICE = "post_nice";
    private static final String POST_TOP = "post_top";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_share_popup;
    }

    @Override
    protected void onInit() {
        wxShareUtils = new WXShareUtils(this);
        wxShareUtils.register();
        Intent intent = getIntent();
        shareData = (ShareDataModel) intent.getSerializableExtra("share_data");
        targetTag = intent.getStringExtra("targetTag");
        targetId = intent.getIntExtra("targetId",0);
        targetUserId = intent.getIntExtra("targetUserId",0);
        postData = (CardShowInfoPrimacy) intent.getSerializableExtra("postData");
        setPostSetting();
        setViewVisibility();
        setListener();
    }

    private void setPostSetting(){
        if (targetTag.equals(POST)&&postData!=null&&postData.getBangumi().isIs_master()){
            niceBtn.setVisibility(View.VISIBLE);
            topBtn.setVisibility(View.VISIBLE);
            if (postData.getPost().isIs_nice()){
                postIsNice = true;
            }else {
                postIsNice = false;
            }
            if (postData.getPost().isIs_top()){
                postIsTop = true;
            }else {
                postIsTop = false;
            }
            setPostNice(postIsNice);
            setPostTop(postIsTop);
        }else {
            niceBtn.setVisibility(View.GONE);
            topBtn.setVisibility(View.GONE);
        }
    }

    private void setPostNice(boolean isNice){
        if (isNice){
            niceIcon.setImageDrawable(getResources().getDrawable(R.mipmap.share_icon_star_active));
            niceText.setText("已加精");
            niceText.setTextColor(getResources().getColor(R.color.theme_magic_sakura_primary));
        }else {
            niceIcon.setImageDrawable(getResources().getDrawable(R.mipmap.share_icon_star));
            niceText.setText("加精");
            niceText.setTextColor(getResources().getColor(R.color.color_text_gray));
        }
        postData.getPost().setIs_nice(isNice);
        niceBtn.setClickable(true);
    }
    private void setPostTop(boolean isTop){
        if (isTop){
            topIcon.setImageDrawable(getResources().getDrawable(R.mipmap.share_icon_up_active));
            topText.setText("已置顶");
            topText.setTextColor(getResources().getColor(R.color.theme_magic_sakura_primary));
        }else {
            topIcon.setImageDrawable(getResources().getDrawable(R.mipmap.share_icon_up));
            topText.setText("置顶");
            topText.setTextColor(getResources().getColor(R.color.color_text_gray));
        }
        postData.getPost().setIs_top(isTop);
        topBtn.setClickable(true);
    }

    private void setViewVisibility() {
        if (targetTag.equals(USER)||
                targetTag.equals(BANGUMI)||
                targetTag.equals(ROLE)){
            //这三者模式没有删除按钮
            deleteBtn.setVisibility(View.GONE);

            if (targetTag.equals(USER)){
                //通过targetId判断用户页面目标是否是用户自己
                if (Constants.userInfoData.getId() == targetId){
                    reportBtn.setVisibility(View.GONE);
                }else {
                    reportBtn.setVisibility(View.VISIBLE);
                }
            }else {
                if (Constants.userInfoData.getId() == targetUserId){
                    reportBtn.setVisibility(View.GONE);
                }else {
                    deleteBtn.setVisibility(View.GONE);
                }
            }
        }else {
            if (Constants.userInfoData.getId() == targetUserId){
                deleteBtn.setVisibility(View.VISIBLE);
                reportBtn.setVisibility(View.GONE);
            }else {
                deleteBtn.setVisibility(View.GONE);
                reportBtn.setVisibility(View.VISIBLE);
            }
        }

        setDeleteDialog();
        setDeleteCall(targetTag,targetId);
    }

    @Override
    protected void onStop() {
        wxShareUtils.unregister();
        super.onStop();
    }

    private void setListener() {
        qqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QQShareUtils.toShareQQ(getActivity(),shareData);
            }
        });
        zoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QQShareUtils.toShareQzone(getActivity(),shareData);
            }
        });
        wechatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wxShareUtils.toShareWX(shareData);
            }
        });
        timelineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wxShareUtils.toShareWXTimeline(shareData);
            }
        });
        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = shareData.getLink();
                // 获取系统剪贴板
                final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clipUrl = ClipData.newPlainText("calibur", url);
                 //把数据集设置（复制）到剪贴板
                clipboard.setPrimaryClip(clipUrl);
                ToastUtils.showLong(getActivity(),"内容链接已复制到粘贴板");
                finish();
            }
        });

        niceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPostNiceNet(String.valueOf(postData.getPost().getId()));
            }
        });
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPostTopNet(String.valueOf(postData.getPost().getId()));
            }
        });
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ReportActivity.class);
                intent.putExtra("reportModel",targetTag);
                intent.putExtra("reportId",targetId);
                startActivity(intent);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.show();
            }
        });

        emptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setPostNiceNet(String id) {

        if (postIsNice){
            settingApi = apiService.getCallPostNiceRemove(id);
            niceText.setText("取消中");
        }else {
            settingApi = apiService.getCallPostNiceSet(id);
            niceText.setText("加精中");
        }
        niceBtn.setClickable(false);
        settingApi.compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<String>(){
                    @Override
                    public void onSuccess(String s) {
//                        ToastUtils.showLong(SharePopupActivity.this,s);
                        postIsNice = !postIsNice;
                        setPostNice(postIsNice);
                        Intent intent = new Intent();
                        intent.putExtra("postData",postData);
                        setResult(SHARE_POPUP_POST_SETTING_RESULT_CODE,intent);
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setPostNice(postIsNice);
                    }
                });
    }

    private void setPostTopNet(String id) {

        if (postIsTop){
            settingApi = apiService.getCallPostTopRemove(id);
            topText.setText("取消中");
        }else {
            settingApi = apiService.getCallPostTopSet(id);
            topText.setText("置顶中");
        }
        topBtn.setClickable(false);
        settingApi.compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<String>(){
                    @Override
                    public void onSuccess(String s) {
//                        ToastUtils.showLong(SharePopupActivity.this,s);
                        postIsTop = !postIsTop;
                        setPostTop(postIsTop);
                        Intent intent = new Intent();
                        intent.putExtra("postData",postData);
                        setResult(SHARE_POPUP_POST_SETTING_RESULT_CODE,intent);
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setPostTop(postIsTop);
                    }
                });
    }

    private void setDeleteCall(String modelTag,int id) {
        switch (modelTag) {
            case POST:
                deleteApi = apiService.getCallDeletePost(id);
                break;
            case IMAGE:
                deleteApi = apiService.getCallDeleteAlbum(id);
                break;
            case SCORE:
                deleteApi = apiService.getCallDeleteScore(id);
                break;
            case POST_COMMENT:
                deleteApi = apiService.getCallDeleteCommentMain("post", id);
                break;
            case IMAGE_COMMENT:
                deleteApi = apiService.getCallDeleteCommentMain("image", id);
                break;
            case SCORE_COMMENT:
                deleteApi = apiService.getCallDeleteCommentMain("score", id);
                break;
            case VIDEO_COMMENT:
                deleteApi = apiService.getCallDeleteCommentMain("video", id);
                break;
            case POST_REPLY:
                deleteApi = apiService.getCallDeleteCommentChild("post", id);
                break;
            case IMAGE_REPLY:
                deleteApi = apiService.getCallDeleteCommentChild("image", id);
                break;
            case SCORE_REPLY:
                deleteApi = apiService.getCallDeleteCommentChild("score", id);
                break;
            case VIDEO_REPLY:
                deleteApi = apiService.getCallDeleteCommentChild("video", id);
                break;
            default:
                deleteApi = null;
                break;
        }
    }

    private void setDeleteDialog() {
        deleteDialog = DialogHelper.getConfirmDialog(getActivity(),
                "删除",
                "确定删除吗？",
                "確定",
                "取消",
                false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteBtn.setClickable(false);
                        deleteBtnText.setText("删除中");
                        delete();
                        deleteDialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteDialog.dismiss();
                    }
                }
        ).create();
    }

    private void delete() {
        if (deleteApi ==null){
            ToastUtils.showShort(getActivity(),"数据类型出错，无法删除");
        }else {
            deleteApi.compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<DeleteInfo>(){
                        @Override
                        public void onSuccess(DeleteInfo deleteInfo) {
                            Intent intent = new Intent(MineFragment.EXPCHANGE);
                            ToastUtils.showShort(getActivity(),deleteInfo.getMessage());
                            intent.putExtra("expChangeNum",deleteInfo.getExp());
                            App.instance().sendBroadcast(intent);
                            deleteBtn.setClickable(true);
                            deleteBtnText.setText("删除");
                            setResult(SHARE_POPUP_DELETE_RESULT_CODE);
                            finish();
                        }
                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (deleteBtn!=null){
                                deleteBtn.setClickable(true);
                                deleteBtnText.setText("删除");
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data,new QQBaseUiListener());
        super.onActivityResult(requestCode, resultCode, data);
    }


    private Activity getActivity(){
        return SharePopupActivity.this;
    }

}

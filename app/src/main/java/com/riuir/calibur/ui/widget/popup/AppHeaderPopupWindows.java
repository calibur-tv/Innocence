package com.riuir.calibur.ui.widget.popup;

import android.app.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.AnimeShowInfo;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.create.DeleteInfo;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.ui.home.Drama.dramaConfig.DramaMasterConfigActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.web.WebViewActivity;
import com.riuir.calibur.utils.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppHeaderPopupWindows extends RelativeLayout {

    Context context;
    Activity activity;
    View view;
    View popupView;
    ImageView showBtn;
    PopupWindow popupWindow;
    LinearLayout reportLayout;
    TextView reportBtn;
    TextView shareBtn;
    LinearLayout shareLayout;
    TextView urlBtn;
    LinearLayout urlLayout;
    TextView onlySeeMasterBtn;
    LinearLayout onlySeeMasterLayout;
    TextView deleteBtn;
    LinearLayout deleteLayout;
    TextView masterBtn;
    LinearLayout masterLayout;
    TextView masterConfigBtn;
    LinearLayout masterConfigLayout;
    TextView cancelBtn;
    LayoutInflater layoutInflater;
    AlertDialog deleteDialog;

    private WindowManager.LayoutParams params;

    private Call<DeleteInfo> deleteCall;
    private String deleteModelTag;
//post_reply, image_reply, score_reoly, video_reply, question_reply, answer_reply, role_reply
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

    private boolean isShowing = false;

    private OnDeleteFinish onDeleteFinish;


    public AppHeaderPopupWindows(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public AppHeaderPopupWindows(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public AppHeaderPopupWindows(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }


    private void initView() {
        activity = (Activity) context;
        params = activity.getWindow().getAttributes();
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.app_header_popup_window_layout,this,true);
        showBtn = view.findViewById(R.id.app_header_popup_window_btn);
        setPopup();
        showBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               popup();
            }
        });

    }
    private void setPopup() {
//        app_report_popup_window_item_report
        popupView = layoutInflater.inflate(R.layout.app_report_popup_layout,null,false);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        reportLayout = popupView.findViewById(R.id.app_report_popup_window_item_report_layout);
        reportBtn = popupView.findViewById(R.id.app_report_popup_window_item_report);
        shareLayout = popupView.findViewById(R.id.app_report_popup_window_item_share_layout);
        shareBtn = popupView.findViewById(R.id.app_report_popup_window_item_share);
        urlLayout = popupView.findViewById(R.id.app_report_popup_window_item_url_layout);
        urlBtn = popupView.findViewById(R.id.app_report_popup_window_item_url);
        onlySeeMasterLayout = popupView.findViewById(R.id.app_report_popup_window_item_only_see_master_layout);
        onlySeeMasterBtn = popupView.findViewById(R.id.app_report_popup_window_item_only_see_master);
        deleteLayout = popupView.findViewById(R.id.app_report_popup_window_item_delete_layout);
        deleteBtn = popupView.findViewById(R.id.app_report_popup_window_item_delete);
        masterLayout = popupView.findViewById(R.id.app_report_popup_window_item_master_layout);
        masterBtn = popupView.findViewById(R.id.app_report_popup_window_item_master);
        masterConfigLayout = popupView.findViewById(R.id.app_report_popup_window_item_master_config_layout);
        masterConfigBtn = popupView.findViewById(R.id.app_report_popup_window_item_master_config);
        cancelBtn = popupView.findViewById(R.id.app_report_popup_window_item_cancel);
//        android:background="@drawable/bg_popup_window_up"
        // 设置PopupWindow的背景
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_popup_window_white));
        // 设置PopupWindow是否能响应外部点击事件
        popupWindow.setOutsideTouchable(true);
           // 设置PopupWindow是否能响应点击事件,具体是其中的item的响应事件
        popupWindow.setTouchable(true);
        //popup消失监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isShowing = false;
                params.alpha= 1f;
                activity.getWindow().setAttributes(params);
            }
        });
        //取消按钮监听
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
    // 显示PopupWindow
    public void popup(){
        LogUtils.d("childCommentId","11111111111");
        if(popupWindow == null){
            setPopup();
        }
        if(!isShowing){
            params.alpha= 0.7f;
            activity.getWindow().setAttributes(params);
            popupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.BOTTOM,0,0);
            isShowing = true;
        }
    }

    private void dismiss(){
        if(popupWindow != null &&isShowing){
            popupWindow.dismiss();
            isShowing = false;
            params.alpha= 1f;
            activity.getWindow().setAttributes(params);
        }
    }

    public void setReportModelTag(final String reportModelTag, final int reportId){


        if (reportModelTag.equals(USER)|| reportModelTag.equals(BANGUMI)
                ||reportModelTag.equals(POST)||reportModelTag.equals(IMAGE)
                || reportModelTag.equals(SCORE)||reportModelTag.equals(ROLE)){
        }else {
            showBtn.setImageDrawable(getResources().getDrawable(R.mipmap.app_btn_more_gray));
        }

        reportBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("reportModel",reportModelTag);
                intent.putExtra("reportId",reportId);
                intent.setAction("calibur.activity.report");
                context.startActivity(intent);
                popupWindow.dismiss();
            }
        });
    }

    //设置分享和复制链接
    public void setShareLayout(String title,String modelTag,int id,String zone){
        shareLayout.setVisibility(VISIBLE);
        if (modelTag.equals(POST)||modelTag.equals(IMAGE)||modelTag.equals(SCORE)){
            urlLayout.setVisibility(VISIBLE);
        }

        String url = "";
        if (modelTag.equals(POST)){
            url = "https://www.calibur.tv/post/"+id;
        }else if (modelTag.equals(IMAGE)){
            url = "https://www.calibur.tv/pin/"+id;
        }else if (modelTag.equals(SCORE)){
            url = "https://www.calibur.tv/review/"+id;
        }else if (modelTag.equals(ROLE)){
            url = "https://www.calibur.tv/role/"+id;
        }else if (modelTag.equals(BANGUMI)){
            url = "https://www.calibur.tv/bangumi/"+id;
        }else if (modelTag.equals(USER)){
            url = "https://www.calibur.tv/user/"+zone;
        }else {
            url = "https://www.calibur.tv/";
        }

        String shareText;
        if (Constants.userInfoData!=null){
            shareText = "来自【calibur.tv】用户:"+Constants.userInfoData.getNickname()+" 的分享：["+title+"]["+url+"]";
        }else {
            shareText = "来自【calibur.tv】 的分享：["+title+"]["+url+"]";
        }

        // 获取系统剪贴板
        final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
        final ClipData clipData = ClipData.newPlainText("calibur", shareText);
        final ClipData clipUrl = ClipData.newPlainText("calibur", url);
        shareBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 把数据集设置（复制）到剪贴板
                clipboard.setPrimaryClip(clipData);
                ToastUtils.showLong(context,"分享数据已复制到粘贴板");
                popupWindow.dismiss();
            }
        });
        //设置复制链接

        urlBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 把数据集设置（复制）到剪贴板
                clipboard.setPrimaryClip(clipUrl);
                ToastUtils.showLong(context,"内容链接已复制到粘贴板");
                popupWindow.dismiss();
            }
        });

    }

    public void setOnlySeeMasterClick(OnClickListener onClickListener){
        onlySeeMasterBtn.setOnClickListener(onClickListener);
    }
    //该方法在setOnlySeeMasterClick传进来的对应的listener中调用
    public void setIsOnlySeeMaster(boolean isOnlySee){
        if (isOnlySee){
            onlySeeMasterBtn.setText("取消只看楼主");
        }else {
            onlySeeMasterBtn.setText("只看楼主");
        }
        popupWindow.dismiss();
    }
    public void initOnlySeeMaster(String modelTag){
        if (modelTag.equals(POST)||modelTag.equals(IMAGE)
                || modelTag.equals(SCORE)){
            onlySeeMasterLayout.setVisibility(VISIBLE);
        }else {
            onlySeeMasterLayout.setVisibility(GONE);
        }
    }

    public void setDeleteLayout(String modelTag, int modelId, int userId,int primacyUserId,ApiPost apiPost){
        if (Constants.ISLOGIN&&Constants.userInfoData!=null){

            if (userId == Constants.userInfoData.getId()&&
                    (modelTag.equals(POST)||modelTag.equals(IMAGE) || modelTag.equals(SCORE)
                            ||modelTag.equals(POST_COMMENT)||modelTag.equals(IMAGE_COMMENT)
                            || modelTag.equals(SCORE_COMMENT)|| modelTag.equals(VIDEO_COMMENT)
                            ||modelTag.equals(POST_REPLY)||modelTag.equals(IMAGE_REPLY)
                            || modelTag.equals(SCORE_REPLY)|| modelTag.equals(VIDEO_REPLY))){

                deleteLayout.setVisibility(VISIBLE);
                deleteModelTag = modelTag;
                setDeleteCall(modelTag,modelId,apiPost);
                setDeleteDialog();
                deleteBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteBtn.setClickable(false);
                        deleteDialog.show();
                    }
                });
            }else if (primacyUserId == Constants.userInfoData.getId()){
                deleteLayout.setVisibility(VISIBLE);
                deleteModelTag = modelTag;
                setDeleteCall(modelTag,modelId,apiPost);
                setDeleteDialog();
                deleteBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteBtn.setClickable(false);
                        deleteDialog.show();
                    }
                });
            }else {
                deleteLayout.setVisibility(GONE);
            }
        }
    }

    private void setDeleteCall(String modelTag,int id,ApiPost apiPost) {
        if (modelTag.equals(POST)){
            deleteCall = apiPost.getCallDeletePost(id);
        }else if (modelTag.equals(IMAGE)){
            deleteCall = apiPost.getCallDeleteAlbum(id);
        }else if (modelTag.equals(SCORE)){
            deleteCall = apiPost.getCallDeleteScore(id);
        }else if (modelTag.equals(POST_COMMENT)){
            deleteCall = apiPost.getCallDeleteCommentMain("post",id);
        }else if (modelTag.equals(IMAGE_COMMENT)){
            deleteCall = apiPost.getCallDeleteCommentMain("image",id);
        }else if ( modelTag.equals(SCORE_COMMENT)) {
            deleteCall = apiPost.getCallDeleteCommentMain("score",id);
        }else if ( modelTag.equals(VIDEO_COMMENT)) {
            deleteCall = apiPost.getCallDeleteCommentMain("video",id);
        }else if (modelTag.equals(POST_REPLY)){
            deleteCall = apiPost.getCallDeleteCommentChild("post",id);
        }else if (modelTag.equals(IMAGE_REPLY)){
            deleteCall = apiPost.getCallDeleteCommentChild("image",id);
        }else if ( modelTag.equals(SCORE_REPLY)) {
            deleteCall = apiPost.getCallDeleteCommentChild("score",id);
        }else if ( modelTag.equals(VIDEO_REPLY)) {
            deleteCall = apiPost.getCallDeleteCommentChild("video",id);
        }else {
            deleteCall = null;
        }
    }

    private void setDeleteDialog() {
        deleteDialog = new AlertDialog.Builder(context)
                .setTitle("删除")
                .setMessage("确定删除吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBtn.setClickable(true);
                        deleteDialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBtn.setText("删除中");
                        delete();
                        deleteDialog.dismiss();
                    }
                })
                .create();
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                deleteBtn.setClickable(true);
            }
        });
    }

    private void delete() {
        if (deleteCall ==null){
            ToastUtils.showShort(context,"数据类型出错，无法删除");
        }else {
            deleteCall.enqueue(new Callback<DeleteInfo>() {
                @Override
                public void onResponse(Call<DeleteInfo> call, Response<DeleteInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        if (activity!=null){
                            Intent intent = new Intent(MineFragment.EXPCHANGE);
                            ToastUtils.showShort(context,response.body().getData().getMessage());
                            intent.putExtra("expChangeNum",response.body().getData().getExp());
                            context.sendBroadcast(intent);
                            deleteBtn.setClickable(true);
                            deleteBtn.setText("删除");
                            popupWindow.dismiss();
                            if (onDeleteFinish!=null){
                                onDeleteFinish.deleteFinish();
                            }

                        }
                    }else if (response!=null&&!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);
                        if (deleteBtn!=null){
                            ToastUtils.showShort(context,info.getMessage());
                            deleteBtn.setClickable(true);
                            deleteBtn.setText("删除");
                        }
                    }else{
                        if (deleteBtn!=null){
                            ToastUtils.showShort(context,"异常原因导致删除失败");
                            deleteBtn.setClickable(true);
                            deleteBtn.setText("删除");
                        }
                    }
                }

                @Override
                public void onFailure(Call<DeleteInfo> call, Throwable t) {
                    if (deleteBtn!=null){
                        ToastUtils.showShort(context,"请检查您的网络");
                        deleteBtn.setClickable(true);
                        deleteBtn.setText("删除");
                    }
                }
            });
        }
    }

    public void setMasterLayout(boolean isMaster, final int webViewIndex, final int bangumi_id, final AnimeShowInfo.AnimeShowInfoData animeShowInfoData){
        if (isMaster){
//            masterConfigLayout.setVisibility(VISIBLE);
//            masterLayout.setVisibility(GONE);
//            masterConfigBtn.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, DramaMasterConfigActivity.class);
//                    intent.putExtra("bangumi_id",bangumi_id);
//                    intent.putExtra("animeShowInfoData",animeShowInfoData);
//                    activity.startActivity(intent);
//                    popupWindow.dismiss();
//                }
//            });
        }else {
            masterLayout.setVisibility(VISIBLE);
            masterConfigLayout.setVisibility(GONE);
            masterBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("type",WebViewActivity.TYPE_RULE);
                    intent.putExtra("index",webViewIndex);
                    activity.startActivity(intent);
                    popupWindow.dismiss();
                }
            });
        }

    }

    public void setOnDeleteFinish(OnDeleteFinish onDeleteFinish){
        this.onDeleteFinish = onDeleteFinish;
    }

    public interface OnDeleteFinish{
        void deleteFinish();
    }

}

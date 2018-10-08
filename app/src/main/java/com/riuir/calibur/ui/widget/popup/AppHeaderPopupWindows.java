package com.riuir.calibur.ui.widget.popup;

import android.app.Activity;

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
import com.riuir.calibur.data.Event;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.ui.home.MineFragment;
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
    TextView reportBtn;
    TextView onlySeeMasterBtn;
    LinearLayout onlySeeMasterLayout;
    TextView deleteBtn;
    LinearLayout deleteLayout;
    TextView cancelBtn;
    LayoutInflater layoutInflater;
    AlertDialog deleteDialog;

    private WindowManager.LayoutParams params;

    private Call<Event<String>> deleteCall;
    private String deleteModelTag;

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

    private boolean isShowing = false;




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
        popupView = layoutInflater.inflate(R.layout.app_report_popup_layout,null,false);
        reportBtn = popupView.findViewById(R.id.app_report_popup_window_item_report);
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
        reportBtn = popupView.findViewById(R.id.app_report_popup_window_item_report);
        onlySeeMasterLayout = popupView.findViewById(R.id.app_report_popup_window_item_only_see_master_layout);
        onlySeeMasterBtn = popupView.findViewById(R.id.app_report_popup_window_item_only_see_master);
        deleteLayout = popupView.findViewById(R.id.app_report_popup_window_item_delete_layout);
        deleteBtn = popupView.findViewById(R.id.app_report_popup_window_item_delete);
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
    private void popup(){
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
                || reportModelTag.equals(SCORE)){
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

    public void setDeleteLayout(String modelTag, int modelId, int userId,ApiPost apiPost){
        if (Constants.ISLOGIN&&Constants.userInfoData!=null&&
                userId == Constants.userInfoData.getId()&&
                (modelTag.equals(POST)||modelTag.equals(IMAGE)
                        || modelTag.equals(SCORE))){
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

    private void setDeleteCall(String modelTag,int id,ApiPost apiPost) {
        if (modelTag.equals(POST)){
            deleteCall = apiPost.getCallDeletePost(id);
        }else if (modelTag.equals(IMAGE)){
            deleteCall = apiPost.getCallDeleteAlbum(id);
        }else if (modelTag.equals(SCORE)){
            deleteCall = apiPost.getCallDeleteScore(id);
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
                        deleteDialog.dismiss();
                        deleteBtn.setText("删除中");
                        delete();
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
            deleteCall.enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    if (response!=null&&response.isSuccessful()){
                        if (activity!=null){
                            Intent intent = new Intent(MineFragment.EXPCHANGE);
                            if (deleteModelTag.equals(POST)){
                                ToastUtils.showShort(context,"删除帖子，经验-4");
                                intent.putExtra("expChangeNum",-4);
                            }else if (deleteModelTag.equals(IMAGE)){
                                ToastUtils.showShort(context,"删除图片/相册，经验-3");
                                intent.putExtra("expChangeNum",-3);
                            }else if (deleteModelTag.equals(SCORE)){
                                ToastUtils.showShort(context,"删除漫评，经验-5");
                                intent.putExtra("expChangeNum",-5);
                            }else {
                                ToastUtils.showShort(context,"删除成功");
                                intent.putExtra("expChangeNum",0);
                            }
                            context.sendBroadcast(intent);
                            activity.finish();
                        }
                    }else if (response!=null&&response.isSuccessful()){
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
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    if (deleteBtn!=null){
                        ToastUtils.showShort(context,"请检查您的网络");
                        deleteBtn.setClickable(true);
                        deleteBtn.setText("删除");
                    }
                }
            });
        }
    }

}

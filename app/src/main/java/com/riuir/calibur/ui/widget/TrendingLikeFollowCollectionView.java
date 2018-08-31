package com.riuir.calibur.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.data.trending.TrendingToggleInfo;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.ui.loginAndRegister.LoginActivity;
import com.riuir.calibur.utils.Constants;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendingLikeFollowCollectionView extends RelativeLayout {

    Button likeCheckBtn;
    Button collectionCheckBtn;
    Button rewardedBtn;



    String type;
    ApiPost apiPost;
    int id;

    boolean rewarded;
    boolean isCreator;
    boolean collected;
    boolean liked;

    Drawable leftDrawLightLike;
    Drawable leftDrawDarkLike;

    Drawable leftDrawLightcollection;
    Drawable leftDrawDarkcollection;

    Context context;

    private static final int NET_STATUS_TOGGLE_LIKE = 0;
    private static final int NET_STATUS_TOGGLE_COLLENTION = 1;
    private static final int NET_STATUS_TOGGLE_REWARDED = 2;

    public TrendingLikeFollowCollectionView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public TrendingLikeFollowCollectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public TrendingLikeFollowCollectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.trending_like_follow_collection_view, this, true);
        likeCheckBtn = view.findViewById(R.id.trending_header_like_checkBtn);
        collectionCheckBtn = view.findViewById(R.id.trending_header_collection_checkBtn);
        rewardedBtn = view.findViewById(R.id.trending_header_rewarded_btn);



    }

    private void setListener() {
        if (isCreator){
            likeCheckBtn.setVisibility(GONE);
            rewardedBtn.setVisibility(VISIBLE);
        }else {
            rewardedBtn.setVisibility(GONE);
            likeCheckBtn.setVisibility(VISIBLE);
        }
        leftDrawLightLike = getResources().getDrawable(R.mipmap.card_show_header_like_checked);
        leftDrawDarkLike = getResources().getDrawable(R.mipmap.card_show_header_like_normal);

        leftDrawDarkcollection = getResources().getDrawable(R.mipmap.card_show_header_star_normal);
        leftDrawLightcollection = getResources().getDrawable(R.mipmap.card_show_header_star_checked);

        leftDrawLightLike.setBounds(0,0,leftDrawLightLike.getMinimumWidth(),leftDrawLightLike.getMinimumHeight());
        leftDrawDarkLike.setBounds(0,0,leftDrawDarkLike.getMinimumWidth(),leftDrawDarkLike.getMinimumHeight());

        leftDrawDarkcollection.setBounds(0,0,leftDrawDarkcollection.getMinimumWidth(),leftDrawDarkcollection.getMinimumHeight());
        leftDrawLightcollection.setBounds(0,0,leftDrawLightcollection.getMinimumWidth(),leftDrawLightcollection.getMinimumHeight());

        if (rewarded){
            rewardedBtn.setCompoundDrawables(leftDrawLightLike,null,null,null);
        }else {
            rewardedBtn.setCompoundDrawables(leftDrawDarkLike,null,null,null);
        }

        if (collected){
            collectionCheckBtn.setCompoundDrawables(leftDrawLightcollection,null,null,null);
        }else {
            collectionCheckBtn.setCompoundDrawables(leftDrawDarkcollection,null,null,null);
        }

        if (liked){
            likeCheckBtn.setCompoundDrawables(leftDrawLightLike,null,null,null);
        }else {
            likeCheckBtn.setCompoundDrawables(leftDrawDarkLike,null,null,null);
        }

        likeCheckBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.ISLOGIN){
                    setNetToToggle(NET_STATUS_TOGGLE_LIKE);
                    likeCheckBtn.setClickable(false);
                    likeCheckBtn.setText("喜欢中");
                }else {
                    LoginUtils.ReLogin(context);
                }
            }
        });

        rewardedBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.ISLOGIN){
                    if (rewarded){
                        ToastUtils.showShort(context,"只能打赏一次哦，您已经打赏过了，请勿重复打赏~");
                    }else {

                        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                        dialog.setTitleText("打赏")
                                .setContentText("确认打赏将会消耗您1金币哦")
                                .setCancelText("取消")
                                .setConfirmText("确定")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.cancel();
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        rewardedBtn.setClickable(false);
                                        sweetAlertDialog.cancel();
                                        setNetToToggle(NET_STATUS_TOGGLE_REWARDED);
                                        rewardedBtn.setText("打赏中");
                                    }
                                })
                                .show();
                    }
                }else {
                    LoginUtils.ReLogin(context);
                }
            }
        });

        collectionCheckBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.ISLOGIN){
                    setNetToToggle(NET_STATUS_TOGGLE_COLLENTION);
                    collectionCheckBtn.setClickable(false);
                    collectionCheckBtn.setText("收藏中");
                }else {
                    LoginUtils.ReLogin(context);
                }
            }
        });
    }


    private void setNetToToggle(int NET_TOGGLE_STATUS) {

        if (NET_TOGGLE_STATUS == NET_STATUS_TOGGLE_LIKE){
            apiPost.getTrendingToggleLike(type,id).enqueue(new Callback<TrendingToggleInfo>() {
                @Override
                public void onResponse(Call<TrendingToggleInfo> call, Response<TrendingToggleInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        if (response.body().getCode() == 0){
                            if (response.body().isData()){
                                ToastUtils.showShort(context,"点赞成功");
                                likeCheckBtn.setCompoundDrawables(leftDrawLightLike,null,null,null);
                            }else {
                                ToastUtils.showShort(context,"取消赞成功");
                                likeCheckBtn.setCompoundDrawables(leftDrawDarkLike,null,null,null);
                            }
                        }
                    }else {
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        TrendingToggleInfo toggleInfo=gson.fromJson(errorStr,TrendingToggleInfo.class);
                        if (toggleInfo.getCode() == 40104){
                            ToastUtils.showShort(context,toggleInfo.getMessage());
                        }else if (toggleInfo.getCode() == 40301){
                            ToastUtils.showShort(context,toggleInfo.getMessage());
                        }else if (toggleInfo.getCode() == 40401){
                            ToastUtils.showShort(context,toggleInfo.getMessage());
                        }
                    }
                    likeCheckBtn.setText("喜欢");
                    likeCheckBtn.setClickable(true);
                }

                @Override
                public void onFailure(Call<TrendingToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(context,"请检查您的网络");
                    likeCheckBtn.setText("喜欢");
                    likeCheckBtn.setClickable(true);
                }
            });
        }
        if (NET_TOGGLE_STATUS == NET_STATUS_TOGGLE_COLLENTION) {
            apiPost.getTrendingToggleCollection(type, id).enqueue(new Callback<TrendingToggleInfo>() {
                @Override
                public void onResponse(Call<TrendingToggleInfo> call, Response<TrendingToggleInfo> response) {
                    if (response != null && response.isSuccessful()) {
                        if (response.body().getCode() == 0) {

                            if (response.body().isData()) {
                                ToastUtils.showShort(context, "收藏成功");
                                collectionCheckBtn.setCompoundDrawables(leftDrawLightcollection,null,null,null);
                            } else {
                                ToastUtils.showShort(context, "取消收藏成功");
                                collectionCheckBtn.setCompoundDrawables(leftDrawDarkcollection,null,null,null);
                            }
                        }
                    } else {
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        TrendingToggleInfo toggleInfo = gson.fromJson(errorStr, TrendingToggleInfo.class);
                        if (toggleInfo.getCode() == 40104) {
                            ToastUtils.showShort(context, toggleInfo.getMessage());
                        } else if (toggleInfo.getCode() == 40301) {
                            ToastUtils.showShort(context, toggleInfo.getMessage());
                        } else if (toggleInfo.getCode() == 40401) {
                            ToastUtils.showShort(context, toggleInfo.getMessage());
                        }
                    }
                    collectionCheckBtn.setClickable(true);
                    collectionCheckBtn.setText("收藏");
                }

                @Override
                public void onFailure(Call<TrendingToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(context, "请检查您的网络");
                    collectionCheckBtn.setClickable(true);
                    collectionCheckBtn.setText("收藏");
                }
            });
        }

        if (NET_TOGGLE_STATUS == NET_STATUS_TOGGLE_REWARDED) {
            apiPost.getTrendingToggleReward(type, id).enqueue(new Callback<TrendingToggleInfo>() {
                @Override
                public void onResponse(Call<TrendingToggleInfo> call, Response<TrendingToggleInfo> response) {
                    if (response != null && response.isSuccessful()) {
                        if (response.body().getCode() == 0) {
                            if (response.body().isData()){
                                ToastUtils.showShort(context, "打赏成功，消耗1金币");
                                rewarded = true;
                                rewardedBtn.setCompoundDrawables(leftDrawLightLike,null,null,null);
                            }else {
                                ToastUtils.showShort(context,response.body().getMessage());
                            }
                        }
                    } else {
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        TrendingToggleInfo toggleInfo = gson.fromJson(errorStr, TrendingToggleInfo.class);
                        if (toggleInfo.getCode() == 40104) {
                            ToastUtils.showShort(context, toggleInfo.getMessage());
                        } else if (toggleInfo.getCode() == 40301) {
                            ToastUtils.showShort(context, toggleInfo.getMessage());
                        } else if (toggleInfo.getCode() == 40401) {
                            ToastUtils.showShort(context, toggleInfo.getMessage());
                        }
                    }
                    rewardedBtn.setClickable(true);
                    rewardedBtn.setText("打赏");
                }

                @Override
                public void onFailure(Call<TrendingToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(context, "请检查您的网络");
                    rewardedBtn.setClickable(true);
                    rewardedBtn.setText("打赏");
                }
            });
        }

    }

    public final void setLiked(boolean liked){
        this.liked = liked;
    }

    public final void setCollected(boolean collected){
        this.collected = collected;
    }

    public final void setRewarded(boolean rewarded){
        this.rewarded = rewarded;
    }

    public final void setType(String type){
        this.type = type;
    }
    public final void setApiPost(ApiPost apiPost){
        this.apiPost = apiPost;
    }
    public final void setID(int id){
        this.id = id;
    }
    public final void setIsCreator(boolean isCreator){
        this.isCreator = isCreator;
    }
    public final void startListenerAndNet(){
        setListener();
    }
}

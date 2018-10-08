package com.riuir.calibur.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.data.trending.TrendingToggleInfo;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.ui.home.MineFragment;

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

    private OnLFCNetFinish onLFCNetFinish;

    private  AlertDialog rewardDialog;

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
                        ToastUtils.showShort(context,"只能投食一次哦，您已经投食过了，请勿重复投食~");
                    }else {

                        rewardDialog = new AlertDialog.Builder(context)
                                .setTitle("投食")
                                .setMessage("确认投食将会消耗您1金币哦")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        rewardDialog.dismiss();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        rewardedBtn.setClickable(false);
                                        setNetToToggle(NET_STATUS_TOGGLE_REWARDED);
                                        rewardedBtn.setText("投食中");
                                        rewardDialog.dismiss();

                                    }
                                })
                                .create();
                        rewardDialog.show();
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
                                ToastUtils.showShort(context, "投食成功，消耗1金币");
                                rewarded = true;
                                rewardedBtn.setCompoundDrawables(leftDrawLightLike,null,null,null);
                                LogUtils.d("testCoin","coin reward 1 = "+Constants.userInfoData.getCoin());
                                Constants.userInfoData.setCoin(Constants.userInfoData.getCoin()-1);
                                Intent intent = new Intent(MineFragment.COINCHANGE);
                                context.sendBroadcast(intent);
                                LogUtils.d("testCoin","coin reward 2 = "+Constants.userInfoData.getCoin());
                                onLFCNetFinish.onRewardFinish();
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
                    rewardedBtn.setText("投食");
                }

                @Override
                public void onFailure(Call<TrendingToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(context, "请检查您的网络");
                    rewardedBtn.setClickable(true);
                    rewardedBtn.setText("投食");
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


    public void setOnLFCNetFinish(OnLFCNetFinish onLFCNetFinish){
        this.onLFCNetFinish = onLFCNetFinish;
    }

    public interface OnLFCNetFinish{
        //需要的时候解开注释 同时给对应的类添加这两个方法
//        void onLikedFinish();
//        void onCollectedFinish();
        void onRewardFinish();
    }
}

package com.riuir.calibur.ui.widget;

import android.content.Context;
import android.content.Intent;
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
import com.riuir.calibur.data.trending.TrendingToggleInfo;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.ui.loginAndRegister.LoginActivity;
import com.riuir.calibur.utils.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendingLikeFollowCollectionView extends RelativeLayout {

    CheckBox likeCheckBox;
    CheckBox collectionCheckBox;
    Button rewardedBtn;

    String type;
    ApiPost apiPost;
    int id;

    boolean rewarded;

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
        likeCheckBox = view.findViewById(R.id.trending_header_like_checkBox);
        collectionCheckBox = view.findViewById(R.id.trending_header_collection_checkBox);
        rewardedBtn = view.findViewById(R.id.trending_header_rewarded_btn);

    }

    private void setListener() {
        likeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (Constants.ISLOGIN){
                    if (b){
                        setNetToToggle(NET_STATUS_TOGGLE_LIKE);
                    }else {
                        setNetToToggle(NET_STATUS_TOGGLE_LIKE);
                    }
                    likeCheckBox.setClickable(false);
                }else {
                    Intent intent = new Intent(context,LoginActivity.class);
                    context.startActivity(intent);
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
                        rewardedBtn.setClickable(false);
                        setNetToToggle(NET_STATUS_TOGGLE_REWARDED);
                    }
                }else {
                    Intent intent = new Intent(context,LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        });
        collectionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (Constants.ISLOGIN){
                    if (b){
                        setNetToToggle(NET_STATUS_TOGGLE_COLLENTION);
                    }else {
                        setNetToToggle(NET_STATUS_TOGGLE_COLLENTION);
                    }
                    collectionCheckBox.setClickable(false);
                }else {
                    Intent intent = new Intent(context,LoginActivity.class);
                    context.startActivity(intent);
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
                            }else {
                                ToastUtils.showShort(context,"取消赞成功");
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
                    likeCheckBox.setClickable(true);
                }

                @Override
                public void onFailure(Call<TrendingToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(context,"请检查您的网络");
//                    headerCardLike.toggle();
                    likeCheckBox.setClickable(true);
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
                            } else {
                                ToastUtils.showShort(context, "取消收藏成功");
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
                    collectionCheckBox.setClickable(true);
                }

                @Override
                public void onFailure(Call<TrendingToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(context, "请检查您的网络");
//                    headerCardCollection.toggle();
                    collectionCheckBox.setClickable(true);
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
                }

                @Override
                public void onFailure(Call<TrendingToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(context, "请检查您的网络");
//                    headerCardCollection.toggle();
                    rewardedBtn.setClickable(true);
                }
            });
        }

    }

    public final void setLiked(boolean liked){
        if (liked){
            likeCheckBox.setChecked(true);
        }else {
            likeCheckBox.setChecked(false);
        }
    }

    public final void setCollected(boolean collected){
        if (collected){
            collectionCheckBox.setChecked(true);
        }else {
            collectionCheckBox.setChecked(false);
        }
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
    public final void startListenerAndNet(){
        setListener();
    }
}

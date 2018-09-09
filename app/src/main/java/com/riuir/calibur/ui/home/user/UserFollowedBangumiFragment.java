package com.riuir.calibur.ui.home.user;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.user.UserFollowedBangumiInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.ui.home.user.adapter.FollowedBangumiAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowedBangumiFragment extends BaseFragment {

    @BindView(R.id.user_main_follow_bangumi_list_view)
    RecyclerView bangumiListView;
    @BindView(R.id.user_main_follow_bangumi_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private int userId;
    private String zone;
    boolean isFirstLoad = false;
    boolean isRefresh = false;

    private List<UserFollowedBangumiInfo.UserFollowedBangumiInfoData> bangumiInfoDatas;
    private List<UserFollowedBangumiInfo.UserFollowedBangumiInfoData> baseBangumiInfoDatas = new ArrayList<>();
    FollowedBangumiAdapter bangumiAdapter;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_user_followed_bangui;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        UserMainActivity activity = (UserMainActivity) getActivity();
        userId = activity.getUserId();
        zone = activity.getZone();
        setAdapter();
        isFirstLoad = true;
        refreshLayout.setRefreshing(true);
        setNet();
    }

    private void setNet() {
        apiGet.getCallUserFollowedBangumi(zone).enqueue(new Callback<UserFollowedBangumiInfo>() {
            @Override
            public void onResponse(Call<UserFollowedBangumiInfo> call, Response<UserFollowedBangumiInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    bangumiInfoDatas = response.body().getData();
                    if (isFirstLoad){
                        isFirstLoad =false;
                        refreshLayout.setRefreshing(false);
                        baseBangumiInfoDatas = response.body().getData();
                        setAdapter();
                    }
                    if (isRefresh){
                        setRefresh();
                    }

                }else if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    if (info.getCode() == 40401){
                        ToastUtils.showShort(getContext(),"该用户不存在！");
                    }
                    if (isFirstLoad){
                        isFirstLoad = false;
                        refreshLayout.setRefreshing(false);
                    }
                    if (isRefresh){
                        isRefresh = false;
                        refreshLayout.setRefreshing(false);
                    }
                }else {
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                    if (isFirstLoad){
                        isFirstLoad = false;
                        refreshLayout.setRefreshing(false);
                    }
                    if (isRefresh){
                        isRefresh = false;
                        refreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserFollowedBangumiInfo> call, Throwable t) {
                ToastUtils.showShort(getContext(),"请检查您的网络哟！");
                if (isFirstLoad){
                    isFirstLoad = false;
                    refreshLayout.setRefreshing(false);
                }
                if (isRefresh){
                    isRefresh = false;
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void setRefresh() {
        isRefresh = false;
        refreshLayout.setRefreshing(false);
        bangumiAdapter.setNewData(bangumiInfoDatas);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }

    private void setAdapter() {
        bangumiAdapter = new FollowedBangumiAdapter(R.layout.drama_timeline_list_item,baseBangumiInfoDatas,getContext());

        bangumiListView.setLayoutManager(new LinearLayoutManager(App.instance()));
        bangumiAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        bangumiAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        bangumiListView.setAdapter(bangumiAdapter);

        //添加监听
        setListener();
    }

    private void setListener() {
        bangumiAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UserFollowedBangumiInfo.UserFollowedBangumiInfoData data = (UserFollowedBangumiInfo.UserFollowedBangumiInfoData)adapter.getData().get(position);
                Intent intent = new Intent(getContext(), DramaActivity.class);
                intent.putExtra("animeId",data.getId());
                startActivity(intent);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }

}

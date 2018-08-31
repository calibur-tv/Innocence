package com.riuir.calibur.ui.home.user;


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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.data.user.UserFollowedBangumiInfo;
import com.riuir.calibur.data.user.UserFollowedRoleInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.role.RolesShowInfoActivity;
import com.riuir.calibur.ui.home.user.adapter.FollowedRoleAdapter;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowedRoleFragment extends BaseFragment {

    @BindView(R.id.user_main_follow_role_list_view)
    RecyclerView roleListView;

    @BindView(R.id.user_main_follow_role_refresh_layout)
    SwipeRefreshLayout roleRefreshLayout;

    private int userId;
    private String zone;

    private boolean isFirstLoad = false;
    private boolean isLoadMore = false;
    boolean isRefresh = false;

    private MainTrendingInfo.MainTrendingInfoData roleInfoData;
    private List<MainTrendingInfo.MainTrendingInfoList> roleInfoList;
    private List<MainTrendingInfo.MainTrendingInfoList> baseRoleInfoList;

    private FollowedRoleAdapter followedRoleAdapter;

    //page默认从0开始
    private int page = 0;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_user_followed_role;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        UserMainActivity activity = (UserMainActivity) getActivity();
        userId = activity.getUserId();
        zone = activity.getZone();
        setNet();
    }

    private void setNet() {
        setPage();
        apiGet.getFollowList("role","hot",0,zone,page,20,0,"").enqueue(new Callback<MainTrendingInfo>() {
            @Override
            public void onResponse(Call<MainTrendingInfo> call, Response<MainTrendingInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    roleInfoData = response.body().getData();
                    roleInfoList = response.body().getData().getList();

                    if (isFirstLoad){
                        baseRoleInfoList = response.body().getData().getList();
                        setAdapter();
                    }
                    if (isLoadMore){
                        setLoadMore();
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
                    if (isLoadMore){
                        followedRoleAdapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        roleRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }

                }else {
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                    if (isLoadMore){
                        followedRoleAdapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        roleRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                }
            }

            @Override
            public void onFailure(Call<MainTrendingInfo> call, Throwable t) {
                ToastUtils.showShort(getContext(),"请检查您的网络哟！");
                if (isLoadMore){
                    followedRoleAdapter.loadMoreFail();
                    isLoadMore = false;
                }
                if (isRefresh){
                    roleRefreshLayout.setRefreshing(false);
                    isRefresh = false;
                }
            }
        });
    }

    private void setPage() {
        if (isFirstLoad||isRefresh){
            page = 0;
        }else {
            page++;
        }
    }

    private void setAdapter() {
        followedRoleAdapter = new FollowedRoleAdapter(R.layout.user_follow_role_list_item,baseRoleInfoList,getContext());
        roleListView.setLayoutManager(new LinearLayoutManager(App.instance()));
        followedRoleAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        followedRoleAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        //开启上拉加载更多
        followedRoleAdapter.setEnableLoadMore(true);


        //添加底部footer
        followedRoleAdapter.setLoadMoreView(new MyLoadMoreView());
        followedRoleAdapter.disableLoadMoreIfNotFullPage(roleListView);

        roleListView.setAdapter(followedRoleAdapter);

        //添加监听
        setListener();
        isFirstLoad = false;
    }

    private void setListener() {
        followedRoleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MainTrendingInfo.MainTrendingInfoList data = (MainTrendingInfo.MainTrendingInfoList)adapter.getData().get(position);
                Intent intent = new Intent(getContext(), RolesShowInfoActivity.class);
                intent.putExtra("roleId",data.getId());
                startActivity(intent);
            }
        });

        //上拉加载监听
        followedRoleAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, roleListView);

        //下拉刷新监听
        roleRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }

    private void setLoadMore() {
        isLoadMore = false;

        if (roleInfoData.isNoMore()) {
            followedRoleAdapter.addData(roleInfoList);
            //数据全部加载完毕
            followedRoleAdapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            followedRoleAdapter.addData(roleInfoList);
            followedRoleAdapter.loadMoreComplete();
        }
    }

    private void setRefresh() {
        isRefresh = false;
        followedRoleAdapter.setNewData(roleInfoList);
        roleRefreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }
}

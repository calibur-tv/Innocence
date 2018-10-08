package com.riuir.calibur.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.role.RolesShowInfoActivity;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.role.adapter.RoleListAdapter;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;

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
public class DramaRolesListFragment extends BaseFragment {

    String seenIds = "";
    List<Integer> seenIdList = new ArrayList<>();

    @BindView(R.id.drama_role_list_list_view)
    RecyclerView roleListView;
    @BindView(R.id.drama_role_list_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private List<MainTrendingInfo.MainTrendingInfoList> dataList;
    private List<MainTrendingInfo.MainTrendingInfoList> baseDataList;

    private MainTrendingInfo.MainTrendingInfoData roleInfoData;

    private RoleListAdapter roleListAdapter;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;

    @Override
    protected int getContentViewID() {
        return R.layout.drama_fragment_roles_list;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        refreshLayout.setRefreshing(true);
        setView();
        setAdapter();
        setNet();

    }

    private void setView() {

    }

    private void setNet() {
        setSeendIdS();
        apiGet.getFollowList("role","hot",0,"",0,0,0,seenIds).enqueue(new Callback<MainTrendingInfo>() {
            @Override
            public void onResponse(Call<MainTrendingInfo> call, Response<MainTrendingInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    dataList = response.body().getData().getList();
                    roleInfoData = response.body().getData();
                    if (isFirstLoad){
                        baseDataList = response.body().getData().getList();
                        refreshLayout.setRefreshing(false);
                        setFirstData();
                    }
                    if (isRefresh){
                        setRefresh();
                    }
                    if (isLoadMore){
                        setLoadMore();
                    }
                    setEmptyView();
                    for (MainTrendingInfo.MainTrendingInfoList hotItem :dataList){
                        seenIdList.add(hotItem.getId());
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

                    ToastUtils.showShort(getContext(),info.getMessage());
                    if (isLoadMore){
                        roleListAdapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        refreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    if (isFirstLoad){
                        refreshLayout.setRefreshing(false);
                    }
                    setFailedView();
                }else {
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                    if (isLoadMore){
                        roleListAdapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        refreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    if (isFirstLoad){
                        refreshLayout.setRefreshing(false);
                    }
                    setFailedView();
                }
            }

            @Override
            public void onFailure(Call<MainTrendingInfo> call, Throwable t) {
                ToastUtils.showShort(getContext(),"请检查您的网络！");
                LogUtils.d("AppNetErrorMessage","drama role list t = "+t.getMessage());
                if (isLoadMore){
                    roleListAdapter.loadMoreFail();
                    isLoadMore = false;
                }
                if (isRefresh){
                    refreshLayout.setRefreshing(false);
                    isRefresh = false;
                }
                if (isFirstLoad){
                    refreshLayout.setRefreshing(false);
                }
                setFailedView();
            }
        });
    }

    private void setEmptyView(){
        if (baseDataList==null||baseDataList.size()==0){
            if (emptyView == null){
                emptyView = new AppListEmptyView(getContext());
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            roleListAdapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 下拉重试
        if (failedView == null){
            failedView = new AppListFailedView(getContext());
            failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        roleListAdapter.setEmptyView(failedView);
    }

    private void setLoadMore() {
        isLoadMore = false;

        if (roleInfoData.isNoMore()) {
            roleListAdapter.addData(dataList);
            //数据全部加载完毕
            roleListAdapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            roleListAdapter.addData(dataList);
            roleListAdapter.loadMoreComplete();
        }
    }

    private void setRefresh() {
        isRefresh = false;
        roleListAdapter.setNewData(dataList);
        refreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }


    private void setSeendIdS() {
        if (seenIdList!=null&&seenIdList.size()!=0){
            for (int position = 0; position <seenIdList.size() ; position++) {
                int id = seenIdList.get(position);
                if (position == 0){
                    if (seenIds == null||seenIds.length() == 0){
                        seenIds = seenIds+id;
                    }else {
                        seenIds = seenIds+","+id;
                    }
                }else {
                    seenIds = seenIds+","+id;
                }
            }
        }
        if (isFirstLoad){
            seenIdList.clear();
            seenIds = "";
        }
        if (isRefresh){
            seenIdList.clear();
            seenIds = "";
        }

        LogUtils.d("image_1","seenIds = "+seenIds );
    }


    private void setAdapter() {
        roleListAdapter = new RoleListAdapter(R.layout.drama_role_list_item,baseDataList,getContext());

        if (roleListView == null){
            roleListView = getView().findViewById(R.id.drama_role_list_list_view);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(App.instance());
        roleListView.setLayoutManager(layoutManager);
        roleListAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        roleListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //开启上拉加载更多
        roleListAdapter.setEnableLoadMore(true);


        //添加底部footer
        roleListAdapter.setLoadMoreView(new MyLoadMoreView());
        roleListAdapter.disableLoadMoreIfNotFullPage(roleListView);

        roleListView.setAdapter(roleListAdapter);

        //添加监听
        setListener();


    }
    private void  setFirstData(){
        isFirstLoad = false;
        roleListAdapter.addData(baseDataList);
    }

    private void setListener() {
        roleListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getContext(),RolesShowInfoActivity.class);
                MainTrendingInfo.MainTrendingInfoList roleInfo = (MainTrendingInfo.MainTrendingInfoList) adapter.getData().get(position);
                intent.putExtra("roleId",roleInfo.getId());
                startActivity(intent);
            }
        });

        //上拉加载监听
        roleListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, roleListView);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }

}

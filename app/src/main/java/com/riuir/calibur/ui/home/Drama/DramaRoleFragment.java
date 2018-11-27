package com.riuir.calibur.ui.home.Drama;


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

import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.role.RolesShowInfoActivity;
import com.riuir.calibur.ui.home.role.adapter.RoleListAdapter;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.followList.MainTrendingInfo;
import calibur.core.http.models.followList.params.FolllowListParams;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DramaRoleFragment extends BaseFragment {
    //fragment_drama_role
    String seenIds = "";
    List<Integer> seenIdList = new ArrayList<>();

    @BindView(R.id.drama_role_list_view)
    RecyclerView roleListView;
    @BindView(R.id.drama_role_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private List<MainTrendingInfo.MainTrendingInfoList> dataList;
    private List<MainTrendingInfo.MainTrendingInfoList> baseDataList = new ArrayList<>();

    private MainTrendingInfo roleInfoData;

    private RoleListAdapter roleListAdapter;


    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;
    int bangumiID = 0;

    private Call<MainTrendingInfo> listCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;
    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_role;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        DramaActivity dramaActivity = (DramaActivity) getActivity();
        bangumiID = dramaActivity.getAnimeID();
        baseDataList.clear();
        setAdapter();
        isFirstLoad = true;
        refreshLayout.setRefreshing(true);
        setNet();

    }

    @Override
    public void onDestroy() {
        if (listCall!=null){
            listCall.cancel();
        }
        super.onDestroy();
    }

    private void setNet() {
        setSeendIdS();
        FolllowListParams folllowListParams = new FolllowListParams();
        folllowListParams.setType("role");
        folllowListParams.setSort("hot");
        folllowListParams.setBangumiId(bangumiID);
        folllowListParams.setUserZone("");
        folllowListParams.setPage(0);
        folllowListParams.setTake(0);
        folllowListParams.setMinId(0);
        folllowListParams.setSeenIds(seenIds);
        apiService.getFollowList(folllowListParams)
                .compose(Rx2Schedulers.<Response<ResponseBean<MainTrendingInfo>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<MainTrendingInfo>() {
                    @Override
                    public void onSuccess(MainTrendingInfo mainTrendingInfo) {
                        dataList = mainTrendingInfo.getList();
                        roleInfoData = mainTrendingInfo;
                        if (isFirstLoad){
                            isFirstLoad = false;
                            baseDataList = mainTrendingInfo.getList();
                            if (refreshLayout!=null&&roleListView!=null){
                                refreshLayout.setRefreshing(false);
                                setAdapter();
                                setEmptyView();
                            }
                        }
                        if (isLoadMore){
                            setLoadMore();
                        }
                        if (isRefresh){
                            setRefresh();
                        }

                        for (MainTrendingInfo.MainTrendingInfoList hotItem :dataList){
                            seenIdList.add(hotItem.getId());
                        }
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (roleListView!=null){
                            if (isFirstLoad){
                                isFirstLoad = false;
                                if (refreshLayout!=null){
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                            if (isLoadMore){
                                isLoadMore = false;
                                roleListAdapter.loadMoreFail();
                            }
                            if (isRefresh){
                                isRefresh = false;
                                if (refreshLayout!=null){
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                            setFailedView();
                        }
                    }
                });
    }

    private void setEmptyView(){
        if (baseDataList==null||baseDataList.size()==0){
            emptyView = new AppListEmptyView(getContext());
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            roleListAdapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 点击重试
        failedView = new AppListFailedView(getContext());
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        roleListAdapter.setEmptyView(failedView);

    }

    private void setRefresh() {
        isRefresh = false;
        if (refreshLayout!=null){
            refreshLayout.setRefreshing(false);
        }
        roleListAdapter.setNewData(dataList);
        ToastUtils.showShort(getContext(),"刷新成功");
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

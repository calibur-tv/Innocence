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
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;

import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.adapter.CardActiveListAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.widget.BannerLoopView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.utils.Constants;
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
public class MainCardActiveFragment extends BaseFragment {
    String seenIds = "";
    List<Integer> seenIdList = new ArrayList<>();

    @BindView(R.id.main_card_active_list_view)
    RecyclerView mainCardHotListView;
    @BindView(R.id.card_fragment_banner_loop)
    BannerLoopView bannerLoopView;
    @BindView(R.id.main_card_active_refresh_layout)
    SwipeRefreshLayout mainCardHotRefreshLayout;
    //用来动态改变RecyclerView的变量
    private List<MainTrendingInfo.MainTrendingInfoList> listActive;
    //传给Adapter的值 首次加载后不可更改 不然会导致数据出错
    private List<MainTrendingInfo.MainTrendingInfoList> baseListActive = new ArrayList<>();

    private MainTrendingInfo mainTrendingInfoData;

    private CardActiveListAdapter adapter;

    //帖子总条目数
    int TOTAL_COUNTER = 0;
    //list里面拥有的
    int listDataCounter = 0;
    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_main_card_active;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        setListAdapter();
        bannerLoopView.setApiGet(apiGet);
        mainCardHotRefreshLayout.setRefreshing(true);
        setNet();
    }

    private void setNet() {
        setSeendIdS();
        FolllowListParams params = new FolllowListParams();
        params.setType("post");
        params.setSort("active");
        params.setBangumiId(0);
        params.setUserZone("");
        params.setMinId(0);
        params.setPage(0);
        params.setTake(0);
        params.setSeenIds(seenIds);
        apiService.getFollowList(params)
                .compose(Rx2Schedulers.<Response<ResponseBean<MainTrendingInfo>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<MainTrendingInfo>() {
                    @Override
                    public void onSuccess(MainTrendingInfo mainTrendingInfo) {
                        listActive = mainTrendingInfo.getList();
                        mainTrendingInfoData = mainTrendingInfo;
                        if (isFirstLoad){
                            baseListActive = mainTrendingInfo.getList();
                            if (mainCardHotRefreshLayout!=null&&adapter!=null){
                                mainCardHotRefreshLayout.setRefreshing(false);
                                setFirstData();
                            }
                        }
                        if (isLoadMore){
                            setLoadMore();
                        }
                        if (isRefresh){
                            setRefresh();
                        }
                        setEmptyView();
                        for (MainTrendingInfo.MainTrendingInfoList hotItem :listActive){
                            seenIdList.add(hotItem.getId());
                        }
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (mainCardHotListView!=null){
                            if (isLoadMore){
                                adapter.loadMoreFail();
                                isLoadMore = false;
                            }
                            if (isRefresh){
                                if (mainCardHotRefreshLayout!=null){
                                    mainCardHotRefreshLayout.setRefreshing(false);
                                }
                                isRefresh = false;
                            }
                            if (isFirstLoad){
                                if (mainCardHotRefreshLayout!=null){
                                    mainCardHotRefreshLayout.setRefreshing(false);
                                }
                            }
                            setFailedView();
                        }
                    }
                });

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
        if (isRefresh){
            seenIds = "";
            seenIdList.clear();
        }

        LogUtils.d("CardHot","seenIds = "+seenIds );
    }

    private void setLoadMore() {
        isLoadMore = false;

        if (mainTrendingInfoData.isNoMore()) {
            adapter.addData(listActive);
            //数据全部加载完毕
            adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            adapter.addData(listActive);
            adapter.loadMoreComplete();
        }
    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(listActive);
        if (mainCardHotRefreshLayout!=null){
            mainCardHotRefreshLayout.setRefreshing(false);
        }
        ToastUtils.showShort(getContext(),"刷新成功！");
    }

    private void setListAdapter() {


        adapter = new CardActiveListAdapter(R.layout.main_card_list_item,baseListActive,getContext());
        mainCardHotListView.setLayoutManager(new LinearLayoutManager(App.instance()));
        adapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //开启上拉加载更多
        adapter.setEnableLoadMore(true);


        //添加底部footer
        adapter.setLoadMoreView(new MyLoadMoreView());
        adapter.disableLoadMoreIfNotFullPage(mainCardHotListView);

        mainCardHotListView.setAdapter(adapter);

        //添加监听
        setListener();
    }
    private void  setFirstData(){
        isFirstLoad = false;
        adapter.addData(baseListActive);
    }
    private void setEmptyView(){
        if (baseListActive==null||baseListActive.size()==0){
            if (emptyView == null) {
                emptyView = new AppListEmptyView(getContext());
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            adapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 点击重试
        if (failedView == null){
            failedView = new AppListFailedView(getContext());
            failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
       adapter.setEmptyView(failedView);

    }

    private void setListener() {

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //item被点击，跳转页面
                Intent intent = new Intent(getActivity(), CardShowInfoActivity.class);
                MainTrendingInfo.MainTrendingInfoList cardInfo = (MainTrendingInfo.MainTrendingInfoList) adapter.getData().get(position);
                int cardID = cardInfo.getId();
                intent.putExtra("cardID",cardID);
                startActivity(intent);

            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.main_card_list_item_user_icon){
                    MainTrendingInfo.MainTrendingInfoList cardInfo = (MainTrendingInfo.MainTrendingInfoList) adapter.getData().get(position);
                    UserMainUtils.toUserMainActivity(getContext(),cardInfo.getUser().getId(),cardInfo.getUser().getZone());
                }
            }
        });


        //上拉加载监听
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, mainCardHotListView);

        //下拉刷新监听
        mainCardHotRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
                bannerLoopView.setApiGet(apiGet);
            }
        });
    }


}

package com.riuir.calibur.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.MainCardInfo;

import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.adapter.CardActiveListAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.followList.MainTrendingInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 *
 * TODO 暂时作废
 *
 *
 */
public class MainCardHotFragment extends BaseFragment {

    String seenIds = "";
    List<Integer> seenIdList = new ArrayList<>();

    @BindView(R.id.main_card_hot_list_view)
    RecyclerView mainCardHotListView;

    @BindView(R.id.main_card_hot_refresh_layout)
    SwipeRefreshLayout mainCardHotRefreshLayout;
    //用来动态改变RecyclerView的变量
    private List<MainTrendingInfo.MainTrendingInfoList> listHot;
    //传给Adapter的值 首次加载后不可更改 不然会导致数据出错
    private List<MainTrendingInfo.MainTrendingInfoList> baseListHot;

    private MainTrendingInfo mainCardInfoData;

    private CardActiveListAdapter adapter;

    //帖子总条目数
    int TOTAL_COUNTER = 0;
    //list里面拥有的
    int listDataCounter = 0;
    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_main_card_hot;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        setNet();
    }

    private void setNet() {
//
//        setSeendIdS();
//        apiGet.getCallMainCardHotGet(seenIds).enqueue(new Callback<MainTrendingInfo>() {
//            @Override
//            public void onResponse(Call<MainTrendingInfo> call, Response<MainTrendingInfo> response) {
//
//                if (response == null||response.body()==null||response.body().getData() == null||response.body().getData().getList().size() == 0){
//                    ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
//                    if (isLoadMore){
//                        adapter.loadMoreFail();
//                        isLoadMore = false;
//                    }
//                    if (isRefresh){
//                        mainCardHotRefreshLayout.setRefreshing(false);
//                        isRefresh = false;
//                    }
//                }else {
//                    listHot = response.body().getData().getList();
//                    mainCardInfoData = response.body().getData();
//                    if (isFirstLoad){
//                        baseListHot = response.body().getData().getList();
//                        setListAdapter();
//                    }
//                    if (isLoadMore){
//                        setLoadMore();
//                    }
//                    if (isRefresh){
//                        setRefresh();
//                    }
//
//                    for (MainTrendingInfo.MainTrendingInfoList hotItem :listHot){
//                        seenIdList.add(hotItem.getId());
//                    }
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(Call<MainTrendingInfo> call, Throwable t) {
//
//                LogUtils.d("cardHot","t = "+t);
//                CrashReport.postCatchedException(t);
//                if (isLoadMore){
//                    adapter.loadMoreFail();
//                    isLoadMore = false;
//                    ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
//                }
//                if (isRefresh){
//                    mainCardHotRefreshLayout.setRefreshing(false);
//                    isRefresh = false;
//                    ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
//                }
//            }
//        });
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


        if (mainCardInfoData.isNoMore()) {
            adapter.addData(listHot);
            //数据全部加载完毕
           adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            adapter.addData(listHot);
            adapter.loadMoreComplete();

        }
    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(listHot);
        mainCardHotRefreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }

    private void setListAdapter() {


        adapter = new CardActiveListAdapter(R.layout.main_card_list_item,baseListHot,getContext());
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
        isFirstLoad = false;
    }

    private void setListener() {

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //item被点击，跳转页面
                Intent intent = new Intent(getActivity(), CardShowInfoActivity.class);
                MainCardInfo.MainCardInfoList cardInfo = (MainCardInfo.MainCardInfoList) adapter.getData().get(position);
                int cardID = cardInfo.getId();
                intent.putExtra("cardID",cardID);
                startActivity(intent);
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
            }
        });
    }



}

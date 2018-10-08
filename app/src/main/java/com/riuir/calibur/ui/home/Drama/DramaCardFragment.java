package com.riuir.calibur.ui.home.Drama;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.adapter.DramaCardListAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.home.image.ImageShowInfoActivity;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DramaCardFragment extends BaseFragment {
    String seenIds = "";
    List<Integer> seenIdList = new ArrayList<>();

    @BindView(R.id.drama_fragment_card_active_list_view)
    RecyclerView cardListView;

    @BindView(R.id.drama_fragment_card_active_refresh_layout)
    SwipeRefreshLayout cardRefreshLayout;
    //用来动态改变RecyclerView的变量
    private List<MainTrendingInfo.MainTrendingInfoList> listActive;
    //传给Adapter的值 首次加载后不可更改 不然会导致数据出错
    private List<MainTrendingInfo.MainTrendingInfoList> baseListActive = new ArrayList<>();

    private MainTrendingInfo.MainTrendingInfoData mainTrendingInfoData;

    private DramaCardListAdapter adapter;

    //帖子总条目数
    int TOTAL_COUNTER = 0;
    //list里面拥有的
    int listDataCounter = 0;
    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;
    int bangumiID = 0;

    private Call<MainTrendingInfo> listCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_card;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        DramaActivity dramaActivity = (DramaActivity) getActivity();
        bangumiID = dramaActivity.getAnimeID();
        setListAdapter();
        isFirstLoad = true;
        cardRefreshLayout.setRefreshing(true);
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
        listCall = apiGet.getFollowList("post","active",bangumiID,"",0,0,0,seenIds);
        listCall.enqueue(new Callback<MainTrendingInfo>() {
            @Override
            public void onResponse(Call<MainTrendingInfo> call, Response<MainTrendingInfo> response) {

                if (response!=null&&response.isSuccessful()){
                    listActive = response.body().getData().getList();
                    mainTrendingInfoData = response.body().getData();
                    if (isFirstLoad){
                        isFirstLoad = false;
                        baseListActive = response.body().getData().getList();
                        if (cardRefreshLayout!=null&&cardListView!=null){
                            cardRefreshLayout.setRefreshing(false);
                            setListAdapter();
                            setEmptyView();
                        }
                    }
                    if (isLoadMore){
                        setLoadMore();
                    }
                    if (isRefresh){
                        setRefresh();
                    }

                    for (MainTrendingInfo.MainTrendingInfoList hotItem :listActive){
                        seenIdList.add(hotItem.getId());
                    }
                }else if (response!=null&&!response.isSuccessful()){
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
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        cardRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    if (isFirstLoad){
                        isFirstLoad = false;
                        cardRefreshLayout.setRefreshing(false);
                    }
                    setFailedView();
                }else {
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        cardRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    if (isFirstLoad){
                        isFirstLoad = false;
                        cardRefreshLayout.setRefreshing(false);
                    }
                    setFailedView();
                }

            }

            @Override
            public void onFailure(Call<MainTrendingInfo> call, Throwable t) {
                if (call.isCanceled()){
                }else {
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        cardRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    if (isFirstLoad){
                        isFirstLoad = false;
                        cardRefreshLayout.setRefreshing(false);
                    }
                    ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
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
        if (isFirstLoad){
            seenIds = "";
            seenIdList.clear();
        }

        LogUtils.d("CardHot","seenIds = "+seenIds );
    }

    private void setEmptyView(){
        if (baseListActive==null||baseListActive.size()==0){
            if (emptyView == null){
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
        cardRefreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }

    private void setListAdapter() {


        adapter = new DramaCardListAdapter(R.layout.main_card_list_item,baseListActive,getContext());
        if (cardListView == null){
            cardListView = rootView.findViewById(R.id.drama_fragment_card_active_list_view);
        }
        cardListView.setLayoutManager(new LinearLayoutManager(App.instance()));
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
        adapter.disableLoadMoreIfNotFullPage(cardListView);

        cardListView.setAdapter(adapter);

        //添加监听
        setListener();

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
        }, cardListView);

        //下拉刷新监听
        cardRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }
}

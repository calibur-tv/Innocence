package com.riuir.calibur.ui.home.user;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.home.user.adapter.ReleaseCardListAdapter;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserReleaseCardFragment extends BaseFragment {

    private int userId;
    private String zone;

    @BindView(R.id.user_release_card_list_view)
    RecyclerView cardListView;

    @BindView(R.id.user_release_card_refresh_layout)
    SwipeRefreshLayout cardRefreshLayout;
    //用来动态改变RecyclerView的变量
    private List<MainTrendingInfo.MainTrendingInfoList> listCard;
    //传给Adapter的值 首次加载后不可更改 不然会导致数据出错
    private List<MainTrendingInfo.MainTrendingInfoList> baseListCard;

    private MainTrendingInfo.MainTrendingInfoData mainTrendingInfoData;

    private ReleaseCardListAdapter adapter;

    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = true;

    //page默认是0
    int page = 0;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_user_release_card;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        UserMainActivity activity = (UserMainActivity) getActivity();
        userId = activity.getUserId();
        zone = activity.getZone();
        setNet();
    }

    private void setNet() {
        setPage();
        apiGet.getCallUserReleseCard(zone,page).enqueue(new Callback<MainTrendingInfo>() {
            @Override
            public void onResponse(Call<MainTrendingInfo> call, Response<MainTrendingInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    listCard = response.body().getData().getList();
                    mainTrendingInfoData = response.body().getData();
                    if (isFirstLoad){
                        baseListCard = response.body().getData().getList();
                        setListAdapter();
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

                    ToastUtils.showShort(getContext(),info.getMessage());
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        cardRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
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
                }
            }

            @Override
            public void onFailure(Call<MainTrendingInfo> call, Throwable t) {
                ToastUtils.showShort(getContext(),"请检查您的网络哟！");
                if (isLoadMore){
                    adapter.loadMoreFail();
                    isLoadMore = false;
                }
                if (isRefresh){
                    cardRefreshLayout.setRefreshing(false);
                    isRefresh = false;
                }
            }
        });

    }

    private void setLoadMore() {
        isLoadMore = false;

        if (mainTrendingInfoData.isNoMore()) {
            adapter.addData(listCard);
            //数据全部加载完毕
            adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            adapter.addData(listCard);
            adapter.loadMoreComplete();
        }
    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(listCard);
        cardRefreshLayout.setRefreshing(false);
    }

    private void setPage() {
        if (isFirstLoad||isRefresh){
            page = 0;
        }else {
            page++;
        }
    }

    private void setListAdapter() {


        adapter = new ReleaseCardListAdapter(R.layout.user_release_card_list_item,baseListCard,getContext());
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
        isFirstLoad = false;
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

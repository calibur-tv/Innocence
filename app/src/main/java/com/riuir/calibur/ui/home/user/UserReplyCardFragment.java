package com.riuir.calibur.ui.home.user;


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
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.ToastUtils;

import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.PostDetailActivity;
import com.riuir.calibur.ui.home.user.adapter.ReplyCardListAdapter;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.user.UserReplyCardInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserReplyCardFragment extends BaseFragment {

    private int userId;
    private String zone;

    @BindView(R.id.user_reply_card_list_view)
    RecyclerView cardListView;

    @BindView(R.id.user_reply_card_refresh_layout)
    SwipeRefreshLayout cardRefreshLayout;

    private UserReplyCardInfo replayCardInfoData;
    private List<UserReplyCardInfo.UserReplayCardInfoList> listCard;
    private List<UserReplyCardInfo.UserReplayCardInfoList> baseListCard = new ArrayList<>();

    private ReplyCardListAdapter adapter;

    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;

    //page默认是0
    int page = 0;

    Call<UserReplyCardInfo> replyCardInfoCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_user_reply_card;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        UserMainActivity activity = (UserMainActivity) getActivity();
        userId = activity.getUserId();
        zone = activity.getZone();
        baseListCard.clear();
        setListAdapter();
        isFirstLoad = true;
        cardRefreshLayout.setRefreshing(true);
        setNet();
    }

    private void setNet() {
        setPage();
        apiService.getCallUserReplyCard(zone,page)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<UserReplyCardInfo>(){
                    @Override
                    public void onSuccess(UserReplyCardInfo userReplyCardInfo) {
                        listCard = userReplyCardInfo.getList();
                        replayCardInfoData = userReplyCardInfo;
                        if (isFirstLoad){
                            isFirstLoad = false;
                            baseListCard = userReplyCardInfo.getList();
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
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (cardRefreshLayout!=null){
                            if (isLoadMore){
                                adapter.loadMoreFail();
                                isLoadMore = false;
                            }
                            if (isRefresh){
                                if (cardRefreshLayout!=null){
                                    cardRefreshLayout.setRefreshing(false);
                                }
                                isRefresh = false;
                            }
                            if (isFirstLoad){
                                isFirstLoad = true;
                                if (cardRefreshLayout!=null){
                                    cardRefreshLayout.setRefreshing(false);
                                }
                            }
                            setFailedView();
                        }
                    }
                });

    }

    private void setListAdapter() {

        adapter = new ReplyCardListAdapter(R.layout.user_reply_card_list_item,baseListCard,getContext());
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
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                UserReplyCardInfo.UserReplayCardInfoList cardInfo = (UserReplyCardInfo.UserReplayCardInfoList) adapter.getData().get(position);
                int cardID = cardInfo.getPost().getId();
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

    private void setEmptyView(){
        if (baseListCard==null||baseListCard.size()==0){
            emptyView = new AppListEmptyView(getContext());
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            adapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 下拉重试
        failedView = new AppListFailedView(getContext());
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        adapter.setEmptyView(failedView);
    }

    private void setLoadMore() {
        isLoadMore = false;

        if (replayCardInfoData.isNoMore()) {
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
        if (cardRefreshLayout!=null){
            cardRefreshLayout.setRefreshing(false);
        }
        ToastUtils.showShort(getContext(),"刷新成功！");
    }

    private void setPage() {
        if (isFirstLoad||isRefresh){
            page = 0;
        }else {
            page++;
        }
    }
}

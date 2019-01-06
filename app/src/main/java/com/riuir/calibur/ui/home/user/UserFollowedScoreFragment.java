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
import com.riuir.calibur.ui.home.score.ScoreDetailActivity;
import com.riuir.calibur.ui.home.user.adapter.UserScoreListAdapter;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.followList.MainTrendingInfo;
import calibur.core.http.models.followList.params.FolllowListParams;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowedScoreFragment extends BaseFragment {

    //page默认0
    private int page = 0;
    private int take = 20;

    @BindView(R.id.user_followed_score_list_view)
    RecyclerView scoreListView;

    @BindView(R.id.user_followed_score_refresh_layout)
    SwipeRefreshLayout scoreRefreshLayout;

    //用来动态改变RecyclerView的变量
    private List<MainTrendingInfo.MainTrendingInfoList> listScore;
    //传给Adapter的值 首次加载后不可更改 不然会导致数据出错
    private List<MainTrendingInfo.MainTrendingInfoList> baseListScore = new ArrayList<>();

    private MainTrendingInfo mainScoreInfoData;

    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;

    private int userId;
    private String zone;

    private UserScoreListAdapter adapter;

    private Call<MainTrendingInfo> listCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_user_followed_score;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        UserMainActivity activity = (UserMainActivity) getActivity();
        userId = activity.getUserId();
        zone = activity.getZone();
        baseListScore.clear();
        setListAdapter();
        isFirstLoad = true;
        scoreRefreshLayout.setRefreshing(true);
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
        setPage();
        FolllowListParams folllowListParams = new FolllowListParams();
        folllowListParams.setType("score");
        folllowListParams.setSort("news");
        folllowListParams.setBangumiId(0);
        folllowListParams.setUserZone(zone);
        folllowListParams.setPage(page);
        folllowListParams.setTake(take);
        folllowListParams.setMinId(0);
        folllowListParams.setSeenIds("");
        apiService.getFollowList(folllowListParams)
                .compose(Rx2Schedulers.<Response<ResponseBean<MainTrendingInfo>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<MainTrendingInfo>() {
                    @Override
                    public void onSuccess(MainTrendingInfo mainTrendingInfo) {
                        listScore = mainTrendingInfo.getList();
                        mainScoreInfoData = mainTrendingInfo;
                        if (isFirstLoad){
                            isFirstLoad = false;
                            baseListScore = mainTrendingInfo.getList();
                            if (scoreRefreshLayout!=null&&scoreListView!=null){
                                scoreRefreshLayout.setRefreshing(false);
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
                        if (scoreListView!=null){
                            if (isLoadMore){
                                adapter.loadMoreFail();
                                isLoadMore = false;
                            }
                            if (isRefresh){
                                if (scoreRefreshLayout!=null){
                                    scoreRefreshLayout.setRefreshing(false);
                                }
                                isRefresh = false;
                            }
                            if (isFirstLoad){
                                isFirstLoad = false;
                                if (scoreRefreshLayout!=null){
                                    scoreRefreshLayout.setRefreshing(false);
                                }
                            }
                            setFailedView();
                        }

                    }
                });
    }

    private void setListAdapter() {
        adapter = new UserScoreListAdapter(R.layout.main_score_list_item,baseListScore,getContext());
        scoreListView.setLayoutManager(new LinearLayoutManager(App.instance()));
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
        adapter.disableLoadMoreIfNotFullPage(scoreListView);

        scoreListView.setAdapter(adapter);

        //添加监听
        setListener();

    }

    private void setPage() {
        if (isFirstLoad||isRefresh){
            page = 0;
        }
        if (isLoadMore){
            page++;
        }
    }

    private void setListener() {

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //item被点击，跳转页面
                Intent intent = new Intent(getActivity(), ScoreDetailActivity.class);
                MainTrendingInfo.MainTrendingInfoList scoreInfo = (MainTrendingInfo.MainTrendingInfoList) adapter.getData().get(position);
                int scoreID = scoreInfo.getId();
                intent.putExtra("scoreID",scoreID);
                startActivity(intent);

            }
        });
        //上拉加载监听
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, scoreListView);

        //下拉刷新监听
        scoreRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }

    private void setEmptyView(){
        if (baseListScore==null||baseListScore.size()==0){
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

        if (mainScoreInfoData.isNoMore()) {
            adapter.addData(listScore);
            //数据全部加载完毕
            adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            adapter.addData(listScore);
            adapter.loadMoreComplete();
        }
    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(listScore);
        scoreRefreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }
}

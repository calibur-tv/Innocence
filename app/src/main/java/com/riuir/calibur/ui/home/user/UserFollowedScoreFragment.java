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
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.adapter.DramaScoreListAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.score.ScoreShowInfoActivity;
import com.riuir.calibur.ui.home.user.adapter.UserScoreListAdapter;

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

    private MainTrendingInfo.MainTrendingInfoData mainScoreInfoData;

    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;

    private int userId;
    private String zone;

    private UserScoreListAdapter adapter;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_user_followed_score;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        UserMainActivity activity = (UserMainActivity) getActivity();
        userId = activity.getUserId();
        zone = activity.getZone();
        setListAdapter();
        isFirstLoad = true;
        scoreRefreshLayout.setRefreshing(true);
        setNet();
    }

    private void setNet() {
        setPage();
        apiGet.getFollowList("score","news",0,zone,page,take,0,"").enqueue(new Callback<MainTrendingInfo>() {
            @Override
            public void onResponse(Call<MainTrendingInfo> call, Response<MainTrendingInfo> response) {
                LogUtils.d("score_fragment","response = "+response+",data = "+response.body().getData());
                if (response!=null&&response.isSuccessful()){
                    listScore = response.body().getData().getList();
                    mainScoreInfoData = response.body().getData();
                    if (isFirstLoad){
                        isFirstLoad = false;
                        scoreRefreshLayout.setRefreshing(false);
                        baseListScore = response.body().getData().getList();
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
                        scoreRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    if (isFirstLoad){
                        isFirstLoad = false;
                        scoreRefreshLayout.setRefreshing(false);
                    }

                }else {
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        scoreRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    if (isFirstLoad){
                        isFirstLoad = false;
                        scoreRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<MainTrendingInfo> call, Throwable t) {
                ToastUtils.showShort(getContext(),"请检查您的网络！");
                if (isLoadMore){
                    adapter.loadMoreFail();
                    isLoadMore = false;
                }
                if (isRefresh){
                    scoreRefreshLayout.setRefreshing(false);
                    isRefresh = false;
                }
                if (isFirstLoad){
                    isFirstLoad = false;
                    scoreRefreshLayout.setRefreshing(false);
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
                Intent intent = new Intent(getActivity(), ScoreShowInfoActivity.class);
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

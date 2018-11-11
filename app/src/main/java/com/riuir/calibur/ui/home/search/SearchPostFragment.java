package com.riuir.calibur.ui.home.search;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.anime.SearchAnimeInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.home.search.adapter.DramaSearchAdapter;
import com.riuir.calibur.ui.home.search.adapter.PostSearchAdapter;
import com.riuir.calibur.ui.home.user.UserMainActivity;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.tencent.bugly.crashreport.CrashReport;

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
public class SearchPostFragment extends BaseFragment {

    @BindView(R.id.search_post_fragment_list_view)
    RecyclerView listView;
    @BindView(R.id.search_post_fragment_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    PostSearchAdapter adapter;

    DramaSearchActivity activity;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    private String editContent = "";
    private int page = 0;
    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;

    private SearchAnimeInfo.SearchAnimeInfoData searchData;
    private List<SearchAnimeInfo.SearchAnimeInfoList> searchList;
    private List<SearchAnimeInfo.SearchAnimeInfoList> baseSearchList = new ArrayList<>();

    private Call<SearchAnimeInfo> searchCall;
    @Override
    protected int getContentViewID() {
        return R.layout.fragment_search_post;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        activity = (DramaSearchActivity) getActivity();

        setListAdapter();
        setEmptyView();
    }

    @Override
    public void onDestroy() {
        if (searchCall!=null){
            searchCall.cancel();
        }
        super.onDestroy();
    }

    private void setListAdapter() {
        adapter = new PostSearchAdapter(R.layout.main_card_list_item,baseSearchList,getContext());
        listView.setLayoutManager(new LinearLayoutManager(App.instance()));
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
        adapter.disableLoadMoreIfNotFullPage(listView);

        listView.setAdapter(adapter);

        //添加监听
        setListener();

    }

    private void setPage() {
        if (isFirstLoad){
            page = 0;
        }
        if (isRefresh){
            page = 0;
        }
        if (isLoadMore){
            page++;
        }
    }

    private void setNet() {
        setPage();
        searchCall = apiGet.getCallSearch(editContent,"post",page);
        searchCall.enqueue(new Callback<SearchAnimeInfo>() {
            @Override
            public void onResponse(Call<SearchAnimeInfo> call, Response<SearchAnimeInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    searchData = response.body().getData();
                    searchList = response.body().getData().getList();
                    if (isFirstLoad){
                        baseSearchList = response.body().getData().getList();
                        if (listView!=null){
                            setSearchFinish();
                            setEmptyView();
                        }
                    }
                    if (isLoadMore){
                        setLoadMore();
                    }
                    if (isRefresh){
                        setRefresh();
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
                        if (refreshLayout!=null){
                            refreshLayout.setRefreshing(false);
                            isRefresh = false;
                        }
                    }
                    setFailedView();
                }else {
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        if (refreshLayout!=null){
                            refreshLayout.setRefreshing(false);
                            isRefresh = false;
                        }
                    }
                    setFailedView();
                }
            }

            @Override
            public void onFailure(Call<SearchAnimeInfo> call, Throwable t) {
                if (call.isCanceled()){
                }else {
                    ToastUtils.showShort(getContext(),"请检查您的网络！");
                    CrashReport.postCatchedException(t);
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        refreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    setFailedView();
                }
            }
        });
    }

    private void setEmptyView(){

        if (baseSearchList==null||baseSearchList.size()==0){
            emptyView = new AppListEmptyView(getContext());
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            adapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 点击重试
        failedView = new AppListFailedView(getContext());
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        adapter.setEmptyView(failedView);
    }

    private void setListener() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //item被点击，跳转页面
                SearchAnimeInfo.SearchAnimeInfoList info = (SearchAnimeInfo.SearchAnimeInfoList) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), CardShowInfoActivity.class);
                int cardID = info.getId();
                intent.putExtra("cardID",cardID);
                startActivity(intent);

            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.main_card_list_item_user_icon:
                        SearchAnimeInfo.SearchAnimeInfoList info = (SearchAnimeInfo.SearchAnimeInfoList) adapter.getData().get(position);
                        Intent intent = new Intent(getContext(), UserMainActivity.class);
                        int animeId = info.getUser().getId();
                        String zone = info.getUser().getZone();
                        intent.putExtra("userId",animeId);
                        intent.putExtra("zone",zone);
                        startActivity(intent);
                        break;
                }
            }
        });
        //上拉加载监听
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, listView);

        //下拉刷新监听
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (TextUtils.isEmpty(editContent)){
                    ToastUtils.showShort(getContext(),"搜索内容为空，请输入搜索内容哦");
                    refreshLayout.setRefreshing(false);
                }else {
                    isRefresh = true;
                    setNet();
                }
            }
        });

        activity.setSearchPostStartListener(new DramaSearchActivity.SearchStartListener() {
            @Override
            public void startSearch(String searchContent) {
                if (editContent.equals(searchContent)&&adapter.getData().size()!=0){
                }else {
                    editContent = searchContent;
                    isFirstLoad = true;
                    setNet();
                    if (refreshLayout!=null){
                        refreshLayout.setRefreshing(true);
                    }
                }
            }
        });
    }

    private void setSearchFinish() {
        isFirstLoad = false;
        adapter.setNewData(baseSearchList);
        if (baseSearchList.size() == 0){
            emptyView.setText("找不到内容呢，\n试试别的吧");
        }
        if (refreshLayout!=null){
            refreshLayout.setRefreshing(false);
        }
    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(searchList);
        refreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }

    private void setLoadMore() {
        isLoadMore = false;

        if (searchData.isNoMore()) {
            adapter.addData(searchList);
            //数据全部加载完毕
            adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            adapter.addData(searchList);
            adapter.loadMoreComplete();
        }
    }
}

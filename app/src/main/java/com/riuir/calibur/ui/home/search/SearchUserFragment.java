package com.riuir.calibur.ui.home.search;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.ToastUtils;

import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;

import com.riuir.calibur.ui.home.search.adapter.UserSearchAdapter;
import com.riuir.calibur.ui.home.user.UserMainActivity;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.anime.SearchAnimeInfo;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchUserFragment extends BaseFragment {

    @BindView(R.id.search_user_fragment_list_view)
    RecyclerView listView;
    @BindView(R.id.search_user_fragment_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    UserSearchAdapter adapter;

    DramaSearchActivity activity;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    private String editContent = "";
    private int page = 0;
    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;

    private SearchAnimeInfo searchData;
    private List<SearchAnimeInfo.SearchAnimeInfoList> searchList;
    private List<SearchAnimeInfo.SearchAnimeInfoList> baseSearchList = new ArrayList<>();

    private Call<SearchAnimeInfo> searchCall;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_search_user;
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
        adapter = new UserSearchAdapter(R.layout.search_user_list_item,baseSearchList,getContext());
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
        apiService.getCallSearch(editContent,"user",page)
                .compose(Rx2Schedulers.<Response<ResponseBean<SearchAnimeInfo>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<SearchAnimeInfo>() {
                    @Override
                    public void onSuccess(SearchAnimeInfo searchAnimeInfo) {
                        searchData = searchAnimeInfo;
                        searchList = searchAnimeInfo.getList();
                        if (isFirstLoad){
                            baseSearchList = searchAnimeInfo.getList();
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
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (listView!=null){
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

                Intent intent = new Intent(getContext(), UserMainActivity.class);

                int userId = info.getId();
                String zone = info.getZone();
                intent.putExtra("userId",userId);
                intent.putExtra("zone",zone);
                startActivity(intent);

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

        activity.setSearchUserStartListener(new DramaSearchActivity.SearchStartListener() {
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

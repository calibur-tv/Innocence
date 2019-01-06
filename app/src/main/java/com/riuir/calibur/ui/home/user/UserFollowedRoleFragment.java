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
import com.riuir.calibur.ui.home.role.RoleDetailActivity;
import com.riuir.calibur.ui.home.user.adapter.FollowedRoleAdapter;
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
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowedRoleFragment extends BaseFragment {

    @BindView(R.id.user_main_follow_role_list_view)
    RecyclerView roleListView;

    @BindView(R.id.user_main_follow_role_refresh_layout)
    SwipeRefreshLayout roleRefreshLayout;

    private int userId;
    private String zone;

    private boolean isFirstLoad = false;
    private boolean isLoadMore = false;
    boolean isRefresh = false;

    private MainTrendingInfo roleInfoData;
    private List<MainTrendingInfo.MainTrendingInfoList> roleInfoList;
    private List<MainTrendingInfo.MainTrendingInfoList> baseRoleInfoList = new ArrayList<>();

    private FollowedRoleAdapter followedRoleAdapter;

    //page默认从0开始
    private int page = 0;

    private Call<MainTrendingInfo> listCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_user_followed_role;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        UserMainActivity activity = (UserMainActivity) getActivity();
        userId = activity.getUserId();
        zone = activity.getZone();
        baseRoleInfoList.clear();
        setAdapter();
        isFirstLoad = true;
        roleRefreshLayout.setRefreshing(true);
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
        folllowListParams.setType("role");
        folllowListParams.setSort("hot");
        folllowListParams.setBangumiId(0);
        folllowListParams.setUserZone(zone);
        folllowListParams.setPage(page);
        folllowListParams.setTake(0);
        folllowListParams.setMinId(0);
        folllowListParams.setSeenIds("");
        apiService.getFollowList(folllowListParams)
                .compose(Rx2Schedulers.<Response<ResponseBean<MainTrendingInfo>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<MainTrendingInfo>() {
                    @Override
                    public void onSuccess(MainTrendingInfo mainTrendingInfo) {
                        roleInfoData = mainTrendingInfo;
                        roleInfoList = mainTrendingInfo.getList();
                        if (isFirstLoad){
                            isFirstLoad = false;
                            baseRoleInfoList = mainTrendingInfo.getList();
                            if (roleRefreshLayout!=null&&roleListView!=null){
                                roleRefreshLayout.setRefreshing(false);
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
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (roleListView!=null){
                            if (isLoadMore){
                                followedRoleAdapter.loadMoreFail();
                                isLoadMore = false;
                            }
                            if (isRefresh){
                                roleRefreshLayout.setRefreshing(false);
                                isRefresh = false;
                            }
                            if (isFirstLoad){
                                isFirstLoad = false;
                                roleRefreshLayout.setRefreshing(false);
                            }
                            setFailedView();
                        }
                    }
                });

    }

    private void setPage() {
        if (isFirstLoad||isRefresh){
            page = 0;
        }else {
            page++;
        }
    }

    private void setAdapter() {
        followedRoleAdapter = new FollowedRoleAdapter(R.layout.user_follow_role_list_item,baseRoleInfoList,getContext());
        roleListView.setLayoutManager(new LinearLayoutManager(App.instance()));
        followedRoleAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        followedRoleAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        //开启上拉加载更多
        followedRoleAdapter.setEnableLoadMore(true);


        //添加底部footer
        followedRoleAdapter.setLoadMoreView(new MyLoadMoreView());
        followedRoleAdapter.disableLoadMoreIfNotFullPage(roleListView);

        roleListView.setAdapter(followedRoleAdapter);

        //添加监听
        setListener();
    }

    private void setListener() {
        followedRoleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MainTrendingInfo.MainTrendingInfoList data = (MainTrendingInfo.MainTrendingInfoList)adapter.getData().get(position);
                Intent intent = new Intent(getContext(), RoleDetailActivity.class);
                intent.putExtra("roleId",data.getId());
                startActivity(intent);
            }
        });

        //上拉加载监听
        followedRoleAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, roleListView);

        //下拉刷新监听
        roleRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }

    private void setEmptyView(){
        if (baseRoleInfoList==null||baseRoleInfoList.size()==0){
            emptyView = new AppListEmptyView(getContext());
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            followedRoleAdapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 下拉重试
        failedView = new AppListFailedView(getContext());
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        followedRoleAdapter.setEmptyView(failedView);
    }

    private void setLoadMore() {
        isLoadMore = false;

        if (roleInfoData.isNoMore()) {
            followedRoleAdapter.addData(roleInfoList);
            //数据全部加载完毕
            followedRoleAdapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            followedRoleAdapter.addData(roleInfoList);
            followedRoleAdapter.loadMoreComplete();
        }
    }

    private void setRefresh() {
        isRefresh = false;
        followedRoleAdapter.setNewData(roleInfoList);
        roleRefreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }
}

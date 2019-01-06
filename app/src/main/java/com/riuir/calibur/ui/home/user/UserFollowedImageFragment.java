package com.riuir.calibur.ui.home.user;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.adapter.ImageListAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.image.ImageDetailActivity;
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
public class UserFollowedImageFragment extends BaseFragment {


    private int userId;
    private String zone;

    private boolean isFirstLoad = false;
    private boolean isLoadMore = false;
    boolean isRefresh = false;

    @BindView(R.id.user_image_list_view)
    RecyclerView imageListView;

    @BindView(R.id.user_image_refresh_layout)
    SwipeRefreshLayout imageRefreshLayout;


    //用来动态改变RecyclerView的变量
    private List<MainTrendingInfo.MainTrendingInfoList> listImage;
    //传给Adapter的值 首次加载后不可更改 不然会导致数据出错
    private List<MainTrendingInfo.MainTrendingInfoList> baseListImage = new ArrayList<>();

    private MainTrendingInfo imageInfoData;

    private ImageListAdapter adapter;

    //page默认从0开始
    private int page = 0;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    private Call<MainTrendingInfo> listCall;
    @Override
    protected int getContentViewID() {
        return R.layout.fragment_user_followed_image;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        UserMainActivity activity = (UserMainActivity) getActivity();
        userId = activity.getUserId();
        zone = activity.getZone();
        baseListImage.clear();
        setListAdapter();
        isFirstLoad = true;
        imageRefreshLayout.setRefreshing(true);
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
        FolllowListParams params = new FolllowListParams();
        params.setType("image");
        params.setSort("news");
        params.setBangumiId(0);
        params.setUserZone(zone);
        params.setMinId(0);
        params.setPage(page);
        params.setTake(0);
        params.setSeenIds("");
        apiService.getFollowList(params)
                .compose(Rx2Schedulers.<Response<ResponseBean<MainTrendingInfo>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<MainTrendingInfo>(){

                    @Override
                    public void onSuccess(MainTrendingInfo mainTrendingInfo) {
                        listImage = mainTrendingInfo.getList();
                        imageInfoData = mainTrendingInfo;
                        if (isFirstLoad){
                            isFirstLoad = false;
                            baseListImage = mainTrendingInfo.getList();
                            if (imageRefreshLayout!=null&&imageListView!=null){
                                imageRefreshLayout.setRefreshing(false);
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
                        if (imageListView!=null){
                            if (isLoadMore){
                                adapter.loadMoreFail();
                                isLoadMore = false;
                            }
                            if (isRefresh){
                                imageRefreshLayout.setRefreshing(false);
                                isRefresh = false;
                            }
                            if (isFirstLoad){
                                isFirstLoad = false;
                                imageRefreshLayout.setRefreshing(false);
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

    private void setListAdapter() {
        adapter = new ImageListAdapter(R.layout.main_image_list_item,baseListImage,getContext());
        imageListView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
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
        adapter.disableLoadMoreIfNotFullPage(imageListView);

        imageListView.setAdapter(adapter);

        //添加监听
        setListener();

    }

    private void setListener() {

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //item被点击，跳转页面
                Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                MainTrendingInfo.MainTrendingInfoList imageInfo = (MainTrendingInfo.MainTrendingInfoList) adapter.getData().get(position);
                int imageID = imageInfo.getId();
                intent.putExtra("imageID",imageID);
                startActivity(intent);

            }
        });
        //上拉加载监听
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, imageListView);

        //下拉刷新监听
        imageRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }

    private void setEmptyView(){
        if (baseListImage==null||baseListImage.size()==0){
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

        if (imageInfoData.isNoMore()) {
            adapter.addData(listImage);
            //数据全部加载完毕
            adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            adapter.addData(listImage);
            adapter.loadMoreComplete();
        }
    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(listImage);
        imageRefreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }
}

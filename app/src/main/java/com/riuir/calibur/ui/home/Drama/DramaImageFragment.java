package com.riuir.calibur.ui.home.Drama;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
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
public class DramaImageFragment extends BaseFragment {


    String seenIds = "";
    List<Integer> seenIdList = new ArrayList<>();

    @BindView(R.id.drama_image_list_view)
    RecyclerView imageListView;

    @BindView(R.id.drama_image_refresh_layout)
    SwipeRefreshLayout imageRefreshLayout;

    //用来动态改变RecyclerView的变量
    private List<MainTrendingInfo.MainTrendingInfoList> listImage;
    //传给Adapter的值 首次加载后不可更改 不然会导致数据出错
    private List<MainTrendingInfo.MainTrendingInfoList> baseListImage = new ArrayList<>();

    private MainTrendingInfo mainImageInfoData;

    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;
    int bangumiID = 0;

    private ImageListAdapter adapter;

    private Call<MainTrendingInfo> listCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_image;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        DramaActivity dramaActivity = (DramaActivity) getActivity();
        bangumiID = dramaActivity.getAnimeID();
        isFirstLoad = true;
        imageRefreshLayout.setRefreshing(true);
        baseListImage.clear();
        setListAdapter();
        setNet();
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

        LogUtils.d("image_1","seenIds = "+seenIds );
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
        FolllowListParams folllowListParams = new FolllowListParams();
        folllowListParams.setType("image");
        folllowListParams.setSort("active");
        folllowListParams.setBangumiId(bangumiID);
        folllowListParams.setUserZone("");
        folllowListParams.setPage(0);
        folllowListParams.setTake(0);
        folllowListParams.setMinId(0);
        folllowListParams.setSeenIds(seenIds);

        apiService.getFollowList(folllowListParams)
                .compose(Rx2Schedulers.<Response<ResponseBean<MainTrendingInfo>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<MainTrendingInfo>() {
                    @Override
                    public void onSuccess(MainTrendingInfo mainTrendingInfo) {
                        listImage = mainTrendingInfo.getList();
                        mainImageInfoData = mainTrendingInfo;
                        if (isFirstLoad){
                            isFirstLoad = false;
                            baseListImage = mainTrendingInfo.getList();
                            if (imageRefreshLayout!=null&&imageListView!=null){
                                if (imageRefreshLayout!=null){
                                    imageRefreshLayout.setRefreshing(false);
                                }
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
                        for (MainTrendingInfo.MainTrendingInfoList hotItem :listImage){
                            seenIdList.add(hotItem.getId());
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
                                if (imageRefreshLayout!=null){
                                    imageRefreshLayout.setRefreshing(false);
                                }
                                isRefresh = false;
                            }
                            if (isFirstLoad){
                                if (imageRefreshLayout!=null){
                                    imageRefreshLayout.setRefreshing(false);
                                }
                                isFirstLoad = false;
                            }
                            setFailedView();
                        }
                    }
                });

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

    private void setEmptyView(){
        if (baseListImage==null||baseListImage.size()==0){
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

    private void setLoadMore() {
        isLoadMore = false;

        if (mainImageInfoData.isNoMore()) {
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
        if (imageRefreshLayout!=null){
            imageRefreshLayout.setRefreshing(false);
        }
        ToastUtils.showShort(getContext(),"刷新成功！");
    }
}

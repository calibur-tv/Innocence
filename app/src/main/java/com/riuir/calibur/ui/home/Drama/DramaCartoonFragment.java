package com.riuir.calibur.ui.home.Drama;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.data.anime.CartoonListInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.adapter.DramaCartoonListAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.image.ImageShowInfoActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class DramaCartoonFragment extends BaseFragment {

    @BindView(R.id.fragment_cartoon_list_view)
    RecyclerView cartoonListView;
    @BindView(R.id.fragment_cartoon_refresh)
    SwipeRefreshLayout refreshLayout;
    private int page = 0;
    private int take = 0;
    private String sort = "desc";

    private Call<CartoonListInfo> callCartoonList;
    CartoonListInfo.CartoonListInfoData listInfoData;
    List<CartoonListInfo.CartoonListInfoList> baseCartoonList = new ArrayList<>();
    List<CartoonListInfo.CartoonListInfoList> cartoonList;

    DramaCartoonListAdapter cartoonListAdapter;

    int bangumiID = 0;
    private boolean isFristLoad = false;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_cartoon;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        DramaActivity dramaActivity = (DramaActivity) getActivity();
        bangumiID = dramaActivity.getAnimeID();
        setAdapter();
        setListener();
        isFristLoad = true;
        refreshLayout.setRefreshing(true);
        setNet();
    }

    @Override
    public void onDestroy() {
        if (callCartoonList!=null){
            callCartoonList.cancel();
        }
        super.onDestroy();
    }

    private void setPage() {
        if (isLoadMore){
            page++;
        }
        if (isRefresh){
            page = 0;
        }
    }

    private void setNet() {
        setPage();
        callCartoonList = apiGet.getCartoonList(bangumiID,take,page,"");
        callCartoonList.enqueue(new Callback<CartoonListInfo>() {
            @Override
            public void onResponse(Call<CartoonListInfo> call, Response<CartoonListInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    listInfoData = response.body().getData();
                    cartoonList = response.body().getData().getList();
                    if (isFristLoad){
                        baseCartoonList = response.body().getData().getList();
                    }
                    setLoadComplete();
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
                    setLoadFailed();
                }else{
                    setLoadFailed();
                    ToastUtils.showShort(getContext(),"获取不到服务器");
                }
            }

            @Override
            public void onFailure(Call<CartoonListInfo> call, Throwable t) {
                if (call.isCanceled()){
                }else {
                    setLoadFailed();
                    ToastUtils.showShort(getContext(),"请检查您的网络");
                }
            }
        });
    }

    private void setAdapter() {
        cartoonListAdapter = new DramaCartoonListAdapter(R.layout.drama_cartoon_list_item_layout,baseCartoonList,getContext());
        cartoonListView.setLayoutManager(new GridLayoutManager(getContext(),3));
        cartoonListAdapter.setHasStableIds(true);
        cartoonListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //开启上拉加载更多
        cartoonListAdapter.setEnableLoadMore(true);
        //添加底部footer
        cartoonListAdapter.setLoadMoreView(new MyLoadMoreView());
        cartoonListAdapter.disableLoadMoreIfNotFullPage(cartoonListView);
        cartoonListView.setAdapter(cartoonListAdapter);
    }

    private void setListener() {
        cartoonListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        },cartoonListView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
        cartoonListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //item被点击，跳转页面
                Intent intent = new Intent(getActivity(), DramaCartoonShowActivity.class);
                CartoonListInfo.CartoonListInfoList info = (CartoonListInfo.CartoonListInfoList) adapter.getData().get(position);
                int imageID = info.getId();
                intent.putExtra("cartoonId",imageID);
                startActivity(intent);

            }
        });
    }

    private void setLoadComplete(){
        if (cartoonListAdapter!=null){
            if (isFristLoad){
                isFristLoad = false;
                if (refreshLayout!=null){
                    refreshLayout.setRefreshing(false);
                }
                cartoonListAdapter.setNewData(baseCartoonList);
            }else if (isRefresh){
                isRefresh = false;
                if (refreshLayout!=null){
                    refreshLayout.setRefreshing(false);
                }
                cartoonListAdapter.setNewData(cartoonList);
            }else if (isLoadMore){
                isLoadMore = false;
                if (listInfoData.isNoMore()) {
                    cartoonListAdapter.addData(cartoonList);
                    //数据全部加载完毕
                    cartoonListAdapter.loadMoreEnd();
                } else {
                    //成功获取更多数据
                    cartoonListAdapter.addData(cartoonList);
                    cartoonListAdapter.loadMoreComplete();
                }
            }
        }
    }

    private void setLoadFailed(){
        if (refreshLayout!=null){
            if (isFristLoad){
                isFristLoad = false;
            }
            if (isRefresh){
                isRefresh = false;
            }
            refreshLayout.setRefreshing(false);
        }
        if (isLoadMore){
            isLoadMore = false;
            if (cartoonListAdapter!=null){
                cartoonListAdapter.loadMoreFail();
            }
        }
    }
}

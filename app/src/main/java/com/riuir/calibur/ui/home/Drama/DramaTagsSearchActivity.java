package com.riuir.calibur.ui.home.Drama;

import android.content.Intent;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.AnimeListForTagsSearch;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.adapter.DramaTagsAnimeListAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
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

public class DramaTagsSearchActivity extends BaseActivity {

    @BindView(R.id.drama_tags_search_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.drama_tags_search_back_btn)
    ImageView backBtn;
    @BindView(R.id.drama_tags_search_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.drama_tags_search_title_tags_text)
    TextView tagsText;

    AnimeListForTagsSearch animeListForTagsSearch = new AnimeListForTagsSearch();
    //用来动态改变RecyclerView的变量
    List<AnimeListForTagsSearch.AnimeListForTagsSearchData> animeListForTagsSearchesData = new ArrayList<>();
    //传给Adapter的值 首次加载后不可更改
    List<AnimeListForTagsSearch.AnimeListForTagsSearchData> baseAnimeListForTagsSearchesData = new ArrayList<>();

    boolean isRefresh = false;
    boolean isLoadMore = false;
    boolean isFirstLoad = false;

    Call<AnimeListForTagsSearch> callTagsSearch;

    DramaTagsAnimeListAdapter adapter;

    String tagsIDStr;
    String tagsNameStr;

    int page;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_tags_search;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        tagsIDStr = intent.getStringExtra("tagsIDStr");
        tagsNameStr = intent.getStringExtra("tagsNameStr");
        tagsText.setText(tagsNameStr);
        refreshLayout.setRefreshing(true);
        isFirstLoad = true;
        setAdapter();
        setNet();
    }

    @Override
    public void onDestroy() {
        if (callTagsSearch!=null){
            callTagsSearch.cancel();
        }
        super.onDestroy();
    }

    private void setNet() {
        setPage();
        callTagsSearch = apiGet.getCallSearchDramaForTags(tagsIDStr,page+"");
        callTagsSearch.enqueue(new Callback<AnimeListForTagsSearch>() {
                @Override
                public void onResponse(Call<AnimeListForTagsSearch> call, Response<AnimeListForTagsSearch> response) {
                    if (response!=null&&response.isSuccessful()){
                        animeListForTagsSearch = response.body();
                        animeListForTagsSearchesData = response.body().getData().getList();
                        if (isFirstLoad){
                            baseAnimeListForTagsSearchesData = response.body().getData().getList();
                            refreshLayout.setRefreshing(false);
                            isFirstLoad = false;
                            setAdapter();
                            setEmptyView();
                        }
                        if (isRefresh){
                            setRefresh();
                        }
                        if (isLoadMore){
                            setLoadMore();
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
                        ToastUtils.showShort(DramaTagsSearchActivity.this,info.getMessage());
                        if (isRefresh){
                            isRefresh = false;
                            refreshLayout.setRefreshing(false);
                        }
                        if (isLoadMore){
                            isLoadMore = false;
                            adapter.loadMoreFail();
                        }
                        setFailedView();
                    }else {
                        ToastUtils.showShort(DramaTagsSearchActivity.this,"未知错误导致加载失败");
                        if (isRefresh){
                            isRefresh = false;
                            refreshLayout.setRefreshing(false);
                        }
                        if (isLoadMore){
                            isLoadMore = false;
                            adapter.loadMoreFail();
                        }
                        setFailedView();
                    }
                }

                @Override
                public void onFailure(Call<AnimeListForTagsSearch> call, Throwable t) {
                    if (call.isCanceled()){
                    }else {
                        ToastUtils.showShort(DramaTagsSearchActivity.this,"请检查您的网络哟~");
                        LogUtils.d("tagSearch","t = "+t.getMessage());
                        CrashReport.postCatchedException(t);
                        if (isRefresh){
                            isRefresh = false;
                            refreshLayout.setRefreshing(false);
                        }
                        if (isLoadMore){
                            isLoadMore = false;
                            adapter.loadMoreFail();
                        }
                        setFailedView();
                    }
                }
            });
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

    private void setAdapter() {
        adapter = new DramaTagsAnimeListAdapter(R.layout.drama_timeline_list_item,baseAnimeListForTagsSearchesData,DramaTagsSearchActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(DramaTagsSearchActivity.this));
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

        //添加底部footer
        adapter.setEnableLoadMore(true);
        adapter.setLoadMoreView(new MyLoadMoreView());
        adapter.disableLoadMoreIfNotFullPage(recyclerView);


        recyclerView.setAdapter(adapter);

        seyListener();
    }

    private void seyListener() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //下拉刷新监听
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
        //上拉加载监听
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, recyclerView);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AnimeListForTagsSearch.AnimeListForTagsSearchData data = (AnimeListForTagsSearch.AnimeListForTagsSearchData)adapter.getData().get(position);
                int animeId = data.getId();
                Intent intent = new Intent(DramaTagsSearchActivity.this,DramaActivity.class);
                intent.putExtra("animeId",animeId);
                startActivity(intent);
            }
        });

    }

    private void setEmptyView(){
        if (baseAnimeListForTagsSearchesData==null||baseAnimeListForTagsSearchesData.size()==0){
            emptyView = new AppListEmptyView(DramaTagsSearchActivity.this);
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            adapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 下拉重试
        failedView = new AppListFailedView(DramaTagsSearchActivity.this);
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        adapter.setEmptyView(failedView);

    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(animeListForTagsSearchesData);
        refreshLayout.setRefreshing(false);
        ToastUtils.showShort(DramaTagsSearchActivity.this,"刷新成功！");

    }

    private void setLoadMore() {
        isLoadMore = false;
        if (animeListForTagsSearch.getData().isNoMore()) {
            //最后一次加载
            adapter.addData(animeListForTagsSearchesData);
            //数据全部加载完毕
            adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            LogUtils.d("cardShow","list = "+animeListForTagsSearchesData.toString());
            adapter.addData(animeListForTagsSearchesData);
            adapter.loadMoreComplete();
        }
    }

    @Override
    protected void handler(Message msg) {

    }
}

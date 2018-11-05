package com.riuir.calibur.ui.home;


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;


import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;

import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.AnimeListForTimeLine;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.view.ChildListView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.utils.GlideUtils;
import com.tencent.bugly.crashreport.CrashReport;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaTimelineFragment extends BaseFragment {

    @BindView(R.id.drama_timeline_list)
    RecyclerView timeLineListView;
    @BindView(R.id.drama_timeline_refresh_layout)
    SwipeRefreshLayout timeLineRefreshLayout;
    public final static int TYPE_GROUP = 0;
    public final static int TYPE_CHILD = 1;

    //timeLineList 动态改变 用于传值
    List<MultiItemEntity> timeLineList;
    //baseTimeLineList首次加载的时候添加完数据后保持不变（默认与adapter中的DATA关联）
    List<MultiItemEntity> baseTimeLineList = new ArrayList<>();

    List<AnimeListForTimeLine.AnimeClassifyForDate> groupList;

    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private boolean isFirstLoad = false;

    TimeLineAdapter adapter;

    int year = TimeUtils.getYear();
    int howLongYear = 1;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_timeline;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        timeLineRefreshLayout.setRefreshing(true);
        setDramaAdapter();
        setNet();
    }



    private void setNet() {
        if (isRefresh){
            year = TimeUtils.getYear();
        }
        apiGet.getCallDramaTimeGet(year,howLongYear).enqueue(new Callback<AnimeListForTimeLine>() {
            @Override
            public void onResponse(Call<AnimeListForTimeLine> call, Response<AnimeListForTimeLine> response) {
                if (response!=null&&response.isSuccessful()){
                    groupList =  response.body().getData().getList();

                    year -= howLongYear;
                    if (isFirstLoad){
                        if (timeLineRefreshLayout!=null&&adapter!=null){
                            setFirstData();
                            timeLineRefreshLayout.setRefreshing(false);
                        }
                    }
                    if (isRefresh){
                        setRefresh();
                    }
                    if (isLoadMore){
                        setLoadMore();
                    }
                    setEmptyView();
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
                    if (isRefresh){
                        if (timeLineRefreshLayout!=null){
                            timeLineRefreshLayout.setRefreshing(false);
                        }
                        isRefresh = false;
                    }
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isFirstLoad){
                        if (timeLineRefreshLayout!=null){
                            timeLineRefreshLayout.setRefreshing(false);
                        }
                    }
                    setFailedView();
                }else {
                    if (isRefresh){
                        if (timeLineRefreshLayout!=null){
                            timeLineRefreshLayout.setRefreshing(false);
                        }
                        isRefresh = false;
                    }
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isFirstLoad){
                        if (timeLineRefreshLayout!=null){
                            timeLineRefreshLayout.setRefreshing(false);
                        }
                    }
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了");
                    setFailedView();
                }
            }

            @Override
            public void onFailure(Call<AnimeListForTimeLine> call, Throwable t) {
                ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                LogUtils.v("AppNetErrorMessage","drama time line t = "+t.getMessage());
                CrashReport.postCatchedException(t);
                if (isRefresh){
                    if (timeLineRefreshLayout!=null){
                        timeLineRefreshLayout.setRefreshing(false);
                    }
                    isRefresh = false;
                }
                if (isLoadMore){
                    adapter.loadMoreFail();
                    isLoadMore = false;
                }
                if (isFirstLoad){
                    if (timeLineRefreshLayout!=null){
                        timeLineRefreshLayout.setRefreshing(false);
                    }
                }
                setFailedView();
            }
        });
    }

    private void setEmptyView(){
        if (baseTimeLineList==null||baseTimeLineList.size()==0){
            if (emptyView == null){
                emptyView = new AppListEmptyView(getContext());
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            adapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 下拉重试
        if (failedView == null){
            failedView = new AppListFailedView(getContext());
            failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        adapter.setEmptyView(failedView);
    }

    private void setLoadMore() {

        //TODO 等获取到条目总数之后改为 listDataCounter >=  TOTAL_COUNTER
        if (year < 1957 ) {
            //数据全部加载完毕
           adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            LogUtils.d("TimeLine","all list view 1 = "+adapter.getData().toString());
            setList();

            adapter.addData(timeLineList);
            LogUtils.d("TimeLine","all list view 2 = "+adapter.getData().toString());
            adapter.loadMoreComplete();


        }

        isLoadMore = false;
    }

    private void setRefresh() {
        isRefresh = false;
        setList();
        adapter.setNewData(timeLineList);
        if (timeLineRefreshLayout!=null){
            timeLineRefreshLayout.setRefreshing(false);
        }
        ToastUtils.showShort(getContext(),"刷新成功！");

    }

    private void setDramaAdapter() {
        LogUtils.d("TimeLine","setAdapter ");

        setList();
        adapter = new TimeLineAdapter(baseTimeLineList);
        timeLineListView.setLayoutManager(new LinearLayoutManager(App.instance()));
        adapter.setHasStableIds(true);

        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //开启上拉加载更多
        adapter.setEnableLoadMore(true);
        //添加底部footer
        adapter.setLoadMoreView(new MyLoadMoreView());
        adapter.disableLoadMoreIfNotFullPage(timeLineListView);
        adapter.expandAll();
        timeLineListView.setAdapter(adapter);

        setListener();
    }

    private void setFirstData(){
        setList();
        adapter.addData(baseTimeLineList);
        isFirstLoad = false;
    }

    private void setList() {

        if (timeLineList == null){
            timeLineList = new ArrayList<>();
            baseTimeLineList = new ArrayList<>();
        }

        if (timeLineList.size()!=0){
            timeLineList.clear();
        }

        if (groupList!=null&&groupList.size()!=0){
            for (AnimeListForTimeLine.AnimeClassifyForDate groupItem:groupList){
                timeLineList.add(groupItem);
                if (isFirstLoad){
                    baseTimeLineList.add(groupItem);

                }
                for (AnimeListForTimeLine.AnimeInfo childItem:groupItem.getList()){
                    timeLineList.add(childItem);
                    if (isFirstLoad){
                        baseTimeLineList.add(childItem);
                    }
                }
            }
        }
        LogUtils.d("TimeLine","base = "+baseTimeLineList.toString());
        LogUtils.d("TimeLine","timeLineList = "+timeLineList.toString());
    }

    private void setListener() {
        //上拉加载监听
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, timeLineListView);

        //下拉刷新监听
        timeLineRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }




    class TimeLineAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity,BaseViewHolder> {


        /**
         * Same as QuickAdapter#QuickAdapter(Context,int) but with
         * some initialization data.
         *
         * @param data A new list is created out of this one to avoid mutable list
         */
        public TimeLineAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(TYPE_GROUP,R.layout.drama_timeline_time_title);
            addItemType(TYPE_CHILD,R.layout.drama_timeline_list_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, MultiItemEntity  item) {

            switch (helper.getItemViewType()){
                case TYPE_GROUP:
                    AnimeListForTimeLine.AnimeClassifyForDate itemGroup = (AnimeListForTimeLine.AnimeClassifyForDate) item;
                    helper.setText(R.id.drama_timeline_title_time,itemGroup.getDate());
                    break;
                case TYPE_CHILD:
                    final AnimeListForTimeLine.AnimeInfo itemChild = (AnimeListForTimeLine.AnimeInfo) item;
                    helper.setText(R.id.drama_timeline_list_item_name,itemChild.getName());
                    helper.setText(R.id.drama_timeline_list_item_summary,itemChild.getSummary());
                    ImageView imageView = helper.getView(R.id.drama_timeline_list_item_image);
                    LogUtils.d("timelineAvatar","width = "+imageView.getLayoutParams().width);
                    GlideUtils.loadImageViewRoundedCorners(getContext(),
                            GlideUtils.setImageUrlForWidth(getContext(),itemChild.getAvatar(), imageView.getLayoutParams().width),
                            imageView,6);

                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int animeId = itemChild.getId();
                            Intent intent = new Intent(getActivity(),DramaActivity.class);
                            intent.putExtra("animeId",animeId);
                            startActivity(intent);
                        }
                    });
                    break;
            }
        }
    }

}

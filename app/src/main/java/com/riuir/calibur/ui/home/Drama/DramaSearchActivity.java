package com.riuir.calibur.ui.home.Drama;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.KeyBoardUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.data.anime.SearchAnimeInfo;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.adapter.DramaSearchAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.score.ScoreShowInfoActivity;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaSearchActivity extends BaseActivity {

    @BindView(R.id.drama_search_activity_edit_text)
    EditText searchEdit;
    @BindView(R.id.drama_search_activity_btn)
    ImageView searchBtn;
    @BindView(R.id.drama_search_activity_back)
    ImageView backBtn;

    @BindView(R.id.drama_search_activity_list_view)
    RecyclerView listView;

    @BindView(R.id.drama_search_activity_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.drama_search_activity_clear_edit)
    TextView editClear;

    private String editContent;
    private int page = 0;

    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;

    private SearchAnimeInfo.SearchAnimeInfoData searchData;
    private List<SearchAnimeInfo.SearchAnimeInfoList> searchList;
    private List<SearchAnimeInfo.SearchAnimeInfoList> baseSearchList = new ArrayList<>();

    DramaSearchAdapter adapter;

    private Call<SearchAnimeInfo> searchCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_search;
    }

    @Override
    protected void onInit() {
        searchEdit.requestFocus();
        setEditListener();
        setListAdapter();
        setEmptyView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchEdit.requestFocus();
    }

    private void setEditListener() {
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    KeyBoardUtils.closeKeybord(searchEdit,DramaSearchActivity.this);
                    editContent = searchEdit.getText().toString();
                    if (TextUtils.isEmpty(editContent)){
                        ToastUtils.showShort(DramaSearchActivity.this,"搜索内容为空，请输入搜索内容哦");
                        return true;
                    }
                    searchBtn.setClickable(false);
                    isFirstLoad = true;
                    setNet();
                    return true;
                }
                return false;
            }
        });
        searchEdit.addTextChangedListener(textWatcher);
        editClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.setText("");
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContent = searchEdit.getText().toString();
                if (TextUtils.isEmpty(editContent)){
                    ToastUtils.showShort(DramaSearchActivity.this,"搜索内容为空，请输入搜索内容哦");
                }else {
                    searchBtn.setClickable(false);
                    isFirstLoad = true;
                    KeyBoardUtils.closeKeybord(searchEdit,DramaSearchActivity.this);
                    setNet();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    private void setNet() {
        searchEdit.clearFocus();
        setPage();
        searchCall = apiGet.getCallSearch(editContent,"bangumi",page);
        searchCall.enqueue(new Callback<SearchAnimeInfo>() {
            @Override
            public void onResponse(Call<SearchAnimeInfo> call, Response<SearchAnimeInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    searchData = response.body().getData();
                    searchList = response.body().getData().getList();
                    if (isFirstLoad){
                        baseSearchList = response.body().getData().getList();
                        if (listView!=null){
//                            setListAdapter();
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

                }else if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);

                    ToastUtils.showShort(DramaSearchActivity.this,info.getMessage());
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        refreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    setFailedView();
                }else {
                    ToastUtils.showShort(DramaSearchActivity.this,"未知原因导致加载失败了！");
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

            @Override
            public void onFailure(Call<SearchAnimeInfo> call, Throwable t) {
                if (call.isCanceled()){
                }else {
                    ToastUtils.showShort(DramaSearchActivity.this,"请检查您的网络！");
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




    private void setListAdapter() {
        adapter = new DramaSearchAdapter(R.layout.drama_timeline_list_item,baseSearchList,DramaSearchActivity.this);
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
//        searchBtn.setClickable(true);
//        isFirstLoad = false;
    }

    private void setListener() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //item被点击，跳转页面
                SearchAnimeInfo.SearchAnimeInfoList info = (SearchAnimeInfo.SearchAnimeInfoList) adapter.getData().get(position);

                Intent intent = new Intent(DramaSearchActivity.this, DramaActivity.class);

                int animeId = info.getId();

                intent.putExtra("animeId",animeId);
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
                    ToastUtils.showShort(DramaSearchActivity.this,"搜索内容为空，请输入搜索内容哦");
                    refreshLayout.setRefreshing(false);
                }else {
                    isRefresh = true;
                    KeyBoardUtils.closeKeybord(searchEdit,DramaSearchActivity.this);
                    setNet();
                }
            }
        });
    }

    private void setEmptyView(){

        if (baseSearchList==null||baseSearchList.size()==0){
            if (emptyView == null){
                emptyView = new AppListEmptyView(DramaSearchActivity.this);
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            adapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 点击重试
        if (failedView == null){
            failedView = new AppListFailedView(DramaSearchActivity.this);
            failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        adapter.setEmptyView(failedView);


    }

    private void setSearchFinish() {
        isFirstLoad = false;
        searchBtn.setClickable(true);
        adapter.setNewData(baseSearchList);
        if (baseSearchList.size() == 0){
            ToastUtils.showShort(DramaSearchActivity.this,"没有找到内容呢，试试搜索别的吧");
        }
    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(searchList);
        refreshLayout.setRefreshing(false);
        ToastUtils.showShort(DramaSearchActivity.this,"刷新成功！");
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

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length()!=0){
                editClear.setVisibility(View.VISIBLE);
            }else {
                editClear.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void handler(Message msg) {

    }
}

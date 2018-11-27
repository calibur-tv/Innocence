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
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.trending.ImageShowInfoPrimacy;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.comment.TrendingShowInfoCommentMain;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaCartoonCommentActivity extends BaseActivity {

    @BindView(R.id.drama_cartoon_comment_back_btn)
    ImageView backBtn;
    @BindView(R.id.drama_cartoon_comment_title_name)
    TextView titleName;
    @BindView(R.id.drama_cartoon_comment_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.drama_cartoon_comment_list_view)
    RecyclerView commentListView;
    @BindView(R.id.drama_cartoon_comment_comment_view)
    ReplyAndCommentView commentView;

    private CommentAdapter commentAdapter;

    private TrendingShowInfoCommentMain commentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList = new ArrayList<>();
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> commentMainList;

    private ImageShowInfoPrimacy.ImageShowInfoPrimacyData cartoonData;
    private int cartoonId;

    private Call<TrendingShowInfoCommentMain> commentMainCall;


    int fetchId = 0;

    boolean isLoadMore = false;
    boolean isFirstLoad = false;
    boolean isRefresh = false;

    AppListFailedView failedView;
    AppListEmptyView emptyView;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_cartoon_comment;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        cartoonData = (ImageShowInfoPrimacy.ImageShowInfoPrimacyData) intent.getSerializableExtra("cartoonData");
        cartoonId = cartoonData.getId();
        setView();
        setAdapter();
        setCommentView();
        isFirstLoad = true;
        setNet();
        setListener();
    }

    @Override
    public void onDestroy() {
        if (commentMainCall!=null){
            commentMainCall.cancel();
        }
        super.onDestroy();
    }

    private void setView() {
        titleName.setText("第"+cartoonData.getPart()+"话："+cartoonData.getName()+" 评论");
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        commentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        },commentListView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });

        commentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> dataList =  adapter.getData();

                if (view.getId() == R.id.card_show_info_list_comment_item_user_icon){
                    UserMainUtils.toUserMainActivity(DramaCartoonCommentActivity.this,
                            dataList.get(position).getFrom_user_id(),dataList.get(position).getFrom_user_zone());
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_reply){
                    int commentId = dataList.get(position).getId();
                    Intent intent = new Intent(DramaCartoonCommentActivity.this,CardChildCommentActivity.class);
                    intent.putExtra("id",commentId);
                    intent.putExtra("mainComment",dataList.get(position));
                    intent.putExtra("type","image");
                    startActivity(intent);
                }
            }
        });
        commentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> dataList =  adapter.getData();
                int commentId = dataList.get(position).getId();
                Intent intent = new Intent(DramaCartoonCommentActivity.this,CardChildCommentActivity.class);
                intent.putExtra("id",commentId);
                intent.putExtra("mainComment",dataList.get(position));
                intent.putExtra("type","image");
                startActivity(intent);
            }
        });

    }

    private void setAdapter() {
        commentAdapter = new CommentAdapter(R.layout.card_show_info_list_comment_item,baseCommentMainList,
                DramaCartoonCommentActivity.this,apiPost,CommentAdapter.TYPE_IMAGE,cartoonId);
        commentListView.setLayoutManager(new LinearLayoutManager(DramaCartoonCommentActivity.this));
        commentAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        commentAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        //添加底部footer
        commentAdapter.setEnableLoadMore(true);
        commentAdapter.setLoadMoreView(new MyLoadMoreView());
        commentAdapter.disableLoadMoreIfNotFullPage(commentListView);

        commentListView.setAdapter(commentAdapter);
    }

    private void setNet() {
        setFetchID();
        apiService.getCallMainComment("image",cartoonId,fetchId,0)
                .compose(Rx2Schedulers.<Response<ResponseBean<TrendingShowInfoCommentMain>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<TrendingShowInfoCommentMain>() {
                    @Override
                    public void onSuccess(TrendingShowInfoCommentMain trendingShowInfoCommentMain) {
                        commentMainData = trendingShowInfoCommentMain;
                        commentMainList = trendingShowInfoCommentMain.getList();
                        if (isFirstLoad){
                            baseCommentMainList = trendingShowInfoCommentMain.getList();
                        }
                        setLoadComplete();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (refreshLayout!=null){
                            setLoadFailed();
                            setFailedView();
                        }
                    }
                });
    }

    private void setFetchID() {
        if (isLoadMore){
            fetchId = commentAdapter.getData().get(commentAdapter.getData().size()-1).getId();
        }
        if (isFirstLoad){
            fetchId = 0;
        }
        if (isRefresh){
            fetchId = 0;
        }
        LogUtils.d("cardShow","fetchID = "+fetchId);
    }

    private void setLoadComplete(){
        if (commentAdapter!=null){
            if (isFirstLoad){
                isFirstLoad = false;
                if (refreshLayout!=null){
                    refreshLayout.setRefreshing(false);
                }
                commentAdapter.setNewData(baseCommentMainList);
            }else if (isRefresh){
                isRefresh = false;
                if (refreshLayout!=null){
                    refreshLayout.setRefreshing(false);
                }
                commentAdapter.setNewData(commentMainList);
            }else if (isLoadMore){
                isLoadMore = false;
                if (commentMainData.isNoMore()) {
                    commentAdapter.addData(commentMainList);
                    //数据全部加载完毕
                    commentAdapter.loadMoreEnd();
                } else {
                    //成功获取更多数据
                    commentAdapter.addData(commentMainList);
                    commentAdapter.loadMoreComplete();
                }
            }
            setEmptyView();
        }
    }


    private void setLoadFailed(){
        if (refreshLayout!=null){
            if (isFirstLoad){
                isFirstLoad = false;
            }
            if (isRefresh){
                isRefresh = false;
            }
            refreshLayout.setRefreshing(false);
        }
        if (isLoadMore){
            isLoadMore = false;
            if (commentAdapter!=null){
                commentAdapter.loadMoreFail();
            }
        }
    }

    private void setEmptyView(){
        if (baseCommentMainList==null||baseCommentMainList.size()==0){
            emptyView = new AppListEmptyView(DramaCartoonCommentActivity.this);
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            commentAdapter.setEmptyView(emptyView);
        }
    }

    private void setCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_MAIN_COMMENT);
        commentView.setApiPost(apiPost);
        commentView.setCommentAdapter(commentAdapter);
        commentView.setFromUserName("");
        commentView.setId(cartoonId);
        commentView.setTitleId(cartoonData.getUser().getId());
        commentView.setType(ReplyAndCommentView.TYPE_IMAGE);
        commentView.setTargetUserId(0);
        commentView.setIs_creator(cartoonData.isIs_creator());
        commentView.setLiked(cartoonData.isLiked());
        commentView.setRewarded(cartoonData.isRewarded());
        commentView.setMarked(cartoonData.isMarked());

        commentView.setNetAndListener();
    }

    private void setFailedView(){
        //加载失败 下拉重试
        failedView = new AppListFailedView(DramaCartoonCommentActivity.this);
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        commentAdapter.setEmptyView(failedView);
    }

    @Override
    protected void handler(Message msg) {

    }
}

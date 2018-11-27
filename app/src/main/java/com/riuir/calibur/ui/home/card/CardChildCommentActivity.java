package com.riuir.calibur.ui.home.card;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;


import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.image.ImageShowInfoActivity;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.GlideUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.comment.TrendingChildCommentInfo;
import calibur.core.http.models.comment.TrendingShowInfoCommentMain;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardChildCommentActivity extends BaseActivity {

    @BindView(R.id.card_child_comment_header_icon)
    ImageView mainCommentUserIcon;
    @BindView(R.id.card_child_comment_header_name)
    TextView mainCommentUserName;
    @BindView(R.id.card_child_comment_header_card_master)
    TextView titleCardMaster;
    @BindView(R.id.card_child_comment_header_bangumi_master)
    ImageView titleBangumiMaster;
    @BindView(R.id.card_child_comment_header_executer)
    ImageView titleExecuter;
    @BindView(R.id.card_child_comment_header_info)
    TextView mainCommentInfo;
    @BindView(R.id.card_child_comment_header_more)
    TextView mainCommentMore;
    @BindView(R.id.card_child_comment_header_comment_text)
    TextView mainCommentText;
    @BindView(R.id.card_child_comment_header_comment_image_layout)
    LinearLayout commentImageLayout;
    @BindView(R.id.card_child_comment_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.card_child_comment_header_child_comment_list)
    RecyclerView childCommentListView;
    @BindView(R.id.card_child_comment_header_comment_upovte_text)
    TextView likeCount;

    @BindView(R.id.card_child_comment_reply_layout)
    ReplyAndCommentView replyView;
    @BindView(R.id.card_child_comment_back_btn)
    ImageView backBtn;

    CardChildCommentListAdapter childCommentListAdapter;

    TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList mainComment;

    TrendingChildCommentInfo childCommentInfoData;
    List<TrendingChildCommentInfo.TrendingChildCommentInfoList> baseChildList = new ArrayList<>();
    List<TrendingChildCommentInfo.TrendingChildCommentInfoList> childList;

    String commentReplyToUserName = "";
    int commentReplyToUserId = 0;

    String commentReplyString;

    int commentId;
    int maxId;
    String type;
    boolean isFirstLoad =false;
    boolean isLoadMore = false;
    boolean isRefresh = false;

    private static final int NET_STATUS_GET_LIST = 0;
    private static final int NET_STATUS_REPLY_COMMENT = 1;

    private Call<TrendingChildCommentInfo> childCommentInfoCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    private static CardChildCommentActivity instance;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_child_comment;
    }

    @Override
    protected void onInit() {
        instance = this;
        Intent intent = getIntent();
        commentId = intent.getIntExtra("id",0);
        type = intent.getStringExtra("type");
        mainComment = (TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList) intent.getSerializableExtra("mainComment");

        childCommentListView.setNestedScrollingEnabled(false);
        setView();
        setAdapter();
        isFirstLoad = true;
        setNet(NET_STATUS_GET_LIST);
    }

    private void setView() {
        commentImageLayout.removeAllViews();
        GlideUtils.loadImageViewCircle(CardChildCommentActivity.this,mainComment.getFrom_user_avatar(),mainCommentUserIcon);
        mainCommentUserName.setText(mainComment.getFrom_user_name().replace("\n",""));
        mainCommentInfo.setText("第"+mainComment.getFloor_count()+"楼·"+ TimeUtils.HowLongTimeForNow(mainComment.getCreated_at()));
        mainCommentText.setText(Html.fromHtml(mainComment.getContent()));
        if (mainComment.getImages().size()!=0){
            for (int i = 0; i < mainComment.getImages().size(); i++) {
                ImageView commentImage = new ImageView(CardChildCommentActivity.this);
                int height = GlideUtils.getImageHeightDp(CardChildCommentActivity.this,
                        Integer.parseInt(mainComment.getImages().get(i).getHeight()),
                        Integer.parseInt(mainComment.getImages().get(i).getWidth()),
                        87,1);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

                params.setMargins(DensityUtils.dp2px(CardChildCommentActivity.this,0),
                        DensityUtils.dp2px(CardChildCommentActivity.this,4),
                        DensityUtils.dp2px(CardChildCommentActivity.this,0),
                        DensityUtils.dp2px(CardChildCommentActivity.this,4));
                commentImage.setLayoutParams(params);
                commentImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideUtils.loadImageView(CardChildCommentActivity.this,
                        GlideUtils.setImageUrl(CardChildCommentActivity.this,
                                mainComment.getImages().get(i).getUrl(),GlideUtils.HALF_SCREEN)
                        ,commentImage);
                commentImageLayout.addView(commentImage);
            }
        }
        likeCount.setText(mainComment.getLike_count()+"");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (mainComment.isIs_owner()){
            titleCardMaster.setVisibility(View.VISIBLE);
        }else {
            titleCardMaster.setVisibility(View.GONE);
        }
        if (mainComment.isIs_master()){
            titleExecuter.setVisibility(View.VISIBLE);
        }else {
            titleExecuter.setVisibility(View.GONE);
        }
        if (mainComment.isIs_leader()){
            titleBangumiMaster.setVisibility(View.VISIBLE);
        }else {
            titleBangumiMaster.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy() {
        if (childCommentInfoCall!=null){
            childCommentInfoCall.cancel();
        }
        super.onDestroy();
    }

    private void setNet(int NET_STATUS) {
        if (NET_STATUS == NET_STATUS_GET_LIST){
            setMaxId();
            apiService.getCallChildComment(type,commentId,maxId)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<TrendingChildCommentInfo>(){

                        @Override
                        public void onSuccess(TrendingChildCommentInfo info) {
                            childCommentInfoData = info;
                            childList = info.getList();
                            if (isFirstLoad){
                                isFirstLoad=false;
                                baseChildList = info.getList();
                                setAdapter();
                            }
                            if (isRefresh){
                                isRefresh = false;
                                setRefresh();
                            }
                            if (isLoadMore&&childCommentListAdapter!=null){
                                isLoadMore = false;
                                setLoadMore();
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (refreshLayout!=null){
                                if (isLoadMore&&childCommentListAdapter!=null){
                                    isLoadMore = false;
                                    childCommentListAdapter.loadMoreFail();
                                    setFailedView();
                                }
                                if (isRefresh&&refreshLayout!=null){
                                    isRefresh = false;
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    });
        }
    }

    private void setRefresh(){
        isRefresh = false;
        if (childCommentListAdapter!=null&&refreshLayout!=null){
            childCommentListAdapter.setNewData(childList);
            refreshLayout.setRefreshing(false);
        }
    }

    private void setLoadMore() {
        if (childCommentInfoData.isNoMore()){
            childCommentListAdapter.loadMoreEnd();
        }else {
            childCommentListAdapter.loadMoreComplete();
        }
        childCommentListAdapter.addData(childList);
    }

    private void setMaxId() {
        if (isFirstLoad||isRefresh){
            maxId = 0;
        }
        if (isLoadMore){
            if (childList!=null&&childList.size()!=0){
                maxId = childList.get(childList.size()-1).getId();
            }else {
                maxId = 0;
            }
        }
    }

    private void setAdapter() {

        childCommentListAdapter = new CardChildCommentListAdapter(R.layout.card_child_comment_list_item,baseChildList);
        childCommentListView.setLayoutManager(new LinearLayoutManager(CardChildCommentActivity.this));
        childCommentListAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        childCommentListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        //添加底部footer
        childCommentListAdapter.setEnableLoadMore(true);
        childCommentListAdapter.setLoadMoreView(new MyLoadMoreView());
        childCommentListAdapter.disableLoadMoreIfNotFullPage(childCommentListView);

        childCommentListView.setAdapter(childCommentListAdapter);

        setReplyView();
        setListener();
        setEmptyView();
    }

    private void setReplyView() {
        replyView.setStatus(ReplyAndCommentView.STATUS_SUB_COMMENT);
        replyView.setSubType(ReplyAndCommentView.TYPE_SUB_COMMENT);
        replyView.setApiPost(apiPost);
        replyView.setChildCommentAdapter(childCommentListAdapter);
        replyView.setFromUserName("");
        replyView.setMainCommentid(commentId);
        replyView.setType(type);
        replyView.setTargetUserId(0);
        replyView.setTargetUserMainId(mainComment.getFrom_user_id());
        replyView.setNetAndListener();
    }

    private void setListener() {
        childCommentListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (childCommentInfoData!=null&&childCommentInfoData.isNoMore()){
                    childCommentListAdapter.loadMoreEnd();
                }else {
                    isLoadMore =true;
                    setNet(NET_STATUS_GET_LIST);
                }
            }
        },childCommentListView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet(NET_STATUS_GET_LIST);
            }
        });
    }

    private void setEmptyView(){
        if (baseChildList==null||baseChildList.size()==0){
            emptyView = new AppListEmptyView(CardChildCommentActivity.this);
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            childCommentListAdapter.setEmptyView(emptyView);
        }
    }

    private void setFailedView(){
        //加载失败 点击重试
        failedView = new AppListFailedView(CardChildCommentActivity.this);
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        childCommentListAdapter.setEmptyView(failedView);
    }

    @Override
    protected void handler(Message msg) {

    }

    public class CardChildCommentListAdapter extends BaseQuickAdapter<TrendingChildCommentInfo.TrendingChildCommentInfoList,BaseViewHolder>{



        public CardChildCommentListAdapter(int layoutResId, @Nullable List<TrendingChildCommentInfo.TrendingChildCommentInfoList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final TrendingChildCommentInfo.TrendingChildCommentInfoList item) {

            TextView commentChildText = helper.getView(R.id.card_child_comment_list_item_content);

            commentChildText.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableString fromUserName = new SpannableString(item.getFrom_user_name().replace("\n",""));
            fromUserName.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    UserMainUtils.toUserMainActivity(CardChildCommentActivity.this,item.getFrom_user_id(),
                            item.getFrom_user_zone());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.theme_magic_sakura_primary));
                    ds.setUnderlineText(false);
                }
            }, 0, fromUserName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            commentChildText.setText(fromUserName);

            if (item.getTo_user_id() == 0){

            }else {
                commentChildText.append(" 回复 ");
                SpannableString toUserName = new SpannableString(item.getTo_user_name().replace("\n",""));
                toUserName.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        UserMainUtils.toUserMainActivity(CardChildCommentActivity.this,item.getTo_user_id(),
                                item.getTo_user_zone());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.theme_magic_sakura_primary));
                        ds.setUnderlineText(false);
                    }
                },0,toUserName.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentChildText.append(toUserName);
            }
            commentChildText.append(" : ");

            SpannableString replyContent = new SpannableString(item.getContent());
            replyContent.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {

                    replyView.setTargetUserId(item.getFrom_user_id());
                    replyView.setFromUserName(item.getFrom_user_name().replace("\n",""));
                    replyView.setRequestFocus();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.color_FF7B7B7B));
                    ds.setUnderlineText(false);
                }
            },0,replyContent.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            commentChildText.append(replyContent);
//            commentChildText.setClickable(false);
//            commentChildText.setLongClickable(false);

            final AppHeaderPopupWindows longClickMore = new AppHeaderPopupWindows(CardChildCommentActivity.this);
            if (type.equals("post")){
                longClickMore.setReportModelTag(AppHeaderPopupWindows.POST_REPLY,item.getId());
                longClickMore.setDeleteLayout(AppHeaderPopupWindows.POST_REPLY,item.getId(),item.getFrom_user_id()
                        ,mainComment.getFrom_user_id(),apiPost);
            }else if (type.equals("image")){
                longClickMore.setReportModelTag(AppHeaderPopupWindows.IMAGE_REPLY,item.getId());
                longClickMore.setDeleteLayout(AppHeaderPopupWindows.IMAGE_REPLY,item.getId(),item.getFrom_user_id()
                        ,mainComment.getFrom_user_id(),apiPost);
            }else if (type.equals("score")){
                longClickMore.setReportModelTag(AppHeaderPopupWindows.SCORE_REPLY,item.getId());
                longClickMore.setDeleteLayout(AppHeaderPopupWindows.SCORE_REPLY,item.getId(),item.getFrom_user_id()
                        ,mainComment.getFrom_user_id(),apiPost);
            }else if (type.equals("video")){
                longClickMore.setReportModelTag(AppHeaderPopupWindows.VIDEO_REPLY,item.getId());
                longClickMore.setDeleteLayout(AppHeaderPopupWindows.VIDEO_REPLY,item.getId(),item.getFrom_user_id()
                        ,mainComment.getFrom_user_id(),apiPost);
            }
            longClickMore.setOnDeleteFinish(new AppHeaderPopupWindows.OnDeleteFinish() {
                @Override
                public void deleteFinish() {
                    remove(helper.getAdapterPosition());
                }
            });


            commentChildText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longClickMore.popup();
                    return true;
                }
            });
        }

    }

    public CardChildCommentListAdapter getCommentAdapter(){
        return childCommentListAdapter;
    }

    public static CardChildCommentActivity getInstance(){
        return instance;
    }


}

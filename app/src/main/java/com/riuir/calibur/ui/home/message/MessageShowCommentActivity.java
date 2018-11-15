package com.riuir.calibur.ui.home.message;

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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.trending.TrendingChildCommentInfo;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentItem;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentMain;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaVideoPlayActivity;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.home.image.ImageShowInfoActivity;
import com.riuir.calibur.ui.home.score.ScoreShowInfoActivity;
import com.riuir.calibur.ui.widget.ReplyAndCommentView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.GlideUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageShowCommentActivity extends BaseActivity {


    @BindView(R.id.message_show_comment_header_icon)
    RoundedImageView mainCommentUserIcon;
    @BindView(R.id.message_show_comment_header_name)
    TextView mainCommentUserName;

    @BindView(R.id.message_show_comment_header_card_master)
    TextView titleCardMaster;
    @BindView(R.id.message_show_comment_header_bangumi_master)
    ImageView titleBangumiMaster;
    @BindView(R.id.message_show_comment_header_executer)
    ImageView titleExecuter;

    @BindView(R.id.message_show_comment_header_info)
    TextView mainCommentInfo;
    @BindView(R.id.message_show_comment_header_comment_upovte_text)
    TextView likeCount;
    @BindView(R.id.message_show_comment_header_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.message_show_comment_header_comment_text)
    TextView mainCommentText;
//    @BindView(R.id.message_show_comment_header_scroll_view)
//    ScrollView commentScrollView;
    @BindView(R.id.message_show_comment_header_comment_image_layout)
    LinearLayout commentImageLayout;
    @BindView(R.id.message_show_comment_header_child_comment_list)
    RecyclerView childCommentListView;
    @BindView(R.id.message_show_comment_main_card)
    TextView mainCardBtn;
    @BindView(R.id.message_show_comment_reply_layout)
    ReplyAndCommentView replyView;
    @BindView(R.id.message_show_comment_back_btn)
    ImageView backBtn;

    int comment_id;
    int reply_id;
    int main_card_id;
    String type;

    Call<TrendingShowInfoCommentItem> callCommentItem;

    List<TrendingChildCommentInfo.TrendingChildCommentInfoList> baseChildList;
    CommentChildAdapter commentChildAdapter;

    TrendingShowInfoCommentItem.TrendingShowInfoCommentItemData commentData;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_message_show_comment;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        comment_id = intent.getIntExtra("comment_id",0);
        reply_id = intent.getIntExtra("reply_id",0);
        main_card_id = intent.getIntExtra("main_card_id",0);
        type = intent.getStringExtra("type");
        setListener();
        setNet();
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mainCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (type.equals("post")){
                    intent.setClass(MessageShowCommentActivity.this,CardShowInfoActivity.class);
                    intent.putExtra("cardID",main_card_id);
                }else if (type.equals("image")){
                    intent.setClass(MessageShowCommentActivity.this,ImageShowInfoActivity.class);
                    intent.putExtra("imageID",main_card_id);
                }else if (type.equals("score")){
                    intent.setClass(MessageShowCommentActivity.this,ScoreShowInfoActivity.class);
                    intent.putExtra("scoreID",main_card_id);
                }else {
                    intent.setClass(MessageShowCommentActivity.this,DramaVideoPlayActivity.class);
                    intent.putExtra("videoId",main_card_id);
                }
                startActivity(intent);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setNet();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (callCommentItem!=null){
            callCommentItem.cancel();
        }
        super.onDestroy();
    }

    private void setNet() {
        callCommentItem = apiGetHasAuth.getCallMainItemComment(comment_id,reply_id,type);
        callCommentItem.enqueue(new Callback<TrendingShowInfoCommentItem>() {
            @Override
            public void onResponse(Call<TrendingShowInfoCommentItem> call, Response<TrendingShowInfoCommentItem> response) {
                if (response!=null&&response.isSuccessful()){
                    commentData = response.body().getData();
                    LogUtils.d("messageShow","commentData = "+commentData.toString());
                    if (refreshLayout!=null){
                        refreshLayout.setRefreshing(false);
                    }
                    setView();
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
                    ToastUtils.showShort(MessageShowCommentActivity.this,info.getMessage());
                    setFailedView();

                }else {
                    ToastUtils.showShort(MessageShowCommentActivity.this,"网络异常，返回为空");
                    setFailedView();
                }
            }

            @Override
            public void onFailure(Call<TrendingShowInfoCommentItem> call, Throwable t) {
                if (call.isCanceled()){
                }else {
                    ToastUtils.showShort(MessageShowCommentActivity.this,"请检查您的网络");
                    CrashReport.postCatchedException(t);
                    setFailedView();
                }
            }
        });
    }

    private void setView() {
        commentImageLayout.removeAllViews();
        GlideUtils.loadImageViewCircle(MessageShowCommentActivity.this,commentData.getFrom_user_avatar(),mainCommentUserIcon);
        mainCommentUserName.setText(commentData.getFrom_user_name());
        mainCommentInfo.setText("第"+commentData.getFloor_count()+"楼·"+ TimeUtils.HowLongTimeForNow(commentData.getCreated_at()));
        mainCommentText.setText(Html.fromHtml(commentData.getContent()));
        if (commentData.getImages().size()!=0){
            for (int i = 0; i < commentData.getImages().size(); i++) {
                ImageView commentImage = new ImageView(MessageShowCommentActivity.this);
                int height = GlideUtils.getImageHeightDp(MessageShowCommentActivity.this,
                        Integer.parseInt(commentData.getImages().get(i).getHeight()),
                        Integer.parseInt(commentData.getImages().get(i).getWidth()),
                        87,1);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);

                params.setMargins(DensityUtils.dp2px(MessageShowCommentActivity.this,0),
                        DensityUtils.dp2px(MessageShowCommentActivity.this,4),
                        DensityUtils.dp2px(MessageShowCommentActivity.this,0),
                        DensityUtils.dp2px(MessageShowCommentActivity.this,4));
                commentImage.setLayoutParams(params);
                commentImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideUtils.loadImageView(MessageShowCommentActivity.this,
                        GlideUtils.setImageUrl(MessageShowCommentActivity.this,
                                commentData.getImages().get(i).getUrl(),GlideUtils.HALF_SCREEN)
                        ,commentImage);
                commentImageLayout.addView(commentImage);
            }
        }
        likeCount.setText(commentData.getLike_count()+"");
        baseChildList = commentData.getComments().getList();

        if (commentData.isIs_owner()){
            titleCardMaster.setVisibility(View.VISIBLE);
        }else {
            titleCardMaster.setVisibility(View.GONE);
        }
        if (commentData.isIs_master()){
            titleExecuter.setVisibility(View.VISIBLE);
        }else {
            titleExecuter.setVisibility(View.GONE);
        }
        if (commentData.isIs_leader()){
            titleBangumiMaster.setVisibility(View.VISIBLE);
        }else {
            titleBangumiMaster.setVisibility(View.GONE);
        }

        setAdapter();
        setReplyView();
    }

    private void setAdapter() {
        commentChildAdapter = new CommentChildAdapter(R.layout.card_child_comment_list_item,baseChildList);
        childCommentListView.setLayoutManager(new LinearLayoutManager(MessageShowCommentActivity.this));
        commentChildAdapter.setHasStableIds(true);
        childCommentListView.setAdapter(commentChildAdapter);
    }

    private void setReplyView() {
        replyView.setStatus(ReplyAndCommentView.STATUS_SUB_COMMENT);
        replyView.setApiPost(apiPost);
        replyView.setChildCommentAdapter(commentChildAdapter);
        replyView.setFromUserName("");
        replyView.setMainCommentid(comment_id);
        replyView.setType(type);
        replyView.setTargetUserId(0);
        replyView.setTargetUserMainId(commentData.getFrom_user_id());
        replyView.setNetAndListener();
    }

    private void setEmptyView(){
        if (baseChildList==null||baseChildList.size()==0){
            emptyView = new AppListEmptyView(MessageShowCommentActivity.this);
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (commentChildAdapter!=null){
                commentChildAdapter.setEmptyView(emptyView);
            }
        }
    }

    private void setFailedView(){
        //加载失败 点击重试
        failedView = new AppListFailedView(MessageShowCommentActivity.this);
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (commentChildAdapter!=null){
            commentChildAdapter.setEmptyView(failedView);
        }
    }

    public class CommentChildAdapter extends BaseQuickAdapter<TrendingChildCommentInfo.TrendingChildCommentInfoList,BaseViewHolder>{

        public CommentChildAdapter(int layoutResId, @Nullable List<TrendingChildCommentInfo.TrendingChildCommentInfoList> data) {
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
                    UserMainUtils.toUserMainActivity(MessageShowCommentActivity.this,item.getFrom_user_id(),
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
                        UserMainUtils.toUserMainActivity(MessageShowCommentActivity.this,item.getTo_user_id(),
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

            final AppHeaderPopupWindows longClickMore = new AppHeaderPopupWindows(MessageShowCommentActivity.this);
            if (type.equals("post")){
                longClickMore.setReportModelTag(AppHeaderPopupWindows.POST_REPLY,item.getId());
                longClickMore.setDeleteLayout(AppHeaderPopupWindows.POST_REPLY,item.getId(),item.getFrom_user_id()
                        ,commentData.getFrom_user_id(),apiPost);
            }else if (type.equals("image")){
                longClickMore.setReportModelTag(AppHeaderPopupWindows.IMAGE_REPLY,item.getId());
                longClickMore.setDeleteLayout(AppHeaderPopupWindows.IMAGE_REPLY,item.getId(),item.getFrom_user_id()
                        ,commentData.getFrom_user_id(),apiPost);
            }else if (type.equals("score")){
                longClickMore.setReportModelTag(AppHeaderPopupWindows.SCORE_REPLY,item.getId());
                longClickMore.setDeleteLayout(AppHeaderPopupWindows.SCORE_REPLY,item.getId(),item.getFrom_user_id()
                        ,commentData.getFrom_user_id(),apiPost);
            }else if (type.equals("video")){
                longClickMore.setReportModelTag(AppHeaderPopupWindows.VIDEO_REPLY,item.getId());
                longClickMore.setDeleteLayout(AppHeaderPopupWindows.VIDEO_REPLY,item.getId(),item.getFrom_user_id()
                        ,commentData.getFrom_user_id(),apiPost);
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

    @Override
    protected void handler(Message msg) {

    }
}

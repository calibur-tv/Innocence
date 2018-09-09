package com.riuir.calibur.ui.home.card;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.Nullable;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.trending.TrendingChildCommentInfo;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentMain;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.widget.ReplyAndCommentView;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardChildCommentActivity extends BaseActivity {

    @BindView(R.id.card_child_comment_header_icon)
    ImageView mainCommentUserIcon;
    @BindView(R.id.card_child_comment_header_name)
    TextView mainCommentUserName;
    @BindView(R.id.card_child_comment_header_info)
    TextView mainCommentInfo;
    @BindView(R.id.card_child_comment_header_more)
    TextView mainCommentMore;
    @BindView(R.id.card_child_comment_header_comment_text)
    TextView mainCommentText;
    @BindView(R.id.card_child_comment_header_scroll_view)
    ScrollView commentScrollView;
    @BindView(R.id.card_child_comment_header_comment_image_layout)
    LinearLayout commentImageLayout;
    @BindView(R.id.card_child_comment_header_child_comment_list)
    RecyclerView childCommentListView;
//    @BindView(R.id.card_reply_comment_edit)
//    EditText replyCommentEdit;
//    @BindView(R.id.card_reply_comment_button)
//    Button replyCommentBtn;
    @BindView(R.id.card_child_comment_reply_layout)
    ReplyAndCommentView replyView;
    @BindView(R.id.card_child_comment_back_btn)
    ImageView backBtn;

    CardChildCommentListAdapter childCommentListAdapter;

    TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList mainComment;

    TrendingChildCommentInfo.TrendingChildCommentInfoData childCommentInfoData;
    List<TrendingChildCommentInfo.TrendingChildCommentInfoList> baseChildList;
    List<TrendingChildCommentInfo.TrendingChildCommentInfoList> childList;

    String commentReplyToUserName = "";
    int commentReplyToUserId = 0;

    String commentReplyString;

    int commentId;
    int maxId;
    String type;
    boolean isFirstLoad =false;
    boolean isLoadMore = false;

    private static final int NET_STATUS_GET_LIST = 0;
    private static final int NET_STATUS_REPLY_COMMENT = 1;



    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_child_comment;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        commentId = intent.getIntExtra("id",0);
        type = intent.getStringExtra("type");
        mainComment = (TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList) intent.getSerializableExtra("mainComment");

        setView();
        isFirstLoad = true;
        setNet(NET_STATUS_GET_LIST);
    }

    private void setView() {
        GlideUtils.loadImageViewCircle(CardChildCommentActivity.this,mainComment.getFrom_user_avatar(),mainCommentUserIcon);
        mainCommentUserName.setText(mainComment.getFrom_user_name());
        mainCommentInfo.setText("第"+mainComment.getFloor_count()+"楼·"+ TimeUtils.HowLongTimeForNow(mainComment.getCreated_at()));
        mainCommentText.setText(Html.fromHtml(mainComment.getContent()));
        if (mainComment.getImages().size()!=0){
            for (int i = 0; i < mainComment.getImages().size(); i++) {
                ImageView commentImage = new ImageView(CardChildCommentActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(DensityUtils.dp2px(CardChildCommentActivity.this,0),
                        DensityUtils.dp2px(CardChildCommentActivity.this,4),
                        DensityUtils.dp2px(CardChildCommentActivity.this,0),
                        DensityUtils.dp2px(CardChildCommentActivity.this,4));
                commentImage.setLayoutParams(params);
                commentImage.setScaleType(ImageView.ScaleType.FIT_START);
                GlideUtils.loadImageView(CardChildCommentActivity.this,mainComment.getImages().get(i).getUrl(),commentImage);
                commentImageLayout.addView(commentImage);
            }
        }

    }

    private void setNet(int NET_STATUS) {
        if (NET_STATUS == NET_STATUS_GET_LIST){
            setMaxId();
            apiGet.getCallChildComment(type,commentId,maxId).enqueue(new Callback<TrendingChildCommentInfo>() {
                @Override
                public void onResponse(Call<TrendingChildCommentInfo> call, Response<TrendingChildCommentInfo> response) {
                    LogUtils.d("childComment","isSuccessful = "+response.isSuccessful());
                    if (response.isSuccessful()){
                        childCommentInfoData = response.body().getData();
                        childList = response.body().getData().getList();
                        if (isFirstLoad){
                            isFirstLoad=false;
                            baseChildList = response.body().getData().getList();
                            setAdapter();
                        }
                        if (isLoadMore){
                            isLoadMore = false;
                            setLoadMore();
                        }
                    }else {
                        if (childCommentListAdapter!=null){
                            childCommentListAdapter.loadMoreFail();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TrendingChildCommentInfo> call, Throwable t) {
                    LogUtils.d("childComment","Throwable = "+t);
                    if (isLoadMore){
                        isLoadMore = false;
                        childCommentListAdapter.loadMoreFail();
                    }
                }
            });
        }

        //TODO
        if (NET_STATUS == NET_STATUS_REPLY_COMMENT){
            apiPost.geCallReplyChildComment(commentId,commentReplyToUserId,commentReplyString).enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {

                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {

                }
            });
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
        if (isFirstLoad){
            maxId = 0;
        }
        if (isLoadMore){

        }
    }

    private void setAdapter() {

        LogUtils.d("childComment","1111111");
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

        if (childCommentInfoData.isNoMore()){
            childCommentListAdapter.setEnableLoadMore(false);
        }else {
            //添加底部footer
            childCommentListAdapter.setEnableLoadMore(true);
            childCommentListAdapter.setLoadMoreView(new MyLoadMoreView());
            childCommentListAdapter.disableLoadMoreIfNotFullPage(childCommentListView);
        }

        childCommentListView.setAdapter(childCommentListAdapter);

        setReplyView();
        setListener();
    }

    private void setReplyView() {
        replyView.setStatus(ReplyAndCommentView.STATUS_SUB_COMMENT);
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
                if (childCommentInfoData.isNoMore()){

                    childCommentListAdapter.loadMoreEnd();

                }else {
                    isLoadMore =true;
                    setNet(NET_STATUS_GET_LIST);
                }
            }
        },childCommentListView);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void handler(Message msg) {

    }

    public class CardChildCommentListAdapter extends BaseQuickAdapter<TrendingChildCommentInfo.TrendingChildCommentInfoList,BaseViewHolder>{


        public CardChildCommentListAdapter(int layoutResId, @Nullable List<TrendingChildCommentInfo.TrendingChildCommentInfoList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final TrendingChildCommentInfo.TrendingChildCommentInfoList item) {

            TextView commentChildText = helper.getView(R.id.card_child_comment_list_item_content);

            commentChildText.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableString fromUserName = new SpannableString(item.getFrom_user_name());
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
                SpannableString toUserName = new SpannableString(item.getTo_user_name());
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
//                    commentReplyToUserId = item.getFrom_user_id();
//                    commentReplyToUserName = item.getFrom_user_name();
//                    replyCommentEdit.setFocusable(true);
//                    replyCommentEdit.setFocusableInTouchMode(true);
//                    replyCommentEdit.requestFocus();
//                    replyCommentEdit.setText("回复 "+commentReplyToUserName+" :");
//                    replyCommentEdit.setSelection(replyCommentEdit.getText().toString().length());
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    replyView.setTargetUserId(item.getFrom_user_id());
                    replyView.setFromUserName(item.getFrom_user_name());
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
            commentChildText.setLongClickable(false);

        }
    }

}

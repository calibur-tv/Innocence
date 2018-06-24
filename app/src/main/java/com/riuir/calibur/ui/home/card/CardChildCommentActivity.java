package com.riuir.calibur.ui.home.card;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.card.CardChildCommentInfo;
import com.riuir.calibur.data.card.CardShowInfoCommentMain;
import com.riuir.calibur.ui.common.BaseActivity;
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
    @BindView(R.id.card_reply_comment_edit)
    EditText replyCommentEdit;
    @BindView(R.id.card_reply_comment_button)
    Button replyCommentBtn;

    CardChildCommentListAdapter childCommentListAdapter;

    int commentId;
    CardShowInfoCommentMain.CardShowInfoCommentMainList mainComment;

    CardChildCommentInfo.CardChildCommentInfoData childCommentInfoData;
    List<CardChildCommentInfo.CardChildCommentInfoList> baseChildList;
    List<CardChildCommentInfo.CardChildCommentInfoList> childList;

    String commentReplyToUserName;
    int commentReplyToUserId;

    int maxId;
    boolean isFristLoad =false;
    boolean isLoadMore = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_child_comment;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        commentId = intent.getIntExtra("commentId",0);
        mainComment = (CardShowInfoCommentMain.CardShowInfoCommentMainList) intent.getSerializableExtra("mainComment");

        setView();
        isFristLoad = true;
        setNet();
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

    private void setNet() {

        setMaxId();
        apiGet.getCallChildComment("post",commentId,maxId).enqueue(new Callback<CardChildCommentInfo>() {
            @Override
            public void onResponse(Call<CardChildCommentInfo> call, Response<CardChildCommentInfo> response) {
                LogUtils.d("childComment","isSuccessful = "+response.isSuccessful());
                if (response.isSuccessful()){
                    childCommentInfoData = response.body().getData();
                    childList = response.body().getData().getList();
                    if (isFristLoad){
                        isFristLoad=false;
                        baseChildList = response.body().getData().getList();
                        setAdapter();
                    }
                    if (isLoadMore){
                        isLoadMore = false;
                        setLoadMore();
                    }
                }else {
                    childCommentListAdapter.loadMoreFail();
                }
            }

            @Override
            public void onFailure(Call<CardChildCommentInfo> call, Throwable t) {
                LogUtils.d("childComment","Throwable = "+t);
                if (isLoadMore){
                    isLoadMore = false;
                    childCommentListAdapter.loadMoreFail();
                }
            }
        });
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
        if (isFristLoad){
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
            childCommentListAdapter.setLoadMoreView(new CardLoadMoreView());
            childCommentListAdapter.disableLoadMoreIfNotFullPage(childCommentListView);
        }

        childCommentListView.setAdapter(childCommentListAdapter);

        setListener();
    }

    private void setListener() {
        childCommentListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (childCommentInfoData.isNoMore()){

                    childCommentListAdapter.loadMoreEnd();

                }else {
                    isLoadMore =true;
                    setNet();
                }
            }
        },childCommentListView);

    }

    @Override
    protected void handler(Message msg) {

    }

    class CardChildCommentListAdapter extends BaseQuickAdapter<CardChildCommentInfo.CardChildCommentInfoList,BaseViewHolder>{


        public CardChildCommentListAdapter(int layoutResId, @Nullable List<CardChildCommentInfo.CardChildCommentInfoList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final CardChildCommentInfo.CardChildCommentInfoList item) {

            TextView commentChildText = helper.getView(R.id.card_child_comment_list_item_content);

            commentChildText.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableString fromUserName = new SpannableString(item.getFrom_user_name());
            fromUserName.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    ToastUtils.showShort(CardChildCommentActivity.this,"点击了A");
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.theme_magic_sakura_blue));
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
                        ToastUtils.showShort(CardChildCommentActivity.this,"点击了B");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.theme_magic_sakura_blue));
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
                    commentReplyToUserId = item.getFrom_user_id();
                    commentReplyToUserName = item.getFrom_user_name();
                    replyCommentEdit.setFocusable(true);
                    replyCommentEdit.setFocusableInTouchMode(true);
                    replyCommentEdit.requestFocus();
                    replyCommentEdit.setText("回复 "+commentReplyToUserName+" :");
                    replyCommentEdit.setSelection(replyCommentEdit.getText().toString().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

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

    public  class CardLoadMoreView extends LoadMoreView {

        @Override public int getLayoutId() {
            return R.layout.brvah_quick_view_load_more;
        }

        /**
         * 如果返回true，数据全部加载完毕后会隐藏加载更多
         * 如果返回false，数据全部加载完毕后会显示getLoadEndViewId()布局
         */
        @Override public boolean isLoadEndGone() {
            return false;
        }

        @Override protected int getLoadingViewId() {
            return R.id.load_more_loading_view;
        }

        @Override protected int getLoadFailViewId() {
            return R.id.load_more_load_fail_view;
        }

        /**
         * isLoadEndGone()为true，可以返回0
         * isLoadEndGone()为false，不能返回0
         */
        @Override protected int getLoadEndViewId() {
            return R.id.load_more_load_end_view;
        }

    }
}

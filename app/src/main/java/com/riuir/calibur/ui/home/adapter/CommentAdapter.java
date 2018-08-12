package com.riuir.calibur.ui.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentMain;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.ui.loginAndRegister.LoginActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends BaseQuickAdapter<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList,BaseViewHolder> {

    CheckBox commentUpvoteCheckBox;
    ApiPost apiPost;

    private Context context;
    private String type;

    public static final String TYPE_POST = "post";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_SCORE = "score";

    public CommentAdapter(int layoutResId, @Nullable List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> data,Context context,ApiPost apiPost,String type){
        super(layoutResId, data);
        
        this.context = context;
        this.apiPost = apiPost;
        this.type = type;
    }

    public CommentAdapter(int layoutResId, @Nullable List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList item) {

        final int commentId = item.getId();

        helper.setText(R.id.card_show_info_list_comment_item_user_name,item.getFrom_user_name());
        GlideUtils.loadImageViewCircle(context,item.getFrom_user_avatar(),
                (ImageView) helper.getView(R.id.card_show_info_list_comment_item_user_icon));
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_user_icon);
        helper.setText(R.id.card_show_info_list_comment_item_comment_info,"第"+item.getFloor_count()+"楼·"+ TimeUtils.HowLongTimeForNow(item.getCreated_at()));
        helper.setText(R.id.card_show_info_list_comment_item_comment, Html.fromHtml(item.getContent()));
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_card_more);

        ImageView commentMainImageView1 = helper.getView(R.id.card_show_info_list_comment_item_image1);
        ImageView commentMainImageView2 = helper.getView(R.id.card_show_info_list_comment_item_image2);
        ImageView commentMainImageView3 = helper.getView(R.id.card_show_info_list_comment_item_image3);
        ImageView commentMainImageView4 = helper.getView(R.id.card_show_info_list_comment_item_image4);
        ImageView commentMainImageView5 = helper.getView(R.id.card_show_info_list_comment_item_image5);
        ImageView commentMainImageView6 = helper.getView(R.id.card_show_info_list_comment_item_image6);
        ImageView commentMainImageView7 = helper.getView(R.id.card_show_info_list_comment_item_image7);
        ImageView commentMainImageView8 = helper.getView(R.id.card_show_info_list_comment_item_image8);
        ImageView commentMainImageView9 = helper.getView(R.id.card_show_info_list_comment_item_image9);

        helper.addOnClickListener(R.id.card_show_info_list_comment_item_image1);
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_image2);
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_image3);
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_image4);
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_image5);
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_image6);
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_image7);
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_image8);
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_image9);
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_reply);

        commentUpvoteCheckBox= helper.getView(R.id.card_show_info_list_comment_item_upovte);

        if (item.isLiked()){
            commentUpvoteCheckBox.setChecked(true);
        }else {
            commentUpvoteCheckBox.setChecked(false);
        }

        if (commentUpvoteCheckBox.isChecked()){
            commentUpvoteCheckBox.setChecked(true);
        }else {
            commentUpvoteCheckBox.setChecked(false);
        }

        commentUpvoteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if (Constants.ISLOGIN){
                    commentUpvoteCheckBox.setClickable(false);
                    apiPost.getCardCommentToggleLike(commentId).enqueue(new Callback<Event<String>>() {
                        @Override
                        public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                            if (response!=null&&response.body()!=null){
                                if (response.body().getCode() == 0){
                                    ToastUtils.showShort(context,"点赞或取消点赞成功");
                                }else if (response.body().getCode() == 40003){
                                    ToastUtils.showShort(context,response.body().getMessage());
//                                        commentUpvoteCheckBox.toggle();
                                }
                            }else {
                                ToastUtils.showShort(context,"点赞/取消点赞失败了");
//                                    commentUpvoteCheckBox.toggle();
                            }
                            commentUpvoteCheckBox.setClickable(true);
                        }

                        @Override
                        public void onFailure(Call<Event<String>> call, Throwable t) {
                            commentUpvoteCheckBox.setClickable(true);
                            ToastUtils.showShort(context,"点赞/取消点赞失败了");
//                                commentUpvoteCheckBox.toggle();
                        }
                    });
                }else {
                    Intent intent = new Intent(context,LoginActivity.class);
                    context.startActivity(intent);
                }


            }
        });


        TextView commentChild1 = helper.getView(R.id.card_show_info_list_comment_item_child_comment1);
        TextView commentChild2 = helper.getView(R.id.card_show_info_list_comment_item_child_comment2);
        TextView commentChildMore = helper.getView(R.id.card_show_info_list_comment_item_child_comment_more);
        LinearLayout commentChildLayout = helper.getView(R.id.card_show_info_list_comment_item_child_comment_layout);
        //设置TextView部分变蓝并且可点击之前需要调用的方法
        commentChild1.setMovementMethod(LinkMovementMethod.getInstance());
        commentChild2.setMovementMethod(LinkMovementMethod.getInstance());

        if (item.getImages()!=null&&item.getImages().size()!=0){
            if (item.getImages().size() == 1){
                commentMainImageView1.setVisibility(View.VISIBLE);
                commentMainImageView2.setVisibility(View.GONE);
                commentMainImageView3.setVisibility(View.GONE);
                commentMainImageView4.setVisibility(View.GONE);
                commentMainImageView5.setVisibility(View.GONE);
                commentMainImageView6.setVisibility(View.GONE);
                commentMainImageView7.setVisibility(View.GONE);
                commentMainImageView8.setVisibility(View.GONE);
                commentMainImageView9.setVisibility(View.GONE);

                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);

            }else if (item.getImages().size() == 2){
                commentMainImageView1.setVisibility(View.VISIBLE);
                commentMainImageView2.setVisibility(View.VISIBLE);
                commentMainImageView3.setVisibility(View.GONE);
                commentMainImageView4.setVisibility(View.GONE);
                commentMainImageView5.setVisibility(View.GONE);
                commentMainImageView6.setVisibility(View.GONE);
                commentMainImageView7.setVisibility(View.GONE);
                commentMainImageView8.setVisibility(View.GONE);
                commentMainImageView9.setVisibility(View.GONE);


                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
            }else if (item.getImages().size() == 3){
                commentMainImageView1.setVisibility(View.VISIBLE);
                commentMainImageView2.setVisibility(View.VISIBLE);
                commentMainImageView3.setVisibility(View.VISIBLE);
                commentMainImageView4.setVisibility(View.GONE);
                commentMainImageView5.setVisibility(View.GONE);
                commentMainImageView6.setVisibility(View.GONE);
                commentMainImageView7.setVisibility(View.GONE);
                commentMainImageView8.setVisibility(View.GONE);
                commentMainImageView9.setVisibility(View.GONE);

                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
            }else if (item.getImages().size() == 4){
                commentMainImageView1.setVisibility(View.VISIBLE);
                commentMainImageView2.setVisibility(View.VISIBLE);
                commentMainImageView3.setVisibility(View.VISIBLE);
                commentMainImageView4.setVisibility(View.VISIBLE);
                commentMainImageView5.setVisibility(View.GONE);
                commentMainImageView6.setVisibility(View.GONE);
                commentMainImageView7.setVisibility(View.GONE);
                commentMainImageView8.setVisibility(View.GONE);
                commentMainImageView9.setVisibility(View.GONE);

                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
            }else if (item.getImages().size() == 5){
                commentMainImageView1.setVisibility(View.VISIBLE);
                commentMainImageView2.setVisibility(View.VISIBLE);
                commentMainImageView3.setVisibility(View.VISIBLE);
                commentMainImageView4.setVisibility(View.VISIBLE);
                commentMainImageView5.setVisibility(View.VISIBLE);
                commentMainImageView6.setVisibility(View.GONE);
                commentMainImageView7.setVisibility(View.GONE);
                commentMainImageView8.setVisibility(View.GONE);
                commentMainImageView9.setVisibility(View.GONE);

                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
            }else if (item.getImages().size() == 6){
                commentMainImageView1.setVisibility(View.VISIBLE);
                commentMainImageView2.setVisibility(View.VISIBLE);
                commentMainImageView3.setVisibility(View.VISIBLE);
                commentMainImageView4.setVisibility(View.VISIBLE);
                commentMainImageView5.setVisibility(View.VISIBLE);
                commentMainImageView6.setVisibility(View.VISIBLE);
                commentMainImageView7.setVisibility(View.GONE);
                commentMainImageView8.setVisibility(View.GONE);
                commentMainImageView9.setVisibility(View.GONE);

                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
                GlideUtils.loadImageView(context,item.getImages().get(5).getUrl(),commentMainImageView6);
            }else if (item.getImages().size() == 7){
                commentMainImageView1.setVisibility(View.VISIBLE);
                commentMainImageView2.setVisibility(View.VISIBLE);
                commentMainImageView3.setVisibility(View.VISIBLE);
                commentMainImageView4.setVisibility(View.VISIBLE);
                commentMainImageView5.setVisibility(View.VISIBLE);
                commentMainImageView6.setVisibility(View.VISIBLE);
                commentMainImageView7.setVisibility(View.VISIBLE);
                commentMainImageView8.setVisibility(View.GONE);
                commentMainImageView9.setVisibility(View.GONE);

                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
                GlideUtils.loadImageView(context,item.getImages().get(5).getUrl(),commentMainImageView6);
                GlideUtils.loadImageView(context,item.getImages().get(6).getUrl(),commentMainImageView7);
            }else if (item.getImages().size() == 8){
                commentMainImageView1.setVisibility(View.VISIBLE);
                commentMainImageView2.setVisibility(View.VISIBLE);
                commentMainImageView3.setVisibility(View.VISIBLE);
                commentMainImageView4.setVisibility(View.VISIBLE);
                commentMainImageView5.setVisibility(View.VISIBLE);
                commentMainImageView6.setVisibility(View.VISIBLE);
                commentMainImageView7.setVisibility(View.VISIBLE);
                commentMainImageView8.setVisibility(View.VISIBLE);
                commentMainImageView9.setVisibility(View.GONE);

                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
                GlideUtils.loadImageView(context,item.getImages().get(5).getUrl(),commentMainImageView6);
                GlideUtils.loadImageView(context,item.getImages().get(6).getUrl(),commentMainImageView7);
                GlideUtils.loadImageView(context,item.getImages().get(7).getUrl(),commentMainImageView8);
            }else if (item.getImages().size() >= 9){
                commentMainImageView1.setVisibility(View.VISIBLE);
                commentMainImageView2.setVisibility(View.VISIBLE);
                commentMainImageView3.setVisibility(View.VISIBLE);
                commentMainImageView4.setVisibility(View.VISIBLE);
                commentMainImageView5.setVisibility(View.VISIBLE);
                commentMainImageView6.setVisibility(View.VISIBLE);
                commentMainImageView7.setVisibility(View.VISIBLE);
                commentMainImageView8.setVisibility(View.VISIBLE);
                commentMainImageView9.setVisibility(View.VISIBLE);

                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
                GlideUtils.loadImageView(context,item.getImages().get(5).getUrl(),commentMainImageView6);
                GlideUtils.loadImageView(context,item.getImages().get(6).getUrl(),commentMainImageView7);
                GlideUtils.loadImageView(context,item.getImages().get(7).getUrl(),commentMainImageView8);
                GlideUtils.loadImageView(context,item.getImages().get(8).getUrl(),commentMainImageView9);
            }
        }else {
            commentMainImageView1.setVisibility(View.GONE);
            commentMainImageView2.setVisibility(View.GONE);
            commentMainImageView3.setVisibility(View.GONE);
            commentMainImageView4.setVisibility(View.GONE);
            commentMainImageView5.setVisibility(View.GONE);
            commentMainImageView6.setVisibility(View.GONE);
            commentMainImageView7.setVisibility(View.GONE);
            commentMainImageView8.setVisibility(View.GONE);
            commentMainImageView9.setVisibility(View.GONE);
        }
        if (item.getComments().getList()!=null&&item.getComments().getList().size()!=0){
            if (item.getComments().getList().size()==1){
                commentChildLayout.setVisibility(View.VISIBLE);
                commentChild1.setVisibility(View.VISIBLE);
                commentChild2.setVisibility(View.GONE);
                commentChildMore.setVisibility(View.GONE);
                SpannableString fromUserName1 = new SpannableString(item.getComments().getList().get(0).getFrom_user_name());
                fromUserName1.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShort(context,"点击了A");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(context.getResources().getColor(R.color.theme_magic_sakura_primary));
                        ds.setUnderlineText(false);
                    }
                }, 0, fromUserName1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentChild1.setText(fromUserName1);
                commentChild1.append(" 回复 ");
                SpannableString toUserName1 = new SpannableString(item.getComments().getList().get(0).getTo_user_name());
                toUserName1.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShort(context,"点击了B");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(context.getResources().getColor(R.color.theme_magic_sakura_primary));
                        ds.setUnderlineText(false);
                    }
                },0,toUserName1.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentChild1.append(toUserName1);
                commentChild1.append(" : "+item.getComments().getList().get(0).getContent());

            }else if(item.getComments().getList().size() >= 2){
                commentChildLayout.setVisibility(View.VISIBLE);
                commentChild1.setVisibility(View.VISIBLE);
                commentChild2.setVisibility(View.VISIBLE);
                commentChildMore.setVisibility(View.VISIBLE);

                SpannableString fromUserName1 = new SpannableString(item.getComments().getList().get(0).getFrom_user_name());
                fromUserName1.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShort(context,"点击了A");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(context.getResources().getColor(R.color.theme_magic_sakura_primary));
                        ds.setUnderlineText(false);
                    }
                }, 0, fromUserName1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentChild1.setText(fromUserName1);
                commentChild1.append(" 回复 ");
                SpannableString toUserName1 = new SpannableString(item.getComments().getList().get(0).getTo_user_name());
                toUserName1.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShort(context,"点击了B");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(context.getResources().getColor(R.color.theme_magic_sakura_primary));
                        ds.setUnderlineText(false);
                    }
                },0,toUserName1.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentChild1.append(toUserName1);
                commentChild1.append(" : "+item.getComments().getList().get(0).getContent());

                //回复2
                SpannableString fromUserName2 = new SpannableString(item.getComments().getList().get(1).getFrom_user_name());
                fromUserName2.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShort(context,"点击了A");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(context.getResources().getColor(R.color.theme_magic_sakura_primary));
                        ds.setUnderlineText(false);
                    }
                }, 0, fromUserName2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentChild2.setText(fromUserName2);
                commentChild2.append(" 回复 ");
                SpannableString toUserName2 = new SpannableString(item.getComments().getList().get(1).getTo_user_name());
                toUserName2.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShort(context,"点击了B");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(context.getResources().getColor(R.color.theme_magic_sakura_primary));
                        ds.setUnderlineText(false);
                    }
                },0,toUserName2.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                commentChild2.append(toUserName2);
                commentChild2.append(" : "+item.getComments().getList().get(1).getContent());

                //更多回复
                commentChildMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context,CardChildCommentActivity.class);
                        intent.putExtra("id",commentId);
                        intent.putExtra("mainComment",item);
                        intent.putExtra("type",type);
                        context.startActivity(intent);
                    }
                });

            }else {
                commentChildLayout.setVisibility(View.GONE);
                commentChild1.setVisibility(View.GONE);
                commentChild2.setVisibility(View.GONE);
                commentChildMore.setVisibility(View.GONE);
            }
        }else{
            commentChildLayout.setVisibility(View.GONE);
            commentChild1.setVisibility(View.GONE);
            commentChild2.setVisibility(View.GONE);
            commentChildMore.setVisibility(View.GONE);
        }

    }

}
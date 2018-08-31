package com.riuir.calibur.ui.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ScreenUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentMain;
import com.riuir.calibur.data.trending.TrendingToggleInfo;
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


    ApiPost apiPost;


    private Context context;
    private String type;

    public static final String TYPE_POST = "post";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_SCORE = "score";
    public static final String TYPE_VIDEO = "video";

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
        final LinearLayout commentUpvoteCheckBtn;
        final ImageView commentUpvoteCheckIcon;
        final TextView commentUpvoteCheckText;

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

        commentUpvoteCheckBtn= helper.getView(R.id.card_show_info_list_comment_item_upovte);
        commentUpvoteCheckIcon = helper.getView(R.id.card_show_info_list_comment_item_upovte_icon);
        commentUpvoteCheckText = helper.getView(R.id.card_show_info_list_comment_item_upovte_text);



        if (item.isLiked()){
            commentUpvoteCheckIcon.setImageResource(R.mipmap.ic_zan_active);
        }else {
            commentUpvoteCheckIcon.setImageResource(R.mipmap.ic_zan_normal);
        }

        commentUpvoteCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.ISLOGIN){
                    commentUpvoteCheckText.setText("点赞中");
                    apiPost.getCardCommentToggleLike(commentId,type).enqueue(new Callback<TrendingToggleInfo>() {
                        @Override
                        public void onResponse(Call<TrendingToggleInfo> call, Response<TrendingToggleInfo> response) {
                            if (response!=null&&response.isSuccessful()){
                                if (response.body().isData()){
                                    ToastUtils.showShort(context,"点赞成功");
                                    commentUpvoteCheckIcon.setImageResource(R.mipmap.ic_zan_active);
                                }else {
                                    ToastUtils.showShort(context,"取消赞成功");
                                    commentUpvoteCheckIcon.setImageResource(R.mipmap.ic_zan_normal);
                                }
                            }else {
                                ToastUtils.showShort(context,"点赞/取消点赞失败了");
                            }
                            commentUpvoteCheckText.setText("赞");
                        }

                        @Override
                        public void onFailure(Call<TrendingToggleInfo> call, Throwable t) {
                            ToastUtils.showShort(context,"点赞/取消点赞失败了");
                            commentUpvoteCheckText.setText("赞");
                        }
                    });
                }else {
                    ToastUtils.showShort(context,"未登录不能点赞哦");
                    LoginUtils.ReLogin(context);
                }
            }
        });

        int height1,height2,height3,height4,height5,height6,height7,height8,height9;
        ViewGroup.LayoutParams params1,params2,params3,params4,params5,params6,params7,params8,params9;
        double screenWidth = DensityUtils.px2dp(context, ScreenUtils.getScreenWidth(context));
        int width = (int) (screenWidth-79);

        commentMainImageView1.setVisibility(View.GONE);
        commentMainImageView2.setVisibility(View.GONE);
        commentMainImageView3.setVisibility(View.GONE);
        commentMainImageView4.setVisibility(View.GONE);
        commentMainImageView5.setVisibility(View.GONE);
        commentMainImageView6.setVisibility(View.GONE);
        commentMainImageView7.setVisibility(View.GONE);
        commentMainImageView8.setVisibility(View.GONE);
        commentMainImageView9.setVisibility(View.GONE);

        for (int i = 0; i < item.getImages().size(); i++) {

            switch (i){
                case 0:
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    height1 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params1 = commentMainImageView1.getLayoutParams();
                    params1.height = height1;
                    commentMainImageView1.setLayoutParams(params1);
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView1);
                    break;
                case 1:
                    commentMainImageView2.setVisibility(View.VISIBLE);
                    height2 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params2 = commentMainImageView1.getLayoutParams();
                    params2.height = height2;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView2);
                    break;
                case 2:
                    commentMainImageView3.setVisibility(View.VISIBLE);
                    height3 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params3 = commentMainImageView1.getLayoutParams();
                    params3.height = height3;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView3);
                    break;
                case 3:
                    commentMainImageView4.setVisibility(View.VISIBLE);
                    height4 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params4 = commentMainImageView1.getLayoutParams();
                    params4.height = height4;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView4);
                    break;
                case 4:
                    commentMainImageView5.setVisibility(View.VISIBLE);
                    height5 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params5 = commentMainImageView1.getLayoutParams();
                    params5.height = height5;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView5);
                    break;
                case 5:
                    commentMainImageView6.setVisibility(View.VISIBLE);
                    height6 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params6 = commentMainImageView1.getLayoutParams();
                    params6.height = height6;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView6);
                    break;
                case 6:
                    commentMainImageView7.setVisibility(View.VISIBLE);
                    height7 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params7 = commentMainImageView1.getLayoutParams();
                    params7.height = height7;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView7);
                    break;
                case 7:
                    commentMainImageView8.setVisibility(View.VISIBLE);
                    height8 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params8 = commentMainImageView1.getLayoutParams();
                    params8.height = height8;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView8);
                    break;
                case 8:
                    commentMainImageView9.setVisibility(View.VISIBLE);
                    height9 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params9 = commentMainImageView1.getLayoutParams();
                    params9.height = height9;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView9);
                    break;
                default:
                    break;
            }

        }


//        if (item.getImages()!=null&&item.getImages().size()!=0){
//            if (item.getImages().size() == 1){
//                commentMainImageView1.setVisibility(View.VISIBLE);
//                commentMainImageView2.setVisibility(View.GONE);
//                commentMainImageView3.setVisibility(View.GONE);
//                commentMainImageView4.setVisibility(View.GONE);
//                commentMainImageView5.setVisibility(View.GONE);
//                commentMainImageView6.setVisibility(View.GONE);
//                commentMainImageView7.setVisibility(View.GONE);
//                commentMainImageView8.setVisibility(View.GONE);
//                commentMainImageView9.setVisibility(View.GONE);
//
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView1);
//
//            }else if (item.getImages().size() == 2){
//                commentMainImageView1.setVisibility(View.VISIBLE);
//                commentMainImageView2.setVisibility(View.VISIBLE);
//                commentMainImageView3.setVisibility(View.GONE);
//                commentMainImageView4.setVisibility(View.GONE);
//                commentMainImageView5.setVisibility(View.GONE);
//                commentMainImageView6.setVisibility(View.GONE);
//                commentMainImageView7.setVisibility(View.GONE);
//                commentMainImageView8.setVisibility(View.GONE);
//                commentMainImageView9.setVisibility(View.GONE);
//
//
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView1);
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(1).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView2);
//            }else if (item.getImages().size() == 3){
//                commentMainImageView1.setVisibility(View.VISIBLE);
//                commentMainImageView2.setVisibility(View.VISIBLE);
//                commentMainImageView3.setVisibility(View.VISIBLE);
//                commentMainImageView4.setVisibility(View.GONE);
//                commentMainImageView5.setVisibility(View.GONE);
//                commentMainImageView6.setVisibility(View.GONE);
//                commentMainImageView7.setVisibility(View.GONE);
//                commentMainImageView8.setVisibility(View.GONE);
//                commentMainImageView9.setVisibility(View.GONE);
//
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView1);
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(1).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView2);
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(2).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView3);
//            }else if (item.getImages().size() == 4){
//                commentMainImageView1.setVisibility(View.VISIBLE);
//                commentMainImageView2.setVisibility(View.VISIBLE);
//                commentMainImageView3.setVisibility(View.VISIBLE);
//                commentMainImageView4.setVisibility(View.VISIBLE);
//                commentMainImageView5.setVisibility(View.GONE);
//                commentMainImageView6.setVisibility(View.GONE);
//                commentMainImageView7.setVisibility(View.GONE);
//                commentMainImageView8.setVisibility(View.GONE);
//                commentMainImageView9.setVisibility(View.GONE);
//
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView1);
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(1).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView2);
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(2).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView3);
//                GlideUtils.loadImageView(context,
//                        GlideUtils.setImageUrl(context,item.getImages().get(3).getUrl(),GlideUtils.FULL_SCREEN),
//                        commentMainImageView4);
//            }else if (item.getImages().size() == 5){
//                commentMainImageView1.setVisibility(View.VISIBLE);
//                commentMainImageView2.setVisibility(View.VISIBLE);
//                commentMainImageView3.setVisibility(View.VISIBLE);
//                commentMainImageView4.setVisibility(View.VISIBLE);
//                commentMainImageView5.setVisibility(View.VISIBLE);
//                commentMainImageView6.setVisibility(View.GONE);
//                commentMainImageView7.setVisibility(View.GONE);
//                commentMainImageView8.setVisibility(View.GONE);
//                commentMainImageView9.setVisibility(View.GONE);
//
//                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
//                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
//                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
//                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
//                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
//            }else if (item.getImages().size() == 6){
//                commentMainImageView1.setVisibility(View.VISIBLE);
//                commentMainImageView2.setVisibility(View.VISIBLE);
//                commentMainImageView3.setVisibility(View.VISIBLE);
//                commentMainImageView4.setVisibility(View.VISIBLE);
//                commentMainImageView5.setVisibility(View.VISIBLE);
//                commentMainImageView6.setVisibility(View.VISIBLE);
//                commentMainImageView7.setVisibility(View.GONE);
//                commentMainImageView8.setVisibility(View.GONE);
//                commentMainImageView9.setVisibility(View.GONE);
//
//                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
//                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
//                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
//                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
//                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
//                GlideUtils.loadImageView(context,item.getImages().get(5).getUrl(),commentMainImageView6);
//            }else if (item.getImages().size() == 7){
//                commentMainImageView1.setVisibility(View.VISIBLE);
//                commentMainImageView2.setVisibility(View.VISIBLE);
//                commentMainImageView3.setVisibility(View.VISIBLE);
//                commentMainImageView4.setVisibility(View.VISIBLE);
//                commentMainImageView5.setVisibility(View.VISIBLE);
//                commentMainImageView6.setVisibility(View.VISIBLE);
//                commentMainImageView7.setVisibility(View.VISIBLE);
//                commentMainImageView8.setVisibility(View.GONE);
//                commentMainImageView9.setVisibility(View.GONE);
//
//                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
//                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
//                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
//                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
//                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
//                GlideUtils.loadImageView(context,item.getImages().get(5).getUrl(),commentMainImageView6);
//                GlideUtils.loadImageView(context,item.getImages().get(6).getUrl(),commentMainImageView7);
//            }else if (item.getImages().size() == 8){
//                commentMainImageView1.setVisibility(View.VISIBLE);
//                commentMainImageView2.setVisibility(View.VISIBLE);
//                commentMainImageView3.setVisibility(View.VISIBLE);
//                commentMainImageView4.setVisibility(View.VISIBLE);
//                commentMainImageView5.setVisibility(View.VISIBLE);
//                commentMainImageView6.setVisibility(View.VISIBLE);
//                commentMainImageView7.setVisibility(View.VISIBLE);
//                commentMainImageView8.setVisibility(View.VISIBLE);
//                commentMainImageView9.setVisibility(View.GONE);
//
//                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
//                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
//                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
//                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
//                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
//                GlideUtils.loadImageView(context,item.getImages().get(5).getUrl(),commentMainImageView6);
//                GlideUtils.loadImageView(context,item.getImages().get(6).getUrl(),commentMainImageView7);
//                GlideUtils.loadImageView(context,item.getImages().get(7).getUrl(),commentMainImageView8);
//            }else if (item.getImages().size() >= 9){
//                commentMainImageView1.setVisibility(View.VISIBLE);
//                commentMainImageView2.setVisibility(View.VISIBLE);
//                commentMainImageView3.setVisibility(View.VISIBLE);
//                commentMainImageView4.setVisibility(View.VISIBLE);
//                commentMainImageView5.setVisibility(View.VISIBLE);
//                commentMainImageView6.setVisibility(View.VISIBLE);
//                commentMainImageView7.setVisibility(View.VISIBLE);
//                commentMainImageView8.setVisibility(View.VISIBLE);
//                commentMainImageView9.setVisibility(View.VISIBLE);
//
//                GlideUtils.loadImageView(context,item.getImages().get(0).getUrl(),commentMainImageView1);
//                GlideUtils.loadImageView(context,item.getImages().get(1).getUrl(),commentMainImageView2);
//                GlideUtils.loadImageView(context,item.getImages().get(2).getUrl(),commentMainImageView3);
//                GlideUtils.loadImageView(context,item.getImages().get(3).getUrl(),commentMainImageView4);
//                GlideUtils.loadImageView(context,item.getImages().get(4).getUrl(),commentMainImageView5);
//                GlideUtils.loadImageView(context,item.getImages().get(5).getUrl(),commentMainImageView6);
//                GlideUtils.loadImageView(context,item.getImages().get(6).getUrl(),commentMainImageView7);
//                GlideUtils.loadImageView(context,item.getImages().get(7).getUrl(),commentMainImageView8);
//                GlideUtils.loadImageView(context,item.getImages().get(8).getUrl(),commentMainImageView9);
//            }
//        }else {
//            commentMainImageView1.setVisibility(View.GONE);
//            commentMainImageView2.setVisibility(View.GONE);
//            commentMainImageView3.setVisibility(View.GONE);
//            commentMainImageView4.setVisibility(View.GONE);
//            commentMainImageView5.setVisibility(View.GONE);
//            commentMainImageView6.setVisibility(View.GONE);
//            commentMainImageView7.setVisibility(View.GONE);
//            commentMainImageView8.setVisibility(View.GONE);
//            commentMainImageView9.setVisibility(View.GONE);
//        }

        TextView commentChild1 = helper.getView(R.id.card_show_info_list_comment_item_child_comment1);
        TextView commentChild2 = helper.getView(R.id.card_show_info_list_comment_item_child_comment2);
        TextView commentChildMore = helper.getView(R.id.card_show_info_list_comment_item_child_comment_more);
        LinearLayout commentChildLayout = helper.getView(R.id.card_show_info_list_comment_item_child_comment_layout);
        //设置TextView部分变蓝并且可点击之前需要调用的方法
        commentChild1.setMovementMethod(LinkMovementMethod.getInstance());
        commentChild2.setMovementMethod(LinkMovementMethod.getInstance());


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
                        UserMainUtils.toUserMainActivity(context,item.getComments().getList().get(0).getFrom_user_id(),
                                                        item.getComments().getList().get(0).getFrom_user_zone());
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
                        UserMainUtils.toUserMainActivity(context,item.getComments().getList().get(0).getTo_user_id(),
                                item.getComments().getList().get(0).getTo_user_zone());
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
                        UserMainUtils.toUserMainActivity(context,item.getComments().getList().get(0).getFrom_user_id(),
                                item.getComments().getList().get(0).getFrom_user_zone());
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
                        UserMainUtils.toUserMainActivity(context,item.getComments().getList().get(0).getTo_user_id(),
                                item.getComments().getList().get(0).getTo_user_zone());
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
                        UserMainUtils.toUserMainActivity(context,item.getComments().getList().get(1).getFrom_user_id(),
                                item.getComments().getList().get(1).getFrom_user_zone());
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
                        UserMainUtils.toUserMainActivity(context,item.getComments().getList().get(1).getTo_user_id(),
                                item.getComments().getList().get(1).getTo_user_zone());
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
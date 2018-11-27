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

import com.riuir.calibur.data.trending.TrendingToggleInfo;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;

import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.comment.TrendingShowInfoCommentMain;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends BaseQuickAdapter<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList,BaseViewHolder> {


    ApiPost apiPost;


    private Context context;
    private String type;
    private int primacyUserId;

    public static final String TYPE_POST = "post";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_SCORE = "score";
    public static final String TYPE_VIDEO = "video";

    public CommentAdapter(int layoutResId, @Nullable List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> data,
                          Context context,ApiPost apiPost,String type,int primacyUserId){
        super(layoutResId, data);
        
        this.context = context;
        this.apiPost = apiPost;
        this.type = type;
        this.primacyUserId = primacyUserId;
    }

    public CommentAdapter(int layoutResId, @Nullable List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList item) {

        final int commentId = item.getId();
        final LinearLayout commentUpvoteCheckBtn;
        final ImageView commentUpvoteCheckIcon;
        final TextView commentUpvoteCheckText;
        AppHeaderPopupWindows headerMore;

        helper.setText(R.id.card_show_info_list_comment_item_user_name,item.getFrom_user_name().replace("\n",""));
        GlideUtils.loadImageViewCircle(context,item.getFrom_user_avatar(),
                (ImageView) helper.getView(R.id.card_show_info_list_comment_item_user_icon));
        helper.addOnClickListener(R.id.card_show_info_list_comment_item_user_icon);
        int floor = item.getFloor_count();
        if (type.equals("post")){
        }else {
            floor--;
        }
        helper.setText(R.id.card_show_info_list_comment_item_comment_info,"第"+floor+"楼·"+ TimeUtils.HowLongTimeForNow(item.getCreated_at()));
        helper.setText(R.id.card_show_info_list_comment_item_comment, Html.fromHtml(item.getContent()));
        headerMore = helper.getView(R.id.card_show_info_list_comment_item_card_more);

        if (type.equals(TYPE_POST)){
            headerMore.setReportModelTag(AppHeaderPopupWindows.POST_COMMENT,item.getId());
            headerMore.setDeleteLayout(AppHeaderPopupWindows.POST_COMMENT,item.getId(),item.getFrom_user_id()
                    ,primacyUserId,apiPost);
        }else if (type.equals(TYPE_IMAGE)){
            headerMore.setReportModelTag(AppHeaderPopupWindows.IMAGE_COMMENT,item.getId());
            headerMore.setDeleteLayout(AppHeaderPopupWindows.IMAGE_COMMENT,item.getId(),item.getFrom_user_id()
                    ,primacyUserId,apiPost);
        }else if (type.equals(TYPE_SCORE)){
            headerMore.setReportModelTag(AppHeaderPopupWindows.SCORE_COMMENT,item.getId());
            headerMore.setDeleteLayout(AppHeaderPopupWindows.SCORE_COMMENT,item.getId(),item.getFrom_user_id()
                    ,primacyUserId,apiPost);
        }else if (type.equals(TYPE_VIDEO)){
            headerMore.setReportModelTag(AppHeaderPopupWindows.VIDEO_COMMENT,item.getId());
            headerMore.setDeleteLayout(AppHeaderPopupWindows.VIDEO_COMMENT,item.getId(),item.getFrom_user_id()
                    ,primacyUserId,apiPost);
        }
        headerMore.setOnDeleteFinish(new AppHeaderPopupWindows.OnDeleteFinish() {
            @Override
            public void deleteFinish() {
                if (type.equals(TYPE_IMAGE)){
                    remove(helper.getAdapterPosition());
                }else {
                    remove(helper.getAdapterPosition()-1);
                }
            }
        });

        TextView cardMaster = helper.getView(R.id.card_show_info_list_comment_item_card_master);
        ImageView bangumiMaster = helper.getView(R.id.card_show_info_list_comment_item_bangumi_master);
        ImageView executer = helper.getView(R.id.card_show_info_list_comment_item_executer);

        if (item.isIs_owner()){
            //楼主
            cardMaster.setVisibility(View.VISIBLE);
        }else {
            cardMaster.setVisibility(View.GONE);
        }
        if (item.isIs_leader()){
            //版主
            bangumiMaster.setVisibility(View.VISIBLE);
        }else {
            bangumiMaster.setVisibility(View.GONE);
        }
        if (item.isIs_master()){
            //代行者
            executer.setVisibility(View.VISIBLE);
        }else {
            executer.setVisibility(View.GONE);
        }


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

        commentUpvoteCheckText.setText(item.getLike_count()+"");
        commentUpvoteCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.ISLOGIN){
                    commentUpvoteCheckText.setText("点赞中");
                    RetrofitManager.getInstance().getService(APIService.class)
                            .getCardCommentToggleLike(commentId,type)
                            .compose(Rx2Schedulers.applyObservableAsync())
                            .subscribe(new ObserverWrapper<Boolean>(){
                                @Override
                                public void onSuccess(Boolean isLike) {
                                    if (isLike){
                                        ToastUtils.showShort(context,"点赞成功");
                                        commentUpvoteCheckIcon.setImageResource(R.mipmap.ic_zan_active);
                                        item.setLike_count(item.getLike_count()+1);
                                    }else {
                                        ToastUtils.showShort(context,"取消赞成功");
                                        commentUpvoteCheckIcon.setImageResource(R.mipmap.ic_zan_normal);
                                        item.setLike_count(item.getLike_count()-1);
                                    }
                                    commentUpvoteCheckText.setText(item.getLike_count()+"");
                                }

                                @Override
                                public void onFailure(int code, String errorMsg) {
                                    super.onFailure(code, errorMsg);
                                    if (commentUpvoteCheckText!=null){
                                        commentUpvoteCheckText.setText(item.getLike_count()+"");
                                    }
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
                    params2 = commentMainImageView2.getLayoutParams();
                    params2.height = height2;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView2);
                    break;
                case 2:
                    commentMainImageView3.setVisibility(View.VISIBLE);
                    height3 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params3 = commentMainImageView3.getLayoutParams();
                    params3.height = height3;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView3);
                    break;
                case 3:
                    commentMainImageView4.setVisibility(View.VISIBLE);
                    height4 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params4 = commentMainImageView4.getLayoutParams();
                    params4.height = height4;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView4);
                    break;
                case 4:
                    commentMainImageView5.setVisibility(View.VISIBLE);
                    height5 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params5 = commentMainImageView5.getLayoutParams();
                    params5.height = height5;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView5);
                    break;
                case 5:
                    commentMainImageView6.setVisibility(View.VISIBLE);
                    height6 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params6 = commentMainImageView6.getLayoutParams();
                    params6.height = height6;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView6);
                    break;
                case 6:
                    commentMainImageView7.setVisibility(View.VISIBLE);
                    height7 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params7 = commentMainImageView7.getLayoutParams();
                    params7.height = height7;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView7);
                    break;
                case 7:
                    commentMainImageView8.setVisibility(View.VISIBLE);
                    height8 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params8 = commentMainImageView8.getLayoutParams();
                    params8.height = height8;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView8);
                    break;
                case 8:
                    commentMainImageView9.setVisibility(View.VISIBLE);
                    height9 = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getImages().get(i).getHeight()),
                            Integer.parseInt(item.getImages().get(i).getWidth()),79,1);
                    params9 = commentMainImageView9.getLayoutParams();
                    params9.height = height9;
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,item.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            commentMainImageView9);
                    break;
                default:
                    break;
            }

        }



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
                SpannableString fromUserName1 = new SpannableString(item.getComments().getList().get(0).getFrom_user_name().replace("\n",""));
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
                SpannableString toUserName1 = new SpannableString(item.getComments().getList().get(0).getTo_user_name().replace("\n",""));
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

                SpannableString fromUserName1 = new SpannableString(item.getComments().getList().get(0).getFrom_user_name().replace("\n",""));
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
                SpannableString toUserName1 = new SpannableString(item.getComments().getList().get(0).getTo_user_name().replace("\n",""));
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
                SpannableString fromUserName2 = new SpannableString(item.getComments().getList().get(1).getFrom_user_name().replace("\n",""));
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
                SpannableString toUserName2 = new SpannableString(item.getComments().getList().get(1).getTo_user_name().replace("\n",""));
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
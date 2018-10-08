package com.riuir.calibur.ui.home.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class CardActiveListAdapter extends BaseQuickAdapter<MainTrendingInfo.MainTrendingInfoList,BaseViewHolder> {

    private Context context;

    public CardActiveListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public CardActiveListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final MainTrendingInfo.MainTrendingInfoList item) {

        helper.setText(R.id.main_card_list_item_user_name, item.getUser().getNickname());
        helper.setText(R.id.main_card_list_item_anime_name, item.getBangumi().getName()+"·"+ TimeUtils.HowLongTimeForNow(item.getCreated_at()));
        helper.setText(R.id.main_card_list_item_card_title, item.getTitle());


        helper.setText(R.id.main_card_list_item_reward_count, ""+item.getReward_count());
        helper.setText(R.id.main_card_list_item_zan_count, ""+item.getLike_count());
        helper.setText(R.id.main_card_list_item_comment_count, ""+item.getComment_count());
        helper.setText(R.id.main_card_list_item_marked_count, ""+item.getMark_count());

        TextView desc = helper.getView(R.id.main_card_list_item_card_desc);
        desc.setText(item.getDesc());


        if (item.isIs_creator()){
            //原创显示投食数
            helper.setVisible(R.id.main_card_list_item_zan_count,false);
            helper.setVisible(R.id.main_card_list_item_reward_count,true);
            helper.setVisible(R.id.main_card_list_item_zan_icon,false);
            helper.setVisible(R.id.main_card_list_item_reward_icon,true);
        }else {
            //非原创显示赞数
            helper.setVisible(R.id.main_card_list_item_zan_count,true);
            helper.setVisible(R.id.main_card_list_item_reward_count,false);
            helper.setVisible(R.id.main_card_list_item_zan_icon,true);
            helper.setVisible(R.id.main_card_list_item_reward_icon,false);
        }

        LogUtils.d("cardList","isliked = "+item.isLiked()+", ismarked = "+item.isMarked());

        if (item.isLiked()){
            helper.setImageResource(R.id.main_card_list_item_zan_icon,R.mipmap.ic_zan_active);
        }else {
            helper.setImageResource(R.id.main_card_list_item_zan_icon,R.mipmap.ic_zan_normal);
        }
        if (item.isMarked()){
            helper.setImageResource(R.id.main_card_list_item_marked_icon,R.mipmap.ic_mark_active);
        }else {
            helper.setImageResource(R.id.main_card_list_item_marked_icon,R.mipmap.ic_mark_normal);
        }
        ImageView userIcon = helper.getView(R.id.main_card_list_item_user_icon);
        GlideUtils.loadImageViewCircle(context,
                GlideUtils.setImageUrlForWidth(context,item.getUser().getAvatar(),userIcon.getLayoutParams().width),
                userIcon);
        helper.addOnClickListener(R.id.main_card_list_item_user_icon);

        ImageView  little1, little2, little3;
        LinearLayout littleGroup;
        RoundedImageView bigOne;
        bigOne = helper.getView(R.id.main_card_list_item_big_image);
        littleGroup = helper.getView(R.id.main_card_list_item_little_image_group);
        little1 = helper.getView(R.id.main_card_list_item_little_image_1);
        little2 = helper.getView(R.id.main_card_list_item_little_image_2);
        little3 = helper.getView(R.id.main_card_list_item_little_image_3);

        //可以通过helper.getLayoutPosition() 获取当前item的position

        if (item.getImages() == null || item.getImages().size() == 0) {
            littleGroup.setVisibility(View.GONE);
            bigOne.setVisibility(View.GONE);
        } else if (item.getImages().size() == 1) {
            littleGroup.setVisibility(View.GONE);
            bigOne.setVisibility(View.VISIBLE);

//            ViewGroup.LayoutParams params = bigOne.getLayoutParams();
//
//            params.height = GlideUtils.getPostListImageHeightDp(context,Integer.parseInt(item.getImages().get(0).getHeight()),
//                    Integer.parseInt(item.getImages().get(0).getWidth()),30,1);
//            bigOne.setLayoutParams(params);

//            GlideUtils.loadImageViewRoundedCorners(context, GlideUtils.setImageCropHeightUrl(context,item.getImages().get(0).getUrl(),
//                    Integer.parseInt(item.getImages().get(0).getWidth()),Integer.parseInt(item.getImages().get(0).getHeight())), bigOne,15);

            GlideUtils.loadImageView(context, GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),GlideUtils.FULL_SCREEN), bigOne);

        } else {
            littleGroup.setVisibility(View.VISIBLE);
            bigOne.setVisibility(View.GONE);
            little1.setVisibility(View.VISIBLE);
            little2.setVisibility(View.VISIBLE);

            GlideUtils.loadImageView(context,
                    GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),
                            Integer.parseInt(item.getImages().get(0).getWidth())
                            ,Integer.parseInt(item.getImages().get(0).getHeight()))
                    , little1);
            GlideUtils.loadImageView(context,
                    GlideUtils.setImageUrl(context,item.getImages().get(1).getUrl(),
                            Integer.parseInt(item.getImages().get(1).getWidth())
                            ,Integer.parseInt(item.getImages().get(1).getHeight()))
                    , little2);
            if (item.getImages().size() == 2) {
                little3.setVisibility(View.INVISIBLE);
            } else {
                little3.setVisibility(View.VISIBLE);
                GlideUtils.loadImageView(context,
                        GlideUtils.setImageUrl(context,item.getImages().get(2).getUrl(),
                                Integer.parseInt(item.getImages().get(2).getWidth())
                                ,Integer.parseInt(item.getImages().get(2).getHeight()))
                        , little3);
            }

        }


    }

}
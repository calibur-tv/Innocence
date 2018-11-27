package com.riuir.calibur.ui.home.Drama.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.TimeUtils;

import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.followList.MainTrendingInfo;

public class DramaCardListAdapter extends BaseQuickAdapter<MainTrendingInfo.MainTrendingInfoList,BaseViewHolder> {

    private Context context;
    private Activity activity;

    public DramaCardListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
        this.activity = (Activity) context;
    }

    public DramaCardListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final MainTrendingInfo.MainTrendingInfoList item) {

        helper.setText(R.id.main_card_list_item_user_name, item.getUser().getNickname());
        helper.setText(R.id.main_card_list_item_anime_name,  TimeUtils.HowLongTimeForNow(item.getUpdated_at()));
        helper.setText(R.id.main_card_list_item_card_title, item.getTitle());
        helper.setText(R.id.main_card_list_item_card_desc, item.getDesc());

        helper.setText(R.id.main_card_list_item_reward_count, ""+item.getReward_count());
        helper.setText(R.id.main_card_list_item_zan_count, ""+item.getLike_count());
        helper.setText(R.id.main_card_list_item_comment_count, ""+item.getComment_count());
        helper.setText(R.id.main_card_list_item_marked_count, ""+item.getMark_count());

        helper.addOnClickListener(R.id.main_card_list_item_user_icon);
        GlideUtils.loadImageViewCircle(context, item.getUser().getAvatar(), (ImageView) helper.getView(R.id.main_card_list_item_user_icon));

        LinearLayout tagBangumiLayout = helper.getView(R.id.main_card_list_item_tag_bangumi_layout);
        tagBangumiLayout.setVisibility(View.GONE);
        LinearLayout tagLayout = helper.getView(R.id.main_card_list_item_tag_layout);

        TextView tag1 = helper.getView(R.id.main_card_list_item_tag_1);
        TextView tag2 = helper.getView(R.id.main_card_list_item_tag_2);
        TextView tag3 = helper.getView(R.id.main_card_list_item_tag_3);
        if (item.getTags()!=null&&item.getTags().size()!=0){
            if (item.getTags().size()==1){
                tag1.setVisibility(View.VISIBLE);
                tag2.setVisibility(View.GONE);
                tag3.setVisibility(View.GONE);
                tag1.setText(item.getTags().get(0).getName());
            }
            if (item.getTags().size()==2){
                tag1.setVisibility(View.VISIBLE);
                tag2.setVisibility(View.VISIBLE);
                tag3.setVisibility(View.GONE);
                tag1.setText(item.getTags().get(0).getName());
                tag2.setText(item.getTags().get(1).getName());
            }
            if (item.getTags().size()==3){
                tag1.setVisibility(View.VISIBLE);
                tag2.setVisibility(View.VISIBLE);
                tag3.setVisibility(View.VISIBLE);
                tag1.setText(item.getTags().get(0).getName());
                tag2.setText(item.getTags().get(1).getName());
                tag3.setText(item.getTags().get(2).getName());
            }
        }else {
            tag1.setVisibility(View.GONE);
            tag2.setVisibility(View.GONE);
            tag3.setVisibility(View.GONE);
            tagLayout.setVisibility(View.GONE);
        }

        TextView isNiceText = helper.getView(R.id.main_card_list_item_card_is_nice);
        if (item.isIs_nice()){
            isNiceText.setVisibility(View.VISIBLE);
        }else {
            isNiceText.setVisibility(View.GONE);
        }
        TextView isCreatorText = helper.getView(R.id.main_card_list_item_card_is_creator);
        if (item.isIs_creator()){
            isCreatorText.setVisibility(View.VISIBLE);
        }else {
            isCreatorText.setVisibility(View.GONE);
        }
        TextView isTopText = helper.getView(R.id.main_card_list_item_card_is_top);
        if (item.getTop_at()!=null&&item.getTop_at().length()!=0){
            isTopText.setVisibility(View.VISIBLE);
        }else {
            isTopText.setVisibility(View.GONE);
        }


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

        ImageView  little1, little2, little3;
        RoundedImageView bigOne;
        LinearLayout littleGroup;
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

            /**
             * 七牛剪裁 动态设置高度模式
             */
//            ViewGroup.LayoutParams params = bigOne.getLayoutParams();
//
//            params.height = GlideUtils.getPostListImageHeightDp(context,Integer.parseInt(item.getImages().get(0).getHeight()),
//                    Integer.parseInt(item.getImages().get(0).getWidth()),30,1);
//
//            bigOne.setLayoutParams(params);
//            GlideUtils.loadImageViewRoundedCorners(context, GlideUtils.setImageCropHeightUrl(context,item.getImages().get(0).getUrl(),
//                    Integer.parseInt(item.getImages().get(0).getWidth()),Integer.parseInt(item.getImages().get(0).getHeight())), bigOne,15);

            GlideUtils.loadImageView(context, GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),GlideUtils.FULL_SCREEN), bigOne);

        } else {
            littleGroup.setVisibility(View.VISIBLE);
            bigOne.setVisibility(View.GONE);
            little1.setVisibility(View.VISIBLE);
            little2.setVisibility(View.VISIBLE);
            GlideUtils.loadImageViewRoundedCorners(context,
                    GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),
                            Integer.parseInt(item.getImages().get(0).getWidth())
                            ,Integer.parseInt(item.getImages().get(0).getHeight()))
                    , little1,10);
            GlideUtils.loadImageViewRoundedCorners(context,
                    GlideUtils.setImageUrl(context,item.getImages().get(1).getUrl(),
                            Integer.parseInt(item.getImages().get(1).getWidth())
                            ,Integer.parseInt(item.getImages().get(1).getHeight()))
                    , little2,10);
            if (item.getImages().size() == 2) {
                little3.setVisibility(View.INVISIBLE);
            } else {
                little3.setVisibility(View.VISIBLE);
                GlideUtils.loadImageViewRoundedCorners(context,
                        GlideUtils.setImageUrl(context,item.getImages().get(2).getUrl(),
                                Integer.parseInt(item.getImages().get(2).getWidth())
                                ,Integer.parseInt(item.getImages().get(2).getHeight()))
                        , little3,10);
            }


        }


    }

}

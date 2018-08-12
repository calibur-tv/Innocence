package com.riuir.calibur.ui.home.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
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
    protected void convert(BaseViewHolder helper, MainTrendingInfo.MainTrendingInfoList item) {

        helper.setText(R.id.main_card_list_item_user_name, item.getUser().getNickname());
        helper.setText(R.id.main_card_list_item_anime_name, item.getBangumi().getName()+"·"+ TimeUtils.HowLongTimeForNow(item.getUpdated_at()));
        helper.setText(R.id.main_card_list_item_card_title, item.getTitle());
        helper.setText(R.id.main_card_list_item_card_desc, item.getDesc());

        helper.setText(R.id.main_card_list_item_reward_count, "赞赏："+item.getReward_count());
        helper.setText(R.id.main_card_list_item_comment_count, "评论："+item.getComment_count());
        helper.setText(R.id.main_card_list_item_marked_count, "收藏："+item.getMark_count());


        GlideUtils.loadImageViewCircle(context, item.getUser().getAvatar(), (ImageView) helper.getView(R.id.main_card_list_item_user_icon));
        helper.addOnClickListener(R.id.main_card_list_item_user_icon);

        ImageView bigOne, little1, little2, little3;
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
            GlideUtils.loadImageView(context, GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),GlideUtils.FULL_SCREEN), bigOne);

        } else {
            littleGroup.setVisibility(View.VISIBLE);
            bigOne.setVisibility(View.GONE);
            little1.setVisibility(View.VISIBLE);
            little2.setVisibility(View.VISIBLE);
            GlideUtils.loadImageView(context, GlideUtils.setImageUrl(context,item.getImages().get(0).getUrl(),GlideUtils.THIRD_SCREEN), little1);
            GlideUtils.loadImageView(context, GlideUtils.setImageUrl(context,item.getImages().get(1).getUrl(),GlideUtils.THIRD_SCREEN), little2);
            if (item.getImages().size() == 2) {
                little3.setVisibility(View.INVISIBLE);
            } else {
                little3.setVisibility(View.VISIBLE);
                GlideUtils.loadImageView(context, GlideUtils.setImageUrl(context,item.getImages().get(2).getUrl(),GlideUtils.THIRD_SCREEN), little3);
            }


        }


    }

}
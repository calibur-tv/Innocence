package com.riuir.calibur.ui.home.user.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.data.user.UserReplyCardInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;


public class ReplyCardListAdapter extends BaseQuickAdapter<UserReplyCardInfo.UserReplayCardInfoList,BaseViewHolder> {

    private Context context;

    public ReplyCardListAdapter(int layoutResId, @Nullable List<UserReplyCardInfo.UserReplayCardInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public ReplyCardListAdapter(int layoutResId, @Nullable List<UserReplyCardInfo.UserReplayCardInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserReplyCardInfo.UserReplayCardInfoList item) {
        helper.setText(R.id.user_reply_list_item_card_name,"回复："+item.getPost().getTitle());
        helper.setText(R.id.user_reply_list_item_card_desc, Html.fromHtml(item.getPost().getContent()));
        helper.setText(R.id.user_reply_list_item_card_content, Html.fromHtml(item.getContent()));

        ImageView bigOne, little1, little2, little3;
        LinearLayout littleGroup;
        bigOne = helper.getView(R.id.user_reply_list_item_card_big_image);
        littleGroup = helper.getView(R.id.user_reply_list_item_card_little_image_group);
        little1 = helper.getView(R.id.user_reply_list_item_card_little_image_1);
        little2 = helper.getView(R.id.user_reply_list_item_card_little_image_2);
        little3 = helper.getView(R.id.user_reply_list_item_card_little_image_3);

        //可以通过helper.getLayoutPosition() 获取当前item的position

        if (item.getPost().getImages() == null || item.getPost().getImages().size() == 0) {
            littleGroup.setVisibility(View.GONE);
            bigOne.setVisibility(View.GONE);
        } else if (item.getPost().getImages().size() == 1) {
            littleGroup.setVisibility(View.GONE);

            ViewGroup.LayoutParams params = bigOne.getLayoutParams();

            params.height = GlideUtils.getImageHeightDp(context,Integer.parseInt(item.getPost().getImages().get(0).getHeight()),
                    Integer.parseInt(item.getPost().getImages().get(0).getWidth()),30,1);
            bigOne.setLayoutParams(params);

            bigOne.setVisibility(View.VISIBLE);
            GlideUtils.loadImageView(context, GlideUtils.setImageUrl(context,item.getPost().getImages().get(0).getUrl(),GlideUtils.FULL_SCREEN), bigOne);


        } else {
            littleGroup.setVisibility(View.VISIBLE);
            bigOne.setVisibility(View.GONE);
            little1.setVisibility(View.VISIBLE);
            little2.setVisibility(View.VISIBLE);
            GlideUtils.loadImageView(context,
                    GlideUtils.setImageUrl(context,item.getPost().getImages().get(0).getUrl(),
                            Integer.parseInt(item.getPost().getImages().get(0).getWidth())
                            ,Integer.parseInt(item.getPost().getImages().get(0).getHeight()))
                    , little1);
            GlideUtils.loadImageView(context,
                    GlideUtils.setImageUrl(context,item.getPost().getImages().get(1).getUrl(),
                            Integer.parseInt(item.getPost().getImages().get(1).getWidth())
                            ,Integer.parseInt(item.getPost().getImages().get(1).getHeight()))
                    , little2);
            if (item.getPost().getImages().size() == 2) {
                little3.setVisibility(View.INVISIBLE);
            } else {
                little3.setVisibility(View.VISIBLE);
                GlideUtils.loadImageView(context,
                        GlideUtils.setImageUrl(context,item.getPost().getImages().get(2).getUrl(),
                                Integer.parseInt(item.getPost().getImages().get(2).getWidth())
                                ,Integer.parseInt(item.getPost().getImages().get(2).getHeight()))
                        , little3);
            }
        }
    }
}

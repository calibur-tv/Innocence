package com.riuir.calibur.ui.home.Drama.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hedgehog.ratingbar.RatingBar;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class DramaScoreListAdapter extends BaseQuickAdapter<MainTrendingInfo.MainTrendingInfoList,BaseViewHolder> {

    private Context context;

    public DramaScoreListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public DramaScoreListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainTrendingInfo.MainTrendingInfoList item) {
        ImageView userIcon = helper.getView(R.id.main_score_list_item_user_icon);
        GlideUtils.loadImageViewCircle(context, GlideUtils.setImageUrlForWidth(context,item.getUser().getAvatar(),
                userIcon.getLayoutParams().width), userIcon);

        RelativeLayout bangUmiLayout = helper.getView(R.id.main_score_list_item_bangumi_layout);
        if (item.getBangumi()==null){
            bangUmiLayout.setVisibility(View.GONE);
        }else {
            ImageView bangumiIcon = helper.getView(R.id.main_score_list_item_bangumi_icon);
            GlideUtils.loadImageView(context,item.getBangumi().getAvatar(),bangumiIcon);
            helper.setText(R.id.main_score_list_item_bangumi_name,item.getBangumi().getName());
        }

        helper.setText(R.id.main_score_list_item_user_name, item.getUser().getNickname());
        helper.setText(R.id.main_score_list_item_time, TimeUtils.HowLongTimeForNow(item.getCreated_at()));
        helper.setText(R.id.main_score_list_item_title, item.getTitle());
        helper.setText(R.id.main_score_list_item_intro, item.getIntro());
        helper.setText(R.id.main_score_list_item_zan_count,""+item.getLike_count());
        helper.setText(R.id.main_score_list_item_reward_count,""+item.getReward_count());
        helper.setText(R.id.main_score_list_item_comment_count,""+item.getComment_count());
        helper.setText(R.id.main_score_list_item_marked_count,""+item.getMark_count());

        if (item.isIs_creator()){
            //原创显示投食数
            helper.setVisible(R.id.main_score_list_item_zan_count,false);
            helper.setVisible(R.id.main_score_list_item_reward_count,true);
            helper.setVisible(R.id.main_score_list_item_zan_icon,false);
            helper.setVisible(R.id.main_score_list_item_reward_icon,true);
        }else {
            //非原创显示赞数
            helper.setVisible(R.id.main_score_list_item_zan_count,true);
            helper.setVisible(R.id.main_score_list_item_reward_count,false);
            helper.setVisible(R.id.main_score_list_item_zan_icon,true);
            helper.setVisible(R.id.main_score_list_item_reward_icon,false);
        }

        if (item.isLiked()){
            helper.setImageResource(R.id.main_score_list_item_zan_icon,R.mipmap.ic_zan_active);
        }else {
            helper.setImageResource(R.id.main_score_list_item_zan_icon,R.mipmap.ic_zan_normal);
        }
        if (item.isMarked()){
            helper.setImageResource(R.id.main_score_list_item_marked_icon,R.mipmap.ic_mark_active);
        }else {
            helper.setImageResource(R.id.main_score_list_item_marked_icon,R.mipmap.ic_mark_normal);
        }


        float starNum = (float) ((double) item.getTotal() / 2.0);
        LogUtils.d("score_fragment", "starNum = " + starNum + ",total = " + item.getTotal());
        RatingBar ratingBar = helper.getView(R.id.main_score_list_item_score_start);
        ratingBar.setStar(starNum);

    }
}
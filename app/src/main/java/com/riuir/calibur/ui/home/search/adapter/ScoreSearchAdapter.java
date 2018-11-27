package com.riuir.calibur.ui.home.search.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hedgehog.ratingbar.RatingBar;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;

import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.anime.SearchAnimeInfo;

public class ScoreSearchAdapter extends BaseQuickAdapter<SearchAnimeInfo.SearchAnimeInfoList,BaseViewHolder> {

    Context context;

    public ScoreSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public ScoreSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchAnimeInfo.SearchAnimeInfoList item) {
        ImageView userIcon = helper.getView(R.id.main_score_list_item_user_icon);
        GlideUtils.loadImageViewCircle(context, GlideUtils.setImageUrl(context,item.getUser().getAvatar(),
                userIcon.getLayoutParams().width),userIcon);
        ImageView bangumiIcon = helper.getView(R.id.main_score_list_item_bangumi_icon);
        GlideUtils.loadImageView(context,item.getBangumi().getAvatar(),bangumiIcon);
        helper.setText(R.id.main_score_list_item_bangumi_name,item.getBangumi().getName());
        helper.setText(R.id.main_score_list_item_user_name,item.getUser().getNickname());
        helper.setText(R.id.main_score_list_item_time, TimeUtils.HowLongTimeForNow(item.getCreated_at()));
        helper.setText(R.id.main_score_list_item_title,item.getTitle());
        helper.setText(R.id.main_score_list_item_intro,item.getIntro());
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
        helper.addOnClickListener(R.id.main_score_list_item_user_icon);

        float starNum = (float) ((double) item.getTotal()/2.0);
        LogUtils.d("score_fragment","starNum = "+starNum+",total = "+item.getTotal());
        RatingBar ratingBar = helper.getView(R.id.main_score_list_item_score_start);
        ratingBar.setStar(starNum);
    }
}

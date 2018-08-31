package com.riuir.calibur.ui.home.Drama.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

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
        GlideUtils.loadImageViewCircle(context, item.getUser().getAvatar(), userIcon);
        helper.setText(R.id.main_score_list_item_anime_name, item.getUser().getNickname());
        helper.setText(R.id.main_score_list_item_user_name_time, TimeUtils.HowLongTimeForNow(item.getCreated_at()));
        helper.setText(R.id.main_score_list_item_title, item.getTitle());
        helper.setText(R.id.main_score_list_item_intro, item.getIntro());
        helper.setText(R.id.main_score_list_item_like_count, "喜欢：" + item.getLike_count());
        helper.setText(R.id.main_score_list_item_comment_count, "评论：" + item.getComment_count());
        helper.setText(R.id.main_score_list_item_collection_count, "收藏：0");

        float starNum = (float) ((double) item.getTotal() / 2.0);
        LogUtils.d("score_fragment", "starNum = " + starNum + ",total = " + item.getTotal());
        RatingBar ratingBar = helper.getView(R.id.main_score_list_item_score_start);
        ratingBar.setStar(starNum);

    }
}
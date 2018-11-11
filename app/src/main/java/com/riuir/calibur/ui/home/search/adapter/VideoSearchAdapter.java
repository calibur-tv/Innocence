package com.riuir.calibur.ui.home.search.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.data.anime.SearchAnimeInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class VideoSearchAdapter extends BaseQuickAdapter<SearchAnimeInfo.SearchAnimeInfoList,BaseViewHolder> {

    private Context context;
    public VideoSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public VideoSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchAnimeInfo.SearchAnimeInfoList item) {
        helper.setText(R.id.drama_video_episodes_list_item_episodes,"");
        helper.setText(R.id.drama_video_episodes_list_item_part_name,item.getName());
        ImageView imageView = helper.getView(R.id.drama_video_episodes_list_item_image);
        GlideUtils.loadImageView(context,item.getPoster(),imageView);
    }
}

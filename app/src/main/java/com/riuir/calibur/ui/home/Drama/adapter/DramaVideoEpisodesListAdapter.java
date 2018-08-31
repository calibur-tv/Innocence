package com.riuir.calibur.ui.home.Drama.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.data.anime.AnimeShowVideosInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class DramaVideoEpisodesListAdapter extends BaseQuickAdapter<AnimeShowVideosInfo.AnimeShowVideosInfoDataEpisodes,BaseViewHolder> {

    Context context;

    public DramaVideoEpisodesListAdapter(int layoutResId, @Nullable List<AnimeShowVideosInfo.AnimeShowVideosInfoDataEpisodes> data,Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public DramaVideoEpisodesListAdapter(int layoutResId, @Nullable List<AnimeShowVideosInfo.AnimeShowVideosInfoDataEpisodes> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeShowVideosInfo.AnimeShowVideosInfoDataEpisodes item) {
        helper.setText(R.id.drama_video_episodes_list_item_episodes,"第"+item.getPart()+"话");
        helper.setText(R.id.drama_video_episodes_list_item_part_name,item.getName());
        ImageView imageView = helper.getView(R.id.drama_video_episodes_list_item_image);
        GlideUtils.loadImageView(context,item.getPoster(),imageView);
    }
}

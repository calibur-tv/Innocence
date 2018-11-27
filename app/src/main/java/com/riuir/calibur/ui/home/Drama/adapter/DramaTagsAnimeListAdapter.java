package com.riuir.calibur.ui.home.Drama.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;

import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.anime.AnimeListForTagsSearch;

public class DramaTagsAnimeListAdapter extends BaseQuickAdapter<AnimeListForTagsSearch.AnimeListForTagsSearchData,BaseViewHolder> {

    Context context;
    public DramaTagsAnimeListAdapter(int layoutResId, @Nullable List<AnimeListForTagsSearch.AnimeListForTagsSearchData> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public DramaTagsAnimeListAdapter(int layoutResId, @Nullable List<AnimeListForTagsSearch.AnimeListForTagsSearchData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeListForTagsSearch.AnimeListForTagsSearchData item) {
        helper.setText(R.id.drama_timeline_list_item_name,item.getName());
        helper.setText(R.id.drama_timeline_list_item_summary,item.getSummary());
        GlideUtils.loadImageViewRoundedCorners(context,item.getAvatar(), (ImageView) helper.getView(R.id.drama_timeline_list_item_image),10);
    }
}
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

public class DramaSearchAdapter extends BaseQuickAdapter<SearchAnimeInfo.SearchAnimeInfoList,BaseViewHolder> {

    Context context;

    public DramaSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data,Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public DramaSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchAnimeInfo.SearchAnimeInfoList item) {
        helper.setText(R.id.drama_timeline_list_item_name,item.getName());
        helper.setText(R.id.drama_timeline_list_item_summary,item.getSummary());
        GlideUtils.loadImageView(context,item.getAvatar(), (ImageView) helper.getView(R.id.drama_timeline_list_item_image));
    }
}

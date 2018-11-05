package com.riuir.calibur.ui.home.Drama.dramaConfig.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.data.AnimeShowInfo;

import java.util.List;

public class AnimeSettingTagAdapter extends BaseQuickAdapter<AnimeShowInfo.AnimeShowInfoTags,BaseViewHolder> {

    Context context;

    public AnimeSettingTagAdapter(int layoutResId, @Nullable List<AnimeShowInfo.AnimeShowInfoTags> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public AnimeSettingTagAdapter(int layoutResId, @Nullable List<AnimeShowInfo.AnimeShowInfoTags> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AnimeShowInfo.AnimeShowInfoTags item) {
        helper.setText(R.id.drama_master_anime_setting_item_text,item.getName());
        helper.addOnClickListener(R.id.drama_master_anime_setting_item_delete_btn);
    }
}

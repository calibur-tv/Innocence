package com.riuir.calibur.ui.home.choose.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;

import com.riuir.calibur.data.user.UserFollowedBangumiInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.anime.BangumiAllList;

public class ChooseBangumiAdapter extends BaseQuickAdapter<BangumiAllList,BaseViewHolder> {

    Context context;

    public ChooseBangumiAdapter(int layoutResId, @Nullable List<BangumiAllList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public ChooseBangumiAdapter(int layoutResId, @Nullable List<BangumiAllList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BangumiAllList item) {
        helper.setText(R.id.choose_all_bangumi_item_name,item.getName());

        GlideUtils.loadImageView(context,item.getAvatar(), (ImageView) helper.getView(R.id.choose_all_bangumi_item_icon));
    }
}
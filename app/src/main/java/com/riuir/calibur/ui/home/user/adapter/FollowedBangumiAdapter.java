package com.riuir.calibur.ui.home.user.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;

import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.user.UserFollowedBangumiInfo;

public class FollowedBangumiAdapter extends BaseQuickAdapter<UserFollowedBangumiInfo,BaseViewHolder> {

    Context context;

    public FollowedBangumiAdapter(int layoutResId, @Nullable List<UserFollowedBangumiInfo> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public FollowedBangumiAdapter(int layoutResId, @Nullable List<UserFollowedBangumiInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserFollowedBangumiInfo item) {
        helper.setText(R.id.drama_timeline_list_item_name,item.getName());
        helper.setText(R.id.drama_timeline_list_item_summary,item.getName());
        GlideUtils.loadImageView(context,item.getAvatar(), (ImageView) helper.getView(R.id.drama_timeline_list_item_image));
    }
}

package com.riuir.calibur.ui.home.role.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;

import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.anime.RoleFansListInfo;

public class RoleFansListAdapter extends BaseQuickAdapter<RoleFansListInfo.RoleFansListInfoList,BaseViewHolder> {

    Context context;

    public RoleFansListAdapter(int layoutResId, @Nullable List<RoleFansListInfo.RoleFansListInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public RoleFansListAdapter(int layoutResId, @Nullable List<RoleFansListInfo.RoleFansListInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoleFansListInfo.RoleFansListInfoList item) {
        ImageView userIcon = helper.getView(R.id.role_fans_list_item_fans_icon);
        GlideUtils.loadImageViewCircle(context,
                GlideUtils.setImageUrlForWidth(context,item.getAvatar(),
                        userIcon.getLayoutParams().width),userIcon);
        helper.setText(R.id.role_fans_list_item_fans_name,item.getNickname());
        helper.setText(R.id.role_fans_list_item_score,"应援次数:"+item.getScore());
    }
}

package com.riuir.calibur.ui.home.role.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.data.role.RoleFansListInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class RoleFansListAdapter extends BaseQuickAdapter<RoleFansListInfo.RoleFansListInfoData,BaseViewHolder> {

    Context context;

    public RoleFansListAdapter(int layoutResId, @Nullable List<RoleFansListInfo.RoleFansListInfoData> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public RoleFansListAdapter(int layoutResId, @Nullable List<RoleFansListInfo.RoleFansListInfoData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoleFansListInfo.RoleFansListInfoData item) {
        ImageView userIcon = helper.getView(R.id.role_fans_list_item_fans_icon);
        GlideUtils.loadImageViewCircle(context,item.getAvatar(),userIcon);
        helper.setText(R.id.role_fans_list_item_fans_name,item.getNickname());
        helper.setText(R.id.role_fans_list_item_score,"应援次数:"+item.getScore());
    }
}

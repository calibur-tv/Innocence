package com.riuir.calibur.ui.home.role.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.data.AnimeListForRole;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class RoleListAdapter extends BaseQuickAdapter<MainTrendingInfo.MainTrendingInfoList,BaseViewHolder> {

    Context context;

    public RoleListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public RoleListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainTrendingInfo.MainTrendingInfoList item) {
        helper.setText(R.id.drama_role_list_item_role_name,item.getName());
        String loverName;
        if (item.getLover() == null){
            loverName ="暂无";
        }else {
            loverName = item.getLover().getNickname();
        }
        if (loverName == null){
            loverName ="暂无";
        }
        helper.setText(R.id.drama_role_list_item_role_knight,"守护者："+loverName);
        helper.setText(R.id.drama_role_list_item_role_intro,item.getIntro());
        helper.setText(R.id.drama_role_list_item_role_anime_name,item.getBangumi().getName());
        helper.setText(R.id.drama_role_list_item_number,""+(helper.getLayoutPosition()+1));
        GlideUtils.loadImageView(context,item.getAvatar(), (ImageView) helper.getView(R.id.drama_role_list_item_image));

    }
}
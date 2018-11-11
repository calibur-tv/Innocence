package com.riuir.calibur.ui.home.Drama.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.data.anime.CartoonListInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class DramaCartoonListAdapter extends BaseQuickAdapter<CartoonListInfo.CartoonListInfoList,BaseViewHolder> {

    Context context;
    Activity activity;
    public DramaCartoonListAdapter(int layoutResId, @Nullable List<CartoonListInfo.CartoonListInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
        activity = (Activity) context;
    }

    public DramaCartoonListAdapter(int layoutResId, @Nullable List<CartoonListInfo.CartoonListInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CartoonListInfo.CartoonListInfoList item) {
        RoundedImageView source = helper.getView(R.id.drama_cartoon_list_item_source);
        TextView title = helper.getView(R.id.drama_cartoon_list_item_title);
        GlideUtils.loadImageView(context,
                GlideUtils.setImageUrl(context,item.getSource().getUrl(),GlideUtils.HALF_SCREEN),
                source);
        int partInt = (int) item.getPart();
        float chengedPart = partInt;

        if (chengedPart == item.getPart()){
            title.setText("第"+partInt+"话："+item.getName());
        }else {
            title.setText("第"+item.getPart()+"话："+item.getName());
        }

    }
}

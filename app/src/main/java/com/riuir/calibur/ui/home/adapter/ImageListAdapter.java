package com.riuir.calibur.ui.home.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ScreenUtils;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class ImageListAdapter extends BaseQuickAdapter<MainTrendingInfo.MainTrendingInfoList,BaseViewHolder> {

    private Context context;

    public ImageListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public ImageListAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainTrendingInfo.MainTrendingInfoList item) {

        ImageView img = (ImageView) helper.getView(R.id.main_image_list_item_image);

        ViewGroup.LayoutParams params = img.getLayoutParams();


        double screenWidth = DensityUtils.px2dp(context, ScreenUtils.getScreenWidth(context));

        double h = item.getSource().getHeight();
        double w = item.getSource().getWidth();
        double m = h/w;

        LogUtils.d("image_1","screenWidth = "+screenWidth+",m = "+m+
                ",heigth = "+h+" , width = "+w);

        double height = (screenWidth-24.0)*m;

        params.height = (int) height;
        img.setLayoutParams(params);

        GlideUtils.loadImageViewRoundedCorners(context,
                GlideUtils.setImageUrl(mContext,item.getSource().getUrl(),GlideUtils.HALF_SCREEN),
                img);
        helper.setText(R.id.main_image_list_item_image_name,item.getName());

    }
}
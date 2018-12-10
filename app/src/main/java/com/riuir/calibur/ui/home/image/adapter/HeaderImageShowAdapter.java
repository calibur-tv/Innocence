package com.riuir.calibur.ui.home.image.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.activityUtils.PerviewImageUtils;

import com.riuir.calibur.ui.home.image.ImageShowInfoActivity;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.followList.image.ImageShowInfoPrimacy;

public class HeaderImageShowAdapter extends BaseQuickAdapter<ImageShowInfoPrimacy.ImageShowInfoPrimacyImages,BaseViewHolder>{

    Context context;
    //image_show_info_header_image_list_adapter
    public HeaderImageShowAdapter(int layoutResId, @Nullable List<ImageShowInfoPrimacy.ImageShowInfoPrimacyImages> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }
    public HeaderImageShowAdapter(int layoutResId, @Nullable List<ImageShowInfoPrimacy.ImageShowInfoPrimacyImages> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final ImageShowInfoPrimacy.ImageShowInfoPrimacyImages item) {
        ImageView imageView = helper.getView(R.id.image_show_info_header_image_list_adapter_image);
        int height = GlideUtils.getImageHeightDp(context,
                item.getHeight(),item.getWidth(),24,1);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.height = height;
        GlideUtils.loadImageView(context,
                GlideUtils.setImageUrl(context,item.getUrl(),GlideUtils.FULL_SCREEN,String.valueOf(item.getHeight())),
                imageView);

        helper.addOnClickListener(R.id.image_show_info_header_image_list_adapter_image);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String url = item.getUrl();
//                PerviewImageUtils.startPerviewImage(context,previewImagesList,url,view);
//            }
//        });
    }
}

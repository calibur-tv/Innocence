package com.riuir.calibur.ui.home.Drama.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.chrisbanes.photoview.PhotoView;
import com.riuir.calibur.R;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.followList.image.ImageShowInfoPrimacy;

public class DramaCartoonShowAdapter extends BaseQuickAdapter<ImageShowInfoPrimacy.ImageShowInfoPrimacyImages,BaseViewHolder> {

    Context context;
    public DramaCartoonShowAdapter(int layoutResId, @Nullable List<ImageShowInfoPrimacy.ImageShowInfoPrimacyImages> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public DramaCartoonShowAdapter(int layoutResId, @Nullable List<ImageShowInfoPrimacy.ImageShowInfoPrimacyImages> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageShowInfoPrimacy.ImageShowInfoPrimacyImages item) {
        PhotoView photoView = helper.getView(R.id.drama_cartoon_show_item_photo_view);
        int height = GlideUtils.getImageHeightDp(context,
                item.getHeight(),item.getWidth(),0,1);
        ViewGroup.LayoutParams params = photoView.getLayoutParams();
        params.height = height;
        GlideUtils.loadImageView(context,
                GlideUtils.setImageUrl(context,item.getUrl(),GlideUtils.FULL_SCREEN),
                photoView);
//        photoView.enable();
        helper.addOnClickListener(R.id.drama_cartoon_show_item_photo_view);
    }
}

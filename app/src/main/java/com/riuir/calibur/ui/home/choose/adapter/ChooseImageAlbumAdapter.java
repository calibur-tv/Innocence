package com.riuir.calibur.ui.home.choose.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;

import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.create.ChooseImageAlbum;

public class ChooseImageAlbumAdapter extends BaseQuickAdapter<ChooseImageAlbum,BaseViewHolder> {

    Context context;

    public ChooseImageAlbumAdapter(int layoutResId, @Nullable List<ChooseImageAlbum> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public ChooseImageAlbumAdapter(int layoutResId, @Nullable List<ChooseImageAlbum> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChooseImageAlbum item) {
        helper.setText(R.id.choose_all_bangumi_item_name,item.getName());

        GlideUtils.loadImageView(context,GlideUtils.setImageUrlForAlbum(context,item.getPoster(),50),
                (ImageView) helper.getView(R.id.choose_all_bangumi_item_icon));
    }
}

package com.riuir.calibur.ui.home.search.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;

import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import calibur.core.http.models.anime.SearchAnimeInfo;

public class UserSearchAdapter extends BaseQuickAdapter<SearchAnimeInfo.SearchAnimeInfoList,BaseViewHolder> {

    Context context;

    public UserSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data,Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public UserSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchAnimeInfo.SearchAnimeInfoList item) {
        helper.setText(R.id.search_user_list_item_name,item.getNickname().replace("\n",""));
        helper.setText(R.id.search_user_list_item_signature,item.getSignature());
        RoundedImageView userIc = helper.getView(R.id.search_user_list_item_icon);
        GlideUtils.loadImageView(context,
                GlideUtils.setImageUrl(context,item.getAvatar(),GlideUtils.THIRD_SCREEN),
                userIc);
    }
}

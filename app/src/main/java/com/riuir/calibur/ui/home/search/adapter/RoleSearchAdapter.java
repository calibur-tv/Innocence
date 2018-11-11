package com.riuir.calibur.ui.home.search.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;
import com.riuir.calibur.data.anime.SearchAnimeInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class RoleSearchAdapter extends BaseQuickAdapter<SearchAnimeInfo.SearchAnimeInfoList,BaseViewHolder> {

    private Context context;

    public RoleSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data,Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public RoleSearchAdapter(int layoutResId, @Nullable List<SearchAnimeInfo.SearchAnimeInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchAnimeInfo.SearchAnimeInfoList item) {
        helper.setText(R.id.user_followed_role_list_item_role_name,item.getName());
        String loverName;
        if (item.getLover() == null){
            loverName ="暂无";
        }else {
            loverName = item.getLover().getNickname();
        }
        if (loverName == null){
            loverName ="暂无";
        }
//        helper.setText(R.id.drama_role_list_item_role_intro,item.getIntro());
        helper.setText(R.id.user_followed_role_list_item_role_anime_name,item.getBangumi().getName());
        helper.setText(R.id.user_followed_role_list_item_number,"团子："+item.getStar_count());
        helper.setText(R.id.user_followed_role_list_item_role_knight,loverName+" 守护");
        RoundedImageView loverIc = helper.getView(R.id.user_followed_role_list_item_role_knight_icon);
        if (item.getLover()!=null){
            GlideUtils.loadImageView(context,item.getLover().getAvatar(),loverIc);
        }else {
            loverIc.setImageDrawable(context.getResources().getDrawable(R.drawable.app_bg_item_white_gray));
        }

        GlideUtils.loadImageViewCircle(context,item.getAvatar(), (ImageView) helper.getView(R.id.user_followed_role_list_item_image));
    }
}

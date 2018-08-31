package com.riuir.calibur.ui.home.role.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
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
        helper.setText(R.id.drama_role_list_item_role_knight,loverName+" 守护");
//        helper.setText(R.id.drama_role_list_item_role_intro,item.getIntro());
        helper.setText(R.id.drama_role_list_item_role_anime_name,item.getBangumi().getName());
        helper.setText(R.id.drama_role_list_item_number,""+(helper.getLayoutPosition()+1));

        ImageView icNum = helper.getView(R.id.drama_role_list_item_number_ic);
        TextView num = helper.getView(R.id.drama_role_list_item_number);
        RoundedImageView imageView = helper.getView(R.id.drama_role_list_item_image);
        int p = helper.getLayoutPosition();
        if (p<=2){
            imageView.setBorderWidth(3f);
            imageView.setOval(true);
            icNum.setVisibility(View.VISIBLE);
            num.setVisibility(View.INVISIBLE);
            if (p == 0){
                imageView.setBorderColor(context.getResources().getColor(R.color.gold));
                icNum.setImageResource(R.mipmap.role_ic_first);
            }else if (p == 1){
                imageView.setBorderColor(context.getResources().getColor(R.color.silver));
                icNum.setImageResource(R.mipmap.role_ic_second);
            }else {
                imageView.setBorderColor(context.getResources().getColor(R.color.copper));
                icNum.setImageResource(R.mipmap.role_ic_thrid);
            }
        }else {
            imageView.setBorderWidth(0f);
            icNum.setVisibility(View.INVISIBLE);
            num.setVisibility(View.VISIBLE);
        }

        if (item.getLover()!=null&&item.getLover().getAvatar()!=null){
            GlideUtils.loadImageViewCircle(context,item.getLover().getAvatar(), (ImageView) helper.getView(R.id.drama_role_list_item_role_knight_icon));
        }
        GlideUtils.loadImageViewCircle(context,item.getAvatar(), (ImageView) helper.getView(R.id.drama_role_list_item_image));

    }
}
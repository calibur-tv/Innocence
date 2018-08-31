package com.riuir.calibur.ui.home.user.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.data.user.UserFollowedRoleInfo;
import com.riuir.calibur.ui.home.user.UserFollowedRoleFragment;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class FollowedRoleAdapter extends BaseQuickAdapter<MainTrendingInfo.MainTrendingInfoList,BaseViewHolder> {

    Context context;

    public FollowedRoleAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public FollowedRoleAdapter(int layoutResId, @Nullable List<MainTrendingInfo.MainTrendingInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainTrendingInfo.MainTrendingInfoList item) {
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
        helper.setText(R.id.user_followed_role_list_item_role_knight,"守护者："+loverName);
//        helper.setText(R.id.drama_role_list_item_role_intro,item.getIntro());
        helper.setText(R.id.user_followed_role_list_item_role_name,item.getBangumi().getName());
        helper.setText(R.id.user_followed_role_list_item_number,"贡献："+item.getHas_star());


        GlideUtils.loadImageViewCircle(context,item.getAvatar(), (ImageView) helper.getView(R.id.user_followed_role_list_item_image));

    }
}

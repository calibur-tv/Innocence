package com.riuir.calibur.ui.home.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.data.user.UserNotificationInfo;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

public class UserMessageAdapter extends BaseQuickAdapter<UserNotificationInfo.UserNotificationInfoList,BaseViewHolder> {

    Context context;

    public UserMessageAdapter(int layoutResId, @Nullable List<UserNotificationInfo.UserNotificationInfoList> data,Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public UserMessageAdapter(int layoutResId, @Nullable List<UserNotificationInfo.UserNotificationInfoList> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserNotificationInfo.UserNotificationInfoList item) {
        helper.setText(R.id.user_message_list_item_msg_text, Html.fromHtml(item.getMessage()));
        ImageView imageView = helper.getView(R.id.user_message_list_item_user_icon);
        GlideUtils.loadImageViewCircle(context,item.getUser().getAvatar(),imageView);
        if (item.getCreated_at()==null){
            helper.setText(R.id.user_message_list_item_user_name,item.getUser().getNickname());
        }else {
            helper.setText(R.id.user_message_list_item_user_name,item.getUser().getNickname()+
                    "·"+TimeUtils.HowLongTimeForNow(item.getCreated_at()));
        }
        helper.addOnClickListener(R.id.user_message_list_item_user_icon);
    }
}

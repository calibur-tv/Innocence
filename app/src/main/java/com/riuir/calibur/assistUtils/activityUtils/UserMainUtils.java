package com.riuir.calibur.assistUtils.activityUtils;

import android.content.Context;
import android.content.Intent;

import com.riuir.calibur.ui.home.user.UserMainActivity;

public class UserMainUtils {
    public static void toUserMainActivity(Context context,int userId,String zone){
        Intent intent = new Intent(context, UserMainActivity.class);
        intent.putExtra("userId",userId);
        intent.putExtra("zone",zone);
        context.startActivity(intent);
    }
}

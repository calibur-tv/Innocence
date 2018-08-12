package com.riuir.calibur.assistUtils.activityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.ui.loginAndRegister.LoginActivity;
import com.riuir.calibur.utils.Constants;

public class LoginUtils {

    public static void ReLogin(Context context){
        Constants.ISLOGIN = false;
        Constants.AUTH_TOKEN = "";
        SharedPreferencesUtils.put(App.instance(),"Authorization","");
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);

    }

    public static void CancelLogin(Context context, Activity activity){
        Constants.ISLOGIN = false;
        Constants.AUTH_TOKEN = "";
        SharedPreferencesUtils.put(App.instance(),"Authorization","");
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
        activity.finish();
    }

}

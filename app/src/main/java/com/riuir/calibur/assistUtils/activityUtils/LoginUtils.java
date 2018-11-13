package com.riuir.calibur.assistUtils.activityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;

import com.riuir.calibur.ui.loginAndRegister.LoginAndRegisterActivity;
import com.riuir.calibur.utils.Constants;

public class LoginUtils {

    public static void ReLogin(Context context){
        Constants.ISLOGIN = false;
        Constants.AUTH_TOKEN = "";
        Constants.userInfoData = null;
        SharedPreferencesUtils.remove(App.instance(),"Authorization");
        SharedPreferencesUtils.remove(App.instance(),"userInfoData");
        Intent intent = new Intent("calibur.activity.loginAndRegister");
        context.startActivity(intent);
    }

    public static void CancelLogin(Context context, Activity activity){
        Constants.ISLOGIN = false;
        Constants.AUTH_TOKEN = "";
        Constants.userInfoData = null;
        SharedPreferencesUtils.remove(App.instance(),"Authorization");
        SharedPreferencesUtils.remove(App.instance(),"userInfoData");
        Intent intent = new Intent("calibur.activity.loginAndRegister");
        activity.startActivity(intent);
        activity.finish();
    }

}

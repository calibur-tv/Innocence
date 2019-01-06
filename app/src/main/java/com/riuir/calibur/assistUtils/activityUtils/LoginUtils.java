package com.riuir.calibur.assistUtils.activityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;

import com.riuir.calibur.ui.loginAndRegister.LoginAndRegisterActivity;
import com.riuir.calibur.ui.splash.SplashActivity;
import com.riuir.calibur.utils.Constants;

import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.user.MineUserInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;

public class LoginUtils {

    public static void ReLogin(Context context){
        Constants.ISLOGIN = false;
        Constants.AUTH_TOKEN = "";
        Constants.userInfoData = null;
        UserSystem.getInstance().clearUserToken();
        SharedPreferencesUtils.remove(App.instance(),"Authorization");
        SharedPreferencesUtils.remove(App.instance(),"userInfoData");
        Intent intent = new Intent("calibur.activity.loginAndRegister");
        context.startActivity(intent);
    }

    public static void CancelLogin(Context context, Activity activity){
        Constants.ISLOGIN = false;
        Constants.AUTH_TOKEN = "";
        UserSystem.getInstance().clearUserToken();
        Constants.userInfoData = null;
        SharedPreferencesUtils.remove(App.instance(),"Authorization");
        SharedPreferencesUtils.remove(App.instance(),"userInfoData");
        Intent intent = new Intent("calibur.activity.loginAndRegister");
        activity.startActivity(intent);
        activity.finish();
    }

    public static void getUserInfo(Context context){
        RetrofitManager.getInstance().getService(APIService.class)
                .getMineUserInfo().compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<MineUserInfo>() {
                    @Override
                    public void onSuccess(MineUserInfo mineUserInfo) {
                        Constants.userInfoData = mineUserInfo;
                        SharedPreferencesUtils.putUserInfoData(App.instance(), Constants.userInfoData);
                        BangumiAllListUtils.setBangumiAllList(context);
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        BangumiAllListUtils.setBangumiAllList(context);
                    }
                });
    }

}

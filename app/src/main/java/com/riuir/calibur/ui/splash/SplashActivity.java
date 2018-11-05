package com.riuir.calibur.ui.splash;

import android.os.Message;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;

import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.BangumiAllListUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MineUserInfo;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;

import com.riuir.calibur.ui.loginAndRegister.LoginAndRegisterActivity;

import com.riuir.calibur.utils.Constants;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    //预设的登录状态 默认false
    boolean isLogin = false;
    String AUTH_TOKEN = "";

    private Call<MineUserInfo> callUserInfo;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onInit() {
        AUTH_TOKEN = Constants.AUTH_TOKEN;
        isLogin = Constants.ISLOGIN;

        LogUtils.d("Bearer","token = "+AUTH_TOKEN);

        if (isLogin){
            setNetToGetUserInfo();
        }else {
            handler.sendEmptyMessageDelayed(1,1500);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callUserInfo!=null){
            callUserInfo.cancel();
        }
    }

    private void setNetToGetUserInfo() {
        callUserInfo = apiPost.getMineUserInfo();
        callUserInfo.enqueue(new Callback<MineUserInfo>() {
            @Override
            public void onResponse(Call<MineUserInfo> call, Response<MineUserInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    Constants.userInfoData = response.body().getData();
                    BangumiAllListUtils.setBangumiAllList(SplashActivity.this,apiGet);
                }else  if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.d("splashError","error = "+errorStr);
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(SplashActivity.this,info.getMessage());

                    startActivity(LoginAndRegisterActivity.class);
                    finish();
                }else {
                    ToastUtils.showShort(SplashActivity.this,"网络异常,请检查您的网络");
                    startActivity(LoginAndRegisterActivity.class);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<MineUserInfo> call, Throwable t) {
                if (call.isCanceled()){
                }else {
                    ToastUtils.showShort(SplashActivity.this,"网络异常,请检查您的网络");
                    LogUtils.v("AppNetErrorMessage","splash t = "+t.getMessage());
                    CrashReport.postCatchedException(t);
                    startActivity(LoginAndRegisterActivity.class);
                    finish();
                }
            }
        });
    }

//    private void setAllBangumiData() {
//        apiGet.getBangumiAllList().enqueue(new Callback<BangumiAllList>() {
//            @Override
//            public void onResponse(Call<BangumiAllList> call, Response<BangumiAllList> response) {
//                if (response!=null&&response.isSuccessful()){
//                    Constants.bangumiAllListData = response.body().getData();
//                }else  if (!response.isSuccessful()){
//                    String errorStr = "";
//                    try {
//                        errorStr = response.errorBody().string();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Gson gson = new Gson();
//                    Event<String> info =gson.fromJson(errorStr,Event.class);
//                    ToastUtils.showShort(SplashActivity.this,info.getMessage());
//
//                }else {
////                    ToastUtils.showShort(SplashActivity.this,"网络异常,请检查您的网络");
//                }
//                startActivity(MainActivity.class);
//                finish();
//            }
//
//            @Override
//            public void onFailure(Call<BangumiAllList> call, Throwable t) {
//                startActivity(MainActivity.class);
//                finish();
//            }
//        });
//    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            case 0:
                startActivity(MainActivity.class);
                finish();
                return;
            case 1:
                startActivity(LoginAndRegisterActivity.class);
                finish();
                return;
        }
    }
}

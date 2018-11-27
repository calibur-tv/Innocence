package com.riuir.calibur.assistUtils.activityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;

import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.ui.splash.SplashActivity;
import com.riuir.calibur.utils.Constants;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;

import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.anime.BangumiAllList;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BangumiAllListUtils {
    public static void setBangumiAllList(final Context context, ApiGet apiGet){
        final Activity activity = (Activity) context;
        RetrofitManager.getInstance().getService(APIService.class)
                .getBangumiAllList()
                .compose(Rx2Schedulers.<Response<ResponseBean<ArrayList<BangumiAllList>>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<ArrayList<BangumiAllList>>() {
                    @Override
                    public void onSuccess(ArrayList<BangumiAllList> bangumiAllLists) {
                        Constants.bangumiAllListData = bangumiAllLists;
                        SharedPreferencesUtils.putBangumiAllListList(App.instance(),"bangumiAllListData",bangumiAllLists);
                        Intent intent = new Intent(context,MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        Intent intent = new Intent(context,MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });

    }
}

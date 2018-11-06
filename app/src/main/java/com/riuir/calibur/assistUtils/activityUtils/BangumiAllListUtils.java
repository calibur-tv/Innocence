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
import com.riuir.calibur.data.anime.BangumiAllList;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.ui.splash.SplashActivity;
import com.riuir.calibur.utils.Constants;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BangumiAllListUtils {
    public static void setBangumiAllList(final Context context, ApiGet apiGet){
        final Activity activity = (Activity) context;
        apiGet.getBangumiAllList().enqueue(new Callback<BangumiAllList>() {
            @Override
            public void onResponse(Call<BangumiAllList> call, Response<BangumiAllList> response) {
                if (response!=null&&response.isSuccessful()){
                    Constants.bangumiAllListData = response.body().getData();
                    SharedPreferencesUtils.putBangumiAllListList(App.instance(),"bangumiAllListData",response.body().getData());
                }else  if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info = null;
                    try {
                        info = gson.fromJson(errorStr,Event.class);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }else {
                }
                Intent intent = new Intent(context,MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void onFailure(Call<BangumiAllList> call, Throwable t) {
                LogUtils.v("AppNetErrorMessage","splash t = "+t.getMessage());
                CrashReport.postCatchedException(t);
                Intent intent = new Intent(context,MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }
}

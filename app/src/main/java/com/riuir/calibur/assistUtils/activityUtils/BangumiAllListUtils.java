package com.riuir.calibur.assistUtils.activityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.anime.BangumiAllList;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.ui.splash.SplashActivity;
import com.riuir.calibur.utils.Constants;

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
                    LogUtils.d("bangumiAllList","re = "+Constants.bangumiAllListData);
                }else  if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(context,info.getMessage());

                }else {
//                    ToastUtils.showShort(SplashActivity.this,"网络异常,请检查您的网络");
                }
                Intent intent = new Intent(context,MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void onFailure(Call<BangumiAllList> call, Throwable t) {
                Intent intent = new Intent(context,MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }
}

package com.riuir.calibur.assistUtils.activityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.anime.BangumiAllList;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.utils.Constants;
import java.util.ArrayList;

public class BangumiAllListUtils {
    public static void setBangumiAllList(final Context context){
        final Activity activity = (Activity) context;
        RetrofitManager.getInstance().getService(APIService.class)
                .getBangumiAllList()
                .compose(Rx2Schedulers.applyObservableAsync())
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

package com.riuir.calibur.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.BindView;
import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.AppVersionCheckData;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.TemplateRenderManager;
import calibur.core.manager.templaterender.EditorTemplateRender;
import calibur.core.manager.templaterender.ImageDetailPageTemplateRender;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.AppUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.VersionUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.RCode;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.widget.MainBottomBar;
import com.riuir.calibur.utils.Constants;
import com.tencent.bugly.crashreport.CrashReport;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 主activity
 */
public class MainActivity extends BaseActivity implements MainBottomBar.OnSingleClickListener {
    @BindView(R.id.framelayout_main)
    FrameLayout framelayoutMain;
    @BindView(R.id.maintab_bottombar)
    MainBottomBar maintabBottombar;


    private MainFragment fragmentMain = MainFragment.newInstance();
    private DramaFragment fragmentDrama = DramaFragment.newInstance();
    private MessageFragment fragmentMessage = MessageFragment.newInstance();
    private MineFragment fragmentMine = MineFragment.newInstance();

    FloatingActionMenu actionMenu;
    ImageView childIcon1;
    ImageView childIcon2;
    ImageView childIcon3;


    private FragmentManager fragmentManager;

    String oldVersion;
    String newVersion;
    boolean forceUpdate;
    String downloadUrl;
    AlertDialog checkVersionDialog;

    //未读消息
    Call<Event<Integer>> messageCountCall;
    int messageCount;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onInit() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.framelayout_main, fragmentMain)
                .add(R.id.framelayout_main, fragmentDrama)
                .add(R.id.framelayout_main, fragmentMessage)
                .add(R.id.framelayout_main, fragmentMine)
                .hide(fragmentMain)
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .commit();
        fragmentManager
                .beginTransaction()
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .show(fragmentMain)
                .commitAllowingStateLoss();

        maintabBottombar.init();
        maintabBottombar.setOnSingleClickListener(this);

        setCheckVersion();
        TemplateRenderManager.getInstance().setTemplateRender(new ImageDetailPageTemplateRender());
        TemplateRenderManager.getInstance().checkAllTemplateForUpdate();

        if (Constants.userInfoData == null){
            Constants.userInfoData = SharedPreferencesUtils.getUserInfoData(App.instance());
        }
        //demo TODO
//        Logger.d("oninit");
//        handler.sendEmptyMessageDelayed(0, 200);
//        EventBusUtil.sendEvent(new Event(RCode.EventCode.A));
//        setFloatingActionBth();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setMessageCount();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (messageCountCall!=null){
            messageCountCall.cancel();
        }
    }

    private void setMessageCount() {
        messageCountCall = apiGetHasAuth.getUserNotificationCount();
        messageCountCall.enqueue(new Callback<Event<Integer>>() {
            @Override
            public void onResponse(Call<Event<Integer>> call, Response<Event<Integer>> response) {
                if (response!=null&&response.isSuccessful()){
                    messageCount = response.body().getData();
                    maintabBottombar.updateMsgRedDot(messageCount);
                    LogUtils.d("readNotification","messageCount = "+messageCount);
                }else if (response!=null&&response.isSuccessful()){
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
                        LogUtils.d("messageCount", "请求不成功 message = "+info.getMessage());
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }else{
                    LogUtils.d("messageCount", "请求返回为空");
                }
            }

            @Override
            public void onFailure(Call<Event<Integer>> call, Throwable t) {
                if (call.isCanceled()){
                }else {
                    LogUtils.d("messageCount", "请求失败");
                    CrashReport.postCatchedException(t);
                }
            }
        });
    }


    private void setCheckVersion() {
        oldVersion = AppUtil.getAppVersionName();
        RetrofitManager.getInstance().getService(APIService.class).getCallAppVersionCheck(1,oldVersion)
            .compose(Rx2Schedulers.applyObservableAsync())
            .subscribe(new ObserverWrapper<AppVersionCheckData>() {
                @Override public void onSuccess(AppVersionCheckData model) {
                    newVersion = model.getLatest_version();
                    forceUpdate = model.isForce_update();
                    downloadUrl = model.getDownload_url();
                    setIsVerUpDate();
                }

                @Override public void onFailure(int code, String errorMsg) {
                    super.onFailure(code, errorMsg);
                }
            });
        //apiGet.getCallAppVersionCheck(1,oldVersion).enqueue(new Callback<AppVersionCheck>() {
        //    @Override
        //    public void onResponse(Call<AppVersionCheck> call, Response<AppVersionCheck> response) {
        //        if (response!=null&&response.isSuccessful()){
        //            if (response.body().getData()!=null){
        //                LogUtils.d("VersionCheck", "请求成功 data = "+response.body().getData().toString());
        //                newVersion = response.body().getData().getLatest_version();
        //                forceUpdate = response.body().getData().isForce_update();
        //                downloadUrl = response.body().getData().getDownload_url();
        //                setIsVerUpDate();
        //
        //            }
        //        }else if (response!=null&&!response.isSuccessful()){
        //            String errorStr = "";
        //            try {
        //                errorStr = response.errorBody().string();
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //            Gson gson = new Gson();
        //            Event<String> info =gson.fromJson(errorStr,Event.class);
        //
        //            LogUtils.d("VersionCheck", "请求不成功 message = "+info.getMessage());
        //        }else {
        //            LogUtils.d("VersionCheck", "请求返回为空");
        //        }
        //    }
        //
        //    @Override
        //    public void onFailure(Call<AppVersionCheck> call, Throwable t) {
        //        LogUtils.d("VersionCheck", "请求失败");
        //        CrashReport.postCatchedException(t);
        //    }
        //});
    }

    private void setIsVerUpDate() {
        int i = VersionUtils.compareVersion(newVersion,oldVersion);
        if (i == 1){
            if (downloadUrl!=null&&downloadUrl.length()!=0){
                setUpVersionDialog();
            }else {
                LogUtils.d("VersionCheck", "网址为空");
            }
        }else {
            LogUtils.d("VersionCheck", "不需要更新 i = "+i);
        }
    }

    private void setUpVersionDialog() {
        String dialogMessage = "";
        if (forceUpdate){
            dialogMessage = "检测到新版本，该版本需要您进行更新";
        }else {
            dialogMessage = "检测到新版本，是否进行更新？";
        }
        if (forceUpdate){
            checkVersionDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("检测到新版本")
                    .setMessage(dialogMessage)
                    .setPositiveButton("确定", null)
                    .create();
            checkVersionDialog.setCancelable(false);
            checkVersionDialog.show();
            checkVersionDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse(downloadUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }else {
            checkVersionDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("检测到新版本")
                    .setMessage(dialogMessage)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkVersionDialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse(downloadUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    })
                    .create();
            checkVersionDialog.setCancelable(false);
            checkVersionDialog.show();
        }
    }

//
//    private void setNetToGetUserInfo() {
//        apiPost.getMineUserInfo().enqueue(new Callback<MineUserInfo>() {
//            @Override
//            public void onResponse(Call<MineUserInfo> call, Response<MineUserInfo> response) {
//                if (response!=null&&response.isSuccessful()){
//                    Constants.userInfoData = response.body().getData();
//                    SharedPreferencesUtils.putUserInfoData(App.instance(),Constants.userInfoData);
//                }else  if (!response.isSuccessful()){
//                    String errorStr = "";
//                    try {
//                        errorStr = response.errorBody().string();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Gson gson = new Gson();
//                    Event<String> info =gson.fromJson(errorStr,Event.class);
//                    ToastUtils.showShort(MainActivity.this,"mainactivity user="+info.getMessage());
//                }else {
//                    ToastUtils.showShort(MainActivity.this,"网络异常,请检查您的网络");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MineUserInfo> call, Throwable t) {
//                ToastUtils.showShort(MainActivity.this,"网络异常,请检查您的网络");
//                CrashReport.postCatchedException(t);
//            }
//        });
//    }


    @Override
    protected boolean isRegisterEventBus() {

        return true;
    }

    @Override
    protected void handler(Message msg) {
        if (msg.what == 0) {
            ToastUtils.showShort(this,"app launch");
        }
    }

    @Override
    protected void receiveEvent(Event event) {
        super.receiveEvent(event);
        if (event.getCode() == RCode.EventCode.A) {
            ToastUtils.showShort(this,"event bus received");
        }
    }



    @Override
    public void onClickOne() {
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
        hideAllFragment();
        fragmentManager
                .beginTransaction()
                .show(fragmentMain)
                .commitAllowingStateLoss();

    }

    @Override
    public void onClickTwo() {
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
        hideAllFragment();
        fragmentManager
                .beginTransaction()
                .show(fragmentDrama)
                .commitAllowingStateLoss();

    }



    @Override
    public void onClickThree() {
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
        hideAllFragment();
        fragmentManager
                .beginTransaction()
                .show(fragmentMessage)
                .commitAllowingStateLoss();

        if (onRefreshMessageList!=null){
            onRefreshMessageList.OnReFresh(messageCount);
        }
    }

    @Override
    public void onClickFour() {
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
        hideAllFragment();
        fragmentManager
                .beginTransaction()
                .show(fragmentMine)
                .commitAllowingStateLoss();

    }


    private void hideAllFragment(){
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .hide(fragmentMain)
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        ExitApp();
    }

    private long exitTime = 0;

    public void ExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showShort(MainActivity.this,"再次点击返回退出程序哟~");
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }

    private OnRefreshMessageList onRefreshMessageList;

    public void setOnRefreshMessageList(OnRefreshMessageList onRefreshMessageList) {
        this.onRefreshMessageList = onRefreshMessageList;
    }

    public interface OnRefreshMessageList{
        void OnReFresh(int count);
    }

    public void setNoReadMsgZero() {
        messageCount = 0;
        maintabBottombar.updateMsgRedDot(0);
    }

}

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
import calibur.core.http.models.AppVersionCheckData;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.templates.renders.BookmarksTemplateRender;
import calibur.core.templates.renders.CommentItemTemplateRender;
import calibur.core.templates.renders.EditorTemplateRender;
import calibur.core.templates.renders.HomeTemplateRender;
import calibur.core.templates.renders.ImageDetailPageTemplateRender;
import calibur.core.templates.renders.NoticeTemplateRender;
import calibur.core.templates.renders.NotificationsTemplateRender;
import calibur.core.templates.renders.PostDetailPageTemplateRender;
import calibur.core.templates.renders.ReviewTemplateRender;
import calibur.core.templates.renders.RoleDetailTemplateRender;
import calibur.core.templates.renders.TransactionsTemplateRender;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.AppUtil;
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

import retrofit2.Call;
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
//    private MessageFragment fragmentMessage = MessageFragment.newInstance();
    private NotificationListFragment fragmentNotificationList = NotificationListFragment.newInstance();
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
//        downloadAndCheckTemplates();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.framelayout_main, fragmentMain)
                .add(R.id.framelayout_main, fragmentDrama)
//                .add(R.id.framelayout_main, fragmentMessage)
                .add(R.id.framelayout_main, fragmentNotificationList)
                .add(R.id.framelayout_main, fragmentMine)
                .hide(fragmentMain)
                .hide(fragmentDrama)
//                .hide(fragmentMessage)
                .hide(fragmentNotificationList)
                .hide(fragmentMine)
                .commit();
        fragmentManager.beginTransaction()
                .hide(fragmentDrama)
//                .hide(fragmentMessage)
                .hide(fragmentNotificationList)
                .hide(fragmentMine)
                .show(fragmentMain)
                .commitAllowingStateLoss();

        maintabBottombar.init();
        maintabBottombar.setOnSingleClickListener(this);
        setCheckVersion();

        if (Constants.userInfoData == null){
            Constants.userInfoData = SharedPreferencesUtils.getUserInfoData(App.instance());
        }
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
        apiService.getUserNotificationCount()
                .compose(Rx2Schedulers.<Response<ResponseBean<Integer>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        messageCount = integer;
                        maintabBottombar.updateMsgRedDot(messageCount);
                        LogUtils.d("readNotification","messageCount = "+messageCount);
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }
                });

    }

    private void setCheckVersion() {
        oldVersion = AppUtil.getAppVersionName();
        apiService.getCallAppVersionCheck(1,oldVersion)
            .compose(Rx2Schedulers.<Response<ResponseBean<AppVersionCheckData>>>applyObservableAsync())
            .subscribe(new ObserverWrapper<AppVersionCheckData>() {
                @Override
                public void onSuccess(AppVersionCheckData model) {
                    newVersion = model.getLatest_version();
                    forceUpdate = model.isForce_update();
                    downloadUrl = model.getDownload_url();
                    setIsVerUpDate();
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    super.onFailure(code, errorMsg);
                }
            });
    }

    private void downloadAndCheckTemplates() {
        TemplateRenderEngine.getInstance().setTemplateRender(new ImageDetailPageTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new BookmarksTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new EditorTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new NoticeTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new ReviewTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new PostDetailPageTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new NotificationsTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new TransactionsTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new HomeTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new RoleDetailTemplateRender());
        TemplateRenderEngine.getInstance().setTemplateRender(new CommentItemTemplateRender());
        TemplateRenderEngine.getInstance().checkAllTemplateForUpdate();
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

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void handler(Message msg) {
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
//                .show(fragmentMessage)
                .show(fragmentNotificationList)
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
//                .hide(fragmentMessage)
                .hide(fragmentNotificationList)
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
    public void setNoReadMsgCount(int count){
        messageCount = count;
        maintabBottombar.updateMsgRedDot(count);
    }

}

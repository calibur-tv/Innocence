package com.riuir.calibur.ui.home;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.VersionUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MineUserInfo;
import com.riuir.calibur.data.RCode;
import com.riuir.calibur.data.check.AppVersionCheck;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.home.image.CreateNewImageActivity;
import com.riuir.calibur.ui.widget.MainBottomBar;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.assistUtils.ToastUtils;

import java.io.IOException;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/12/24
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 * ************************************
 */
public class MainActivity extends BaseActivity implements MainBottomBar.OnSingleClickListener {
    @BindView(R.id.framelayout_main)
    FrameLayout framelayoutMain;
    @BindView(R.id.maintab_bottombar)
    MainBottomBar maintabBottombar;


    private Fragment fragmentMain = MainFragment.newInstance();
    private Fragment fragmentDrama = DramaFragment.newInstance();
    private Fragment fragmentMessage = MessageFragment.newInstance();
    private Fragment fragmentMine = MineFragment.newInstance();

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

        if (Constants.userInfoData == null){
            setNetToGetUserInfo();
        }
        //demo TODO
//        Logger.d("oninit");
//        handler.sendEmptyMessageDelayed(0, 200);
//        EventBusUtil.sendEvent(new Event(RCode.EventCode.A));
//        setFloatingActionBth();

    }



    private void setCheckVersion() {
        oldVersion = VersionUtils.getLocalVersionName();
        apiGet.getCallAppVersionCheck(1,oldVersion).enqueue(new Callback<AppVersionCheck>() {
            @Override
            public void onResponse(Call<AppVersionCheck> call, Response<AppVersionCheck> response) {
                if (response!=null&&response.isSuccessful()){
                    if (response.body().getData()!=null){
                        LogUtils.d("VersionCheck", "请求成功 data = "+response.body().getData().toString());
                        newVersion = response.body().getData().getLatest_version();
                        forceUpdate = response.body().getData().isForce_update();
                        downloadUrl = response.body().getData().getDownload_url();
                        setIsVerUpDate();

                    }
                }else if (response!=null&&!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);

                    LogUtils.d("VersionCheck", "请求不成功 message = "+info.getMessage());
                }else {
                    LogUtils.d("VersionCheck", "请求返回为空");
                }
            }

            @Override
            public void onFailure(Call<AppVersionCheck> call, Throwable t) {
                LogUtils.d("VersionCheck", "请求失败");
            }
        });
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


    private void setNetToGetUserInfo() {
        apiPost.getMineUserInfo().enqueue(new Callback<MineUserInfo>() {
            @Override
            public void onResponse(Call<MineUserInfo> call, Response<MineUserInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    Constants.userInfoData = response.body().getData();
                }else  if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(MainActivity.this,"mainactivity user="+info.getMessage());
                }else {
                    ToastUtils.showShort(MainActivity.this,"网络异常,请检查您的网络");
                }
            }

            @Override
            public void onFailure(Call<MineUserInfo> call, Throwable t) {
                ToastUtils.showShort(MainActivity.this,"网络异常,请检查您的网络");
            }
        });
    }


    //将addBtn 以actionBtn的方式在这里初始化
//    private void setFloatingActionBth() {
//        //宽高的px  由dp转换而成
//        int addBtnParams = DensityUtils.dp2px(this,45);
//        int itemIconParams = DensityUtils.dp2px(this,40);
//
//        //设置CircularFloatingActionMenu点击展开扇形图
//        final ImageView addActionImg = new ImageView(this);
//        addActionImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        //设置image宽高边距
//        FloatingActionButton.LayoutParams addImgParams = new FloatingActionButton.LayoutParams(addBtnParams,
//                addBtnParams);
//        addImgParams.setMargins(0, 0, 0,
//                8);
//        addActionImg.setLayoutParams(addImgParams);
//        addActionImg.setImageDrawable(getResources().getDrawable(R.mipmap.maintab_new_normal));
//
//        //设置FloatingActionButton宽高边距
//        FloatingActionButton.LayoutParams addActionButtonParams = new FloatingActionButton.LayoutParams(
//                addBtnParams, addBtnParams);
//        addActionButtonParams.setMargins(0, 0,
//                0, 0);
//        final FloatingActionButton addActionButton =
//                new FloatingActionButton.Builder(this).setContentView(addActionImg,addActionButtonParams)
//                        .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
//                        .setLayoutParams(addImgParams)
//                        .build();
//
//        // Set up customized SubActionButtons for the right center menu
//        SubActionButton.Builder subBuilder = new SubActionButton.Builder(this);
////        lCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));
//
//        FrameLayout.LayoutParams itemIconLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.MATCH_PARENT);
//        itemIconLayoutParams.setMargins(0, 0,
//                0, 0);
//        subBuilder.setLayoutParams(itemIconLayoutParams);
//        // Set custom layout params
//        FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(itemIconParams,
//                itemIconParams);
//        subBuilder.setLayoutParams(itemParams);
//
//        childIcon1 = new ImageView(this);
//        childIcon2 = new ImageView(this);
//        childIcon3 = new ImageView(this);
//
//        childIcon1.setImageDrawable(getResources().getDrawable(R.mipmap.ic_main_add_post));
//        childIcon2.setImageDrawable(getResources().getDrawable(R.mipmap.ic_main_add_picture));
//        childIcon3.setImageDrawable(getResources().getDrawable(R.mipmap.ic_main_add_score));
//
//        SubActionButton itemSub1 = subBuilder.setContentView(childIcon1, itemIconLayoutParams).build();
//        SubActionButton itemSub2 = subBuilder.setContentView(childIcon2, itemIconLayoutParams).build();
//        SubActionButton itemSub3 = subBuilder.setContentView(childIcon3, itemIconLayoutParams).build();
//
//
//        actionMenu = new FloatingActionMenu.Builder(this)
//                .addSubActionView(itemSub1)
//                .addSubActionView(itemSub2)
//                .addSubActionView(itemSub3)
//                .setStartAngle(-135)
//                .setEndAngle(-45)
//                .attachTo(addActionButton)
//                .build();
//
//        itemSub1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (Constants.ISLOGIN){
//                    Intent intent = new Intent(MainActivity.this, CardCreateNewActivity.class);
//                    startActivity(intent);
//                }else {
//                    ToastUtils.showShort(MainActivity.this,"登录状态才能发帖哦");
//                }
//
//                actionMenu.close(true);
//            }
//        });
//        itemSub2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Constants.ISLOGIN){
//                    Intent intent = new Intent(MainActivity.this, CreateNewImageActivity.class);
//                    startActivity(intent);
//                }else {
//                    ToastUtils.showShort(MainActivity.this,"登录状态才能发图哦");
//                }
//                actionMenu.close(true);
//            }
//        });
//        itemSub3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtils.showShort(MainActivity.this,"开发中，敬请期待3");
//                actionMenu.close(true);
//            }
//        });
//
//
//        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
//            @Override
//            public void onMenuOpened(FloatingActionMenu menu) {
//                // 增加按钮中的+号图标顺时针旋转45度
//                addActionImg.setRotation(0);
//                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
//                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(addActionImg, pvhR);
//                animation.start();
//                //屏幕变暗
////                ScreenUtils.setScreenBgDarken(MainActivity.this);
//            }
//
//            @Override
//            public void onMenuClosed(FloatingActionMenu menu) {
//                // 增加按钮中的+号图标逆时针旋转45度
//
//                addActionImg.setRotation(45);
//                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
//                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(addActionImg, pvhR);
//                animation.start();
//                //屏幕变亮
////                ScreenUtils.setScreenBgLight(MainActivity.this);
//            }
//        });
//
//
//
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
}

package com.riuir.calibur.ui.loginAndRegister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.wxapi.WXEntryActivity;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import butterknife.BindView;
import calibur.core.http.models.login.QQLoginModel;
import calibur.core.http.models.login.WeChatLoginModel;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.JSONUtil;
import calibur.share.util.QQShareUtils;
import calibur.share.util.WXShareUtils;

@Route(path = RouteUtils.userLoginPath)
public class LoginAndRegisterActivity extends BaseActivity implements IUiListener {

    WeChatLoginModel wxLoginData;
    QQLoginModel qqLoginData;

    @BindView(R.id.login_and_register_viewpager)
    ViewPager viewPager;
    @BindView(R.id.login_and_register_tab)
    MyPagerSlidingTabStrip tabStrip;
    @BindView(R.id.login_and_register_wx_login)
    ImageView wechatLogin;
    @BindView(R.id.login_and_register_qq_login)
    ImageView qqLogin;
    @BindView(R.id.login_and_register_activity_loading_view)
    ImageView loadingView;

    WXShareUtils wxShareUtils;

    RegisterFragment registerFragment;
    LoginFragment loginFragment;
    private DisplayMetrics dm;

    WXLoginReceiver wxLoginReceiver;
    IntentFilter wxFilter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login_and_register;
    }

    @Override
    protected void onInit() {
        dm = getResources().getDisplayMetrics();
        setViewPager();
        GlideUtils.loadImageViewStaticGif(this,R.mipmap.web_page_loading,loadingView);
        wxShareUtils = new WXShareUtils(this);
        wxShareUtils.register();
        setListener();
        registerReceiver();
    }

    private void registerReceiver() {
        wxLoginReceiver = new WXLoginReceiver();
        wxFilter = new IntentFilter();
        wxFilter.addAction(WXEntryActivity.WX_LOGIN_CALLBACK_DATA);
        registerReceiver(wxLoginReceiver,wxFilter);
    }

    private void  unRegisterReceiver(){
        unregisterReceiver(wxLoginReceiver);
    }

    @Override
    public void onDestroy() {
        wxShareUtils.unregister();
        unRegisterReceiver();
        super.onDestroy();
    }

    private void startLoading(){
        loadingView.setVisibility(View.VISIBLE);
    }
    private void stopLoading(){
        loadingView.setVisibility(View.GONE);
    }


    private void setListener() {
        wechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoading();
                wxShareUtils.login();
            }
        });
        qqLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoading();
                QQShareUtils.login(LoginAndRegisterActivity.this,LoginAndRegisterActivity.this);
            }
        });
    }


    private void setViewPager() {
        viewPager.setAdapter(new LoginAndRegisterAdapter(getSupportFragmentManager()));
        tabStrip.setViewPager(viewPager);
        setTabs();
    }

    private void setTabs() {
        // 设置Tab是自动填充满屏幕的
        tabStrip.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabStrip.setDividerColor(Color.TRANSPARENT);
        tabStrip.setBackgroundResource(R.color.color_00FFFFFF);
        tabStrip.setUnderlineColor(Color.TRANSPARENT);
        //设置underLine
        tabStrip.setUnderlineHeight(0);
        tabStrip.setUnderlineColorResource(R.color.theme_magic_sakura_primary);
        //设置Tab Indicator的高度
        tabStrip.setIndicatorColorResource(R.color.color_FFFFFFFF);
        // 设置Tab Indicator的高度
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, dm));
        //设置textclolo
        tabStrip.setTextColorResource(R.color.color_FFFFFFFF);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabStrip.setSelectedTextColorResource(R.color.color_FFFFFFFF);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        tabStrip.setRoundRadius(1);

        // 取消点击Tab时的背景色
        tabStrip.setTabBackground(0);
    }

    @Override
    public void onComplete(Object o) {
        LogUtils.d("3rdLoginToken","qq obj = "+o.toString());
        qqLoginData = JSONUtil.fromJson(o.toString(),QQLoginModel.class);
        setQQLogin();
    }
    @Override
    public void onError(UiError uiError) {
        ToastUtils.showShort(this,uiError.errorCode+" "+uiError.errorMessage+","+uiError.errorDetail);
    }
    @Override
    public void onCancel() {
        ToastUtils.showShort(this,"取消QQ登录");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data,this);
        super.onActivityResult(requestCode, resultCode, data);
    }

    class LoginAndRegisterAdapter extends FragmentPagerAdapter{
        public LoginAndRegisterAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "登录", "注册" };

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (loginFragment == null){
                        loginFragment = new LoginFragment();
                    }
                    return loginFragment;
                case 1:
                    if (registerFragment == null) {
                        registerFragment = new RegisterFragment();
                    }
                    return registerFragment;

                default:
                    return null;
            }
        }
    }

    @Override
    protected void handler(Message msg) {

    }

    private class WXLoginReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean wxLoginSuccess = intent.getBooleanExtra("wxLoginSuccess",false);
            if (wxLoginSuccess){
                wxLoginData = (WeChatLoginModel) intent.getSerializableExtra("wxLoginData");
                setWeChatLogin();
            }else {
                stopLoading();
                ToastUtils.showShort(LoginAndRegisterActivity.this,"微信登录失败，请重试");
            }
        }
    }

    private void setWeChatLogin() {
        if (wxLoginData!=null){
            apiService.doorWXLogin(wxLoginData.getAccess_token(),"sign",wxLoginData.getOpenid())
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<String>(){
                        @Override
                        public void onSuccess(String s) {
                            // 登录成功
//                            stopLoading();
                            LogUtils.d("3rdLoginToken","token = "+s);
                            ToastUtils.showShort(LoginAndRegisterActivity.this,"登录成功！✿✿ヽ(°▽°)ノ✿");
                            //返回JWT-Token(userToken) 存储下来 作为判断用户是否登录的凭证
                            UserSystem.getInstance().updateUserToken(s);
                            Constants.ISLOGIN = true;
                            Constants.AUTH_TOKEN = s;
                            LoginUtils.getUserInfo(LoginAndRegisterActivity.this);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            stopLoading();
                        }
                    });
        }else {
            ToastUtils.showShort(LoginAndRegisterActivity.this,"微信登录数据为空");
            stopLoading();
        }
    }
    private void setQQLogin() {
        if (qqLoginData!=null){
            apiService.doorQQLogin(qqLoginData.getAccess_token(),"sign")
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<String>(){
                        @Override
                        public void onSuccess(String s) {
                            // 登录成功
//                            stopLoading();
                            LogUtils.d("3rdLoginToken","token = "+s);
                            ToastUtils.showShort(LoginAndRegisterActivity.this,"登录成功！✿✿ヽ(°▽°)ノ✿");
                            //返回JWT-Token(userToken) 存储下来 作为判断用户是否登录的凭证
                            UserSystem.getInstance().updateUserToken(s);
                            Constants.ISLOGIN = true;
                            Constants.AUTH_TOKEN = s;
                            LoginUtils.getUserInfo(LoginAndRegisterActivity.this);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            stopLoading();
                        }
                    });
        }else {
            ToastUtils.showShort(LoginAndRegisterActivity.this,"QQ登录数据为空");
            stopLoading();
        }
    }
}

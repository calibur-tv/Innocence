package com.riuir.calibur.ui.loginAndRegister;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainCardActiveFragment;
import com.riuir.calibur.ui.home.MainFragment;
import com.riuir.calibur.ui.home.MainImageFragment;
import com.riuir.calibur.ui.home.MainScoreFragment;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;

import butterknife.BindView;

public class LoginAndRegisterActivity extends BaseActivity {


    @BindView(R.id.login_and_register_viewpager)
    ViewPager viewPager;
    @BindView(R.id.login_and_register_tab)
    MyPagerSlidingTabStrip tabStrip;
    RegisterFragment registerFragment;
    LoginFragment loginFragment;
    private DisplayMetrics dm;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login_and_register;
    }

    @Override
    protected void onInit() {
        dm = getResources().getDisplayMetrics();
        setViewPager();
    }

    private void setViewPager() {
        viewPager.setAdapter(new LoginAndRegisterAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(5);
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
        tabStrip.setUnderlineColorResource(R.color.color_FFFFFFFF);
        //设置Tab Indicator的高度
        tabStrip.setIndicatorColorResource(R.color.theme_magic_sakura_primary);
        // 设置Tab Indicator的高度
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, dm));
        //设置textclolo
        tabStrip.setTextColorResource(R.color.theme_magic_sakura_primary);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabStrip.setSelectedTextColorResource(R.color.theme_magic_sakura_primary);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        tabStrip.setRoundRadius(1);

        // 取消点击Tab时的背景色
        tabStrip.setTabBackground(0);
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
}

package com.riuir.calibur.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.data.ResponseWrapper;
import com.riuir.calibur.net.RxApiErrorHandleTransformer;
import com.riuir.calibur.net.RxProgressTransformer;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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

/**
 * 帖子fragment
 */
public class MainFragment extends BaseFragment {
    @BindView(R.id.main_card_pager_tab)
    MyPagerSlidingTabStrip mainCardTab;
    @BindView(R.id.main_card_view_pager)
    ViewPager mainCardViewPager;

    MainCardHotFragment mainCardHotFragment;
    MainCardNewFragment mainCardNewFragment;
    MainCardActiveFragment mainCardActiveFragment;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    public static Fragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        Bundle b = new Bundle();
        mainFragment.setArguments(b);
        return mainFragment;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_main;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        dm = getResources().getDisplayMetrics();
        setViewPager();

    }

    private void setViewPager() {
        mainCardViewPager.setAdapter(new MainCardPagerAdapter(getChildFragmentManager()));
        mainCardViewPager.setOffscreenPageLimit(2);
        mainCardTab.setViewPager(mainCardViewPager);
        setMainCardTabs();
    }

    private void setMainCardTabs() {
        // 设置Tab是自动填充满屏幕的
        mainCardTab.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        mainCardTab.setDividerColor(Color.TRANSPARENT);
        mainCardTab.setBackgroundResource(R.color.color_FF23ADE5);
        mainCardTab.setUnderlineColor(Color.TRANSPARENT);
        //设置underLine
        mainCardTab.setUnderlineHeight(2);
        mainCardTab.setUnderlineColorResource(R.color.color_FF23ADE5);
        //设置Tab Indicator的高度
        mainCardTab.setIndicatorColorResource(R.color.color_FFFFFFFF);
        // 设置Tab Indicator的高度
        mainCardTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm));
        // 设置Tab标题文字的大小
        mainCardTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm));
        //设置textclolo
        mainCardTab.setTextColorResource(R.color.color_FFFFFFFF);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        mainCardTab.setSelectedTextColorResource(R.color.color_FFFFFFFF);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        mainCardTab.setRoundRadius(3);

        // 取消点击Tab时的背景色
        mainCardTab.setTabBackground(0);
    }

    public class MainCardPagerAdapter extends FragmentPagerAdapter {

        public MainCardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "最新", "最热","动态" };

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
                    if (mainCardNewFragment == null) {
                        mainCardNewFragment = new MainCardNewFragment();
                    }
                    return mainCardNewFragment;
                case 1:
                    if (mainCardHotFragment == null) {
                        mainCardHotFragment = new MainCardHotFragment();
                    }
                    return mainCardHotFragment;
                case 2:
                    if (mainCardActiveFragment == null){
                        mainCardActiveFragment = new MainCardActiveFragment();
                    }
                    return mainCardActiveFragment;
                default:
                    return null;
            }
        }

    }


}

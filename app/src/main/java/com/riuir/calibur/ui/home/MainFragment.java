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

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.ui.widget.SearchLayout;
import com.riuir.calibur.utils.ActivityUtils;

import butterknife.BindView;

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

    @BindView(R.id.main_search_layout)
    SearchLayout searchLayout;

    MainPostListFragment mainCardActiveFragment;
//    MainCardHotFragment mainCardHotFragment;
//    MainCardNewFragment mainCardNewFragment;
    MainImageListFragment mainImageFragment;
    MainScoreListFragment mainScoreFragment;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    public static MainFragment newInstance() {
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
//        int stautsBarHeight = ActivityUtils.getStatusBarHeight(getContext());
//        rootView.setPadding(0,stautsBarHeight,0,0);
        setViewPager();

    }

    @Override
    public void onResume() {
        super.onResume();
        int stautsBarHeight = ActivityUtils.getStatusBarHeight(getContext());
        rootView.setPadding(0,stautsBarHeight,0,0);
    }

    private void setViewPager() {
        mainCardViewPager.setAdapter(new MainCardPagerAdapter(getChildFragmentManager()));
        mainCardViewPager.setOffscreenPageLimit(5);
        mainCardTab.setViewPager(mainCardViewPager);
        setMainCardTabs();
    }

    private void setMainCardTabs() {
        // 设置Tab是自动填充满屏幕的
        mainCardTab.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        mainCardTab.setDividerColor(Color.TRANSPARENT);
        mainCardTab.setBackgroundResource(R.color.theme_magic_sakura_primary);
        mainCardTab.setUnderlineColor(Color.TRANSPARENT);
        //设置underLine
        mainCardTab.setUnderlineHeight(0);
        mainCardTab.setUnderlineColorResource(R.color.theme_magic_sakura_primary);
        //设置Tab Indicator的高度
        mainCardTab.setIndicatorColorResource(R.color.color_FFFFFFFF);
        // 设置Tab Indicator的高度
        mainCardTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        mainCardTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, dm));
        //设置textclolo
        mainCardTab.setTextColorResource(R.color.color_FFFFFFFF);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        mainCardTab.setSelectedTextColorResource(R.color.color_FFFFFFFF);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        mainCardTab.setRoundRadius(1);

        // 取消点击Tab时的背景色
        mainCardTab.setTabBackground(0);
    }

    public class MainCardPagerAdapter extends FragmentPagerAdapter {

        public MainCardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "帖子", "图片","漫评" };

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
                    if (mainCardActiveFragment == null){
                        mainCardActiveFragment = new MainPostListFragment();
                    }
                    return mainCardActiveFragment;
                case 1:
                    if (mainImageFragment == null) {
                        mainImageFragment = new MainImageListFragment();
                    }
                    return mainImageFragment;
                case 2:
                    if (mainScoreFragment == null) {
                        mainScoreFragment = new MainScoreListFragment();
                    }
                    return mainScoreFragment;

                default:
                    return null;
            }
        }

    }


}

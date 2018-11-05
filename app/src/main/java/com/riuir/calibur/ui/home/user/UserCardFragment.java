package com.riuir.calibur.ui.home.user;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserCardFragment extends BaseFragment {


    @BindView(R.id.user_card_pager_tab)
    MyPagerSlidingTabStrip userCardPagerTab;
    @BindView(R.id.user_card_view_pager)
    ViewPager userCardPager;

    private int userId;
    private String zone;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    //viewpager Tab标题
    private List<String> titles = new ArrayList<>();

    private UserReleaseCardFragment userReleaseCardFragment;
    private UserReplyCardFragment userReplyCardFragment;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_user_card;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {

        dm = getResources().getDisplayMetrics();

        UserMainActivity activity = (UserMainActivity) getActivity();
        userId = activity.getUserId();
        zone = activity.getZone();
        setViewPager();
    }

    private void setViewPager() {
        titles.clear();
        userReleaseCardFragment = null;
        userReplyCardFragment = null;
        //添加帖子fragment
        titles.add("TA的主题帖");
        titles.add("TA回复的");

        userCardPager.setAdapter(new UserCardPagerAdapter(getChildFragmentManager()));
        userCardPagerTab.setViewPager(userCardPager);
        userCardPager.setOffscreenPageLimit(5);
        setUserMainTabs();
    }

    private void setUserMainTabs() {
        // 设置Tab是自动填充满屏幕的
        userCardPagerTab.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        userCardPagerTab.setDividerColor(Color.TRANSPARENT);
        userCardPagerTab.setBackgroundResource(R.color.color_FFFFFFFF);
        //设置underLine
        userCardPagerTab.setUnderlineHeight(0);
        userCardPagerTab.setUnderlineColorResource(R.color.color_FFFFFFFF);
        //设置Tab Indicator的高度
        userCardPagerTab.setIndicatorColorResource(R.color.theme_magic_sakura_primary);
        // 设置Tab Indicator的高度
        userCardPagerTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        userCardPagerTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm));
        //设置textclolo
        userCardPagerTab.setTextColorResource(R.color.color_FF5B5B5B);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        userCardPagerTab.setSelectedTextColorResource(R.color.theme_magic_sakura_primary);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        userCardPagerTab.setRoundRadius(2.5f);

        // 取消点击Tab时的背景色
        userCardPagerTab.setTabBackground(0);

    }

    public class UserCardPagerAdapter extends FragmentPagerAdapter {

        public UserCardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public Fragment getItem(int position) {
            switch (titles.get(position)) {
                case "TA的主题帖":
                    if (userReleaseCardFragment == null) {
                        userReleaseCardFragment = new UserReleaseCardFragment();
                    }
                    return userReleaseCardFragment;
                case "TA回复的":
                    if (userReplyCardFragment == null) {
                        userReplyCardFragment = new UserReplyCardFragment();
                    }
                    return userReplyCardFragment;

                default:
                    return null;
            }
        }

    }

}

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ScreenUtils;
import com.riuir.calibur.data.AnimeListForTimeLine;
import com.riuir.calibur.data.DramaListResp;

import com.riuir.calibur.data.ResponseWrapper;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.net.NetService;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
 * 动漫fragment
 */

public class DramaFragment extends BaseFragment {


    @BindView(R.id.drama_pager_tab)
    MyPagerSlidingTabStrip dramaPagerTab;
    @BindView(R.id.drama_view_pager)
    ViewPager dramaViewPager;

    DramaTagsFragment dramaTagsFragment;
    DramaTimelineFragment dramaTimelineFragment;
    DramaNewAnimeListFragment dramaNewAnimeListFragment;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    public static Fragment newInstance() {
        DramaFragment dramaFragment = new DramaFragment();
        Bundle b = new Bundle();
        dramaFragment.setArguments(b);
        return dramaFragment;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        dm = getResources().getDisplayMetrics();

        setViewPager();

    }





    private void setViewPager() {
        dramaViewPager.setAdapter(new DramaPagerAdapter(getChildFragmentManager()));
        dramaViewPager.setOffscreenPageLimit(2);
        dramaPagerTab.setViewPager(dramaViewPager);
        setDramaTabs();
    }

    private void setDramaTabs() {
        // 设置Tab是自动填充满屏幕的
        dramaPagerTab.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        dramaPagerTab.setDividerColor(Color.TRANSPARENT);
        dramaPagerTab.setBackgroundResource(R.color.theme_magic_sakura_blue);
        //设置underLine
        dramaPagerTab.setUnderlineHeight(2);
        dramaPagerTab.setUnderlineColorResource(R.color.theme_magic_sakura_blue);
        //设置Tab Indicator的高度
        dramaPagerTab.setIndicatorColorResource(R.color.color_FFFFFFFF);
        // 设置Tab Indicator的高度
        dramaPagerTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm));
        // 设置Tab标题文字的大小
        dramaPagerTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm));
        //设置textclolo
        dramaPagerTab.setTextColorResource(R.color.color_FFFFFFFF);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        dramaPagerTab.setSelectedTextColorResource(R.color.color_FFFFFFFF);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        dramaPagerTab.setRoundRadius(3);

        // 取消点击Tab时的背景色
        dramaPagerTab.setTabBackground(0);
    }

    public class DramaPagerAdapter extends FragmentPagerAdapter {

        public DramaPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "新番放送","标签", "时间轴" };

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
                    if (dramaNewAnimeListFragment == null) {
                        dramaNewAnimeListFragment = new DramaNewAnimeListFragment();
                    }
                    return dramaNewAnimeListFragment;
                case 1:
                    if (dramaTagsFragment == null) {
                        dramaTagsFragment = new DramaTagsFragment();
                    }
                    return dramaTagsFragment;
                case 2:
                    if (dramaTimelineFragment == null) {
                        dramaTimelineFragment = new DramaTimelineFragment();
                    }
                    return dramaTimelineFragment;
                default:
                    return null;
            }
        }

    }
}

package com.riuir.calibur.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ScreenUtils;
import com.riuir.calibur.data.AnimeListForTimeLine;
import com.riuir.calibur.data.DramaListResp;

import com.riuir.calibur.data.ResponseWrapper;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.net.NetService;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaSearchActivity;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.utils.ActivityUtils;

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

    @BindView(R.id.drama_search_edit_text)
    TextView searchEdit;
    @BindView(R.id.drama_search_btn)
    ImageView searchBtn;

    @BindView(R.id.drama_pager_tab)
    MyPagerSlidingTabStrip dramaPagerTab;
    @BindView(R.id.drama_view_pager)
    ViewPager dramaViewPager;

    DramaTagsFragment dramaTagsFragment;
    DramaTimelineFragment dramaTimelineFragment;
    DramaNewAnimeListFragment dramaNewAnimeListFragment;
    DramaRolesListFragment dramaRolesListFragment;

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
        int stautsBarHeight = ActivityUtils.getStatusBarHeight(getContext());
        rootView.setPadding(0,stautsBarHeight,0,0);
        setViewPager();

    }

    private void setViewPager() {
        dramaViewPager.setAdapter(new DramaPagerAdapter(getChildFragmentManager()));
        dramaViewPager.setOffscreenPageLimit(5);
        dramaPagerTab.setViewPager(dramaViewPager);
        setListener();
        setDramaTabs();
    }

    private void setListener() {
        searchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toSearchActivityIntent = new Intent(getContext(), DramaSearchActivity.class);

                //版本大于5.0的时候带有动画
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(toSearchActivityIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                            view, "ToSearchDramaActivity").toBundle());
                }else {
                    startActivity(toSearchActivityIntent);
                }
            }
        });
    }

    private void setDramaTabs() {
        // 设置Tab是自动填充满屏幕的
        dramaPagerTab.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        dramaPagerTab.setDividerColor(Color.TRANSPARENT);
        dramaPagerTab.setBackgroundResource(R.color.theme_magic_sakura_primary);
        //设置underLine
        dramaPagerTab.setUnderlineHeight(0);
        dramaPagerTab.setUnderlineColorResource(R.color.theme_magic_sakura_primary);
        //设置Tab Indicator的高度
        dramaPagerTab.setIndicatorColorResource(R.color.color_FFFFFFFF);
        // 设置Tab Indicator的高度
//        dramaPagerTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        dramaPagerTab.setIndicatorHeight(DensityUtils.dp2px(getContext(),2));
        // 设置Tab标题文字的大小
        dramaPagerTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, dm));
        //设置textclolo
        dramaPagerTab.setTextColorResource(R.color.color_FFFFFFFF);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        dramaPagerTab.setSelectedTextColorResource(R.color.color_FFFFFFFF);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        dramaPagerTab.setRoundRadius(2.5f);

        // 取消点击Tab时的背景色
        dramaPagerTab.setTabBackground(0);
    }

    public class DramaPagerAdapter extends FragmentPagerAdapter {

        public DramaPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "新番","标签", "时间轴","角色榜" };

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
                case 3:
                    if (dramaRolesListFragment == null) {
                        dramaRolesListFragment = new DramaRolesListFragment();
                    }
                    return dramaRolesListFragment;
                default:
                    return null;
            }
        }

    }
}

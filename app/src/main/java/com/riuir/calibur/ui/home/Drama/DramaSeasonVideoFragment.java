package com.riuir.calibur.ui.home.Drama;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.anime.AnimeShowVideosInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class DramaSeasonVideoFragment extends BaseFragment {

    @BindView(R.id.drama_video_pager_tab)
    MyPagerSlidingTabStrip dramaVideoPagerTab;
    @BindView(R.id.drama_video_view_pager)
    ViewPager dramaVideoViewPager;
    @BindView(R.id.drama_video_pager_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.drama_video_pager_empty_view_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.drama_video_pager_empty_view_icon)
    ImageView emptyIcon;
    @BindView(R.id.drama_video_pager_empty_view_text)
    TextView emptyText;
    private int animeID;

    List<AnimeShowVideosInfo.AnimeShowVideosInfoVideos> animeShowVideosInfoVideos;

    //viewpager Tab标题
    private List<String> titles = new ArrayList<>();

    private DramaVideoEpisodesFragment episodesFragment1;
    private DramaVideoEpisodesFragment episodesFragment2;
    private DramaVideoEpisodesFragment episodesFragment3;
    private DramaVideoEpisodesFragment episodesFragment4;
    private DramaVideoEpisodesFragment episodesFragment5;
    private DramaVideoEpisodesFragment episodesFragment6;
    private DramaVideoEpisodesFragment episodesFragment7;
    private DramaVideoEpisodesFragment episodesFragment8;
    private DramaVideoEpisodesFragment episodesFragment9;
    private DramaVideoEpisodesFragment episodesFragment10;

    private DramaVideoPagerAdapter pagerAdapter;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    private Call<AnimeShowVideosInfo> videosInfoCall;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_video;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        dm = getResources().getDisplayMetrics();
        DramaActivity dramaActivity = (DramaActivity) getActivity();
        animeID = dramaActivity.getAnimeID();
        refreshLayout.setRefreshing(true);
        if (animeShowVideosInfoVideos != null) {
            animeShowVideosInfoVideos.clear();
        }
        setNet();
        setListener();
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setNet();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (videosInfoCall != null) {
            videosInfoCall.cancel();
        }
        super.onDestroy();
    }

    private void setNet() {

        apiService.getCallAnimeShowVideos(animeID)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<AnimeShowVideosInfo>() {

                    @Override
                    public void onSuccess(AnimeShowVideosInfo animeShowVideosInfo) {
                        animeShowVideosInfoVideos = animeShowVideosInfo.getVideos();

                        if (dramaVideoViewPager != null && refreshLayout != null) {
                            setViewPager();
                            refreshLayout.setRefreshing(false);
                            refreshLayout.setEnabled(false);
                        }
                        setEmptyView();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (refreshLayout != null) {
                            refreshLayout.setRefreshing(false);
                            setFailedView();
                        }
                    }
                });
    }

    private void setEmptyView() {
        if (animeShowVideosInfoVideos.size() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
            emptyIcon.setImageResource(R.mipmap.ic_no_content_empty_view);
            emptyText.setText("这里空空如也");
        }
    }

    private void setFailedView() {
        emptyLayout.setVisibility(View.VISIBLE);
        emptyIcon.setImageResource(R.mipmap.ic_failed_empty_view);
        emptyText.setText("加载失败，下拉重试");
    }

    private void setHideEmptyView() {
        if (animeShowVideosInfoVideos.size() != 0) {
            emptyLayout.setVisibility(View.GONE);
        }
    }

    private void setViewPager() {


        titles.clear();

        if (animeShowVideosInfoVideos.size() == 1) {
            titles.add("番剧列表");
        } else {
            for (AnimeShowVideosInfo.AnimeShowVideosInfoVideos videos : animeShowVideosInfoVideos) {
                titles.add(videos.getName());
            }
        }

        pagerAdapter = new DramaVideoPagerAdapter(getChildFragmentManager());
        dramaVideoViewPager.setAdapter(pagerAdapter);
        dramaVideoPagerTab.setViewPager(dramaVideoViewPager);
        dramaVideoViewPager.setOffscreenPageLimit(3);
        setDramaTabs();
        setHideEmptyView();
    }

    private void setDramaTabs() {
        // 设置Tab是自动填充满屏幕的
        dramaVideoPagerTab.setShouldExpand(false);
        // 设置Tab的分割线是透明的
        dramaVideoPagerTab.setDividerColor(Color.TRANSPARENT);
        dramaVideoPagerTab.setBackgroundResource(R.color.color_FFFFFFFF);
        //设置underLine
        dramaVideoPagerTab.setUnderlineHeight(0);
        dramaVideoPagerTab.setUnderlineColorResource(R.color.color_FFFFFFFF);
        //设置Tab Indicator的高度
        dramaVideoPagerTab.setIndicatorColorResource(R.color.theme_magic_sakura_primary);
        // 设置Tab Indicator的高度
        dramaVideoPagerTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        dramaVideoPagerTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm));
        //设置textclolo
        dramaVideoPagerTab.setTextColorResource(R.color.color_FF5B5B5B);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        dramaVideoPagerTab.setSelectedTextColorResource(R.color.theme_magic_sakura_primary);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        dramaVideoPagerTab.setRoundRadius(2.5f);

        // 取消点击Tab时的背景色
        dramaVideoPagerTab.setTabBackground(0);
    }

    public class DramaVideoPagerAdapter extends FragmentPagerAdapter {

        public DramaVideoPagerAdapter(FragmentManager fm) {
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

            switch (position) {
                case 0:
                    if (episodesFragment1 == null) {
                        episodesFragment1 = new DramaVideoEpisodesFragment();
                        episodesFragment1.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment1;
                case 1:
                    if (episodesFragment2 == null) {
                        episodesFragment2 = new DramaVideoEpisodesFragment();
                        episodesFragment2.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment2;
                case 2:
                    if (episodesFragment3 == null) {
                        episodesFragment3 = new DramaVideoEpisodesFragment();
                        episodesFragment3.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment3;
                case 3:
                    if (episodesFragment4 == null) {
                        episodesFragment4 = new DramaVideoEpisodesFragment();
                        episodesFragment4.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment4;
                case 4:
                    if (episodesFragment5 == null) {
                        episodesFragment5 = new DramaVideoEpisodesFragment();
                        episodesFragment5.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment5;
                case 5:
                    if (episodesFragment6 == null) {
                        episodesFragment6 = new DramaVideoEpisodesFragment();
                        episodesFragment6.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment6;
                case 6:
                    if (episodesFragment7 == null) {
                        episodesFragment7 = new DramaVideoEpisodesFragment();
                        episodesFragment7.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment7;
                case 7:
                    if (episodesFragment8 == null) {
                        episodesFragment8 = new DramaVideoEpisodesFragment();
                        episodesFragment8.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment8;
                case 8:
                    if (episodesFragment9 == null) {
                        episodesFragment9 = new DramaVideoEpisodesFragment();
                        episodesFragment9.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment9;
                case 9:
                    if (episodesFragment10 == null) {
                        episodesFragment10 = new DramaVideoEpisodesFragment();
                        episodesFragment10.setData(animeShowVideosInfoVideos, position);
                    }
                    return episodesFragment10;
                default:
                    return null;
            }
        }

    }

}

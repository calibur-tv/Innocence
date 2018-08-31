package com.riuir.calibur.ui.home.Drama;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.anime.AnimeShowVideosInfo;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DramaSeasonVideoFragment extends BaseFragment {


    @BindView(R.id.drama_video_pager_tab)
    MyPagerSlidingTabStrip dramaVideoPagerTab;
    @BindView(R.id.drama_video_view_pager)
    ViewPager dramaVideoViewPager;

    private int animeID;

    List<AnimeShowVideosInfo.AnimeShowVideosInfoVideos> animeShowVideosInfoVideos;

    //viewpager Tab标题
    private List<String> titles = new ArrayList<>();

    private DramaVideoEpisodesFragment episodesFragment1,episodesFragment2,episodesFragment3,episodesFragment4,
                                        episodesFragment5,episodesFragment6,episodesFragment7,episodesFragment8,episodesFragment9,episodesFragment10;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_video;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        dm = getResources().getDisplayMetrics();
        DramaActivity dramaActivity = (DramaActivity) getActivity();
        animeID = dramaActivity.getAnimeID();
        setNet();
    }

    private void setNet() {
        apiGet.getCallAnimeShowVideos(animeID).enqueue(new Callback<AnimeShowVideosInfo>() {
            @Override
            public void onResponse(Call<AnimeShowVideosInfo> call, Response<AnimeShowVideosInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    animeShowVideosInfoVideos = response.body().getData().getVideos();
                    setViewPager();
                }else if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(getContext(),info.getMessage());
                }else {
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                }
            }

            @Override
            public void onFailure(Call<AnimeShowVideosInfo> call, Throwable t) {
                ToastUtils.showShort(getContext(),"请检查您的网络哟！");
            }
        });
    }

    private void setViewPager() {

        titles.clear();

        if (animeShowVideosInfoVideos.size() == 1){
            titles.add("番剧列表");
        }else {
            for (AnimeShowVideosInfo.AnimeShowVideosInfoVideos videos:animeShowVideosInfoVideos){
                titles.add(videos.getName());
            }
        }

        dramaVideoViewPager.setAdapter(new DramaVideoPagerAdapter(getChildFragmentManager()));
        dramaVideoPagerTab.setViewPager(dramaVideoViewPager);
        dramaVideoViewPager.setOffscreenPageLimit(5);
        setDramaTabs();
    }

    private void setDramaTabs() {
        // 设置Tab是自动填充满屏幕的
        dramaVideoPagerTab.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        dramaVideoPagerTab.setDividerColor(Color.TRANSPARENT);
        dramaVideoPagerTab.setBackgroundResource(R.color.theme_magic_sakura_primary);
        //设置underLine
        dramaVideoPagerTab.setUnderlineHeight(2);
        dramaVideoPagerTab.setUnderlineColorResource(R.color.theme_magic_sakura_primary);
        //设置Tab Indicator的高度
        dramaVideoPagerTab.setIndicatorColorResource(R.color.color_FFFFFFFF);
        // 设置Tab Indicator的高度
        dramaVideoPagerTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm));
        // 设置Tab标题文字的大小
        dramaVideoPagerTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm));
        //设置textclolo
        dramaVideoPagerTab.setTextColorResource(R.color.color_FFFFFFFF);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        dramaVideoPagerTab.setSelectedTextColorResource(R.color.color_FFFFFFFF);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        dramaVideoPagerTab.setRoundRadius(3);

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
                        episodesFragment1.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment1;
                case 1:
                    if (episodesFragment2 == null) {
                        episodesFragment2 = new DramaVideoEpisodesFragment();
                        episodesFragment2.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment2;
                case 2:
                    if (episodesFragment3 == null) {
                        episodesFragment3 = new DramaVideoEpisodesFragment();
                        episodesFragment3.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment3;
                case 3:
                    if (episodesFragment4 == null) {
                        episodesFragment4 = new DramaVideoEpisodesFragment();
                        episodesFragment4.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment4;
                case 4:
                    if (episodesFragment5 == null) {
                        episodesFragment5 = new DramaVideoEpisodesFragment();
                        episodesFragment5.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment5;
                case 5:
                    if (episodesFragment6 == null) {
                        episodesFragment6 = new DramaVideoEpisodesFragment();
                        episodesFragment6.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment6;
                case 6:
                    if (episodesFragment7 == null) {
                        episodesFragment7 = new DramaVideoEpisodesFragment();
                        episodesFragment7.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment7;
                case 7:
                    if (episodesFragment8 == null) {
                        episodesFragment8 = new DramaVideoEpisodesFragment();
                        episodesFragment8.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment8;
                case 8:
                    if (episodesFragment9 == null) {
                        episodesFragment9 = new DramaVideoEpisodesFragment();
                        episodesFragment9.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment9;
                case 9:
                    if (episodesFragment10 == null) {
                        episodesFragment10 = new DramaVideoEpisodesFragment();
                        episodesFragment10.setData(animeShowVideosInfoVideos,position);
                    }
                    return episodesFragment10;
                default:
                    return null;
            }
        }

    }


}

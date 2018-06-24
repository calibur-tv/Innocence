package com.riuir.calibur.ui.home.Drama;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.data.AnimeShowInfo;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.DramaFragment;
import com.riuir.calibur.ui.home.DramaNewAnimeListFragment;
import com.riuir.calibur.ui.home.DramaTagsFragment;
import com.riuir.calibur.ui.home.DramaTimelineFragment;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaActivity extends BaseActivity {

    @BindView(R.id.drama_activity_anime_icon)
    ImageView animeIcon;
    @BindView(R.id.drama_activity_anime_banner)
    ImageView animeBanner;
    @BindView(R.id.drama_activity_anime_name)
    TextView animeName;
    @BindView(R.id.drama_activity_anime_follow)
    TextView animeFollowCount;
    @BindView(R.id.drama_activity_anime_card_count)
    TextView animeCardCount;
    @BindView(R.id.drama_activity_anime_follow_btn)
    LinearLayout animeFollowBtn;
    @BindView(R.id.drama_activity_anime_follow_btn_text)
    TextView animeFollowBtnText;


    @BindView(R.id.drama_activity_pager_tab)
    MyPagerSlidingTabStrip dramaPagerTab;
    @BindView(R.id.drama_activity_view_pager)
    ViewPager dramaViewPager;

    AnimeShowInfo.AnimeShowInfoData animeShowInfoData;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    DramaCardFragment dramaCardFragment;
    DramaVideoFragment dramaVideoFragment;
    DramaCartoonFragment dramaCartoonFragment;

    int animeID;

    //viewpager Tab标题
    private List<String> titles = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama;
    }

    @Override
    protected void onInit() {
        dm = getResources().getDisplayMetrics();
        Intent intent = getIntent();
        animeID = intent.getIntExtra("animeId",0);

        setNet();

    }

    private void setView() {
        GlideUtils.loadImageView(DramaActivity.this,animeShowInfoData.getAvatar(),animeIcon);
        GlideUtils.loadImageViewBlur(DramaActivity.this,animeShowInfoData.getBanner(),animeBanner);
        animeName.setText(animeShowInfoData.getName());
        animeFollowCount.setText(animeShowInfoData.getCount_like()+"");

        setViewPager();
    }

    private void setNet() {
        apiGet.getCallAnimeShow(animeID).enqueue(new Callback<AnimeShowInfo>() {
            @Override
            public void onResponse(Call<AnimeShowInfo> call, Response<AnimeShowInfo> response) {
                if (response!=null&&response.body()!=null){
                    if (response.body().getCode() == 0){
                        animeShowInfoData = response.body().getData();
                        setView();
                    }
                }else {

                }
            }

            @Override
            public void onFailure(Call<AnimeShowInfo> call, Throwable t) {

            }
        });
    }

    private void setViewPager() {

        //添加帖子fragment
        titles.add("帖子");
        if (animeShowInfoData.isHas_cartoon()){
            //添加漫画fragment
            titles.add("漫画");
        }
        if (animeShowInfoData.isHas_video()){
            //添加视频fragment
            titles.add("视频");
        }
        dramaViewPager.setAdapter(new DramaActivityPagerAdapter(getSupportFragmentManager()));
        dramaViewPager.setOffscreenPageLimit(5);
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
        dramaPagerTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm));
        //设置textclolo
        dramaPagerTab.setTextColorResource(R.color.color_FFFFFFFF);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        dramaPagerTab.setSelectedTextColorResource(R.color.color_FFFFFFFF);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        dramaPagerTab.setRoundRadius(3);

        // 取消点击Tab时的背景色
        dramaPagerTab.setTabBackground(0);
    }

    @Override
    protected void handler(Message msg) {

    }

    public class DramaActivityPagerAdapter extends FragmentPagerAdapter {

        public DramaActivityPagerAdapter(FragmentManager fm) {
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
                case "帖子":
                    if (dramaCardFragment == null) {
                        dramaCardFragment = new DramaCardFragment();
                    }
                    return dramaCardFragment;
                case "视频":
                    if (dramaVideoFragment == null) {
                        dramaVideoFragment = new DramaVideoFragment();
                    }
                    return dramaVideoFragment;
                case "漫画":
                    if (dramaCartoonFragment == null) {
                        dramaCartoonFragment = new DramaCartoonFragment();
                    }
                    return dramaCartoonFragment;
                default:
                    return null;
            }


        }

    }

    public int getAnimeID() {
        return animeID;
    }

}

package com.riuir.calibur.ui.home.Drama;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.data.AnimeShowInfo;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.DramaNewAnimeListFragment;
import com.riuir.calibur.ui.home.DramaTagsFragment;
import com.riuir.calibur.ui.home.DramaTimelineFragment;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.utils.GlideUtils;

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


    int animeID;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        animeID = intent.getIntExtra("animeId",0);

        setNet();

    }

    private void setView() {
        GlideUtils.loadImageView(DramaActivity.this,animeShowInfoData.getAvatar(),animeIcon);
        GlideUtils.loadImageView(DramaActivity.this,animeShowInfoData.getBanner(),animeBanner);
        animeName.setText(animeShowInfoData.getName());
        animeFollowCount.setText(animeShowInfoData.getCount_like()+"");

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



    @Override
    protected void handler(Message msg) {

    }

    public class DramaActivityPagerAdapter extends FragmentPagerAdapter {

        public DramaActivityPagerAdapter(FragmentManager fm) {
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
//            switch (position) {
//                case 0:
//                    if (dramaNewAnimeListFragment == null) {
//                        dramaNewAnimeListFragment = new DramaNewAnimeListFragment();
//                    }
//                    return dramaNewAnimeListFragment;
//                case 1:
//                    if (dramaTagsFragment == null) {
//                        dramaTagsFragment = new DramaTagsFragment();
//                    }
//                    return dramaTagsFragment;
//                case 2:
//                    if (dramaTimelineFragment == null) {
//                        dramaTimelineFragment = new DramaTimelineFragment();
//                    }
//                    return dramaTimelineFragment;
//                default:
                    return null;
//            }
        }

    }

}

package com.riuir.calibur.ui.home.Drama;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.AnimeShowInfo;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.anime.AnimeFollowInfo;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.io.IOException;
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
    @BindView(R.id.drama_activity_anime_follow_count)
    TextView animeFollowCount;
    @BindView(R.id.drama_activity_anime_card_count)
    TextView animeCardCount;

    @BindView(R.id.drama_activity_anime_follow_btn)
    LinearLayout animeFollowBtn;
    @BindView(R.id.drama_activity_anime_follow_btn_icon)
    ImageView animeFollowBtnIcon;
    @BindView(R.id.drama_activity_anime_follow_btn_text)
    TextView animeFollowBtnText;


    @BindView(R.id.drama_activity_pager_tab)
    MyPagerSlidingTabStrip dramaPagerTab;
    @BindView(R.id.drama_activity_view_pager)
    ViewPager dramaViewPager;

    @BindView(R.id.drama_activity_back_btn)
     ImageView backBtn;

    AnimeShowInfo.AnimeShowInfoData animeShowInfoData;

    int followCount;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    DramaCardFragment dramaCardFragment;
    DramaSeasonVideoFragment dramaSeasonVideoFragment;
    DramaCartoonFragment dramaCartoonFragment;
    DramaImageFragment dramaImageFragment;
    DramaScoreFragment dramaScoreFragment;
    DramaRoleFragment dramaRoleFragment;

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
        setListener();

    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        animeFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animeFollowBtnText.setText("关注中");
                animeFollowBtn.setClickable(false);
                setFollowNet();
            }
        });
    }



    private void setView() {
        GlideUtils.loadImageView(DramaActivity.this,animeShowInfoData.getAvatar(),animeIcon);
        GlideUtils.loadImageViewBlur(DramaActivity.this,
                GlideUtils.setImageUrl(DramaActivity.this,animeShowInfoData.getBanner(),GlideUtils.FULL_SCREEN),
                animeBanner);
        animeName.setText(animeShowInfoData.getName());
        followCount = animeShowInfoData.getFollow_users().getTotal();
        animeFollowCount.setText(followCount+"");
        animeCardCount.setText(animeShowInfoData.getCount_score()+"");

        LogUtils.d("isfollowed","isfollowed = "+animeShowInfoData.isFollowed());
        if (animeShowInfoData.isFollowed()){
            animeFollowBtnIcon.setImageResource(R.mipmap.card_show_header_star_checked);
            animeFollowBtnText.setText("已关注");
        }else {
            animeFollowBtnIcon.setImageResource(R.mipmap.card_show_header_star_normal);
            animeFollowBtnText.setText("关注");
        }

        setViewPager();
    }

    private void setFollowNet() {
        apiPost.getCallBangumiToggleFollow("bangumi",animeID).enqueue(new Callback<AnimeFollowInfo>() {
            @Override
            public void onResponse(Call<AnimeFollowInfo> call, Response<AnimeFollowInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    if (response.body().isData()){
                        //关注成功
                        animeFollowBtnText.setText("已关注");
                        animeFollowBtnIcon.setImageResource(R.mipmap.card_show_header_star_checked);
                        ToastUtils.showShort(DramaActivity.this,"关注成功！");
                        followCount++;
                        animeFollowCount.setText(followCount+"");
                    }else {
                        //取消关注
                        animeFollowBtnText.setText("关注");
                        animeFollowBtnIcon.setImageResource(R.mipmap.card_show_header_star_normal);
                        ToastUtils.showShort(DramaActivity.this,"取消关注成功");
                        followCount--;
                        animeFollowCount.setText(followCount+"");
                    }

                }else if (response!=null&&!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);

                    ToastUtils.showShort(DramaActivity.this,info.getMessage());
                    if (animeShowInfoData.isFollowed()){
                        animeFollowBtnText.setText("已关注");
                    }else {
                        animeFollowBtnText.setText("关注");
                    }
                }else {
                    if (animeShowInfoData.isFollowed()){
                        animeFollowBtnText.setText("已关注");
                    }else {
                        animeFollowBtnText.setText("关注");
                    }
                    ToastUtils.showShort(DramaActivity.this,"网络异常，操作失败了");
                }
                animeFollowBtn.setClickable(true);
            }

            @Override
            public void onFailure(Call<AnimeFollowInfo> call, Throwable t) {
                animeFollowBtn.setClickable(true);

                if (animeShowInfoData.isFollowed()){
                    animeFollowBtnText.setText("已关注");
                }else {
                    animeFollowBtnText.setText("关注");
                }
                ToastUtils.showShort(DramaActivity.this,"网络异常，操作失败了");
            }
        });
    }

    private void setNet() {
        LogUtils.d("animeId","animeId = "+animeID);
        ApiGet mApiGet;
        if (Constants.ISLOGIN){
            mApiGet = apiGetHasAuth;
        }else {
            mApiGet = apiGet;
        }
        mApiGet.getCallAnimeShow(animeID).enqueue(new Callback<AnimeShowInfo>() {
            @Override
            public void onResponse(Call<AnimeShowInfo> call, Response<AnimeShowInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    if (response.body().getCode() == 0){
                        LogUtils.d("animeId","11111re  = "+response.body().toString());
                        animeShowInfoData = response.body().getData();
                        setView();
                    }
                }else {
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    LogUtils.d("animeId","errorStrInfo = "+info.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AnimeShowInfo> call, Throwable t) {
                LogUtils.d("animeId","t = "+t.getMessage());
            }
        });
    }

    private void setViewPager() {

        //添加帖子fragment
        titles.add("帖子");
        if (animeShowInfoData.isHas_cartoon()){
            //添加漫画fragment
            //TODO
//            titles.add("漫画");
        }
        if (animeShowInfoData.isHas_video()){
            //添加视频fragment
            titles.add("视频");
        }
        titles.add("相册");

        titles.add("评分");

        titles.add("偶像");

        dramaViewPager.setAdapter(new DramaActivityPagerAdapter(getSupportFragmentManager()));
        dramaPagerTab.setViewPager(dramaViewPager);
        dramaViewPager.setOffscreenPageLimit(5);
        setDramaTabs();
    }

    private void setDramaTabs() {
        // 设置Tab是自动填充满屏幕的
        dramaPagerTab.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        dramaPagerTab.setDividerColor(Color.TRANSPARENT);
        dramaPagerTab.setBackgroundResource(R.color.theme_magic_sakura_primary);
        //设置underLine
        dramaPagerTab.setUnderlineHeight(2);
        dramaPagerTab.setUnderlineColorResource(R.color.theme_magic_sakura_primary);
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
                    if (dramaSeasonVideoFragment == null) {
                        dramaSeasonVideoFragment = new DramaSeasonVideoFragment();
                    }
                    return dramaSeasonVideoFragment;
                case "漫画":
                    if (dramaCartoonFragment == null) {
                        dramaCartoonFragment = new DramaCartoonFragment();
                    }
                    return dramaCartoonFragment;
                case "相册":
                    if (dramaImageFragment == null) {
                        dramaImageFragment = new DramaImageFragment();
                    }
                    return dramaImageFragment;
                case "评分":
                    if (dramaScoreFragment == null) {
                        dramaScoreFragment = new DramaScoreFragment();
                    }
                    return dramaScoreFragment;
                case "偶像":
                    if (dramaRoleFragment == null) {
                        dramaRoleFragment = new DramaRoleFragment();
                    }
                    return dramaRoleFragment;
                default:
                    return null;
            }


        }

    }

    public int getAnimeID() {
        return animeID;
    }

}

package com.riuir.calibur.ui.home.user;

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
import android.widget.TextView;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.user.UserMainInfo;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.utils.GlideUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMainActivity extends BaseActivity {

    @BindView(R.id.user_main_activity_banner)
    ImageView bannerImg;
    @BindView(R.id.user_main_activity_user_icon)
    ImageView userIcon;
    @BindView(R.id.user_main_activity_user_name)
    TextView userName;
    @BindView(R.id.user_main_activity_user_signature)
    TextView userSignature;
    @BindView(R.id.user_main_activity_pager_tab)
    MyPagerSlidingTabStrip userMainPagerTab;
    @BindView(R.id.user_main_activity_view_pager)
    ViewPager userMainPager;
    @BindView(R.id.user_main_info_back_btn)
    ImageView backBtn;

    int userId;
    String zone;

    UserMainInfo.UserMainInfoData userData;

    //viewpager Tab标题
    private List<String> titles = new ArrayList<>();

    private UserFollowedBanguiFragment userFollowedBanguiFragment;
    private UserFollowedRoleFragment userFollowedRoleFragment;
    private UserFollowedImageFragment userFollowedImageFragment;
    private UserCardFragment userCardFragment;
    private UserFollowedScoreFragment userFollowedScoreFragment;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_main;
    }

    @Override
    protected void onInit() {
        dm = getResources().getDisplayMetrics();

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId",0);
        zone = intent.getStringExtra("zone");
        setListener();
        setNet();
    }

    private void setNet() {
        apiGet.getCallUserMainInfo(zone).enqueue(new Callback<UserMainInfo>() {
            @Override
            public void onResponse(Call<UserMainInfo> call, Response<UserMainInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    userData = response.body().getData();
                    setView();
                }else if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    if (info.getCode() == 40401){
                        userName.setText("该用户不存在");
                    }
                }else {
                    ToastUtils.showShort(UserMainActivity.this,"未知错误导致加载失败了！");
                }
            }

            @Override
            public void onFailure(Call<UserMainInfo> call, Throwable t) {
                ToastUtils.showShort(UserMainActivity.this,"请检查您的网络哦！");
            }
        });
    }

    private void setView() {
        GlideUtils.loadImageViewBlur(UserMainActivity.this,
                GlideUtils.setImageUrl(UserMainActivity.this,userData.getBanner(),GlideUtils.FULL_SCREEN),bannerImg);
        GlideUtils.loadImageViewCircle(UserMainActivity.this,userData.getAvatar(),userIcon);
        userName.setText(userData.getNickname());
        userSignature.setText(userData.getSignature());

        setViewPager();
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void handler(Message msg) {

    }

    private void setViewPager() {

        //添加帖子fragment
        titles.add("番剧");

        titles.add("帖子");
//
        titles.add("相册");
//
        titles.add("评分");

        titles.add("偶像");

        userMainPager.setAdapter(new UserMainPagerAdapter(getSupportFragmentManager()));
        userMainPagerTab.setViewPager(userMainPager);
        userMainPager.setOffscreenPageLimit(5);
        setUserMainTabs();
    }

    private void setUserMainTabs() {
        // 设置Tab是自动填充满屏幕的
        userMainPagerTab.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        userMainPagerTab.setDividerColor(Color.TRANSPARENT);
        userMainPagerTab.setBackgroundResource(R.color.theme_magic_sakura_primary);
        //设置underLine
        userMainPagerTab.setUnderlineHeight(2);
        userMainPagerTab.setUnderlineColorResource(R.color.theme_magic_sakura_primary);
        //设置Tab Indicator的高度
        userMainPagerTab.setIndicatorColorResource(R.color.color_FFFFFFFF);
        // 设置Tab Indicator的高度
        userMainPagerTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm));
        // 设置Tab标题文字的大小
        userMainPagerTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm));
        //设置textclolo
        userMainPagerTab.setTextColorResource(R.color.color_FFFFFFFF);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        userMainPagerTab.setSelectedTextColorResource(R.color.color_FFFFFFFF);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        userMainPagerTab.setRoundRadius(3);

        // 取消点击Tab时的背景色
        userMainPagerTab.setTabBackground(0);
    }

    public class UserMainPagerAdapter extends FragmentPagerAdapter {

        public UserMainPagerAdapter(FragmentManager fm) {
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
                case "番剧":
                    if (userFollowedBanguiFragment == null) {
                        userFollowedBanguiFragment = new UserFollowedBanguiFragment();
                    }
                    return userFollowedBanguiFragment;
                case "帖子":
                    if (userCardFragment == null) {
                        userCardFragment = new UserCardFragment();
                    }
                    return userCardFragment;
                case "相册":
                    if (userFollowedImageFragment == null) {
                        userFollowedImageFragment = new UserFollowedImageFragment();
                    }
                    return userFollowedImageFragment;
                case "评分":
                    if (userFollowedScoreFragment == null) {
                        userFollowedScoreFragment = new UserFollowedScoreFragment();
                    }
                    return userFollowedScoreFragment;
                case "偶像":
                    if (userFollowedRoleFragment == null) {
                        userFollowedRoleFragment = new UserFollowedRoleFragment();
                    }
                    return userFollowedRoleFragment;

                default:
                    return null;
            }
        }

    }


    public int getUserId(){
        return userId;
    }

    public String getZone() {
        return zone;
    }
}

package com.riuir.calibur.ui.home.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.card.PostDetailActivity;
import com.riuir.calibur.ui.home.score.ScoreDetailActivity;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.share.SharePopupActivity;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.GlideUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.user.UserMainInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户主页
 */
@Route(path = RouteUtils.userColumnPath)
public class UserMainActivity extends BaseActivity {

    @BindView(R.id.user_main_activity_banner)
    ImageView bannerImg;
    @BindView(R.id.user_main_activity_user_icon)
    ImageView userIcon;
    @BindView(R.id.user_main_activity_user_name)
    TextView userName;
    @BindView(R.id.user_main_activity_user_level)
    TextView userLevel;
    @BindView(R.id.user_main_activity_user_signature)
    TextView userSignature;
    @BindView(R.id.user_main_activity_pager_tab)
    MyPagerSlidingTabStrip userMainPagerTab;
    @BindView(R.id.user_main_activity_view_pager)
    ViewPager userMainPager;
    @BindView(R.id.user_main_info_back_btn)
    ImageView backBtn;
    @BindView(R.id.user_main_info_more)
    ImageView moreBtn;

    int userId;
    String zone;

    UserMainInfo userData;

    //viewpager Tab标题
    private List<String> titles = new ArrayList<>();

    private UserFollowedBangumiFragment userFollowedBangumiFragment;
    private UserFollowedRoleFragment userFollowedRoleFragment;
    private UserFollowedImageFragment userFollowedImageFragment;
    private UserCardFragment userCardFragment;
    private UserFollowedScoreFragment userFollowedScoreFragment;

    private Call<UserMainInfo> userMainInfoCall;
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
        zone = intent.getStringExtra("zone");

        setListener();
        setNet();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (userMainPager.getAdapter() != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            List<Fragment> fragments = fm.getFragments();
            if(fragments != null && fragments.size() >0){
                for (int i = 0; i < fragments.size(); i++) {
                    ft.remove(fragments.get(i));
                }
            }
            ft.commit();
        }
        titles.clear();
        userMainPager.setAdapter(null);
        userFollowedBangumiFragment = null;
        userFollowedRoleFragment = null;
        userFollowedImageFragment = null;
        userCardFragment = null;
        userFollowedScoreFragment = null;

        zone = intent.getStringExtra("zone");
        setListener();
        setNet();
    }

    @Override
    public void onDestroy() {
        if (userMainInfoCall!=null){
            userMainInfoCall.cancel();
        }
        super.onDestroy();
    }

    private void setNet() {
        apiService.getCallUserMainInfo(zone)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<UserMainInfo>(){

                    @Override
                    public void onSuccess(UserMainInfo info) {
                        userData = info;
                        userId = userData.getId();
                        setView();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }
                });
    }

    private void setView() {
        GlideUtils.loadImageViewBlur(UserMainActivity.this,
                GlideUtils.setImageUrl(UserMainActivity.this,
                                userData.getBanner(),GlideUtils.FULL_SCREEN),bannerImg);
        GlideUtils.loadImageView(UserMainActivity.this,
                GlideUtils.setImageUrlForWidth(UserMainActivity.this,userData.getAvatar(),
                        userIcon.getLayoutParams().width),userIcon);
        userName.setText(userData.getNickname().replace("\n",""));
        userLevel.setText("Lv"+userData.getLevel()+" · 战斗力："+userData.getPower());
        userSignature.setText(userData.getSignature());

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainActivity.this,SharePopupActivity.class);
                intent.putExtra("share_data",userData.getShare_data());
                intent.putExtra("targetTag",SharePopupActivity.USER);
                intent.putExtra("targetId",userData.getId());
                startActivityForResult(intent,SharePopupActivity.SHARE_POPUP_REQUEST_CODE);
            }
        });

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
        titles.add("漫评");

        titles.add("偶像");

        userMainPager.setAdapter(new UserMainPagerAdapter(getSupportFragmentManager()));
        userMainPagerTab.setViewPager(userMainPager);
        userMainPager.setOffscreenPageLimit(5);
        setUserMainTabs();
    }

    private void setUserMainTabs() {
        // 设置Tab是自动填充满屏幕的
        userMainPagerTab.setShouldExpand(false);
        // 设置Tab的分割线是透明的
        userMainPagerTab.setDividerColor(Color.TRANSPARENT);
        userMainPagerTab.setBackgroundResource(R.color.color_FFFFFFFF);
        //设置underLine
        userMainPagerTab.setUnderlineHeight(0);
        userMainPagerTab.setUnderlineColorResource(R.color.color_FFFFFFFF);
        //设置Tab Indicator的高度
        userMainPagerTab.setIndicatorColorResource(R.color.theme_magic_sakura_primary);
        // 设置Tab Indicator的高度
        userMainPagerTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        userMainPagerTab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm));
        //设置textclolo
        userMainPagerTab.setTextColorResource(R.color.color_FF5B5B5B);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        userMainPagerTab.setSelectedTextColorResource(R.color.theme_magic_sakura_primary);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        userMainPagerTab.setRoundRadius(2.5f);

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
                    if (userFollowedBangumiFragment == null) {
                        userFollowedBangumiFragment = new UserFollowedBangumiFragment();
                    }
                    return userFollowedBangumiFragment;
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
                case "漫评":
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

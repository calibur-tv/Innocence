package com.riuir.calibur.ui.home.image;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.PermissionUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.DramaFragment;
import com.riuir.calibur.ui.home.DramaNewAnimeListFragment;
import com.riuir.calibur.ui.home.DramaRolesListFragment;
import com.riuir.calibur.ui.home.DramaTagsFragment;
import com.riuir.calibur.ui.home.DramaTimelineFragment;
import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.utils.Constants;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class CreateNewImageActivity extends BaseActivity {
    @BindView(R.id.create_new_image_activity_cancel)
    ImageView cancelBtn;

    @BindView(R.id.create_new_image_activity_view_pager)
    ViewPager viewPager;
    @BindView(R.id.create_new_image_activity_pager_tab)
    MyPagerSlidingTabStrip tabStrip;

    CreateImageUpLoadFragment imageUpLoadFragment;
    CreateImageAlbumFragment imageAlbumFragment;

    SweetAlertDialog cancelDialog;
    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_create_new_image;
    }

    @Override
    protected void onInit() {
        if (!Constants.ISLOGIN){
            ToastUtils.showShort(this,"登录之后才能发帖");
            finish();
        }
        dm = getResources().getDisplayMetrics();
        setViewPager();
        setListener();
        PermissionUtils.chekReadAndWritePermission(CreateNewImageActivity.this);
    }

    private void setListener() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelListener();

            }
        });
    }

    private void setCancelListener() {
        cancelDialog = new SweetAlertDialog(CreateNewImageActivity.this, SweetAlertDialog.WARNING_TYPE);
        cancelDialog.setTitleText("退出发图")
                .setContentText("退出的话，已编辑的数据不会保存哦")
                .setCancelText("取消")
                .setConfirmText("退出")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        setCancelListener();
    }

    private void setViewPager() {
        viewPager.setAdapter(new NewImagePagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        tabStrip.setViewPager(viewPager);
        setDramaTabs();
    }


    private void setDramaTabs() {
        // 设置Tab是自动填充满屏幕的
        tabStrip.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabStrip.setDividerColor(Color.TRANSPARENT);
        tabStrip.setBackgroundResource(R.color.theme_magic_sakura_primary);
        //设置underLine
        tabStrip.setUnderlineHeight(0);
        tabStrip.setUnderlineColorResource(R.color.theme_magic_sakura_primary);
        //设置Tab Indicator的高度
        tabStrip.setIndicatorColorResource(R.color.color_FFFFFFFF);
        // 设置Tab Indicator的高度
//        dramaPagerTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        tabStrip.setIndicatorHeight(DensityUtils.dp2px(this,2));
        // 设置Tab标题文字的大小
        tabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, dm));
        //设置textclolo
        tabStrip.setTextColorResource(R.color.color_FFFFFFFF);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabStrip.setSelectedTextColorResource(R.color.color_FFFFFFFF);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        tabStrip.setRoundRadius(2.5f);

        // 取消点击Tab时的背景色
        tabStrip.setTabBackground(0);
    }

    public class NewImagePagerAdapter extends FragmentPagerAdapter {

        public NewImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "上传图片","新建相册" };

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
                    if (imageUpLoadFragment == null) {
                        imageUpLoadFragment = new CreateImageUpLoadFragment();
                    }
                    return imageUpLoadFragment;
                case 1:
                    if (imageAlbumFragment == null) {
                        imageAlbumFragment = new CreateImageAlbumFragment();
                    }
                    return imageAlbumFragment;
                default:
                    return null;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以做你要做的事情了。
                } else {
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
                    ToastUtils.showShort(CreateNewImageActivity.this, "未取得授权，无法选择图片");
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void handler(Message msg) {

    }
}

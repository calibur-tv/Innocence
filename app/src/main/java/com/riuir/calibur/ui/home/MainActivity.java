package com.riuir.calibur.ui.home;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.orhanobut.logger.Logger;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ScreenUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.RCode;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.widget.MainBottomBar;
import com.riuir.calibur.utils.EventBusUtil;
import com.riuir.calibur.assistUtils.ToastUtils;

import butterknife.BindView;

/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/12/24
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 * ************************************
 */
public class MainActivity extends BaseActivity implements MainBottomBar.OnSingleClickListener {
    @BindView(R.id.framelayout_main)
    FrameLayout framelayoutMain;
    @BindView(R.id.maintab_bottombar)
    MainBottomBar maintabBottombar;
    private Fragment fragmentMain = MainFragment.newInstance();
    private Fragment fragmentDrama = DramaFragment.newInstance();
    private Fragment fragmentMessage = MessageFragment.newInstance();
    private Fragment fragmentMine = MineFragment.newInstance();

    FloatingActionMenu actionMenu;
    ImageView childIcon1;
    ImageView childIcon2;
    ImageView childIcon3;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onInit() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.framelayout_main, fragmentMain)
                .add(R.id.framelayout_main, fragmentDrama)
                .add(R.id.framelayout_main, fragmentMessage)
                .add(R.id.framelayout_main, fragmentMine)
                .hide(fragmentMain)
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .commit();
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .show(fragmentMain)
                .commitAllowingStateLoss();

        maintabBottombar.init();
        maintabBottombar.setOnSingleClickListener(this);

        //demo TODO
//        Logger.d("oninit");
//        handler.sendEmptyMessageDelayed(0, 200);
//        EventBusUtil.sendEvent(new Event(RCode.EventCode.A));


        setFloatingActionBth();
    }

    //将addBtn 以actionBtn的方式在这里初始化
    private void setFloatingActionBth() {
        //宽高的px  由dp转换而成
        int addBtnParams = DensityUtils.dp2px(this,45);
        int itemIconParams = DensityUtils.dp2px(this,40);

        //设置CircularFloatingActionMenu点击展开扇形图
        final ImageView addActionImg = new ImageView(this);
        addActionImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //设置image宽高边距
        FloatingActionButton.LayoutParams addImgParams = new FloatingActionButton.LayoutParams(addBtnParams,
                addBtnParams);
        addImgParams.setMargins(0, 0, 0,
                8);
        addActionImg.setLayoutParams(addImgParams);
        addActionImg.setImageDrawable(getResources().getDrawable(R.mipmap.maintab_new_normal));

        //设置FloatingActionButton宽高边距
        FloatingActionButton.LayoutParams addActionButtonParams = new FloatingActionButton.LayoutParams(
                addBtnParams, addBtnParams);
        addActionButtonParams.setMargins(0, 0,
                0, 0);
        final FloatingActionButton addActionButton =
                new FloatingActionButton.Builder(this).setContentView(addActionImg,addActionButtonParams)
                        .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                        .setLayoutParams(addImgParams)
                        .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder subBuilder = new SubActionButton.Builder(this);
//        lCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

        FrameLayout.LayoutParams itemIconLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        itemIconLayoutParams.setMargins(0, 0,
                0, 0);
        subBuilder.setLayoutParams(itemIconLayoutParams);
        // Set custom layout params
        FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(itemIconParams,
                itemIconParams);
        subBuilder.setLayoutParams(itemParams);

        childIcon1 = new ImageView(this);
        childIcon2 = new ImageView(this);
        childIcon3 = new ImageView(this);

        childIcon1.setImageDrawable(getResources().getDrawable(R.mipmap.main_bottom_add_item_write));
        childIcon2.setImageDrawable(getResources().getDrawable(R.mipmap.main_bottom_add_item_picture));
        childIcon3.setImageDrawable(getResources().getDrawable(R.mipmap.main_bottom_add_item_message));

        SubActionButton itemSub1 = subBuilder.setContentView(childIcon1, itemIconLayoutParams).build();
        SubActionButton itemSub2 = subBuilder.setContentView(childIcon2, itemIconLayoutParams).build();
        SubActionButton itemSub3 = subBuilder.setContentView(childIcon3, itemIconLayoutParams).build();


        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(itemSub1)
                .addSubActionView(itemSub2)
                .addSubActionView(itemSub3)
                .setStartAngle(-135)
                .setEndAngle(-45)
                .attachTo(addActionButton)
                .build();

        itemSub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(MainActivity.this,"点击了1");
                actionMenu.close(true);
            }
        });
        itemSub2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(MainActivity.this,"点击了2");
                actionMenu.close(true);
            }
        });
        itemSub3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(MainActivity.this,"点击了3");
                actionMenu.close(true);
            }
        });


        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // 增加按钮中的+号图标顺时针旋转45度
                addActionImg.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(addActionImg, pvhR);
                animation.start();
                //屏幕变暗
//                ScreenUtils.setScreenBgDarken(MainActivity.this);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // 增加按钮中的+号图标逆时针旋转45度

                addActionImg.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(addActionImg, pvhR);
                animation.start();
                //屏幕变亮
//                ScreenUtils.setScreenBgLight(MainActivity.this);
            }
        });



    }


    @Override
    protected boolean isRegisterEventBus() {

        return true;
    }

    @Override
    protected void handler(Message msg) {
        if (msg.what == 0) {
            ToastUtils.showShort(this,"app launch");
        }
    }

    @Override
    protected void receiveEvent(Event event) {
        super.receiveEvent(event);
        if (event.getCode() == RCode.EventCode.A) {
            ToastUtils.showShort(this,"event bus received");
        }
    }

    @Override
    public void onClickAdd() {

    }

    @Override
    public void onClickOne() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .show(fragmentMain)
                .commitAllowingStateLoss();
    }

    @Override
    public void onClickTwo() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentMain)
                .hide(fragmentMessage)
                .hide(fragmentMine)
                .show(fragmentDrama)
                .commitAllowingStateLoss();
    }

    @Override
    public void onClickThree() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentDrama)
                .hide(fragmentMain)
                .hide(fragmentMine)
                .show(fragmentMessage)
                .commitAllowingStateLoss();
    }

    @Override
    public void onClickFour() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentDrama)
                .hide(fragmentMessage)
                .hide(fragmentMain)
                .show(fragmentMine)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        ExitApp();
    }

    private long exitTime = 0;

    public void ExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showShort(MainActivity.this,"再次点击返回退出程序哟~");
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }
}

package com.riuir.calibur.ui.home;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;
import com.riuir.calibur.R;
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
        //demo
        Logger.d("oninit");
        handler.sendEmptyMessageDelayed(0, 200);
        EventBusUtil.sendEvent(new Event(RCode.EventCode.A));
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
}

package com.riuir.calibur.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.riuir.calibur.data.Event;

import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.net.NetService;
import com.riuir.calibur.utils.ActivityUtils;
import com.riuir.calibur.utils.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/12/24
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 *
 * ************************************
 */
public abstract class BaseActivity extends AppCompatActivity {
    private LayoutInflater mInflater;
    private Unbinder unbinder;
    protected static UIHandler handler = new UIHandler(Looper.getMainLooper());
    protected ApiPost apiPost,apiPostNoAuth;
    protected ApiGet apiGet,apiGetHasAuth;
    protected CompositeDisposable compositeDisposable = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //拿到NetService网络请求的Api返回对象
        apiPost = NetService.getInstance().createServicePost();
        apiPostNoAuth = NetService.getInstance().createServicePostNoAuth();
        apiGet = NetService.getInstance().createServiceGet();
        apiGetHasAuth = NetService.getInstance().createServiceGetHasAuth();

        compositeDisposable = new CompositeDisposable();
        //状态栏透明
        ActivityUtils.setTranslucentStatus(this, true);
        setHandler();

        setContentView(getContentViewId());
        unbinder = ButterKnife.bind(this);
        mInflater = LayoutInflater.from(this);
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
        onInit();
    }

    private void setHandler() {
        handler.setHandler(new IHandler() {
            public void handleMessage(Message msg) {
                handler(msg);//有消息就提交给子类实现的方法
            }
        });
    }

    protected LayoutInflater getInflater() {
        return mInflater;
    }

    /**
     * UI 布局文件
     *
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 初始化
     */
    protected abstract void onInit();

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    /**
     * 子类处理消息
     * @param msg
     */
    protected abstract void handler(Message msg);

    /**
     * [页面跳转]
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(BaseActivity.this,clz));
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * [含有Bundle通过Class打开编辑界面]
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusCome(Event event) {
        if (event != null) {
            receiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(Event event) {
        if (event != null) {
            receiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void receiveEvent(Event event) {

    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    protected void receiveStickyEvent(Event event) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}

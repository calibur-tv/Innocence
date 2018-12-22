package com.riuir.calibur.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import calibur.foundation.bus.BusinessBus;
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
import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
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
    protected ApiPost apiPost,apiPostNoAuth,apiPostNoGeetest;
    protected ApiGet apiGet,apiGetHasAuth;
    protected APIService apiService;
    protected CompositeDisposable compositeDisposable = null;
    private SwipeRefreshLayout mLoadView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //拿到NetService网络请求的Api返回对象
        apiService = RetrofitManager.getInstance().getService(APIService.class);
        apiPost = NetService.getInstance().createServicePost();
        apiGet = NetService.getInstance().createServiceGet();

        apiPostNoAuth = NetService.getInstance().createServicePostNoAuth();
        apiPostNoGeetest = NetService.getInstance().createServicePostNoGeetest();
        apiGetHasAuth = NetService.getInstance().createServiceGetHasAuth();

        compositeDisposable = new CompositeDisposable();
        setHandler();

        setContentView(getContentViewId());
        //状态栏透明
        ActivityUtils.setTranslucentStatusLight(BaseActivity.this);
        ActivityUtils.setTranslucentStatus(this, true);
        unbinder = ButterKnife.bind(this);
        mInflater = LayoutInflater.from(this);
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }

        try {
            onInit();
            onLoadData();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            BusinessBus.post(null, "mainModule/postException2Bugly", throwable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiService = RetrofitManager.getInstance().getService(APIService.class);
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

    protected void onLoadData(){};

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
    protected void handler(Message msg) {}

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

    protected void setLoadingView(View view) {
        this.mLoadView = (SwipeRefreshLayout) view;
    }

    protected void showLoading() {
        if(mLoadView != null) mLoadView.setRefreshing(true);
    }

    protected void hideLoading() {
        if(mLoadView != null) mLoadView.setRefreshing(false);
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

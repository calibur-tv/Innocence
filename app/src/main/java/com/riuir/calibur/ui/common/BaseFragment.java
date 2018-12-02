package com.riuir.calibur.ui.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.net.NetService;
import com.riuir.calibur.utils.ActivityUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import io.reactivex.disposables.CompositeDisposable;

/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/11/16
 * 邮箱：hanbaokun@lanjingren.com
 * 描述：fragment 基类
 * ************************************
 */

public abstract class BaseFragment extends Fragment {
    protected LayoutInflater mLayoutInflater;
    protected View rootView;
    protected ApiPost apiPost,apiPostNoAuth,apiPostNoGeetest;
    protected ApiGet apiGet,apiGetHasAuth;
    protected APIService apiService;
    protected Activity activity;
    protected CompositeDisposable compositeDisposable = null;
    Unbinder unbinder;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        //拿到NetService网络请求的Api返回对象
        apiService = RetrofitManager.getInstance().getService(APIService.class);
        apiPost = NetService.getInstance().createServicePost();
        apiPostNoAuth = NetService.getInstance().createServicePostNoAuth();
        apiPostNoGeetest = NetService.getInstance().createServicePostNoGeetest();
        apiGet = NetService.getInstance().createServiceGet();
        apiGetHasAuth = NetService.getInstance().createServiceGetHasAuth();
        compositeDisposable = new CompositeDisposable();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        rootView = inflater.inflate(getContentViewID(), null);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInit(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        apiService = RetrofitManager.getInstance().getService(APIService.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected abstract int getContentViewID();

    protected abstract void onInit(@Nullable Bundle savedInstanceState);


}

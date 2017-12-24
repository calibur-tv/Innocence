package com.riuir.calibur.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.data.ResponseWrapper;
import com.riuir.calibur.net.RxApiErrorHandleTransformer;
import com.riuir.calibur.net.RxProgressTransformer;
import com.riuir.calibur.ui.common.BaseFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/12/24
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 * ************************************
 */
public class MainFragment extends BaseFragment {
    @BindView(R.id.tv_main)
    TextView tvMain;

    public static Fragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        Bundle b = new Bundle();
        mainFragment.setArguments(b);
        return mainFragment;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_main;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        tvMain.setText("帖子");
        //demo 网络请求
        loadData();
    }

    private void loadData() {
        Map<String, Object> args = new HashMap<>();
        api.dramaList(args)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(new RxApiErrorHandleTransformer<>())
                .compose(new RxProgressTransformer<ResponseWrapper>(activity))
                .subscribe(new Observer<ResponseWrapper>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseWrapper responseWrapper) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}

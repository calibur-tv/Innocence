package com.riuir.calibur.net;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.riuir.calibur.utils.Huder;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @desc 该类为rx 异步过程自动插入progressHud 显示
 * 如：异步网络请求，在开始时（onSubscribe）显示progressHud，在结束（onComplete）或出错（onError）时dismissHUd
 */

public class RxProgressTransformer<T> implements ObservableTransformer<T, T> {

    private static final String DEFAULT_PROGRESS_MESSAGE = "加载中...";
    private Context mContext = null;
    private String mMessage = null;
    private KProgressHUD mHud = null;
    private boolean shouldApply = true;

    /**
     * 构造方法
     * @param mContext context
     * @param message progressHud 显示的message
     */
    public RxProgressTransformer(@NonNull Context mContext, @Nullable String message) {
        this.mContext = mContext;
        this.mMessage = message;
    }

    /**
     * 构造方法
     * @param mContext context
     */
    public RxProgressTransformer(@NonNull Context mContext, @Nullable boolean shouldApply) {
        this.mContext = mContext;
        this.shouldApply = shouldApply;
    }

    /**
     * 构造方法，无message，默认显示DEFAULT_PROGRESS_MESSAGE
     * @param mContext context
     */
    public RxProgressTransformer(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ObservableSource apply(Observable<T> upstream) {
        if(shouldApply)
        {
            return upstream
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            hudShow();
                        }
                    }).doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                            hudDismiss();
                        }
                    }).doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            hudDismiss();
                        }
                    });
        }
        else
        {
            return upstream;
        }


    }

    private void hudShow() {
        if (null != mContext
                && mContext instanceof Activity
                && ((Activity) mContext).isFinishing()) {

                return;
        }
        mHud = Huder.hudProgress(mContext, TextUtils.isEmpty(mMessage) ? DEFAULT_PROGRESS_MESSAGE : mMessage);
    }

    private void hudDismiss() {
        Huder.safeDismissHud(mHud);
    }
}

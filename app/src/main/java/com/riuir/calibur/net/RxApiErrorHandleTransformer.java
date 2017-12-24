package com.riuir.calibur.net;

import com.riuir.calibur.data.RCode;
import com.riuir.calibur.data.ResponseWrapper;
import com.riuir.calibur.utils.ToastUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class RxApiErrorHandleTransformer<T extends ResponseWrapper> implements ObservableTransformer<T, T> {
    private boolean autoToast = false;

    public RxApiErrorHandleTransformer(boolean autoToast) {
        this.autoToast = autoToast;
    }

    public RxApiErrorHandleTransformer() {
    }

    @Override
    public ObservableSource<T> apply(final Observable<T> upstream) {
        return upstream
                .lift(new ObservableOperator<T, T>() {
                    @Override
                    public Observer<? super T> apply(final Observer<? super T> observer) throws Exception {
                        return new Observer<T>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                observer.onSubscribe(d);
                            }

                            @Override
                            public void onNext(T baseResponseModel) {
                                try {
                                    if (baseResponseModel.code == 1000) {
                                        observer.onNext(baseResponseModel);
                                    } else {
                                        handleError(observer, baseResponseModel.code, baseResponseModel.message);
                                    }
                                } catch (Exception e) {
                                    handleError(observer, RCode.ErrorCode.ERROR_LOCAL, "操作失败，请检查网络后重试");
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtils.toastShort("操作失败，请检查网络后重试");
                                observer.onError(e);
                            }

                            @Override
                            public void onComplete() {
                                observer.onComplete();
                            }
                        };
                    }
                });
    }

    private void handleError(Observer<? super T> observer, int errorCode, String message) {
        ApiThrowable throwable = new ApiThrowable(errorCode, message);
        if (autoToast) {
            ToastUtils.toastShort(throwable.getMessage());
        }
        observer.onError(throwable);
    }
}

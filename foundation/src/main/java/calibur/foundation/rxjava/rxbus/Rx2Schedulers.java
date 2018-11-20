package calibur.foundation.rxjava.rxbus;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/06/28 10:54 AM
 * version: 1.0
 * description:
 */
public class Rx2Schedulers {

  public static <T> ObservableTransformer<T, T> applyObservableAsync() {
    return new ObservableTransformer<T, T>() {
      @Override
      public ObservableSource<T> apply(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
      }
    };
  }

  public static <T> SingleTransformer <T, T> applySingleAsync(){
    return new SingleTransformer<T, T>() {
      @Override public SingleSource<T> apply(Single<T> observable) {
        return observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
      }
    };
  }
}

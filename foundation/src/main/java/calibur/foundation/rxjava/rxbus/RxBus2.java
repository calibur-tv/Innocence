package calibur.foundation.rxjava.rxbus;

import com.orhanobut.logger.Logger;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/06/26 3:43 PM
 * version: 1.0
 * description:
 */
@SuppressWarnings("unchecked")
public class RxBus2 {

  private static RxBus2 sInstance;
  private final Subject<Object> mEvents;

  private RxBus2() {
    mEvents = PublishSubject.create().toSerialized();
  }

  public static RxBus2 getDefault() {
    if (sInstance == null) {
      synchronized (RxBus2.class) {
        if (sInstance == null) {
          sInstance = new RxBus2();
        }
      }
    }
    return sInstance;
  }

  public void post(@NonNull Object obj) {
    mEvents.onNext(obj);
  }

  public <T> Disposable register(Class<T> clz, RxBus2Consumer<? super T> onNext) {
    return mEvents.ofType(clz).subscribe(onNext, new RxBus2Consumer<Throwable>() {
      @Override public void consume(Throwable throwable) {
        Logger.e(RxBus2.class.getCanonicalName(), throwable.getMessage());
      }
    });
  }

  public <T> Disposable register(RxBus2Consumer<? super T> onNext) {
    return mEvents.ofType(onNext.getEntity()).subscribe(onNext, new RxBus2Consumer<Throwable>() {
      @Override public void consume(Throwable throwable) {
        Logger.e(RxBus2.class.getCanonicalName(), throwable.getMessage());
      }
    });
  }

  public void unRegister(Disposable disposable) {
    if (disposable != null && !disposable.isDisposed()) {
      disposable.dispose();
    }
  }

  public void unRegisterAll() {
    mEvents.onComplete();
  }

  public boolean hasObservers() {
    return mEvents.hasObservers();
  }
}

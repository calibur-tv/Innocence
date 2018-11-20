package calibur.foundation.rxjava.rxbus;

import calibur.foundation.bus.BusinessBus;
import io.reactivex.functions.Consumer;
import java.lang.reflect.ParameterizedType;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/06/27 4:00 PM
 * version: 1.0
 * description:
 */
public abstract class RxBus2Consumer<T> implements Consumer<T> {

  public Class getEntity() {
    ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
    return (Class) type.getActualTypeArguments()[0];
  }

  public abstract void consume(T t);

  @Override public void accept(T o){
    try {
      consume(o);
    } catch (Throwable t) {
      t.printStackTrace();
      BusinessBus.post(null, "mainModule/postException2Bugly", t);
    }

  }
}

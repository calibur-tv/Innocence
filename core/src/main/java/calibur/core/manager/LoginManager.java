package calibur.core.manager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/19 6:02 PM
 * version: 1.0
 * description:
 */
public class LoginManager {
  private static LoginManager sInstance;

  static LoginManager getInstance() {
    if (sInstance == null) {
      sInstance = new LoginManager();
    }
    return sInstance;
  }

  Observable<String> logout(String token) {
    return Observable.create(new ObservableOnSubscribe<String>() {
      @Override public void subscribe(ObservableEmitter<String> e) throws Exception {

      }
    });
  }

  Observable<String> login(String username, String pwd) {
    return Observable.create(new ObservableOnSubscribe<String>() {
      @Override public void subscribe(ObservableEmitter<String> e) throws Exception {

      }
    });
  }
}

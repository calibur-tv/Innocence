package calibur.core.manager;

import io.reactivex.Observable;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/19 5:58 PM
 * version: 1.0
 * description:
 */
 class ProfileManager {
  private static ProfileManager sInstance;

  private ProfileManager() {
  }

  static ProfileManager getInstance() {
    if (sInstance == null) {
      synchronized (ProfileManager.class) {
        if (sInstance == null) sInstance = new ProfileManager();
      }
    }
    return sInstance;
  }

  Observable<Object> getUserInfo(long userId) {

    return (Observable<Object>) new Object();
  }

}

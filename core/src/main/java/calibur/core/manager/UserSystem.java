package calibur.core.manager;

import android.text.TextUtils;
import calibur.core.utils.ISharedPreferencesKeys;
import calibur.core.utils.SharedPreferencesUtil;
import io.reactivex.Observable;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/19 5:54 PM
 * version: 1.0
 * description: 用户信息管理
 */
public class UserSystem {

  private volatile static UserSystem sInstance;
  private volatile static String mUserToken;

  private UserSystem() {
  }

  public static UserSystem getInstance() {
    if (sInstance == null) {
      synchronized (UserSystem.class) {
        if (sInstance == null) sInstance = new UserSystem();
      }
    }
    return sInstance;
  }

  public synchronized void updateUserToken(String token) {
    mUserToken = "Bearer " + token;
    SharedPreferencesUtil.putString(ISharedPreferencesKeys.MOBILE_TOKEN, token);
  }

  public String getUserToken() {
    if(TextUtils.isEmpty(mUserToken))
      mUserToken = "Bearer " + SharedPreferencesUtil.getString(ISharedPreferencesKeys.MOBILE_TOKEN);
    return mUserToken;
  }

  //注销登录时使用
  public void clearUserToken(){
    mUserToken = "";
    SharedPreferencesUtil.remove(ISharedPreferencesKeys.MOBILE_TOKEN);
  }

  public boolean isLogin() {
    return !TextUtils.isEmpty(SharedPreferencesUtil.getString(ISharedPreferencesKeys.MOBILE_TOKEN));
  }

  public Observable<Object> getUserInfo(long userId) {
    return ProfileManager.getInstance().getUserInfo(userId);
  }

  public Observable<String> login(String username, String pwd) {
    return LoginManager.getInstance().login(username, pwd);
  }

  public Observable<String> logout(String token) {
    return LoginManager.getInstance().logout(token);
  }
}

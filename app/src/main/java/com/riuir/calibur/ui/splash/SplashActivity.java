package com.riuir.calibur.ui.splash;

import android.os.Message;
import calibur.core.http.models.user.MineUserInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.activityUtils.BangumiAllListUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.ui.loginAndRegister.LoginAndRegisterActivity;
import com.riuir.calibur.utils.Constants;

public class SplashActivity extends BaseActivity {

  //预设的登录状态 默认false
  boolean isLogin = false;
  String AUTH_TOKEN = "";

  @Override
  protected int getContentViewId() {
    return R.layout.activity_splash;
  }

  @Override
  protected void onInit() {
    AUTH_TOKEN = Constants.AUTH_TOKEN;
    isLogin = UserSystem.getInstance().isLogin();
    LogUtils.d("Bearer", "token = " + AUTH_TOKEN);
    if (isLogin) {
      setNetToGetUserInfo();
      handler.sendEmptyMessageDelayed(0, 1500);
    } else {
      handler.sendEmptyMessageDelayed(1, 1500);
    }
  }

  private void setNetToGetUserInfo() {
    apiService.getMineUserInfo().compose(Rx2Schedulers.applyObservableAsync())
        .subscribe(new ObserverWrapper<MineUserInfo>() {
          @Override
          public void onSuccess(MineUserInfo mineUserInfo) {
            Constants.userInfoData = mineUserInfo;
            SharedPreferencesUtils.putUserInfoData(App.instance(), Constants.userInfoData);
            BangumiAllListUtils.setBangumiAllList(SplashActivity.this);
          }

          @Override
          public void onFailure(int code, String errorMsg) {
            super.onFailure(code, errorMsg);
          }
        });
  }

  @Override
  protected void handler(Message msg) {
    switch (msg.what) {
      case 0:
        startActivity(MainActivity.class);
        finish();
        break;
      case 1:
        startActivity(LoginAndRegisterActivity.class);
        finish();
        break;
      default:
        break;
    }
  }
}

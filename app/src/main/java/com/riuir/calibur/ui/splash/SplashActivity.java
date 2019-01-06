package com.riuir.calibur.ui.splash;

import android.os.Message;
import calibur.core.http.models.user.MineUserInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.templates.renders.BookmarksTemplateRender;
import calibur.core.templates.renders.CommentItemTemplateRender;
import calibur.core.templates.renders.EditorTemplateRender;
import calibur.core.templates.renders.HomeTemplateRender;
import calibur.core.templates.renders.ImageDetailPageTemplateRender;
import calibur.core.templates.renders.NoticeTemplateRender;
import calibur.core.templates.renders.NotificationsTemplateRender;
import calibur.core.templates.renders.PostDetailPageTemplateRender;
import calibur.core.templates.renders.ReviewTemplateRender;
import calibur.core.templates.renders.RoleDetailTemplateRender;
import calibur.core.templates.renders.TransactionsTemplateRender;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.activityUtils.BangumiAllListUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
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
    AUTH_TOKEN = UserSystem.getInstance().getUserToken();
    isLogin = UserSystem.getInstance().isLogin();
    LogUtils.d("Bearer", "token = " + AUTH_TOKEN);
  }

  @Override
  protected void onLoadData() {
    downloadAndCheckTemplates();
    if (isLogin) {
      setNetToGetUserInfo();
    } else {
      handler.sendEmptyMessageDelayed(1, 1500);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  private void downloadAndCheckTemplates() {
    TemplateRenderEngine.getInstance().setTemplateRender(new ImageDetailPageTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new BookmarksTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new EditorTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new NoticeTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new ReviewTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new PostDetailPageTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new NotificationsTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new TransactionsTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new HomeTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new RoleDetailTemplateRender());
    TemplateRenderEngine.getInstance().setTemplateRender(new CommentItemTemplateRender());
    TemplateRenderEngine.getInstance().checkAllTemplateForUpdate();
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
            handler.sendEmptyMessage(1);
            super.onFailure(code, "获取用户信息失败，请登录");
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

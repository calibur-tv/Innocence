package com.riuir.calibur.ui.splash;

import android.os.Message;

import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.ui.loginAndRegister.LoginActivity;
import com.riuir.calibur.ui.loginAndRegister.RegisterActivity;
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
        isLogin = Constants.ISLOGIN;

        if (isLogin){
            handler.sendEmptyMessageDelayed(0,1500);
        }else {
            handler.sendEmptyMessageDelayed(1,1500);
        }
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            case 0:
                startActivity(MainActivity.class);
                finish();
                return;
            case 1:
                startActivity(LoginActivity.class);
                finish();
                return;
        }
    }
}

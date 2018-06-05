package com.riuir.calibur.ui.splash;

import android.os.Message;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.ui.loginAndRegister.LoginActivity;

public class SplashActivity extends BaseActivity {

    //预设的登录状态 默认false
    boolean isLogin = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onInit() {
        if (isLogin){
            handler.sendEmptyMessageDelayed(0,1500);
        }else {
            handler.sendEmptyMessageDelayed(0,1500);
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

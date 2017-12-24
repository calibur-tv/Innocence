package com.riuir.calibur.ui.splash;

import android.os.Message;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;

public class SplashActivity extends BaseActivity {


    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onInit() {
        handler.sendEmptyMessageDelayed(0,1500);
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            case 0:
                startActivity(MainActivity.class);
                finish();
                break;
        }
    }
}

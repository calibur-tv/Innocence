package com.riuir.calibur.ui.home.score;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;

public class CreateNewScoreActivity extends BaseActivity {

    @BindView(R.id.score_create_new_activity_web_view)
    WebView createScoreWebView;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_create_new_score;
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void handler(Message msg) {

    }
}

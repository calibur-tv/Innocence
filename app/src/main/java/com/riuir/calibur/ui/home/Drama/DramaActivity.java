package com.riuir.calibur.ui.home.Drama;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;

import butterknife.BindView;

public class DramaActivity extends BaseActivity {

    @BindView(R.id.drama_activity_name)
    TextView dramaText;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("animeId",0);
        dramaText.setText(intent.getStringExtra("animeName")+""+id);
    }

    @Override
    protected void handler(Message msg) {

    }
}

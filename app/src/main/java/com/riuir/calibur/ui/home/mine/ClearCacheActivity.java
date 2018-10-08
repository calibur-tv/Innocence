package com.riuir.calibur.ui.home.mine;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.glide.GlideClearCatchUtils;

import java.io.File;
import java.math.BigDecimal;

import butterknife.BindView;

public class ClearCacheActivity extends BaseActivity {
    @BindView(R.id.clear_cache_activity_cache_size)
    TextView cacheSizeText;
    @BindView(R.id.clear_cache_activity_clear_btn)
    Button clearBtn;
    @BindView(R.id.clear_cache_activity_back_btn)
    ImageView backBtn;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_clear_cache;
    }

    @Override
    protected void onInit() {
        setBack();
        setSize();
        setClear();
    }

    private void setClear() {
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBtn.setText("清理中");
                clearBtn.setClickable(false);
                new ClearCacheTask().execute();
            }
        });
    }

    private void setSize() {
        cacheSizeText.setText(GlideClearCatchUtils.getCacheSize());
    }

    private void setBack() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    class ClearCacheTask extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... strings) {
            GlideUtils.GuideClearDiskCache(App.instance());
            String finishSize = GlideClearCatchUtils.getCacheSize();
            return finishSize;
        }

        @Override
        protected void onPostExecute(String finishSize) {
            super.onPostExecute(finishSize);
            cacheSizeText.setText(finishSize);
            clearBtn.setText("点击清理");
            clearBtn.setClickable(true);
            ToastUtils.showShort(ClearCacheActivity.this,"清理成功！");
        }
    }

    @Override
    protected void handler(Message msg) {

    }

}

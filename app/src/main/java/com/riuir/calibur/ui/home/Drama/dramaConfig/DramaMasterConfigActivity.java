package com.riuir.calibur.ui.home.Drama.dramaConfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.data.AnimeShowInfo;
import com.riuir.calibur.ui.common.BaseActivity;

import butterknife.BindView;

public class DramaMasterConfigActivity extends BaseActivity {
    @BindView(R.id.drama_master_config_back_btn)
    ImageView backBtn;
    @BindView(R.id.drama_master_config_anime_setting)
    TextView animeSettingBtn;
    @BindView(R.id.drama_master_config_role_setting)
    TextView roleSettingBtn;
    @BindView(R.id.drama_master_config_post_setting)
    TextView postSettingBtn;

    private int bangumi_id = 0;

    AnimeShowInfo.AnimeShowInfoData animeShowInfoData;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_master_config;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        bangumi_id = intent.getIntExtra("bangumi_id",0);
        animeShowInfoData = (AnimeShowInfo.AnimeShowInfoData) intent.getSerializableExtra("animeShowInfoData");
        setListener();
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        animeSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DramaMasterConfigActivity.this,DramaMasterAnimeSettingActivity.class);
                intent.putExtra("animeShowInfoData",animeShowInfoData);
                intent.putExtra("bangumi_id",bangumi_id);
                startActivity(intent);
                finish();
            }
        });
        roleSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DramaMasterConfigActivity.this,DramaMasterRoleSettingActivity.class);
                intent.putExtra("bangumi_id",bangumi_id);
                intent.putExtra("animeShowInfoData",animeShowInfoData);
                startActivity(intent);
            }
        });
        postSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DramaMasterConfigActivity.this,DramaMasterPostSettingActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void handler(Message msg) {

    }
}

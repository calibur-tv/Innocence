package com.riuir.calibur.ui.home.card;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;

import butterknife.BindView;

public class CardCreateNewActivity extends BaseActivity {

    @BindView(R.id.card_create_new_activity_finish)
    ImageView finishBtn;
    @BindView(R.id.card_create_new_activity_cancel)
    ImageView cancelBtn;
    @BindView(R.id.card_create_new_activity_edit)
    EditText cardSummaryEdit;
    @BindView(R.id.card_create_new_activity_image_grid)
    RecyclerView addImageGrid;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_create_new;
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void handler(Message msg) {

    }
}

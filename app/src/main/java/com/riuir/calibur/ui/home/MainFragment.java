package com.riuir.calibur.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseFragment;

import butterknife.BindView;

/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/12/24
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 * ************************************
 */
public class MainFragment extends BaseFragment {
    @BindView(R.id.tv_main)
    TextView tvMain;

    public static Fragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        Bundle b = new Bundle();
        mainFragment.setArguments(b);
        return mainFragment;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_main;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        tvMain.setText("帖子");
    }
}

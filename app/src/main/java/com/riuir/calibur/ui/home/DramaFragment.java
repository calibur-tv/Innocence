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
public class DramaFragment extends BaseFragment {

    @BindView(R.id.tv_main)
    TextView tvMain;

    public static Fragment newInstance() {
        DramaFragment dramaFragment = new DramaFragment();
        Bundle b = new Bundle();
        dramaFragment.setArguments(b);
        return dramaFragment;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_main;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        tvMain.setText("番剧");
    }
}

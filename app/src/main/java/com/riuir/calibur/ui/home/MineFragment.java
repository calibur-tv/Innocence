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

/**
 * 我的fragment
 */
public class MineFragment extends BaseFragment {


    public static Fragment newInstance() {
        MineFragment mineFragment = new MineFragment();
        Bundle b = new Bundle();
        mineFragment.setArguments(b);
        return mineFragment;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {

    }
}

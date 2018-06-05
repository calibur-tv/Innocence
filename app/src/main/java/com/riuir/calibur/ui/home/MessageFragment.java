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
 * 消息fragment
 */
public class MessageFragment extends BaseFragment {


    public static Fragment newInstance() {
        MessageFragment messageFragment = new MessageFragment();
        Bundle b = new Bundle();
        messageFragment.setArguments(b);
        return messageFragment;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_message;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {

    }
}

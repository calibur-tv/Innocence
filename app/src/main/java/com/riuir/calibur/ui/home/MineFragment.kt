package com.riuir.calibur.ui.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.riuir.calibur.R
import kotlinx.android.synthetic.main.fragment_main.*

/**
 *************************************
 * 作者：韩宝坤
 * 日期：2017/11/21
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 *************************************
 */
class MineFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_main, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_main.text = "我的"
    }
}
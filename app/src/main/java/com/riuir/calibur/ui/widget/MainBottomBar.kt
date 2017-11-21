package com.riuir.calibur.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.riuir.calibur.R
import kotlinx.android.synthetic.main.layout_main_bottombar.view.*


/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/11/16
 * 邮箱：hanbaokun@outlook.com
 * 描述：首页底部导航
 * ************************************
 */

class MainBottomBar : RelativeLayout, View.OnClickListener {

    private var maintabMenu: LinearLayout? = null
    private var maintabDis: RelativeLayout? = null
    private var maintabDisIv: ImageView? = null
    private var maintabDisTv: TextView? = null
    private var maintabDisReddot: ImageView? = null
    private var maintabAdd: ImageView? = null
    private var maintabCircle: RelativeLayout? = null
    private var maintabCircleIv: ImageView? = null
    private var maintabCircleTv: TextView? = null
    private var maintabCircleReddot: ImageView? = null
    private var maintabMsg: RelativeLayout? = null
    private var maintabMsgIv: ImageView? = null
    private var maintabMsgTv: TextView? = null
    private var maintabMsgReddot: ImageView? = null
    private var maintabMine: RelativeLayout? = null
    private var maintabMineIv: ImageView? = null
    private var maintabMineTv: TextView? = null
    private var maintabMineReddot: ImageView? = null
    private var listener: OnSingleClickListener? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.layout_main_bottombar, this, true)
        maintabMenu = view.findViewById<View>(R.id.maintab_menu) as LinearLayout
        maintab_menu

        maintabAdd = view.findViewById<View>(R.id.maintab_add) as ImageView

        maintabDis = view.findViewById<View>(R.id.maintab_dis) as RelativeLayout
        maintabDisIv = view.findViewById<View>(R.id.maintab_dis_iv) as ImageView
        maintabDisTv = view.findViewById<View>(R.id.maintab_dis_tv) as TextView
        maintabDisReddot = view.findViewById<View>(R.id.maintab_dis_reddot) as ImageView

        maintabCircle = view.findViewById<View>(R.id.maintab_circle) as RelativeLayout
        maintabCircleIv = view.findViewById<View>(R.id.maintab_circle_iv) as ImageView
        maintabCircleTv = view.findViewById<View>(R.id.maintab_circle_tv) as TextView
        maintabCircleReddot = view.findViewById<View>(R.id.maintab_circle_reddot) as ImageView

        maintabMsg = view.findViewById<View>(R.id.maintab_msg) as RelativeLayout
        maintabMsgIv = view.findViewById<View>(R.id.maintab_msg_iv) as ImageView
        maintabMsgTv = view.findViewById<View>(R.id.maintab_msg_tv) as TextView
        maintabMsgReddot = view.findViewById<View>(R.id.maintab_msg_reddot) as ImageView

        maintabMine = view.findViewById<View>(R.id.maintab_mine) as RelativeLayout
        maintabMineIv = view.findViewById<View>(R.id.maintab_mine_iv) as ImageView
        maintabMineTv = view.findViewById<View>(R.id.maintab_mine_tv) as TextView
        maintabMineReddot = view.findViewById<View>(R.id.maintab_mine_reddot) as ImageView

        maintabAdd!!.setOnClickListener(this)

        maintabDis!!.setOnClickListener(this)
        maintabCircle!!.setOnClickListener(this)
        maintabMsg!!.setOnClickListener(this)
        maintabMine!!.setOnClickListener(this)
    }

    fun init() {
        reset()
        maintabDis!!.isSelected = true
    }

    private fun reset() {
        maintabDis!!.isSelected = false
        maintabCircle!!.isSelected = false
        maintabMsg!!.isSelected = false
        maintabMine!!.isSelected = false
    }

    fun setSelectMine() {
        reset()
        maintabMine!!.isSelected = true
        if (null != listener) {
            listener!!.onClickMine()
        }
    }

    fun setSelectMsg() {
        reset()
        maintabMsg!!.isSelected = true
        if (null != listener) {
            listener!!.onClickMsg()
        }
    }

    fun setSelectCircle() {
        reset()
        maintabCircle!!.isSelected = true
        if (null != listener) {
            listener!!.onClickCircle()
        }
    }

    fun setSelectDis() {
        reset()
        maintabDis!!.isSelected = true
        if (null != listener) {
            listener!!.onClickDis()
        }
    }

    fun updateDisRedDot(count: Int) {
        if (count > 0) {
            maintabDisReddot!!.visibility = View.VISIBLE
        } else {
            maintabDisReddot!!.visibility = View.GONE
        }
    }

    fun updateCircleRedDot(count: Int) {
        if (count > 0) {
            maintabCircleReddot!!.visibility = View.VISIBLE
        } else {
            maintabCircleReddot!!.visibility = View.GONE
        }
    }

    fun updateMsgRedDot(count: Int) {
        if (count > 0) {
            maintabMsgReddot!!.visibility = View.VISIBLE
        } else {
            maintabMsgReddot!!.visibility = View.GONE
        }
    }

    fun updateMineRedDot(count: Int) {
        if (count > 0) {
            maintabMineReddot!!.visibility = View.VISIBLE
        } else {
            maintabMineReddot!!.visibility = View.GONE
        }
    }

    fun setOnSingleClickListener(listener: OnSingleClickListener) {
        this.listener = listener
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.maintab_add) {
            if (null != listener) {
                listener!!.onClickAdd()
            }
        } else if (i == R.id.maintab_dis) {
            reset()
            maintabDis!!.isSelected = true
            if (null != listener) {
                listener!!.onClickDis()
            }
        } else if (i == R.id.maintab_circle) {
            reset()
            maintabCircle!!.isSelected = true
            if (null != listener) {
                listener!!.onClickCircle()
            }
        } else if (i == R.id.maintab_msg) {
            reset()
            maintabMsg!!.isSelected = true
            if (null != listener) {
                listener!!.onClickMsg()
            }
        } else if (i == R.id.maintab_mine) {
            reset()
            maintabMine!!.isSelected = true
            if (null != listener) {
                listener!!.onClickMine()
            }
        }
    }

    interface OnSingleClickListener {
        fun onClickAdd()

        fun onClickDis()

        fun onClickCircle()

        fun onClickMsg()

        fun onClickMine()
    }
}

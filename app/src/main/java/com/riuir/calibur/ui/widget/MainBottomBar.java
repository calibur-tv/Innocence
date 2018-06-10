package com.riuir.calibur.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.riuir.calibur.R;


/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/11/16
 * 邮箱：hanbaokun@lanjingren.com
 * 描述：首页底部导航
 * ************************************
 */

public class MainBottomBar extends RelativeLayout implements View.OnClickListener {

    private LinearLayout maintabMenu;
    private RelativeLayout maintabDis;
    private ImageView maintabDisIv;
    private TextView maintabDisTv;
    private ImageView maintabDisReddot;
    private ImageView maintabAdd;
    private RelativeLayout maintabCircle;
    private ImageView maintabCircleIv;
    private TextView maintabCircleTv;
    private ImageView maintabCircleReddot;
    private RelativeLayout maintabMsg;
    private ImageView maintabMsgIv;
    private TextView maintabMsgTv;
    private ImageView maintabMsgReddot;
    private RelativeLayout maintabMine;
    private ImageView maintabMineIv;
    private TextView maintabMineTv;
    private ImageView maintabMineReddot;
    private OnSingleClickListener listener;

    public MainBottomBar(Context context) {
        super(context);
        initView(context);
    }

    public MainBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MainBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_main_bottombar, this, true);
        maintabMenu = (LinearLayout) view.findViewById(R.id.maintab_menu);


        maintabAdd = (ImageView) view.findViewById(R.id.maintab_add);


        maintabDis = (RelativeLayout) view.findViewById(R.id.maintab_dis);
        maintabDisIv = (ImageView) view.findViewById(R.id.maintab_dis_iv);
        maintabDisTv = (TextView) view.findViewById(R.id.maintab_dis_tv);
        maintabDisReddot = (ImageView) view.findViewById(R.id.maintab_dis_reddot);

        maintabCircle = (RelativeLayout) view.findViewById(R.id.maintab_circle);
        maintabCircleIv = (ImageView) view.findViewById(R.id.maintab_circle_iv);
        maintabCircleTv = (TextView) view.findViewById(R.id.maintab_circle_tv);
        maintabCircleReddot = (ImageView) view.findViewById(R.id.maintab_circle_reddot);

        maintabMsg = (RelativeLayout) view.findViewById(R.id.maintab_msg);
        maintabMsgIv = (ImageView) view.findViewById(R.id.maintab_msg_iv);
        maintabMsgTv = (TextView) view.findViewById(R.id.maintab_msg_tv);
        maintabMsgReddot = (ImageView) view.findViewById(R.id.maintab_msg_reddot);

        maintabMine = (RelativeLayout) view.findViewById(R.id.maintab_mine);
        maintabMineIv = (ImageView) view.findViewById(R.id.maintab_mine_iv);
        maintabMineTv = (TextView) view.findViewById(R.id.maintab_mine_tv);
        maintabMineReddot = (ImageView) view.findViewById(R.id.maintab_mine_reddot);

        maintabAdd.setOnClickListener(this);

        maintabDis.setOnClickListener(this);
        maintabCircle.setOnClickListener(this);
        maintabMsg.setOnClickListener(this);
        maintabMine.setOnClickListener(this);
    }



    public void init() {
        reset();
        maintabDis.setSelected(true);
    }

    private void reset() {
        maintabDis.setSelected(false);
        maintabCircle.setSelected(false);
        maintabMsg.setSelected(false);
        maintabMine.setSelected(false);
    }

    public void setSelectMine() {
        reset();
        maintabMine.setSelected(true);
        if (null != listener) {
            listener.onClickFour();
        }
    }

    public void setSelectMsg() {
        reset();
        maintabMsg.setSelected(true);
        if (null != listener) {
            listener.onClickThree();
        }
    }

    public void setSelectCircle() {
        reset();
        maintabCircle.setSelected(true);
        if (null != listener) {
            listener.onClickTwo();
        }
    }

    public void setSelectDis() {
        reset();
        maintabDis.setSelected(true);
        if (null != listener) {
            listener.onClickOne();
        }
    }

    public void updateDisRedDot(int count) {
        if (count > 0) {
            maintabDisReddot.setVisibility(View.VISIBLE);
        } else {
            maintabDisReddot.setVisibility(View.GONE);
        }
    }

    public void updateCircleRedDot(int count) {
        if (count > 0) {
            maintabCircleReddot.setVisibility(View.VISIBLE);
        } else {
            maintabCircleReddot.setVisibility(View.GONE);
        }
    }

    public void updateMsgRedDot(int count) {
        if (count > 0) {
            maintabMsgReddot.setVisibility(View.VISIBLE);
        } else {
            maintabMsgReddot.setVisibility(View.GONE);
        }
    }

    public void updateMineRedDot(int count) {
        if (count > 0) {
            maintabMineReddot.setVisibility(View.VISIBLE);
        } else {
            maintabMineReddot.setVisibility(View.GONE);
        }
    }

    public void setOnSingleClickListener(OnSingleClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.maintab_add) {
            if (null != listener) {
                listener.onClickAdd();
            }
        } else if (i == R.id.maintab_dis) {
            reset();
            maintabDis.setSelected(true);
            if (null != listener) {
                listener.onClickOne();
            }
        } else if (i == R.id.maintab_circle) {
            reset();
            maintabCircle.setSelected(true);
            if (null != listener) {
                listener.onClickTwo();
            }
        } else if (i == R.id.maintab_msg) {
            reset();
            maintabMsg.setSelected(true);
            if (null != listener) {
                listener.onClickThree();
            }
        } else if (i == R.id.maintab_mine) {
            reset();
            maintabMine.setSelected(true);
            if (null != listener) {
                listener.onClickFour();
            }
        }
    }

    public interface OnSingleClickListener {
        void onClickAdd();

        void onClickOne();

        void onClickTwo();

        void onClickThree();

        void onClickFour();
    }
}

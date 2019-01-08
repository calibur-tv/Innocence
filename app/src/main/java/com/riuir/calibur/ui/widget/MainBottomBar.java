package com.riuir.calibur.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.home.image.CreateNewImageActivity;

import calibur.core.manager.UserSystem;


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
    private PopupWindow addPopupWindow;
    private ImageView addPost;
    private ImageView addImage;
    private ImageView addScore;
    private ImageView closePopup;

    private RelativeLayout maintabCircle;
    private ImageView maintabCircleIv;
    private TextView maintabCircleTv;
    private ImageView maintabCircleReddot;
    private RelativeLayout maintabMsg;
    private ImageView maintabMsgIv;
    private TextView maintabMsgTv;
    private TextView maintabMsgReddot;
    private RelativeLayout maintabMine;
    private ImageView maintabMineIv;
    private TextView maintabMineTv;
    private ImageView maintabMineReddot;
    private OnSingleClickListener listener;

    Context context;
    Activity activity;

    public MainBottomBar(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public MainBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public MainBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        activity = (Activity) context;
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
        maintabMsgReddot = (TextView) view.findViewById(R.id.maintab_msg_reddot);

        maintabMine = (RelativeLayout) view.findViewById(R.id.maintab_mine);
        maintabMineIv = (ImageView) view.findViewById(R.id.maintab_mine_iv);
        maintabMineTv = (TextView) view.findViewById(R.id.maintab_mine_tv);
        maintabMineReddot = (ImageView) view.findViewById(R.id.maintab_mine_reddot);

        setBottomAdd();
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
            if (count>99){
                maintabMsgReddot.setText("99");
            }else {
                maintabMsgReddot.setText(count+"");
            }
        } else {
            maintabMsgReddot.setText("0");
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
//                listener.onClickAdd();
                setAddListener();
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


    private void setBottomAdd() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.main_add_popup_window_layout,null,false);
        addPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,true);
        addPost = view.findViewById(R.id.main_add_popup_window_add_post);
        addImage = view.findViewById(R.id.main_add_popup_window_add_image);
        addScore = view.findViewById(R.id.main_add_popup_window_add_score);
        closePopup = view.findViewById(R.id.main_add_popup_window_close);

        // 设置PopupWindow的背景
        addPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_popup_window_alpha_white));
        // 设置PopupWindow是否能响应外部点击事件
        addPopupWindow.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件,具体是其中的item的响应事件
        addPopupWindow.setTouchable(true);

        addPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                maintabAdd.setRotation(135);
                PropertyValuesHolder pvhRClose = PropertyValuesHolder.ofFloat(View.ROTATION, 135,0);
                ObjectAnimator animationClose = ObjectAnimator.ofPropertyValuesHolder(maintabAdd, pvhRClose);
                animationClose.start();
            }
        });

        setPopupClickListener();

    }

    private void setAddListener() {

        addPopupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.TOP,
                0,0);
//        closePopup.setRotation(0);
//        maintabAdd.setRotation(0);
        PropertyValuesHolder pvhROpen = PropertyValuesHolder.ofFloat(View.ROTATION, 0,135);
//        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(maintabAdd, pvhROpen);
//        animation.start();
        ObjectAnimator animationOpen = ObjectAnimator.ofPropertyValuesHolder(closePopup, pvhROpen);
        animationOpen.start();

    }

    private void setPopupClickListener() {
        addPost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserSystem.getInstance().isLogin()){
                    addPopupWindow.dismiss();
                    Intent intent = new Intent(context, CardCreateNewActivity.class);
                    context.startActivity(intent);
                }else {
                    ToastUtils.showShort(context,"登录状态才能发帖哦");
                }
            }
        });
        addImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserSystem.getInstance().isLogin()){
                    addPopupWindow.dismiss();
                    Intent intent = new Intent(context, CreateNewImageActivity.class);
                    context.startActivity(intent);
                }else {
                    ToastUtils.showShort(context,"登录状态才能发图哦");
                }
            }
        });
        addScore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort(context,"开发中，敬请期待3");
                addPopupWindow.dismiss();
            }
        });
        closePopup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addPopupWindow.dismiss();
            }
        });
    }

    public interface OnSingleClickListener {

        void onClickOne();

        void onClickTwo();

        void onClickThree();

        void onClickFour();


    }
}

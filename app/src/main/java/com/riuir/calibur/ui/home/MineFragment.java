package com.riuir.calibur.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.home.marked.BookMarksActivity;
import com.riuir.calibur.ui.home.mine.ClearCacheActivity;
import com.riuir.calibur.ui.home.mine.MineInfoSettingActivity;
import com.riuir.calibur.ui.home.report.FeedbackActivity;
import com.riuir.calibur.ui.home.user.UserBulletinActivity;
import com.riuir.calibur.ui.home.user.UserMainActivity;
import com.riuir.calibur.ui.home.user.UserTransactionsActivity;
import com.riuir.calibur.ui.web.WebViewActivity;
import com.riuir.calibur.utils.ActivityUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.user.MineUserInfo;
import calibur.core.http.models.user.UserDaySign;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 我的fragment
 */
public class MineFragment extends BaseFragment {

    @BindView(R.id.mine_fragment_mine_banner)
    ImageView userBanner;
    @BindView(R.id.mine_fragment_mine_icon)
    RoundedImageView userIcon;
    @BindView(R.id.mine_fragment_mine_name)
    TextView userName;


    @BindView(R.id.mine_fragment_day_sign_in_btn)
    TextView daySignBtn;
    @BindView(R.id.mine_fragment_mine_user_log_off)
    TextView logOffBtn;
    @BindView(R.id.mine_fragment_mine_user_reset)
    Button reSetBtn;
    @BindView(R.id.mine_fragment_mine_home_layout)
    RelativeLayout mineMainPage;
    @BindView(R.id.mine_fragment_mine_invite_layout)
    RelativeLayout mineInviteLayout;
    @BindView(R.id.mine_fragment_mine_book_marks_layout)
    RelativeLayout mineBookMarksLayout;
    @BindView(R.id.mine_fragment_mine_withdrawals_layout)
    RelativeLayout mineWithdrawalsLayout;
    @BindView(R.id.mine_fragment_clear_cache_layout)
    RelativeLayout mineClearCacheLayout;
    @BindView(R.id.mine_fragment_handbook_layout)
    RelativeLayout mineHandbookLayout;
    @BindView(R.id.mine_fragment_feedback_layout)
    RelativeLayout mineFeedbackLayout;
    @BindView(R.id.mine_fragment_tips_layout)
    RelativeLayout tipsLayout;
    @BindView(R.id.mine_fragment_mine_refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.mine_fragment_mine_level_number)
    TextView level;
    @BindView(R.id.mine_fragment_mine_level_text)
    TextView levelText;
    @BindView(R.id.mine_fragment_mine_fight)
    TextView fightNumber;
    @BindView(R.id.mine_fragment_coin_number)
    TextView coinNumber;
    @BindView(R.id.mine_fragment_id_number)
    TextView idNumber;

    @BindView(R.id.mine_fragment_mine_level_progress)
    NumberProgressBar levelProgress;
    @BindView(R.id.mine_fragment_mine_level_question_btn)
    ImageView questionBtn;

    AlertDialog levelDialog;

    MineUserInfo userInfoData;

    private CoinChangeBroadCastReceiver coinReceiver;
    private ExpChangeBroadCastReceiver expReceiver;
    private IntentFilter coinIntentFilter;
    private IntentFilter expIntentFilter;
    public static final String COINCHANGE = "user_coin_changed";
    public static final String EXPCHANGE = "user_exp_changed";

    public static final int NET_GET_USER_INFO = 0;
    public static final int NET_LOG_OFF = 1;
    public static final int NET_DAY_SIGN = 2;


    public static MineFragment newInstance() {
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
        reSetBtn.setVisibility(View.GONE);
        registerReceiver();
        if (UserSystem.getInstance().isLogin()){
            if (Constants.userInfoData == null){
                Constants.userInfoData = SharedPreferencesUtils.getUserInfoData(App.instance());
                if (Constants.userInfoData == null){
                    setNet(NET_GET_USER_INFO);
                }
            }else {
                setUserInfoView();
            }

        }
    }

    private void registerReceiver() {
        coinReceiver = new CoinChangeBroadCastReceiver();
        coinIntentFilter = new IntentFilter();
        coinIntentFilter.addAction(COINCHANGE);
        getContext().registerReceiver(coinReceiver,coinIntentFilter);

        expReceiver = new ExpChangeBroadCastReceiver();
        expIntentFilter = new IntentFilter();
        expIntentFilter.addAction(EXPCHANGE);
        getContext().registerReceiver(expReceiver,expIntentFilter);
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(coinReceiver);
        getContext().unregisterReceiver(expReceiver);
        super.onDestroy();
    }

    private void setNet(int NET_STATUS) {
        if (NET_STATUS == NET_GET_USER_INFO){
            apiService.getMineUserInfo()
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<MineUserInfo>(){
                        @Override
                        public void onSuccess(MineUserInfo mineUserInfo) {
                            Constants.userInfoData = mineUserInfo;
                            SharedPreferencesUtils.putUserInfoData(App.instance(),Constants.userInfoData);
                            userInfoData = mineUserInfo;
                            setUserInfoView();
                            if (refreshLayout!=null){
                                refreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            setReSet();
                            if (refreshLayout!=null){
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    });
        }
        if (NET_STATUS == NET_LOG_OFF){
            apiService.getMineUserLogOut()
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<String>(){
                        @Override
                        public void onSuccess(String s) {
                            LoginUtils.CancelLogin(App.instance(),getActivity());
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            LoginUtils.CancelLogin(App.instance(),getActivity());
                        }
                    });
        }
        if (NET_STATUS == NET_DAY_SIGN){
            apiService.getCallUserDaySign()
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<UserDaySign>(){
                        @Override
                        public void onSuccess(UserDaySign userDaySign) {
                            daySignBtn.setText("已签到");
                            coinNumber.setText(""+(Constants.userInfoData.getCoin()+1));
                            Constants.userInfoData.setCoin(Constants.userInfoData.getCoin()+1);
                            Constants.userInfoData.setDaySign(true);
                            //签到成功经验+2
                            setUserExpChanged(userDaySign.getExp());
                            ToastUtils.showShort(getContext(),userDaySign.getMessage());
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (daySignBtn!=null){
                                daySignBtn.setText("签到");
                                daySignBtn.setClickable(true);
                            }
                        }
                    });
        }
    }

    private void setUserInfoView() {
        if (userInfoData == null&&Constants.userInfoData!=null){
            userInfoData = Constants.userInfoData;
        }
        GlideUtils.loadImageViewCircle(getContext(),
                GlideUtils.setImageUrlForWidth(getContext(),userInfoData.getAvatar(),userIcon.getLayoutParams().width),
                userIcon);

        GlideUtils.loadImageViewBlur(getContext(),
                GlideUtils.setImageUrl(getContext(),userInfoData.getBanner(),GlideUtils.FULL_SCREEN),
                userBanner);
        userName.setText(userInfoData.getNickname());
        coinNumber.setText(""+userInfoData.getCoin());
        idNumber.setText("邀请码："+userInfoData.getId());
        if (userInfoData.isDaySign()){
            daySignBtn.setText("已签到");
        }else {
            daySignBtn.setText("签到");
        }
        level.setText(""+userInfoData.getExp().getLevel());
        fightNumber.setText(""+userInfoData.getPower());
        levelText.setText("Lv "+userInfoData.getExp().getLevel());

        levelProgress.setMax(userInfoData.getExp().getNext_level_exp());
        levelProgress.setProgress(userInfoData.getExp().getHave_exp());

        setListener();
    }

    private void setReSet(){
        if (Constants.userInfoData == null&&userInfoData == null){
            reSetBtn.setVisibility(View.VISIBLE);
            reSetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reSetBtn.setVisibility(View.GONE);
                    if (UserSystem.getInstance().isLogin()){
                        setNet(NET_GET_USER_INFO);
                    }else {
                        ToastUtils.showShort(getContext(),"登录状态过时，重新登录");
                        LoginUtils.CancelLogin(App.instance(),getActivity());
                    }
                }
            });
        }
    }

    private void setListener() {

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setNet(NET_GET_USER_INFO);
            }
        });

        mineMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserMainActivity.class);
                intent.putExtra("userId",userInfoData.getId());
                intent.putExtra("zone",userInfoData.getZone());
                startActivity(intent);
            }
        });

        tipsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("type",WebViewActivity.TYPE_RULE);
                intent.putExtra("index",1);
                startActivity(intent);
            }
        });

        mineInviteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("type",WebViewActivity.TYPE_INVITE);
                startActivity(intent);
            }
        });

        mineBookMarksLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BookMarksActivity.class);
                startActivity(intent);
            }
        });
        mineWithdrawalsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserTransactionsActivity.class);
                startActivity(intent);
            }
        });

        mineClearCacheLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ClearCacheActivity.class);
                startActivity(intent);
            }
        });
        mineHandbookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("type",WebViewActivity.TYPE_RULE);
                intent.putExtra("index",0);
                startActivity(intent);
            }
        });
        mineFeedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FeedbackActivity.class);
                startActivity(intent);
            }
        });

        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MineInfoSettingActivity.class);
                intent.putExtra("userData",userInfoData);
                startActivityForResult(intent,MineInfoSettingActivity.SETTING_CODE);
            }
        });


        daySignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daySignBtn.setClickable(false);
                daySignBtn.setText("签到中");
                setNet(NET_DAY_SIGN);
            }
        });
        if (userInfoData.isDaySign()){
            daySignBtn.setClickable(false);
        }else {
            daySignBtn.setClickable(true);
        }
        logOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserSystem.getInstance().isLogin()){
                    setNet(NET_LOG_OFF);
                }
            }
        });

        questionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setDialog();
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("type",WebViewActivity.TYPE_RULE);
                intent.putExtra("index",2);
                startActivity(intent);
            }
        });
        levelProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog();
            }
        });

    }


    private void setDialog() {
        View view = getLayoutInflater().inflate(R.layout.level_rule_diaglog_layout,null);
        TextView levelNum = view.findViewById(R.id.level_rile_dialog_level_number);
        TextView lvExpBum = view.findViewById(R.id.level_rile_dialog_level_exp_number);
        if (userInfoData!=null){
            levelNum.setText(userInfoData.getExp().getLevel()+"");
            lvExpBum.setText(userInfoData.getExp().getHave_exp()+"/"+userInfoData.getExp().getNext_level_exp());
        }
        levelDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();
        levelDialog.setCanceledOnTouchOutside(true);
        levelDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MineInfoSettingActivity.SETTING_CODE){
            //数据返回 进行刷新
            setNet(NET_GET_USER_INFO);
        }
    }

    class CoinChangeBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            coinNumber.setText(""+(Constants.userInfoData.getCoin()));
        }
    }

    class ExpChangeBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int expNum = intent.getIntExtra("expChangeNum",0);
            Log.d("expChangeNum","expChangeNum = "+expNum);
            if (expNum!=0){
                setUserExpChanged(expNum);
            }
        }
    }

    private void setUserExpChanged(int expNum){
        if (Constants.userInfoData!=null){
            //每个等级需要的总经验 = 等级x等级+等级x10
            MineUserInfo.MineUserInfoExp infoExp = Constants.userInfoData.getExp();
            if (expNum>0){
                //大于0的时候，用户新建了内容，增加经验
                if (infoExp.getHave_exp()+expNum>=infoExp.getNext_level_exp()){
                    infoExp.setHave_exp((infoExp.getHave_exp()+expNum)-infoExp.getNext_level_exp());
                    infoExp.setLevel(infoExp.getLevel()+1);
                    infoExp.setNext_level_exp((infoExp.getLevel()*infoExp.getLevel())+
                            (infoExp.getLevel()*10));
                }else {
                    infoExp.setHave_exp(infoExp.getHave_exp()+expNum);
                }
            }else {
                //小于0的时候，用户新建了内容，减少经验
                expNum = expNum*-1;
                if (infoExp.getHave_exp()>=expNum){
                    infoExp.setHave_exp(infoExp.getHave_exp()-expNum);
                }else {
                    infoExp.setLevel(infoExp.getLevel()-1);
                    infoExp.setNext_level_exp((infoExp.getLevel()*infoExp.getLevel())+
                            (infoExp.getLevel()*10));
                    infoExp.setHave_exp(infoExp.getNext_level_exp()-(infoExp.getHave_exp()-expNum));
                }
            }

            Constants.userInfoData.setExp(infoExp);
            setUserInfoView();
        }else {
            if (UserSystem.getInstance().isLogin()){
                setNet(NET_LOG_OFF);
            }
        }
    }
}

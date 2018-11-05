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
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MineUserInfo;
import com.riuir.calibur.data.user.UserDaySign;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.home.mine.ClearCacheActivity;
import com.riuir.calibur.ui.home.mine.MineInfoSettingActivity;
import com.riuir.calibur.ui.home.report.FeedbackActivity;
import com.riuir.calibur.ui.home.user.UserMainActivity;
import com.riuir.calibur.ui.web.WebViewActivity;
import com.riuir.calibur.utils.ActivityUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.BindView;
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
    @BindView(R.id.mine_fragment_coin_number)
    TextView coinNumber;
    @BindView(R.id.mine_fragment_id_number)
    TextView idNumber;
    @BindView(R.id.mine_fragment_day_sign_in_btn)
    TextView daySignBtn;
    @BindView(R.id.mine_fragment_mine_user_log_off)
    TextView logOffBtn;
    @BindView(R.id.mine_fragment_mine_user_reset)
    Button reSetBtn;
    @BindView(R.id.mine_fragment_mine_home_layout)
    RelativeLayout mineMainPage;
    @BindView(R.id.mine_fragment_mine_draft_layout)
    RelativeLayout mineDraftLayout;
    @BindView(R.id.mine_fragment_mine_invite_layout)
    RelativeLayout mineInviteLayout;
    @BindView(R.id.mine_fragment_clear_cache_layout)
    RelativeLayout mineClearCacheLayout;
    @BindView(R.id.mine_fragment_feedback_layout)
    RelativeLayout mineFeedbackLayout;
    @BindView(R.id.mine_fragment_tips_layout)
    RelativeLayout tipsLayout;

    @BindView(R.id.mine_fragment_mine_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.mine_fragment_mine_level)
    TextView level;
    @BindView(R.id.mine_fragment_mine_level_progress)
    NumberProgressBar levelProgress;
    @BindView(R.id.mine_fragment_mine_level_question_btn)
    ImageView questionBtn;

    AlertDialog levelDialog;

    MineUserInfo.MinEUserInfoData userInfoData;

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
//        int stautsBarHeight = ActivityUtils.getStatusBarHeight(getContext());
//        rootView.setPadding(0,stautsBarHeight,0,0);
        reSetBtn.setVisibility(View.GONE);
        registerReceiver();
        if (Constants.ISLOGIN){
            if (Constants.userInfoData == null){
                setNet(NET_GET_USER_INFO);
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
            apiPost.getMineUserInfo().enqueue(new Callback<MineUserInfo>() {
                @Override
                public void onResponse(Call<MineUserInfo> call, Response<MineUserInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        Constants.userInfoData = response.body().getData();
                        userInfoData = response.body().getData();
                        setUserInfoView();
                    }else  if (!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);
                        ToastUtils.showShort(getContext(),info.getMessage());
                        setReSet();
                    }else {
                        ToastUtils.showShort(getContext(),"网络异常,请检查您的网络");
                        setReSet();
                    }
                    if (refreshLayout!=null){
                        refreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<MineUserInfo> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"网络异常,请检查您的网络");
                    CrashReport.postCatchedException(t);
                    setReSet();
                    if (refreshLayout!=null){
                        refreshLayout.setRefreshing(false);
                    }
                }
            });
        }
        if (NET_STATUS == NET_LOG_OFF){
            apiPost.getMineUserLogOut().enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    if (response!=null&&response.isSuccessful()){
                    }else  if (!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);
                        LogUtils.d("umLogin","info = "+info.getMessage());
                    }else {
                    }
                    LoginUtils.CancelLogin(App.instance(),getActivity());
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    CrashReport.postCatchedException(t);
                    LoginUtils.CancelLogin(App.instance(),getActivity());
                }
            });
        }
        if (NET_STATUS == NET_DAY_SIGN){
            apiPost.getCallUserDaySign().enqueue(new Callback<UserDaySign>() {
                @Override
                public void onResponse(Call<UserDaySign> call, Response<UserDaySign> response) {
                    if (response!=null&&response.isSuccessful()){
                        daySignBtn.setText("已签到");
                        coinNumber.setText("团子："+(Constants.userInfoData.getCoin()+1));
                        Constants.userInfoData.setCoin(Constants.userInfoData.getCoin()+1);
                        Constants.userInfoData.setDaySign(true);
                        //签到成功经验+2
                        setUserExpChanged(response.body().getData().getExp());
                        ToastUtils.showShort(getContext(),response.body().getData().getMessage());

                    }else  if (!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);
                        ToastUtils.showShort(getContext(),info.getMessage());

                        daySignBtn.setText("签到");
                        daySignBtn.setClickable(true);

                    }else {
                        ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                        daySignBtn.setText("签到");
                        daySignBtn.setClickable(true);
                    }
                }

                @Override
                public void onFailure(Call<UserDaySign> call, Throwable t) {
//                    call.isCanceled();
//                    call.isExecuted();
                    CrashReport.postCatchedException(t);
                    ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                    daySignBtn.setText("签到");
                    daySignBtn.setClickable(true);
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
        coinNumber.setText("团子："+userInfoData.getCoin());
        idNumber.setText("邀请码："+userInfoData.getId());
        if (userInfoData.isDaySign()){
            daySignBtn.setText("已签到");
        }else {
            daySignBtn.setText("签到");
        }
        level.setText("Lv"+userInfoData.getExp().getLevel()+" · 战斗力："+userInfoData.getPower());

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
                    if (Constants.ISLOGIN){
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

        mineDraftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort(getContext(),"功能研发中，敬请期待");
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

        mineClearCacheLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ClearCacheActivity.class);
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
                if (Constants.ISLOGIN){
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
            coinNumber.setText("团子："+(Constants.userInfoData.getCoin()));
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
            if (Constants.ISLOGIN){
                setNet(NET_LOG_OFF);
            }
        }
    }
}

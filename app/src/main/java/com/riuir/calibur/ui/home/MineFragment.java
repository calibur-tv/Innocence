package com.riuir.calibur.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MineUserInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.home.user.UserMainActivity;
import com.riuir.calibur.utils.ActivityUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.io.IOException;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @BindView(R.id.mine_fragment_mine_icon)
    ImageView userIcon;
    @BindView(R.id.mine_fragment_mine_name)
    TextView userName;
    @BindView(R.id.mine_fragment_coin_number)
    TextView coinNumber;
    @BindView(R.id.mine_fragment_day_sign_in_btn)
    TextView daySignBtn;
    @BindView(R.id.mine_fragment_mine_more_btn)
    TextView moreBtn;
    @BindView(R.id.mine_fragment_mine_user_log_off)
    Button logOffBtn;
    @BindView(R.id.mine_fragment_mine_home_layout)
    RelativeLayout mineMainPage;

    MineUserInfo.MinEUserInfoData userInfoData;


    public static final int NET_GET_USER_INFO = 0;
    public static final int NET_LOG_OFF = 1;
    public static final int NET_DAY_SIGN = 2;

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
        int stautsBarHeight = ActivityUtils.getStatusBarHeight(getContext());
        rootView.setPadding(0,stautsBarHeight,0,0);
        if (Constants.ISLOGIN){
            setNet(NET_GET_USER_INFO);
        }
    }

    private void setNet(int NET_STATUS) {
        if (NET_STATUS == NET_GET_USER_INFO){
            apiPost.getMineUserInfo().enqueue(new Callback<MineUserInfo>() {
                @Override
                public void onResponse(Call<MineUserInfo> call, Response<MineUserInfo> response) {
                    if (response!=null&&response.isSuccessful()){
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
                    }else {
                        ToastUtils.showShort(getContext(),"网络异常,请检查您的网络");
                    }
                }

                @Override
                public void onFailure(Call<MineUserInfo> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"网络异常,请检查您的网络");
                }
            });
        }
        if (NET_STATUS == NET_LOG_OFF){
            apiPost.getMineUserLogOut().enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    if (response!=null&&response.isSuccessful()){
                        LoginUtils.CancelLogin(getContext(),getActivity());
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

                    }else {
                        ToastUtils.showShort(getContext(),"网络异常，退出登录失败");
                    }
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"网络异常，退出登录失败");
                }
            });
        }
        if (NET_STATUS == NET_DAY_SIGN){
            apiPost.getCallUserDaySign().enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    if (response!=null&&response.isSuccessful()){
                        daySignBtn.setText("已签到");
                        coinNumber.setText("金币："+(userInfoData.getCoin()+1));
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
                        if (info.getCode() == 40104){
//                            LoginUtils.ReLogin(getContext());
                        }else if (info.getCode() == 40301){
                            daySignBtn.setText("已签到");
                        }

                    }else {
                        ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                        daySignBtn.setText("点击签到");
                        daySignBtn.setClickable(true);
                    }
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                    daySignBtn.setText("点击签到");
                    daySignBtn.setClickable(true);
                }
            });
        }

    }

    private void setUserInfoView() {
        GlideUtils.loadImageViewCircle(getContext(),userInfoData.getAvatar(),userIcon);
        userName.setText(userInfoData.getNickname());
        coinNumber.setText("金币："+userInfoData.getCoin());
        if (userInfoData.isDaySign()){
            daySignBtn.setText("已签到");
        }else {
            daySignBtn.setText("点击签到");
        }

        setListener();
    }

    private void setListener() {
        mineMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserMainActivity.class);
                intent.putExtra("userId",userInfoData.getId());
                intent.putExtra("zone",userInfoData.getZone());
                startActivity(intent);
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
    }

}

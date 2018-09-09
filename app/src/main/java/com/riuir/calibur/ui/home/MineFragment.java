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
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MineUserInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.home.mine.MineInfoSettingActivity;
import com.riuir.calibur.ui.home.user.UserMainActivity;
import com.riuir.calibur.utils.ActivityUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import org.w3c.dom.Text;

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

    @BindView(R.id.mine_fragment_mine_banner)
    ImageView userBanner;
    @BindView(R.id.mine_fragment_mine_icon)
    RoundedImageView userIcon;
    @BindView(R.id.mine_fragment_mine_name)
    TextView userName;
    @BindView(R.id.mine_fragment_coin_number)
    TextView coinNumber;
    @BindView(R.id.mine_fragment_id_number)
    TextView idNumner;
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
    @BindView(R.id.mine_fragment_tips_layout)
    RelativeLayout tipsLayout;
    @BindView(R.id.mine_fragment_tips_close)
    TextView closeTips;

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
//        int stautsBarHeight = ActivityUtils.getStatusBarHeight(getContext());
//        rootView.setPadding(0,stautsBarHeight,0,0);
        reSetBtn.setVisibility(View.GONE);
        if (Constants.ISLOGIN){
            if (Constants.userInfoData == null){
                setNet(NET_GET_USER_INFO);
            }else {
                setUserInfoView();
            }

        }
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
                }

                @Override
                public void onFailure(Call<MineUserInfo> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"网络异常,请检查您的网络");
                    setReSet();
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
                    LoginUtils.CancelLogin(getContext(),getActivity());
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    LoginUtils.CancelLogin(getContext(),getActivity());
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

                        daySignBtn.setText("签到");
                        daySignBtn.setClickable(true);

                    }else {
                        ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                        daySignBtn.setText("签到");
                        daySignBtn.setClickable(true);
                    }
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
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
        GlideUtils.loadImageViewCircle(getContext(),userInfoData.getAvatar(),userIcon);
        GlideUtils.loadImageViewBlur(getContext(),
                GlideUtils.setImageUrl(getContext(),userInfoData.getBanner(),GlideUtils.FULL_SCREEN),
                userBanner);
        userName.setText(userInfoData.getNickname());
        coinNumber.setText("金币："+userInfoData.getCoin());
        idNumner.setText("邀请码："+userInfoData.getId());
        if (userInfoData.isDaySign()){
            daySignBtn.setText("已签到");
        }else {
            daySignBtn.setText("签到");
        }

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
                        LoginUtils.CancelLogin(getContext(),getActivity());
                    }
                }
            });
        }
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

        closeTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipsLayout.setVisibility(View.GONE);
            }
        });


        mineDraftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort(getContext(),"功能研发中，敬请期待");
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MineInfoSettingActivity.SETTING_CODE){
            //数据返回 进行刷新
            setNet(NET_GET_USER_INFO);
        }
    }
}

package com.riuir.calibur.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MineUserInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

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
    @BindView(R.id.mine_fragment_sign_in_btn)
    TextView sinInBtn;
    @BindView(R.id.mine_fragment_mine_more_btn)
    TextView moreBtn;
    @BindView(R.id.mine_fragment_mine_user_log_off)
    Button logOffBtn;

    MineUserInfo.MinEUserInfoData userInfoData;


    public static final int NET_GET_USER_INFO = 0;
    public static final int NET_LOG_OFF = 1;

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
        if (Constants.ISLOGIN){
            setNet(NET_GET_USER_INFO);
            logOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setNet(NET_LOG_OFF);
                }
            });
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
                    }
                }

                @Override
                public void onFailure(Call<MineUserInfo> call, Throwable t) {
                }
            });
        }
        if (NET_STATUS == NET_LOG_OFF){
            apiPost.getMineUserLogOut().enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    if (response!=null&&response.isSuccessful()){
                        LoginUtils.CancelLogin(getContext(),getActivity());
                    }
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {

                }
            });
        }
    }

    private void setUserInfoView() {
        GlideUtils.loadImageViewCircle(getContext(),userInfoData.getAvatar(),userIcon);
        userName.setText(userInfoData.getNickname());
        coinNumber.setText("金币："+userInfoData.getCoin());
    }

}

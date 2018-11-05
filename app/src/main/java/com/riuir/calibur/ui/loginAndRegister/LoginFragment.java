package com.riuir.calibur.ui.loginAndRegister;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.BangumiAllListUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.params.VerificationCodeBody;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.geetest.GeetestUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment {

    private static final int NET_GEE_STATUS_login = 1;

    @BindView(R.id.login_fragment_username_edit)
    EditText userNameEdit;
    @BindView(R.id.login_fragment_password_edit)
    EditText passWordEdit;
    @BindView(R.id.login_fragment_password_visibility)
    ImageView passwordVisibilityBtn;

    boolean passwordIsVisibility = false;

    String userNameStr;
    String passWordStr;

    @BindView(R.id.login_fragment_login_btn)
    Button loginBtn;
    @BindView(R.id.login_fragment_start_activity_forget_password_btn)
    TextView startActivityForgetPassWordBtn;

    GT3GeetestUtilsBind gt3GeetestUtilsBindLogin;
    GT3GeetestBindListener bindListenerLogin;
    GeetestUtils geetestUtils;



//    private LoginBroadCastReceiver loginBroadCastReceiver;
//    private IntentFilter intentFilter;


    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_login;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();

        gt3GeetestUtilsBindLogin = new GT3GeetestUtilsBind(getContext());

//        registerReceiver();
        initOnClickListener();
    }

//    private void registerReceiver() {
//        //动态接受网络变化的广播接收器
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(GeetestUtils.FailAction);
//        loginBroadCastReceiver = new LoginBroadCastReceiver();
//        getActivity().registerReceiver(loginBroadCastReceiver,intentFilter);
//
//    }

    private void setGeeTestUtils() {
        geetestUtils = new GeetestUtils();
        geetestUtils.setGeetestStart(getContext(),apiGet,
                gt3GeetestUtilsBindLogin);
        geetestUtils.setOnGeetestFailedListener(new GeetestUtils.OnGeetestFailedListener() {
            @Override
            public void onFailed(String failedMessage) {
                if (loginBtn!=null){
                    loginBtn.setClickable(true);
                    loginBtn.setText("登录");
                }
            }
        });
        geetestUtils.setOnGeetestSuccessListener(new GeetestUtils.OnGeetestSuccessListener() {
            @Override
            public void onSuccess(VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGee) {
                verificationCodeBodyGeeTest = verificationCodeBodyGee;
                gt3GeetestUtilsBindLogin.gt3TestFinish();
                setNet(NET_GEE_STATUS_login);
            }
        });
    }

    private void initOnClickListener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameStr = userNameEdit.getText().toString();
                passWordStr = passWordEdit.getText().toString();
                if (userNameStr==null||userNameStr.length() != 11){
                    ToastUtils.showShort(getContext(),"请输入正确的11位手机号");
                }else if (passWordStr == null||passWordStr.length()<6) {
                    ToastUtils.showShort(getContext(),"密码长度最少6位");
                }else {
                    //TODO
                    loginBtn.setClickable(false);
                    loginBtn.setText("登录中...");
//                    setNetWithOutGee();
//                    setNet(NET_GEE_STATUS_captcha);
                    setGeeTestUtils();
                }
            }
        });
        startActivityForgetPassWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ForgetPassWordActivity.class);
                startActivity(intent);
            }
        });
        passwordVisibilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordIsVisibility){
                    passwordIsVisibility = false;
                    passWordEdit.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());  //以密文显示，以.代替
                }else {
                    passwordIsVisibility = true;
                    passWordEdit.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());  //密码以明文显示
                }
            }
        });

    }

    private void setNet(int NET_STATUS){

        if (NET_STATUS == NET_GEE_STATUS_login){

            //自定义API2 登录
            Map<String,Object> params = new HashMap<>();
            params.put("access",userNameStr);
            params.put("secret",passWordStr);
            params.put("geetest",verificationCodeBodyGeeTest);

            LogUtils.d("loginActivity","geetest = "+verificationCodeBodyGeeTest.toString());
            apiPostNoAuth.getCallLogin(params).enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    if (response!=null&&response.isSuccessful()){

                        int code = response.body().getCode();
                        if (code == 0){
                            // 登录成功
                            ToastUtils.showShort(getContext(),"登录成功！✿✿ヽ(°▽°)ノ✿");
                            //返回JWT-Token(userToken) 存储下来 作为判断用户是否登录的凭证
                            SharedPreferencesUtils.put(App.instance(),"Authorization",response.body().getData());
                            Constants.ISLOGIN = true;
                            Constants.AUTH_TOKEN = response.body().getData();

                            BangumiAllListUtils.setBangumiAllList(getContext(),apiGet);
//                            startActivity(MainActivity.class);
//                            finish();
                        }
                    }else if (response!=null&&!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        loginBtn.setClickable(true);
                        loginBtn.setText("登录");
                        ToastUtils.showShort(getContext(),info.getMessage());
                    }else {
                        loginBtn.setClickable(true);
                        loginBtn.setText("登录");
                        ToastUtils.showShort(getContext(),"不明原因导致登录失败QAQ");
                    }
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"请检查您的网络哟~");
                    LogUtils.v("AppNetErrorMessage","login t = "+t.getMessage());
                    CrashReport.postCatchedException(t);
                    loginBtn.setClickable(true);
                    loginBtn.setText("登录");
                }
            });
        }

    }


    @Override
    public void onDestroy() {
        /**
         * 页面关闭时释放资源
         */
        gt3GeetestUtilsBindLogin.cancelUtils();
        //取消动态网络变化广播接收器的注册
//        getActivity().unregisterReceiver(loginBroadCastReceiver);
        super.onDestroy();
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /**
         * 设置后，界面横竖屏不会关闭验证码，推荐设置
         */
        gt3GeetestUtilsBindLogin.changeDialogLayout();
    }


//    public class LoginBroadCastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            loginBtn.setClickable(true);
//            loginBtn.setText("登录");
//        }
//    }
}
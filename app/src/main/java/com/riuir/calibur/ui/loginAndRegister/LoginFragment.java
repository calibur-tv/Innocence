package com.riuir.calibur.ui.loginAndRegister;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.geetest.params.VerificationCodeBody;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.core.utils.ISharedPreferencesKeys;
import calibur.core.utils.SharedPreferencesUtil;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.BangumiAllListUtils;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.geetest.GeetestUtils;
import java.util.HashMap;
import java.util.Map;
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
            apiService.getCallLogin(params)
                    .compose(Rx2Schedulers.<Response<ResponseBean<String>>>applyObservableAsync())
                    .subscribe(new ObserverWrapper<String>() {
                        @Override
                        public void onSuccess(String s) {
                            // 登录成功
                            ToastUtils.showShort(getContext(),"登录成功！✿✿ヽ(°▽°)ノ✿");
                            //返回JWT-Token(userToken) 存储下来 作为判断用户是否登录的凭证
                            SharedPreferencesUtil.putString(ISharedPreferencesKeys.MOBILE_TOKEN, s);
                            UserSystem.getInstance().updateUserToken(s);
                            Constants.ISLOGIN = true;
                            Constants.AUTH_TOKEN = s;

                            BangumiAllListUtils.setBangumiAllList(getContext());
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (loginBtn!=null){
                                loginBtn.setClickable(true);
                                loginBtn.setText("登录");
                            }
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

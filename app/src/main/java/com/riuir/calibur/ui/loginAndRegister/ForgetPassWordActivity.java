package com.riuir.calibur.ui.loginAndRegister;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.GeeTestInfo;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.ui.home.image.CreateImageAlbumActivity;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.utils.geetest.GeetestUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.geetest.params.VerificationCodeBody;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@Route(path = RouteUtils.userForgetPswPath)
public class ForgetPassWordActivity extends BaseActivity {

    private static final int NET_GEE_STATUS_captcha = 0;
    private static final int NET_GEE_STATUS_sendMessage = 1;
    private static final int NET_reset_password = 2;

    @BindView(R.id.forget_username_edit)
    EditText userNameEdit;
    @BindView(R.id.forget_new_password_edit)
    EditText newPassWordEdit;
    @BindView(R.id.forget_verification_code_edit)
    EditText verificationCodeEdit;
    @BindView(R.id.forget_new_password_visibility)
    ImageView passwordVisibilityBtn;

    boolean passwordIsVisibility = false;

    String userNameStr;
    String newPassWordStr;
    String verificationCodeStr;

    @BindView(R.id.forget_reset_password_btn)
    Button reSetPassWordBtn;
    @BindView(R.id.forget_start_activity_login_btn)
    ImageView startActivityLoginBtn;
    @BindView(R.id.forget_send_verification_code_btn)
    TextView sendMessageBtn;

    GT3GeetestUtilsBind gt3GeetestUtilsBindForgetPassword;

    int reSetSendMessageBtnSecond = 0;

    private GeeTestInfo geeTestInfo;
    private GeeTestInfo.GeeTest geeTest;

    private VerificationCodeBody verificationCodebodyBody;
    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;
    GeetestUtils geetestUtils;

//    private IntentFilter intentFilter;
//    private ForgetPassWordBroadCastReceiver receiver;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_forget_pass_word;
    }

    @Override
    protected void onInit() {
        verificationCodebodyBody = new VerificationCodeBody();
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();

        gt3GeetestUtilsBindForgetPassword = new GT3GeetestUtilsBind(ForgetPassWordActivity.this);
//        registerReceiver();
        initOnClicklistener();
    }

//    private void registerReceiver() {
//        receiver = new ForgetPassWordBroadCastReceiver();
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(GeetestUtils.FailAction);
//        registerReceiver(receiver,intentFilter);
//    }

    private void setGeeTestUtils() {
        geetestUtils = new GeetestUtils();
        geetestUtils.setGeetestStart(ForgetPassWordActivity.this,apiGet,
                gt3GeetestUtilsBindForgetPassword);
        geetestUtils.setOnGeetestFailedListener(new GeetestUtils.OnGeetestFailedListener() {
            @Override
            public void onFailed(String failedMessage) {
                handler.sendEmptyMessage(1);
            }
        });
        geetestUtils.setOnGeetestSuccessListener(new GeetestUtils.OnGeetestSuccessListener() {
            @Override
            public void onSuccess(VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGee) {
                verificationCodeBodyGeeTest = verificationCodeBodyGee;
                gt3GeetestUtilsBindForgetPassword.gt3TestFinish();
                setNet(NET_GEE_STATUS_sendMessage);
            }
        });
    }

    private void initOnClicklistener() {
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameStr = userNameEdit.getText().toString();
                if (userNameStr==null||userNameStr.length() != 11){
                    ToastUtils.showShort(ForgetPassWordActivity.this,"请输入正确的11位手机号(账号)");
                }else {
                    sendMessageBtn.setText("发送中..");
                    sendMessageBtn.setClickable(false);
//                    setNet(NET_GEE_STATUS_captcha);
//                    sendVerWithOutGee();
                    setGeeTestUtils();
                }
            }
        });
        startActivityLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reSetPassWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameStr = userNameEdit.getText().toString();
                newPassWordStr = newPassWordEdit.getText().toString();
                verificationCodeStr = verificationCodeEdit.getText().toString();
                if (userNameStr==null||userNameStr.length() != 11){
                    ToastUtils.showShort(ForgetPassWordActivity.this,"请输入正确的11位手机号(账号)");
                }else if (newPassWordStr==null||newPassWordStr.length() < 6){
                    ToastUtils.showShort(ForgetPassWordActivity.this,"密码长度最少6位");
                }else if (verificationCodeStr==null||verificationCodeStr.length() == 0){
                    ToastUtils.showShort(ForgetPassWordActivity.this,"请输入正确的验证码");
                }else {
                    reSetPassWordBtn.setText("重置密码中");
                    reSetPassWordBtn.setClickable(false);
                    setNet(NET_reset_password);
                }
            }
        });

        passwordVisibilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordIsVisibility){
                    passwordIsVisibility = false;
                    newPassWordEdit.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());  //以密文显示，以.代替
                }else {
                    passwordIsVisibility = true;
                    newPassWordEdit.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());  //密码以明文显示
                }
            }
        });
    }


    private void setNet(int NET_STATUS){

        if (NET_STATUS == NET_GEE_STATUS_sendMessage){

            //自定义API2 登录
            verificationCodebodyBody.setType("forgot_password");
            verificationCodebodyBody.setPhone_number(userNameStr);
            verificationCodebodyBody.setGeetest(verificationCodeBodyGeeTest);

            apiService.getGeeTestSendValidate(verificationCodebodyBody)
                    .compose(Rx2Schedulers.<Response<ResponseBean<String>>>applyObservableAsync())
                    .subscribe(new ObserverWrapper<String>(){

                        @Override
                        public void onSuccess(String s) {
                            ToastUtils.showShort(ForgetPassWordActivity.this,"重置密码短信发送成功！✿✿ヽ(°▽°)ノ✿");
                            reSetSendMessageBtnSecond = 60;
                            handler.sendEmptyMessage(0);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            handler.sendEmptyMessage(1);
                        }
                    });
        }

        //点击重置密码
        if (NET_STATUS == NET_reset_password){
            Map<String,Object> parmas = new HashMap<>();
            parmas.put("access",userNameStr);
            parmas.put("secret",newPassWordStr);
            parmas.put("authCode",verificationCodeStr);

            apiService.getCallReSetPassWord(parmas)
                    .compose(Rx2Schedulers.<Response<ResponseBean<String>>>applyObservableAsync())
                    .subscribe(new ObserverWrapper<String>() {
                        @Override
                        public void onSuccess(String s) {
                            ToastUtils.showLong(ForgetPassWordActivity.this,"重置密码成功(＾Ｕ＾)ノ~");
                            finish();
                            reSetPassWordBtn.setText("重置密码");
                            reSetPassWordBtn.setClickable(true);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            reSetPassWordBtn.setText("重置密码");
                            reSetPassWordBtn.setClickable(true);
                        }
                    });
        }

    }



    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case 0:
                // 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
                handler.removeMessages(0);
                // app的功能逻辑处理
                sendMessageBtn.setClickable(false);
                sendMessageBtn.setTextColor(getResources().getColor(R.color.color_FFDDDDDD));
                sendMessageBtn.setText("发送验证码("+reSetSendMessageBtnSecond+")");
                reSetSendMessageBtnSecond --;
                if (reSetSendMessageBtnSecond == 0){
                    // 发出msg，停止更新
                    handler.sendEmptyMessageDelayed(1, 1000);
                }else {
                    // 再次发出msg，循环更新
                    handler.sendEmptyMessageDelayed(0, 1000);
                }
                break;

            case 1:
                // 直接移除，定时器停止
                handler.removeMessages(0);
                handler.removeMessages(1);
                reSetSendMessageBtnSecond = 60;
                sendMessageBtn.setClickable(true);
                sendMessageBtn.setText("发送验证码");
                sendMessageBtn.setTextColor(getResources().getColor(R.color.theme_magic_sakura_primary));
                break;

            default:
                break;
        }
    }

    @Override
    protected void onStop() {

        /**
         * 页面关闭时释放资源
         */
        handler.removeMessages(0);
        gt3GeetestUtilsBindForgetPassword.cancelUtils();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        //取消动态网络变化广播接收器的注册
//        unregisterReceiver(receiver);
        super.onDestroy();
    }

//    public class ForgetPassWordBroadCastReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            handler.sendEmptyMessage(1);
//        }
//    }
}

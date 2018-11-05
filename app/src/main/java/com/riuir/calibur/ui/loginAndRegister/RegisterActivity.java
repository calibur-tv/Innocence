package com.riuir.calibur.ui.loginAndRegister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.geetest.sdk.GT3GeetestButton;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.BangumiAllListUtils;
import com.riuir.calibur.data.DramaListResp;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.GeeTestInfo;
import com.riuir.calibur.data.params.VerificationCodeBody;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.ui.splash.SplashActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.geetest.GeetestUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 已弃用
 */

public class RegisterActivity extends BaseActivity {



//    String validateURL = Constants.API_BASE_URL + "/door/send";

    private static final int NET_GEE_STATUS_captcha = 0;
    private static final int NET_GEE_STATUS_validate = 1;
    private static final int NET_REGISTER = 2;

    private GT3GeetestUtilsBind gt3GeetestUtilsBindRegister;
    private GT3GeetestBindListener bindListenerRegister;

    @BindView(R.id.register_phone_number_edit)
    EditText phoneNumberEdit;
    @BindView(R.id.register_password_edit)
    EditText passWordEdit;
    @BindView(R.id.register_password_confirm_edit)
    EditText passwordConfirmEdit;
    @BindView(R.id.register_verification_code_edit)
    EditText verificationCodeEdit;
    @BindView(R.id.register_invite_code_edit)
    EditText inviteCodeEdit;
    @BindView(R.id.register_phone_or_nickname_edit)
    EditText nicknameEdit;

    @BindView(R.id.register_start_activity_login_btn)
    ImageView startActivityToLoginBtn;
    @BindView(R.id.register_register_btn)
    Button registerBtn;

    String nicknameStr = "";
    String phoneNumber = "";
    String passWordStr = "";
    String passwordConfirmdStr = "";
    String verificationCodeStr = "";
    String inviteCodeStr = "";


    @BindView(R.id.register_send_verification_code_btn)
    TextView sendVerificationCodeBtn;
    private int reSetsendVerificationCodeBtnSecond = 60;

    private GeeTestInfo geeTestInfo;
    private GeeTestInfo.GeeTest geeTest;

    private VerificationCodeBody verificationCodebodyBody;
    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;

    private RegisterBroadCastReceiver receiver;
    private IntentFilter intentFilter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_register;
    }

    @Override
    protected void onInit() {
        verificationCodebodyBody = new VerificationCodeBody();
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();

        gt3GeetestUtilsBindRegister = new GT3GeetestUtilsBind(RegisterActivity.this);
        registerReceiver();
        initBindListener();
        initSendVerificationCodeBtn();
        initRegisterAndLoginBtn();

    }

    private void registerReceiver() {
        receiver = new RegisterBroadCastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(GeetestUtils.FailAction);
        registerReceiver(receiver,intentFilter);
    }

    private void initRegisterAndLoginBtn() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phoneNumberEdit.getText().toString();
                nicknameStr = nicknameEdit.getText().toString();
                passWordStr = passWordEdit.getText().toString();
                passwordConfirmdStr = passwordConfirmEdit.getText().toString();
                verificationCodeStr = verificationCodeEdit.getText().toString();
                inviteCodeStr = inviteCodeEdit.getText().toString();

                if (phoneNumber == null || phoneNumber.length() != 11
                        ||nicknameStr == null || nicknameStr.length() == 0
                        ||passWordStr == null || passWordStr.length() == 0
                        ||passwordConfirmdStr == null || passwordConfirmdStr.length() == 0
                        ||verificationCodeStr == null || verificationCodeStr.length() == 0){
                    ToastUtils.showShort(RegisterActivity.this,"请完善您的信息哟(＾Ｕ＾)ノ~");
                }else {
                    if (passWordStr.equals(passwordConfirmdStr)){
                        registerBtn.setText("注册中");
                        registerBtn.setClickable(false);
                        setNet(NET_REGISTER);
                    }else {
                        ToastUtils.showShort(RegisterActivity.this,"两次输入的密码不一样哟(＾Ｕ＾)ノ~");
                    }
                }

            }
        });
        startActivityToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(LoginActivity.class);
                finish();
            }
        });

    }

    private void initSendVerificationCodeBtn() {

        sendVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phoneNumberEdit.getText().toString();

                if (phoneNumber == null || phoneNumber.length() != 11 ){
                    ToastUtils.showShort(RegisterActivity.this,"请输入正确的手机号码哟(＾Ｕ＾)ノ~");
                }else{
                    verificationCodebodyBody.setType("sign_up");
                    verificationCodebodyBody.setPhone_number(phoneNumber);
                    sendVerificationCodeBtn.setClickable(false);
                    sendVerificationCodeBtn.setText("发送中..");
//                    sendVerWithOutGee();
//                    setNet(NET_GEE_STATUS_captcha);
//                    GeetestUtils.setGeetestStart(RegisterActivity.this,apiGet,bindListenerRegister,
//                            verificationCodeBodyGeeTest,
//                            gt3GeetestUtilsBindRegister);
                }
            }
        });
    }


    private void setNet(int NET_STATUS){

        if (NET_STATUS == NET_GEE_STATUS_validate){

            //自定义API2
            LogUtils.d("registerLog","verificationCodebodyBody = "+verificationCodebodyBody.toString());
            apiPostNoAuth.getGeeTestSendValidate(verificationCodebodyBody).enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    if (response!=null&&response.isSuccessful()){

                        int code = response.body().getCode();
                        //发送成功
                        ToastUtils.showShort(RegisterActivity.this,response.body().getData());
                        gt3GeetestUtilsBindRegister.gt3TestFinish();
                        reSetsendVerificationCodeBtnSecond = 60;
                        handler.sendEmptyMessage(0);

                    }else if (response!=null&&!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        ToastUtils.showShort(RegisterActivity.this,info.getMessage());
                        gt3GeetestUtilsBindRegister.gt3TestClose();
                        handler.sendEmptyMessage(1);
                    }else {
                        ToastUtils.showShort(RegisterActivity.this,"验证失败");
                        gt3GeetestUtilsBindRegister.gt3TestClose();
                        handler.sendEmptyMessage(1);
                    }
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(RegisterActivity.this,"验证失败");
                    gt3GeetestUtilsBindRegister.gt3TestClose();
                    handler.sendEmptyMessage(1);
                }
            });
        }
        //点击注册
        if (NET_STATUS == NET_REGISTER){
            Map<String,Object> parmas = new HashMap<>();
            parmas.put("nickname",nicknameStr);
            parmas.put("access",phoneNumber);
            parmas.put("secret",passwordConfirmdStr);
            parmas.put("authCode",verificationCodeStr);
            if (inviteCodeStr!=null&&inviteCodeStr.length()!=0){
                parmas.put("inviteCode",nicknameStr);
            }
            apiPostNoAuth.getCallRegister(parmas).enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    if (response!=null&&response.isSuccessful()){

                            ToastUtils.showShort(RegisterActivity.this,"注册成功！✿✿ヽ(°▽°)ノ✿");
                            //注册成功 返回JWT-Token(userToken) 存储下来 作为判断用户是否登录的凭证
                            SharedPreferencesUtils.put(App.instance(),"Authorization",response.body().getData());
                            Constants.ISLOGIN = true;
                            Constants.AUTH_TOKEN = response.body().getData();
                            BangumiAllListUtils.setBangumiAllList(RegisterActivity.this,apiGet);
//                            startActivity(MainActivity.class);
//                            finish();
                    }else if (response!=null&&!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        ToastUtils.showShort(RegisterActivity.this,info.getMessage());
                    }else {
                        ToastUtils.showShort(RegisterActivity.this,"不明原因导致注册失败了QAQ");
                    }

                    registerBtn.setText("注册并登陆");
                    registerBtn.setClickable(true);
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(RegisterActivity.this,"请检查您的网络哟~");
                    registerBtn.setText("注册并登陆");
                    registerBtn.setClickable(true);
                }
            });
        }

    }

    private void initBindListener() {
        bindListenerRegister = new GT3GeetestBindListener() {
            @Override
            public void gt3CloseDialog(int i) {
                super.gt3CloseDialog(i);
                handler.sendEmptyMessage(1);
            }

            @Override
            public void gt3DialogReady() {
                super.gt3DialogReady();
            }

            //用户是否自定义二次验证
            @Override
            public boolean gt3SetIsCustom() {
                return true;
            }

            @Override
            public void gt3GeetestStatisticsJson(JSONObject jsonObject) {
                super.gt3GeetestStatisticsJson(jsonObject);
            }

            /**
             * 自定义二次验证，也就是当gtSetIsCustom为ture时才执行
             * 拿到第二个url（API2）需要的数据
             * 在该回调里面自行请求api2
             * 对api2的结果进行处理
             * status 如果是true执行自定义接口2请求
             */
            @Override
            public void gt3GetDialogResult(boolean status, String result) {
//
                if (status){
                    //基本使用方法：

                     // 1.取出该接口返回的三个参数用于自定义二次验证
                    JSONObject res_json = null;
                    try {
                        res_json = new JSONObject(result);

                        verificationCodeBodyGeeTest.setGeetest_challenge(res_json.getString("geetest_challenge"));
                        verificationCodeBodyGeeTest.setGeetest_validate(res_json.getString("geetest_validate"));
                        verificationCodeBodyGeeTest.setGeetest_seccode(res_json.getString("geetest_seccode"));


                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtils.d("registerLog","verificationCodeBodyGeeTest = "+verificationCodeBodyGeeTest.toString());
                    verificationCodebodyBody.setGeetest(verificationCodeBodyGeeTest);
                    setNet(NET_GEE_STATUS_validate);

                }

            }



            @Override
            public void gt3DialogOnError(String s) {
                super.gt3DialogOnError(s);
                handler.sendEmptyMessage(1);
            }
        };
    }

//    private void initBind() {
//        // 开启LoadDialog 第二个参数为lang（语言，如果为null则为系统语言）
//        gt3GeetestUtilsBindRegister.showLoadingDialog(this, null);
//        // 设置是否可以点击Dialog灰色区域关闭验证码
//        gt3GeetestUtilsBindRegister.setDialogTouch(false);
//        initBindListener();
//        gt3GeetestUtilsBindRegister.getGeetest(this,"","",null,bindListenerRegister);
//    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case 0:
                // 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
                handler.removeMessages(0);
                // app的功能逻辑处理
                sendVerificationCodeBtn.setClickable(false);
                sendVerificationCodeBtn.setTextColor(getResources().getColor(R.color.color_FFDDDDDD));
                sendVerificationCodeBtn.setText("发送验证码("+reSetsendVerificationCodeBtnSecond+")");
                reSetsendVerificationCodeBtnSecond --;
                if (reSetsendVerificationCodeBtnSecond == 0){
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
                sendVerificationCodeBtn.setClickable(true);
                sendVerificationCodeBtn.setText("发送验证码");
                sendVerificationCodeBtn.setTextColor(getResources().getColor(R.color.theme_magic_sakura_primary));
                break;

            default:
                break;
        }

    }


    @Override
    public void onBackPressed() {
        startActivity(LoginActivity.class);
        finish();
    }

    @Override
    protected void onStop() {

        handler.removeMessages(0);
        /**
         * 页面关闭时释放资源
         */
        gt3GeetestUtilsBindRegister.cancelUtils();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class RegisterBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            handler.sendEmptyMessage(1);
        }
    }
}

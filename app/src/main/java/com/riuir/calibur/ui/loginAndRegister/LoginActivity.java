package com.riuir.calibur.ui.loginAndRegister;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.geetest.sdk.Bind.GT3Geetest;
import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.geetest.sdk.GT3GeetestButton;
import com.geetest.sdk.GT3GeetestListener;
import com.geetest.sdk.GT3GeetestUtils;
import com.geetest.sdk.Gt3GeetestTestMsg;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.GeeTestInfo;
import com.riuir.calibur.data.params.VerificationCodeBody;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.utils.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private static final int NET_GEE_STATUS_captcha = 0;
    private static final int NET_GEE_STATUS_login = 1;


    @BindView(R.id.login_username_edit)
    EditText userNameEdit;
    @BindView(R.id.login_password_edit)
    EditText passWordEdit;

    String userNameStr;
    String passWordStr;

    @BindView(R.id.login_login_btn)
    Button loginBtn;
    @BindView(R.id.login_start_activity_forget_password_btn)
    TextView startActivityForgetPassWordBtn;
    @BindView(R.id.login_start_activity_register_btn)
    TextView startActivityRegisterBtn;

    GT3GeetestUtilsBind gt3GeetestUtilsBindLogin;
    GT3GeetestBindListener bindListenerLogin;

    //跳转来登陆页面之前的activity的className
    String fromClassName;

    private GeeTestInfo geeTestInfo;
    private GeeTestInfo.GeeTest geeTest;

    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onInit() {
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();

        gt3GeetestUtilsBindLogin = new GT3GeetestUtilsBind(LoginActivity.this);

        Intent intent = getIntent();
        fromClassName = intent.getComponent().getClassName();

        initOnClicklistener();

    }

    private void initOnClicklistener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameStr = userNameEdit.getText().toString();
                passWordStr = passWordEdit.getText().toString();
                if (userNameStr==null||userNameStr.length() != 11
                        ||passWordStr == null||passWordStr.length()==0){
                    ToastUtils.showShort(LoginActivity.this,"请完善您的信息哟(＾Ｕ＾)ノ~");
                }else {
                    //TODO
                    setNetWithOutGee();
//                    setNet(NET_GEE_STATUS_captcha);
                }
            }
        });
        startActivityForgetPassWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ForgetPassWordActivity.class);
            }
        });
        startActivityRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RegisterActivity.class);
                finish();
            }
        });
    }

    private void setNetWithOutGee(){



        //自定义API2 登录
        Map<String,Object> params = new HashMap<>();
        params.put("access",userNameStr);
        params.put("secret",passWordStr);
//        params.put("geetest",verificationCodeBodyGeeTest);

        LogUtils.d("loginActivity","geetest = "+verificationCodeBodyGeeTest.toString());
        apiPostNoGeetest.getCallLogin(params).enqueue(new Callback<Event<String>>() {
            @Override
            public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                if (response!=null&&response.isSuccessful()){

                    int code = response.body().getCode();
                    if (code == 0){
                        gt3GeetestUtilsBindLogin.gt3TestFinish();
                        // 登录成功
                        ToastUtils.showShort(LoginActivity.this,"登录成功！✿✿ヽ(°▽°)ノ✿");
                        //返回JWT-Token(userToken) 存储下来 作为判断用户是否登录的凭证
                        SharedPreferencesUtils.put(App.instance(),"Authorization",response.body().getData());
                        Constants.ISLOGIN = true;
                        Constants.AUTH_TOKEN = response.body().getData();

                        startActivity(MainActivity.class);
                        finish();
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

                    ToastUtils.showShort(LoginActivity.this,info.getMessage());
                }else {
                    ToastUtils.showShort(LoginActivity.this,"不明原因导致登录失败QAQ");
                    gt3GeetestUtilsBindLogin.gt3TestClose();
                }
            }

            @Override
            public void onFailure(Call<Event<String>> call, Throwable t) {
                ToastUtils.showShort(LoginActivity.this,"请检查您的网络哟~");
                gt3GeetestUtilsBindLogin.gt3TestClose();
            }
        });
    }

    private void setNet(int NET_STATUS){
        if (NET_STATUS == NET_GEE_STATUS_captcha){
            //自定义API1后 将API1的返回数据传给gt3GeetestUtils
            apiGet.getCallGeeTestImageCaptcha().enqueue(new Callback<GeeTestInfo>() {
                @Override
                public void onResponse(Call<GeeTestInfo> call, Response<GeeTestInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        geeTestInfo = response.body();
                        geeTest = geeTestInfo.getData();

                        verificationCodeBodyGeeTest.setSuccess(geeTest.getSuccess());
                        verificationCodeBodyGeeTest.setPayload(geeTest.getPayload());
                        JSONObject params = new JSONObject();
                        try {
                            params.put("success",geeTest.getSuccess());
                            params.put("gt",geeTest.getGt());
                            params.put("challenge",geeTest.getChallenge());
                            params.put("new_captcha",true);
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                        }
                        gt3GeetestUtilsBindLogin.gtSetApi1Json(params);
                        initBind();

                    }else if (response!=null&&!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        ToastUtils.showShort(LoginActivity.this,info.getMessage());
                    }else {
                        ToastUtils.showShort(LoginActivity.this,"不明原因导致验证码发送失败QAQ");
                    }
                }

                @Override
                public void onFailure(Call<GeeTestInfo> call, Throwable t) {
                    ToastUtils.showShort(LoginActivity.this,"请检查您的网络哟~");
                }
            });
        }
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
                            gt3GeetestUtilsBindLogin.gt3TestFinish();
                            // 登录成功
                            ToastUtils.showShort(LoginActivity.this,"登录成功！✿✿ヽ(°▽°)ノ✿");
                            //返回JWT-Token(userToken) 存储下来 作为判断用户是否登录的凭证
                            SharedPreferencesUtils.put(App.instance(),"Authorization",response.body().getData());
                            Constants.ISLOGIN = true;
                            Constants.AUTH_TOKEN = response.body().getData();

                            startActivity(MainActivity.class);
                            finish();
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

                        ToastUtils.showShort(LoginActivity.this,info.getMessage());
                    }else {
                        ToastUtils.showShort(LoginActivity.this,"不明原因导致登录失败QAQ");
                        gt3GeetestUtilsBindLogin.gt3TestClose();
                    }
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(LoginActivity.this,"请检查您的网络哟~");
                    gt3GeetestUtilsBindLogin.gt3TestClose();
                }
            });
        }


    }

    private void initBindListener() {
        bindListenerLogin = new GT3GeetestBindListener() {
            @Override
            public void gt3CloseDialog(int i) {
                super.gt3CloseDialog(i);
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

                    setNet(NET_GEE_STATUS_login);

                }

            }



            @Override
            public void gt3DialogOnError(String s) {
                super.gt3DialogOnError(s);
            }
        };
    }

    private void initBind() {
        initBindListener();
        gt3GeetestUtilsBindLogin.getGeetest(this,"","",null,bindListenerLogin);
    }



    @Override
    public void onDestroy() {
        /**
         * 页面关闭时释放资源
         */
        gt3GeetestUtilsBindLogin.cancelUtils();
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

    @Override
    protected void handler(Message msg) {

    }
}

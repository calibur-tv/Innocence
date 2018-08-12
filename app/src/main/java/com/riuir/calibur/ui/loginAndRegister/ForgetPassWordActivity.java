package com.riuir.calibur.ui.loginAndRegister;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.GeeTestInfo;
import com.riuir.calibur.data.params.VerificationCodeBody;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


    String userNameStr;
    String newPassWordStr;
    String verificationCodeStr;

    @BindView(R.id.forget_reset_password_btn)
    Button reSetPassWordBtn;
    @BindView(R.id.forget_start_activity_login_btn)
    TextView startActivityLoginBtn;
    @BindView(R.id.forget_send_verification_code_btn)
    TextView sendMessageBtn;

    GT3GeetestUtilsBind gt3GeetestUtilsBindForgetPassword;
    GT3GeetestBindListener bindListenerForgetPassword;

    int reSetSendMessageBtnSecond = 0;

    private GeeTestInfo geeTestInfo;
    private GeeTestInfo.GeeTest geeTest;

    private VerificationCodeBody verificationCodebodyBody;
    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_forget_pass_word;
    }

    @Override
    protected void onInit() {
        verificationCodebodyBody = new VerificationCodeBody();
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();

        gt3GeetestUtilsBindForgetPassword = new GT3GeetestUtilsBind(ForgetPassWordActivity.this);

        initOnClicklistener();
    }

    private void initOnClicklistener() {
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameStr = userNameEdit.getText().toString();
                if (userNameStr==null||userNameStr.length() != 11){
                    ToastUtils.showShort(ForgetPassWordActivity.this,"请输入正确的手机号哟(＾Ｕ＾)ノ~");
                }else {
                    setNet(NET_GEE_STATUS_captcha);
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
                if (userNameStr==null||userNameStr.length() != 11
                        ||newPassWordStr==null||newPassWordStr.length() == 0
                        ||verificationCodeStr==null||verificationCodeStr.length() == 0){
                    ToastUtils.showShort(ForgetPassWordActivity.this,"请完善您的信息哟(＾Ｕ＾)ノ~");
                }else {
                    setNet(NET_reset_password);
                }
            }
        });
    }

    private void setNet(int NET_STATUS){
        if (NET_STATUS == NET_GEE_STATUS_captcha){
            //自定义API1后 将API1的返回数据传给gt3GeetestUtils
            apiGet.getCallGeeTestImageCaptcha().enqueue(new Callback<GeeTestInfo>() {
                @Override
                public void onResponse(Call<GeeTestInfo> call, Response<GeeTestInfo> response) {
                    if (response!=null&&response.body()!=null&&response.body().getCode()==0){
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
                        gt3GeetestUtilsBindForgetPassword.gtSetApi1Json(params);
                        initBind();

                    }else {
                        ToastUtils.showShort(ForgetPassWordActivity.this,"不明原因导致验证码发送失败QAQ");
                    }
                }

                @Override
                public void onFailure(Call<GeeTestInfo> call, Throwable t) {
                    ToastUtils.showShort(ForgetPassWordActivity.this,"请检查您的网络~");
                }
            });
        }
        if (NET_STATUS == NET_GEE_STATUS_sendMessage){

            //自定义API2 登录
            verificationCodebodyBody.setType("forgot_password");
            verificationCodebodyBody.setPhone_number(userNameStr);
            verificationCodebodyBody.setGeetest(verificationCodeBodyGeeTest);

            apiPostNoAuth.getGeeTestSendValidate(verificationCodebodyBody).enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                        LogUtils.d("resetPass",response.toString());
                    if (response!=null&&response.body()!=null){
                        LogUtils.d("resetPass",response.body().toString());
                        int code = response.body().getCode();
                        if (code == 0){
                            // 登录成功
                            ToastUtils.showShort(ForgetPassWordActivity.this,"重置密码短信发送成功！✿✿ヽ(°▽°)ノ✿");
                            gt3GeetestUtilsBindForgetPassword.gt3TestFinish();
                            reSetSendMessageBtnSecond = 60;
                            handler.sendEmptyMessage(0);
                        }else if (code == 40003){
                            ToastUtils.showShort(ForgetPassWordActivity.this,response.body().getData());
                            gt3GeetestUtilsBindForgetPassword.gt3TestClose();
                        }else if (code == 40004){
                            ToastUtils.showShort(ForgetPassWordActivity.this,response.body().getMessage());
                            gt3GeetestUtilsBindForgetPassword.gt3TestClose();
                        }else {
                            ToastUtils.showShort(ForgetPassWordActivity.this,"不明原因导致验证码发送失败");
                            gt3GeetestUtilsBindForgetPassword.gt3TestClose();
                        }
                    }else if (response.code() == 503){
                        ToastUtils.showShort(ForgetPassWordActivity.this,"您发送的太多啦，试试明天再来吧");
                    }else {
                        ToastUtils.showShort(ForgetPassWordActivity.this,"不明原因导致验证码发送失败QAQ");
                        gt3GeetestUtilsBindForgetPassword.gt3TestClose();
                    }
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(ForgetPassWordActivity.this,"请检查您的网络哟");
                    gt3GeetestUtilsBindForgetPassword.gt3TestClose();
                }
            });
        }

        //点击注册
        if (NET_STATUS == NET_reset_password){
            Map<String,Object> parmas = new HashMap<>();
            parmas.put("access",userNameStr);
            parmas.put("secret",newPassWordStr);
            parmas.put("authCode",verificationCodeStr);

            apiPostNoAuth.getCallReSetPassWord(parmas).enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    LogUtils.d("resetPass",response.toString());

                    if (response!=null&&response.body()!=null){
                        LogUtils.d("resetPass",response.body().toString());
                        if(response.body().getCode() == 0){
                            ToastUtils.showLong(ForgetPassWordActivity.this,"重置密码成功(＾Ｕ＾)ノ~");
                            finish();
                        }else if (response.body().getCode() == 400){
                            ToastUtils.showShort(ForgetPassWordActivity.this,response.body().getData());
                        }else if (response.body().getCode() == 403){
                            ToastUtils.showShort(ForgetPassWordActivity.this,response.body().getMessage());
                        }else {
                            ToastUtils.showShort(ForgetPassWordActivity.this,"不明原因导致注册失败了");
                        }
                    }else {
                        ToastUtils.showShort(ForgetPassWordActivity.this,"不明原因导致注册失败了QAQ");
                    }


                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(ForgetPassWordActivity.this,"请检查您的网络哟~");
                }
            });
        }

    }

    private void initBindListener() {
        bindListenerForgetPassword = new GT3GeetestBindListener() {
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

                    setNet(NET_GEE_STATUS_sendMessage);

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
        gt3GeetestUtilsBindForgetPassword.getGeetest(this,"","",null,bindListenerForgetPassword);
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
}

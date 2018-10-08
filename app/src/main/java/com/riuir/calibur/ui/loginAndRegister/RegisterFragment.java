package com.riuir.calibur.ui.loginAndRegister;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ScreenUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.BangumiAllListUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.GeeTestInfo;
import com.riuir.calibur.data.params.VerificationCodeBody;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.common.IHandler;
import com.riuir.calibur.ui.common.UIHandler;
import com.riuir.calibur.ui.home.MainActivity;
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
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseFragment {

    private static final int NET_GEE_STATUS_validate = 1;
    private static final int NET_REGISTER = 2;

    private   UIHandler handler = new UIHandler(Looper.getMainLooper());

    private GT3GeetestUtilsBind gt3GeetestUtilsBindRegister;
    private GT3GeetestBindListener bindListenerRegister;

    @BindView(R.id.register_fragment_phone_number_edit)
    EditText phoneNumberEdit;
    @BindView(R.id.register_fragment_password_edit)
    EditText passWordEdit;
    @BindView(R.id.register_fragment_phone_or_nickname_edit)
    EditText nicknameEdit;
    @BindView(R.id.register_fragment_invite_code_edit)
    EditText inviteCodeEdit;
    @BindView(R.id.register_fragment_password_visibility)
    ImageView passwordVisibilityBtn;

    @BindView(R.id.register_fragment_register_btn)
    Button registerBtn;

    @BindView(R.id.register_fragment_has_invite_code)
    TextView hasInviteCodeBtn;

    @BindView(R.id.register_fragment_invite_code_layout)
    LinearLayout inviteLayout;

    boolean hasInviteCode = false;
    boolean passwordIsVisibility = false;

    String nicknameStr = "";
    String phoneNumber = "";
    String passWordStr = "";
    String verificationCodeStr = "";
    String inviteCodeStr = "";

    private AlertDialog alertDialog;
    EditText verificationCodeEdit;
    TextView sendVerificationCodeBtn;
    TextView verificationCodeOk;
    TextView verificationCodeCancel;

    private int reSetsendVerificationCodeBtnSecond = 60;


    private VerificationCodeBody verificationCodebodyBody;
    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;

    private RegisterBroadCastReceiver receiver;
    private IntentFilter intentFilter;
    @Override
    protected int getContentViewID() {
        return R.layout.fragment_register;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        verificationCodebodyBody = new VerificationCodeBody();
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();

        gt3GeetestUtilsBindRegister = new GT3GeetestUtilsBind(getContext());
        setHandler();
        registerReceiver();
        initBindListener();
//        initSendVerificationCodeBtn();
        setDialog();
        initRegisterAndLoginBtn();
    }

    private void registerReceiver() {
        receiver = new RegisterBroadCastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(GeetestUtils.FailAction);
        getActivity().registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onStop() {

        handler.removeMessages(0);
        /**
         * 页面关闭时释放资源
         */
        gt3GeetestUtilsBindRegister.cancelUtils();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initRegisterAndLoginBtn() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phoneNumberEdit.getText().toString();
                nicknameStr = nicknameEdit.getText().toString();
                passWordStr = passWordEdit.getText().toString();
//                passwordConfirmdStr = passwordConfirmEdit.getText().toString();
//                verificationCodeStr = verificationCodeEdit.getText().toString();
                inviteCodeStr = inviteCodeEdit.getText().toString();

                if (phoneNumber == null || phoneNumber.length() != 11){
                    ToastUtils.showShort(getContext(),"请输入正确的11位手机号");
                }else if (nicknameStr == null || nicknameStr.length() == 0){
                    ToastUtils.showShort(getContext(),"请输入您的昵称");
                }else if (passWordStr == null || passWordStr.length() < 6){
                    ToastUtils.showShort(getContext(),"密码长度最少6位");
                } else {
                    alertDialog.show();

                }

            }
        });
        hasInviteCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasInviteCode){
                    inviteLayout.setVisibility(View.GONE);
                    hasInviteCode = false;
                    hasInviteCodeBtn.setText("我有邀请码");
                }else {
                    inviteLayout.setVisibility(View.VISIBLE);
                    hasInviteCode = true;
                    hasInviteCodeBtn.setText("没有邀请码");
                }
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

    private void setDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_register_verfication_code,null,false);
        alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();
        verificationCodeEdit = view.findViewById(R.id.dialog_register_verification_code_edit);
        sendVerificationCodeBtn = view.findViewById(R.id.dialog_register_send_verification_code_btn);
        verificationCodeOk = view.findViewById(R.id.dialog_register_verification_code_ok);
        verificationCodeCancel = view.findViewById(R.id.dialog_register_verification_code_cancel);

        sendVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber == null || phoneNumber.length() != 11 ){
                    ToastUtils.showShort(getContext(),"请输入正确的手机号码哟(＾Ｕ＾)ノ~");
                }else{
                    verificationCodebodyBody.setType("sign_up");
                    verificationCodebodyBody.setPhone_number(phoneNumber);
                    sendVerificationCodeBtn.setClickable(false);
                    sendVerificationCodeBtn.setText("发送中..");
                    GeetestUtils.setGeetestStart(getContext(),apiGet,bindListenerRegister,
                            verificationCodeBodyGeeTest,
                            gt3GeetestUtilsBindRegister);

                }
            }
        });

        verificationCodeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        verificationCodeOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificationCodeStr = verificationCodeEdit.getText().toString();
                if (verificationCodeStr == null&&verificationCodeStr.length()==0){
                    ToastUtils.showShort(getContext(),"请输入正确的验证码");
                }else {
                    phoneNumber = phoneNumberEdit.getText().toString();
                    verificationCodeOk.setText("注册中");
                    verificationCodeOk.setClickable(false);
                    registerBtn.setText("注册中");
                    registerBtn.setClickable(false);
                    setNet(NET_REGISTER);
                }
            }
        });

        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
        alertDialog.getWindow().setLayout((ScreenUtils.getScreenWidth(getContext())/4*3)
                ,LinearLayout.LayoutParams.WRAP_CONTENT);

    }

//    private void initSendVerificationCodeBtn() {
//
////        sendVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                phoneNumber = phoneNumberEdit.getText().toString();
////
////                if (phoneNumber == null || phoneNumber.length() != 11 ){
////
////                    ToastUtils.showShort(getContext(),"请输入正确的手机号码哟(＾Ｕ＾)ノ~");
////                }else{
////                    verificationCodebodyBody.setType("sign_up");
////                    verificationCodebodyBody.setPhone_number(phoneNumber);
////                    sendVerificationCodeBtn.setClickable(false);
////                    sendVerificationCodeBtn.setText("发送中..");
//////                    sendVerWithOutGee();
//////                    setNet(NET_GEE_STATUS_captcha);
////                    GeetestUtils.setGeetestStart(getContext(),apiGet,bindListenerRegister,
////                            verificationCodeBodyGeeTest,
////                            gt3GeetestUtilsBindRegister);
////                }
////            }
////        });
//
//    }

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
                        ToastUtils.showShort(getContext(),response.body().getData());
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

                        ToastUtils.showShort(getContext(),info.getMessage());
                        handler.sendEmptyMessage(1);
                    }else {
                        ToastUtils.showShort(getContext(),"未知原因导致验证失败(返回值为null)");
                        handler.sendEmptyMessage(1);
                    }
                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"请检查您的网络\n  t = "+t.getMessage());
                    LogUtils.d("AppNetErrorMessage","register t = "+t.getMessage());
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
            parmas.put("secret",passWordStr);
            parmas.put("authCode",verificationCodeStr);
            if (inviteCodeStr!=null&&inviteCodeStr.length()!=0){
                parmas.put("inviteCode",nicknameStr);
            }
            apiPostNoAuth.getCallRegister(parmas).enqueue(new Callback<Event<String>>() {
                @Override
                public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                    if (response!=null&&response.isSuccessful()){

                        ToastUtils.showShort(getContext(),"注册成功！✿✿ヽ(°▽°)ノ✿");
                        //注册成功 返回JWT-Token(userToken) 存储下来 作为判断用户是否登录的凭证
                        SharedPreferencesUtils.put(App.instance(),"Authorization",response.body().getData());
                        Constants.ISLOGIN = true;
                        Constants.AUTH_TOKEN = response.body().getData();
                        BangumiAllListUtils.setBangumiAllList(getContext(),apiGet);
//                        Intent intent = new Intent(getContext(),MainActivity.class);
//                        getActivity().startActivity(intent);
//                        getActivity().finish();

                    }else if (response!=null&&!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        ToastUtils.showShort(getContext(),info.getMessage());
                        verificationCodeOk.setText("注册");
                        verificationCodeOk.setClickable(true);
                        registerBtn.setText("注册");
                        registerBtn.setClickable(true);
                    }else {
                        ToastUtils.showShort(getContext(),"不明原因导致注册失败了");
                        verificationCodeOk.setText("注册");
                        verificationCodeOk.setClickable(true);
                        registerBtn.setText("注册");
                        registerBtn.setClickable(true);
                    }

                }

                @Override
                public void onFailure(Call<Event<String>> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"请检查您的网络");
                    verificationCodeOk.setText("注册");
                    verificationCodeOk.setClickable(true);
                    registerBtn.setText("注册");
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
                    gt3GeetestUtilsBindRegister.gt3TestFinish();
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

    private void setHandler() {
        handler.setHandler(new IHandler() {
            public void handleMessage(Message msg) {
                handler(msg);//有消息就提交给子类实现的方法
            }
        });
    }


    private void handler(Message msg){
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
                reSetsendVerificationCodeBtnSecond = 60;
                sendVerificationCodeBtn.setClickable(true);
                sendVerificationCodeBtn.setText("发送验证码");
                sendVerificationCodeBtn.setTextColor(getResources().getColor(R.color.theme_magic_sakura_primary));
                break;

            default:
                break;
        }
    }

    public class RegisterBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            handler.sendEmptyMessage(1);
        }
    }
}

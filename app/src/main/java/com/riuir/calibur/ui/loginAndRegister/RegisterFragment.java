package com.riuir.calibur.ui.loginAndRegister;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import calibur.core.http.models.geetest.params.VerificationCodeBody;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ScreenUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.BangumiAllListUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.common.IHandler;
import com.riuir.calibur.ui.common.UIHandler;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.geetest.GeetestUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends BaseFragment {

    private static final int NET_GEE_STATUS_validate = 1;
    private static final int NET_REGISTER = 2;

    private   UIHandler handler = new UIHandler(Looper.getMainLooper());

    private GT3GeetestUtilsBind gt3GeetestUtilsBindRegister;
    GeetestUtils geetestUtils;

    @BindView(R.id.register_fragment_phone_number_edit)
    EditText phoneNumberEdit;
    @BindView(R.id.register_fragment_password_edit)
    EditText passWordEdit;
    @BindView(R.id.register_fragment_phone_or_nickname_edit)
    EditText nicknameEdit;
    @BindView(R.id.register_fragment_invite_code_edit)
    EditText inviteCodeEdit;
//    @BindView(R.id.register_fragment_password_visibility)
//    ImageView passwordVisibilityBtn;

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

//    private RegisterBroadCastReceiver receiver;
//    private IntentFilter intentFilter;
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
//        registerReceiver();
//        initSendVerificationCodeBtn();
        setDialog();
        initRegisterAndLoginBtn();
    }

//    private void registerReceiver() {
//        receiver = new RegisterBroadCastReceiver();
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(GeetestUtils.FailAction);
//        getActivity().registerReceiver(receiver,intentFilter);
//    }

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
//        getActivity().unregisterReceiver(receiver);
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
//        passwordVisibilityBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (passwordIsVisibility){
//                    passwordIsVisibility = false;
//                    passWordEdit.setTransformationMethod(PasswordTransformationMethod
//                            .getInstance());  //以密文显示，以.代替
//                }else {
//                    passwordIsVisibility = true;
//                    passWordEdit.setTransformationMethod(HideReturnsTransformationMethod
//                            .getInstance());  //密码以明文显示
//                }
//            }
//        });

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
                    setGeeTestUtils();

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
                if (verificationCodeStr == null||verificationCodeStr.length()!=6){
                    ToastUtils.showShort(getContext(),"请输入正确的6位数字验证码");
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

    private void setGeeTestUtils() {
        geetestUtils = new GeetestUtils();
        geetestUtils.setGeetestStart(getContext(),apiGet,
                gt3GeetestUtilsBindRegister);
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
                verificationCodebodyBody.setGeetest(verificationCodeBodyGeeTest);
                gt3GeetestUtilsBindRegister.gt3TestFinish();
                setNet(NET_GEE_STATUS_validate);
            }
        });
    }


    private void setNet(int NET_STATUS){

        if (NET_STATUS == NET_GEE_STATUS_validate){

            //自定义API2
            LogUtils.d("registerLog","verificationCodebodyBody = "+verificationCodebodyBody.toString());
            apiService.getGeeTestSendValidate(verificationCodebodyBody)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<String>() {
                        @Override
                        public void onSuccess(String s) {
                            //发送成功
                            ToastUtils.showShort(getContext(),s);
                            reSetsendVerificationCodeBtnSecond = 60;
                            handler.sendEmptyMessage(0);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
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
            apiService.getCallRegister(parmas)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<String>() {
                        @Override
                        public void onSuccess(String s) {
                            ToastUtils.showShort(getContext(),"注册成功！✿✿ヽ(°▽°)ノ✿");
                            //注册成功 返回JWT-Token(userToken) 存储下来 作为判断用户是否登录的凭证
                            UserSystem.getInstance().updateUserToken(s);
                            Constants.ISLOGIN = true;
                            Constants.AUTH_TOKEN = s;
                            LoginUtils.getUserInfo(getContext());
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (registerBtn!=null){
                                verificationCodeOk.setText("注册");
                                verificationCodeOk.setClickable(true);
                                registerBtn.setText("注册");
                                registerBtn.setClickable(true);
                            }
                        }
                    });

        }

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

}

package com.riuir.calibur.utils.geetest;

import android.content.Context;
import android.content.Intent;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.google.gson.Gson;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.GeeTestInfo;
import com.riuir.calibur.data.params.VerificationCodeBody;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.home.image.CreateImageAlbumActivity;


import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeetestUtils {


    private  GeeTestInfo geeTestInfo;
    private  GeeTestInfo.GeeTest geeTest;
    public static final String FailAction = "getGeetestFailed";
    private  GT3GeetestBindListener bindListener;
    private  VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();
    private OnGeetestSuccessListener onGeetestSuccessListener;
    private OnGeetestFailedListener onGeetestFailedListener;

    public void setGeetestStart(final Context context, ApiGet apiGet,
                                       final GT3GeetestUtilsBind gt3GeetestUtilsBind){

        initListener();

//        final Intent intent = new Intent(FailAction);
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
                    gt3GeetestUtilsBind.gtSetApi1Json(params);
                    LogUtils.d("geetestLogin","params = "+params.toString());

                    initBind(context,bindListener,gt3GeetestUtilsBind);

                }else if (response!=null&&!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);

                    ToastUtils.showShort(context,info.getMessage());
//                    context.sendBroadcast(intent);
                    setFailed("");
                }else {
                    ToastUtils.showShort(context,"不明原因导致验证码发送失败QAQ");
//                    context.sendBroadcast(intent);
                    setFailed("");
                }
            }

            @Override
            public void onFailure(Call<GeeTestInfo> call, Throwable t) {
                LogUtils.d("throwableMessage","gee t = "+t.getMessage());
                ToastUtils.showShort(context,"请检查您的网络哟~");
//                context.sendBroadcast(intent);
                setFailed("");
            }
        });
    }

    private void initBind(Context context,GT3GeetestBindListener geetestBindListener,GT3GeetestUtilsBind gt3GeetestUtilsBind) {

        // 开启LoadDialog 第二个参数为lang（语言，如果为null则为系统语言）
        gt3GeetestUtilsBind.showLoadingDialog(context, null);
        // 设置是否可以点击Dialog灰色区域关闭验证码
        gt3GeetestUtilsBind.setDialogTouch(false);

        gt3GeetestUtilsBind.getGeetest(context,
                "",""
                ,null,geetestBindListener);
    }

    private  void initListener(){
        bindListener = new GT3GeetestBindListener() {
            @Override
            public void gt3CloseDialog(int i) {
                super.gt3CloseDialog(i);
                setFailed("");
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
                    //检查和验证完成 开启上传
                    setSuccess(verificationCodeBodyGeeTest);

                }

            }

            @Override
            public void gt3DialogOnError(String s) {
                super.gt3DialogOnError(s);
                setFailed("");
            }
        };
    }

    private void setFailed(String failedMessage){
        if (onGeetestFailedListener!=null){
            onGeetestFailedListener.onFailed(failedMessage);
        }
    }
    private void setSuccess(VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest){
        if (onGeetestSuccessListener!=null){
            onGeetestSuccessListener.onSuccess(verificationCodeBodyGeeTest);
        }
    }

    public void setOnGeetestSuccessListener(OnGeetestSuccessListener onGeetestSuccessListener) {
        this.onGeetestSuccessListener = onGeetestSuccessListener;
    }

    public void setOnGeetestFailedListener(OnGeetestFailedListener onGeetestFailedListener) {
        this.onGeetestFailedListener = onGeetestFailedListener;
    }

    public interface OnGeetestSuccessListener{
        void onSuccess(VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest);
    }
    public interface OnGeetestFailedListener{
        void onFailed(String failedMessage);
    }
}

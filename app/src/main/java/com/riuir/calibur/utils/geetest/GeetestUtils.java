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


import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeetestUtils {


    private static GeeTestInfo geeTestInfo;
    private static GeeTestInfo.GeeTest geeTest;
    public static final String FailAction = "getGeetestFailed";


    public static void setGeetestStart(final Context context, ApiGet apiGet, final GT3GeetestBindListener geetestBindListener,
                                       final VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest,
                                       final GT3GeetestUtilsBind gt3GeetestUtilsBind){

        final Intent intent = new Intent(FailAction);
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

                    initBind(context,geetestBindListener,gt3GeetestUtilsBind);

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
                    context.sendBroadcast(intent);
                }else {
                    ToastUtils.showShort(context,"不明原因导致验证码发送失败QAQ");
                    context.sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<GeeTestInfo> call, Throwable t) {
                LogUtils.d("throwableMessage","gee t = "+t.getMessage());
                ToastUtils.showShort(context,"请检查您的网络哟~");
                context.sendBroadcast(intent);
            }
        });
    }

    private static void initBind(Context context,GT3GeetestBindListener geetestBindListener,GT3GeetestUtilsBind gt3GeetestUtilsBind) {

        // 开启LoadDialog 第二个参数为lang（语言，如果为null则为系统语言）
        gt3GeetestUtilsBind.showLoadingDialog(context, null);
        // 设置是否可以点击Dialog灰色区域关闭验证码
        gt3GeetestUtilsBind.setDialogTouch(false);

        gt3GeetestUtilsBind.getGeetest(context,
                "",""
                ,null,geetestBindListener);
    }

}
